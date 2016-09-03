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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arsdigita.util.Assert;

/**
 * A Set-based implementation of the {@link com.arsdigita.util.Graph} interface.
 * Once you've added a node to this graph, you must not mutate the node in a way
 * that affects its <code>equals(Object)</code> and <code>hashCode()</code>
 * methods.
 *
 * <p>This class permits the <code>null</code> node.</p>
 *
 * <p><strong>This implementation is not synchronized.</strong>.</p>
 *
 * @author Archit Shah (ashah@mit.edut)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-01-22
 * @version $Date$
 **/
public class GraphSet implements Graph {
    private final static String LINE_SEP = System.getProperty("line.separator");

    private Set m_nodes = new HashSet();
    private Map m_outgoingEdges = new HashMap();
    private Map m_incomingEdges = new HashMap();
    private String m_label = "directed_graph";

    public Graph copy() {
        Graph newGraph = new GraphSet();
        for (Iterator nodes = getNodes().iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            newGraph.addNode(node);
            for (Iterator edges=getOutgoingEdges(node).iterator(); edges.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) edges.next();
                newGraph.addEdge(edge.getTail(), edge.getHead(), edge.getLabel());
            }
        }
        newGraph.setLabel(getLabel());
        return newGraph;
    }

    public void setLabel(String label) {
        Assert.isTrue(label !=null, "label is not null");
        m_label = label;
    }

    public String getLabel() {
        return m_label;
    }

    public void addNode(Object name) {
        m_nodes.add(name);
    }

    public boolean hasNode(Object nodeName) {
        return m_nodes.contains(nodeName);
    }

    /**
     * @pre hasNode(edge.getTail()) && hasNode(edge.getHead())
     **/
    public boolean hasEdge(Graph.Edge edge) {
        return outgoingEdges(edge.getTail()).contains(edge);
    }

    public int nodeCount() {
        return m_nodes.size();
    }

    public void addEdge(Graph.Edge edge) {
        m_nodes.add(edge.getTail());
        m_nodes.add(edge.getHead());
        outgoingEdges(edge.getTail()).add(edge);
        incomingEdges(edge.getHead()).add(edge);
    }

    public void addEdge(Object tail, Object head, Object label) {
        addEdge(new GraphEdge(tail, head, label));
    }

    public List getNodes() {
        return new ArrayList(m_nodes);
    }

    public boolean removeNode(Object nodeName) {
        boolean hasNode = m_nodes.remove(nodeName);
        if (hasNode) {
            Set out = (Set) m_outgoingEdges.remove(nodeName);
            if (out != null) {
                for (Iterator it = out.iterator(); it.hasNext(); ) {
                    Graph.Edge e = (Graph.Edge) it.next();
                    incomingEdges(e.getHead()).remove(e);
                }
            }
            Set in = (Set) m_incomingEdges.remove(nodeName);
            if (in != null) {
                for (Iterator it = in.iterator(); it.hasNext(); ) {
                    Graph.Edge e = (Graph.Edge) it.next();
                    outgoingEdges(e.getTail()).remove(e);
                }
            }
        }
        return hasNode;
    }

    public boolean removeEdge(Object tail, Object head, Object label) {
        return removeEdge(new GraphEdge(tail, head, label));
    }

    public boolean removeEdge(Graph.Edge edge) {
        boolean hasEdge = outgoingEdges(edge.getTail()).remove(edge);
        if (hasEdge) { incomingEdges(edge.getHead()).remove(edge); }
        return hasEdge;
    }

    public void removeAll() {
        m_nodes.clear();
        m_outgoingEdges.clear();
        m_incomingEdges.clear();
    }

    private Set outgoingEdges(Object nodeName) {
        Set edges = (Set) m_outgoingEdges.get(nodeName);
        if (edges == null) {
            edges = new HashSet(4);
            m_outgoingEdges.put(nodeName, edges);
        }

        return edges;
    }

    private static String objToString(Object obj) {
        return obj == null ? "null" : obj.toString();
    }

    public List getOutgoingEdges(Object node) {
        Assert.isTrue(hasNode(node), objToString(node));
        return new ArrayList(outgoingEdges(node));
    }

    public int outgoingEdgeCount(Object node) {
        Assert.isTrue(hasNode(node), objToString(node));
        return outgoingEdges(node).size();
    }

    public int incomingEdgeCount(Object node) {
        Assert.isTrue(hasNode(node), objToString(node));
        return incomingEdges(node).size();
    }

    private Set incomingEdges(Object nodeName) {
        Set edges = (Set) m_incomingEdges.get(nodeName);
        if ( edges == null ) {
            edges = new HashSet();
            m_incomingEdges.put(nodeName, edges);
        }
        return edges;
    }

    public List getIncomingEdges(Object node) {
        Assert.isTrue(hasNode(node), objToString(node));
        return new ArrayList(incomingEdges(node));
    }

    /**
     * Returns a printable representation of the graph that has the following
     * form.
     *
     * <pre>
     * digraph foo {
     *     Boston -> New_York [label="214 miles"];
     *     Boston -> Chicago [label="983 miles"];
     *     New_York -> Chicago [label="787 miles"];
     *     Boston -> Westford [label="35 miles"];
     *     Raleigh -> Westford [label="722 miles"];
     * }
     * </pre>
     *
     * <p>Note that to get a neat printable representation, each node and edge
     * label must have a short printable representation.</p>
     **/
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("graph ").append(getLabel()).append(" {");
        sb.append(LINE_SEP);
        List sortedNodes = new ArrayList(m_nodes);
        Collections.sort(sortedNodes);
        for (Iterator nodes=sortedNodes.iterator(); nodes.hasNext(); ) {
            Object node = nodes.next();
            for (Iterator edges = getOutgoingEdges(node).iterator(); edges.hasNext(); ) {
                Graph.Edge edge = (Graph.Edge) edges.next();
                sb.append("    ");
                sb.append(objToString(node)).append(" -> ");
                sb.append(objToString(edge.getHead()));
                sb.append("[label=\"").append(edge.getLabel());
                sb.append("\"];");
                sb.append(LINE_SEP);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
