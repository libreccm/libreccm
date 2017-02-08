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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A pane used to administer the permissions of one {@link
 * ACSObject}. This is a reusable component that can be embedded into a page to
 * provide a generic UI. The page must have the "?po_id=" parameter to supply to
 * ACSObject id of the item one is managing permissions for.
 *
 * @author sdeusch@arsdigita.com
 * @authro <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CMSPermissionsPane extends SimpleContainer implements Resettable,
                                                                   ActionListener,
                                                                   RequestListener {

    // non-shared parameter models; leave package scope for access from its members.
    private ParameterModel searchString = new StringParameter(
        CMSPermissionsConstants.SEARCH_QUERY);
    private ParameterModel privilegeArray = new ArrayParameter(
        CMSPermissionsConstants.PRIV_SET);

    private String[] privileges;
    private Map<String, String> privilegeNameMap;
    private SimpleContainer permissionsTable;
    private CMSPermissionsTables allPermissions;
    private CMSPermissionsHeader PermissionsHeader;
    private SimpleContainer directPermissions;
    private Form roleSearchForm;
    private SimpleContainer inheritedPermissions;
    private SimpleComponent contextPanel;
    private SimpleContainer permissionsGrantPanel;
    private SimpleContainer noResultsPanel;
    private ObjectAdminListing adminListing;
    private CcmObjectSelectionModel<CcmObject> selectionModel;

    private RequestLocal userObjectInfo;

    /**
     * Default constructor creates components that show the default privileges
     * as defined in PermissionsConstants interface
     *
     * @param model
     */
    public CMSPermissionsPane(final CcmObjectSelectionModel<CcmObject> model) {
        this(CMSPermissionsConstants.DEFAULT_PRIVILEGES, new HashMap<>(), model);
        privilegeNameMap.put("read", "Read");
        privilegeNameMap.put("write", "Write");
        privilegeNameMap.put("create", "Create");
        privilegeNameMap.put("delete", "Delete");
        privilegeNameMap.put("admin", "Admin");
    }

    /**
     * Creates a PermissionsPane with components showing the privileges that are
     * passed in as argument.
     *
     * @param privileges
     * @param privilegeNameMap
     * @param selectionModel
     */
    public CMSPermissionsPane(
        final String[] privileges,
        final Map<String, String> privilegeNameMap,
        final CcmObjectSelectionModel<CcmObject> selectionModel) {

        userObjectInfo = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                return new CMSUserObjectStruct(state, selectionModel);
            }

        };

        this.privileges = privileges;
        this.selectionModel = selectionModel;
        this.privilegeNameMap = privilegeNameMap;
    }

    /**
     * Overwrite this method to construct your default Permissions Pane with the
     * components you need. You can subclass anonymously overwriting just the
     * register method. Note: the getXXX methods are lazy instantiators, i.e.
     * they produce the components only if not already there. (You can even
     * overwrite the getXXX components with your own implementation, e.g., if
     * you want to show a List instead of a Table for the direct permissions,
     * but still use a Table for the inherited permissions.
     *
     * @param page
     */
    @Override
    public void register(final Page page) {
        super.register(page);

        // add permissions components to this specific implementation
        // add(getPermissionsHeader());
//        add(getContextPanel());
        add(getPermissionsTable());
//        add(getDirectPermissionsPanel());
//        add(getUserSearchForm())
//        add(getInheritedPermissionsPanel());
//        add(getPermissionGrantPanel());
//        add(getNoSearchResultPanel());
//        add(getAdminListingPanel());

        // set initial visibility of components
        // p.setVisibleDefault(getPermissionsHeader(), true);
        page.setVisibleDefault(getPermissionsTable(), true);
//        page.setVisibleDefault(getDirectPermissionsPanel(), true);
//        page.setVisibleDefault(getUserSearchForm(), true);
//        page.setVisibleDefault(getInheritedPermissionsPanel(), true);
//        page.setVisibleDefault(getContextPanel(), true);
//        page.setVisibleDefault(getPermissionGrantPanel(), false);
//        page.setVisibleDefault(getNoSearchResultPanel(), false);
//        page.setVisibleDefault(getAdminListingPanel(), false);

        // p.addActionListener(this);
        // p.addRequestListener(this);
        // add state parameters
        page.addGlobalStateParam(searchString);
        page.addGlobalStateParam(privilegeArray);

    }

    /**
     * Implementation of interface bebop.Resettable. Use {@code reset} to reset
     * permissions component to initial state, e.g. if you embed it into another
     * container.
     */
    @Override
    public void reset(final PageState state) {
//        showAdmin(state);
    }

    /**
     * Utility method to get the authenticated user or group
     *
     * @param state
     *
     * @return
     */
    public Role getRequestingRole(final PageState state) {
        return ((CMSUserObjectStruct) userObjectInfo.get(state)).getRole();
    }

    /**
     * Utility method to get the ACSObject from the page state
     *
     * @param state
     *
     * @return
     */
    public CcmObject getObject(final PageState state) {
        return ((CMSUserObjectStruct) userObjectInfo.get(state)).getObject();
    }

    /**
     * Returns the title "Permissions on object articles", e.g.
     *
     * @return
     */
    public Label getTitle() {
        return ((CMSPermissionsHeader) getPermissionsHeader()).getTitle();
    }

    /**
     * Returns a string array of privilege names as defined in the constructor
     *
     * @return
     */
    public String[] getPrivileges() {
        return Arrays.copyOf(privileges, privileges.length);
    }

    private SimpleContainer getPermissionsTable() {
        if (permissionsTable != null) {
            return permissionsTable;
        }

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        final Label header = new Label(new GlobalizedMessage(
            "cms.ui.permissions.table.header",
            CmsConstants.CMS_BUNDLE));
        panel.add(header);

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
            "cms.ui.permissions.table.remove_all.header")));
        final Table table = new Table(new CMSPermissionsTableModelBuilder(this),
                                      headerLabels.toArray());
        table.setClassAttr("dataTable");
        for (int j = 1; j < table.getColumnModel().size() - 1; j++) {
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

                final CcmObject object = getObject(state);
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

        permissionsTable = panel;
        return panel;
    }

    private Label generatePrivilegeColumnHeader(final String privilege) {
        return new Label(new GlobalizedMessage(
            String.format("cms.ui.permissions.table.privilege.headers.%s",
                          privilege),
            CmsConstants.CMS_BUNDLE));
    }

