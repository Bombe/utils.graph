/*
 * utils.graph - Edge.java - Copyright © 2011 David Roden
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

import net.pterodactylus.util.validation.Validation;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class Edge {

	private final Graph graph;
	private final Node startNode;
	private final Node endNode;
	private final Relationship relationship;

	Edge(Graph graph, Node startNode, Node endNode, Relationship relationship) {
		Validation.begin().isNotNull("Graph", graph).isNotNull("Start Node", startNode).isNotNull("End Node", endNode).isNotNull("Relationship", relationship).check().isEqual("Start Node’s Graph", startNode.getGraph(), graph).isEqual("End Node’s Graph", endNode.getGraph(), graph).check();
		this.graph = graph;
		this.startNode = startNode;
		this.endNode = endNode;
		this.relationship = relationship;
	}

	public Graph getGraph() {
		return graph;
	}

	public Node getStartNode() {
		return startNode;
	}

	public Node getEndNode() {
		return endNode;
	}

	public Relationship getRelationship() {
		return relationship;
	}

	//
	// OBJECT METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return graph.hashCode() ^ startNode.hashCode() ^ endNode.hashCode() ^ relationship.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Edge)) {
			return false;
		}
		Edge edge = (Edge) object;
		return graph.equals(edge.graph) && startNode.equals(edge.startNode) && endNode.equals(edge.endNode) && relationship.equals(edge.relationship);
	}

}
