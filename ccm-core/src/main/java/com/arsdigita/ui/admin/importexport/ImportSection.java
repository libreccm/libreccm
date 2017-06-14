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
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;

import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImportSection extends BoxPanel {

    public ImportSection() {
        super(BoxPanel.VERTICAL);

        add(new ImportForm());
        add(new StatusLabel());
        add(new ReportForm());
    }

    private class ImportForm extends Form implements FormProcessListener {

        private static final String SELECTED_IMPORTS_PARAM
                                        = "selectedImportsParam";

        private final CheckboxGroup importsSelector;
        private final SaveCancelSection saveCancelSection;

        public ImportForm() {
            super("importForm", new BoxPanel(BoxPanel.VERTICAL));

            add(new Label(new GlobalizedMessage(
                "ui.admin.importexport.import.select_files_to_import",
                AdminUiConstants.ADMIN_BUNDLE)));
            importsSelector = new CheckboxGroup(SELECTED_IMPORTS_PARAM);
            try {
                importsSelector.addPrintListener(event -> {
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final ImportExportController controller = cdiUtil
                        .findBean(ImportExportController.class);
                    final CheckboxGroup target = (CheckboxGroup) event
                        .getTarget();
                    final List<String> importFiles = controller
                        .getAvailableImportFiles();
                    importFiles.forEach(file -> {
                        target.addOption(new Option(file, new Text(file)));
                    });
                });
            } catch (TooManyListenersException ex) {
                throw new UnexpectedErrorException(ex);
            }
            add(importsSelector);

            saveCancelSection = new SaveCancelSection();
            saveCancelSection
                .getSaveButton()
                .setButtonLabel(new GlobalizedMessage(
                    "ui.admin.importexport.import.execute",
                    AdminUiConstants.ADMIN_BUNDLE));
            add(saveCancelSection);
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

                final String[] selectedFiles = (String[]) importsSelector
                    .getValue(state);
                controller.importFiles(Arrays.asList(selectedFiles));
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
                        "ui.admin.importexport.import.status.export_active",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else if (monitor.isImportActive()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.import.status.import_active",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.import.status.locked",
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

            super("importReportForm", new BoxPanel(BoxPanel.VERTICAL));

            final Label title = new Label(event -> {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImportExportMonitor monitor = cdiUtil
                    .findBean(ImportExportMonitor.class);

                final Label target = (Label) event.getTarget();

                if (monitor.isExportActive()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.import.current_status",
                        AdminUiConstants.ADMIN_BUNDLE));
                } else if (monitor.isExportReportAvailable()) {
                    target.setLabel(new GlobalizedMessage(
                        "ui.admin.importexport.import.report",
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

            return monitor.isImportActive()
                       || monitor.isImportReportAvailable();
        }

    }

}
