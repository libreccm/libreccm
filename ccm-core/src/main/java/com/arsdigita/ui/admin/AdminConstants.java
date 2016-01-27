/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Centralize place for all constants used in the admin UI.
 *
 * @author David Dao
 * @version $Revision$ $Date$
 */
interface AdminConstants {

    /**
     * The XML namespace used by admin components.
     */
    String ADMIN_XML_NS = "http://www.arsdigita.com/admin-ui/1.0";

    /**
     * Globalization resource for admin ui.
     */
    String BUNDLE_NAME = "com.arsdigita.ui.admin.AdminResources";

    /**
     * Navigational dimension bar labels.
     */
    //      Label MY_WORKSPACE_LABEL = new Label
    //          (new GlobalizedMessage("ui.admin.nav.workspace",
    //                               BUNDLE_NAME));
    //      Label LOG_OUT_LABEL = new Label
    //          (new GlobalizedMessage("ui.admin.nav.logout",
    //                                 BUNDLE_NAME));
    /**
     * Administration page title
     */
    Label PAGE_TITLE_LABEL = new Label(new GlobalizedMessage("ui.admin.dispatcher.title",
                                                             BUNDLE_NAME));

    /**
     * Administration main tab names.
     */
    Label USER_TAB_TITLE = new Label(new GlobalizedMessage("ui.admin.tab.user",
                                                           BUNDLE_NAME));

    Label GROUP_TAB_TITLE = new Label(new GlobalizedMessage("ui.admin.tab.group",
                                                            BUNDLE_NAME));

    Label APPLICATIONS_TAB_TITLE = new Label(new GlobalizedMessage("ui.admin.tab.applications",
                                                                   BUNDLE_NAME));

    Label SYSINFO_TAB_TITLE = new Label(new GlobalizedMessage("ui.admin.tab.sysinfo.title", BUNDLE_NAME));

    GlobalizedMessage USER_NAVBAR_TITLE = new GlobalizedMessage("ui.admin.tab.user.navbartitle",
                                                                BUNDLE_NAME);

    /**
     * Tabbed pane indices
     */
    int USER_TAB_INDEX = 2;
    int GROUP_TAB_INDEX = 3;

    /**
     * User tab name
     */
    Label USER_TAB_SUMMARY = new Label(new GlobalizedMessage("ui.admin.tab.user.summary",
                                                             BUNDLE_NAME));
    Label USER_TAB_BROWSE = new Label(new GlobalizedMessage("ui.admin.tab.user.browse",
                                                            BUNDLE_NAME));
    Label USER_TAB_SEARCH = new Label(new GlobalizedMessage("ui.admin.tab.user.search",
                                                            BUNDLE_NAME));
    Label USER_TAB_CREATE_USER = new Label(new GlobalizedMessage("ui.admin.tab.user.createuser",
                                                                 BUNDLE_NAME));

    int USER_TAB_SUMMARY_INDEX = 0;
    int USER_TAB_BROWSE_INDEX = 1;
    int USER_TAB_SEARCH_INDEX = 2;
    int USER_TAB_CREATE_USER_INDEX = 3;

    /**
     * Global state parameters.
     */
    BigDecimalParameter GROUP_ID_PARAM = new BigDecimalParameter("group_id");

    BigDecimalParameter APPLICATIONS_ID_PARAM = new BigDecimalParameter("application_id");

    BigDecimalParameter USER_ID_PARAM = new BigDecimalParameter("user_id");

    /**
     * User summary panel.
     */
    Label SUMMARY_PANEL_HEADER = new Label(
        new GlobalizedMessage("ui.admin.user.summarypanel.header", BUNDLE_NAME));

    Label CREATE_USER_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.summarypanel.createUser", BUNDLE_NAME));

    Label TOTAL_USERS_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.summarypanel.totalusers", BUNDLE_NAME));

