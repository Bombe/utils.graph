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

import net.pterodactylus.util.graph.DefaultRelationship;
import net.pterodactylus.util.graph.Relationship;
import net.pterodactylus.util.graph.StoreException;

/**
 * {@link Relationship} implementation that is used by {@link DiskStore}. It
 * adds an ID to the relationship that is used when comparing relationships with
 * {@link Object#equals(Object)}.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
class DiskRelationship extends DefaultRelationship<DiskGraph, DiskNode, DiskEdge, DiskRelationship> implements Storable {

	/** The ID of the relationship. */
	private final long id;

	/**
	 * Creates a new relationship.
	 *
	 * @param id
	 *            The ID of the relationship
	 * @param name
	 *            The name of the relationship
	 */
	DiskRelationship(long id, String name) {
		super(name);
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
		return (int) ((id >> 32) ^ (id & 0xffffffff));
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

	//
	// INTERFACE Storable
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getBuffer() throws StoreException {
		byte[] buffer = new byte[12 + getName().length() * 2];
		Storable.Utils.putLong(id, buffer, 0);
		Storable.Utils.putInt(getName().length(), buffer, 8);
		int index = 0;
		for (char c : getName().toCharArray()) {
			Storable.Utils.putChar(c, buffer, 12 + index * 2);
			++index;
		}
		return buffer;
	}

}
