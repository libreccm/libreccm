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
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.ComponentModel;

import java.lang.reflect.InvocationTargetException;

/**
 * Tab for {@code /ccm/admin} for managing {@link PageModel}s.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PageModelsTab extends LayoutPanel {

    private final ParameterSingleSelectionModel<String> selectedModelId;
    private final ParameterSingleSelectionModel<String> selectedComponentId;
    private final ActionLink addNewModel;
    private final PageModelsTable pageModelsTable;
    private final PageModelDetails pageModelDetails;
    private final PageModelForm pageModelForm;
    private final MetaForm componentForm;

    private Class<? extends ComponentModel> componentModelClass;

    public PageModelsTab() {

        super();

        super.setClassAttr("sidebarNavPanel");

        final BoxPanel left = new BoxPanel(BoxPanel.VERTICAL);

        selectedModelId = new ParameterSingleSelectionModel<>(
            new StringParameter("selected_pagemodel_id"));
        selectedComponentId = new ParameterSingleSelectionModel<>(
            new StringParameter(("selected_pagemodel_component_id")));

        pageModelsTable = new PageModelsTable(this, selectedModelId);
        pageModelDetails = new PageModelDetails(this,
                                                selectedModelId,
                                                selectedComponentId);
        pageModelForm = new PageModelForm(this, selectedModelId);

        addNewModel = new ActionLink(new GlobalizedMessage(
            "ui.admin.pagemodels.add_new_pagemodel_link",
            AdminUiConstants.ADMIN_BUNDLE));
        addNewModel.addActionListener(event -> {
            showPageModelForm(event.getPageState());
        });

        componentForm = new MetaForm("componentsForm") {

            @Override
            public Form buildForm(final PageState state) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PageModelsController controller = cdiUtil
                    .findBean(PageModelsController.class);

                try {
                    final Class<? extends Form> formClass;
                    if (selectedComponentId.getSelectedKey(state) == null
                            || selectedComponentId.getSelectedKey(state)
                            .isEmpty()) {
                        formClass = controller
                            .getComponentModelForm(componentModelClass);
                    } else {
                        formClass = controller
                            .getComponentModelForm(Long
                                .parseLong(selectedComponentId
                                    .getSelectedKey(state)));
                    }
                    return formClass
                        .getDeclaredConstructor(PageModelsTab.class,
                            ParameterSingleSelectionModel.class,
                            ParameterSingleSelectionModel.class)
                        .newInstance(PageModelsTab.this,
                                     selectedModelId,
                                     selectedComponentId);
                } catch (InstantiationException
                             | InvocationTargetException
                             | IllegalAccessException
                             | NoSuchMethodException ex) {
                    throw new UnexpectedErrorException(ex);
                }
            }
        };

        final BoxPanel right = new BoxPanel(BoxPanel.VERTICAL);
        right.add(addNewModel);
        right.add(pageModelsTable);
        right.add(pageModelDetails);
        right.add(pageModelForm);
        right.add(componentForm);

        setLeft(left);
        setRight(right);
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addGlobalStateParam(selectedModelId.getStateParameter());
        page.addGlobalStateParam(selectedComponentId.getStateParameter());

        page.setVisibleDefault(addNewModel, true);
        page.setVisibleDefault(pageModelsTable, true);
        page.setVisibleDefault(pageModelDetails, false);
        page.setVisibleDefault(pageModelForm, false);
        page.setVisibleDefault(componentForm, false);
    }

    protected void showNewComponentForm(
        final PageState state,
        final Class<? extends ComponentModel> componentModelClass) {

        this.componentModelClass = componentModelClass;
        showComponentForm(state);

    }

    protected void showComponentForm(final PageState state) {
        addNewModel.setVisible(state, false);
        pageModelsTable.setVisible(state, false);
        pageModelDetails.setVisible(state, false);
        pageModelForm.setVisible(state, false);
        componentForm.setVisible(state, true);
    }

    protected void showPageModelDetails(final PageState state) {
        addNewModel.setVisible(state, false);
        pageModelsTable.setVisible(state, false);
        pageModelDetails.setVisible(state, true);
        pageModelForm.setVisible(state, false);
        componentForm.setVisible(state, false);
    }

    protected void showPageModelForm(final PageState state) {
        addNewModel.setVisible(state, false);
        pageModelsTable.setVisible(state, false);
        pageModelDetails.setVisible(state, false);
        pageModelForm.setVisible(state, true);
        componentForm.setVisible(state, false);
    }

    protected void showPageModelsTable(final PageState state) {
        addNewModel.setVisible(state, true);
        pageModelsTable.setVisible(state, true);
        pageModelDetails.setVisible(state, false);
        pageModelForm.setVisible(state, false);
        componentForm.setVisible(state, false);
    }

}
