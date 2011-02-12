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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.pterodactylus.util.graph.GraphException;
import net.pterodactylus.util.graph.Store;
import net.pterodactylus.util.storage.Allocation;
import net.pterodactylus.util.storage.Factory;
import net.pterodactylus.util.storage.Storable;
import net.pterodactylus.util.storage.Storage;
import net.pterodactylus.util.storage.StorageException;

/**
 * {@link Store} implementation that stores the complete graph on disk.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskStore implements Store<DiskGraph, DiskNode, DiskEdge, DiskRelationship> {

	/** {@link Factory} that can create {@link DiskRelationship}s. */
	@SuppressWarnings("synthetic-access")
	public final Factory<DiskRelationship> DISK_RELATIONSHIP_FACTORY = new DiskRelationshipFactory();

	/** {@link Factory} that can create {@link DiskNode}s. */
	public final Factory<DiskNode> DISK_NODE_FACTORY = new DiskNodeFactory(this);

	/** {@link Factory} that can create {@link NodeEdgeList}s. */
	@SuppressWarnings("synthetic-access")
	private static final Factory<NodeEdgeList> NODE_EDGE_LIST_FACTORY = new NodeEdgeListFactory();

	/** ID Counter for new nodes. */
	private static long nodeCounter = 0;

	/** ID counter for new edges. */
	private static long edgeCounter = 0;

	/** ID counter for new relationships. */
	private static long relationshipCounter = 0;

	/** The graph of this store. */
	private DiskGraph graph;

	/** Cache for relationships. */
	private final Map<String, DiskRelationship> relationships = new HashMap<String, DiskRelationship>();

	/** The storage for the node-edge lists. */
	private final Storage<NodeEdgeList> nodeEdgeListStorage;

	/** The storage for the nodes. */
	private final Storage<DiskNode> nodeStorage;

	/** The storage for the relationships. */
	private final Storage<DiskRelationship> relationshipStorage;

	/**
	 * Creates a new disk store in or loads a disk store from the given
	 * directory.
	 *
	 * @param directory
	 *            The directory to create the store in or to load the store from
	 * @throws GraphException
	 *             if the store can not be created in or loaded from the given
	 *             directory
	 */
	public DiskStore(String directory) throws GraphException {
		this(new File(directory));
	}

	/**
	 * Creates a new disk store in or loads a disk store from the given
	 * directory.
	 *
	 * @param directory
	 *            The directory to create the store in or to load the store from
	 * @throws GraphException
	 *             if the store can not be created in or loaded from the given
	 *             directory
	 */
	public DiskStore(File directory) throws GraphException {
		if (!directory.exists() || !directory.isDirectory() || !directory.canWrite()) {
			throw new GraphException("“" + directory + "” is not a writable directory.");
		}
		try {
			relationshipStorage = new Storage<DiskRelationship>(128, DISK_RELATIONSHIP_FACTORY, directory, "relationships");
			nodeStorage = new Storage<DiskNode>(512, DISK_NODE_FACTORY, directory, "nodes");
			nodeEdgeListStorage = new Storage<NodeEdgeList>(64, NODE_EDGE_LIST_FACTORY, directory, "edges");
			loadDiskStore();
		} catch (IOException ioe1) {
			throw new GraphException("Could not create store in or load store from “" + directory + "”!", ioe1);
		}
	}

	//
	// ACTIONS
	//

	/**
	 * Creates a new node.
	 *
	 * @return The new node
	 */
	DiskNode createNode() {
		DiskNode node = new DiskNode(nodeCounter++, graph);
		storeNode(node);
		return node;
	}

	/**
	 * Loads the node with the given ID.
	 *
	 * @param nodeId
	 *            The ID of the node
	 * @return The node, or {@code null} if there is no node with the given ID
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	DiskNode getNode(long nodeId) throws IOException {
		return nodeStorage.load(nodeId);
	}

	/**
	 * Removes the given node and all edges to or from it from the storage.
	 *
	 * @param node
	 *            The node to remove
	 */
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

	/**
	 * Stores the node in the storage. This method should be called after a node
	 * was created or its properties were changed.
	 *
	 * @param node
	 *            The node to store
	 */
	void storeNode(DiskNode node) {
		try {
			nodeStorage.add(node);
		} catch (StorageException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * Creates a new edge.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the two nodes
	 * @return The new edge
	 */
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
		} catch (StorageException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	/**
	 * Returns all edges that match the given requirements. Only one of
	 * {@code startNode} and {@code endNode} may be {@code null}.
	 *
	 * @param startNode
	 *            The start node of the edge (or {@code null} to match all start
	 *            nodes)
	 * @param endNode
	 *            The end node of the edge (or {@code null} to match all end
	 *            nodes)
	 * @param relationship
	 *            The relationship of the edge
	 * @return The matching edges
	 */
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
				edges.add(new DiskEdge(nodeEdges.getEdgeId(index), graph, loadedStartNode, loadedEndNode, relationship));
			}
			return edges;
		} catch (IOException ioe1) {
			/* TODO */
			return null;
		}
	}

	/**
	 * Returns the node-edge list for the node with the given ID.
	 *
	 * @param nodeId
	 *            The ID of the node
	 * @return The node-edge list for the given node
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws StorageException
	 *             if a store error occurs
	 */
	NodeEdgeList getNodeEdgeList(long nodeId) throws IOException, StorageException {
		NodeEdgeList nodeEdges = nodeEdgeListStorage.load(nodeId);
		if (nodeEdges == null) {
			nodeEdges = new NodeEdgeList(nodeId);
			nodeEdgeListStorage.add(nodeEdges);
		}
		return nodeEdges;
	}

	/**
	 * Removes the edge with the given relationship between the given nodes.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship of the edge
	 */
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
		} catch (StorageException e) {
			// TODO Auto-generated catch block
		}
	}

	/**
	 * Returns the edge with the given relationship between the given nodes.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the nodes
	 * @return The edge, or {@code null} if there is no such edge
	 */
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

	/**
	 * Returns the relationship with the given name. If no relationship with the
	 * given name exists, one is created.
	 *
	 * @param name
	 *            The name of the relationship
	 * @return The relationship with the given name
	 */
	DiskRelationship getRelationship(String name) {
		DiskRelationship relationship = relationships.get(name);
		if (relationship == null) {
			relationship = new DiskRelationship(relationshipCounter++, name);
			relationships.put(name, relationship);
			try {
				relationshipStorage.add(relationship);
			} catch (StorageException e) {
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

	/**
	 * Attempts to load a store from the disk.
	 *
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws GraphException
	 *             if a storage error occurs
	 */
	private void loadDiskStore() throws IOException, GraphException {

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

	/**
	 * {@link Factory} implementation that can create {@link DiskRelationship}
	 * objects.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
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

	/**
	 * {@link Factory} implementation that can create {@link DiskNode}s.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	private static class DiskNodeFactory implements Factory<DiskNode> {

		/** The store. */
		private final DiskStore store;

		/**
		 * Creates a new disk node factory.
		 *
		 * @param store
		 *            The store the nodes belong to
		 */
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
				@SuppressWarnings("unchecked")
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

	/**
	 * A node-edge list is a node-specific list that contains all edges from or
	 * to the given node and references to the nodes on the respective other
	 * ends of the edge.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	private static class NodeEdgeList implements Storable {

		/** The ID of the node. */
		private final long nodeId;

		/** The IDs of all connecting edges. */
		private List<Long> edges = new ArrayList<Long>();

		/** The start nodes of the edges. */
		private List<Long> startNodes = new ArrayList<Long>();

		/** The end nodes of the edges. */
		private List<Long> endNodes = new ArrayList<Long>();

		/** The relationships between the nodes. */
		private List<Long> relationships = new ArrayList<Long>();

		/**
		 * Creates a new node-edge list for the node with the given ID.
		 *
		 * @param nodeId
		 *            The ID of the node
		 */
		public NodeEdgeList(long nodeId) {
			this.nodeId = nodeId;
		}

		/**
		 * Adds an edge to this node-edge list.
		 *
		 * @param edgeId
		 *            The ID of the edge to add
		 * @param startNodeId
		 *            The ID of the start node of the edge
		 * @param endNodeId
		 *            The ID of the end node of the edge
		 * @param relationshipId
		 *            The ID of the relationship between the nodes
		 */
		public void addEdge(long edgeId, long startNodeId, long endNodeId, long relationshipId) {
			edges.add(edgeId);
			startNodes.add(startNodeId);
			endNodes.add(endNodeId);
			relationships.add(relationshipId);
		}

		/**
		 * Removes the edge with the given ID.
		 *
		 * @param edgeId
		 *            The ID of the edge to remove
		 */
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

		/**
		 * Returns the number of edges in this list.
		 *
		 * @return The number of edges
		 */
		public int size() {
			return edges.size();
		}

		/**
		 * Returns the ID of the edge at the given index.
		 *
		 * @param index
		 *            The index of the edge
		 * @return The ID of the edge at the given index
		 */
		public long getEdgeId(int index) {
			return edges.get(index);
		}

		/**
		 * Returns the ID of the start node at the given index.
		 *
		 * @param index
		 *            The index of the start node
		 * @return The ID of the start node at the given index
		 */
		public long getStartNodeId(int index) {
			return startNodes.get(index);
		}

		/**
		 * Returns the ID of the end node at the given index.
		 *
		 * @param index
		 *            The index of the end node
		 * @return The ID of the end node at the given index
		 */
		public long getEndNodeId(int index) {
			return endNodes.get(index);
		}

		/**
		 * Returns the ID of the relationship at the given index.
		 *
		 * @param index
		 *            The index of the relationship
		 * @return The ID of the relationship at the given index
		 */
		public long getRelationshipId(int index) {
			return relationships.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] getBuffer() {
			byte[] buffer = new byte[12 + size() * 8 * 4];
			Storable.Utils.putLong(nodeId, buffer, 0);
			Storable.Utils.putInt(size(), buffer, 8);
			for (int index = 0; index < size(); ++index) {
				Storable.Utils.putLong(edges.get(index), buffer, 12 + index * 32);
				Storable.Utils.putLong(startNodes.get(index), buffer, 20 + index * 32);
				Storable.Utils.putLong(endNodes.get(index), buffer, 28 + index * 32);
				Storable.Utils.putLong(relationships.get(index), buffer, 36 + index * 32);
			}
			return buffer;
		}

	}

	/**
	 * {@link Factory} implementation that can create {@link NodeEdgeList}s.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	private static class NodeEdgeListFactory implements Factory<NodeEdgeList> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeEdgeList restore(byte[] buffer) {
			long nodeId = Storable.Utils.getLong(buffer, 0);
			NodeEdgeList nodeEdgeList = new NodeEdgeList(nodeId);
			int size = Storable.Utils.getInt(buffer, 8);
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
