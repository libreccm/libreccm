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
package com.arsdigita.ui.admin.sites;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.Iterator;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SitesTable extends Table {

    public static final int COL_SITE_DOMAIN = 0;
    public static final int COL_IS_DEFAULT_SITE = 1;
    public static final int COL_DEFAULT_THEME = 2;
    public static final int COL_APPLICATIONS = 3;
    public static final int COL_REMOVE = 4;

    public SitesTable(
        final SitesTab parent,
        final ParameterSingleSelectionModel<String> selectedSiteId) {

        super();

        super.setIdAttr("sitesTable");
        super.setStyleAttr("width: 30em");

        setEmptyView(new Label(new GlobalizedMessage("ui.admin.sites.no_sites",
                                                     ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_SITE_DOMAIN,
            new Label(new GlobalizedMessage(
                "ui.admin.sites.table.columns.domain.header"))));
        columnModel.add(new TableColumn(
            COL_IS_DEFAULT_SITE,
            new Label(new GlobalizedMessage(
                "ui.admin.sites.table.columns.default_site.header"))));
        columnModel.add(new TableColumn(
            COL_DEFAULT_THEME,
            new Label(new GlobalizedMessage(
                "ui.admin.sites.table.columns.default_theme.header"))));
        columnModel.add(new TableColumn(
            COL_APPLICATIONS,
            new Label(new GlobalizedMessage(
                "ui.admin.sites.table.columns.applications.header"))));
        columnModel.add(new TableColumn(
            COL_APPLICATIONS,
            new Label(new GlobalizedMessage(
                "ui.admin.sites.table.columns.delete.header"))));

        columnModel
            .get(COL_SITE_DOMAIN)
            .setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {

                    return new ControlLink((String) value);
                }

            });

        columnModel.get(COL_REMOVE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                if (value == null) {
                    return new Text("");
                } else {
                    final ControlLink link = new ControlLink((Component) value);
                    link.setConfirmation(new GlobalizedMessage(
                        "ui.admin.sites.delete.confirm", ADMIN_BUNDLE));
                    return link;
                }
            }

        });

        super.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_SITE_DOMAIN:
                        selectedSiteId.setSelectedKey(state, key);
                        parent.showSiteForm(state);
                        break;
                    case COL_REMOVE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final SitesController controller = cdiUtil
                            .findBean(SitesController.class);
                        controller.deleteSite(Long.parseLong(key));
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Invalid value for column.");
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {

                //Nothing
            }

        });

        super.setModelBuilder(new SitesTableModelBuilder());
    }

    private class SitesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final SitesController controller = cdiUtil
                .findBean(SitesController.class);
            return new SitesTableModel(controller.findSites());
        }

    }

    private class SitesTableModel implements TableModel {

        private final Iterator<SitesTableRow> iterator;
        private SitesTableRow currentRow;

        public SitesTableModel(final List<SitesTableRow> rows) {
            iterator = rows.iterator();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public boolean nextRow() {
            if (iterator.hasNext()) {
                currentRow = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case COL_SITE_DOMAIN:
                    return currentRow.getDomainOfSite();
                case COL_IS_DEFAULT_SITE:
                    return currentRow.isDefaultSite();
                case COL_DEFAULT_THEME:
                    return currentRow.getDefaultTheme();
                case COL_APPLICATIONS:
                    final String apps = String
                        .join(",\n",
                              currentRow
                                  .getApplications()
                                  .toArray(new String[]{}));
                    return new Label(apps, false);
                case COL_REMOVE:
                    if (currentRow.isDeletable()) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.sites.table.columns.remove.label",
                            ADMIN_BUNDLE));
                    } else {
                        return null;
                    }
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return currentRow.getSiteId();
        }

    }

}
