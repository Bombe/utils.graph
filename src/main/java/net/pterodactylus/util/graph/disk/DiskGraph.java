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

package net.pterodactylus.util.graph.disk;

import java.util.Set;

import net.pterodactylus.util.graph.Edge;
import net.pterodactylus.util.graph.Graph;
import net.pterodactylus.util.graph.Relationship;
import net.pterodactylus.util.validation.Validation;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class DiskGraph implements Graph<DiskGraph, DiskNode, DiskEdge, DiskRelationship> {

	private final DiskStore store;
	private DiskNode rootNode;

	DiskGraph(DiskStore store) {
		this.store = store;
	}

	public DiskNode getRootNode() {
		return rootNode;
	}

	void setRootNode(DiskNode rootNode) {
		this.rootNode = rootNode;
	}

	//
	// ACCESSORS
	//

	public Set<DiskEdge> getEdgesFrom(DiskNode node, DiskRelationship relationship) {
		return store.getEdges(node, null, relationship);
	}

	public Set<DiskEdge> getEdgesTo(DiskNode node, DiskRelationship relationship) {
		return store.getEdges(null, node, relationship);
	}

	//
	// ACTIONS
	//

	public Relationship getRelationship(String name) {
		return store.getRelationship(name);
	}

	/* TODO - NodeFactory? */
	public DiskNode createNode() {
		DiskNode node = store.createNode();
		return node;
	}

	public void storeNode(DiskNode node) {
		store.storeNode(node);
	}

	public void removeNode(DiskNode node) {
		Validation.begin().isNotNull("Node", node).check().isEqual("Node’s Graph", node.getGraph(), this).check();
		store.removeNode(node);
	}

	public Edge createEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		return store.createEdge(this, startNode, endNode, relationship);
	}

	public void removeEdge(DiskNode startNode, DiskNode endNode, DiskRelationship relationship) {
		store.removeEdge(startNode, endNode, relationship);
	}

}
