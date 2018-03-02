/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "item_permissions_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "item_permissions_step.description"
)
public class ItemPermissionsStep extends ResettableContainer {

    private final ItemSelectionModel itemSelectionModel;

    public ItemPermissionsStep(final ItemSelectionModel itemSelectionModel,
                               final AuthoringKitWizard authoringKitWizard,
                               final StringParameter selectedLanguage) {

        super("cms:permissionsStep", CMS.CMS_XML_NS);

        this.itemSelectionModel = itemSelectionModel;

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        final Label header = new Label(new GlobalizedMessage(
            "cms.ui.permissions.table.header",
            CmsConstants.CMS_BUNDLE));
        panel.add(header);
        super.add(panel);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);
        final List<String> privileges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class);
        final List<Label> headerLabels = privileges.stream()
            .map(privilege -> generatePrivilegeColumnHeader(privilege))
            .collect(Collectors.toList());
        headerLabels.add(0,
                         new Label(new GlobalizedMessage(
                             "cms.ui.permissions.table.role_header",
                             CmsConstants.CMS_BUNDLE)));
        headerLabels.add(new Label(new GlobalizedMessage(
            "cms.ui.permissions.table.remove_all.header",
            CmsConstants.CMS_BUNDLE)));
        final Table table = new Table(
            new PermissionsTableModelBuilder(),
            headerLabels.toArray());
        table.setClassAttr("dataTable");
        for (int j = 1; j < table.getColumnModel().size() - 1; j++) {
            table.getColumn(j).setKey(privileges.get(j - 1));
            table.getColumn(j).setCellRenderer(new TableCellRenderer() {

                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {

                    final ControlLink link = new ControlLink("");

                    final CMSPermissionsTableColumn col
                                                        = (CMSPermissionsTableColumn) value;
                    if (col.isPermitted()) {
                        link.setClassAttr("checkBoxChecked");
                    } else {
                        link.setClassAttr("checkBoxUnchecked");
                    }

                    return link;
                }

            });
        }
        table.getColumn(table.getColumnModel().size() - 1).setCellRenderer(
            new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                final ControlLink link = new ControlLink(new Label(
                    new GlobalizedMessage(
                        "cms.ui.permissions.table.actions.remove_all",
                        CmsConstants.CMS_BUNDLE)));
                link.setConfirmation(new GlobalizedMessage(
                    "cms.ui.permissions.table.actions.remove_all.confirm",
                    CmsConstants.CMS_BUNDLE));

                return link;
            }

        });
        table.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final int columnIndex = event.getColumn();
                if (event.getRowKey() == null) {
                    return;
                }
                final String roleName = (String) event.getRowKey();

                final Table table = (Table) event.getSource();
                final int columnCount = table.getColumnModel().size();
                final int lastColumnIndex = columnCount - 1;

                final CcmObject object = itemSelectionModel
                    .getSelectedItem(state);
                final RoleRepository roleRepo = cdiUtil.findBean(
                    RoleRepository.class);
                final Optional<Role> role = roleRepo.findByName(roleName);
                if (!role.isPresent()) {
                    throw new UnexpectedErrorException(String.format(
                        "Role \"%s\" was not found inthe database, but was in "
                            + "the permissions table.",
                        roleName));
                }
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
                if (columnIndex > 0 && columnIndex < lastColumnIndex) {
                    final String privilege = table.getColumn(columnIndex)
                        .getKey();

                    if (permissionChecker.isPermitted(privilege,
                                                      object,
                                                      role.get())) {
                        permissionManager.revokePrivilege(privilege,
                                                          role.get(),
                                                          object);
                    } else {
                        permissionManager.grantPrivilege(privilege,
                                                         role.get(),
                                                         object);
                    }
                } else if (columnIndex == lastColumnIndex) {
                    final List<String> privileges = permissionManager
                        .listDefiniedPrivileges(ItemPrivileges.class);
                    privileges.forEach(privilege -> permissionManager
                        .revokePrivilege(privilege, role.get(), object));
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        panel.add(table);
    }

    private Label generatePrivilegeColumnHeader(final String privilege) {
        return new Label(new GlobalizedMessage(
            String.format("cms.ui.permissions.table.privilege.headers.%s",
                          privilege),
            CmsConstants.CMS_BUNDLE));
    }

    private class PermissionsTableModelBuilder extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            final CcmObject object = itemSelectionModel.getSelectedItem(state);
            return new CMSPermissionsTableModel(object);
        }

    }

//    private class PermissionsTableModel implements TableModel {
//        
//        private final Iterator<PermissionsTableRow> iterator;
//        private PermissionsTableRow currentRow;
//        
//        public PermissionsTableModel(final CcmObject object) {
//            
//        }
//        
//    }
}
