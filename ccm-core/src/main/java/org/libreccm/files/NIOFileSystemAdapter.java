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

import org.libreccm.configuration.ConfigurationManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * This implementation of the {@link FileSystemAdapter} interface is used by
 * {@link CcmFiles} as a fallback if no other implementations are available.
 *
 * This adapter uses the classes from the {@code java.nio} for accessing the
 * file system directly. Using this adapter is not recommended. Operations may
 * fail due to security constraints of the application server if this adapter is
 * used.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class NIOFileSystemAdapter implements FileSystemAdapter {

    @Inject
    private ConfigurationManager confManager;

    private String dataPath;

    @PostConstruct
    private void init() {
        final CcmFilesConfiguration filesConf = confManager.findConfiguration(
            CcmFilesConfiguration.class);
        dataPath = filesConf.getDataPath();
    }

    @Override
    public boolean isConfigured() {
        return dataPath != null && !dataPath.isEmpty();
    }

    @Override
    public Reader createReader(final String path)
        throws FileDoesNotExistException,
               FileAccessException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.exists(nioPath)) {
            throw new FileDoesNotExistException(path);
        }

        if (!Files.isReadable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        final FileReader fileReader;
        try {
            fileReader = new FileReader(nioPath.toFile());
        } catch (FileNotFoundException ex) {
            throw new FileDoesNotExistException(path, ex);
        }

        return fileReader;
    }

    @Override
    public Writer createWriter(final String path)
        throws FileAccessException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.exists(nioPath)) {
            try {
                Files.createFile(nioPath);
            } catch (IOException ex) {
                throw new FileAccessException(path, ex);
            }
        }

        if (!Files.isWritable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        final FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(nioPath.toFile());
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }

        return fileWriter;
    }

    @Override
    public InputStream createInputStream(final String path) throws
        FileDoesNotExistException,
        FileAccessException,
        InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.exists(nioPath)) {
            throw new FileDoesNotExistException(path);
        }

        if (!Files.isReadable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        final FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(nioPath.toFile());
        } catch (FileNotFoundException ex) {
            throw new FileDoesNotExistException(path, ex);
        }

        return fileInputStream;
    }

    @Override
    public OutputStream createOutputStream(final String path) throws
        FileAccessException,
        InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.exists(nioPath)) {
            try {
                Files.createFile(nioPath);
            } catch (IOException ex) {
                throw new FileAccessException(path, ex);
            }
        }

        if (!Files.isWritable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        final FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(nioPath.toFile());
        } catch (FileNotFoundException ex) {
            throw new FileAccessException(path, ex);
        }

        return fileOutputStream;
    }

    @Override
    public boolean existsFile(final String path)
        throws FileAccessException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        return Files.exists(nioPath);
    }

    @Override
    public boolean isDirectory(final String path) throws FileAccessException,
                                                         FileDoesNotExistException,
                                                         InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        return Files.isDirectory(nioPath);
    }

    @Override
    public void createDirectory(final String path)
        throws FileAccessException,
               FileAlreadyExistsException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (Files.exists(nioPath)) {
            throw new FileAlreadyExistsException(path);
        }

        try {
            Files.createDirectories(nioPath);
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Override
    public List<String> listFiles(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.isDirectory(nioPath)) {
            throw new FileAccessException(path);
        }

        final Stream<Path> paths;
        try {
            paths = Files.list(nioPath);
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }

        return paths
            .map(filePath -> filePath.getFileName().toString())
            .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               DirectoryNotEmptyException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.isWritable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        try {
            if (Files.isDirectory(nioPath) && Files.list(nioPath).count() > 0) {
                throw new DirectoryNotEmptyException(path);
            }
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }

        try {
            Files.deleteIfExists(nioPath);
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Override
    public void deleteFile(final String path, final boolean recursively)
        throws FileAccessException,
               FileDoesNotExistException,
               DirectoryNotEmptyException,
               InsufficientPermissionsException {

        final Path nioPath = Paths.get(path);

        if (!Files.isWritable(nioPath)) {
            throw new InsufficientPermissionsException(path);
        }

        try {
            if (Files.isDirectory(nioPath) && Files.list(nioPath).count() > 0) {
                throw new DirectoryNotEmptyException(path);
            }
        } catch (IOException ex) {
            throw new FileAccessException(path, ex);
        }

        if (recursively && Files.isDirectory(nioPath)) {
            final List<String> files;
            try {
                files = Files
                    .list(nioPath)
                    .map(file -> file.toString())
                    .collect(Collectors.toList());
            } catch (IOException ex) {
                throw new FileAccessException(path, ex);
            }
            for (final String file : files) {
                deleteFile(file, recursively);
            }
        } else {
            deleteFile(path);
        }
    }

}
