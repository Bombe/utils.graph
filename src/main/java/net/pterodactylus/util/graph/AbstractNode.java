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
 * Abstract base implementation of a {@link Node}. This implementation stores
 * the {@link Graph} of a node and its properties.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public abstract class AbstractNode implements Node {

	/** The graph this node belongs to. */
	private transient final Graph graph;

	/** The properties of this node. */
	private final Map<String, Object> properties = new HashMap<String, Object>();

	/**
	 * Creates a new abstract node.
	 *
	 * @param graph
	 *            The graph the node belongs to
	 */
	protected AbstractNode(Graph graph) {
		this.graph = graph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Graph getGraph() {
		return graph;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * Returns the properties of this node.
	 *
	 * @return The properties of this node
	 */
	protected Map<String, Object> getProperties() {
		return properties;
	}

}
