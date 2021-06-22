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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class BinaryAssetBlobDataProvider implements BinaryAssetDataProvider {

    @Resource(lookup = "java:/comp/env/jdbc/libreccm/db")
    private DataSource dataSource;

    @Override
    public InputStream retrieveData(final BinaryAsset asset) {
        Objects.requireNonNull(asset, "Can't retrieve data from null.");
        try ( Connection connection = dataSource.getConnection()) {
            final PreparedStatement stmt = connection
                .prepareStatement(
                    "SELECT data FROM binary_assets WHERE object_id  = ?"
                );
            stmt.setLong(1, asset.getObjectId());

            try (ResultSet resultSet = stmt.executeQuery()) {
                final Blob blob = resultSet.getBlob("data");
                return blob.getBinaryStream();
            }
        } catch (SQLException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @Override
    public void saveData(final BinaryAsset asset, final InputStream stream) {
        Objects.requireNonNull(asset, "Can't save data to null.");
        try ( Connection connection = dataSource.getConnection()) {
            final PreparedStatement stmt = connection
                .prepareStatement(
                    "UPDATE binary_assets SET data = ? WHERE object_id = ?"
                );
            stmt.setBlob(1, stream);
            stmt.setLong(2, asset.getObjectId());
        } catch (SQLException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

}
