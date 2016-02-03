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

import org.apache.oro.text.perl.Perl5Util;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;

/**
 * Manager class for complex operations on {@code AbstractResource}-objects.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
public abstract class AbstractResourceManager<T extends AbstractResource> {

    @Inject
    private FileRepository fileRepository;

    @Inject
    private FolderRepository folderRepository;

    /**
     * Makes a copy of a given {@link AbstractResource}.
     *
     * @param original  The {@link AbstractResource} to be copied
     * @param copy      The {@code copy} of the given {@code original}
     *                  {@link AbstractResource}
     */
    public void copy(T original, T copy) {
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setPath(String.format("%s/%s", original.getPath(), copy.getName()));
        copy.setMimeType(original.getMimeType());
        copy.setSize(original.getSize());
        // for file: setContent
        copy.setCreationDate(original.getCreationDate());
        copy.setLastModifiedDate(original.getLastModifiedDate());
        copy.setCreationIp(original.getCreationIp());
        copy.setLastModifiedIp(original.getLastModifiedIp());
        copy.setCreationUser(original.getCreationUser());
        copy.setLastModifiedUser(original.getLastModifiedUser());
        if (copy.getParent() == null)
            copy.setParent(original.getParent());
        copy.setRepository(original.getRepository());
        // for folder: setImmediateChildren
        // for folder: setRootAssignedRepository
    }

    /**
     * Overloads previous method and sets a new parent (aka location) before
     * calling the method to copy the {@code original} {@link AbstractResource} into
     * the {@code copy}.
     *
     * @param original  The {@link AbstractResource} to be copied
     * @param copy      The {@code copy} of the given {@code original}
     *                  {@link AbstractResource}
     * @param newParent The new parent of the copy. Equals an new location
     */
    public void copy(T original, T copy, Folder newParent) {
        copy.setParent(newParent);
        copy(original, copy);
    }

    /**
     * Determines weather the given name is a valid new name for the also
     * given {@link AbstractResource}.
     *
     * Verifies that the string only contains valid characters for
     * {@link AbstractResource} names.  The following name patterns are allowed:
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
    public boolean isValidNewResourceName(String name, T resource) {
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
        validName &= (fileRepository.findByName(name).isEmpty() &&
                     folderRepository.findByName(name).isEmpty());

        // checks that the name corresponds to a compatible MIME type
        validName &= new MimetypesFileTypeMap().getContentType(name).equals
                (resource.getMimeType().toString());

        return validName;
    }
}
