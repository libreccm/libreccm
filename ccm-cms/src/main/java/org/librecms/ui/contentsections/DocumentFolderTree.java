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
package org.librecms.ui.contentsections;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * The document folder tree of a {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class DocumentFolderTree
    extends AbstractFolderTree<DocumentFolderTreeNode, DocumentPermissionsModel> {

    /**
     * {@link DocumentPermissions} instance used to check permissions on the
     * documents.
     */
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
