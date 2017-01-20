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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.CcmObjectSelectionModel;

import org.libreccm.core.CcmObject;
import org.libreccm.security.User;

import com.arsdigita.util.Assert;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.UserRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;
import java.util.TooManyListenersException;

/**
 * <p>
 * This component is a form for adding object administrators
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ObjectAddAdmin extends SimpleContainer
    implements FormProcessListener {

    private final static String SEARCH_QUERY = "searchQuery";
    private final static String USERS = "roles";
    private final static String SUBMIT = "addSubmit";
    private final static String CANCEL = "addCancel";

    private Widget searchWidget;
    private final RequestLocal queryRequestLocal;
    private String labelText;
    private String m_submitText;

    private final CMSContainer noMatchesContainer;
    private final CMSContainer matchesContainer;

    private final Form form;
    private Hidden searchQuery;
    private CheckboxGroup rolesCheckboxGroup;
    private Submit submit;
    private Submit cancel;

    private final CcmObjectSelectionModel<CcmObject> objectSelectionModel;

    public ObjectAddAdmin(
        final CcmObjectSelectionModel<CcmObject> objectSelectionModel,
        final TextField search) {

//        super(search, "ObjectAddAdmin");
        labelText = "Check the box next to the name of the person(s) to assign.";
        m_submitText = "Add Members";
        searchWidget = search;
        queryRequestLocal = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                return makeQuery(state);
            }

        };
        this.objectSelectionModel = objectSelectionModel;

        form = makeForm("ObjectAddAdmin");
        final Label title = new Label(new GlobalizedMessage("cms.ui.matches",
                                                            CmsConstants.CMS_BUNDLE));
        title.setFontWeight(Label.BOLD);

        final Label label = new Label(new GlobalizedMessage(
            "cms.ui.there_was_no_one_matching_the_search_criteria",
            CmsConstants.CMS_BUNDLE));
        label.setFontWeight("em");

        noMatchesContainer = new CMSContainer();
        noMatchesContainer.add(title);
        noMatchesContainer.add(label);
        add(noMatchesContainer);

        matchesContainer = new CMSContainer();
        matchesContainer.add(title);
        matchesContainer.add(form);
        add(matchesContainer);
    }

    /**
     * Build the form used to add roles.
     *
     * @param name
     *
     * @return The form
     */
    protected Form makeForm(final String name) {
        final CMSForm form = new CMSForm(name) {

            public final boolean isCancelled(final PageState state) {
                return cancel.isSelected(state);
            }

        };

        // This hidden field will store the search query. A hidden widget is
        // used instead of a request local variable because the search query
        // should only be updated when the search form is submitted.
        searchQuery = new Hidden(SEARCH_QUERY);
        form.add(searchQuery, ColumnPanel.FULL_WIDTH);

        final Text label = new Text(labelText);
        form.add(label, ColumnPanel.FULL_WIDTH);

        // Add the list of roles that can be added.
        rolesCheckboxGroup = new CheckboxGroup(USERS);
        rolesCheckboxGroup
            .addValidationListener(new NotNullValidationListener());
        try {
            rolesCheckboxGroup.addPrintListener(new PrintListener() {

                @Override
                public void prepare(PrintEvent event) {
                    final CheckboxGroup target = (CheckboxGroup) event
                        .getTarget();
                    final PageState state = event.getPageState();
                    // Ensures that the init listener gets fired before the
                    // print listeners.
                    addRoles(state, target);
                }

            });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException(ex);
        }
        form.add(rolesCheckboxGroup, ColumnPanel.FULL_WIDTH);

        // Submit and Cancel buttons.
        final SimpleContainer container = new SimpleContainer();
        submit = new Submit(SUBMIT, m_submitText);
        container.add(submit);
        cancel = new Submit(CANCEL, "Cancel");
        container.add(cancel);
        form.add(container, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        form.addProcessListener(this);

        return form;
    }

    /**
     * Fetches the form for adding users.
     *
     * @return The "add user" form
     */
    public Form getForm() {
        return form;
    }

    /**
     * Fetches the widget that contains the search string.
     *
     * @return The widget that contains the search string
     */
    protected Widget getSearchWidget() {
        return searchQuery;
    }

    /**
     * Adds roles to the option group.
     *
     * @param state  The page state
     * @param target The option group
     *
     * @pre ( state != null && target != null )
     */
    protected void addRoles(final PageState state, final OptionGroup target) {

        @SuppressWarnings("unchecked")
        final List<Role> roles = (List<Role>) queryRequestLocal.get(state);

        roles.forEach(role -> target.addOption(
            new Option(Long.toString(role.getRoleId()), role.getName())));
    }

    protected List<User> makeQuery(final PageState state) {
        Assert.isTrue(objectSelectionModel.isSelected(state));

        final CcmObject object = (CcmObject) objectSelectionModel
            .getSelectedObject(state);
        final String searchQuery = (String) getSearchWidget().getValue(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final UserRepository userRepo = cdiUtil.findBean(UserRepository.class);

        return userRepo.filtered(searchQuery);
    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);
        final RoleRepository roleRepo = cdiUtil.findBean(RoleRepository.class);

        final CcmObject object = objectSelectionModel.getSelectedObject(state);

        permissionChecker.checkPermission(ItemPrivileges.ADMINISTER, object);

        final String[] roleIds = (String[]) data.get("roles");
        if (roleIds != null) {

            // Add each checked user to the object
            for (final String roleId : roleIds) {
                final Role role = roleRepo.findById(Long.parseLong(roleId));
                if (role == null) {
                    throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.permissions.cannot_add_user",
                        CmsConstants.CMS_BUNDLE));
                }
                permissionManager.grantPrivilege(ItemPrivileges.ADMINISTER,
                                                 role,
                                                 object);
            }

        } else {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.permissions.no_roles_were_selected",
                CmsConstants.CMS_BUNDLE));
        }

        fireCompletionEvent(state);
    }

}
