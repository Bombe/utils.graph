/*
 * utils.graph - GraphException.java - Copyright © 2011 David Roden
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
 * Base exception for all {@link Graph}-related errors.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class GraphException extends Exception {

	/**
	 * Creates a new graph exception.
	 */
	public GraphException() {
		super();
	}

	/**
	 * Creates a new graph exception.
	 *
	 * @param message
	 *            The message of the exception
	 */
	public GraphException(String message) {
		super(message);
	}

	/**
	 * Creates a new graph exception.
	 *
	 * @param cause
	 *            The cause of the exception
	 */
	public GraphException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates a new graph exception.
	 *
	 * @param message
	 *            The message of the exception
	 * @param cause
	 *            The cause of the exception
	 */
	public GraphException(String message, Throwable cause) {
		super(message, cause);
	}

}
