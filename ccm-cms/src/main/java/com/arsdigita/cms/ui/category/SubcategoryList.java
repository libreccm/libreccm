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
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;

/**
 * A List of all subcategories of the current category.
 *
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SubcategoryList extends SortableCategoryList {

    private final CategoryRequestLocal parentCategory;
    private final SingleSelectionModel<String> selectedCategoryId;

    public SubcategoryList(
        final CategoryRequestLocal parentCategory,
        final SingleSelectionModel<String> selectedCategoryId) {

        super(parentCategory);

        this.parentCategory = parentCategory;
        this.selectedCategoryId = selectedCategoryId;

        super.setIdAttr("subcategories_list");

        setModelBuilder(new SubcategoryModelBuilder());

        // Select the category in the main tree when the
        // user selects it here
        super.addActionListener(this::actionPerformed);

        Label label = new Label(new GlobalizedMessage(
            "cms.ui.category.subcategory.none",
            CmsConstants.CMS_BUNDLE));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    /**
     * Select the category in the main tree when the user selects it here
     *
     * @param event
     */
    private void actionPerformed(final ActionEvent event) {

        final PageState state = event.getPageState();
        final String categoryId = (String) getSelectedKey(state);

        if (categoryId != null) {
            selectedCategoryId.setSelectedKey(state, categoryId);
        }
    }

    public ListModel makeMake(final List list, final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryManager categoryManager = cdiUtil
            .findBean(CategoryManager.class);
        final Category category = parentCategory.getCategory(state);

        if (category != null
                && categoryManager.hasSubCategories(category)) {

            final CategoryAdminController controller = cdiUtil.findBean(
                CategoryAdminController.class);
            final java.util.List<CategoryListItem> children = controller
                .generateSubCategoryList(category);

            return new CategoryListModel(children);
        } else {
            return List.EMPTY_MODEL;
        }

    }

    private class SubcategoryModelBuilder extends LockableImpl
        implements ListModelBuilder {

        @Override
        public ListModel makeModel(final List list, final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryManager categoryManager = cdiUtil
                .findBean(CategoryManager.class);
            final Category category = parentCategory.getCategory(state);

            if (category != null
                    && categoryManager.hasSubCategories(category)) {

                final CategoryAdminController controller = cdiUtil.findBean(
                    CategoryAdminController.class);
                final java.util.List<CategoryListItem> children = controller
                    .generateSubCategoryList(category);

                return new CategoryListModel(children);
            } else {
                return List.EMPTY_MODEL;
            }
        }

    }

}
