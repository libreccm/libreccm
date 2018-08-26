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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;

import javax.enterprise.inject.Instance;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides access to the (local) file system. If available an
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
 * The method in this class encapsulates the details of the access to the local
 * file system. Therefore the paths to the files to read or write are usually
 * provided as strings.
 *
 * This class does not provide access to the complete file system of the server.
 * Instead a directory has been configured before using this class. The methods
 * in this class will interpret the paths provided relative to this directory.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CcmFiles {

    private static final Logger LOGGER = LogManager.getLogger(CcmFiles.class);

    @Inject
    private Instance<FileSystemAdapter> fileSystemAdapters;

    @Inject
    private ConfigurationManager confManager;

    /**
     * A helper method for getting the {@link FileSystemAdapter} implementation
     * to use.
     *
     * @return The implementation of the {@link FileSystemAdapter} interface to
     * use. If no other implementation is available the default implementation
     * {@link NIOFileSystemAdapter}.
     */
    private FileSystemAdapter getFileSystemAdapter() {

        LOGGER.debug("Trying to find FileSystemAdapter...");

        final List<FileSystemAdapter> adapters = new ArrayList<>();
        for (FileSystemAdapter fileSystemAdapter : fileSystemAdapters) {
            adapters.add(fileSystemAdapter);
        }

        if (adapters.isEmpty()) {
            throw new UnexpectedErrorException(
                "No FileSystemAdapters available.");
        } else if (adapters.size() > 1) {
            if (adapters.size() == 2) {
                final FileSystemAdapter adapter = adapters
                    .stream()
                    .filter(adap -> {
                        return !NIOFileSystemAdapter.class.getName()
                            .equals(adap.getClass().getName());
                    })
                    .findAny()
                    .get();

                if (adapter.isConfigured()) {
                    LOGGER.debug("Found correctly configured "
                                     + "FileSystemAdapter '{}'.",
                                 adapter.getClass().getName());
                    return adapter;
                } else {
                    LOGGER.warn("A FileSystemAdapter ({}) is available but not "
                                    + "correctly configured. Falling back to default "
                                + "adapter '{}'.",
                                adapter.getClass().getName(),
                                NIOFileSystemAdapter.class.getName());
                    return adapters
                        .stream()
                        .filter(adap -> {
                            return NIOFileSystemAdapter.class.getName()
                                .equals(adap.getClass().getName());

                        })
                        .findAny()
                        .get();
                }
            } else {
                LOGGER.debug("Multiple FileSystemAdapters are available.");
                final CcmFilesConfiguration filesConf = confManager
                    .findConfiguration(CcmFilesConfiguration.class);
                final String activeFileSystemAdapterClassName = filesConf
                    .getActiveFileSystemAdapter();
                if (activeFileSystemAdapterClassName == null
                        || activeFileSystemAdapterClassName.trim().isEmpty()) {

                    throw new UnexpectedErrorException(
                        "Multiple implementations of the FileSystemAdapter "
                            + "interface are available but "
                            + "activeFileSystemAdapter is not set. Please set "
                            + "the FileSystemAdapter to use.");
                }

                final FileSystemAdapter adapter = adapters
                    .stream()
                    .filter(adap -> {
                        return activeFileSystemAdapterClassName
                            .equals(adap.getClass().getName());
                    })
                    .findAny()
                    .orElseThrow(() -> new UnexpectedErrorException(
                    String.format(
                        "activeFileSystemAdapter set to '%s' but there is no "
                            + "implementation with that class name available.",
                        activeFileSystemAdapterClassName)));

                if (adapter.isConfigured()) {
                    return adapter;
                } else {
                    throw new UnexpectedErrorException(String.format(
                        "Active FileSystemAdapter '%s' is not configured "
                            + "correctly.",
                        adapter.getClass().getName()));
                }
            }
        } else {
            LOGGER.debug("Only one FileSystemAdapter is avaiable.");
            final FileSystemAdapter adapter = adapters.get(0);
            if (NIOFileSystemAdapter.class.getName().equals(adapter.getClass()
                .getName())) {
                LOGGER.warn("Only the default FileSystemAdapter '{}' which "
                                + "accesses the file system directly is available. It is"
                            + "strongly recommanded to install and configure a another "
                            + "FileSystemAdapter which accesses the file system in a way"
                            + "which complies to the Java EE specification.");
            }
            if (adapter.isConfigured()) {
                return adapter;
            } else {
                throw new UnexpectedErrorException(
                    "Only the default FileSystemAdapter is available but is "
                        + "not correctly configured.");
            }
        }
    }

    /**
     * A helper method to create the path relative the the path of the data
     * directory This method also normalises the path by removing or adding
     * slashes at the end or beginning of the string as necessary.
     *
     * @param path The path the transform into a path relative to the data
     * directories path.
     * @return The absolute path.
     */
    private String getDataPath(final String path) {
        final StringBuilder builder = new StringBuilder();

        final CcmFilesConfiguration filesConf = confManager
            .findConfiguration(CcmFilesConfiguration.class);
        final String dataPath = filesConf.getDataPath();

        if (dataPath == null || dataPath.trim().isEmpty()) {
            throw new UnexpectedErrorException("dataPath is not configured.");
        }

        if (dataPath.endsWith("/")) {
            builder.append(dataPath.substring(0, dataPath.length() - 1));
        } else {
            builder.append(dataPath);
        }

        builder.append("/");

        if (path.startsWith("/")) {
            builder.append(path.substring(1));
        } else {
            builder.append(path);
        }

        return builder.toString();
    }

    /**
     * Creates a {@link Reader} for the provided {@code path}.
     *
     * @param path The path of the file.
     *
     * @return A {@link Reader} for the file identified by the provided
     * {@code path}.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public Reader createReader(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().createReader(getDataPath(path));
    }

    /**
     * Creates a {@link Writer} for the provided {@code path}.
     *
     * @param path The path.
     *
     * @return A {@link Writer} for the file identified by {@code path}.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public Writer createWriter(final String path)
        throws FileAccessException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().createWriter(getDataPath(path));
    }

    /**
     * Creates a {@link InputStream} for the provided {@code path}.
     *
     * @param path
     *
     * @return
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public InputStream createInputStream(final String path)
        throws FileDoesNotExistException,
               FileAccessException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().createInputStream(getDataPath(path));
    }

    /**
     * Creates a {@link OutputStream} for the provided {@code path}.
     *
     * @param path
     *
     * @return
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public OutputStream createOutputStream(final String path)
        throws FileAccessException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().createOutputStream(getDataPath(path));
    }

    /**
     * Checks if a file exists.
     *
     * @param path
     *
     * @return {@code true} if a file with the provided {@code path} exists,
     * {@code false} otherwise.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public boolean existsFile(final String path)
        throws FileAccessException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().existsFile(getDataPath(path));
    }

    /**
     * checks if the provided path points to a directory.
     *
     * @param path
     *
     * @return {@code true} if the the file to which the provided path points
     * exists and is a directory, {@code false} otherwise.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public boolean isDirectory(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().isDirectory(getDataPath(path));
    }

    /**
     * Create a directory at the provided path.
     *
     * @param path The path of the new directory.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileAlreadyExistsException If the requested file does not exist.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public void createDirectory(final String path)
        throws FileAccessException,
               FileAlreadyExistsException,
               InsufficientPermissionsException {

        getFileSystemAdapter().createDirectory(getDataPath(path));
    }

    /**
     * List the files in a directory.
     *
     * @param path The {@code path} of the directory.
     *
     * @return A list of the names of the files in the directory.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public List<String> listFiles(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException {

        return getFileSystemAdapter().listFiles(getDataPath(path));
    }
    
    public String getMimeType(final String path) throws FileAccessException {
        
        return getFileSystemAdapter().getMimeType(path);
    }

    public void copyFile(final String sourcePath, 
                    final String targetPath) throws FileAccessException {
        
         getFileSystemAdapter().copy(sourcePath, targetPath, false);
    }
    
    public void copyFile(final String sourcePath, 
                    final String targetPath,
                    final boolean recursive) throws FileAccessException{
        
         getFileSystemAdapter().copy(sourcePath, targetPath, recursive);
    }
    
    /**
     * Delete a file or directory. If the file is a directory the directory must
     * be empty.
     *
     * @param path The path of the file to delete.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws DirectoryNotEmptyException If the file to delete is a non empty
     * directory. To delete a directory recursively use
     * {@link #deleteFile(java.lang.String, boolean)}.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public void deleteFile(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               DirectoryNotEmptyException,
               InsufficientPermissionsException {

        getFileSystemAdapter().deleteFile(getDataPath(path));
    }

    /**
     * Delete a file or directory.
     *
     * @param path
     * @param recursively Delete directories recursively.
     *
     * @throws FileAccessException If an error not covered by other exceptions
     * occurs.
     * @throws FileDoesNotExistException If the requested file does not exist.
     * @throws DirectoryNotEmptyException If the directory is not empty
     * <em>and</em> {@code recursively} is set to {@code false}.
     * @throws InsufficientPermissionsException If the user which runs the
     * application server does not have the permission to access the requested
     * file.
     */
    public void deleteFile(final String path, final boolean recursively)
        throws FileAccessException,
               FileDoesNotExistException,
               DirectoryNotEmptyException,
               InsufficientPermissionsException {

        getFileSystemAdapter().deleteFile(getDataPath(path), recursively);
    }

}
