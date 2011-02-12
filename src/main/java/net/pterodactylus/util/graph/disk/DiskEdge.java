/*
 * utils.graph - DiskEdge.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.AbstractEdge;
import net.pterodactylus.util.graph.Edge;

/**
 * {@link Edge} implementation that is used by {@link DiskStore}. In addition to
 * extends {@link AbstractEdge} it adds an ID to the edge which is used for
 * comparison with {@link Object#equals(Object)}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
class DiskEdge extends AbstractEdge {

	/** The ID of the edge. */
	private final long id;

	/**
	 * Creates a new edge.
	 *
	 * @param id
	 *            The ID of the edge
	 * @param graph
	 *            The graph the edge belongs to
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the two nodes
	 */
	public DiskEdge(long id, DiskGraph graph, DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		super(graph, startNode, endNode, relationship);
		this.id = id;
	}

	/**
	 * Returns the ID of the edge.
	 *
	 * @return The ID of the edge
	 */
	public long getId() {
		return id;
	}

	//
	// OBJECT METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) ((id & 0xffffffff) ^ ((id >> 32) & 0xffffffff));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (object instanceof DiskEdge) {
			return ((DiskEdge) object).id == id;
		}
		return super.equals(object);
	}

}
