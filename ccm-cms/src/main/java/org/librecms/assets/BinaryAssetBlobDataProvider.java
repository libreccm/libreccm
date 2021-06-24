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

import org.libreccm.core.UnexpectedErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.sql.DataSource;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class BinaryAssetBlobDataProvider implements BinaryAssetDataProvider {

    @Resource(lookup = "java:/comp/env/jdbc/libreccm/db")
    private DataSource dataSource;

    @Override
    public void copyDataToOutputStream(
        final BinaryAsset asset, final OutputStream outputStream
    ) {
        Objects.requireNonNull(asset, "Can't retrieve data from null.");
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
                blob.getBinaryStream().transferTo(outputStream);
//                try ( InputStream inputStream = blob.getBinaryStream()) {
//                    byte[] buffer = new byte[8192];
//                    int length;
//                    while ((length = inputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, length);
//                    }
//                }
            }
        } catch (SQLException | IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public void saveData(
        final BinaryAsset asset,
        final InputStream stream,
        final String fileName,
        final String mimeType,
        final long fileSize
    ) {
        Objects.requireNonNull(asset, "Can't save data to null.");
        try ( Connection connection = dataSource.getConnection()) {
            final PreparedStatement stmt = connection
                .prepareStatement(
                    "UPDATE ccm_cms.binary_assets SET asset_data = ?, filename = ?, mime_type = ?, data_size = ? WHERE object_id = ?"
                );
            stmt.setBlob(1, stream);
            stmt.setString(2, fileName);
            stmt.setString(3, mimeType);
            stmt.setLong(4, fileSize);
            stmt.setLong(5, asset.getObjectId());

            stmt.execute();

        } catch (SQLException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

}
