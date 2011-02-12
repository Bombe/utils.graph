/*
 * utils.graph - SpeedTest.java - Copyright © 2011 David Roden
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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.pterodactylus.util.graph.disk.DiskStore;
import net.pterodactylus.util.storage.StorageException;

/**
 * TODO
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class SpeedTest {

	private static final int NODE_COUNT = 2000;
	private static final int RELATIONSHIP_COUNT = 500;
	private static final int EDGE_COUNT = NODE_COUNT * 10;
	private static final int LINK_COUNT = 1000000;

	public static void main(String... arguments) throws StorageException, IOException, GraphException {
		while (true) {
			test();
		}
	}

	public static void test() throws GraphException {
		for (File file : new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".dat") || name.endsWith(".idx");
			}
		})) {
			file.delete();
		}
		DiskStore diskStore = new DiskStore(".");
		Graph graph = diskStore.getGraph();
		List<Node> nodes = new ArrayList<Node>();
		List<Relationship> relationships = new ArrayList<Relationship>();
		long timestamp;

		System.out.println("creating " + NODE_COUNT + " nodes... ");
		timestamp = System.nanoTime();
		for (int i = 0; i < NODE_COUNT; ++i) {
			Node node = graph.createNode();
			nodes.add(node);
			node.set("index", i);
		}
		double milliseconds = (System.nanoTime() - timestamp) / 1000000.0;
		System.out.println("time: " + milliseconds + " ms, " + (milliseconds / NODE_COUNT) + " ms/node");

		System.out.println("creating " + RELATIONSHIP_COUNT + " relationships...");
		timestamp = System.nanoTime();
		for (int i = 0; i < RELATIONSHIP_COUNT; ++i) {
			relationships.add(graph.getRelationship(String.valueOf(i)));
		}
		milliseconds = (System.nanoTime() - timestamp) / 1000000.0;
		System.out.println("time: " + milliseconds + " ms, " + (milliseconds / RELATIONSHIP_COUNT) + " ms/relationship");

		System.out.println("creating " + EDGE_COUNT + " edges...");
		timestamp = System.nanoTime();
		for (int i = 0; i < EDGE_COUNT; ++i) {
			Node startNode = nodes.get((int) (Math.random() * nodes.size()));
			Node endNode = nodes.get((int) (Math.random() * nodes.size()));
			Relationship relationship = relationships.get((int) (Math.random() * relationships.size()));
			startNode.link(endNode, relationship);
		}
		milliseconds = (System.nanoTime() - timestamp) / 1000000.0;
		System.out.println("time: " + milliseconds + " ms, " + (milliseconds / EDGE_COUNT) + " ms/edge");

		System.out.println("getting " + LINK_COUNT + " links...");
		timestamp = System.nanoTime();
		long edgeCount = 0;
		for (int i = 0; i < LINK_COUNT; ++i) {
			Node targetNode = nodes.get(/* (int) (Math.random() */(i % nodes.size()));
			Relationship relationship = relationships.get(/* (int) (Math.random() */i % relationships.size());
			Set<? extends Edge> edges = targetNode.getOutgoingLinks(relationship);
			edgeCount += edges.size();
		}
		milliseconds = (System.nanoTime() - timestamp) / 1000000.0;
		System.out.println("time: " + milliseconds + " ms, " + (milliseconds / LINK_COUNT) + " ms/link");
		System.out.println("edges: " + edgeCount);

		System.out.println("removing " + NODE_COUNT + " nodes...");
		for (Node node : nodes) {
			graph.removeNode(node);
		}
		milliseconds = (System.nanoTime() - timestamp) / 1000000.0;
		System.out.println("time: " + milliseconds + " ms, " + (milliseconds / NODE_COUNT) + " ms/node");

	}

}
