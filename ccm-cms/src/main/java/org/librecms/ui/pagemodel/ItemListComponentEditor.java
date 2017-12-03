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

import com.vaadin.server.UserError;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.libreccm.admin.ui.AbstractPageModelComponentEditor;
import org.libreccm.admin.ui.PageModelComponentEditorController;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.librecms.CmsConstants;
import org.librecms.pagemodel.ItemListComponent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemListComponentEditor
    extends AbstractPageModelComponentEditor<ItemListComponent> {

    private static final long serialVersionUID = 8607871974091248260L;

    private final PageModelComponentEditorController controller;

    private CheckBox descendingCheckBox;
    private TextField limitToTypeField;
    private TextField pageSizeField;
    private TextArea listOrderArea;

    public ItemListComponentEditor(
        final PageModel pageModel,
        final PageModelComponentModel componentModelInfo,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModelInfo, controller);

        this.controller = controller;

        addWidgets();
    }

    public ItemListComponentEditor(
        final PageModel pageModel,
        final ItemListComponent componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);

        this.controller = controller;

        addWidgets();
    }

    private void addWidgets() {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        descendingCheckBox = new CheckBox(textsUtil
            .getText(
                "cms.ui.pagemodel.itemlist_component_form.descending.label"));
        addComponent(descendingCheckBox);

        limitToTypeField = new TextField(textsUtil
            .getText(
                "cms.ui.pagemodel.itemlist_component_form.limit_to_type.label"));
        addComponent(limitToTypeField);

        pageSizeField = new TextField(textsUtil
            .getText("cms.ui.pagemodel.itemlist_component_form.page_size.label"));
        pageSizeField.setValue("25");
        addComponent(pageSizeField);

        listOrderArea = new TextArea(textsUtil
            .getText("cms.ui.pagemodel.itemlist_component_form.list_order.label"));
        addComponent(listOrderArea);
    }

    @Override
    protected void initWidgets() {

        final ItemListComponent itemListComponent = getComponentModel();

        if (itemListComponent != null) {
            descendingCheckBox.setValue(itemListComponent.isDescending());
            limitToTypeField.setValue(itemListComponent.getLimitToType());
            pageSizeField
                .setValue(Integer.toString(itemListComponent.getPageSize()));
            listOrderArea.setValue(String.join("\n", 
                                               itemListComponent.getListOrder()));
            
        }
    }

    @Override
    protected boolean validate() {

        final String pageSizeValue = pageSizeField.getValue();
        if (pageSizeValue != null
                && !pageSizeValue.isEmpty()
                && !pageSizeValue.matches("\\d*")) {

            final LocalizedTextsUtil textsUtil = controller
                .getGlobalizationHelper()
                .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

            pageSizeField.setComponentError(new UserError(textsUtil
                .getText(
                    "cms.ui.pagemodel.itemlist_component_form.page_size.error.not_a_number")));

            return false;
        }

        return true;
    }

    @Override
    protected ItemListComponent createComponentModel() {
        return new ItemListComponent();
    }

    @Override
    protected void updateComponentModel() {
        
        final ItemListComponent component = getComponentModel();
        
        final boolean descending = descendingCheckBox.getValue();
        final String limitToType = limitToTypeField.getValue();
        final int pageSize = Integer.parseInt(pageSizeField.getValue());
        final List<String> listOrder = Arrays
        .stream(listOrderArea.getValue().split("\n"))
        .collect(Collectors.toList());
        
        component.setDescending(descending);
        component.setLimitToType(limitToType);
        component.setPageSize(pageSize);
        component.setListOrder(listOrder);
    }

}
