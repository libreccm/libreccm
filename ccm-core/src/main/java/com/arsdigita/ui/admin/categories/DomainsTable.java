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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainsTable extends Table {

    private final static Logger LOGGER = LogManager.getLogger(
        DomainsTable.class);

    private static final int COL_DOMAIN_KEY = 0;
    private static final int COL_DOMAIN_URI = 1;
    private static final int COL_DOMAIN_TITLE = 2;
    private static final int COL_DOMAIN_DEL = 3;

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedDomainId;
    private final TextField domainsFilter;

    public DomainsTable(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final TextField domainsFilter) {
        super();

        setIdAttr("domainsTable");

        this.categoriesTab = categoriesTab;
        this.selectedDomainId = selectedDomainId;
        this.domainsFilter = domainsFilter;

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.categories.domains.none",
            ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_DOMAIN_KEY,
            new Label(new GlobalizedMessage(
                "ui,admin.categories.domains.table.col_key",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DOMAIN_URI,
            new Label(new GlobalizedMessage(
                "ui,admin.categories.domains.table.col_uri",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DOMAIN_TITLE,
            new Label(new GlobalizedMessage(
                "ui,admin.categories.domains.table.col_title",
                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DOMAIN_DEL,
            new Label(new GlobalizedMessage(
                "ui,admin.categories.domains.table.col_del",
                ADMIN_BUNDLE))));

        columnModel.get(COL_DOMAIN_KEY).setCellRenderer(
            new TableCellRenderer() {

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

        columnModel.get(COL_DOMAIN_TITLE).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                final LocalizedString title = (LocalizedString) value;
                final GlobalizationHelper globalizationHelper = CdiUtil.
                    createCdiUtil().findBean(GlobalizationHelper.class);
                return new Text(title.getValue(globalizationHelper.
                    getNegotiatedLocale()));
            }

        });

        columnModel.get(COL_DOMAIN_DEL).setCellRenderer(
            new TableCellRenderer() {

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
                        "ui.admin.categories.domains.table.del_confirm",
                        ADMIN_BUNDLE));
                    return link;
                }
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();

                switch (event.getColumn()) {
                    case COL_DOMAIN_KEY:
                        selectedDomainId.setSelectedKey(state,
                                                        event.getRowKey());
                        categoriesTab.showDomainDetails(state);
                        break;
                    case COL_DOMAIN_DEL:
                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        setModelBuilder(new DomainsTableModelBuilder());
    }

    private class DomainsTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new DomainsTableModel(state);
        }

    }

    private class DomainsTableModel implements TableModel {

        private final List<Domain> domains;
        private int index = -1;

        public DomainsTableModel(final PageState state) {
            LOGGER.debug("Creating DomainsTableModel");
            final String filterTerm = (String) domainsFilter.getValue(state);
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final DomainRepository domainRepository = cdiUtil.findBean(
                DomainRepository.class);
            if (Strings.isBlank(filterTerm)) {
                domains = domainRepository.findAll("Domain.withOwners");
                LOGGER.debug("Found {} domains in the database.",
                             domains.size());
            } else {
                domains = domainRepository.search(filterTerm);
                LOGGER.debug("Found {} domain which match the "
                                 + "filter \"{}\".",
                             domains.size(),
                             filterTerm);
            }
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < domains.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            LOGGER.debug("Getting element for index {}, column {}...",
                         index,
                         columnIndex);
            final Domain domain = domains.get(index);
            switch (columnIndex) {
                case COL_DOMAIN_KEY:
                    return domain.getDomainKey();
                case COL_DOMAIN_URI:
                    return domain.getUri();
                case COL_DOMAIN_TITLE:
                    return domain.getTitle();
                case COL_DOMAIN_DEL:
                    if (isDeleteable(domain)) {
                        return new Label(new GlobalizedMessage(
                            "ui.admin.categories.domains.table.del",
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
            return domains.get(index).getObjectId();
        }

        private boolean isDeleteable(final Domain domain) {
            return !(domain.getOwners() != null && !domain.getOwners().isEmpty());
        }

    }

}
