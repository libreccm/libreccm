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
import java.util.Iterator;
import java.util.List;

/**
 * This class a represents the <a
 * href="http://mathworld.wolfram.com/Tree.html">tree</a> abstraction. This
 * implementation takes a recursive definition where a tree is a root node
 * connected to other (sub)trees.
 *
 * <p>This implementation allows the same node to be used in more than position
 * in the tree. For example, you can do something like this: </p>
 *
 * <pre>
 *  Tree aa = new Tree("a");
 *  Tree bb = aa.addChild("b");
 *  bb.addChild("a");
 *  aa.addChild("c");
 * </pre>
 *
 * <p>This can be visualized as follows:</p>
 *
 * <pre>
 *    a
 *   / \
 *  /   \
 * b     c
 *  \
 *   \
 *    a
 * </pre>
 *
 * <p>The only ways to add node to the tree is through the {@link #Tree(Object)
 * constructor} and the {@link #addChild(Object)} {@link #addChild(Object,
 * Object)} methods. </p>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Date$
 * @since 2003-01-23
 **/
public class Tree {
    private Tree m_parent;
    private final Object m_root;
    private final List m_children;
    private String m_label;

    /**
     * @param root the root node of the tree
     * @pre root != null
     **/
    public Tree(Object root) {
        this(null, root);
    }

    private Tree(Tree parent, Object root) {
        m_parent = parent;
        m_root = root;
        m_children = new ArrayList();
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public String getLabel() {
        return m_label;
    }

    /**
     * Returns the root of this tree.
     **/
    public Object getRoot() {
        return m_root;
    }

    /**
     * Adds a child element to the root of this tree. Returns the subtree rooted
     * at the newly created node.
     **/
    public Tree addChild(Object child, Object edge) {
        Tree tree = new Tree(this, child);
        m_children.add(new EdgeTreePair(edge, tree));
        return tree;
    }

    /**
     * A shortcut for <code>addChild(child, null)</code>.
     *
     * @see #addChild(Object, Object)
     **/
    public Tree addChild(Object child) {
        return addChild(child, null);
    }

    /**
     * Adds <code>subtree</code> to the root node of this tree.
     *
     * <p><span style="color: FireBrick;">Note</span>: This doesn't check for
     * cycles. If you do something like,</p>
     * 
     * <pre>
     * tree.addSubtree(tree);
     * </pre>
     *
     * <p>you're on your own. I'll add a check for cycles like this when I have
     * the time. (This probably means never.)</p>
     * 
     * @pre subtree != null && subtree.getParent() == 0
     **/
    public void addSubtree(Tree subtree, Object edge) {
        Assert.exists(subtree, "subtree");
        Assert.isTrue(subtree.getParent() == null, "parent must be null");
        subtree.m_parent = this;
        m_children.add(new EdgeTreePair(edge, subtree));
    }

    /**
     * A shortcut for <code>addSubtree(subtree,null)</code>.
     *
     * @see #addSubtree(Tree, Object)
     **/
    public void addSubtree(Tree subtree) {
        addSubtree(subtree, null);
    }

    /**
     * Returns the tree rooted at the parent node of the root of this tree or
     * <code>null</code>, if the root of this tree has no parent node.
     * 
     **/
    public Tree getParent() {
        return m_parent;
    }

    /**
     * Returns the list of {@link Tree.EdgeTreePair edge-tree pairs} parented to
     * the root node of this tree in the order in which they were initially
     * added. Manipulating the returned list does not affect this tree. For
     * example, if you remove an element from the list, no changes are made to
     * this tree.
     **/
    public List getSubtrees() {
        return new ArrayList(m_children);
    }

    /**
     * Returns a copy of this tree. The returned copy does not have a parent
     * tree. In other words, the returned tree is no longer a part of a bigger
     * tree, even if this tree was.
     *
     @ return.getParent() == null
     **/
    public Tree copy() {
        Tree result = new Tree(getRoot());
        copyRecurse(this, result);
        return result;
    }

    private static void copyRecurse(Tree from, Tree to) {
        Iterator children = from.getSubtrees().iterator();
        while ( children.hasNext() ) {
            EdgeTreePair pair = (EdgeTreePair) children.next();
            Tree result = to.addChild(pair.getTree().getRoot(), pair.getEdge());
            copyRecurse(pair.getTree(), result);
        }
    }

    public int nodeCount() {
        return nodeCountRecurse(this);
    }

    private static int nodeCountRecurse(Tree tree) {
        int result = 1;
        for ( Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            EdgeTreePair pair = (EdgeTreePair) ii.next();
            result += nodeCountRecurse(pair.getTree());
        }
        return result;
    }

    /**
     * Returns the maximum depth of the tree.
     **/
    public int depth() {
        return depthRecurse(this);
    }

    private static int depthRecurse(Tree tree) {
        if ( tree.getSubtrees().size() == 0 ) {
            return 1;
        }
        int maxDepth = 0;
        for (Iterator ii=tree.getSubtrees().iterator(); ii.hasNext(); ) {
            EdgeTreePair pair = (EdgeTreePair) ii.next();
            int depth = depthRecurse(pair.getTree());
            if ( depth > maxDepth ) {
                maxDepth = depth;
            }
        }
        return maxDepth + 1;
    }

    /**
     * Returns the list of trees such that each of the returned trees is rooted
     * at the ancestor nodes of this tree or an empty list, if the root of this
     * tree has no ancestors. The closer ancestors appear first in the list.
     **/
    public List getAncestors() {
        List result = new ArrayList();
        if ( getParent() == null ) {
            return result;
        }
        ancestorsRecurse(getParent(), result);
        return result;
    }

    private static void ancestorsRecurse(Tree node, List accumulator) {
        accumulator.add(node);
        if ( node.getParent() != null ) {
            ancestorsRecurse(node.getParent(), accumulator);
        }
    }

    /**
     * Takes a list of trees and returns a new list where each tree from the
     * passed in list is replaced with its root node.
     *
     * @pre trees != null
     **/
    public static List treesToNodes(List trees) {
        Assert.exists(trees, "trees");
        List result = new ArrayList();
        for (Iterator ii=trees.iterator(); ii.hasNext(); ) {
            result.add(((Tree) ii.next()).getRoot());
        }
        return result;
    }

    /**
     * Nodes in a tree are connected with edges. An edge can be object that
     * characterizes the relationship between the parent and the child nodes.
     * For example, if you use the tree to represent the composition structure
     * of XSLT stylesheets, then the edge object may be a string with one of two
     * possible values: "xsl:import" and "xsl:include", allowing you to
     * distinguish the method by which the parent stylesheet incorporates the
     * child.
     *
     * <p>The edge-tree pair class represents an order pair where the first
     * element is the edge, and the second is the subtree rooted at the head
     * node of the edge.</p>
     **/
    public static class EdgeTreePair {
        private Object m_edge;
        private Tree m_tree;

        private EdgeTreePair(Object edge, Tree tree) {
            m_edge = edge;
            m_tree = tree;
        }

        public Object getEdge() {
            return m_edge;
        }

        /**
         * Returns the subtree rooted at the head node of the edge.
         **/
        public Tree getTree() {
            return m_tree;
        }
    }
}
