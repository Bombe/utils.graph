/*
 * utils.graph - DiskStore.java - Copyright © 2011 David Roden
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pterodactylus.util.graph.disk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.pterodactylus.util.graph.Graph;
import net.pterodactylus.util.graph.Store;
import net.pterodactylus.util.graph.StoreException;
import net.pterodactylus.util.graph.disk.Storable.Factory;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskStore implements Store<DiskGraph, DiskNode, DiskEdge, DiskRelationship> {

	public final Factory<DiskRelationship> DISK_RELATIONSHIP_FACTORY = new DiskRelationshipFactory();
	public final Factory<DiskNode> DISK_NODE_FACTORY = new DiskNodeFactory(this);
	private static final Factory<NodeEdgeList> NODE_EDGE_LIST_FACTORY = new NodeEdgeListFactory();

	private static long nodeCounter = 0;
	private static long edgeCounter = 0;
	private static long relationshipCounter = 0;

	private final File directory;
	private DiskGraph graph;
	private final Map<String, DiskRelationship> relationships = new HashMap<String, DiskRelationship>();

	private final Storage<NodeEdgeList> nodeEdgeListStorage;
	private final Storage<DiskNode> nodeStorage;
	private final Storage<DiskRelationship> relationshipStorage;

	public DiskStore(String directory) throws StoreException, FileNotFoundException {
		this(new File(directory));
	}

	public DiskStore(File directory) throws StoreException, FileNotFoundException {
		if (!directory.exists() || !directory.isDirectory() || !directory.canWrite()) {
			throw new StoreException("“" + directory + "” is not a writable directory.");
		}
		this.directory = directory;
		relationshipStorage = new Storage(DISK_RELATIONSHIP_FACTORY, directory, "relationships");
		nodeStorage = new Storage<DiskNode>(DISK_NODE_FACTORY, directory, "nodes");
		nodeEdgeListStorage = new Storage<NodeEdgeList>(NODE_EDGE_LIST_FACTORY, directory, "edges");
		try {
			loadDiskStore();
		} catch (IOException ioe1) {
			// TODO Auto-generated catch block
		}
	}

	//
	// ACTIONS
	//

	DiskNode createNode() {
		DiskNode node = new DiskNode(nodeCounter++, graph);
		storeNode(node);
		return node;
	}

	DiskNode getNode(long nodeId) {
		try {
			return nodeStorage.load(nodeId);
		} catch (IOException ioe1) {
			return null; /* TODO */
		}
	}

	void removeNode(DiskNode node) {
		NodeEdgeList nodeEdges;
		try {
			nodeEdges = nodeEdgeListStorage.load(node.getId());
			nodeEdgeListStorage.remove(nodeEdges);
			nodeStorage.remove(node);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	void storeNode(DiskNode node) {
		try {
			nodeStorage.add(node);
		} catch (StoreException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	DiskEdge createEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		DiskEdge edge = new DiskEdge(edgeCounter++, graph, startNode, endNode, relationship);
		try {
			NodeEdgeList nodeEdges = getNodeEdgeList(startNode.getId());
			nodeEdges.addEdge(edge.getId(), startNode.getId(), endNode.getId(), relationship.getId());
			nodeEdgeListStorage.add(nodeEdges);
			nodeEdges = getNodeEdgeList(endNode.getId());
			nodeEdges.addEdge(edge.getId(), startNode.getId(), endNode.getId(), relationship.getId());
			nodeEdgeListStorage.add(nodeEdges);
			return edge;
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (StoreException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	Set<DiskEdge> getEdges(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		try {
			NodeEdgeList nodeEdges = nodeEdgeListStorage.load((startNode != null) ? startNode.getId() : endNode.getId());
			Set<DiskEdge> edges = new HashSet<DiskEdge>();
			for (int index = 0, size = nodeEdges.size(); index < size; ++index) {
				if (nodeEdges.getRelationshipId(index) != relationship.getId()) {
					continue;
				}
				if ((startNode != null) && (nodeEdges.getStartNodeId(index) != startNode.getId())) {
					continue;
				}
				if ((endNode != null) && (nodeEdges.getEndNodeId(index) != endNode.getId())) {
					continue;
				}
				DiskNode loadedStartNode = (startNode != null) ? startNode : nodeStorage.load(nodeEdges.getStartNodeId(index));
				DiskNode loadedEndNode = (endNode != null) ? endNode : nodeStorage.load(nodeEdges.getEndNodeId(index));
//				System.out.println("adding edge (" + startNode.getId() + "/" + endNode.getId() + "/" + relationship.getName() + ")");
				edges.add(new DiskEdge(nodeEdges.getEdgeId(index), graph, loadedStartNode, loadedEndNode, relationship));
			}
//			System.out.println("recreated " + edges.size() + " edges for node " + node.getId());
			return edges;
		} catch (IOException ioe1) {
			/* TODO */
			return null;
		}
	}

	NodeEdgeList getNodeEdgeList(long nodeId) throws IOException, StoreException {
		NodeEdgeList nodeEdges = nodeEdgeListStorage.load(nodeId);
		if (nodeEdges == null) {
			nodeEdges = new NodeEdgeList(nodeId);
			nodeEdgeListStorage.add(nodeEdges);
		}
		return nodeEdges;
	}

	void removeEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		try {
			DiskEdge edge = getEdge(startNode, endNode, relationship);
			NodeEdgeList nodeEdges = nodeEdgeListStorage.load(startNode.getId());
			nodeEdges.removeEdge(edge.getId());
			nodeEdgeListStorage.add(nodeEdges);
			nodeEdges = nodeEdgeListStorage.load(endNode.getId());
			nodeEdges.removeEdge(edge.getId());
			nodeEdgeListStorage.add(nodeEdges);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} catch (StoreException e) {
			// TODO Auto-generated catch block
		}
	}

	DiskEdge getEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		NodeEdgeList nodeEdges;
		try {
			nodeEdges = nodeEdgeListStorage.load(startNode.getId());
			for (int index = 0, size = nodeEdges.size(); index < size; ++index) {
				if ((nodeEdges.getStartNodeId(index) == startNode.getId()) && (nodeEdges.getEndNodeId(index) == endNode.getId()) && (nodeEdges.getRelationshipId(index) == relationship.getId())) {
					return new DiskEdge(nodeEdges.getEdgeId(index), graph, startNode, endNode, relationship);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	DiskRelationship getRelationship(String name) {
		DiskRelationship relationship = relationships.get(name);
		if (relationship == null) {
			relationship = new DiskRelationship(relationshipCounter++, name);
			relationships.put(name, relationship);
			try {
				relationshipStorage.add(relationship);
			} catch (StoreException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
		return relationship;
	}

	//
	// INTERFACE Store
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskGraph getGraph() {
		return graph;
	}

	//
	// PRIVATE METHODS
	//

	private void loadDiskStore() throws IOException, StoreException {

		relationshipStorage.open();
		nodeStorage.open();
		nodeEdgeListStorage.open();

		for (int directoryIndex = 0; directoryIndex < relationshipStorage.getDirectorySize(); ++directoryIndex) {
			Allocation allocation = relationshipStorage.getAllocation(directoryIndex);
			if (allocation == null) {
				continue;
			}
			DiskRelationship diskRelationship = relationshipStorage.load(allocation.getId());
			relationships.put(diskRelationship.getName(), diskRelationship);
			relationshipCounter = Math.max(relationshipCounter, allocation.getId());
		}
		++relationshipCounter;

		DiskNode rootNode;
		if (nodeStorage.size() > 0) {
			rootNode = nodeStorage.load(0);
			for (int directoryIndex = 0; directoryIndex < nodeStorage.getDirectorySize(); ++directoryIndex) {
				Allocation allocation = nodeStorage.getAllocation(directoryIndex);
				if (allocation == null) {
					continue;
				}
				nodeCounter = Math.max(nodeCounter, allocation.getId());
			}
			++nodeCounter;
		} else {
			rootNode = createNode();
		}

		for (int directoryIndex = 0; directoryIndex < nodeEdgeListStorage.getDirectorySize(); ++directoryIndex) {
			Allocation allocation = nodeEdgeListStorage.getAllocation(directoryIndex);
			if (allocation == null) {
				continue;
			}
			edgeCounter = Math.max(edgeCounter, allocation.getId());
		}
		++edgeCounter;

		graph = new DiskGraph(this);
		graph.setRootNode(rootNode);
		return;

	}

	private static class DiskRelationshipFactory implements Factory<DiskRelationship> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DiskRelationship restore(byte[] buffer) {
			long id = Storable.Utils.getLong(buffer, 0);
			int nameLength = Storable.Utils.getInt(buffer, 8);
			char[] nameCharacters = new char[nameLength];
			for (int index = 0; index < nameLength; ++index) {
				nameCharacters[index] = Storable.Utils.getChar(buffer, 12 + index * 2);
			}
			return new DiskRelationship(id, new String(nameCharacters));
		}

	}

	private static class DiskNodeFactory implements Factory<DiskNode> {

		private final DiskStore store;

		public DiskNodeFactory(DiskStore store) {
			this.store = store;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DiskNode restore(byte[] buffer) {
			long id = Storable.Utils.getLong(buffer, 0);
			DiskNode node = new DiskNode(id, store.getGraph());
			try {
				ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer, 8, buffer.length - 8));
				Map<String, Object> properties = (Map<String, Object>) objectInputStream.readObject();
				for (Entry<String, Object> property : properties.entrySet()) {
					node.set(property.getKey(), property.getValue());
				}
			} catch (IOException ioe1) {
				// TODO Auto-generated catch block
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			}
			return node;
		}

	}

	private static class NodeEdgeList implements Storable {

		private final long nodeId;
		private List<Long> edges = new ArrayList<Long>();
		private List<Long> startNodes = new ArrayList<Long>();
		private List<Long> endNodes = new ArrayList<Long>();
		private List<Long> relationships = new ArrayList<Long>();

		public NodeEdgeList(long nodeId) {
			this.nodeId = nodeId;
		}

		public void addEdge(long edgeId, long startNodeId, long endNodeId, long relationshipId) {
			edges.add(edgeId);
			startNodes.add(startNodeId);
			endNodes.add(endNodeId);
			relationships.add(relationshipId);
		}

		public void removeEdge(long edgeId) {
			int index = edges.indexOf(edgeId);
			if (index == -1) {
				return;
			}
			edges.remove(index);
			startNodes.remove(index);
			endNodes.remove(index);
			relationships.remove(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long getId() {
			return nodeId;
		}

		public int size() {
			return edges.size();
		}

		public long getEdgeId(int index) {
			return edges.get(index);
		}

		public long getStartNodeId(int index) {
			return startNodes.get(index);
		}

		public long getEndNodeId(int index) {
			return endNodes.get(index);
		}

		public long getRelationshipId(int index) {
			return relationships.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] getBuffer() throws StoreException {
			byte[] buffer = new byte[12 + size() * 8 * 4];
			Storable.Utils.putLong(nodeId, buffer, 0);
			Storable.Utils.putInt(size(), buffer, 8);
// System.out.println("Storing " + size() + " NodeEdges in " + buffer.capacity()
// + " bytes.");
			for (int index = 0; index < size(); ++index) {
				Storable.Utils.putLong(edges.get(index), buffer, 12 + index * 32);
				Storable.Utils.putLong(startNodes.get(index), buffer, 20 + index * 32);
				Storable.Utils.putLong(endNodes.get(index), buffer, 28 + index * 32);
				Storable.Utils.putLong(relationships.get(index), buffer, 36 + index * 32);
			}
			return buffer;
		}

	}

	private static class NodeEdgeListFactory implements Factory<NodeEdgeList> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeEdgeList restore(byte[] buffer) {
			long nodeId = Storable.Utils.getLong(buffer, 0);
			NodeEdgeList nodeEdgeList = new NodeEdgeList(nodeId);
			int size = Storable.Utils.getInt(buffer, 8);
// System.out.println("Reading " + size + " NodeEdges, remaining bytes: " +
// buffer.remaining());
			for (int index = 0; index < size; ++index) {
				long edgeId = Storable.Utils.getLong(buffer, 12 + index * 32);
				long startNodeId = Storable.Utils.getLong(buffer, 20 + index * 32);
				long endNodeId = Storable.Utils.getLong(buffer, 28 + index * 32);
				long relationshipId = Storable.Utils.getLong(buffer, 36 + index * 32);
				nodeEdgeList.addEdge(edgeId, startNodeId, endNodeId, relationshipId);
			}
			return nodeEdgeList;
		}

	}

}
