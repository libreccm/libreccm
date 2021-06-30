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
package org.librecms.assets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.jdbc.BlobProxy;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.AssetRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class BinaryAssetBlobDataProvider implements BinaryAssetDataProvider {

    private static final Logger LOGGER = LogManager.getLogger(
        BinaryAssetBlobDataProvider.class
    );

    @Resource(lookup = "java:/comp/env/jdbc/libreccm/db")
    private DataSource dataSource;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private EntityManager entityManager;

    @Override
    public void copyDataToOutputStream(
        final BinaryAsset asset, final OutputStream outputStream
    ) {
        try ( Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            final PreparedStatement stmt = connection
                .prepareStatement(
                    "SELECT asset_data FROM ccm_cms.binary_assets WHERE object_id  = ?"
                );
            stmt.setLong(1, asset.getObjectId());

            try ( ResultSet resultSet = stmt.executeQuery()) {
                resultSet.next();
                final Blob blob = resultSet.getBlob("asset_data");
                try ( InputStream inputStream = blob.getBinaryStream()) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void saveData(
        final BinaryAsset asset,
        final InputStream inputStream,
        final String fileName,
        final String mimeType,
        final long fileSizeParam
    ) {
        Objects.requireNonNull(asset, "Can't save data to null.");
        Objects.requireNonNull(inputStream, "Can't read data from null");
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(mimeType);

        try {
            final Path tmpFilePath = Files.createTempFile("upload", fileName);
            int fileSize = 0;
            try ( OutputStream outputStream = Files.newOutputStream(tmpFilePath)) {
                int length;
                byte[] buffer = new byte[8192];
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer);
                    fileSize += length;
                }
                outputStream.flush();
            }

            final Blob data = BlobProxy.generateProxy(
                new UploadInputStream(tmpFilePath), -1
            );
            asset.setFileName(fileName);
            asset.setData(data);
            asset.setSize(fileSize);
            try {
                asset.setMimeType(new MimeType(mimeType));
            } catch (MimeTypeParseException ex) {
                LOGGER.error(
                    "Failed to upload file for FileAsset {}:",
                    asset.getUuid()
                );
                LOGGER.error(ex);

                throw new UnexpectedErrorException(ex);
            }
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }

        assetRepo.save(asset);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    private void updateAudTable(final long assetId) {
        try ( Connection connection = dataSource.getConnection()) {
            final PreparedStatement findRevStmt = connection
                .prepareStatement(
                    "SELECT rev FROM ccm_cms.binary_assets_aud WHERE object_id = ? ORDER BY rev DESC LIMIT 1"
                );
            findRevStmt.setLong(1, assetId);

            final long rev;
            try ( ResultSet resultSet = findRevStmt.executeQuery()) {
                resultSet.next();
                rev = resultSet.getLong("rev");
            }

            final PreparedStatement updateDataStmt = connection
                .prepareStatement(
                    "UPDATE ccm_cms.binary_assets_aud SET asset_data = (SELECT asset_data FROM ccm_cms.binary_assets WHERE object_id = ?) WHERE object_id = ? AND rev = ?"
                );
            updateDataStmt.setLong(1, assetId);
            updateDataStmt.setLong(2, assetId);
            updateDataStmt.setLong(3, rev);

            updateDataStmt.execute();
        } catch (SQLException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    private class UploadInputStream extends InputStream {

        private final Path tmpFilePath;

        private InputStream inputStream;

        public UploadInputStream(final Path tmpFilePath) {
            this.tmpFilePath = tmpFilePath;
        }

        @Override
        public int available() throws IOException {
            openNewInputStreamIfNecessary();
            return inputStream.available();
        }

        @Override
        public void close() throws IOException {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
        }

        @Override
        public void mark(final int readLimit) {
            try {
                openNewInputStreamIfNecessary();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            inputStream.mark(readLimit);
        }

        @Override
        public boolean markSupported() {
            try {
                openNewInputStreamIfNecessary();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return inputStream.markSupported();
        }

        @Override
        public int read() throws IOException {
            openNewInputStreamIfNecessary();
            return inputStream.read();
        }

        @Override
        public int read(final byte[] data) throws IOException {
            openNewInputStreamIfNecessary();
            return inputStream.read(data);
        }

        @Override
        public int read(final byte[] data, final int offset, final int length)
            throws IOException {
            openNewInputStreamIfNecessary();
            return inputStream.read(data, offset, length);
        }

        @Override
        public void reset() throws IOException {
            if (inputStream == null) {
                openNewInputStreamIfNecessary();
            } else {
                inputStream.reset();
            }
        }

        @Override
        public long skip(long nBytes) throws IOException {
            openNewInputStreamIfNecessary();
            return inputStream.skip(nBytes);
        }

        private void openNewInputStreamIfNecessary() throws IOException {
            if (inputStream == null) {
                inputStream = Files.newInputStream(tmpFilePath);
            }
        }

    }

}
