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
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.pagemodels.AbstractComponentModelForm;
import com.arsdigita.ui.admin.pagemodels.PageModelTab;

import org.librecms.CmsConstants;
import org.librecms.pagemodel.ItemListComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemListComponentForm
    extends AbstractComponentModelForm<ItemListComponent> {

    private static final String DESCENDING_BOX = "descendingBox";
    private static final String DESCENDING = "descending";
    private static final String LIMIT_TO_TYPE = "limitToType";
    private static final String PAGE_SIZE = "pageSize";
    private static final String LIST_ORDER = "listOrder";

    private CheckboxGroup descendingBox;
    private TextField limitToTypeField;
    private TextField pageSizeField;
    private TextArea listOrderArea;

    public ItemListComponentForm(
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super("ItemListComponentForm",
              pageModelTab,
              selectedModelId,
              selectedComponentId);
    }

    @Override
    protected void addWidgets() {

        descendingBox = new CheckboxGroup(DESCENDING_BOX);
        descendingBox.addOption(new Option(
            DESCENDING, new Label(
                new GlobalizedMessage(
                    "cms.ui.pagemodel.itemlist_component_form.descending.label",
                    CmsConstants.CMS_BUNDLE))));
        add(descendingBox);

        limitToTypeField = new TextField(LIMIT_TO_TYPE);
        limitToTypeField.setLabel(new GlobalizedMessage(
            "cms.ui.pagemodel.itemlist_component_form.limit_to_type.label",
            CmsConstants.CMS_BUNDLE));
        add(limitToTypeField);

        pageSizeField = new TextField(PAGE_SIZE);
        pageSizeField.setLabel(new GlobalizedMessage(
            "cms.ui.pagemodel.itemlist_component_form.page_size.label",
            CmsConstants.CMS_BUNDLE));
        add(pageSizeField);

        listOrderArea = new TextArea(LIST_ORDER);
        listOrderArea.setLabel(new GlobalizedMessage(
            "cms.ui.pagemodel.itemlist_component_form.list_order.label",
            CmsConstants.CMS_BUNDLE));
        add(listOrderArea);
    }

    @Override
    protected ItemListComponent createComponentModel() {
        return new ItemListComponent();
    }

    @Override
    protected void updateComponentModel(final ItemListComponent componentModel,
                                        final PageState state,
                                        final FormData data) {

        final Object[] descendingValues = (Object[]) data.get(DESCENDING);
        final String limitToTypeValue = data.getString(LIMIT_TO_TYPE);
        final String pageSizeValue = data.getString(PAGE_SIZE);
        final String listOrderValue = data.getString(LIST_ORDER);

        final boolean descendingValue;
        if (descendingValues != null
                && descendingValues.length != 0
                && DESCENDING.equals(descendingValues[0])) {

            descendingValue = true;
        } else {
            descendingValue = false;
        }

        final List<String> listOrder = Arrays
            .stream(listOrderValue.split("\n"))
            .collect(Collectors.toList());

        componentModel.setDescending(descendingValue);
        componentModel.setLimitToTypes(limitToTypeValue);
        componentModel.setPageSize(Integer.parseInt(pageSizeValue));

        componentModel.setListOrder(listOrder);
    }

    @Override
    public void init(final FormSectionEvent event)
        throws FormProcessException {

        super.init(event);

        final PageState state = event.getPageState();

        final ItemListComponent component = getComponentModel();

        final Object[] descendingValue;
        if (component.isDescending()) {
            descendingValue = new Object[]{DESCENDING};
        } else {
            descendingValue = new Object[]{};
        }
        descendingBox.setValue(state, descendingValue);

        limitToTypeField.setValue(state, component.getLimitToType());

        pageSizeField.setValue(state, Integer.toString(component.getPageSize()));

        listOrderArea.setValue(state,
                               String.join("\n", component.getListOrder()));

    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        super.validate(event);

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();

        if (getSaveCancelSection().getSaveButton().isSelected(state)) {

            final String pageSizeValue = data.getString(PAGE_SIZE);
            if (pageSizeValue != null
                    && !pageSizeValue.isEmpty()
                    && !pageSizeValue.matches("\\d*")) {

                data.addError(
                    PAGE_SIZE,
                    new GlobalizedMessage(
                        "cms.ui.pagemodel.itemlist_component_form.page_size.error.not_a_number",
                        CmsConstants.CMS_BUNDLE));
            }
        }
    }
}
