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
package org.libreccm.files;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface FileSystemAdapter {

    /**
     * Tests if all resources (eg JNDI resources) etc required by an
     * implementation of the {@link FileSystemAdapter} are available.
     *
     * @return {@code true} if all resources are available and the adapter can
     *         be used, {@code false} if not.
     */
    boolean isConfigured();

    /**
     * Creates a {@link Reader} for the provided {@code path}.
     *
     * @param path The path of the file.
     *
     * @return A {@link Reader} for the file identified by the provided
     *         {@code path}.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    Reader createReader(String path) throws FileDoesNotExistException,
                                            FileAccessException,
                                            InsufficientPermissionsException;

    /**
     * Creates a {@link Writer} for the provided {@code path}.
     *
     * @param path The path.
     *
     * @return A {@link Writer} for the file identified by {@code path}.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    Writer createWriter(String path) throws FileAccessException,
                                            InsufficientPermissionsException;

    /**
     * Creates a {@link InputStream} for the provided {@code path}.
     *
     * @param path
     *
     * @return
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    InputStream createInputStream(String path)
        throws FileDoesNotExistException,
               FileAccessException,
               InsufficientPermissionsException;

    /**
     * Creates a {@link OutputStream} for the provided {@code path}.
     *
     * @param path
     *
     * @return
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    OutputStream createOutputStream(String path)
        throws FileAccessException,
               InsufficientPermissionsException;

    /**
     * Checks if a file exists.
     *
     * @param path
     *
     * @return {@code true} if a file with the provided {@code path} exists,
     *         {@code false} otherwise.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    boolean existsFile(String path)
        throws FileAccessException,
               InsufficientPermissionsException;

    /**
     * checks if the provided path points to a directory.
     *
     * @param path
     *
     * @return {@code true} if the the file to which the provided path points
     *         exists and is a directory, {@code false} otherwise.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    boolean isDirectory(String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException;

    /**
     * Create a directory at the provided path.
     *
     * @param path The path of the new directory.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileAlreadyExistsException       If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    void createDirectory(String path)
        throws FileAccessException,
               FileAlreadyExistsException,
               InsufficientPermissionsException;

    /**
     * List the files in a directory.
     *
     * @param path The {@code path} of the directory.
     *
     * @return A list of the names of the files in the directory.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    List<String> listFiles(String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException;

    /**
     * Delete a file or directory. If the file is a directory the directory must
     * be empty.
     *
     * @param path The path of the file to delete.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws DirectoryNotEmptyException       If the file to delete is a non
     *                                          empty directory. To delete a
     *                                          directory recursively use
     *                                          {@link #deleteFile(java.lang.String, boolean)}.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    void deleteFile(String path)
        throws FileAccessException,
               FileDoesNotExistException,
               DirectoryNotEmptyException,
               InsufficientPermissionsException;

    /**
     * Delete a file or directory.
     *
     * @param path
     * @param recursively Delete directories recursively.
     *
     * @throws FileAccessException              If an error not covered by other
     *                                          exceptions occurs.
     * @throws FileDoesNotExistException        If the requested file does not
     *                                          exist.
     * @throws InsufficientPermissionsException If the user which runs the
     *                                          application server does not have
     *                                          the permission to access the
     *                                          requested file.
     */
    void deleteFile(String path, boolean recursively)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException;

}
