/*
 * utils.graph - Graph.java - Copyright © 2011 David Roden
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

import java.util.Set;

import net.pterodactylus.util.graph.Graph;
import net.pterodactylus.util.graph.Node;
import net.pterodactylus.util.validation.Validation;

/**
 * {@link Graph} implementation that is used by {@link DiskStore}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
class DiskGraph implements Graph {

	/** The disk store. */
	private final DiskStore store;

	/** The root node. */
	private DiskNode rootNode;

	/**
	 * Creates a new graph.
	 *
	 * @param store
	 *            The store this graph belongs to
	 */
	DiskGraph(DiskStore store) {
		this.store = store;
	}

	/**
	 * Returns the root node of this graph.
	 *
	 * @return The root node of this graph
	 */
	@Override
	public DiskNode getRootNode() {
		return rootNode;
	}

	/**
	 * Sets the root node of this graph.
	 *
	 * @param rootNode
	 *            The root node of this graph
	 */
	void setRootNode(DiskNode rootNode) {
		this.rootNode = rootNode;
	}

	//
	// ACCESSORS
	//

	/**
	 * Returns all edges that have the given node as start node and the given
	 * relationship.
	 *
	 * @param node
	 *            The start node
	 * @param relationship
	 *            The relationship
	 * @return All edges that have the given node as start node and the given
	 *         relationship
	 */
	public Set<DiskEdge> getEdgesFrom(DiskNode node, DiskRelationship relationship) {
		return store.getEdges(node, null, relationship);
	}

	/**
	 * Returns all edges that have the given node as end node and the given
	 * relationship.
	 *
	 * @param node
	 *            The end node
	 * @param relationship
	 *            The relationship
	 * @return All edges that have the given node as end node and the given
	 *         relationship
	 */
	public Set<DiskEdge> getEdgesTo(DiskNode node, DiskRelationship relationship) {
		return store.getEdges(null, node, relationship);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskRelationship getRelationship(String name) {
		return store.getRelationship(name);
	}

	//
	// ACTIONS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DiskNode createNode() {
		DiskNode node = store.createNode();
		return node;
	}

	/**
	 * Stores the given node in the store. This method should be called after a
	 * node’s properties have changed.
	 *
	 * @param node
	 *            The node to store
	 */
	public void storeNode(DiskNode node) {
		store.storeNode(node);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeNode(Node node) {
		Validation.begin().isNotNull("Node", node).check().isEqual("Node’s Graph", node.getGraph(), this).isInstanceOf("Node", node, DiskNode.class).check();
		store.removeNode((DiskNode) node);
	}

	/**
	 * Creates a new edge between the given nodes that has the given
	 * relationship.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship of the nodes
	 * @return The newly created edge
	 */
	public DiskEdge createEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		return store.createEdge(startNode, endNode, relationship);
	}

	/**
	 * Removes the given edge.
	 *
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship of the nodes
	 */
	public void removeEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		store.removeEdge(startNode, endNode, relationship);
	}

}
