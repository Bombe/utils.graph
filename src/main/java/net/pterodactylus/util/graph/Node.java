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
public class Node {

	private final long id;
	private final Graph graph;
	private final Map<String, Object> properties = new HashMap<String, Object>();

	Node(long id, Graph graph) {
		this.id = id;
		this.graph = graph;
	}

	long getId() {
		return id;
	}

	public Graph getGraph() {
		return graph;
	}

	public Node set(String key, Object value) {
		properties.put(key, value);
		return this;
	}

	public Object get(String key) {
		return properties.get(key);
	}

	//
	// ACTIONS
	//

	public Node link(Node otherNode, Relationship relationship) {
		graph.createEdge(this, otherNode, relationship);
		return this;
	}

	public Node unlink(Node otherNode, Relationship relationship) {
		graph.removeEdge(this, otherNode, relationship);
		return this;
	}

}