    /**
     * User browse panel.
     */
    Label BROWSE_USER_PANEL_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.user.browsepanel.header",
        BUNDLE_NAME));

    Label USER_INFO_LABEL = new Label(new GlobalizedMessage("ui.admin.user.userinfo.header",
                                                            BUNDLE_NAME));

    Label USER_EDIT_PANEL_HEADER = new Label(new GlobalizedMessage("ui.admin.user.useredit.header",
                                                                   BUNDLE_NAME));

    Label USER_GROUP_PANEL_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.user.groupmembership.header",
        BUNDLE_NAME));

    Label USER_DELETE_FAILED_PANEL_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.user.action.delete.failed.header",
        BUNDLE_NAME));

    Label USER_PASSWORD_PANEL_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.user.password.header",
        BUNDLE_NAME));

    Label USER_ACTION_PANEL_HEADER = new Label(new GlobalizedMessage("ui.admin.user.action.header",
                                                                     BUNDLE_NAME));

    Label USER_ACTION_CONTINUE = new Label(new GlobalizedMessage("ui.admin.user.action.continue",
                                                                 BUNDLE_NAME));

    Label USER_DELETE_LABEL = new Label(new GlobalizedMessage("ui.admin.user.delete.label",
                                                              BUNDLE_NAME));

    Label USER_BAN_LABEL = new Label(new GlobalizedMessage("ui.admin.user.ban.label",
                                                           BUNDLE_NAME));

    Label USER_UNBAN_LABEL = new Label(new GlobalizedMessage("ui.admin.user.unban.label",
                                                             BUNDLE_NAME));

    GlobalizedMessage USER_DELETE_CONFIRMATION = new GlobalizedMessage(
        "ui.admin.user.delete.confirm", BUNDLE_NAME);

    GlobalizedMessage USER_BAN_CONFIRMATION = new GlobalizedMessage("ui.admin.user.ban.confirm",
                                                                    BUNDLE_NAME);

    GlobalizedMessage USER_UNBAN_CONFIRMATION = new GlobalizedMessage("ui.admin.user.unban.confirm",
                                                                      BUNDLE_NAME);

    GlobalizedMessage USER_DELETE_FAILED_MSG = new GlobalizedMessage(
        "ui.admin.user.delete.failed.label", BUNDLE_NAME);

    Label USER_TAB_EXTREME_ACTION_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.browsepanel.extremeaction",
        BUNDLE_NAME));

    Label UPDATE_USER_PASSWORD_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.user.browsepanel.updatePassword",
        BUNDLE_NAME));

    Label BECOME_USER_LABEL = new Label(
        new GlobalizedMessage("ui.admin.user.browsepanel.becomeUser",
                              BUNDLE_NAME));

    /**
     * Create new user panel.
     */
    Label CREATE_USER_PANEL_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.user.createpanel.header",
        BUNDLE_NAME));

    /**
     * User search panel.
     */
    Label SEARCH_PANEL_HEADER = new Label(new GlobalizedMessage("ui.admin.user.search.header",
                                                                BUNDLE_NAME));

    Label PASSWORD_FORM_LABEL_PASSWORD = new Label(new GlobalizedMessage(
        "ui.admin.user.userpasswordform.passwordlabel",
        BUNDLE_NAME));

    Label PASSWORD_FORM_LABEL_CONFIRMATION_PASSWORD = new Label(new GlobalizedMessage(
        "ui.admin.user.userpasswordform.confirmpasswordlabel",
        BUNDLE_NAME));

    Label PASSWORD_FORM_LABEL_QUESTION = new Label(new GlobalizedMessage(
        "ui.admin.user.userpasswordform.question",
        BUNDLE_NAME), false);

    Label PASSWORD_FORM_LABEL_ANSWER = new Label(new GlobalizedMessage(
        "ui.admin.user.userpasswordform.answer",
        BUNDLE_NAME), false);

    GlobalizedMessage PASSWORD_FORM_SUBMIT = new GlobalizedMessage(
        "ui.admin.user.userpasswordform.submit",
        BUNDLE_NAME);

    /**
     * Constants for user add/edit form.
     */
    String USER_FORM_ADD = "user-add-form";
    String USER_FORM_EDIT = "user-edit-form";
    String USER_FORM_INPUT_FIRST_NAME = "firstname";
    String USER_FORM_INPUT_LAST_NAME = "lastname";
    String USER_FORM_INPUT_PASSWORD = "password";
    String USER_FORM_INPUT_PASSWORD_CONFIRMATION = "password_confirmation";
    String USER_FORM_INPUT_QUESTION = "question";
    String USER_FORM_INPUT_ANSWER = "answer";
    String USER_FORM_INPUT_PRIMARY_EMAIL = "email";
    String USER_FORM_INPUT_ADDITIONAL_EMAIL = "additional_email";
    String USER_FORM_INPUT_SCREEN_NAME = "screenname";
    String USER_FORM_INPUT_SSO = "sso_login";
    String USER_FORM_INPUT_URL = "url";
    String USER_FORM_INPUT_URL_DEFAULT = "http://";

    Label USER_FORM_LABEL_FIRST_NAME = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.firstname",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_LAST_NAME = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.lastname",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_PASSWORD = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.password",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_PASSWORD_CONFIRMATION = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.confirmation",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_QUESTION = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.question",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_ANSWER = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.answer",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_PRIMARY_EMAIL = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.primaryemail",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_ADDITIONAL_EMAIL = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.additionalemail",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_ADDITIONAL_EMAIL_LIST = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.additionalemaillist",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_SCREEN_NAME = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.screenname",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_SSO = new Label(new GlobalizedMessage(
        "ui.admin.user.addeditform.ssologinname",
        BUNDLE_NAME));

    Label USER_FORM_LABEL_URL = new Label(new GlobalizedMessage("ui.admin.user.addeditform.url",
                                                                BUNDLE_NAME));

    Label USER_FORM_DELETE_ADDITIONAL_EMAIL = new Label(
        new GlobalizedMessage("ui.admin.user.addeditform.deleteemail",
                              BUNDLE_NAME));

    GlobalizedMessage USER_FORM_SUBMIT = new GlobalizedMessage("ui.admin.user.addeditform.submit",
                                                               BUNDLE_NAME);

    GlobalizedMessage USER_FORM_ERROR_SCREEN_NAME_NOT_UNIQUE = new GlobalizedMessage(
        "ui.admin.user.addeditform.error.screenname.notunique",
        BUNDLE_NAME);

    GlobalizedMessage USER_FORM_ERROR_PRIMARY_EMAIL_NOT_UNIQUE = new GlobalizedMessage(
        "ui.admin.user.addeditform.error.primaryemail.notunique",
        BUNDLE_NAME);

    GlobalizedMessage USER_FORM_ERROR_PASSWORD_NOT_MATCH = new GlobalizedMessage(
        "ui.admin.user.addeditform.error.password.notmatch",
        BUNDLE_NAME);

    GlobalizedMessage USER_FORM_ERROR_ANSWER_NULL = new GlobalizedMessage(
        "ui.admin.user.addeditform.error.answer.null",
        BUNDLE_NAME);

    GlobalizedMessage USER_FORM_ERROR_ANSWER_WHITESPACE = new GlobalizedMessage(
        "ui.admin.user.addeditform.error.answer.whitespace",
        BUNDLE_NAME);

    /**
     * Constants for group add/edit form.
     */
    String GROUP_FORM_ADD = "group-add-form";
    String GROUP_FORM_EDIT = "group-edit-form";
    String GROUP_FORM_INPUT_NAME = "name";
    String GROUP_FORM_INPUT_PRIMARY_EMAIL = "email";

    Label GROUP_FORM_LABEL_NAME = new Label(new GlobalizedMessage(
        "ui.admin.groups.addeditform.namelabel",
        BUNDLE_NAME));

    Label GROUP_FORM_LABEL_PRIMARY_EMAIL = new Label(new GlobalizedMessage(
        "ui.admin.groups.addeditform.primaryemaillabel",
        BUNDLE_NAME));

    GlobalizedMessage GROUP_FORM_SUBMIT
                      = new GlobalizedMessage("ui.admin.groups.addeditform.submit", BUNDLE_NAME);

    /**
     * Constants for group administration tab.
     */
    Label GROUP_ACTION_CONTINUE = new Label(new GlobalizedMessage("ui.admin.groups.actioncontinue",
                                                                  BUNDLE_NAME));

    GlobalizedMessage GROUP_DELETE_FAILED_MSG = new GlobalizedMessage(
        "ui.admin.groups.groupdeletefailed",
        BUNDLE_NAME);

    Label GROUP_INFORMATION_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.groups.groupinformation",
        BUNDLE_NAME));

    Label SUBGROUP_HEADER = new Label(new GlobalizedMessage("ui.admin.groups.subgroups",
                                                            BUNDLE_NAME));

    Label GROUP_EDIT_HEADER = new Label(new GlobalizedMessage("ui.admin.groups.groupedit",
                                                              BUNDLE_NAME));
    Label ADD_SUBGROUP_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.add",
                                                               BUNDLE_NAME));

    Label SUBMEMBER_HEADER = new Label(new GlobalizedMessage("ui.admin.groups.submembers",
                                                             BUNDLE_NAME));

    Label DELETE_GROUP_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.delete",
                                                               BUNDLE_NAME));

    Label GROUP_EXTREME_ACTIONS_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.groups.extremeaction",
        BUNDLE_NAME));

    Label GROUP_DELETE_FAILED_HEADER = new Label(new GlobalizedMessage(
        "ui.admin.groups.deletefailed",
        BUNDLE_NAME));

    Label ADD_GROUP_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.addgrouplabel",
                                                            BUNDLE_NAME));
    Label EDIT_GROUP_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.edit",
                                                             BUNDLE_NAME));

    Label SUBGROUP_COUNT_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.groups.subgroupcountlabel",
        BUNDLE_NAME));
    String GROUP_DELETE_CONFIRMATION = "Are you sure you want to delete this group?";

    Label ADD_SUBMEMBER_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.addsubmemberlabel",
                                                                BUNDLE_NAME));

    Label REMOVE_SUBMEMBER_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.groups.removesubmemberlabel",
        BUNDLE_NAME));
    Label ADD_EXISTING_GROUP_TO_SUBGROUPS_LABEL = new Label(new GlobalizedMessage(
        "ui.admin.groups.addExisting",
        BUNDLE_NAME));

    Label REMOVE_SUBGROUP_LABEL = new Label(new GlobalizedMessage("ui.admin.groups.removeExisting",
                                                                  BUNDLE_NAME));

    Label GROUP_SEARCH_LABEL = new Label(
        new GlobalizedMessage("ui.admin.groups.search", BUNDLE_NAME));

    GlobalizedMessage SEARCH_BUTTON = new GlobalizedMessage("ui.admin.groups.button.search",
                                                            BUNDLE_NAME);

    Label GROUP_NO_RESULTS = new Label(new GlobalizedMessage("ui.admin.groups.searchForm.noResults",
                                                             BUNDLE_NAME));

    Label FOUND_GROUPS_TITLE = new Label(new GlobalizedMessage("ui.admin.groups.found.title",
                                                               BUNDLE_NAME));

    Label PICK_GROUPS = new Label(new GlobalizedMessage("ui.admin.groups.select.explanation",
                                                        BUNDLE_NAME));

    GlobalizedMessage SAVE_BUTTON = new GlobalizedMessage("ui.admin.save", BUNDLE_NAME);

    String SEARCH_QUERY = "query";

    public final static String ADMIN_PAGE_URL = "/admin/";
    
    public final static String ADMIN_SERVLET = "/admin/*";
    
    
}
