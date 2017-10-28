/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.l10n.GlobalizationHelper;

import javax.inject.Inject;

/**
 * Contains injection points for all CDI beans by the AdminView and its
 * components.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class AdminViewController {

    @Inject
    private ConfigurationsTabController confTabController;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private JpqlConsoleController jpqlConsoleController;

    @Inject
    private UsersGroupsRolesController usersGroupsRolesController;

    protected AdminViewController() {
        super();
    }

    public ConfigurationsTabController getConfTabController() {
        return confTabController;
    }

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

    protected JpqlConsoleController getJpqlConsoleController() {
        return jpqlConsoleController;
    }

    protected UsersGroupsRolesController getUsersGroupsRolesController() {
        return usersGroupsRolesController;
    }

}
