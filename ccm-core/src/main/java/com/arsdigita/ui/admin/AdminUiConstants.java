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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class AdminUiConstants {

    public static final String ADMIN_APP_TYPE = "com.arsdigita.ui.Admin";
    
    /**
     * The XML namespace used by administration components.
     */
    public static final String ADMIN_XML_NS
                                   = "http://www.arsdigita.com/admin-ui/1.0";

    /**
     * Globalisation resource for administration UI.
     */
    public static final String ADMIN_BUNDLE
                               = "com.arsdigita.ui.admin.AdminResources";

    /**
     * Navigational dimension bar labels.
     */
    //      Label MY_WORKSPACE_LABEL = new Label
    //          (new GlobalizedMessage("ui.admin.nav.workspace",
    //                               ADMIN_BUNDLE));
    //      Label LOG_OUT_LABEL = new Label
    //          (new GlobalizedMessage("ui.admin.nav.logout",
    //                                 ADMIN_BUNDLE));
    /**
     * Administration page title
     */
    public static final Label PAGE_TITLE_LABEL = new Label(
        new GlobalizedMessage("ui.admin.dispatcher.title",
                              ADMIN_BUNDLE));

    /**
     * Administration main tab names.
     */
    public static final Label USER_TAB_TITLE = new Label(new GlobalizedMessage(
        "ui.admin.tab.user",
        ADMIN_BUNDLE));

    public static final Label GROUP_TAB_TITLE = new Label(new GlobalizedMessage(
        "ui.admin.tab.group",
        ADMIN_BUNDLE));

    public static final Label APPLICATIONS_TAB_TITLE = new Label(
        new GlobalizedMessage("ui.admin.tab.applications",
                              ADMIN_BUNDLE));

    public static final Label SYSINFO_TAB_TITLE = new Label(
        new GlobalizedMessage("ui.admin.tab.sysinfo.title", ADMIN_BUNDLE));

    public static final GlobalizedMessage USER_NAVBAR_TITLE
                                          = new GlobalizedMessage(
            "ui.admin.tab.user.navbartitle",
            ADMIN_BUNDLE);

    /**
     * Tabbed pane indices
     */
    public static final int USER_TAB_INDEX = 2;
    public static final int GROUP_TAB_INDEX = 3;

    /**
     * User tab name
     */
    public static final Label USER_TAB_SUMMARY = new Label(
        new GlobalizedMessage("ui.admin.tab.user.summary",
                              ADMIN_BUNDLE));
    public static final Label USER_TAB_BROWSE = new Label(new GlobalizedMessage(
        "ui.admin.tab.user.browse",
        ADMIN_BUNDLE));
    public static final Label USER_TAB_SEARCH = new Label(new GlobalizedMessage(
        "ui.admin.tab.user.search",
        ADMIN_BUNDLE));
    public static final Label USER_TAB_CREATE_USER = new Label(
        new GlobalizedMessage("ui.admin.tab.user.createuser",
                              ADMIN_BUNDLE));

    public static final int USER_TAB_SUMMARY_INDEX = 0;
    public static final int USER_TAB_BROWSE_INDEX = 1;
    public static final int USER_TAB_SEARCH_INDEX = 2;
    public static final int USER_TAB_CREATE_USER_INDEX = 3;

    /**
     * Global state parameters.
     */
    public static final BigDecimalParameter GROUP_ID_PARAM
                                            = new BigDecimalParameter("group_id");

    public static final BigDecimalParameter APPLICATIONS_ID_PARAM
                                            = new BigDecimalParameter(
            "application_id");

    public static final BigDecimalParameter USER_ID_PARAM
                                            = new BigDecimalParameter("user_id");

    /**
     * User summary panel.
     */
    public static final Label SUMMARY_PANEL_HEADER = new Label(
        new GlobalizedMessage("ui.admin.user.summarypanel.header", ADMIN_BUNDLE));

    public static final Label CREATE_USER_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.summarypanel.createUser", ADMIN_BUNDLE));

    public static final Label TOTAL_USERS_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.summarypanel.totalusers", ADMIN_BUNDLE));

    /**
     * User browse panel.
     */
    public static final Label BROWSE_USER_PANEL_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.browsepanel.header",
            ADMIN_BUNDLE));

    public static final Label USER_INFO_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.userinfo.header",
        ADMIN_BUNDLE));

    public static final Label USER_EDIT_PANEL_HEADER = new Label(
        new GlobalizedMessage("ui.admin.user.useredit.header",
                              ADMIN_BUNDLE));

    public static final Label USER_GROUP_PANEL_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.groupmembership.header",
            ADMIN_BUNDLE));

    public static final Label USER_DELETE_FAILED_PANEL_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.action.delete.failed.header",
            ADMIN_BUNDLE));

    public static final Label USER_PASSWORD_PANEL_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.password.header",
            ADMIN_BUNDLE));

    public static final Label USER_ACTION_PANEL_HEADER = new Label(
        new GlobalizedMessage("ui.admin.user.action.header",
                              ADMIN_BUNDLE));

    public static final Label USER_ACTION_CONTINUE = new Label(
        new GlobalizedMessage("ui.admin.user.action.continue",
                              ADMIN_BUNDLE));

    public static final Label USER_DELETE_LABEL = new Label(
        new GlobalizedMessage("ui.admin.user.delete.label",
                              ADMIN_BUNDLE));

    public static final Label USER_BAN_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.ban.label",
        ADMIN_BUNDLE));

    public static final Label USER_UNBAN_LABEL = new Label(
        new GlobalizedMessage("ui.admin.user.unban.label",
                              ADMIN_BUNDLE));

    public static final GlobalizedMessage USER_DELETE_CONFIRMATION
                                          = new GlobalizedMessage(
            "ui.admin.user.delete.confirm", ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_BAN_CONFIRMATION
                                          = new GlobalizedMessage(
            "ui.admin.user.ban.confirm",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_UNBAN_CONFIRMATION
                                          = new GlobalizedMessage(
            "ui.admin.user.unban.confirm",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_DELETE_FAILED_MSG
                                          = new GlobalizedMessage(
            "ui.admin.user.delete.failed.label", ADMIN_BUNDLE);

    public static final Label USER_TAB_EXTREME_ACTION_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.browsepanel.extremeaction",
            ADMIN_BUNDLE));

    public static final Label UPDATE_USER_PASSWORD_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.browsepanel.updatePassword",
            ADMIN_BUNDLE));

    public static final Label BECOME_USER_LABEL = new Label(
        new GlobalizedMessage("ui.admin.user.browsepanel.becomeUser",
                              ADMIN_BUNDLE));

    /**
     * Create new user panel.
     */
    public static final Label CREATE_USER_PANEL_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.createpanel.header",
            ADMIN_BUNDLE));

    /**
     * User search panel.
     */
    public static final Label SEARCH_PANEL_HEADER = new Label(
        new GlobalizedMessage("ui.admin.user.search.header",
                              ADMIN_BUNDLE));

    public static final Label PASSWORD_FORM_LABEL_PASSWORD = new Label(
        new GlobalizedMessage(
            "ui.admin.user.userpasswordform.passwordlabel",
            ADMIN_BUNDLE));

    public static final Label PASSWORD_FORM_LABEL_CONFIRMATION_PASSWORD
                              = new Label(new GlobalizedMessage(
            "ui.admin.user.userpasswordform.confirmpasswordlabel",
            ADMIN_BUNDLE));

    public static final Label PASSWORD_FORM_LABEL_QUESTION = new Label(
        new GlobalizedMessage(
            "ui.admin.user.userpasswordform.question",
            ADMIN_BUNDLE), false);

    public static final Label PASSWORD_FORM_LABEL_ANSWER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.userpasswordform.answer",
            ADMIN_BUNDLE), false);

    public static final GlobalizedMessage PASSWORD_FORM_SUBMIT
                                          = new GlobalizedMessage(
            "ui.admin.user.userpasswordform.submit",
            ADMIN_BUNDLE);

    /**
     * Constants for user add/edit form.
     */
    public static final String USER_FORM_ADD = "user-add-form";
    public static final String USER_FORM_EDIT = "user-edit-form";
    public static final String USER_FORM_INPUT_FIRST_NAME = "firstname";
    public static final String USER_FORM_INPUT_LAST_NAME = "lastname";
    public static final String USER_FORM_INPUT_PASSWORD = "password";
    public static final String USER_FORM_INPUT_PASSWORD_CONFIRMATION
                               = "password_confirmation";
    public static final String USER_FORM_INPUT_QUESTION = "question";
    public static final String USER_FORM_INPUT_ANSWER = "answer";
    public static final String USER_FORM_INPUT_PRIMARY_EMAIL = "email";
    public static final String USER_FORM_INPUT_ADDITIONAL_EMAIL
                               = "additional_email";
    public static final String USER_FORM_INPUT_SCREEN_NAME = "screenname";
    public static final String USER_FORM_INPUT_SSO = "sso_login";
    public static final String USER_FORM_INPUT_URL = "url";
    public static final String USER_FORM_INPUT_URL_DEFAULT = "http://";

    public static final Label USER_FORM_LABEL_FIRST_NAME = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.firstname",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_LAST_NAME = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.lastname",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_PASSWORD = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.password",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_PASSWORD_CONFIRMATION = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.confirmation",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_QUESTION = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.question",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_ANSWER = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.answer",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_PRIMARY_EMAIL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.primaryemail",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_ADDITIONAL_EMAIL = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.additionalemail",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.additionalemaillist",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_SCREEN_NAME = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.screenname",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_SSO = new Label(
        new GlobalizedMessage(
            "ui.admin.user.addeditform.ssologinname",
            ADMIN_BUNDLE));

    public static final Label USER_FORM_LABEL_URL = new Label(
        new GlobalizedMessage("ui.admin.user.addeditform.url",
                              ADMIN_BUNDLE));

    public static final Label USER_FORM_DELETE_ADDITIONAL_EMAIL = new Label(
        new GlobalizedMessage("ui.admin.user.addeditform.deleteemail",
                              ADMIN_BUNDLE));

    public static final GlobalizedMessage USER_FORM_SUBMIT
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.submit",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_FORM_ERROR_SCREEN_NAME_NOT_UNIQUE
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.error.screenname.notunique",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_FORM_ERROR_PRIMARY_EMAIL_NOT_UNIQUE
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.error.primaryemail.notunique",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_FORM_ERROR_PASSWORD_NOT_MATCH
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.error.password.notmatch",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_FORM_ERROR_ANSWER_NULL
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.error.answer.null",
            ADMIN_BUNDLE);

    public static final GlobalizedMessage USER_FORM_ERROR_ANSWER_WHITESPACE
                                          = new GlobalizedMessage(
            "ui.admin.user.addeditform.error.answer.whitespace",
            ADMIN_BUNDLE);

    /**
     * Constants for group add/edit form.
     */
    public static final String GROUP_FORM_ADD = "group-add-form";
    public static final String GROUP_FORM_EDIT = "group-edit-form";
    public static final String GROUP_FORM_INPUT_NAME = "name";
    public static final String GROUP_FORM_INPUT_PRIMARY_EMAIL = "email";

    public static final Label GROUP_FORM_LABEL_NAME = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.addeditform.namelabel",
            ADMIN_BUNDLE));

    public static final Label GROUP_FORM_LABEL_PRIMARY_EMAIL = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.addeditform.primaryemaillabel",
            ADMIN_BUNDLE));

    public static final GlobalizedMessage GROUP_FORM_SUBMIT
                                              = new GlobalizedMessage(
            "ui.admin.groups.addeditform.submit", ADMIN_BUNDLE);

    /**
     * Constants for group administration tab.
     */
    public static final Label GROUP_ACTION_CONTINUE = new Label(
        new GlobalizedMessage("ui.admin.groups.actioncontinue",
                              ADMIN_BUNDLE));

    public static final GlobalizedMessage GROUP_DELETE_FAILED_MSG
                                          = new GlobalizedMessage(
            "ui.admin.groups.groupdeletefailed",
            ADMIN_BUNDLE);

    public static final Label GROUP_INFORMATION_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.groupinformation",
            ADMIN_BUNDLE));

    public static final Label SUBGROUP_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.groups.subgroups",
        ADMIN_BUNDLE));

    public static final Label GROUP_EDIT_HEADER = new Label(
        new GlobalizedMessage("ui.admin.groups.groupedit",
                              ADMIN_BUNDLE));
    public static final Label ADD_SUBGROUP_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.add",
                              ADMIN_BUNDLE));

    public static final Label SUBMEMBER_HEADER = new Label(
        new GlobalizedMessage("ui.admin.groups.submembers",
                              ADMIN_BUNDLE));

    public static final Label DELETE_GROUP_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.delete",
                              ADMIN_BUNDLE));

    public static final Label GROUP_EXTREME_ACTIONS_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.extremeaction",
            ADMIN_BUNDLE));

    public static final Label GROUP_DELETE_FAILED_HEADER = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.deletefailed",
            ADMIN_BUNDLE));

    public static final Label ADD_GROUP_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.groups.addgrouplabel",
        ADMIN_BUNDLE));
    public static final Label EDIT_GROUP_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.edit",
                              ADMIN_BUNDLE));

    public static final Label SUBGROUP_COUNT_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.subgroupcountlabel",
            ADMIN_BUNDLE));
    public static final String GROUP_DELETE_CONFIRMATION
                               = "Are you sure you want to delete this group?";

    public static final Label ADD_SUBMEMBER_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.addsubmemberlabel",
                              ADMIN_BUNDLE));

    public static final Label REMOVE_SUBMEMBER_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.removesubmemberlabel",
            ADMIN_BUNDLE));
    public static final Label ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL = new Label(
        new GlobalizedMessage(
            "ui.admin.groups.addExisting",
            ADMIN_BUNDLE));

    public static final Label REMOVE_SUBGROUP_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.removeExisting",
                              ADMIN_BUNDLE));

    public static final Label GROUP_SEARCH_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.search", ADMIN_BUNDLE));

    public static final GlobalizedMessage SEARCH_BUTTON = new GlobalizedMessage(
        "ui.admin.groups.button.search",
        ADMIN_BUNDLE);

    public static final Label GROUP_NO_RESULTS = new Label(
        new GlobalizedMessage("ui.admin.groups.searchForm.noResults",
                              ADMIN_BUNDLE));

    public static final Label FOUND_GROUPS_TITLE = new Label(
        new GlobalizedMessage("ui.admin.groups.found.title",
                              ADMIN_BUNDLE));

    public static final Label PICK_GROUPS = new Label(new GlobalizedMessage(
        "ui.admin.groups.select.explanation",
        ADMIN_BUNDLE));

    public static final GlobalizedMessage SAVE_BUTTON = new GlobalizedMessage(
        "ui.admin.save", ADMIN_BUNDLE);

    public static final String SEARCH_QUERY = "query";

    public final static String ADMIN_PAGE_URL = "/admin/";

    public final static String ADMIN_SERVLET_PATH = "/admin/*";

}
