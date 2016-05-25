/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationTreeModel implements TreeModel {

    public ApplicationTreeModel() {

    }

    @Override
    public TreeNode getRoot(final PageState state) {
        return new RootTreeNode();
    }

    @Override
    public boolean hasChildren(final TreeNode node,
                               final PageState state) {
        if (node instanceof RootTreeNode) {
            return true;
        } else if (node instanceof ApplicationTypeTreeNode) {
            final ApplicationTypeTreeNode typeNode
                                              = (ApplicationTypeTreeNode) node;
            final ApplicationType appType = typeNode.getApplicationType();
            if (appType.singleton()) {
                return false;
            } else {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ApplicationRepository appRepo = cdiUtil.findBean(
                    ApplicationRepository.class);
                final List<CcmApplication> apps = appRepo.findByType(appType
                    .name());
                return !apps.isEmpty();
            }
        } else if (node instanceof ApplicationInstanceTreeNode) {
            return false;
        } else {
            throw new IllegalArgumentException(
                "The ApplicationTreeModel can only work with "
                    + "ApplicationTypeTreeNodes and "
                    + "ApplicationInstanceTreeNodes.");
        }
    }

    @Override
    public Iterator getChildren(final TreeNode node,
                                final PageState state) {
        if (node instanceof RootTreeNode) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final org.libreccm.web.ApplicationManager appManager = cdiUtil
                .findBean(org.libreccm.web.ApplicationManager.class);
            return new AppTypesIterator(appManager.getApplicationTypes()
                .values());
        } else if (node instanceof ApplicationTypeTreeNode) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ApplicationRepository appRepo = cdiUtil.findBean(
                ApplicationRepository.class);
            return new AppIterator(appRepo.findByType(
                ((ApplicationTypeTreeNode) node).getApplicationType().name()));
        } else {
            throw new IllegalArgumentException(
                "ApplicationTreeModel#getChildren can only process "
                    + "the RootTreeNode and the instances of "
                    + "ApplicationTypeTreeNode.");
        }
    }

    private class RootTreeNode implements TreeNode {

        public RootTreeNode() {
            //Nothing
        }

        @Override
        public Object getKey() {
            return "-1";
        }

        @Override
        public Object getElement() {
            return "/";
        }

    }

    private class AppTypesIterator
        implements Iterator<ApplicationTypeTreeNode> {

        private final Iterator<ApplicationType> appTypes;

        public AppTypesIterator(final Collection<ApplicationType> appTypes) {
            this.appTypes = appTypes.iterator();
        }

        @Override
        public boolean hasNext() {
            return appTypes.hasNext();
        }

        @Override
        public ApplicationTypeTreeNode next() {
            return new ApplicationTypeTreeNode(appTypes.next());
        }

    }

    private class AppIterator implements Iterator<ApplicationInstanceTreeNode> {

        private final Iterator<CcmApplication> applications;

        public AppIterator(final Collection<CcmApplication> applications) {
            this.applications = applications.iterator();
        }

        @Override
        public boolean hasNext() {
            return applications.hasNext();
        }

        @Override
        public ApplicationInstanceTreeNode next() {
            return new ApplicationInstanceTreeNode(applications.next());
        }

    }

}
