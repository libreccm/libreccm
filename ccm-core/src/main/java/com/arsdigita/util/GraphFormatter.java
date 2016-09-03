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

/**
 * Implementations of this interface can be used for pretty-printing graphs.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date$
 * @since 2003-01-23
 **/
public interface GraphFormatter {

    /**
     * Returns graph attributes.
     * 
     * <p>For example, if you choose to pretty-print your graph in the DOT
     * language, then the graph attributes section may look like so:</p>
     *
     * <pre>
     *  digraph mygraph {
     *     // the following two lines are graph attributes
     *     node[shape=box,fontsize=8,fontname=verdana,height=0.2,width=0.2,style=filled];
     *     ranksep=0.05;
     *
     *     // the following lines are nodes and edges
     *     A -> B -> C -> D;
     *     B -> E -> F;
     *     C -> G;
     *     D -> I;
     *     D -> J -> H;
     *  }

     **/
    String graphAttributes(Graph graph);

    /**
     * Returns a textual representation of the node, preferably a short one that
     * can be used in the following plain-text representation of the tree.
     *
     * <pre>
     * digraph tree {
     *     A -> B -> C -> D;
     *     B -> E -> F;
     *     C -> G;
     *     D -> I;
     *     D -> J -> H;
     * }
     * </pre>
     * 
     * <p>Example implementation:</p>
     *
     * <pre>
     *  public String formatNode(Object node) {
     *      return node == null ? null : ((ObjectType) node).getName();
     *  }
     * </pre>
     **/
    String nodeName(Object node);

    /**
     * Returns [bracketed] node attributes.
     *
     * <pre>
     *  digraph g {
     *      C<strong>[label="The C Language"]</strong>;
     *      J<strong[label="The Java Language"]</strong>;
     *      C -> J;
     *  }
     * </pre>
     **/
    String nodeAttributes(Object node);

    /**
     * Returns a short textual label describing the edge.
     **/
    String edge(Object edge);
}
