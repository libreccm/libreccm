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
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;

import java.util.ArrayList;

/**
 * A List of all secondary parents of the current category.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryLinks extends List {

    public final static String SUB_CATEGORY = "sc";

    private final CategoryRequestLocal m_parent;
    private final SingleSelectionModel m_model;

    public CategoryLinks(final CategoryRequestLocal parent,
                         final SingleSelectionModel model) {
        super(new ParameterSingleSelectionModel(new BigDecimalParameter(
            SUB_CATEGORY)));
        setIdAttr("category_links_list");

        m_parent = parent;
        m_model = model;

        setModelBuilder(new LinkedCategoryModelBuilder());

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

        final Label label = new Label(new GlobalizedMessage(
            "cms.ui.category.linked_none",
            CmsConstants.CMS_BUNDLE));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    // Since this part is for non default parents, but there is only one... this is not needed anymore, i guess
    private class LinkedCategoryModelBuilder extends LockableImpl
        implements ListModelBuilder {

        @Override
        public ListModel makeModel(List list, PageState state) {
            final Category category = m_parent.getCategory(state);

            if (category != null && category.getParentCategory() != null) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryController controller = cdiUtil
                    .findBean(CategoryController.class);
                final GlobalizationHelper globalizationHelper = cdiUtil
                    .findBean(GlobalizationHelper.class);
                final Category parent = controller
                    .getParentCategory(category).get();

                java.util.List<CategoryListItem> categories = new ArrayList<>();
                final CategoryListItem parentItem = new CategoryListItem();
                parentItem.setCategoryId(parent.getObjectId());
                final String label = globalizationHelper
                    .getValueFromLocalizedString(parent.getTitle(),
                                                 parent::getName);
                parentItem.setLabel(label);

                categories.add(parentItem);

                return new CategoryListModel(
                    categories,
                    parent.getObjectId());

//                return new CategoryListModel(categories,
//                                             category.getParentCategory()
//                                                 == null ? null : Long
//                                                     .parseLong(
//                                                         parent
//                                                             .getUniqueId()));
            } else {
                return List.EMPTY_MODEL;
            }
        }

    }

}
