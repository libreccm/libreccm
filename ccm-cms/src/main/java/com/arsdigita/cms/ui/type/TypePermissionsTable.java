package com.arsdigita.cms.ui.type;

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
import org.librecms.contentsection.ContentType;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.ContentSectionRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.GlobalizationUtil;
import org.libreccm.security.Party;
import org.libreccm.security.Role;
import com.arsdigita.util.LockableImpl;
import java.awt.image.Kernel;
import java.util.List;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Permission;
import org.libreccm.security.RoleRepository;
import org.librecms.CmsConstants;
import sun.security.util.SecurityConstants;

/**
 *
 * ToDo, maybe not needed anymore...
 * 
 * @author Jens Pelzetter
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

        setEmptyView(new Label(new GlobalizedMessage(
                "cms.ui.type.permissions.none",
                CmsConstants.CMS_BUNDLE)));

        TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0,
                new GlobalizedMessage("cms.ui.type.permissions.role",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_ROLE));

        columnModel.add(new TableColumn(
                1,
                new GlobalizedMessage("cms.ui.type.permissions_can_use",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_CAN_USE));

        columnModel.add(new TableColumn(
                2,
                new GlobalizedMessage("cms.ui.type.permission.action",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_ACTION));

        setModelBuilder(new TypePermissionsTableModelBuilder());

        columnModel.get(0).setCellRenderer(new RoleCellRenderer());
        columnModel.get(1).setCellRenderer(new CanUseCellRenderer());
        columnModel.get(2).setCellRenderer(new ActionCellRenderer());

        addTableActionListener(this);
    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        final TableColumn column = getColumnModel().get(event.getColumn());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepo = cdiUtil.findBean(RoleRepository.class);

        if (TABLE_COL_ACTION.equals(column.getHeaderKey().toString())) {
            final Role role = roleRepo.findById(Long.parseLong(
                    event.getRowKey().toString()));
            ContentType contentType = getType().getContentType(state);
//            ToDo
//            ObjectPermissionCollection permissions
//                                       = PermissionService.
//                            getDirectGrantedPermissions(contentType.getOID());
//
//            if ((permissions.size() == 0)) {
//                role.grantPermission(contentType,
//                                     PrivilegeDescriptor.get(
//                                             com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
//            } else if (!role.checkPermission(contentType, PrivilegeDescriptor.
//                                             get(com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
//                role.grantPermission(contentType,
//                                     PrivilegeDescriptor.get(
//                                             com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
//            } else {
//                role.revokePermission(contentType,
//                                      PrivilegeDescriptor.get(
//                                              com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM));
//            }
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }

    private class TypePermissionsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public TypePermissionsTableModelBuilder() {
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new TypePermissionsTableModel(table, state);
        }
    }

    private class TypePermissionsTableModel implements TableModel {

        private Table table;
        private List<Role> roles;
        private ContentType contentType;
        private List<Permission> permissions;

        public TypePermissionsTableModel(final Table table,
                                         final PageState state) {
            this.table = table;
            contentType
            = ((TypePermissionsTable) table).getType().getContentType(
                            state);

            roles = CMS.getContext().getContentSection().getRoles();

//            permissions
//            = PermissionService.
//                            getDirectGrantedPermissions(contentType.getOID());
        }

        @Override
        public int getColumnCount() {
            if (roles == null) {
                return 0;
            } else {
                return (int) roles.size();
            }
        }

        @Override
        public boolean nextRow() {
            return false;
//            if (roles == null) {
//                return false;
//            } else {
//                return roles.next();
//            }
        }

        @Override
        public Object getElementAt(int columnIndex) {
//            switch (columnIndex) {
//                case 0:
//                    return roles.getRole().getName();
//                case 1:
//                    if (permissions.isEmpty()) {
//                        return "cms.ui.type.permissions.can_use.yes";
//                    } else {
//                        if (roles.getRole().checkPermission(contentType,
//                                                            PrivilegeDescriptor.
//                                                                    get(
//                                                                            com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
//                            return "cms.ui.type.permissions.can_use.yes";
//                        } else {
//                            return "cms.ui.type.permissions.can_use.no";
//                        }
//                    }
//                case 2:
//                    if (permissions.size() == 0) {
//                        return "cms.ui.type.permissions.actions.restrict_to_this_role";
//                    } else {
//                        if (roles.getRole().checkPermission(contentType,
//                                                            PrivilegeDescriptor.
//                                                                    get(
//                                                                            com.arsdigita.cms.SecurityManager.CMS_NEW_ITEM))) {
//                            return "cms.ui.type.permissions.actions.revoke";
//                        } else {
//                            return "cms.ui.type.permissions.can_use.grant";
//                        }
//                    }
//                default:
//                    return null;
//            }
            return null;
        }

        @Override
        public Object getKeyAt(int columnIndex) {
//            return roles.getRole().getID();
            return null;
        }
    }

    private class RoleCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
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

        @Override
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
//            com.arsdigita.cms.SecurityManager securityManager = Utilities.
//                    getSecurityManager(state);
//            Party party = Kernel.getContext().getParty();
//            if (party == null) {
//                party = Kernel.getPublicUser();
//            }
//            if (securityManager.canAccess(party,
//                                          SecurityConstants.CONTENT_TYPE_ADMIN)) {
//                ControlLink link = new ControlLink((String) GlobalizationUtil.
//                        globalize(
//                                value.toString()).localize());
//
//                return link;
//            } else {
                return new Label(value.toString());
//            }

        }
    }

    private ContentTypeRequestLocal getType() {
        return type;
    }
}