//    /**
//     * Produces the direct and inherited permission tables to the privileges
//     * defined in the constructor.
//     *
//     * @see #getDirectPermissionsPanel(), getInheritedPermissionsPanel()
//     */
//    private CMSPermissionsTables getPermissionsTables() {
//        if (allPermissions == null) {
//            allPermissions = new CMSPermissionsTables(privileges, this);
//        }
//        return allPermissions;
//    }
//
//    /**
//     * Returns the bebop component with a table for the direct permission on the
//     * privileges defined in the constructor
//     *
//     * @return
//     *
//     * @see #getInheritedPermissionsPanel()
//     */
//    public SimpleContainer getDirectPermissionsPanel() {
//        directPermissions = getPermissionsTables().getPermissions(
//            CMSPermissionsConstants.DIRECT);
//        return directPermissions;
//    }
//
//    /**
//     * Returns the bebop component with a table for the inherited permission on
//     * the privileges defined in the constructor. The table is non-editable.
//     *
//     * @return
//     *
//     * @see #getDirectPermissionsPanel()
//     */
//    public SimpleContainer getInheritedPermissionsPanel() {
//        inheritedPermissions = getPermissionsTables()
//            .getPermissions(CMSPermissionsConstants.INHERITED);
//        return inheritedPermissions;
//    }
    public SimpleContainer getAdminListingPanel() {
        if (adminListing == null) {
            adminListing = new ObjectAdminListing(selectionModel);
        }
        return adminListing;
    }

    /**
     * This is an outstanding item.
     *
     * @return
     */
    public SegmentedPanel getUniversalPermissionsPanel() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a bebop form for user and group search.
     *
     * @return
     */
    public Form getUserSearchForm() {
        if (roleSearchForm == null) {
            roleSearchForm = new CMSUserSearchForm(this);
        }
        return roleSearchForm;
    }

    /**
     * Returns a panel with a form with 2 checkbox groups, one for parties to
     * choose, one for privileges to assign.
     *
     * @return
     */
    public SimpleContainer getPermissionGrantPanel() {
        if (permissionsGrantPanel == null) {
            CMSPermissionsGrant permGrant = new CMSPermissionsGrant(this);
            permissionsGrantPanel = permGrant.getPanel();
        }
        return permissionsGrantPanel;
    }

    /**
     * Returns a bebop container with the title to this object and a navigation
     * bar, specific for the UI at /permissions/.
     *
     * @return
     */
    public SimpleContainer getPermissionsHeader() {
        if (PermissionsHeader == null) {
            PermissionsHeader = new CMSPermissionsHeader(this);
        }
        return PermissionsHeader;
    }

    /**
     * Returns a bebop panel indicating that the user search yielded no results.
     * It is customised in the xsl stylesheet.
     *
     * @return
     */
    public SimpleContainer getNoSearchResultPanel() {
        if (noResultsPanel == null) {
            final Label errorMsg = new Label(CMSPermissionsConstants.NO_RESULTS);
            errorMsg.setClassAttr("errorBullet");
            final BoxPanel panel = new BoxPanel();
            panel.add(errorMsg);
            panel.add(new CMSUserSearchForm(this));
            noResultsPanel = new SegmentedPanel().addSegment(new Text(" "),
                                                             panel);
        }
        return noResultsPanel;
    }

