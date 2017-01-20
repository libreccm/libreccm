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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.List;
import java.util.TooManyListenersException;

import static com.arsdigita.cms.ui.permissions.CMSPermissionsConstants.*;

/**
 * Permissions Grant container for permissions assignment. Widgets are currently
 * organised on a bebop SegmentedPanel.
 *
 * @author Stefan Deusch (sdeusch@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsGrant {

    private final static String PARTIES_CBG = "parties_cbg";
    private final static String PRIVILEGES_CBG = "privs_cbg";

    // data keys
    private static final String USER_ID = "userID";
    private static final String SCREEN_NAME = "screenName";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    private final CMSPermissionsPane parent;
    private final SegmentedPanel grantPanel;
    private CheckboxGroup partiesCheckboxGroup;
    private CheckboxGroup privilegesCheckboxGroup;
    private Form form;
    private Submit saveSubmit;

    /**
     * Creates a PermissionsGrant object that will be contained with another
     * component. This is currently used inside the permissions pane.
     *
     * @param parent the enclosing container
     */
    public CMSPermissionsGrant(final CMSPermissionsPane parent) {
        this.parent = parent;
        makeForm();
        grantPanel = new SegmentedPanel();
        grantPanel.addSegment(new Label(PAGE_GRANT_TITLE), form);
    }

    /**
     * Builds the form used to grant pivileges to users and groups.
     */
    private void makeForm() {
        form = new Form("GrantPrivileges", new BoxPanel());
        form.setMethod(Form.POST);
        form.addSubmissionListener(new GrantFormSubmissionListener());
        form.add(new Label(PAGE_GRANT_LEFT));
        partiesCheckboxGroup = new CheckboxGroup(PARTIES_CBG);
        try {
            partiesCheckboxGroup.addPrintListener(new UserSearchPrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e
                .getMessage(), e);
        }
        form.add(partiesCheckboxGroup);

        form.add(new Label(PAGE_GRANT_RIGHT));
        privilegesCheckboxGroup = new CheckboxGroup(PRIVILEGES_CBG);
        try {
            privilegesCheckboxGroup.addPrintListener(
                new PrivilegePrintListener());
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e
                .getMessage(), e);
        }
        form.add(privilegesCheckboxGroup);

        saveSubmit = new Submit("save", SAVE_BUTTON);
        form.add(saveSubmit);
    }

    /**
     * Returns the SegmentedPanel with the permissions grant Form
     *
     * @return the SegmentedPanel with the permissions grant form
     */
    public SegmentedPanel getPanel() {
        return grantPanel;
    }

    private class GrantFormSubmissionListener
        implements FormSubmissionListener {

        @Override
        public void submitted(FormSectionEvent event) throws
            FormProcessException {
            final PageState state = event.getPageState();
            final FormData data = event.getFormData();
            final String[] gids = (String[]) data.get(PARTIES_CBG);
            final String[] privs = (String[]) data.get(PRIVILEGES_CBG);
            if (privs != null && gids != null) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PermissionManager permissionManager = cdiUtil.findBean(
                    PermissionManager.class);

                final Long oID = parent.getObject(state).getObjectId();
                for (String gid : gids) {
                    final Long gID = Long.parseLong(gid);
                    final CMSUserObjectStruct userObjectStruct
                                                  = new CMSUserObjectStruct(gID,
                                                                            oID);
                    for (String priv : privs) {
                        permissionManager.grantPrivilege(
                            priv,
                            userObjectStruct.getRole(),
                            userObjectStruct.getObject());
                    }
                }
            }
            parent.showAdmin(state);
        }

    }

    private class UserSearchPrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final OptionGroup target = (OptionGroup) event.getTarget();

            // get query string
            final String search = StringUtils.stripWhiteSpace((String) state.
                getValue(new StringParameter(SEARCH_QUERY)));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final RoleRepository roleRepo = cdiUtil.findBean(
                RoleRepository.class);

            final List<Role> roles = roleRepo.searchByName(search);

            roles.forEach(role -> target.addOption(new Option(
                Long.toString(role.getRoleId()),
                new Text(role.getName()))));
        }

    }

    private class PrivilegePrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {
            final PageState state = event.getPageState();
            final OptionGroup target = (OptionGroup) event.getTarget();

            // get privileges from page state
            final Object[] privileges = (Object[]) state.getValue(
                new ArrayParameter(
                    PRIV_SET));

            // print ceckbox group with privileges
            for (Object privilege : privileges) {
                target.addOption(new Option((String) privilege,
                                            new Text(parent.getPrivilegeName(
                                                (String) privilege))));
            }
        }

    }

}
