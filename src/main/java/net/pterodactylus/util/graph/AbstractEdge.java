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
 * Abstract base implementation of an {@link Edge}. This implementation stores
 * the {@link Graph}, the two {@link Node}s, and the {@link Relationship} of the
 * edge.
 *
 * @param <G>
 *            The type of the graph
 * @param <N>
 *            The type of the node
 * @param <E>
 *            The type of the edge
 * @param <R>
 *            The type of the relationship
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public abstract class AbstractEdge<G extends Graph<G, N, E, R>, N extends Node<G, N, E, R>, E extends Edge<G, N, E, R>, R extends Relationship<G, N, E, R>> implements Edge<G, N, E, R> {

	/** The graph this edge belongs to. */
	private final G graph;

	/** The start node of this edge. */
	private final N startNode;

	/** The end node of this edge. */
	private final N endNode;

	/** The relationship between the two nodes. */
	private final R relationship;

	/**
	 * Creates a new abstract edge.
	 *
	 * @param graph
	 *            The graph the edge belongs to
	 * @param startNode
	 *            The start node of the edge
	 * @param endNode
	 *            The end node of the edge
	 * @param relationship
	 *            The relationship between the two nodes
	 */
	protected AbstractEdge(G graph, N startNode, N endNode, R relationship) {
		Validation.begin().isNotNull("Graph", graph).isNotNull("Start Node", startNode).isNotNull("End Node", endNode).isNotNull("Relationship", relationship).check().isEqual("Start Node’s Graph", startNode.getGraph(), graph).isEqual("End Node’s Graph", endNode.getGraph(), graph).check();
		this.graph = graph;
		this.startNode = startNode;
		this.endNode = endNode;
		this.relationship = relationship;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public G getGraph() {
		return graph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getStartNode() {
		return startNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getEndNode() {
		return endNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public R getRelationship() {
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
		Edge<?, ?, ?, ?> edge = (Edge<?, ?, ?, ?>) object;
		return graph.equals(edge.getGraph()) && startNode.equals(edge.getStartNode()) && endNode.equals(edge.getEndNode()) && relationship.equals(edge.getRelationship());
	}

}