//    /**
//     * Returns a bebop panel with a link to the permissions administration page
//     * of the object's direct ancestor (parent).
//     *
//     * @return
//     */
//    public SimpleComponent getContextPanel() {
//        if (contextPanel == null) {
//            contextPanel = getPermissionsTables().makeContextPanel();
//        }
//        return contextPanel;
//    }
    ParameterModel getSearchString() {
        return searchString;
    }

    ParameterModel getPrivilegeParam() {
        return privilegeArray;
    }

    CcmObjectSelectionModel<CcmObject> getSelectionModel() {
        return selectionModel;
    }

//    /**
//     * Shows panel with no results to user search.
//     *
//     * @param state
//     */
//    public void showNoResults(final PageState state) {
//        getDirectPermissionsPanel().setVisible(state, false);
//        getInheritedPermissionsPanel().setVisible(state, false);
//        getContextPanel().setVisible(state, false);
//        getUserSearchForm().setVisible(state, false);
//        getPermissionGrantPanel().setVisible(state, false);
//        getNoSearchResultPanel().setVisible(state, true);
//    }
//    /**
//     * Show the Grant privileges panel
//     *
//     * @param state
//     */
//    public void showGrant(final PageState state) {
//        getDirectPermissionsPanel().setVisible(state, false);
//        getInheritedPermissionsPanel().setVisible(state, false);
//        getContextPanel().setVisible(state, false);
//        getUserSearchForm().setVisible(state, false);
//        getNoSearchResultPanel().setVisible(state, false);
//        getPermissionGrantPanel().setVisible(state, true);
//    }
//    /**
//     * Shows the administration page of permissions to one object.
//     *
//     * @param state
//     */
//    public void showAdmin(final PageState state) {
//        final CcmObject object = getObject(state);
//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final PermissionChecker permissionChecker = cdiUtil.findBean(
//            PermissionChecker.class);
//
//        final boolean canAccess;
//        if (object == null) {
//            throw new UncheckedWrapperException(
//                "Unexpected null value for object.");
//        } else if (object instanceof ContentItem) {
//            canAccess = permissionChecker.isPermitted(ItemPrivileges.ADMINISTER,
//                                                      object);
//        } else if (object instanceof Folder) {
//            canAccess = permissionChecker.isPermitted(ItemPrivileges.ADMINISTER,
//                                                      object);
//        } else {
//            throw new UncheckedWrapperException(String.format(
//                "The object is of type \"%s\" which is not supported here.",
//                object.getClass().getName()));
//        }
//
//        if (canAccess) {
//            showCustom(state, true);
//
////                    showCustom(state, false);
//            getContextPanel().setVisible(state, true);
//
//        } else {
//            // do not have permission to set permissions, so don't show them
//            getDirectPermissionsPanel().setVisible(state, false);
//            getInheritedPermissionsPanel().setVisible(state, false);
//            getUserSearchForm().setVisible(state, false);
//            getContextPanel().setVisible(state, false);
//        }
//
//        getPermissionGrantPanel().setVisible(state, false);
//    }
    @Override
    public void actionPerformed(final ActionEvent event) {

        final PageState state = event.getPageState();

        /**
         * check if viewing user has admin privilege on this Object, after
         * Action Event fires everytime the component is visible.
         *
         */
        if (this.isVisible(state)) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            if (!permissionChecker.isPermitted(ItemPrivileges.ADMINISTER,
                                               getObject(state))) {
                try {
                    DispatcherHelper.sendRedirect(state.getRequest(),
                                                  state.getResponse(),
                                                  "/permissions/denied");
                } catch (IOException ex) {
                    throw new UncheckedWrapperException(ex);
                }
            }
        }
    }

//    public void showCustom(final PageState state, final boolean custom) {
//        if (custom) {
//            getDirectPermissionsPanel().setVisible(state, true);
//            getInheritedPermissionsPanel().setVisible(state, false);
//            getUserSearchForm().setVisible(state, true);
//            getAdminListingPanel().setVisible(state, true);
//        } else {
//            getDirectPermissionsPanel().setVisible(state, false);
//            getInheritedPermissionsPanel().setVisible(state, true);
//            getUserSearchForm().setVisible(state, false);
//            getAdminListingPanel().setVisible(state, false);
//        }
//    }
    public String getPrivilegeName(final String privilege) {
        return privilegeNameMap.get(privilege);
    }

    @Override
    public void pageRequested(final RequestEvent event) {
        //     PageState s = e.getPageState();
        //     ACSObject object = getObject(s);
        //     if (object != null) {
        //         DataObject context = PermissionService.getContext(object);
        //         if (context != null) {
        //             showCustom(s, false);
        //         } else {
        //             showCustom(s, true);
        //         }
        //     } else {
        //         throw new IllegalStateException( (String) GlobalizationUtil.globalize("cms.ui.permissions.current_object_is_null").localize());
        //     }
    }

}
