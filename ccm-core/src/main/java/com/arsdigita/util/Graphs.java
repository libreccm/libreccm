/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A collection of methods that operate on {@link com.arsdigita.util.Graph
 * graphs}.
 *
 * @author Archit Shah (ashah@mit.edu)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date$
 * @since 2003-01-22
 **/
public class Graphs {

    /**
     * A simple implementation of the {@link Graphs.EdgeSelector} interface that
     * selects outgoing edges.
     **/
    public final static EdgeSelector FORWARD_SELECTOR = new EdgeSelector() {
            public boolean test(Graph.Edge edge, boolean forward) {
                return forward;
            }
        };

    private static final String INDENT = "    ";

    private Graphs() {}

    /**
     * Finds a path in <code>graph</code> from begin to end
     * 
     * @see #findPath(Graph, Object, Graphs.EdgeSelector, Graphs.NodeSelector)
     * @returns list of edges representing the found path
     * @throws NullPointerException if any of the three arguments is <code>null</code>.
     **/
    public static final List findPath(Graph graph, Object begin, Object end) {
        NodeSelector terminator = new EqualityTerminator(end);
        return findPath(graph, begin, FORWARD_SELECTOR, terminator);
    }

    /**
     * Performs a traversal of <code>graph</code> looking for path that starts
     * with <code>startNode</code> and terminates with a node that satisfies the
     * test specified by <code>terminator</code> and consists only of those
     * edges that satisfy the test specified by <code>selector</code>.
     *
     * @return a path from start node to a node that satisfies the condition
     * specified by <code>terminator</code>, such that all edges in the path
     * satisfy the condition specified by <code>selector</code>. An empty list
     * is returned, if no such path can be found.
     **/
    public static List findPath(Graph graph, Object startNode,
                                EdgeSelector selector,
                                NodeSelector terminator) {

        Stack path = new Stack();
        findPathRecur
            (graph, startNode, selector, terminator, new HashSet(), path);
        return path;
    }


    private static final boolean findPathRecur
        (Graph graph, Object start,
         EdgeSelector selector, NodeSelector terminator,
         Set searched, Stack path) {

        Iterator it = graph.getOutgoingEdges(start).iterator();
        searched.add(start);

        while (it.hasNext()) {
            Graph.Edge edge = (Graph.Edge) it.next();
            if ( !selector.test(edge, true) ) { continue; }

            path.push(edge);
            final Object node = edge.getHead();

            if (terminator.test(node)) {
                return true;
            }

            if (!searched.contains(node)) {
                boolean found = findPathRecur
                    (graph, node, selector, terminator, searched, path);

                if ( found ) { return true; }
            }
            path.pop();
        }
        return false;
    }

    /**
     * @param edgePath a list of edges such as the one returned by
     * {@link #findPath(Graph, Object, Object)}.
     * @return the same path represented as a list of nodes rather than edges.
     *
     * @throws IllegalStateException if <code>edgePath</code> is not a valid
     * path. For example, <code>(a->b, c->d, d->e)</code> is invalid, because
     * the edge between <code>b</code> and <code>c</code> is missing.
     * @throws NullPointerException if <code>edgePath</code> is <code>null</code>
     **/
    public static final List edgePathToNodePath(List edgePath) {
        List path = new ArrayList();
        Graph.Edge lastEdge = null;
        for (Iterator edges = edgePath.iterator(); edges.hasNext(); ) {
            Graph.Edge edge = (Graph.Edge) edges.next();
            if ( lastEdge != null ) {
                if ( !lastEdge.getHead().equals(edge.getTail()) ) {
                    throw new IllegalArgumentException
                        ("non-contiguous path segment:\n" + lastEdge + "\n" +
                         edge);
                }
            }
            path.add(edge.getTail());
            lastEdge = edge;
        }
        if ( lastEdge != null ) {
            path.add(lastEdge.getHead());
        }
        return path;
    }

    private static String objToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    /**
     * @return nodes reachable from <code>start</code>, including the
     * <code>start</code> node itself.
     *
     * @pre graph.hasNode(start)
     **/
    public static Graph nodesReachableFrom(Graph graph, Object start) {
        Assert.isTrue(graph.hasNode(start));
        Graph result = new GraphSet();
        result.addNode(start);
        Set processedTails = new HashSet();
        nodesReachableRecurse(graph, start, processedTails, result);
        return result;
    }

    private static void nodesReachableRecurse(Graph gg, Object currentNode,
                                              Set processedTails,
                                              Graph accumulator) {

        processedTails.add(currentNode);

        for (Iterator edges=gg.getOutgoingEdges(currentNode).iterator(); edges.hasNext(); ) {
            Graph.Edge edge = (Graph.Edge) edges.next();
            if ( processedTails.contains(edge.getHead()) ) {
                continue;
            }
            accumulator.addEdge(edge);
            nodesReachableRecurse
                (gg, edge.getHead(), processedTails, accumulator);
        }
    }

