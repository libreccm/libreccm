/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
