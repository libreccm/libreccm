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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.modules.ModuleManager;
import org.libreccm.pagemodel.ComponentModels;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;

import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelDetails extends BoxPanel {

    public PageModelDetails(
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super(BoxPanel.VERTICAL);

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.pagemodels.details.back",
            AdminUiConstants.ADMIN_BUNDLE));
        backLink.setClassAttr("back-link");
        backLink.addActionListener(event -> {
            selectedModelId.clearSelection(event.getPageState());
            pageModelTab.showPageModelsTable(event.getPageState());
        });
        super.add(backLink);

        final Label heading = new Label();
        heading.setClassAttr("heading");
        heading.addPrintListener(event -> {
            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();
            final PageModelRepository pageModelRepo = CdiUtil
                .createCdiUtil()
                .findBean(PageModelRepository.class);
            final PageModel pageModel = pageModelRepo
                .findById(Long.parseLong(selectedModelId.getSelectedKey(state)))
                .get();
            target.setLabel(new GlobalizedMessage(
                "ui.admin.pagemodels.details.heading",
                AdminUiConstants.ADMIN_BUNDLE,
                new String[]{pageModel.getName()}));
        });
        super.add(heading);

        final PropertySheet propertySheet = new PropertySheet(
            new PageModelPropertySheetModelBuilder(selectedModelId));
        super.add(propertySheet);

        final ActionLink editProperties = new ActionLink(new GlobalizedMessage(
            "ui.admin.pagemodels.details.edit_properties",
            AdminUiConstants.ADMIN_BUNDLE));
        editProperties.addActionListener(event -> {
            pageModelTab.showPageModelForm(event.getPageState());
        });
        super.add(editProperties);

        final ActionLink addComponent = new ActionLink(new GlobalizedMessage(
            "ui.admin.pagemodels.details.add_component",
            AdminUiConstants.ADMIN_BUNDLE));
        addComponent.addActionListener(event -> {
            //pageModelTab.showNewComponentForm(state, componentModelClass);
        });
        super.add(addComponent);

        final ComponentsTable componentsTable
                                  = new ComponentsTable(
                pageModelTab, selectedModelId, selectedComponentId);
        super.add(componentsTable);
    }

    private class AddComponentForm extends Form {

        private final SingleSelect selectType;

        public AddComponentForm() {
            super("pagemodel_add_component_form");

            selectType = new SingleSelect("select_component_type");
            try {
                selectType.addPrintListener(event -> {
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final ComponentModels componentModels = cdiUtil
                        .findBean(ComponentModels.class);
                    
                    

                });
            } catch (TooManyListenersException ex) {
                throw new UnexpectedErrorException(ex);
            }

        }

    }

}
