/*
 * utils.graph - Node.java - Copyright © 2011 David Roden
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

import java.util.Set;

/**
 * A node is the main object of a {@link Graph}. It contains methods to create
 * and sever links to and from other nodes as well as storing custom properties
 * that can be used in an application to create the domain objects.
 *
 * @param <G>
 *            The type of the graph
 * @param <N>
 *            The type of the node
 * @param <E>
 *            The type of the edge
 * @param <R>
 *            The type of the relationship
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface Node<G extends Graph<G, N, E, R>, N extends Node<G, N, E, R>, E extends Edge<G, N, E, R>, R extends Relationship<G, N, E, R>> {

	/**
	 * Returns the graph this node belongs to.
	 *
	 * @return The graph this node belongs to
	 */
	public G getGraph();

	/**
	 * Sets a property on this node.
	 *
	 * @param key
	 *            The key of the property
	 * @param value
	 *            The value of the property
	 * @return This node
	 */
	public N set(String key, Object value);

	/**
	 * Returns the value of the property with the given key.
	 *
	 * @param key
	 *            The key of the property
	 * @return The value of the property
	 */
	public Object get(String key);

	/**
	 * Links this node to the given node with the given relationship.
	 *
	 * @param otherNode
	 *            The node to link to
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 */
	public N link(N otherNode, R relationship);

	/**
	 * Removes the link to the given node with the given relationship.
	 *
	 * @param otherNode
	 *            The node to sever the link from
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 */
	public N unlink(N otherNode, R relationship);

	/**
	 * Returns all edges that have this node as the end node.
	 *
	 * @see Edge#getEndNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the end node
	 */
	public Set<E> getIncomingLinks(R relationship);

	/**
	 * Returns all edges that have this node as the start node.
	 *
	 * @see Edge#getStartNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the start node
	 */
	public Set<E> getOutgoingLinks(R relationship);

}
