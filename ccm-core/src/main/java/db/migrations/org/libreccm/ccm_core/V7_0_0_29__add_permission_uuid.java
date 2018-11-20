/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package db.migrations.org.libreccm.ccm_core;

import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class V7_0_0_29__add_permission_uuid implements JdbcMigration {

    @Override
    public void migrate(Connection connection) throws Exception {

        final PreparedStatement retrievePermissions = connection
            .prepareStatement("select PERMISSION_ID from CCM_CORE.PERMISSIONS");
        
          final PreparedStatement addUuidCol = connection
            .prepareStatement("alter table CCM_CORE.PERMISSIONS "
                                  + "add column UUID varchar(255)");

        final PreparedStatement setUuid = connection
            .prepareStatement("update CCM_CORE.PERMISSIONS SET uuid = ? "
                                  + "where PERMISSION_ID = ?");

        final PreparedStatement addUuidNotNull = connection
            .prepareStatement("alter table CCM_CORE.PERMISSIONS "
                                  + "alter column UUID set not null");

        final PreparedStatement addUniqueConstraint = connection
            .prepareStatement(
                "alter table CCM_CORE.PERMISSIONS "
                    + "add constraint UK_p50se7rdexv7xnkiqsl6ijyti "
                    + "unique (UUID)");

        final ResultSet permissionIds = retrievePermissions.executeQuery();

        addUuidCol.execute();
        
        while(permissionIds.next()) {
            
            setUuid.setString(1, UUID.randomUUID().toString());
            setUuid.setLong(2, permissionIds.getLong("PERMISSION_ID"));
            setUuid.executeUpdate();
        }
        
        addUuidNotNull.execute();
        addUniqueConstraint.execute();
    }

}
