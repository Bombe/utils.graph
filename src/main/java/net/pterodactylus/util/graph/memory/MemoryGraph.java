/*
 * utils.graph - MemoryGraph.java - Copyright © 2011 David Roden
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

package net.pterodactylus.util.graph.memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.pterodactylus.util.graph.Edge;
import net.pterodactylus.util.graph.Graph;
import net.pterodactylus.util.graph.GraphException;
import net.pterodactylus.util.graph.Node;
import net.pterodactylus.util.graph.Relationship;
import net.pterodactylus.util.validation.Validation;

/**
 * Memory-based {@link Graph} implementation.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class MemoryGraph implements Graph {

	/** All relationships. */
	private static final Map<String, MemoryRelationship> relationships = new HashMap<String, MemoryRelationship>();

	/** The edges for all nodes and relationships. */
	private static final Map<MemoryNode, Map<MemoryRelationship, Set<Edge>>> nodeRelationshipEdges = new HashMap<MemoryNode, Map<MemoryRelationship, Set<Edge>>>();

	/** The root node of the graph. */
	private final MemoryNode rootNode = new MemoryNode(this);

	//
	// PACKAGE-PROTECTED METHODS
	//

	/**
	 * Returns all edges for the given node and relationship. Note that this
	 * method will all edges that are connected to the given node, as start node
	 * or as end node.
	 *
	 * @param node
	 *            The node to get the edges for
	 * @param relationship
	 *            The relationship of the edges
	 * @return All edges for the given node with the given relationship
	 */
	Set<Edge> getEdges(MemoryNode node, MemoryRelationship relationship) {
		Map<MemoryRelationship, Set<Edge>> relationshipEdges = nodeRelationshipEdges.get(node);
		if (relationshipEdges != null) {
			Set<Edge> edges = relationshipEdges.get(relationship);
			if (edges != null) {
				return edges;
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Creates an edge between the given nodes with the given relationship.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the nodes
	 * @return {@code true} if a new edge was created, {@code false} if an edge
	 *         already existed
	 */
	boolean createEdge(MemoryNode startNode, MemoryNode endNode, MemoryRelationship relationship) {
		boolean changed = false;
		MemoryEdge edge = new MemoryEdge(this, startNode, endNode, relationship);

		/* add edge for start node. */
		Map<MemoryRelationship, Set<Edge>> relationshipEdges = nodeRelationshipEdges.get(startNode);
		if (relationshipEdges == null) {
			relationshipEdges = new HashMap<MemoryRelationship, Set<Edge>>();
			nodeRelationshipEdges.put(startNode, relationshipEdges);
		}
		Set<Edge> edges = relationshipEdges.get(relationship);
		if (edges == null) {
			edges = new HashSet<Edge>();
			relationshipEdges.put(relationship, edges);
		}
		changed |= edges.add(edge);

		/* add edge for end node. */
		relationshipEdges = nodeRelationshipEdges.get(endNode);
		if (relationshipEdges == null) {
			relationshipEdges = new HashMap<MemoryRelationship, Set<Edge>>();
			nodeRelationshipEdges.put(endNode, relationshipEdges);
		}
		edges = relationshipEdges.get(relationship);
		if (edges == null) {
			edges = new HashSet<Edge>();
			relationshipEdges.put(relationship, edges);
		}
		changed |= edges.add(edge);

		return changed;
	}

	/**
	 * Removes the edge between the given nodes with the given relationship.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the nodes
	 * @return {@code true} if an edge was removed, {@code false} if there was
	 *         no edge to remove
	 */
	boolean removeEdge(MemoryNode startNode, MemoryNode endNode, MemoryRelationship relationship) {
		boolean changed = false;
		MemoryEdge edge = new MemoryEdge(this, startNode, endNode, relationship);

		/* remove edge for start node. */
		Map<MemoryRelationship, Set<Edge>> relationshipEdges = nodeRelationshipEdges.get(startNode);
		if (relationshipEdges != null) {
			Set<Edge> edges = relationshipEdges.get(relationship);
			if (edges != null) {
				changed = edges.remove(edge);
				if (edges.isEmpty()) {
					relationshipEdges.remove(relationship);
					if (relationshipEdges.isEmpty()) {
						nodeRelationshipEdges.remove(startNode);
					}
				}
			}
		}

		/* remove edge for end node. */
		relationshipEdges = nodeRelationshipEdges.get(endNode);
		if (relationshipEdges != null) {
			Set<Edge> edges = relationshipEdges.get(relationship);
			if (edges != null) {
				changed = edges.remove(edge);
				if (edges.isEmpty()) {
					relationshipEdges.remove(relationship);
					if (relationshipEdges.isEmpty()) {
						nodeRelationshipEdges.remove(endNode);
					}
				}
			}
		}

		return changed;
	}

	//
	// INTERFACE Graph
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getRootNode() {
		return rootNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node createNode() throws GraphException {
		return new MemoryNode(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeNode(Node node) throws GraphException {
		Validation.begin().isNotNull("Node", node).check().isInstanceOf("Node", node, MemoryNode.class).isEqual("Node’s Graph", node.getGraph(), this).check();
		Map<MemoryRelationship, Set<Edge>> relationshipEdges = nodeRelationshipEdges.remove(node);
		if (relationshipEdges == null) {
			return;
		}
		for (MemoryRelationship relationship : new HashSet<MemoryRelationship>(relationshipEdges.keySet())) {
			for (Edge edge : new HashSet<Edge>(relationshipEdges.get(relationship))) {
				removeEdge((MemoryNode) edge.getStartNode(), (MemoryNode) edge.getEndNode(), relationship);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Relationship getRelationship(String name) throws GraphException {
		synchronized (relationships) {
			MemoryRelationship relationship = relationships.get(name);
			if (relationship == null) {
				relationship = new MemoryRelationship(name);
				relationships.put(name, relationship);
			}
			return relationship;
		}
	}

}
