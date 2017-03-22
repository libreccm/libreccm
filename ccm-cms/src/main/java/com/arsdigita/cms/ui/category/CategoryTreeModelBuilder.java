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
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryTreeModelLite;

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

    public final TreeModel makeModel(final Tree tree, final PageState state) {
        final Category category = (Category) m_contextModel.getSelectedKey(state);

        final CategoryTreeModelLite model = new CategoryTreeModelLite(category);

        return model;
    }

    private String getUseContext(PageState state) {
        String context = null;
        if (m_contextModel != null) {
            context = (String) m_contextModel.getSelectedKey(state);
            if ((DEFAULT_USE_CONTEXT).equals(context)) {
                context = null;
            }
        }
        return context;
    }
}
