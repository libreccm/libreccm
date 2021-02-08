/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class DocumentFolderTree 
    extends AbstractFolderTree<DocumentFolderTreeNode, DocumentPermissionsModel>{

    @Inject
    private DocumentPermissions documentPermissions;
    
    @Override
    public DocumentFolderTreeNode newFolderTreeNode() {
        return new DocumentFolderTreeNode();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Folder getRootFolder(final ContentSection section) {
        return section.getRootDocumentsFolder();
    }

    @Override
    public DocumentPermissionsModel buildPermissionsModel(final Folder folder) {
        return documentPermissions.buildDocumentPermissionsModel(folder);
    }

  
    
}
