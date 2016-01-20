/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.docrepo;

import org.apache.log4j.Logger;
import org.apache.oro.text.perl.Perl5Util;

import javax.activation.MimetypesFileTypeMap;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Manager class for complex operations on {@code Resource}-objects.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@RequestScoped
public class ResourceManager {
    private static final Logger log = Logger.getLogger(ResourceManager.class);

    @Inject
    private ResourceRepository resourceRepository;

    /**
     * Copies a given {@link File} to a given {@link Folder}.
     *
     * @param original The {@link Resource} to be copied
     * @param folder The {@link Folder} to copy to
     */
    public void copyToFolder(Resource original, Folder folder) {
        Resource copy = original instanceof File ?
                copyFileSpecifics(new File(), (File) original) :
                copyFolderSpecifics(new Folder(), (Folder) original);

        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setPath(String.format("%s/%s", folder.getPath(), copy.getName()));
        copy.setMimeType(original.getMimeType());
        copy.setSize(original.getSize());
        copy.setCreationDate(original.getCreationDate());
        copy.setLastModifiedDate(original.getLastModifiedDate());
        copy.setCreationIp(original.getCreationIp());
        copy.setLastModifiedIp(original.getLastModifiedIp());
        copy.setCreationUser(original.getCreationUser());
        copy.setLastModifiedUser(original.getLastModifiedUser());
        copy.setParent(folder);
        copy.setRepository(folder.getRepository());

        resourceRepository.save(copy);
    }

    /**
     * Helper method to copy the {@link File} specific data to the copy.
     *
     * @param copy The copied {@link File}
     * @param original The originl {@link File}
     * @return  A {@link Resource} with the file-specific data from the
     *          original {@link File}
     */
    private Resource copyFileSpecifics(File copy, File original) {
        copy.setContent(original.getContent());
        return copy;
    }

    /**
     * Helper method to copy the {@link Folder} specific data to the copy.
     *
     * @param copy The copied {@link Folder}
     * @param original The originl {@link Folder}
     * @return  A {@link Resource} with the folder-specific data from the
     *          original {@link Folder}
     */
    private Resource copyFolderSpecifics(Folder copy, Folder original) {
        copy.setImmediateChildren(original.getImmediateChildren());
        copy.setRootAssignedRepository(original.getRootAssignedRepository());
        return copy;
    }



    /**
     * Determines weather the given name is a valid new name for the also
     * given {@link Resource}.
     *
     * Verifies that the string only contains valid characters for
     * {@link Resource} names.  The following name patterns are allowed:
     *
     *    [a-z][A-Z][0-9][-., ]
     *
     * In addition, names cannot begin with ".", i.e. we do NOT support file
     * names like ".profile" at the moment. The current implementation does
     * not allow international characters for resource names.
     *
     * @param name The resource name for validation
     * @param resource The resource for which the new name needs to be validated
     *
     * @return true for a system-valid resource name, otherwise false
     */
    public boolean isValidNewResourceName(String name, Resource resource) {
        Perl5Util perl5Util = new Perl5Util();

        final String INVALID_START_PATTERN = "/^[.]+/";
        final String INVALID_NAME_PATTERN = "/[^a-zA-Z0-9\\_\\.\\-\\ ]+/";
        final String EXTENSION_PATTERN = "/^([^\\.].*)(\\.\\w+)$/";

        // adds an extension if non-existent
        if (!perl5Util.match(EXTENSION_PATTERN, name)) {
            if (perl5Util.match(EXTENSION_PATTERN, resource.getName())) {
                name += perl5Util.group(2);
            }
        }

        // checks pattern of the name
        boolean validName = !(perl5Util.match(INVALID_START_PATTERN, name) ||
                                perl5Util.match(INVALID_NAME_PATTERN, name));

        // checks duplication of the name; database access (mind performance)
        validName &= resourceRepository.findByName(name).isEmpty();

        // checks that the name corresponds to a compatible MIME type
        validName &= new MimetypesFileTypeMap().getContentType(name).equals
                (resource.getMimeType().toString());

        return validName;
    }
}
