/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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
 */
package com.arsdigita.cms.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ui.report.ContentSectionSummaryTable;
import com.arsdigita.cms.ui.report.Report;
import com.arsdigita.cms.ui.report.ReportListModel;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.util.LockableImpl;

/**
 * A pane that shows selectable reports and their results. A selectable list of
 * reports is shown on the left-hand side, a selected report is shown as body.
 *
 * @author
 * <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 * @author
 * <a href="https://sourceforge.net/users/tim-permeance/">tim-permeance</a>
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ReportPane extends BaseAdminPane<String> {

    private final SingleSelectionModel<String> selectionModel;
    private final java.util.List<Report> availableReports;

    public ReportPane() {
        availableReports = getReports();

        selectionModel = new ParameterSingleSelectionModel<>(
            new StringParameter(List.SELECTED));
        selectionModel.addChangeListener(new SelectionListener());
        setSelectionModel(selectionModel);

        List m_reports = new List(new ReportListModelBuilder(availableReports));
        m_reports.setSelectionModel(selectionModel);

        final ReportsListSection reportsListSection = new ReportsListSection(
            m_reports);
        setLeft(reportsListSection);

        // Register the actual components of the reports for later usage
        for (Report report : availableReports) {
            getBody().add(report.getComponent());
        }

        setIntroPane(new Label(gz("cms.ui.reports.intro")));
    }

    /**
     * @return List of available reports.
     */
    private java.util.List<Report> getReports() {
        java.util.List<Report> reports = new ArrayList<>();
        reports.add(new Report("cms.ui.reports.css.reportName",
                               new ContentSectionSummaryTable()));
        // Add other reports as required

        Collections.sort(
            reports,
            (r1, r2) -> r1.getName().compareTo(r2.getName()));

        return reports;
    }

    /**
     * Get the report model that matches the given key.
     *
     * @param key Key to match.
     *
     * @return Report model that matches that given key, null if no matching
     *         report was found.
     */
    private Report getReportByKey(final String key) {
        for (Report report : availableReports) {
            if (report.getKey().equals(key)) {
                return report;
            }
        }
        return null;
    }

    /**
     * UI section for left-hand list of reports.
     */
    private class ReportsListSection extends Section {

        ReportsListSection(final List reports) {
            setHeading(gz("cms.ui.reports.header"));
            ActionGroup group = new ActionGroup();
            setBody(group);
            group.setSubject(reports);
        }

    }

    /**
     * SelectionListener for selected report. It shows the selected report in
     * the body of this component.
     */
    private class SelectionListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {

            final PageState state = event.getPageState();

            getBody().reset(state);

            if (selectionModel.isSelected(state)) {
                Report selectedReport = getReportByKey(selectionModel
                    .getSelectedKey(state).toString());
                if (selectedReport != null) {
                    getBody().push(state, selectedReport.getComponent());
                }
            }
        }

    }

    /**
     * ListModelBuilder creating a ReportListModel for a list of reports.
     */
    private static class ReportListModelBuilder
        extends LockableImpl
        implements ListModelBuilder {

        private final java.util.List<Report> reports;

        private ReportListModelBuilder(final java.util.List<Report> reports) {
            this.reports = reports;
        }

        @Override
        public final ListModel makeModel(final List list,
                                         final PageState state) {
            return new ReportListModel(reports);
        }

    }

}
