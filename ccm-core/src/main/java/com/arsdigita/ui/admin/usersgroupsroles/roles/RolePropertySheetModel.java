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
package com.arsdigita.ui.admin.usersgroupsroles.roles;

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.security.Role;

import java.util.Arrays;
import java.util.Iterator;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RolePropertySheetModel implements PropertySheetModel {

    private static enum RoleProperty {
        ROLE_NAME
    }

    private final Role selectedRole;
    private final Iterator<RoleProperty> propertyIterator;
    private RoleProperty currentProperty;

    public RolePropertySheetModel(final Role selectedRole) {
        this.selectedRole = selectedRole;
        propertyIterator = Arrays.asList(RoleProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (selectedRole == null) {
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
        final RoleProperty roleProperty) {

        final String key = String.join("",
                                       "ui.admin.role.property_sheet.",
                                       roleProperty.toString().toLowerCase());
        return new GlobalizedMessage(key, ADMIN_BUNDLE);
    }

    @Override
    public String getValue() {
        switch(currentProperty) {
            case ROLE_NAME:
                return selectedRole.getName();
            default:
                return "";
        }
    }

}
