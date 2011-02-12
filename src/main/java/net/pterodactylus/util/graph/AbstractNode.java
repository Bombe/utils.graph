/*
 * utils.graph - Node.java - Copyright © 2011 David Roden
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

import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public abstract class AbstractNode<G extends Graph<G, N, E, R>, N extends Node<G, N, E, R>, E extends Edge<G, N, E, R>, R extends Relationship<G, N, E, R>> implements Node<G, N, E, R> {

	private transient final G graph;
	private final Map<String, Object> properties = new HashMap<String, Object>();

	protected AbstractNode(G graph) {
		this.graph = graph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public G getGraph() {
		return graph;
	}

	@Override
	public Node set(String key, Object value) {
		properties.put(key, value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String key) {
		return properties.get(key);
	}

	protected Map<String, Object> getProperties() {
		return properties;
	}

}
