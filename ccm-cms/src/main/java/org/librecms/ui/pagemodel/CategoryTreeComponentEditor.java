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
package org.librecms.ui.pagemodel;

import com.vaadin.ui.CheckBox;
import org.libreccm.admin.ui.AbstractPageModelComponentEditor;
import org.libreccm.admin.ui.PageModelComponentEditorController;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.librecms.CmsConstants;
import org.librecms.pagemodel.CategoryTreeComponent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryTreeComponentEditor
    extends AbstractPageModelComponentEditor<CategoryTreeComponent> {

    private static final long serialVersionUID = -6162769539698324778L;

    private final PageModelComponentEditorController controller;

    private CheckBox showFullTreeCheckBox;

    public CategoryTreeComponentEditor(
        final PageModel pageModel,
        final PageModelComponentModel componentModelInfo,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModelInfo, controller);

        this.controller = controller;

        addWidgets();
    }

    public CategoryTreeComponentEditor(
        final PageModel pageModel,
        final CategoryTreeComponent componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);

        this.controller = controller;

        addWidgets();
    }

    private void addWidgets() {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        showFullTreeCheckBox = new CheckBox(textsUtil
            .getText(
                "cms.ui.pagemodel.category_tree_component_form.show_full_tree.label"));
        addComponent(showFullTreeCheckBox);

    }

    @Override
    protected void initWidgets() {

        final CategoryTreeComponent component = getComponentModel();

        if (component != null) {
            showFullTreeCheckBox.setValue(component.isShowFullTree());
        }
    }

    @Override
    protected boolean validate() {

        //Nothing to validate here
        return true;
    }

    @Override
    protected CategoryTreeComponent createComponentModel() {
        return new CategoryTreeComponent();
    }

    @Override
    protected void updateComponentModel() {

        final CategoryTreeComponent component = getComponentModel();

        component.setShowFullTree(showFullTreeCheckBox.getValue());
    }

}
