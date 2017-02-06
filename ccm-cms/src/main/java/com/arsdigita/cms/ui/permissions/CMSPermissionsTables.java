/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.librecms.CmsConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.arsdigita.cms.ui.permissions.CMSPermissionsConstants.*;

/**
 * Class to represent direct and inherited permissions of an CcmObject. This
 * class provides two SegmentPanels with the direct and the inherited
 * permissions tables, respectively. The expected pageState contains a variable
 * "id=123" of which the permissions are rendered. The viewing user must be
 * authenticated. The permissions representations can be swapped , e.g. with a
 * List, if scalability warrants.
 *
 * @author Stefan Deusch
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsTables {

    private static Logger LOGGER = LogManager.getLogger(
        CMSPermissionsTables.class);

    private CMSPermissionsPane parent;
    private String[] privileges;
    private final GridPanel permissionsPanel[] = new GridPanel[2];
    private final int[] tableColumns = new int[2];

    /**
     * Default constructor uses the DEFAULT_PRIVILEGES as defined in
     * PermissionsConstants.
     */
    CMSPermissionsTables(final CMSPermissionsPane parent) {
        this(DEFAULT_PRIVILEGES, parent);
    }

    /**
     * Constructor that takes an array of PrivilegeDescriptors and builds the
     * grantee - privilege matrix. <strong>The permissions tables contain the
     * set of privileges that are passed into this constructor.</strong>
     *
     * @param privileges the array of PrivilegeDesrciptors with which go into
     *                   table
     * @param parent     the Bebop parent container
     */
    CMSPermissionsTables(final String[] privileges,
                         final CMSPermissionsPane parent) {

        this.parent = parent;

        // fixed table information
        this.privileges = privileges;
        tableColumns[DIRECT] = privileges.length + 2;
        tableColumns[INHERITED] = privileges.length + 1;

        // Construct Direct Permissions Panel
        permissionsPanel[DIRECT] = new GridPanel(1);
        final Table directTable = new Table(
            new PermissionsTableModelBuilder(DIRECT),
            getHeaders(DIRECT));
        directTable.setClassAttr("dataTable");
        setCellRenderers(directTable, DIRECT);
        directTable.addTableActionListener(
            new DirectPermissionsTableActionListener());
        permissionsPanel[DIRECT].add(new Label(new GlobalizedMessage(
            "cms.ui.permissions.these_are_the_custom_permissions"
                + "_that_have_been_granted_on_this_object",
            CmsConstants.CMS_BUNDLE)));
        permissionsPanel[DIRECT].add(directTable);

        // Construct Inherited Permissions Panel
        permissionsPanel[INHERITED] = new GridPanel(1);
        final Table inheritedTable = new Table(
            new PermissionsTableModelBuilder(INHERITED),
            getHeaders(INHERITED));
        inheritedTable.setClassAttr("dataTable");
        setCellRenderers(inheritedTable, INHERITED);
        permissionsPanel[INHERITED].add(new Label(new GlobalizedMessage(
            "cms.ui.permissions.these_are_the_current_permissions_for_this_folder",
            CmsConstants.CMS_BUNDLE)));
        permissionsPanel[INHERITED].add(inheritedTable);

        //m_permPanel[INHERITED].addSegment(new Label(PERM_TABLE_INDIRECT_HEADING),
        //                                 boxpanel);
    }

    /**
     * Returns the SegmentedPanel with either the direct or the indirect
     * permissions table.
     *
     * @param use PermissionsContants.DIRECT or PermissionsContants.INHERITED
     */
    GridPanel getPermissions(int type) {
        return permissionsPanel[type];
    }

    /**
     * Returns the set of privileges of the permission tables as a String array.
     */
    String[] getPrivileges() {
        return Arrays.copyOf(privileges, privileges.length);
    }

    SimpleComponent makeContextPanel() {
        final SimpleContainer contextPanel = new SimpleContainer();
        final Label contextLabel1 = new Label();
        contextLabel1.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label label = (Label) event.getTarget();
                final CcmObject context = getContext(state);
                if (context != null) {
                    label.setLabel(PERM_TABLE_INDIRECT_CONTEXT);
                    contextLabel1.setVisible(state, true);
                } else {
                    label.setLabel(PERM_TABLE_NO_PARENT_CONTEXT);
                    contextLabel1.setVisible(state, false);
                }
            }

        });

        final Label contextLabel2 = new Label();
        contextLabel2.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label label = (Label) event.getTarget();
                final CcmObject context = getContext(state);
                if (context != null) {
                    label.setLabel(context.getDisplayName());
                }
            }

        });
        contextLabel2.setFontWeight(Label.BOLD);

        contextPanel.add(contextLabel1);
        contextPanel.add(contextLabel2);
        return contextPanel;
    }

    CcmObject getContext(final PageState state) {

        return null;
    }

    private String[] getHeaders(final int type) {
        String[] headers = new String[tableColumns[type]];
        headers[0] = PERM_TABLE_GRANTEE.localize() + "";
        for (int j = 0; j < privileges.length; j++) {
            headers[j + 1] = parent.getPrivilegeName(privileges[j]);
        }
        if (type == DIRECT) {
            headers[privileges.length + 1] = PERM_TABLE_ACTIONS.localize()
                                                 + "";
        }
        return headers;
    }

    private void setCellRenderers(final Table table, final int type) {
        int j;
        if (type == DIRECT) {
            for (j = 1; j < table.getColumnModel().size() - 1; j++) {
                table.getColumn(j).setCellRenderer(
                    new PermissionToggleRenderer());
            }
            table.getColumn(j).setCellRenderer(new LinkRenderer());
        } else {
            for (j = 1; j < table.getColumnModel().size(); j++) {
                table.getColumn(j).setCellRenderer(
                    new PermissionStatusRenderer());
            }
        }
    }

    private class DirectPermissionsTableActionListener
        implements TableActionListener {

        @Override
        public void cellSelected(TableActionEvent event) {

            final PageState state = event.getPageState();
            final int col = event.getColumn();
            final String rowkey = (String) event.getRowKey();
            if (rowkey == null) {
                return;
            }

            final Table table = (Table) event.getSource();
            final int no_cols = table.getColumnModel().size();
            final int lastCol = no_cols - 1;

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionManager permissionManager = cdiUtil.findBean(
                PermissionManager.class);

            if (col > 0 && col < lastCol) {

                final PermissionStatus pmds = UserPrivilegeKey
                    .undescribe(rowkey);
                LOGGER.debug("Do perm toggle on {} for {} of {}",
                             pmds.getObject().getObjectId(),
                             pmds.getRole().getName(),
                             pmds.getPrivilege());

                if (pmds.isGranted()) {

                    LOGGER.debug("Do revoke permission");

                    permissionManager.revokePrivilege(pmds.getPrivilege(),
                                                      pmds.getRole(),
                                                      pmds.getObject());
                } else {
                    LOGGER.debug("Do grant permission");

                    permissionManager.grantPrivilege(pmds.getPrivilege(),
                                                     pmds.getRole(),
                                                     pmds.getObject());
                }

            } else if (col == lastCol) {
                // Process Remove All Link
                final String[] tokens = StringUtils.split(rowkey, '.');
                final Long pID = Long.parseLong(tokens[0]);

                /*
                 * Remove all indicated privileges from user
                 * enumerated in tokens array
                 */
                final CcmObject obj = parent.getObject(state);
                final Role role = CMSUserObjectStruct.loadRole(pID);

                LOGGER.debug("Revoke all on {} for {}.",
                             obj.getObjectId(),
                             role.getName());

                for (final String token : tokens) {
                    permissionManager.revokePrivilege(token, role, obj);
                }
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            throw new UnsupportedOperationException();
        }

    }

    private final class PermissionsTableModelBuilder
        extends LockableImpl implements TableModelBuilder {

        private final int m_type;

        PermissionsTableModelBuilder(final int type) {
            m_type = type;
        }

        /*
         * this can be optimized to run the query only
         * once for both tables
         */
        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            final CcmObject object = parent.getObject(state);

            switch (m_type) {
                case DIRECT:
                    return new DirectPermissionsTableModel(object);
                case INHERITED:
                    return new DirectPermissionsTableModel(
                        object);
                default:
                    return null;
            }
        }

    }

    private class DirectPermissionsTableModel implements TableModel {

        private final List<String> userPrivileges = new ArrayList<>();

        private Iterator<Permission> iterator;
        private Permission currentPermission;

        public DirectPermissionsTableModel(final CcmObject object) {
//            this.iterator = permissions.iterator();
        }

        @Override
        public int getColumnCount() {
            return tableColumns[DIRECT];
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            if (columnIndex == 0) {

                // the Grantee column
                return currentPermission.getGrantee().getName();

            } else if (columnIndex == getColumnCount() - 1) {

                // the Action column
                return "Remove All";

            } else {
                if (userHasPermission(columnIndex - 1)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            if (columnIndex == 0) {

                // the key for the grantee
                return currentPermission.getGrantee().getRoleId();

            } else if (columnIndex == getColumnCount() - 1) {

                // key for 'Remove All' link
                return makeRemoveAllKey();

            } else {
                // key for a user privilege
                return (new UserPrivilegeKey(
                        currentPermission.getObject().getObjectId(),
                        currentPermission.getGrantee().getRoleId(),
                        privileges[columnIndex - 1],
                        userHasPermission(columnIndex - 1)))
                    .toString();
            }
        }

        @Override
        public boolean nextRow() {
            if (iterator.hasNext()) {
                currentPermission = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        boolean userHasPermission(final int idx) {
            return userPrivileges.contains(privileges[idx]);
        }

        private String makeRemoveAllKey() {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(Long.toString(currentPermission.getGrantee()
                .getRoleId()));
            for (int i = 0; i < privileges.length; i++) {
                if (userHasPermission(i)) {
                    buffer.append(".").append(privileges[i]);
                }
            }
            return buffer.toString();
        }

        void addPrivilege(final String privilege) {
            for (String current : privileges) {
                if (current.equals(current)) {
                    userPrivileges.add(current);
                    break;
                }
            }
        }

        protected Permission getCurrentPermission() {
            return currentPermission;
        }

    }

    /**
     * Extension of DirectPermissionsTableModel to accomodate Inherited
     * permissions table model.
     */
    private final class InheritedPermissionsTableModel
        extends DirectPermissionsTableModel {

        public InheritedPermissionsTableModel(final CcmObject object) {
            super(object);
        }

        @Override
        public int getColumnCount() {
            return tableColumns[INHERITED];
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            if (columnIndex == 0) {

                // the Grantee column
                return getCurrentPermission().getGrantee().getName();

            } else {
                if (userHasPermission(columnIndex - 1)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            if (columnIndex == 0) {
                // the key for the grantee
                return getCurrentPermission().getGrantee().getRoleId();

            }
            // no keys for inherited permissions
            return null;
        }

    }

    private final class PermissionToggleRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final ControlLink link = new ControlLink("");

            if (((Boolean) value)) {
                link.setClassAttr("checkBoxChecked");
            } else {
                link.setClassAttr("checkBoxUnchecked");
            }

            return link;
        }

    }

    private final class PermissionStatusRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final Label link = new Label();

            if (((Boolean) value)) {
                link.setClassAttr("checkBoxGreyChecked");
            } else {
                link.setClassAttr("checkBoxGreyUnchecked");
            }

            return link;
        }

    }

    private final class LinkRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ControlLink link = new ControlLink((String) value);
            link.setConfirmation(REMOVE_ALL_CONFIRM);
            return link;
        }

    }

}
