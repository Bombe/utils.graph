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
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface Node {

	/**
	 * Returns the graph this node belongs to.
	 *
	 * @return The graph this node belongs to
	 */
	public Graph getGraph();

	/**
	 * Sets a property on this node.
	 *
	 * @param key
	 *            The key of the property
	 * @param value
	 *            The value of the property
	 * @return This node
	 * @throws GraphException
	 *             if the property can not be stored
	 */
	public Node set(String key, Object value) throws GraphException;

	/**
	 * Returns the value of the property with the given key.
	 *
	 * @param key
	 *            The key of the property
	 * @return The value of the property
	 */
	public Object get(String key);

	/**
	 * Links this node to the given node with the given relationship. If such a
	 * link does already exist no new link is created.
	 *
	 * @param otherNode
	 *            The node to link to
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 * @throws GraphException
	 *             if the edge can not be created
	 */
	public Node link(Node otherNode, Relationship relationship) throws GraphException;

	/**
	 * Links this node to the given node with the given relationship. If such a
	 * link does already exist no new link is created.
	 *
	 * @param otherNode
	 *            The node to link to
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 * @throws GraphException
	 *             if the edge can not be created
	 */
	public Node link(Node otherNode, String relationship) throws GraphException;

	/**
	 * Removes the link to the given node with the given relationship.
	 *
	 * @param otherNode
	 *            The node to sever the link from
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 * @throws GraphException
	 *             if the edge can not be removed
	 */
	public Node unlink(Node otherNode, Relationship relationship) throws GraphException;

	/**
	 * Removes the link to the given node with the given relationship.
	 *
	 * @param otherNode
	 *            The node to sever the link from
	 * @param relationship
	 *            The relationship of the link
	 * @return This node
	 * @throws GraphException
	 *             if the edge can not be removed
	 */
	public Node unlink(Node otherNode, String relationship) throws GraphException;

	/**
	 * Returns all edges that have this node as the end node.
	 *
	 * @see Edge#getEndNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the end node
	 * @throws GraphException
	 *             if the links can not be retrieved
	 */
	public Set<? extends Edge> getIncomingLinks(Relationship relationship) throws GraphException;

	/**
	 * Returns all edges that have this node as the end node.
	 *
	 * @see Edge#getEndNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the end node
	 * @throws GraphException
	 *             if the links can not be retrieved
	 */
	public Set<? extends Edge> getIncomingLinks(String relationship) throws GraphException;

	/**
	 * Returns all edges that have this node as the start node.
	 *
	 * @see Edge#getStartNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the start node
	 * @throws GraphException
	 *             if the links can not be retrieved
	 */
	public Set<? extends Edge> getOutgoingLinks(Relationship relationship) throws GraphException;

	/**
	 * Returns all edges that have this node as the start node.
	 *
	 * @see Edge#getStartNode()
	 * @param relationship
	 *            The relationship of the edges
	 * @return The edges that have this node as the start node
	 * @throws GraphException
	 *             if the links can not be retrieved
	 */
	public Set<? extends Edge> getOutgoingLinks(String relationship) throws GraphException;

}
