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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

import org.libreccm.security.Party;

import com.arsdigita.toolbox.ui.SecurityContainer;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * Security container that wraps the canAdministerLifecycles access check around
 * its components.
 *
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LifecycleAdminContainer extends SecurityContainer {

    /**
     * This default constructor should be followed by calls to <code>add</code>.
     */
    public LifecycleAdminContainer() {
        super();
    }

    /**
     * Create a <code>SecurityContainer</code> around a child component.
     *
     * @param component The child component
     */
    public LifecycleAdminContainer(final Component component) {
        super(component);
    }

    /**
     * Returns true if the current user can access the child component.
     *
     * @param state The page state
     *
     * @return true if the access checks pass, false otherwise
     */
    @Override
    protected boolean canAccess(final Party party, final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES);
    }

}
