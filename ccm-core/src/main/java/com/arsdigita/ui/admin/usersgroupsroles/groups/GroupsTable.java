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
package com.arsdigita.ui.admin.usersgroupsroles.groups;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
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
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GroupsTable extends Table {

    private static final Logger LOGGER = LogManager.getLogger(GroupsTable.class);

    private static final int COL_GROUP_NAME = 0;
    private static final int COL_DELETE = 1;

    private final TextField groupsTableFilter;
    private final ParameterSingleSelectionModel<String> selectedGroupId;

    public GroupsTable(
        final GroupAdmin parent,
        final TextField groupsTableFilter,
        final ParameterSingleSelectionModel<String> selectedGroupId) {
        super();

        setIdAttr("groupsTable");

        this.groupsTableFilter = groupsTableFilter;
        this.selectedGroupId = selectedGroupId;

        setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.groups.table.no_groups", ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_GROUP_NAME,
            new Label(new GlobalizedMessage("ui.admin.groups.table.name",
                                            ADMIN_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DELETE,
            new Label(new GlobalizedMessage("ui.admin.groups.table.delete",
                                            ADMIN_BUNDLE))));

        columnModel.get(COL_GROUP_NAME).setCellRenderer(
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

        columnModel.get(COL_DELETE).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                final ControlLink link = new ControlLink((Component) value);
                link.setConfirmation(new GlobalizedMessage(
                    "ui.admin.group.delete.confirm", ADMIN_BUNDLE));
                return link;
            }

        });

        addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                final String key = (String) event.getRowKey();

                switch (event.getColumn()) {
                    case COL_GROUP_NAME:
                        selectedGroupId.setSelectedKey(state, key);
                        parent.showGroupDetails(state);
                        break;
                    case COL_DELETE:
                        final GroupRepository groupRepository = CdiUtil
                            .createCdiUtil().findBean(GroupRepository.class);
                        final Group group = groupRepository.findById(
                            Long.parseLong(key));
                        groupRepository.delete(group);
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

        setModelBuilder(new GroupsTableModelBuilder());
    }

    private class GroupsTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new GroupsTableModel(state);
        }

    }

    private class GroupsTableModel implements TableModel {

        private final List<Group> groups;
        private int index = -1;

        public GroupsTableModel(final PageState state) {
            LOGGER.debug("Creating GroupsTableModel");
            final String filterTerm = (String) groupsTableFilter.getValue(state);
            LOGGER.debug("Value of filter is: \"{}\"", filterTerm);
            final GroupRepository groupRepository = CdiUtil.createCdiUtil().
                findBean(GroupRepository.class);
            if (filterTerm == null || filterTerm.isEmpty()) {
                groups = groupRepository.findAllOrderedByGroupName();
                LOGGER.debug("Found {} groups in database.", groups.size());
            } else {
                groups = groupRepository.searchGroupByName(filterTerm);
                LOGGER.debug("Found {} groups in database which match the "
                                 + "filter \"{}\".",
                             groups.size(),
                             filterTerm);
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            index++;
            LOGGER.debug("Next row called. Index is now {}", index);
            return index < groups.size();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            LOGGER.debug("Getting element for row {}, column {}...",
                         index,
                         columnIndex);
            final Group group = groups.get(index);
            switch (columnIndex) {
                case COL_GROUP_NAME:
                    return group.getName();
                case COL_DELETE:
                    return new Label(new GlobalizedMessage(
                        "ui.admin.groups.table.delete", ADMIN_BUNDLE));
                default:
                    throw new IllegalArgumentException(
                        "Not a valid column index");
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            LOGGER.debug("Getting key for row {}, column {}...",
                         index,
                         columnIndex);
            return groups.get(index).getPartyId();
        }

    }

}
