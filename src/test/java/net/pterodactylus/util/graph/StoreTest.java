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
