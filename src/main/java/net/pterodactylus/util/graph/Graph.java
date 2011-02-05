/*
 * utils.graph - Graph.java - Copyright © 2011 David Roden
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.pterodactylus.util.validation.Validation;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class Graph {

	private final Set<Node> nodes = new HashSet<Node>();
	private final Map<Node, Set<Edge>> nodeEdges = new HashMap<Node, Set<Edge>>();

	//
	// ACCESSORS
	//

	public Set<Edge> getEdgesFrom(Node node, Relationship relationship) {
		Set<Edge> nodeEdges = this.nodeEdges.get(node);
		if (nodeEdges == null) {
			return Collections.emptySet();
		}
		for (Iterator<Edge> edgeIterator = nodeEdges.iterator(); edgeIterator.hasNext();) {
			if (!edgeIterator.next().getStartNode().equals(node)) {
				edgeIterator.remove();
			}
		}
		return nodeEdges;
	}

	public Set<Edge> getEdgesTo(Node node, Relationship relationship) {
		Set<Edge> nodeEdges = this.nodeEdges.get(node);
		if (nodeEdges == null) {
			return Collections.emptySet();
		}
		for (Iterator<Edge> edgeIterator = nodeEdges.iterator(); edgeIterator.hasNext();) {
			if (!edgeIterator.next().getEndNode().equals(node)) {
				edgeIterator.remove();
			}
		}
		return nodeEdges;
	}

	//
	// ACTIONS
	//

	/* TODO - NodeFactory? */
	public Node createNode() {
		Node node = new Node(this);
		nodes.add(node);
		return node;
	}

	public void removeNode(Node node) {
		Validation.begin().isNotNull("Node", node).check().isEqual("Node’s Graph", node.getGraph(), this).check();
		removeEdges(node);
		nodes.remove(node);
		nodeEdges.remove(node);
	}

	public Edge createEdge(Node startNode, Node endNode, Relationship relationship) {
		Edge edge = new Edge(this, startNode, endNode, relationship);
		Set<Edge> startNodeEdges = nodeEdges.get(startNode);
		if (startNodeEdges == null) {
			startNodeEdges = new HashSet<Edge>();
			nodeEdges.put(startNode, startNodeEdges);
		}
		startNodeEdges.add(edge);
		Set<Edge> endNodeEdges = nodeEdges.get(endNode);
		if (endNodeEdges == null) {
			endNodeEdges = new HashSet<Edge>();
			nodeEdges.put(endNode, endNodeEdges);
		}
		endNodeEdges.add(edge);
		return edge;
	}

	public void removeEdge(Node startNode, Node endNode, Relationship relationship) {
		Set<Edge> nodeEdges = this.nodeEdges.get(startNode);
		if (nodeEdges != null) {
			for (Iterator<Edge> edgeIterator = nodeEdges.iterator(); edgeIterator.hasNext();) {
				Edge edge = edgeIterator.next();
				if (edge.getStartNode().equals(startNode) && edge.getEndNode().equals(endNode) && edge.getRelationship().equals(relationship)) {
					edgeIterator.remove();
				}
			}
		}
		nodeEdges = this.nodeEdges.get(endNode);
		if (nodeEdges != null) {
			for (Iterator<Edge> edgeIterator = nodeEdges.iterator(); edgeIterator.hasNext();) {
				Edge edge = edgeIterator.next();
				if (edge.getStartNode().equals(startNode) && edge.getEndNode().equals(endNode) && edge.getRelationship().equals(relationship)) {
					edgeIterator.remove();
				}
			}
		}
	}

	public void removeEdges(Node node) {
		Set<Edge> nodeEdges = this.nodeEdges.remove(node);
		if (nodeEdges == null) {
			return;
		}
		for (Edge edge : nodeEdges) {
			if (edge.getStartNode().equals(node)) {
				removeEdge(node, edge.getEndNode(), edge.getRelationship());
			}
			if (edge.getEndNode().equals(node)) {
				removeEdge(edge.getStartNode(), node, edge.getRelationship());
			}
		}
	}

}
