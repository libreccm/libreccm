/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.Group;
import org.libreccm.core.GroupRepository;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.List;

/**
 *
 *
 * @author David Dao
 *
 */
public class GroupTreeModel implements TreeModel {

//    private class GroupIterator implements Iterator {
//
//        private List< m_coll;
//
//        public GroupIterator(ACSObjectCollection coll) {
//            m_coll = coll;
//        }
//
//        public boolean hasNext() {
//            return m_coll.next();
//        }
//
//        public Object next() {
//            return new GroupTreeNode(m_coll.getACSObject());
//        }
//
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//
//    }
    /**
     * Obtain the root folder of the tree
     *
     * @param state
     *
     * @return
     */
    @Override
    public TreeNode getRoot(final PageState state) {
        return new RootTreeNode();

    }

    /**
     * Check whether a given node has children
     *
     * @param node
     * @param state
     *
     * @return
     */
    @Override
    public boolean hasChildren(final TreeNode node, final PageState state) {

        if (node instanceof RootTreeNode) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Get direct children in this node.
     *
     * @param node
     * @param state
     *
     * @return
     */
    @Override
    public Iterator<Group> getChildren(final TreeNode node,
                                       final PageState state) {

        if (node instanceof RootTreeNode) {

            final CdiUtil cdiUtil = new CdiUtil();
            final GroupRepository groupRepository;
            try {
                groupRepository = cdiUtil.findBean(GroupRepository.class);
            } catch (CdiLookupException ex) {
                throw new UncheckedWrapperException(
                    "Failed to lookup GroupRepository", ex);
            }
            final List<Group> groups = groupRepository.findAll();

            return groups.iterator();
        } else {
            return null;
        }
    }

}

class RootTreeNode implements TreeNode {

    @Override
    public Object getKey() {
        return "-1";
    }

    @Override
    public Object getElement() {
        return "/";
    }

}

class GroupTreeNode implements TreeNode {

    private String m_key;
    private String m_name;

    public GroupTreeNode(Group group) {
        m_key = Long.toString(group.getSubjectId());
        m_name = group.getName();
    }

    @Override
    public Object getKey() {
        return m_key;
    }

    @Override
    public Object getElement() {
        return m_name;
    }

}
