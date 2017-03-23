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
import javax.faces.bean.RequestScoped;

/**
 * This class provides access to the file (local) system. If available an
 * implementation of the {@link FileSystemAdapter} interface is used. The
 * implementations of {@link FileSystemAdapter} provide a (transaction) safe way
 * to access the local file system. If no implementation of
 * {@link FileSystemAdapter} is available this class will use
 * {@link java.nio.file.Path} etc. as fallback. Depending on your application
 * server access to the local file system using these classes may fail. For
 * information about how to deploy and configure a specific implementation of
 * {@link FileSystemAdapter} please refer the the documentation of the
 * implementation.
 *
 * The method in this class encapsulate the details of the access to the local
 * file system. Therefore the paths to the files to read are usually provided as
 * strings.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CcmFiles {

    /**
     * Creates a {@link Reader} for the provided {@code path}.
     *
     * @param path
     * @return
     * @throws org.libreccm.files.FileDoesNotExistException
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public Reader createReader(final String path)
            throws FileDoesNotExistException,
                   FileAccessException,
                   InsufficientPermissionsException {

        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link Writer} for the provided {@code path}.
     *
     * @param path
     * @return
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public Writer createWriter(final String path)
            throws FileAccessException, InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link InputStream} for the provided {@code path}.
     *
     * @param path
     * @return
     * @throws org.libreccm.files.FileDoesNotExistException
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public InputStream createInputStream(final String path)
            throws FileDoesNotExistException,
                   FileAccessException,
                   InsufficientPermissionsException {

        throw new UnsupportedOperationException();
    }

    /**
     * Creates a {@link OutputStream} for the provided {@code path}.
     *
     * @param path
     * @return
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public OutputStream createOutputStream(final String path)
            throws FileAccessException, InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if a file exists.
     *
     * @param path
     * @return {@code true} if a file with the provided {@code path} exists,
     * {@code false} otherwise.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public boolean existsFile(final String path)
            throws FileAccessException, InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * checks if the provided path points to a directory.
     *
     * @param path
     * @return {@code true} if the the file to which the provided path points
     * exists and is a directory, {@code false} otherwise.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.InsufficientPermissionsException
     * @throws org.libreccm.files.FileDoesNotExistException
     */
    public boolean isDirectory(final String path)
            throws FileAccessException,
                   FileDoesNotExistException,
                   InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * Create a directory at the provided path.
     *
     * @param path The path of the new directory.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.FileAlreadyExistsException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public void createDirectory(final String path)
            throws FileAccessException,
                   FileAlreadyExistsException,
                   InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * List the files in a directory.
     *
     * @param path The {@code path} of the directory.
     * @return A list of the names of the files in the directory.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.FileDoesNotExistException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public List<String> listFiles(final String path)
            throws FileAccessException,
                   FileDoesNotExistException,
                   InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete a file or directory. If the file is a directory the directory must
     * be empty.
     *
     * @param path The path of the file to delete.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.FileDoesNotExistException
     * @throws org.libreccm.files.DirectoryNotEmptyException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public void deleteFile(final String path)
            throws FileAccessException,
                   FileDoesNotExistException,
                   DirectoryNotEmptyException,
                   InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }

    /**
     * Delete a file or directory.
     *
     * @param path
     * @param recursively Delete directories recursively.
     * @throws org.libreccm.files.FileAccessException
     * @throws org.libreccm.files.FileDoesNotExistException
     * @throws org.libreccm.files.InsufficientPermissionsException
     */
    public void deleteFile(final String path, final boolean recursively)
            throws FileAccessException,
                   FileDoesNotExistException,
                   InsufficientPermissionsException {
        throw new UnsupportedOperationException();
    }
}
