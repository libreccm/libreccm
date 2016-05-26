/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import java.util.ArrayList;
import java.util.List;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

/**
 * Pane for multi instance applications. Additional to the data provided by
 * {@link LegacyBaseApplicationPane} it shows a table of all instances of the
 * application type and a form for creating new instances of the application
 * type.
 *
 * @param <T>
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class LegacyMultiInstanceApplicationPane<T extends CcmApplication>
        extends LegacyBaseApplicationPane {

    private final static int COL_TITLE = 0;
    private final static int COL_URL = 1;
    private final static int COL_DESC = 2;

    public LegacyMultiInstanceApplicationPane(final ApplicationType applicationType,
                                        final Form createForm) {
        super(applicationType);

        //final ApplicationCollection applications = Application.retrieveAllApplications(applicationType.
        //        getApplicationObjectType());
        //applications.rewind();
        final Table table = new Table();
        table.getColumnModel().add(
                new TableColumn(COL_TITLE,
                                new Label(GlobalizationUtil.globalize(
                                                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_title.header"))));
        table.getColumnModel().add(
                new TableColumn(COL_URL,
                                new Label(GlobalizationUtil.
                                        globalize(
                                                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_url.header"))));
        table.getColumnModel().add(
                new TableColumn(COL_DESC,
                                new Label(GlobalizationUtil.
                                        globalize(
                                                "ui.admin.applicationsMultiInstanceApplicationPane.instances.table.col_desc.header"))));

        //table.setModelBuilder(new ApplicationInstancesTableModelBuilder(applications));
        table.setModelBuilder(new ApplicationInstancesTableModelBuilder(
                applicationType.name()));

        addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.MultiInstanceApplicationPane.instances")),
                   table);

        if (createForm == null) {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.MultiInstanceApplicationPane.manage_instances.heading")),
                       new Label(GlobalizationUtil.globalize(
                                       "ui.admin.MultiInstancePane.manage.no_create_form_found",
                                       new String[]{applicationType.name()})));
        } else {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.MultiInstanceApplicationPane.create_instance")),
                       createForm);

        }
    }

    private class ApplicationInstancesTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        //private final ApplicationCollection applications;
        private final String appType;

        //public ApplicationInstancesTableModelBuilder(final ApplicationCollection applications) {
        //    super();
        //
        //    this.applications = applications;
        //}
        public ApplicationInstancesTableModelBuilder(final String appType) {
            super();

            //this.applications = Application.retrieveAllApplications(appType);
            this.appType = appType;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            return new ApplicationInstancesTableModel(table, appType);
        }
    }

    private class ApplicationInstancesTableModel implements TableModel {

        private final Table table;
        //private final ApplicationCollection applications;
        private final List<AppData> appData = new ArrayList<>();
        private int currentIndex = -1;

        //public ApplicationInstancesTableModel(final Table table, final ApplicationCollection applications) {
        //    this.table = table;
        //    this.applications = applications;
        //}
        public ApplicationInstancesTableModel(final Table table,
                                              final String appType) {
            this.table = table;
            final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                .findBean(ApplicationRepository.class);
            final List<CcmApplication> applications = appRepo.
                    findByType(appType);
            for (CcmApplication application : applications) {
                addAppData(application);
            }
        }

        private void addAppData(final CcmApplication application) {
            appData.add(new AppData(application.getTitle().getValue(),
                                    application.getDescription().getValue(),
                                    application.getPrimaryUrl()));
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            currentIndex++;
            return currentIndex < appData.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case COL_TITLE:
                    //return applications.getApplication().getTitle();
                    return appData.get(currentIndex).getTitle();
                case COL_DESC:
                    //return applications.getApplication().getDescription();
                    return appData.get(currentIndex).getDescription();
                case COL_URL:
                    //return applications.getApplication().getPath();
                    return appData.get(currentIndex).getPath();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            //if (SessionManager.getSession().getTransactionContext().inTxn()) {
            //    SessionManager.getSession().getTransactionContext().commitTxn();
            //}
            //return applications.getApplication().getPath();
            return appData.get(currentIndex).getPath();
        }
    }

    private class AppData {

        private final String title;
        private final String description;
        private final String path;

        public AppData() {
            title = "";
            description = "";
            path = "";
        }

        public AppData(final String title, final String description,
                       final String path) {
            this.title = title;
            this.description = description;
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPath() {
            return path;
        }
    }
}
