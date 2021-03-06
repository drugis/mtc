/*
 * This file is part of the GeMTC software for MTC model generation and
 * analysis. GeMTC is distributed from http://drugis.org/gemtc.
 * Copyright (C) 2009-2012 Gert van Valkenhoef.
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

package org.drugis.mtc.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphUtilTest {
	@Test
	public void testCopyGraph() {
		Graph<String, Integer> graph = new SparseMultigraph<String, Integer>();
		graph.addEdge(1, "A", "B", EdgeType.DIRECTED);
		graph.addEdge(2, "A", "B", EdgeType.DIRECTED);
		graph.addEdge(4, "D", "E", EdgeType.UNDIRECTED);
		graph.addEdge(5, "E", "D", EdgeType.DIRECTED);
		graph.addVertex("C");
		
		Graph<String, Integer> copy = new SparseMultigraph<String, Integer>();
		GraphUtil.copyGraph(graph, copy);
		
		assertEquals(graph.getVertexCount(), copy.getVertexCount());
		assertEquals(graph.getEdgeCount(), copy.getEdgeCount());
		assertTrue(copy.containsVertex("A"));
		assertTrue(copy.containsVertex("B"));
		assertTrue(copy.containsVertex("C"));
		assertTrue(copy.containsVertex("D"));
		assertTrue(copy.containsVertex("E"));
		assertTrue(copy.containsEdge(1));
		assertTrue(copy.containsEdge(2));
		assertTrue(copy.containsEdge(4));
		assertTrue(copy.containsEdge(5));
		assertEquals(EdgeType.DIRECTED, copy.getEdgeType(1));
		assertEquals(EdgeType.DIRECTED, copy.getEdgeType(2));
		assertEquals(EdgeType.UNDIRECTED, copy.getEdgeType(4));
		assertEquals(EdgeType.DIRECTED, copy.getEdgeType(5));
		assertEquals(new Pair<String>("A", "B"), copy.getEndpoints(1));
		assertEquals(new Pair<String>("A", "B"), copy.getEndpoints(2));
		assertEquals(new Pair<String>("D", "E"), copy.getEndpoints(4));
		assertEquals(new Pair<String>("E", "D"), copy.getEndpoints(5));
	}
	
	@Test
	public void testCopyTree() {
		DelegateTree<String, Integer> tree = new DelegateTree<String, Integer>();
		tree.setRoot("A");
		tree.addChild(1, "A", "D");
		tree.addChild(2, "D", "B");
		tree.addChild(3, "D", "C");
		tree.addChild(4, "D", "E");
		tree.addChild(5, "A", "F");
		tree.addChild(6, "C", "G");
		
		Tree<String, Integer> copy = new DelegateTree<String, Integer>();
		GraphUtil.copyTree(tree, copy);
		assertEquals(tree.getVertexCount(), copy.getVertexCount());
		assertEquals(tree.getEdgeCount(), copy.getEdgeCount());
	}
	
	@Test
	public void testIsWeaklyConnected() {
		Graph<String, Integer> graph = new SparseMultigraph<String, Integer>();
		graph.addEdge(1, "A", "B", EdgeType.DIRECTED);
		graph.addVertex("C");
		
		assertFalse(GraphUtil.isWeaklyConnected(graph));
		
		graph.addEdge(2, "A", "C");
		assertTrue(GraphUtil.isWeaklyConnected(graph));
		
		graph.addEdge(3, "D", "E");
		assertFalse(GraphUtil.isWeaklyConnected(graph));
	}
	
	@Test
	public void testAreVerticesWeaklyConnected() {
		Graph<String, Integer> graph = new SparseMultigraph<String, Integer>();
		graph.addEdge(1, "A", "B", EdgeType.DIRECTED);
		graph.addVertex("C");
		
		assertFalse(GraphUtil.areVerticesWeaklyConnected(graph, "A", "C"));
		assertTrue(GraphUtil.areVerticesWeaklyConnected(graph, "A", "B"));
		assertTrue(GraphUtil.areVerticesWeaklyConnected(graph, "B", "A"));
		
		graph.addEdge(2, "B", "C");
		assertTrue(GraphUtil.areVerticesWeaklyConnected(graph, "A", "C"));
		
		graph.addEdge(3, "D", "E");
		assertTrue(GraphUtil.areVerticesWeaklyConnected(graph, "A", "C"));
		
		// Test that we don't keep going around in cycles
		graph.addEdge(4, "C", "A");
		assertTrue(GraphUtil.areVerticesWeaklyConnected(graph, "A", "C"));
		assertFalse(GraphUtil.areVerticesWeaklyConnected(graph, "A", "D"));
	}
	
	@Test
	public void testIsSimpleCycle() {
		UndirectedGraph<String, Integer> graph = new UndirectedSparseGraph<String, Integer>();
		
		graph.addVertex("C");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.removeVertex("C");
		graph.addEdge(1, "A", "B");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.addEdge(2, "A", "C");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.addEdge(3, "C", "B");
		assertTrue(GraphUtil.isSimpleCycle(graph));
		
		graph.addVertex("D");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.addEdge(4, "B", "D");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.addEdge(5, "D", "C");
		assertFalse(GraphUtil.isSimpleCycle(graph));
		
		graph.removeEdge(3);
		assertTrue(GraphUtil.isSimpleCycle(graph));
	}
	
	@Test
	public void testFindCommonAncestor() {
		Tree<String, Integer> tree = new DelegateTree<String, Integer>();
		tree.addVertex("A");
		tree.addEdge(1, "A", "B");
		tree.addEdge(2, "B", "C");
		tree.addEdge(3, "B", "D");
		tree.addEdge(4, "A", "E");
		
		assertEquals("D", GraphUtil.findCommonAncestor(tree, "D", "D"));
		assertEquals("B", GraphUtil.findCommonAncestor(tree, "C", "D"));
		assertEquals("B", GraphUtil.findCommonAncestor(tree, "B", "D"));
		assertEquals("A", GraphUtil.findCommonAncestor(tree, "E", "D"));
	}
	
	@Test
	public void findPath() {
		Tree<String, Integer> tree = new DelegateTree<String, Integer>();
		tree.addVertex("A");
		tree.addEdge(1, "A", "B");
		tree.addEdge(2, "B", "C");
		tree.addEdge(3, "B", "D");
		tree.addEdge(4, "A", "E");
		
		assertEquals(Arrays.asList("D"), GraphUtil.findPath(tree, "D", "D"));
		assertEquals(Arrays.asList("C", "B", "D"), GraphUtil.findPath(tree, "C", "D"));
		assertEquals(Arrays.asList("B", "D"), GraphUtil.findPath(tree, "B", "D"));
		assertEquals(Arrays.asList("E", "A", "B", "D"), GraphUtil.findPath(tree, "E", "D"));		
	}
}
