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
package com.arsdigita.cms.ui.pagemodel;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.pagemodels.AbstractComponentModelForm;
import com.arsdigita.ui.admin.pagemodels.PageModelTab;

import org.librecms.CmsConstants;
import org.librecms.pagemodel.CategoryTreeComponent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryTreeComponentForm extends AbstractComponentModelForm<CategoryTreeComponent> {

    private final static String SHOW_FULL_TREE_BOX = "showFullTreeBox";
    private final static String SHOW_FULL_TREE = "showFullTree";

    private CheckboxGroup showFullTreeCheckbox;

    public CategoryTreeComponentForm(
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super("CategoryTreeComponentForm", pageModelTab, selectedModelId,
              selectedComponentId);
    }

    @Override
    protected void addWidgets() {

        showFullTreeCheckbox = new CheckboxGroup(SHOW_FULL_TREE_BOX);
        showFullTreeCheckbox.addOption(new Option(
            SHOW_FULL_TREE,
            new Label(new GlobalizedMessage(
                "cms.ui.pagemodel.category_tree_component_form.show_full_tree.label",
                CmsConstants.CMS_BUNDLE))));
        add(showFullTreeCheckbox);
    }

    @Override
    protected CategoryTreeComponent createComponentModel() {
        return new CategoryTreeComponent();
    }

    @Override
    protected void updateComponentModel(
        final CategoryTreeComponent componentModel,
        final PageState state,
        final FormData data) {

        final Object[] value = (Object[]) data.get(SHOW_FULL_TREE_BOX);
        if (value != null
                && value.length != 0
                && SHOW_FULL_TREE.equals(value[0])) {

            componentModel.setShowFullTree(true);
        } else {
            componentModel.setShowFullTree(false);
        }
    }

    @Override
    public void init(final FormSectionEvent event)
        throws FormProcessException {

        super.init(event);

        final PageState state = event.getPageState();

        final CategoryTreeComponent component = getComponentModel();

        final Object[] showFullTreeValue;
        if (component != null && component.isShowFullTree()) {
            showFullTreeValue = new Object[]{SHOW_FULL_TREE};
        } else {
            showFullTreeValue = new Object[]{};
        }

        showFullTreeCheckbox.setValue(state, showFullTreeValue);
    }

}
