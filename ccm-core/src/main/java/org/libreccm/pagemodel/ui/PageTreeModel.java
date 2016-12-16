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
package org.libreccm.pagemodel.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;

import java.util.Iterator;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.pagemodel.PageModelRepository;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageTreeModel implements TreeModel {

    @Override
    public TreeNode getRoot(final PageState state) {
        return new RootNode();
    }

    @Override
    public boolean hasChildren(final TreeNode node, final PageState state) {
        if (node instanceof RootNode) {
            return true;
        } else if (node instanceof ApplicationTypeTreeNode) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PageModelRepository pageModelRepo = cdiUtil.findBean(
                PageModelRepository.class);
            final CcmApplication application = ((ApplicationTreeNode) node)
                .getApplication();
            final long count = pageModelRepo.countByApplication(application);
            
            return count > 0;
        } else if (node instanceof PageModelTreeNode) {
            return false;
        } else {
            throw new IllegalArgumentException(String.format(
                "Unexpected node type: \"%s\".", node.getClass().getName()));
        }
    }

    @Override
    public Iterator getChildren(final TreeNode node, final PageState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class RootNode implements TreeNode {

        @Override
        public Object getKey() {
            return "-1";
        }

        @Override
        public Object getElement() {
            return "PageModels";
        }

    }

}
