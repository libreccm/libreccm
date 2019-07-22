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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class FolderPathListModel implements ListModel {

    private final Locale defaultLocale;

    private final Iterator<Folder> pathFolders;

    private Folder currentFolder;

    public FolderPathListModel(final Folder folder) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderManager folderManager = cdiUtil
            .findBean(FolderManager.class);
        final List<Folder> parentFolders = folderManager
            .getParentFolders(folder);
        final List<Folder> path = new ArrayList<>();
        path.addAll(parentFolders);
        path.add(folder);
        pathFolders = path.iterator();

        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        defaultLocale = kernelConfig.getDefaultLocale();
    }

    @Override
    public boolean next() {
        if (pathFolders.hasNext()) {
            currentFolder = pathFolders.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getElement() {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderPathListModelController controller = cdiUtil
            .findBean(FolderPathListModelController.class);

        return controller.getElement(currentFolder, defaultLocale);
    }

    @Override
    public String getKey() {
        return Long.toString(currentFolder.getObjectId());
    }

}
