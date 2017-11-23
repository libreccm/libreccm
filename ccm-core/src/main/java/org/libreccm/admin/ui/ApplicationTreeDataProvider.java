/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class ApplicationTreeDataProvider
    extends AbstractBackEndHierarchicalDataProvider<ApplicationTreeNode, String> {

    private static final long serialVersionUID = 7089444870777208500L;

    @Inject
    private ApplicationManager applicationManager;

    @Inject
    private ApplicationRepository applicationRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private final ApplicationTreeNode rootNode;

    public ApplicationTreeDataProvider() {
        rootNode = new ApplicationTreeNode();
        rootNode.setNodeId(ApplicationTreeNode.ROOT);
        rootNode.setNodeType(ApplicationTreeNodeType.ROOT_NODE);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected Stream<ApplicationTreeNode> fetchChildrenFromBackEnd(
        final HierarchicalQuery<ApplicationTreeNode, String> query) {

        final ApplicationTreeNode node = query
            .getParentOptional()
            .orElse(rootNode);

        switch (node.getNodeType()) {
            case APPLICATION_TYPE_NODE:
                return fetchApplicationInstances(node);
            case ROOT_NODE:
                return fetchApplicationTypes();
            default:
                throw new IllegalArgumentException(String
                    .format("Invalid value \"%s\" for nodeType.",
                            node.getNodeType()));
        }
    }

    private Stream<ApplicationTreeNode> fetchApplicationTypes() {

        return applicationManager
            .getApplicationTypes()
            .values()
            .stream()
            .map(this::buildApplicationTreeNode)
            .sorted();
    }

    private ApplicationTreeNode buildApplicationTreeNode(
        final ApplicationType type) {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(type.descBundle());

        final String title = bundle.getString(type.titleKey());

        final ApplicationTreeNode node = new ApplicationTreeNode();
        node.setTitle(title);

        if (type.singleton()) {
            node.setNodeType(ApplicationTreeNodeType.SINGLETON_APPLICATION_NODE);

            final List<CcmApplication> instances = applicationRepo
                .findByType(type.name());

            if (instances.size() > 1) {
                throw new UnexpectedErrorException(String
                    .format("Application type \"%s\" is marked as singleton but"
                                + "there are multiple instances.",
                            type.name()));
            }

            final CcmApplication instance = instances.get(0);
            node.setNodeId(instance.getUuid());
        } else {
            node.setNodeType(ApplicationTreeNodeType.APPLICATION_TYPE_NODE);
            node.setNodeId(type.name());
        }

        return node;
    }

    private Stream<ApplicationTreeNode> fetchApplicationInstances(
        final ApplicationTreeNode parent) {

        if (parent.getNodeType()
            != ApplicationTreeNodeType.APPLICATION_TYPE_NODE) {
            throw new IllegalArgumentException("Provided parent node is not a "
                                                   + ApplicationTreeNodeType.APPLICATION_TYPE_NODE);
        }

        final String type = parent.getNodeId();

        return applicationRepo
            .findByType(type)
            .stream()
            .map(this::buildApplicationTreeNode)
            .sorted();
    }

    private ApplicationTreeNode buildApplicationTreeNode(
        final CcmApplication application) {

        final ApplicationTreeNode node = new ApplicationTreeNode();

        node.setNodeId(application.getUuid());
        node.setNodeType(ApplicationTreeNodeType.APPLICATION_NODE);
        node.setTitle(globalizationHelper
            .getValueFromLocalizedString(application.getTitle()));
        
        return node;
    }

    @Override
    public int getChildCount(
        final HierarchicalQuery<ApplicationTreeNode, String> query) {
        
        return (int) fetchChildrenFromBackEnd(query).count();
    }

    @Override
    public boolean hasChildren(final ApplicationTreeNode item) {

        switch (item.getNodeType()) {
            case APPLICATION_NODE:
                return false;
            case APPLICATION_TYPE_NODE:
                return true;
            case ROOT_NODE:
                return true;
            case SINGLETON_APPLICATION_NODE:
                return false;
            default:
                throw new IllegalArgumentException(String
                    .format("Invalid value \"%s\" for nodeType.",
                            item.getNodeType()));
        }
    }

}
