/*
 * utils.graph - Edge.java - Copyright © 2011 David Roden
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
 * An edge is a directed connection between two {@link Node}s (one start node
 * and one end node) that have a certain {@link Relationship}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface Edge {

	/**
	 * Returns the graph this edge belongs to.
	 *
	 * @return The graph of the edge
	 */
	public Graph getGraph();

	/**
	 * Returns the start node of this edge.
	 *
	 * @return The start node of this edge
	 */
	public Node getStartNode();

	/**
	 * Returns the end node of this edge.
	 *
	 * @return The end node of this edge
	 */
	public Node getEndNode();

	/**
	 * Returns the relationship between the two nodes.
	 *
	 * @return The relationship between the two nodes
	 */
	public Relationship getRelationship();

}
