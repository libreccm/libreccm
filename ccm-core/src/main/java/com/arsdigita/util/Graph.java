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

import java.util.List;

/**
 * The graph class allows you to build <a
 * href="http://mathworld.wolfram.com/Graph.html">graphs</a> of objects.
 *
 * @author Archit Shah (ashah@mit.edu)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date$
 * @since   2003-01-22
 **/
public interface Graph {
    /**
     * Creates a copy of this graph.
     **/
    Graph copy();

    /**
     * Sets the graph's label.
     **/
    void setLabel(String label);

    /**
     * Returns the graph's label.
     **/
    String getLabel();

    /**
     * Adds a node to the graph.
     **/
    void addNode(Object node);

    /**
     * Returns <code>true</code> if the graph has this node.
     **/
    boolean hasNode(Object node);


    /**
     * Returns <code>true</code> if the graph has this edge.
     *
     * @pre hasNode(edge.getTail()) && hasNode(edge.getHead())
     **/
    boolean hasEdge(Graph.Edge edge);

    /**
     * Returns the count of nodes in this graph.
     **/
    int nodeCount();

    /**
     * Adds an edge to the graph.
     **/
    void addEdge(Graph.Edge edge);

    /**
     * A convenient shortcut for <code>addEdge(new Graph.Edge(tail, head,
     * label))</code>.
     *
     * @see #addEdge(Graph.Edge)
     **/
    void addEdge(Object tail, Object head, Object label);

    /**
     * Returns a list of nodes that this graph has. (Todo: this should probably
     * return a Set.)
     **/
    List getNodes();

    /**
     * Removes specified node and all edges incident to it.
     *
     * @returns true if this Graph contains the specified node
     **/
    boolean removeNode(Object node);

    /**
     * A convenient shortcut for <code>removeEdge(new Graph.Edge(tail, head,
     * label))</code>.
     *
     * @see #removeEdge(Graph.Edge)
     **/
    boolean removeEdge(Object tail, Object head, Object label);

    /**
     * Removes specified edge.
     *
     * @returns true if this Graph has the specified edge
     **/
    boolean removeEdge(Graph.Edge edge);

    /**
     * Removes all nodes and edges.
     **/
    void removeAll();

    /**
     * Returns a list of outgoing edges leaving this node.
     **/
    List getOutgoingEdges(Object node);

    /**
     * Returns the number of outgoing edges this node has. A convenient shortcut
     * for <code>getOutgoingEdges(node).size()</code>.
     *
     * @see #getOutgoingEdges(Object)
     **/
    int outgoingEdgeCount(Object node);

    /**
     * @see #outgoingEdgeCount(Object)
     **/
    int incomingEdgeCount(Object node);

    /**
     * @see #getOutgoingEdges(Object)
     **/
    List getIncomingEdges(Object node);

    /**
     * An edge is an ordered pair of nodes with a label attached to it.  The
     * first node of the pair is called the <em>tail</em> and the second the
     * <em>head</em>.
     *
     * <p>Implementing classes are expected to supply a constructor of the form
     * <code>Graph.Edge(Object tail, Object head, Object label)</code>. </p>
     **/
    interface Edge {

        /**
         * Returns the tail node of the edge.
         *
         * @see #getHead()
         **/
        Object getTail();

        /**
         * Returns the head node of the edge.
         *
         * @see #getTail()
         **/
        Object getHead();

        /**
         * Returns the label associated with this edge. The label can be
         * anything, depending on your particular graph. For example, if your
         * nodes represent cities and edges represent freeways, then the label
         * can be an <code>Float</code> representing the the distance (the
         * length of the route).
         **/
        Object getLabel();

    }
}
