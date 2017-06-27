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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.libreccm.cdi.utils.CdiUtil;

import org.libreccm.security.PermissionChecker;

import java.util.Collections;

/**
 * <p>
 * Wrapper class that registers access checks (actions) to a Bebop
 * component.</p>
 *
 * @author Michael Pih
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ComponentAccess {

    private Component component;
    private List<String> accessCheckList;

    /**
     * @param component The component
     */
    public ComponentAccess(final Component component) {
        accessCheckList = new ArrayList<>();
        this.component = component;
    }

    /**
     * @param component The component
     * @param check An access check
     */
    public ComponentAccess(final Component component, final String check) {
        this(component);
        accessCheckList.add(check);
    }

    /**
     * Add an access check to this component.
     *
     * @param check The access check
     */
    public void addAccessCheck(final String check) {
        accessCheckList.add(check);
    }

    /**
     * Get the access checks.
     *
     * @return The list of access checks
     */
    public List<String> getAccessCheckList() {
        return Collections.unmodifiableList(accessCheckList);
    }

    /**
     * Get the component.
     *
     * @return The component
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Do all the access checks registered to the component pass?
     *
     * @return true if all the access checks pass, false otherwise
     */
    public boolean canAccess() {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

        if (accessCheckList.isEmpty()) {
            return true;
        }
        
        final Optional<Boolean> canAccess = accessCheckList.stream()
                .map(accessCheck -> permissionChecker.isPermitted(accessCheck))
                .reduce((result1, result2) -> result1 && result2);

        return canAccess.orElse(false);
    }
    
    public boolean canAccess(final PageState state) {
        return canAccess();
    }

}
