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
package com.arsdigita.ui.admin.importexport;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.ArrayList;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ExportSection extends BoxPanel {

    public ExportSection() {
        super(BoxPanel.VERTICAL);

        add(new ExportForm());
        add(new StatusLabel());
        add(new ReportForm());

    }

    private class ExportForm extends Form implements FormProcessListener {

        private final SaveCancelSection saveCancelSection;

        public ExportForm() {
            super("exportForm");
            
            // This placeholder will be replaced with a list of the available
            // exporters and checkboxes to select the exporters to use.
            add(new Text("export section placeholder"));
            
            saveCancelSection = new SaveCancelSection();
            saveCancelSection.getSaveButton().setButtonLabel(
                new GlobalizedMessage("ui.admin.importexport.export.start",
                                      AdminUiConstants.ADMIN_BUNDLE));
            add(saveCancelSection);
            addProcessListener(this);
        }

        @Override
        public boolean isVisible(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ImportExportMonitor monitor = cdiUtil
                .findBean(ImportExportMonitor.class);
            return !monitor.isLocked();
        }

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImportExportController controller = cdiUtil
                    .findBean(ImportExportController.class);
                controller.export(new ArrayList<>());
            }
        }

    }

    private class StatusLabel extends Label {

        public StatusLabel() {
            super(event -> {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImportExportMonitor monitor = cdiUtil
                    .findBean(ImportExportMonitor.class);
                final Label target = (Label) event.getTarget();
                if (monitor.isExportActive()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.export.status.export_active",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else if (monitor.isImportActive()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.export.status.import_active",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.export.status.locked",
                        AdminUiConstants.ADMIN_BUNDLE));
                }
            });
        }

        @Override
        public boolean isVisible(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ImportExportMonitor monitor = cdiUtil
                .findBean(ImportExportMonitor.class);
            return monitor.isLocked();
        }

    }

    private class ReportForm extends Form {

        public ReportForm() {
            
            super("exportReportForm", new BoxPanel(BoxPanel.VERTICAL));

            final Label title = new Label(event -> {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImportExportMonitor monitor = cdiUtil
                    .findBean(ImportExportMonitor.class);

                final Label target = (Label) event.getTarget();

                if (monitor.isExportActive()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.export.current_status",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else if (monitor.isExportReportAvailable()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.export.report",
                        AdminUiConstants.ADMIN_BUNDLE));
                }
            });
            add(title);

            final Text text = new Text(event -> {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImportExportMonitor monitor = cdiUtil
                    .findBean(ImportExportMonitor.class);
                final Text target = (Text) event.getTarget();
                target.setText(monitor.getReport().toString());
            });
            text.setClassAttr("preformatted-text");
            add(text);

            add(new Submit(new GlobalizedMessage(
                "ui.admin.importexport.report.update",
                AdminUiConstants.ADMIN_BUNDLE)));
        }

        @Override
        public boolean isVisible(final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ImportExportMonitor monitor = cdiUtil
                .findBean(ImportExportMonitor.class);

            return monitor.isExportActive()
                       || monitor.isExportReportAvailable();
        }

    }


}
