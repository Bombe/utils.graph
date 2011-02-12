/*
 * utils.graph - Allocation.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.StoreException;

class Allocation implements Storable {

	public static final Factory FACTORY = new Factory();

	private final long id;
	private final int position;
	private final int size;

	public Allocation(long id, int position, int size) {
		this.id = id;
		this.position = position;
		this.size = size;
	}

	public long getId() {
		return id;
	}

	public int getPosition() {
		return position;
	}

	public int getSize() {
		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] getBuffer() throws StoreException {
		byte[] buffer = new byte[16];
		Storable.Utils.putLong(id, buffer, 0);
		Storable.Utils.putInt(position, buffer, 8);
		Storable.Utils.putInt(size, buffer, 12);
		return buffer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("%s[id=%d,position=%d,size=%d]", getClass().getName(), id, position, size);
	}

	public static class Factory implements Storable.Factory<Allocation> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Allocation restore(byte[] buffer) {
			return new Allocation(Storable.Utils.getLong(buffer, 0), Storable.Utils.getInt(buffer, 8), Storable.Utils.getInt(buffer, 12));
		}

	}

}
