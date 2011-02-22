/*
 * utils.graph - MemoryNode.java - Copyright © 2011 David Roden
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

import java.util.Set;

import net.pterodactylus.util.filter.Filter;
import net.pterodactylus.util.filter.Filters;
import net.pterodactylus.util.graph.AbstractNode;
import net.pterodactylus.util.graph.Edge;
import net.pterodactylus.util.graph.GraphException;
import net.pterodactylus.util.graph.Node;
import net.pterodactylus.util.graph.Relationship;
import net.pterodactylus.util.validation.Validation;

/**
 * Memory-based {@link Node} implementation.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class MemoryNode extends AbstractNode {

	/** The counter for all created nodes. */
	private static long counter = 0;

	/** The ID of this node. */
	private final long id = counter++;

	/**
	 * Creates a new node that belongs to the given graph.
	 *
	 * @param graph
	 *            The graph this node belongs to
	 */
	MemoryNode(MemoryGraph graph) {
		super(graph);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean link(Node otherNode, Relationship relationship) throws GraphException {
		Validation.begin().isNotNull("Other Node", otherNode).isNotNull("Relationship", relationship).check().isInstanceOf("Other Node", otherNode, MemoryNode.class).isInstanceOf("Relationship", relationship, MemoryRelationship.class).isEqual("Other Node’s Graph", otherNode.getGraph(), getGraph()).check();
		return ((MemoryGraph) getGraph()).createEdge(this, (MemoryNode) otherNode, (MemoryRelationship) relationship);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean unlink(Node otherNode, Relationship relationship) throws GraphException {
		Validation.begin().isNotNull("Other Node", otherNode).isNotNull("Relationship", relationship).check().isInstanceOf("Other Node", otherNode, MemoryNode.class).isInstanceOf("Relationship", relationship, MemoryRelationship.class).isEqual("Other Node’s Graph", otherNode.getGraph(), getGraph()).check();
		return ((MemoryGraph) getGraph()).removeEdge(this, (MemoryNode) otherNode, (MemoryRelationship) relationship);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Edge> getIncomingLinks(Relationship relationship) throws GraphException {
		Validation.begin().isNotNull("Relationship", relationship).check().isInstanceOf("Relationship", relationship, MemoryRelationship.class).check();
		return Filters.filteredSet(((MemoryGraph) getGraph()).getEdges(this, (MemoryRelationship) relationship), new Filter<Edge>() {

			@Override
			public boolean filterObject(Edge edge) {
				return edge.getEndNode().equals(MemoryNode.this);
			}

		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Edge> getOutgoingLinks(Relationship relationship) throws GraphException {
		Validation.begin().isNotNull("Relationship", relationship).check().isInstanceOf("Relationship", relationship, MemoryRelationship.class).check();
		return Filters.filteredSet(((MemoryGraph) getGraph()).getEdges(this, (MemoryRelationship) relationship), new Filter<Edge>() {

			@Override
			public boolean filterObject(Edge edge) {
				return edge.getStartNode().equals(MemoryNode.this);
			}

		});
	}

	//
	// OBJECT METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return (int) ((id >>> 32) ^ (id & 0xffffffff));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MemoryNode)) {
			return false;
		}
		return ((MemoryNode) object).id == id;
	}

}
