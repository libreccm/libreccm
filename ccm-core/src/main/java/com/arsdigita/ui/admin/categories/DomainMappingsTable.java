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
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.ArrayList;
import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainMappingsTable extends Table {

    private static final int COL_APPLICATION = 0;
    private static final int COL_REMOVE = 1;

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedDomainId;

    public DomainMappingsTable(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId) {
        super();

        this.categoriesTab = categoriesTab;
        this.selectedDomainId = selectedDomainId;

        setIdAttr("domainMappingsTable");

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.categories.domain_details.mappings.none",
            ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_APPLICATION,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.mappings.col_app",
                ADMIN_BUNDLE))));
//        columnModel.add(new TableColumn(
//            COL_CONTEXT,
//            new Label(new GlobalizedMessage(
//                "ui.admin.categories.domain_details.mappings.col_context",
//                ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_REMOVE,
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.mappings.col_remove",
                ADMIN_BUNDLE))));

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
                        "ui.admin.categories.domain_details.mappings.remove.confirm",
                        ADMIN_BUNDLE));
                    return link;
                }
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                switch (event.getColumn()) {
                    case COL_REMOVE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final DomainRepository domainRepository = cdiUtil
                            .findBean(DomainRepository.class);
                        final DomainManager domainManager = cdiUtil.findBean(
                            DomainManager.class);
                        final ApplicationRepository appRepository = cdiUtil
                            .findBean(ApplicationRepository.class);

                        final Domain domain = domainRepository.findById(
                            Long.parseLong(selectedDomainId.getSelectedKey(event
                                .getPageState()))).get();

                        final CcmApplication owner = appRepository.findById(
                            Long.parseLong((String)event.getRowKey())).get();
                        
                        domainManager.removeDomainOwner(owner, domain);

                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });
        
        setModelBuilder(new DomainMappingsTableModelBuilder());
    }
    
    private class DomainMappingsTableModelBuilder
        extends LockableImpl 
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            
            return new DomainMappingsTableModel(state);
        }
        
    }
    
    private class DomainMappingsTableModel implements TableModel {

        private final List<DomainOwnership> domainOwnerships;
        private int index = -1;
        
        public DomainMappingsTableModel(final PageState state) {
            final DomainRepository domainRepository = CdiUtil.createCdiUtil()
                .findBean(DomainRepository.class);
            final Domain domain = domainRepository.findById(
                Long.parseLong(selectedDomainId.getSelectedKey(state)),
                "Domain.withOwners").get();
            
            domainOwnerships = new ArrayList<>(domain.getOwners());
            
            domainOwnerships.sort((o1, o2) -> {
                return ((Long)o1.getOwnerOrder()).compareTo(o2.getOwnerOrder());
            });
        }
        
        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < domainOwnerships.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch(columnIndex) {
                case COL_APPLICATION:
                    return domainOwnerships.get(index).getOwner().getPrimaryUrl();
                case COL_REMOVE:
                    return new Label(new GlobalizedMessage(
                    "ui.admin.categories.domain_details.mappings.remove",
                    ADMIN_BUNDLE));
                default:
                     throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return domainOwnerships.get(index).getOwner().getObjectId();
        }
        
    }
}
