/*
 * utils.graph - MemoryRelationship.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.graph.Relationship;

/**
 * Memory-based {@link Relationship} implementation.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class MemoryRelationship implements Relationship {

	/** The name of the relationship. */
	private final String name;

	/**
	 * Creates a new relationship.
	 *
	 * @param name
	 *            The name of the relationship
	 */
	MemoryRelationship(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	//
	// OBJECT METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MemoryRelationship)) {
			return false;
		}
		return ((MemoryRelationship) object).name.equals(name);
	}

}
