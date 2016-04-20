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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Password;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.Group;
import org.libreccm.security.GroupManager;
import org.libreccm.security.GroupRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TooManyListenersException;
import java.util.TreeSet;
import java.util.stream.IntStream;

import javax.mail.MessagingException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserAdmin extends BoxPanel {

    private final StringParameter userIdParameter;
    private final StringParameter emailParameter;
    private final ParameterSingleSelectionModel<String> selectedUserId;
    private final ParameterSingleSelectionModel<String> selectedEmailAddress;
    private final UsersTablePanel usersTablePanel;
    private final UserEditForm userEditForm;
    private final PasswordSetForm passwordSetForm;
    private final UserDetails userDetails;
    private final EmailForm emailForm;
    private final GroupMembershipsForm groupMembershipsForm;
    private final RoleMembershipsForm editRoleMembershipsForm;
    private final NewUserForm newUserForm;

    public UserAdmin() {
        super(BoxPanel.VERTICAL);

        setBasicProperties();
        userIdParameter = new StringParameter("selected_user_id");
        selectedUserId = new ParameterSingleSelectionModel<>(userIdParameter);

        emailParameter = new StringParameter("selected_email_address");
        selectedEmailAddress = new ParameterSingleSelectionModel<>(
            emailParameter);

        usersTablePanel = new UsersTablePanel(this, selectedUserId);
        add(usersTablePanel);

        userDetails = new UserDetails(this,
                                      selectedUserId,
                                      selectedEmailAddress);

        userEditForm = new UserEditForm(this, selectedUserId);
        add(userEditForm);

        passwordSetForm = new PasswordSetForm(this, selectedUserId);
        add(passwordSetForm);

        emailForm = new EmailForm(this, selectedUserId, selectedEmailAddress);
        add(emailForm);

        add(userDetails);

        groupMembershipsForm = new GroupMembershipsForm(this,
                                                        selectedUserId);
        add(groupMembershipsForm);

        editRoleMembershipsForm = new RoleMembershipsForm(this, selectedUserId);
        add(editRoleMembershipsForm);

        newUserForm = new NewUserForm(this, selectedUserId);
        add(newUserForm);
    }

    private void setBasicProperties() {
        setIdAttr("userAdmin");
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(userIdParameter);
        page.addGlobalStateParam(emailParameter);

        page.setVisibleDefault(usersTablePanel, true);
        page.setVisibleDefault(userDetails, false);
        page.setVisibleDefault(userEditForm, false);
        page.setVisibleDefault(passwordSetForm, false);
        page.setVisibleDefault(emailForm, false);
        page.setVisibleDefault(groupMembershipsForm, false);
        page.setVisibleDefault(editRoleMembershipsForm, false);
        page.setVisibleDefault(newUserForm, false);
    }

    protected void showUserDetails(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeUserDetails(final PageState state) {
        selectedUserId.clearSelection(state);
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showUserEditForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, true);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeUserEditForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showPasswordSetForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, true);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closePasswordSetForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEmailForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, true);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeEmailForm(final PageState state) {
        selectedEmailAddress.clearSelection(state);
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEditGroupMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, true);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void closeEditGroupMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showEditRoleMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, true);
        newUserForm.setVisible(state, false);
    }

    protected void closeEditRoleMembershipsForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

    protected void showNewUserForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, true);
    }

    protected void closeNewUserForm(final PageState state) {
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
        userEditForm.setVisible(state, false);
        passwordSetForm.setVisible(state, false);
        emailForm.setVisible(state, false);
        groupMembershipsForm.setVisible(state, false);
        editRoleMembershipsForm.setVisible(state, false);
        newUserForm.setVisible(state, false);
    }

}
