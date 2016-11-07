package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.Column;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Role;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.kernel.permissions.ObjectPermissionCollection;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class TypePermissionsTable extends Table implements TableActionListener {

    private final String TABLE_COL_ROLE = "table_col_role";
    private final String TABLE_COL_CAN_USE = "table_col_can_use";
    private final String TABLE_COL_ACTION = "table_col_action";
    private final ContentTypeRequestLocal type;

    public TypePermissionsTable(final ContentSectionRequestLocal section,
                                final ContentTypeRequestLocal type) {
        super();

        this.type = type;

        setEmptyView(new Label(GlobalizationUtil.globalize(
                "cms.ui.type.permissions.none")));

        TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0,
                GlobalizationUtil.globalize("cms.ui.type.permissions.role").
                localize(),
                TABLE_COL_ROLE));

        columnModel.add(new TableColumn(
                1,
                GlobalizationUtil.globalize("cms.ui.type.permissions_can_use").
                localize(),
                TABLE_COL_CAN_USE));

        columnModel.add(new TableColumn(
                2,
                GlobalizationUtil.globalize(
                "cms.ui.type.permission.action").localize(),
                TABLE_COL_ACTION));

        setModelBuilder(new TypePermissionsTableModelBuilder());

        columnModel.get(0).setCellRenderer(new RoleCellRenderer());
        columnModel.get(1).setCellRenderer(new CanUseCellRenderer());
        columnModel.get(2).setCellRenderer(new ActionCellRenderer());

        addTableActionListener(this);
    }

    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_ACTION.equals(column.getHeaderKey().toString())) {
            Role role = new Role(new BigDecimal(event.getRowKey().toString()));
            ContentType contentType = getType().getContentType(state);
            ObjectPermissionCollection permissions =
                                       PermissionService.
                    getDirectGrantedPermissions(contentType.getOID());
       
            if ((permissions.size() == 0)) {
                role.grantPermission(contentType,
                                     PrivilegeDescriptor.get(
                        com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
            } else if (!role.checkPermission(contentType, PrivilegeDescriptor.
                    get(com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
                role.grantPermission(contentType,
                                     PrivilegeDescriptor.get(
                        com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
            } else {
                role.revokePermission(contentType,
                                      PrivilegeDescriptor.get(
                        com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
            }
        }
    }

    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }

    private class TypePermissionsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public TypePermissionsTableModelBuilder() {
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new TypePermissionsTableModel(table, state);
        }
    }

    private class TypePermissionsTableModel implements TableModel {

        private Table table;
        private RoleCollection roles;
        private ContentType contentType;
        private ObjectPermissionCollection permissions;

        public TypePermissionsTableModel(Table table, PageState state) {
            this.table = table;
            contentType =
            ((TypePermissionsTable) table).getType().getContentType(
                    state);

            roles = CMS.getContext().getContentSection().getStaffGroup().
                    getRoles();

            permissions =
            PermissionService.getDirectGrantedPermissions(contentType.getOID());
        }

        public int getColumnCount() {
            if (roles == null) {
                return 0;
            } else {
                return (int) roles.size();
            }
        }

        public boolean nextRow() {
            if (roles == null) {
                return false;
            } else {
                return roles.next();
            }
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return roles.getRole().getName();
                case 1:
                    if (permissions.size() == 0) {
                        return "cms.ui.type.permissions.can_use.yes";
                    } else {                    
                        if (roles.getRole().checkPermission(contentType,
                                                            PrivilegeDescriptor.
                                get(
                                com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
                            return "cms.ui.type.permissions.can_use.yes";
                        } else {
                            return "cms.ui.type.permissions.can_use.no";
                        }
                    }
                case 2:
                    if (permissions.size() == 0) {
                        return "cms.ui.type.permissions.actions.restrict_to_this_role";
                    } else {
                        if (roles.getRole().checkPermission(contentType,
                                                            PrivilegeDescriptor.
                                get(
                                com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
                            return "cms.ui.type.permissions.actions.revoke";
                        } else {
                            return "cms.ui.type.permissions.can_use.grant";
                        }
                    }
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return roles.getRole().getID();
        }
    }

    private class RoleCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            return new Label(value.toString());
        }
    }

    private class CanUseCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            return new Label(GlobalizationUtil.globalize(value.toString()));
        }
    }

    private class ActionCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            Party party = Kernel.getContext().getParty();
            if (party == null) {
                party = Kernel.getPublicUser();
            }
            if (securityManager.canAccess(party,
                                          SecurityConstants.CONTENT_TYPE_ADMIN)) {
                ControlLink link = new ControlLink((String)GlobalizationUtil.globalize(
                        value.toString()).localize());

                return link;
            } else {
                return new Label(value.toString());
            }


        }
    }

    private ContentTypeRequestLocal getType() {
        return type;
    }
}
