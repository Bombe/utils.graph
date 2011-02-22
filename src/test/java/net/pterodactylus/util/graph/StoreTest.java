/*
 * utils.graph - StoreTest.java - Copyright © 2011 David Roden
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
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * Common test base for tests all {@link Store} implementations have to endure.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class StoreTest extends TestCase {

	/**
	 * Tests whether a single {@link Store} instance always returns the same
	 * {@link Graph} from its {@link Store#getGraph()} method.
	 */
	public void testSingleGraph() {
		for (Store store : getStores()) {
			assertNotNull("Store", store);
			Graph firstGraph = store.getGraph();
			assertNotNull("First Graph", firstGraph);
			Graph secondGraph = store.getGraph();
			assertNotNull("Second Graph", secondGraph);
			assertEquals("Graphs", firstGraph, secondGraph);
		}
	}

	/**
	 * Tests whether the first two root nodes returned by
	 * {@link Graph#getRootNode()} of a single instance are identical.
	 */
	public void testSingleRootNode() {
		for (Store store : getStores()) {
			assertNotNull("Store", store);
			Graph graph = store.getGraph();
			assertNotNull("Graph", graph);
			Node firstRootNode = graph.getRootNode();
			assertNotNull("First Root Node", firstRootNode);
			Node secondRootNode = graph.getRootNode();
			assertNotNull("Second Root Node", secondRootNode);
			assertEquals("Root Nodes", firstRootNode, secondRootNode);
			assertEquals("Root Node’s Graph", graph, firstRootNode.getGraph());
		}
	}

	/**
	 * Tests whether the store’s graph will create different relationships for
	 * different names.
	 *
	 * @throws GraphException
	 *             if a graph error occurs
	 */
	public void testDifferentRelationships() throws GraphException {
		for (Store store : getStores()) {
			Graph graph = store.getGraph();
			Relationship firstRelationship = graph.getRelationship("first");
			assertNotNull("First Relationship", firstRelationship);
			Relationship secondRelationship = graph.getRelationship("second");
			assertNotNull("Second Relationship", secondRelationship);
			assertEquals("Different Relationships", false, firstRelationship.equals(secondRelationship));
		}
	}

	/**
	 * Tests whether the store’s graph will identical different relationships
	 * for identical names.
	 *
	 * @throws GraphException
	 *             if a graph error occurs
	 */
	public void testIdenticalRelationships() throws GraphException {
		for (Store store : getStores()) {
			Graph graph = store.getGraph();
			Relationship firstRelationship = graph.getRelationship("first");
			assertNotNull("First Relationship", firstRelationship);
			Relationship secondRelationship = graph.getRelationship("first");
			assertNotNull("Second Relationship", secondRelationship);
			assertEquals("Identical Relationships", true, firstRelationship.equals(secondRelationship));
		}
	}

	/**
	 * Creates a new node, links the root node to it, and removes the link
	 * again, checking for edge counts along the way.
	 *
	 * @throws GraphException
	 *             if a graph error occurs
	 */
	public void testCreatingLinkingUnlinkingSingleNode() throws GraphException {
		for (Store store : getStores()) {
			Graph graph = store.getGraph();
			Node rootNode = graph.getRootNode();
			Node otherNode = graph.createNode();
			Relationship relationship = graph.getRelationship("tests");
			rootNode.link(otherNode, relationship);

			Set<Edge> outgoingEdges = rootNode.getOutgoingLinks(relationship);
			assertEquals("Root Node’s Outgoing Edges Count", 1, outgoingEdges.size());
			outgoingEdges = rootNode.getOutgoingLinks("tests");
			assertEquals("Root Node’s Outgoing Edges Count", 1, outgoingEdges.size());

			Set<Edge> incomingEdges = rootNode.getIncomingLinks(relationship);
			assertEquals("Root Node’s Incoming Edges Count", 0, incomingEdges.size());
			incomingEdges = rootNode.getIncomingLinks("tests");
			assertEquals("Root Node’s Incoming Edges Count", 0, incomingEdges.size());

			outgoingEdges = otherNode.getOutgoingLinks(relationship);
			assertEquals("Other Node’s Outgoing Edges Count", 0, outgoingEdges.size());
			outgoingEdges = otherNode.getOutgoingLinks("tests");
			assertEquals("Other Node’s Outgoing Edges Count", 0, outgoingEdges.size());

			incomingEdges = otherNode.getIncomingLinks(relationship);
			assertEquals("Other Node’s Incoming Edges Count", 1, incomingEdges.size());
			incomingEdges = otherNode.getIncomingLinks("tests");
			assertEquals("Other Node’s Incoming Edges Count", 1, incomingEdges.size());

			rootNode.unlink(otherNode, relationship);

			outgoingEdges = rootNode.getOutgoingLinks(relationship);
			assertEquals("Root Node’s Outgoing Edges Count", 0, outgoingEdges.size());
			outgoingEdges = rootNode.getOutgoingLinks("tests");
			assertEquals("Root Node’s Outgoing Edges Count", 0, outgoingEdges.size());

			incomingEdges = rootNode.getIncomingLinks(relationship);
			assertEquals("Root Node’s Incoming Edges Count", 0, incomingEdges.size());
			incomingEdges = rootNode.getIncomingLinks("tests");
			assertEquals("Root Node’s Incoming Edges Count", 0, incomingEdges.size());

			outgoingEdges = otherNode.getOutgoingLinks(relationship);
			assertEquals("Other Node’s Outgoing Edges Count", 0, outgoingEdges.size());
			outgoingEdges = otherNode.getOutgoingLinks("tests");
			assertEquals("Other Node’s Outgoing Edges Count", 0, outgoingEdges.size());

			incomingEdges = otherNode.getIncomingLinks(relationship);
			assertEquals("Other Node’s Incoming Edges Count", 0, incomingEdges.size());
			incomingEdges = otherNode.getIncomingLinks("tests");
			assertEquals("Other Node’s Incoming Edges Count", 0, incomingEdges.size());
		}
	}

	//
	// PROTECTED
	//

	/**
	 * Returns a list of {@link Store} instances that should be tested. This
	 * method should be overridden by subclasses of this test to actually test
	 * specific implementations.
	 *
	 * @return The {@link Store} instances to test
	 */
	protected List<Store> getStores() {
		return Collections.emptyList();
	}

}
