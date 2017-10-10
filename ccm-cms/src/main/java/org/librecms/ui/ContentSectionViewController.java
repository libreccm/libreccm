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
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contenttypes.ContentTypesManager;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class ContentSectionViewController {

    @Inject
    private FolderBrowserDataProvider browseDocumentsDataProvider;
    
    @Inject
    private FolderBrowserFolderTreeDataProvider folderTreeDataProvider;
    
    @Inject
    private ContentTypesManager contentTypesManager;

    @Inject
    private FolderRepository folderRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private ContentSectionRepository sectionRepository;

    @Inject
    private ContentSectionViewState contentSectionViewState;

    protected FolderBrowserDataProvider getBrowseDocumentsDataProvider() {
        return browseDocumentsDataProvider;
    }
    
    protected FolderBrowserFolderTreeDataProvider getFolderTreeDataProvider() {
        return folderTreeDataProvider;
    }
    
    protected ContentTypesManager getContentTypesManager() {
        return contentTypesManager;
    }

    protected FolderRepository getFolderRepository() {
        return folderRepository;
    }

    protected GlobalizationHelper getGlobalizationHelper() {
        return globalizationHelper;
    }

    protected PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    protected ContentSectionRepository getSectionRepository() {
        return sectionRepository;
    }

    protected ContentSectionViewState getContentSectionViewState() {
        return contentSectionViewState;
    }

    

}
