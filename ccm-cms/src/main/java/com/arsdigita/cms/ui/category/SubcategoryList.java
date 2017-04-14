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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Category;

/**
 * A List of all subcategories of the current category.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #15 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class SubcategoryList extends SortableCategoryList {
    private final CategoryRequestLocal m_parent;
    private final SingleSelectionModel m_model;

    public SubcategoryList(final CategoryRequestLocal parent,
                           final SingleSelectionModel model) {
        super(parent);

        m_parent = parent;
        m_model = model;

        setIdAttr("subcategories_list");

        setModelBuilder(new SubcategoryModelBuilder());

        // Select the category in the main tree when the
        // user selects it here
        addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    final PageState state = event.getPageState();
                    final String id = (String) getSelectedKey(state);

                    if (id != null) {
                        m_model.setSelectedKey(state, id);
                    }
                }
            });

        Label label = new Label(GlobalizationUtil.globalize
                                ("cms.ui.category.subcategory.none"));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    private class SubcategoryModelBuilder extends LockableImpl
            implements ListModelBuilder {
        public ListModel makeModel(List list, PageState state) {
            final Category category = m_parent.getCategory(state);

            if (category != null && !category.getSubCategories().isEmpty()) {
                java.util.List<Category> children = category.getSubCategories();
                return new CategoryListModel(children);
            } else {
                return List.EMPTY_MODEL;
            }
        }
    }
}
