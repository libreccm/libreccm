package org.libreccm.files;

import org.libreccm.core.UnexpectedErrorException;
import org.xadisk.additional.XAFileInputStreamWrapper;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.XAFileInputStream;
import org.xadisk.bridge.proxies.interfaces.XAFileOutputStream;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

import java.io.File;
import java.io.IOException;

import javax.resource.ResourceException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.activation.MimetypesFileTypeMap;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Transactional;

/**
 * An implementation of the {@link FileSystemAdapter} which uses XADisk to
 * provides a transaction safe access to the file system.
 *
 * This {@link FileSystemAdapter} requires that XADisk.rar is deployed into the
 * application server and that a resource adapter is configured to provided
 * access to the file system. Please refer the documentation of this module for
 * more information.
 *
 * @see http://xadisk.java.net
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class XAFileSystemAdapter implements FileSystemAdapter {

    private Optional<XADiskConnectionFactory> lookupConnectionFactory() {

        final InitialContext context;
        try {
            context = new InitialContext();
        } catch (NamingException ex) {
            throw new UnexpectedErrorException(
                "Failed to create InitialContext for JNDI lookup.", ex);
        }

        final Object result;
        try {
            result = context.lookup("java:/org/libreccm/files/xadiskcf");
        } catch (NamingException ex) {
            throw new UnexpectedErrorException("Failed to lookup "
                                                   + "XAConnectionFactory at JNDI URL "
                                               + "java:/org/libreccm/files/xadiskcf",
                                               ex);
        }

        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of((XADiskConnectionFactory) result);
        }
    }

    private XADiskConnection connect() {

        final XADiskConnectionFactory xadiskcf = lookupConnectionFactory()
            .orElseThrow(() -> new UnexpectedErrorException(
            "No XADiskConnectionFactory available."));

        final XADiskConnection connection;
        try {
            connection = xadiskcf.getConnection();
        } catch (ResourceException ex) {
            throw new UnexpectedErrorException(
                "Failed to create XADiskConnection.", ex);
        }

        return connection;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public boolean isConfigured() {
        return lookupConnectionFactory().isPresent();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Reader createReader(final String path) throws
        FileDoesNotExistException,
        FileAccessException,
        InsufficientPermissionsException {

        return new InputStreamReader(createInputStream(path));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Writer createWriter(final String path) throws FileAccessException,
                                                         InsufficientPermissionsException {

        return new OutputStreamWriter(createOutputStream(path));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public InputStream createInputStream(final String path) throws
        FileDoesNotExistException,
        FileAccessException,
        InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        final XAFileInputStreamWrapper inputStream;
        try {
            final XAFileInputStream stream = connection
                .createXAFileInputStream(file);
            inputStream = new XAFileInputStreamWrapper(stream);
        } catch (FileNotExistsException ex) {
            throw new FileDoesNotExistException(path, ex);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (InterruptedException
                     | LockingFailedException
                     | NoTransactionAssociatedException ex) {
            throw new FileAccessException(path, ex);
        }

        return inputStream;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public OutputStream createOutputStream(final String path) throws
        FileAccessException,
        InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        if (!existsFile(path)) {
            try {
                connection.createFile(file, false);
            } catch (InsufficientPermissionOnFileException ex) {
                throw new InsufficientPermissionsException(path, ex);
            } catch (org.xadisk.filesystem.exceptions.FileAlreadyExistsException
                         | FileNotExistsException
                         | LockingFailedException
                         | NoTransactionAssociatedException
                         | InterruptedException ex) {
                throw new FileAccessException(path, ex);
            }
        }

        final XAFileOutputStreamWrapper outputStream;
        try {
            final XAFileOutputStream stream = connection
                .createXAFileOutputStream(file, true);
            outputStream = new XAFileOutputStreamWrapper(stream);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (FileNotExistsException
                     | FileUnderUseException
                     | InterruptedException
                     | LockingFailedException
                     | NoTransactionAssociatedException ex) {
            throw new FileAccessException(path, ex);
        }

        return outputStream;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public boolean existsFile(final String path) throws FileAccessException,
                                                        InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        try {
            return connection.fileExists(file);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (LockingFailedException
                     | NoTransactionAssociatedException
                     | InterruptedException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Override
    public String getMimeType(final String path) throws FileAccessException {

        final File file = new File(path);

        return MimetypesFileTypeMap
            .getDefaultFileTypeMap()
            .getContentType(file);
    }

    @Override
    public long getSize(final String path) throws FileAccessException {

        final XADiskConnection connection = connect();

        final File file = new File(path);
        try {
            return connection.getFileLength(file);
        } catch (FileNotExistsException
                     | InsufficientPermissionOnFileException
                     | LockingFailedException
                     | NoTransactionAssociatedException
                     | InterruptedException ex) {

            throw new FileAccessException(path, ex);
        }
    }

    @Override
    public void copy(final String sourcePath,
                     final String targetPath,
                     boolean recursive) throws FileAccessException {

        final XADiskConnection connection = connect();
        final File sourceFile = new File(sourcePath);
        final File targetFile = new File(targetPath);

        try {
            if (connection.fileExists(targetFile)) {
                connection.deleteFile(targetFile);
                connection.copyFile(sourceFile, targetFile);
            }
            connection.copyFile(sourceFile, targetFile);
        } catch (org.xadisk.filesystem.exceptions.DirectoryNotEmptyException
                     | org.xadisk.filesystem.exceptions.FileAlreadyExistsException
                 | FileNotExistsException
                     | FileUnderUseException
                     | InsufficientPermissionOnFileException
                     | InterruptedException
                     | LockingFailedException
                     | NoTransactionAssociatedException ex) {

            throw new FileAccessException(targetPath, ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public boolean isDirectory(final String path)
        throws FileAccessException,
               FileDoesNotExistException,
               InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        try {
            return connection.fileExistsAndIsDirectory(file);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (LockingFailedException
                     | NoTransactionAssociatedException
                     | InterruptedException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void createDirectory(final String path) throws FileAccessException,
                                                          FileAlreadyExistsException,
                                                          InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        try {
            connection.createFile(file, true);
        } catch (org.xadisk.filesystem.exceptions.FileAlreadyExistsException ex) {
            throw new FileAlreadyExistsException(path, ex);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (FileNotExistsException
                     | LockingFailedException
                     | InterruptedException
                     | NoTransactionAssociatedException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public List<String> listFiles(final String path) throws FileAccessException,
                                                            FileDoesNotExistException,
                                                            InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        final String[] files;
        try {
            files = connection.listFiles(file);
        } catch (FileNotExistsException ex) {
            throw new FileDoesNotExistException(path, ex);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (LockingFailedException
                     | NoTransactionAssociatedException
                     | InterruptedException ex) {
            throw new FileAccessException(path, ex);
        }

        return Arrays.asList(files);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void deleteFile(final String path) throws FileAccessException,
                                                     FileDoesNotExistException,
                                                     DirectoryNotEmptyException,
                                                     InsufficientPermissionsException {

        final XADiskConnection connection = connect();
        final File file = new File(path);

        if (isDirectory(path) && !listFiles(path).isEmpty()) {
            throw new DirectoryNotEmptyException(path);
        }

        try {
            connection.deleteFile(file);
        } catch (org.xadisk.filesystem.exceptions.DirectoryNotEmptyException ex) {
            throw new DirectoryNotEmptyException(path, ex);
        } catch (FileNotExistsException ex) {
            throw new FileDoesNotExistException(path, ex);
        } catch (InsufficientPermissionOnFileException ex) {
            throw new InsufficientPermissionsException(path, ex);
        } catch (FileUnderUseException
                     | LockingFailedException
                     | NoTransactionAssociatedException
                     | InterruptedException ex) {
            throw new FileAccessException(path, ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void deleteFile(final String path, boolean recursively) throws
        FileAccessException,
        FileDoesNotExistException,
        DirectoryNotEmptyException,
        InsufficientPermissionsException {

        if (isDirectory(path) && recursively) {
            final List<String> files = listFiles(path);
            if (!files.isEmpty()) {
                for (final String file : files) {
                    deleteFile(file, recursively);
                }
            }
        } else {
            deleteFile(path);
        }
    }

}
