/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractApplicationTypeInstancesPane extends BoxPanel {

    private final ParameterSingleSelectionModel<String> selectedAppType;

    private final Form instanceForm;

    public AbstractApplicationTypeInstancesPane(
        final ParameterSingleSelectionModel<String> selectedAppType) {

        super(BoxPanel.VERTICAL);

        this.selectedAppType = selectedAppType;

        instanceForm = createInstanceForm();
        add(instanceForm);

    }

    protected abstract Form createInstanceForm();

    @Override
    public void register(final Page page) {

        page.setVisibleDefault(instanceForm, false);
    }

    private class InstanceTable extends Table {

        private static final int COL_TITLE = 0;
        private static final int COL_URL = 1;
        private static final int COL_EDIT = 2;

        private final AbstractApplicationTypeInstancesPane instancesPane;
        private final ParameterSingleSelectionModel<String> selectedAppType;

        public InstanceTable(
            final AbstractApplicationTypeInstancesPane instancesPane,
            final ParameterSingleSelectionModel<String> selectedAppType) {
            super();

            this.instancesPane = instancesPane;
            this.selectedAppType = selectedAppType;

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                COL_TITLE,
                new Label(new GlobalizedMessage(
                    "ui.admin.applications.instances.col_title",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_URL,
                new Label(new GlobalizedMessage(
                    "ui.admin.applications.instances.col_url",
                    ADMIN_BUNDLE))));
            columnModel.add(new TableColumn(
                COL_EDIT,
                new Label(new GlobalizedMessage(
                    "ui.admin.applications.instances.col_edit",
                    ADMIN_BUNDLE))));
            
        }

    }

}
