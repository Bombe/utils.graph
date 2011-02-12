/*
 * utils.graph - Stroable.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.StoreException;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface Storable {

	public long getId();

	public ByteBuffer getBuffer() throws StoreException;

	public static interface Factory<T> {

		public T restore(ByteBuffer buffer);

	}

	static class Utils {

		public static void copyLong(long value, byte[] bytes, int position) {
			bytes[position + 0] = (byte) (value & 0xff);
			bytes[position + 1] = (byte) ((value >> 8) & 0xff);
			bytes[position + 2] = (byte) ((value >> 16) & 0xff);
			bytes[position + 3] = (byte) ((value >> 24) & 0xff);
			bytes[position + 4] = (byte) ((value >> 32) & 0xff);
			bytes[position + 5] = (byte) ((value >> 40) & 0xff);
			bytes[position + 6] = (byte) ((value >> 48) & 0xff);
			bytes[position + 7] = (byte) ((value >> 56) & 0xff);
		}

	}

}