    /**
     * Returns a list of nodes in <code>gg</code> that have no outgoing edges.
     **/
    public static List getSinkNodes(Graph gg) {
        List result = new ArrayList();
        for (Iterator nodes = gg.getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            if ( gg.getOutgoingEdges(node).size() == 0 ) {
                result.add(node);
            }
        }
        return result;
    }

    /**
     * Pretty-prints the tree in a format patterned off of the <a
     * href="http://www.research.att.com/sw/tools/graphviz/refs.html">DOT
     * language</a>.
     *
     * @pre tree != null
     * @pre fmtr != null
     * @pre writer != null
     **/
    public static void printTree(Tree tree, GraphFormatter fmtr,
                                 PrintWriter writer) {

        Assert.exists(tree, "tree");
        Assert.exists(fmtr, "formatter");
        Assert.exists(writer, "writer");

        writer.println("digraph " + tree.getLabel() + " {");
        printTreeRecurse(tree, fmtr, writer);
        writer.println("}");
    }

    private static void printTreeRecurse(Tree tree, GraphFormatter fmtr,
                                         PrintWriter writer) {

        String root = fmtr.nodeName(tree.getRoot());
        for (Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            Tree.EdgeTreePair pair = (Tree.EdgeTreePair) ii.next();
            String edge = fmtr.edge(pair.getEdge());
            String child = fmtr.nodeName(pair.getTree().getRoot());
            writer.print(INDENT + root + " -> " + child);
            if ( edge != null ) {
                writer.print("[label=\"" + edge + "\"]");
            }
            writer.println(";");
            printTreeRecurse(pair.getTree(), fmtr, writer);
        }
    }

    /**
     * Pretty-prints the graph.
     *
     * @see #printTree(Tree, GraphFormatter,  PrintWriter)
     * @pre graph != null
     * @pre fmtr != null
     * @pre writer != null
     **/
    public static void printGraph(Graph graph, GraphFormatter fmtr,
                                  PrintWriter writer) {

        Assert.exists(graph, "tree");
        Assert.exists(fmtr, "formatter");
        Assert.exists(writer, "writer");

        writer.println("digraph " + graph.getLabel() + " {");
        String graphAttrs = fmtr.graphAttributes(graph);
        if ( graphAttrs != null ) {
            writer.println(graphAttrs);
        }
        for (Iterator nodes=graph.getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            int nodeCount = graph.outgoingEdgeCount(node) +
                graph.incomingEdgeCount(node);

            String nodeName = fmtr.nodeName(node);
            String nodeAttrs = fmtr.nodeAttributes(node);

            if ( nodeCount==0 || nodeAttrs != null ) {
                writer.print(INDENT + nodeName);

                if ( nodeAttrs == null ) {
                    writer.println(";");
                } else {
                    writer.println(nodeAttrs + ";");
                }
            }

            if (graph.outgoingEdgeCount(node) == 0) {
                // we'll print this node when we print the outgoing edges of
                // some other node
                continue;
            }
            Iterator edges = graph.getOutgoingEdges(node).iterator();
            while (edges.hasNext()) {
                Graph.Edge edge = (Graph.Edge) edges.next();
                StringBuffer sb = new StringBuffer();
                sb.append(INDENT).append(fmtr.nodeName(edge.getTail()));
                sb.append(" -> ").append(fmtr.nodeName(edge.getHead()));
                if ( edge.getLabel() != null ) {
                    sb.append("[label=\"");
                    sb.append(fmtr.edge(edge.getLabel()));
                    sb.append("\"]");
                }
                sb.append(";");
                writer.println(sb.toString());
            }
        }
        writer.println("}");
    }

    /**
     * @see #findPath(Graph, Object, Graphs.EdgeSelector, Graphs.NodeSelector)
     **/
    public interface EdgeSelector {
        /**
         * @param edge the edge to test
         * @param forward the value of <code>false</code> indicates that an edge
         * is to be traversed backwards.  Useful, when you want to find a path
         * starting with the destination and working your way back to the
         * source. The value <code>true</code> indicates that edge is being
         * considered for traversal in the natural, forward direction.
         **/
        boolean test(Graph.Edge edge, boolean forward);
    }

    /**
     * @see #findPath(Graph, Object, Graphs.EdgeSelector, Graphs.NodeSelector)
     **/
    public interface NodeSelector {
        boolean test(Object node);
    }

    public final static class EqualityTerminator implements NodeSelector {
        private Object m_node;

        /**
         * @throws NullPointerException if <code>node</code> is null
         **/
        public EqualityTerminator(Object node) {
            if (node==null) { throw new NullPointerException("node"); }

            m_node = node;
        }

        public boolean test(Object node) {
            return m_node.equals(node);
        }
    }

}
