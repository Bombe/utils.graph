/*
 * utils.graph - Relationship.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.DefaultRelationship;
import net.pterodactylus.util.graph.StoreException;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskRelationship extends DefaultRelationship<DiskGraph, DiskNode, DiskEdge, DiskRelationship> implements Storable {

	private final long id;

	DiskRelationship(long id, String name) {
		super(name);
		this.id = id;
	}

	@Override
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DiskRelationship)) {
			return false;
		}
		return ((DiskRelationship) object).id == id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getBuffer() throws StoreException {
		ByteBuffer buffer = ByteBuffer.allocate(12 + getName().length() * 2);
		buffer.putLong(id);
		buffer.putInt(getName().toCharArray().length);
		for (char c : getName().toCharArray()) {
			buffer.putChar(c);
		}
		buffer.flip();
		return buffer;
	}

}
