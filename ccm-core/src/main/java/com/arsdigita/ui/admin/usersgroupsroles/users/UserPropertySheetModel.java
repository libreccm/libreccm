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

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;

import java.util.Arrays;
import java.util.Iterator;

import org.libreccm.security.User;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class UserPropertySheetModel implements PropertySheetModel {

    private static enum UserProperty {
        USER_NAME,
        FAMILY_NAME,
        GIVEN_NAME,
        PASSWORD_SET,
        BANNED,
        PASSWORD_RESET_REQUIRED
    }

    private final User selectedUser;
    private final Iterator<UserProperty> propertyIterator;
    private UserProperty currentProperty;

    public UserPropertySheetModel(final User selectedUser) {
        this.selectedUser = selectedUser;
        propertyIterator = Arrays.asList(UserProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (selectedUser == null) {
            return false;
        }

        if (propertyIterator.hasNext()) {
            currentProperty = propertyIterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getLabel() {
        return currentProperty.toString();
    }

    @Override
    public GlobalizedMessage getGlobalizedLabel() {
        return generateGlobalizedLabel(currentProperty);
    }

    private GlobalizedMessage generateGlobalizedLabel(
        final UserProperty property) {
        final String key = String.join("", "ui.admin.user.property_sheet.",
                                       property.toString().toLowerCase());
        return new GlobalizedMessage(key, ADMIN_BUNDLE);
    }

    @Override
    public String getValue() {
        switch (currentProperty) {
            case USER_NAME:
                return selectedUser.getName();
            case FAMILY_NAME:
                return selectedUser.getFamilyName();
            case GIVEN_NAME:
                return selectedUser.getGivenName();
            case PASSWORD_SET:
                return Boolean.toString(
                    (selectedUser.getPassword() != null
                     && !selectedUser.getPassword().isEmpty()));
            case BANNED:
                return Boolean.toString(selectedUser.isBanned());
            case PASSWORD_RESET_REQUIRED:
                return Boolean.toString(selectedUser.isPasswordResetRequired());
            default:
                return "";
        }
    }
}
