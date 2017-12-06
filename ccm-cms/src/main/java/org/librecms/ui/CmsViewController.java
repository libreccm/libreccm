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
package org.librecms.ui;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.librecms.pages.PageManager;
import org.librecms.pages.PageRepository;
import org.librecms.pages.PagesManager;
import org.librecms.pages.PagesRepository;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class CmsViewController {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PagesController pagesController;
    

    @Inject
    private PermissionManager permissionManager;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private ContentSectionsGridDataProvider sectionsDataProvider;

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }


    protected PagesController getPagesController() {
        return pagesController;
    }
    
    protected PermissionManager getPermissionManager() {
        return permissionManager;
    }

    protected PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    protected ContentSectionsGridDataProvider getSectionsDataProvider() {
        return sectionsDataProvider;
    }

}
