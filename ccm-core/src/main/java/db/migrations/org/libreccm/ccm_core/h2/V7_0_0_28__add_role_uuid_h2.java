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
package db.migrations.org.libreccm.ccm_core.h2;

import db.migrations.org.libreccm.ccm_core.V7_0_0_28__add_role_uuid;
import org.flywaydb.core.api.migration.jdbc.JdbcMigration;

import java.sql.Connection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class V7_0_0_28__add_role_uuid_h2
    extends V7_0_0_28__add_role_uuid
    implements JdbcMigration {

    @Override
    public void migrate(final Connection connection) throws Exception {
        super.migrate(connection);
    }

}
