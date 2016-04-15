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
package com.arsdigita.ui.admin.usersgroupsroles.groups;

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupPropertySheetModel implements PropertySheetModel {

    private static enum GroupProperty {
        GROUP_NAME,
        ROLES
    }

    private final Group selectedGroup;
    private final Iterator<GroupProperty> propertyIterator;
    private GroupProperty currentProperty;

    public GroupPropertySheetModel(final Group selectedGroup) {
        this.selectedGroup = selectedGroup;
        propertyIterator = Arrays.asList(GroupProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (selectedGroup == null) {
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
        final GroupProperty property) {

        final String key = String.join("", "ui.admin.group.property_sheet.",
                                       property.toString().toLowerCase());
        return new GlobalizedMessage(key, ADMIN_BUNDLE);
    }

    @Override
    public String getValue() {
        switch (currentProperty) {
            case GROUP_NAME:
                return selectedGroup.getName();
            case ROLES:
                return retrieveRoles();
            default:
                return "";
        }
    }

    private String retrieveRoles() {
        final Set<RoleMembership> roleMemberships = selectedGroup
            .getRoleMemberships();
        
        final SortedSet<String> roles = new TreeSet<>((r1, r2) -> {
            return r1.compareTo(r2);
        });
        
        roleMemberships.forEach(m -> {
            roles.add(m.getRole().getName());
        });
        
        return String.join(", ", roles.toArray(new String[roles.size()]));
    }

}
