/*
 * utils.graph - MemoryEdge.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.AbstractEdge;
import net.pterodactylus.util.graph.Edge;

/**
 * Memory-based {@link Edge} implementation.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class MemoryEdge extends AbstractEdge {

	/**
	 * Creates a new edge.
	 *
	 * @param graph
	 *            The graph this edge belongs to
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship of the edge
	 */
	MemoryEdge(MemoryGraph graph, MemoryNode startNode, MemoryNode endNode, MemoryRelationship relationship) {
		super(graph, startNode, endNode, relationship);
	}

}
