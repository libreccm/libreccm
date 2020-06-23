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
public class V7_0_0_31__add_rolemembership_uuid extends BaseJavaMigration {
    
    @Override
    public void migrate(final Context context) throws Exception {

        final Connection connection = context.getConnection();
        
        final PreparedStatement retrieveMemberships = connection
            .prepareStatement("select MEMBERSHIP_ID from CCM_CORE.ROLE_MEMBERSHIPS");

        final PreparedStatement addUuidCol = connection
            .prepareStatement("alter table CCM_CORE.ROLE_MEMBERSHIPS "
                                  + "add column UUID varchar(255)");

        final PreparedStatement setUuid = connection
            .prepareStatement("update CCM_CORE.ROLE_MEMBERSHIPS SET uuid = ? "
                                  + "where MEMBERSHIP_ID = ?");

        final PreparedStatement addUuidNotNull = connection
            .prepareStatement("alter table CCM_CORE.ROLE_MEMBERSHIPS "
                                  + "alter column UUID set not null");

        final PreparedStatement addUniqueConstraint = connection
            .prepareStatement(
                "alter table CCM_CORE.ROLE_MEMBERSHIPS "
                    + "add constraint UK_82wdq214bfs99eii71fp50s97 "
                    + "unique (UUID)");

        final ResultSet membershipIds = retrieveMemberships.executeQuery();

        addUuidCol.execute();
        
        while(membershipIds.next()) {
            
            setUuid.setString(1, UUID.randomUUID().toString());
            setUuid.setLong(2, membershipIds.getLong("MEMBERSHIP_ID"));
            setUuid.executeUpdate();
        }
        
        addUuidNotNull.execute();
        addUniqueConstraint.execute();

    }

    
}
