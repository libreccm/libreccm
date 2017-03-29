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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.util.LockableImpl;

import java.util.Collections;
import java.util.Iterator;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.Folder;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.libreccm.configuration.ConfigurationManager;

/**
 * A {@link com.arsdigita.bebop.tree.TreeModelBuilder} that produces trees
 * containing the folder structure underneath a root folder. The root folder can
 * be changed by overriding {@link #getRoot getRoot}.
 *
 * @author <a href="mailto:tri@arsdigita.com">Tri Tran</a>
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class FolderTreeModelBuilder 
    extends LockableImpl
    implements TreeModelBuilder {

    /**
     * Make a tree model that lists the hierarchy of folders underneath the
     * folder returned by {@link #getRoot getRoot}.
     *
     * @param tree  the tree in which the model is used
     * @param state represents the current request
     *
     * @return a tree model that lists the hierarchy of folders underneath the
     *         folder returned by {@link #getRoot getRoot}.
     */
    @Override
    public TreeModel makeModel(final Tree tree, final PageState state) {
        return new TreeModel() {

            @Override
            public TreeNode getRoot(final PageState state) {
                return new FolderTreeNode(getRootFolder(state));
            }

            @Override
            public boolean hasChildren(final TreeNode node,
                                       final PageState state) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final FolderTreeModelController controller = cdiUtil.findBean(
                    FolderTreeModelController.class);

                return controller.hasChildren(node);
            }

            @Override
            public Iterator<TreeNode> getChildren(final TreeNode node,
                                                  final PageState state) {
                final String nodeKey = node.getKey().toString();

                // Always expand root node
                if (nodeKey.equals(getRoot(state).getKey().toString())
                        && tree.isCollapsed(nodeKey, state)) {
                    tree.expand(nodeKey, state);
                }

                if (tree.isCollapsed(nodeKey, state)) {
                    return Collections.emptyIterator();
                }

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final FolderTreeModelController controller = cdiUtil.findBean(
                    FolderTreeModelController.class);

                final List<Folder> subFolders = controller.getChildren(node);
                return subFolders.stream()
                    .map(folder -> generateTreeNode(folder))
                    .sorted((node1, node2) -> {
                        return ((String) node1.getElement())
                            .compareTo((String) node2.getElement());
                    })
                    .collect(Collectors.toList())
                    .iterator();
            }

            private TreeNode generateTreeNode(final Folder folder) {
                final FolderTreeNode node = new FolderTreeNode(folder);

                return node;
            }

        };

    }

//    /**
//     * Return the root folder for the tree model in the current request.
//     *
//     * @param state represents the current request
//     *
//     * @return the root folder for the tree
//     *
//     */
////    protected Folder getRootFolder(final PageState state)
//        throws IllegalStateException {
//
//        final ContentSection section = CMS.getContext().getContentSection();
//        return section.getRootDocumentsFolder();
//    }
    
    /**
     * Return the root folder for the tree model in the current request.
     *
     * @param state represents the current request
     *
     * @return the root folder for the tree
     *
     */
    protected abstract Folder getRootFolder(final PageState state);

    private class FolderTreeNode implements TreeNode {

        private final Folder folder;

        public FolderTreeNode(final Folder folder) {
            this.folder = folder;
        }

        @Override
        public Object getKey() {
            return folder.getObjectId();
        }

        @Override
        public Object getElement() {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final GlobalizationHelper globalizationHelper = cdiUtil.findBean(
                GlobalizationHelper.class);
            final Locale locale = globalizationHelper.getNegotiatedLocale();
            if (folder.getTitle().hasValue(locale)) {
                return folder.getTitle().getValue(locale);
            } else {
                final ConfigurationManager confManager = cdiUtil.findBean(
                    ConfigurationManager.class);
                final KernelConfig kernelConfig = confManager.findConfiguration(
                    KernelConfig.class);
                final String value = folder.getTitle().getValue(kernelConfig.
                    getDefaultLocale());
                if (value == null) {
                    return folder.getName();
                } else {
                    return value;
                }
            }
        }

    }

}
