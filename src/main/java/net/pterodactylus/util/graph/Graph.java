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

package net.pterodactylus.util.graph;

/**
 * A graph contains the root {@link Node} of the graph which is used to access
 * all other nodes.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface Graph {

	/**
	 * Returns the root node of the graph.
	 *
	 * @return The root node of the graph
	 */
	public Node getRootNode();

	/**
	 * Creates a new node. The created node does not have any links to other
	 * nodes but is already persisted.
	 *
	 * @return The new node
	 */
	public Node createNode();

	/**
	 * Removes this node from the graph. This will also remove all edges that
	 * are connected to the node.
	 *
	 * @param node
	 *            The node to remove
	 */
	public void removeNode(Node node);

	/**
	 * Returns the relationship with the given name. If no relationship with the
	 * given name exists, it is created.
	 *
	 * @param name
	 *            The name of the relationship
	 * @return The relationship with the given name
	 */
	public Relationship getRelationship(String name);

}
