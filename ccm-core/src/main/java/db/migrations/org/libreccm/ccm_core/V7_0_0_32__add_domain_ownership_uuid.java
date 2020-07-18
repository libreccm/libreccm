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

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class V7_0_0_32__add_domain_ownership_uuid extends BaseJavaMigration {
    
    
    @Override
    public void migrate(final Context context) throws Exception {

        final Connection connection = context.getConnection();
        
        final PreparedStatement retrieveOwnerships = connection
            .prepareStatement("select OWNERSHIP_ID from CCM_CORE.DOMAIN_OWNERSHIPS");

        final PreparedStatement addUuidCol = connection
            .prepareStatement("alter table CCM_CORE.DOMAIN_OWNERSHIPS "
                                  + "add column UUID varchar(255)");

        final PreparedStatement setUuid = connection
            .prepareStatement("update CCM_CORE.DOMAIN_OWNERSHIPS SET uuid = ? "
                                  + "where OWNERSHIP_ID = ?");

        final PreparedStatement addUuidNotNull = connection
            .prepareStatement("alter table CCM_CORE.DOMAIN_OWNERSHIPS "
                                  + "alter column UUID set not null");

        final PreparedStatement addUniqueConstraint = connection
            .prepareStatement(
                "alter table CCM_CORE.DOMAIN_OWNERSHIPS "
                    + "add constraint UK_j86gai9740v9hshascbsboudb "
                    + "unique (UUID)");

        final ResultSet ownershipIds = retrieveOwnerships.executeQuery();

        addUuidCol.execute();
        
        while(ownershipIds.next()) {
            
            setUuid.setString(1, UUID.randomUUID().toString());
            setUuid.setLong(2, ownershipIds.getLong("OWNERSHIP_ID"));
            setUuid.executeUpdate();
        }
        
        addUuidNotNull.execute();
        addUniqueConstraint.execute();

    }
    
}
