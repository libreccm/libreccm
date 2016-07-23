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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationCreateException;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DefaultApplicationInstanceForm extends AbstractAppInstanceForm {

    private static final int COL_PRIMARY_URL = 0;

    private SaveCancelSection saveCancelSection;

    public DefaultApplicationInstanceForm(
        final String name,
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {

        super(name, selectedAppType, selectedAppInstance);

    }

    @Override
    protected void createWidgets() {

        addInstanceTable();

        final Label newInstanceFormHeading = new Label(new GlobalizedMessage(
            "ui.admin.applications.new_instance_form.title", ADMIN_BUNDLE));
        newInstanceFormHeading.setClassAttr("heading");
        add(newInstanceFormHeading);
        
        final TextField primaryUrl = new TextField("new_instance_primary_url");
        primaryUrl.setLabel(new GlobalizedMessage(
            "ui.admin.applications.new_instance.primary_url", ADMIN_BUNDLE));
        primaryUrl.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage(
                "ui.admin.applications.new_instance.primary_url.error.not_empty",
                ADMIN_BUNDLE)));
        add(primaryUrl);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);
        
        //add(new Text("placeholder"));
    }

    protected SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    protected void addInstanceTable() {
        final Label tableHeading = new Label(new GlobalizedMessage(
            "ui.admin.applications.instance_table.heading", ADMIN_BUNDLE));
        tableHeading.setClassAttr("heading");
        add(tableHeading);

        final Table instanceTable = new Table();
        final TableColumnModel colModel = instanceTable.getColumnModel();
        colModel.add(new TableColumn(
            COL_PRIMARY_URL,
            new Label(new GlobalizedMessage(
                "ui.admin.applications.instances_table.col_primary_url",
                ADMIN_BUNDLE))));
        instanceTable.setModelBuilder(new InstanceTableModelBuilder(this));
        add(instanceTable);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected FormProcessListener createProcessListener() {
        return (FormSectionEvent e) -> {
            final PageState state = e.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String primaryUrlData = data.getString(
                    "new_instance_primary_url");

                final org.libreccm.web.ApplicationManager appManager = CdiUtil
                    .createCdiUtil().findBean(
                        org.libreccm.web.ApplicationManager.class);
                try {
                    appManager.createInstance(
                        getSelectedAppType(state),
                        primaryUrlData,
                        (Class<CcmApplication>) getSelectedAppType(state)
                        .applicationClass());
                } catch (ApplicationCreateException ex) {
                    throw new UncheckedWrapperException(
                        "Failed to create new application instance.", ex);
                }

                final Class<?> clazz = getSelectedAppType(state)
                    .applicationClass();
                if (!clazz.isAssignableFrom(CcmApplication.class)) {
                    throw new UncheckedWrapperException(String.format(
                        "Application class \"%s\" is not a sub class of "
                            + "class \"%s\". Something is wrong...",
                        clazz.getName(), CcmApplication.class.getName()));
                }
                final CcmApplication application;
                try {
                    application = (CcmApplication) clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new UncheckedWrapperException(
                        String.format("Failed to create object of "
                                          + "class \"%s\".",
                                      clazz.getName()),
                        ex);
                }

                application.setPrimaryUrl(primaryUrlData);

                final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                    .findBean(ApplicationRepository.class);
                appRepo.save(application);
            }
        };
    }

    private class InstanceTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final AbstractAppInstanceForm instanceForm;

        public InstanceTableModelBuilder(
            final AbstractAppInstanceForm instanceForm) {
            this.instanceForm = instanceForm;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            return new InstanceTableModel(instanceForm.getSelectedAppType(state));
        }

    }

    private class InstanceTableModel implements TableModel {

        private final List<CcmApplication> instances;
        private int index = -1;

        public InstanceTableModel(final ApplicationType appType) {
            final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                .findBean(ApplicationRepository.class);
            instances = appRepo.findByType(appType.name());
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < instances.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case COL_PRIMARY_URL:
                    return instances.get(index).getPrimaryUrl();
                default:
                    throw new IllegalArgumentException("Illegal columnIndex");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return Long.toString(instances.get(index).getObjectId());
        }

    }

}
