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
package com.arsdigita.ui.login;

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Constants used by Login UI.
 *
 */
public interface LoginConstants {

    public static final String LOGIN_APP_TYPE = "com.arsdigita.ui.Login";
    
    public static final String LOGIN_BUNDLE
                               = "com.arsdigita.ui.login.LoginResources";
    
    public static final String LOGIN_UI_BUNDLE
                               = "org.libreccm.ui.LoginBundle";

    public static final GlobalizedMessage SUBMIT = LoginHelper.getMessage(
        "login.submit");
    public static final GlobalizedMessage PRIMARY_EMAIL = LoginHelper
        .getMessage("login.primaryEmail");
    public static final GlobalizedMessage ADDITIONAL_EMAIL = LoginHelper
        .getMessage("login.additionalEmail");
    public static final GlobalizedMessage SCREEN_NAME = LoginHelper.getMessage(
        "login.screenName");
    public static final GlobalizedMessage FIRST_NAME = LoginHelper.getMessage(
        "login.firstName");
    public static final GlobalizedMessage LAST_NAME = LoginHelper.getMessage(
        "login.lastName");
    public static final GlobalizedMessage PASSWORD = LoginHelper.getMessage(
        "login.password", new Object[]{
            PasswordValidationListener.MIN_LENGTH});
    public static final GlobalizedMessage PASSWORD_CONFIRMATION = LoginHelper
        .getMessage("login.passwordConfirm");
    public static final GlobalizedMessage PASSWORD_QUESTION = LoginHelper
        .getMessage("login.passwordQuestion");
    public static final GlobalizedMessage PASSWORD_ANSWER = LoginHelper
        .getMessage("login.passwordAnswer");
    public static final GlobalizedMessage URL_MSG = LoginHelper.getMessage(
        "login.url");
    public static final GlobalizedMessage BIO = LoginHelper.getMessage(
        "login.bio");

    public static final GlobalizedMessage ERROR_DUPLICATE_SN = LoginHelper
        .getMessage("login.error.duplicateScreenName");
    public static final GlobalizedMessage ERROR_DUPLICATE_EMAIL = LoginHelper
        .getMessage("login.error.duplicateEmail");
    public static final GlobalizedMessage ERROR_MISMATCH_PASSWORD = LoginHelper
        .getMessage("login.error.mismatchPassword");
    public static final GlobalizedMessage ERROR_BAD_PASSWORD = LoginHelper
        .getMessage("login.error.badPassword");

    public static final GlobalizedMessage ERROR_LOGIN_FAIL = LoginHelper
        .getMessage("login.error.loginFail");

    public static final GlobalizedMessage ERROR_BAD_ANSWER = LoginHelper
        .getMessage("login.error.badAnswer");
    public static final GlobalizedMessage ERROR_BAD_EMAIL = LoginHelper
        .getMessage("login.error.badEmail");
    public static final GlobalizedMessage ERROR_BANNED_EMAIL = LoginHelper
        .getMessage("login.error.bannedEmail");

    public static final String FORM_EMAIL = "emailAddress";
    public static final String FORM_SCREEN_NAME = "screenName";

    // Should not really be named email. Kept this way due to external tests 
    // depending on this value.
    public static final String FORM_LOGIN = "email";

    public static final String FORM_ADDITIONAL_EMAIL = "additional_email";
    public static final String FORM_FIRST_NAME = "firstname";
    public static final String FORM_LAST_NAME = "lastname";
    public static final String FORM_GIVEN_NAME = "givenName";
    public static final String FORM_FAMILY_NAME = "familyName";
    public static final String FORM_USER_NAME = "username";
    public static final String FORM_PASSWORD = "password";
    public static final String FORM_PASSWORD_CONFIRMATION
                                   = "password_confirmation";
    public static final String FORM_PASSWORD_QUESTION = "question";
    public static final String FORM_PASSWORD_ANSWER = "answer";
    public static final String FORM_URL = "url";
    public static final String FORM_URL_DEFAULT = "http://";
    public static final String FORM_BIO = "biography";
    public static final String FORM_TIMESTAMP = "timestamp";
    public static final String FORM_PERSISTENT_LOGIN_P = "persistentCookieP";
    public static final String FORM_PERSISTENT_LOGIN_P_DEFAULT = "1";

    public static final int TIMESTAMP_LIFETIME_SECS = 300;
    public static final int MAX_NAME_LEN = 60;

    /**
     * URL_MSG stub of Login page in ServletPath format (with leading slash and
     * without trailing slash
     */
    // Don't modify without adapting instantiation in Loader class and 
    // updating existing databases (table applications)!
    public static final String LOGIN_PAGE_URL = "/register/";
    
    public static final String LOGIN_PATH = "/register";

    public static final String LOGIN_SERVLET_PATH = "/login/*";

}
