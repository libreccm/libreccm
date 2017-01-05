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
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Category;

import java.util.Collection;
import java.util.HashSet;

/**
 * A List of all secondary parents of the current category.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id: CategoryLinks.java 2140 2011-01-16 12:04:20Z pboy $
 */
public class CategoryLinks extends List {
    public final static String SUB_CATEGORY = "sc";

    private final CategoryRequestLocal m_parent;
    private final SingleSelectionModel m_model;

    public CategoryLinks(final CategoryRequestLocal parent,
                         final SingleSelectionModel model) {
        super(new ParameterSingleSelectionModel
              (new BigDecimalParameter(SUB_CATEGORY)));
        setIdAttr("category_links_list");

        m_parent = parent;
        m_model = model;

        //setModelBuilder(new LinkedCategoryModelBuilder());

        // Select the category in the main tree when the
        // user selects it here
        addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final PageState state = e.getPageState();
                    final String id = (String) getSelectedKey(state);

                    if (id != null) {
                        m_model.setSelectedKey(state, id);
                    }
                }
            });

        final Label label = new Label
            (GlobalizationUtil.globalize("cms.ui.category.linked_none"));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    // Since this part is for non default parents, but the is only one... this is not needed anymore, i guess
    /*
    private class LinkedCategoryModelBuilder extends LockableImpl
            implements ListModelBuilder {
        public ListModel makeModel(List list, PageState state) {
            final Category category = m_parent.getCategory(state);

            if (category != null && category.getParent().isPresent()) {

                Collection<Category> categories = new HashSet<>();
                categories.add(category.getParentCategory());

                return new CategoryCollectionListModel
                    (categories,
                     parent == null ? null : parent.getID());
            } else {
                return List.EMPTY_MODEL;
            }
        }
    }*/

}
