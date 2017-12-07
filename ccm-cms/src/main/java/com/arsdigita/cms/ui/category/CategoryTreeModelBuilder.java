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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.CategoryTreeModelLite;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.ContentSection;

/**
 * Lists category tree.
 *
 * @author Tri Tran (tri@arsdigita.com)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
class CategoryTreeModelBuilder extends LockableImpl
    implements TreeModelBuilder {

    private static String DEFAULT_USE_CONTEXT = "<default>";

    private SingleSelectionModel m_contextModel = null;

    public CategoryTreeModelBuilder() {
        this(null);
    }

    public CategoryTreeModelBuilder(SingleSelectionModel contextModel) {
        super();
        m_contextModel = contextModel;
    }

    @Override
    public final TreeModel makeModel(final Tree tree, final PageState state) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Category root;
        if (DEFAULT_USE_CONTEXT.equals(m_contextModel.getSelectedKey(state))) {
            final ContentSection section = CMS
                .getContext()
                .getContentSection();

            final CategoryAdminController controller = cdiUtil
                .findBean(CategoryAdminController.class);
            final java.util.List<DomainOwnership> ownerships
                                                      = controller
                    .retrieveDomains(section);
            root = ownerships.get(0).getDomain().getRoot();
        } else {
            final CategoryRepository categoryRepo = cdiUtil
                .findBean(CategoryRepository.class);
            root = categoryRepo
                .findById(Long.parseLong((String) m_contextModel
                    .getSelectedKey(state)))
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Category with ID %s in the database.",
                        m_contextModel.getSelectedKey(state))));
        }

        return new CategoryTreeModelLite(root);
    }

}
