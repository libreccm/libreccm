/* 
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications.tree;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.ui.admin.ApplicationsAdministrationTab;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

/**
 * A {@link TreeModel} for the tree of applications in
 * {@link ApplicationsAdministrationTab}. The tree consists of two different
 * types of nodes: Nodes for {@link ApplicationTypes} and nodes for
 * {@link CCmApplication} instances.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: ApplicationTreeModel.java 2406 2013-10-31 19:52:22Z jensp $
 */
public class ApplicationTreeModel implements TreeModel {

    public ApplicationTreeModel() {
        //Nothing        
    }

    @Override
    public TreeNode getRoot(final PageState state) {
        return new RootTreeNode();
    }

    @Override
    public boolean hasChildren(final TreeNode node, final PageState state) {
        if (node instanceof RootTreeNode) {
            return true;
        } else if (node instanceof ApplicationTypeTreeNode) {
            final ApplicationTypeTreeNode typeTreeNode
                                              = (ApplicationTypeTreeNode) node;

            //if (typeTreeNode.getApplicationType().isSingleton()) {
            if (typeTreeNode.isSingleton()) {
                return false;
            } else {
                //return !retrieveApplicationInstances(typeTreeNode.getApplicationType()).isEmpty();
                //return !retrieveApplicationInstances(typeTreeNode.getApplicationType()).isEmpty();
                return !retrieveApplicationInstances(typeTreeNode.getObjecType())
                    .isEmpty();
            }
        } else if (node instanceof ApplicationInstanceTreeNode) {
            return false;
        } else {
            throw new IllegalArgumentException(
                "The ApplicationTreeModel can only work with ApplicationTypeTreeNodes and"
                + "ApplicationInstanceTreeNodes.");
        }
    }

    @Override
    public Iterator getChildren(final TreeNode node, final PageState state) {
        if (node instanceof RootTreeNode) {
            final ApplicationManager appManager = CdiUtil.createCdiUtil()
                .findBean(ApplicationManager.class);

            final Collection<ApplicationType> appTypes = appManager
                .getApplicationTypes().values();

            return new AppTypesIterator(appTypes);
        } else if (node instanceof ApplicationTypeTreeNode) {
            final ApplicationTypeTreeNode typeTreeNode
                                              = (ApplicationTypeTreeNode) node;

            final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                .findBean(ApplicationRepository.class);

            final List<CcmApplication> applications = appRepo.findByType(
                typeTreeNode.getObjecType());

            return new AppIterator(applications);
        } else if (node instanceof ApplicationInstanceTreeNode) {
            return null;
        } else {
            throw new IllegalArgumentException(
                "The ApplicationTreeModel can only work with ApplicationTypeTreeNodes and"
                + "ApplicationInstanceTreeNodes.");
        }
    }

    private List<CcmApplication> retrieveApplicationInstances(
        final ApplicationType applicationType) {
        final ApplicationRepository appRepo = CdiUtil.createCdiUtil().findBean(
            ApplicationRepository.class);

        return appRepo.findByType(applicationType.name());
    }

    private List<CcmApplication> retrieveApplicationInstances(
        final String appObjectType) {
        final ApplicationRepository appRepo = CdiUtil.createCdiUtil().findBean(
            ApplicationRepository.class);

        return appRepo.findByType(appObjectType);
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

    private class AppTypesIterator implements Iterator<ApplicationTypeTreeNode> {

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

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
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

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

}
