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

import java.nio.ByteBuffer;

import net.pterodactylus.util.graph.AbstractEdge;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskEdge extends AbstractEdge<DiskGraph, DiskNode, DiskEdge, DiskRelationship> implements Storable {

	private final long id;

	public DiskEdge(long id, DiskGraph graph, DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		super(graph, startNode, endNode, relationship);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	//
	// INTERFACE Storable
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getBuffer() {
		ByteBuffer content = ByteBuffer.allocate(24);
		content.putLong((getStartNode()).getId());
		content.putLong((getEndNode()).getId());
		content.putLong((getRelationship()).getId());
		content.flip();
		return content;
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

	public static class EdgeShell {

		private final long startNodeId;
		private final long endNodeId;
		private final long relationshipId;

		public EdgeShell(long startNodeId, long endNodeId, long relationshipId) {
			this.startNodeId = startNodeId;
			this.endNodeId = endNodeId;
			this.relationshipId = relationshipId;
		}

		public long getStartNodeId() {
			return startNodeId;
		}

		public long getEndNodeId() {
			return endNodeId;
		}

		public long getRelationshipId() {
			return relationshipId;
		}
	}

	public static class Factory implements Storable.Factory<EdgeShell> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EdgeShell restore(ByteBuffer buffer) {
			return new EdgeShell(buffer.getLong(), buffer.getLong(), buffer.getLong());
		}

	}

}
