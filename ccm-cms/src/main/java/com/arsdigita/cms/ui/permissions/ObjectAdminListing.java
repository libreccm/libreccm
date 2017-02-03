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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ObjectAdminListing extends SimpleContainer {

    private final Table adminTable;
    private ActionLink addUserLink;
    private final CcmObjectSelectionModel<CcmObject> objectSelectionModel;
    private ObjectAddSearchAdmin objectAddSearchAdmin;

    public ObjectAdminListing(
        final CcmObjectSelectionModel<CcmObject> objectSelectionModel) {

        super("cms:roleAdmin", CMS.CMS_XML_NS);

        this.objectSelectionModel = objectSelectionModel;

        adminTable = new Table(getTableModelBuilder(objectSelectionModel),
                               new String[]{"Member", "Action"});
        adminTable.setDefaultCellRenderer(new ObjectAdminTableRenderer());
        adminTable.setEmptyView(new Label(
            "There are no administrators for this object"));
        adminTable.setClassAttr("dataTable");
        adminTable.addTableActionListener(new ObjectAdminActionListener());

        addUserLink = new ActionLink(new GlobalizedMessage(
            "cms.ui.permissions.add_administrator", CmsConstants.CMS_BUNDLE));
        addUserLink.setClassAttr("actionLink");

        objectAddSearchAdmin = getObjectAddSearchAdmin(objectSelectionModel);

        addUserLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                objectAddSearchAdmin.setVisible(event.getPageState(), true);
                addUserLink.setVisible(event.getPageState(), false);
            }

        });

        objectAddSearchAdmin.addCompletionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                objectAddSearchAdmin.setVisible(event.getPageState(), false);
                addUserLink.setVisible(event.getPageState(), true);
            }

        });

        add(adminTable);
        add(addUserLink);
        add(objectAddSearchAdmin);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(objectAddSearchAdmin, false);
    }

    // This returns the add search admin form to use for this object
    protected ObjectAddSearchAdmin getObjectAddSearchAdmin(
        CcmObjectSelectionModel<CcmObject> model) {

        return new ObjectAddSearchAdmin(model);
    }

    private class ObjectAdminActionListener implements TableActionListener {

        @Override
        public void cellSelected(final TableActionEvent event) {
            if (event.getColumn() == 1) {
                final PageState state = event.getPageState();

                final CcmObject object = objectSelectionModel.getSelectedObject(
                    state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
                final PermissionManager permissionManager = cdiUtil.findBean(
                    PermissionManager.class);
                final RoleRepository roleRepo = cdiUtil.findBean(
                    RoleRepository.class);

                permissionChecker.checkPermission(
                    ItemPrivileges.ADMINISTER, object);

                final String roleId = (String) event.getRowKey();
                final Optional<Role> role = roleRepo.findById(Long.parseLong(roleId));
                if (!role.isPresent()) {
                    throw new UncheckedWrapperException(String.format(
                        "No role with id %s found.", roleId));
                }

                permissionManager.revokePrivilege(ItemPrivileges.ADMINISTER,
                                                  role.get(),
                                                  object);
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
        }

    }

    protected TableModelBuilder getTableModelBuilder(
        final CcmObjectSelectionModel<CcmObject> model) {

        return new ObjectTableModelBuilder(model);
    }

    private class ObjectTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private final CcmObjectSelectionModel<CcmObject> model;

        ObjectTableModelBuilder(final CcmObjectSelectionModel<CcmObject> model) {
            this.model = model;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            final CcmObject object = model.getSelectedObject(state);

            final List<Role> roles = object.getPermissions().stream()
                .filter(permission -> ItemPrivileges.ADMINISTER.equals(
                permission.getGrantedPrivilege()))
                .map(permission -> permission.getGrantee())
                .collect(Collectors.toList());

            return new ObjectAdminTableModel(roles);
        }

    }

    private class ObjectAdminTableModel implements TableModel {

        private final Iterator<Role> roles;
        private Role currentRole;

        public ObjectAdminTableModel(final List<Role> roles) {
            this.roles = roles.iterator();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            if (roles.hasNext()) {
                currentRole = roles.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int column) {
            return currentRole;
        }

        @Override
        public Object getKeyAt(final int column) {
            return currentRole.getRoleId();
        }

    }

    private class ObjectAdminTableRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table list,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final Role role = (Role) value;

            switch (column) {
                case 0:
                    return new Text(role.getName());
                case 1:
                    return new ControlLink(new Text("remove"));
                default:
                    throw new IllegalArgumentException("Column index " + column
                                                       + " out of bounds 0..1");
            }
        }

    }

}
