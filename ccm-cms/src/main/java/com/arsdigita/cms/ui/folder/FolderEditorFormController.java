/*
 * Copyright (C) 2021 LibreCCM Foundation.
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

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderEditorFormController {
    
    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private FolderRepository folderRepository;
    
    @Transactional(Transactional.TxType.REQUIRED)
    public String getFolderTitle(final Folder ofFolder) {
        final Folder folder = folderRepository
        .findById(Objects.requireNonNull(ofFolder).getObjectId())
        .orElseThrow(
            () -> new IllegalArgumentException(
                String.format("No folder with ID %d available.",
                              ofFolder.getObjectId())
            )
        );
        
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class
        );
        return folder.getTitle().getValue(kernelConfig.getDefaultLocale());
    }
    
}
