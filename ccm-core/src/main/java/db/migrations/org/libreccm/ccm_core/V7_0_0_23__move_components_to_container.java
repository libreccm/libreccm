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
public class V7_0_0_23__move_components_to_container implements JdbcMigration {

    @Override
    public void migrate(final Connection connection) throws Exception {

        // Get all draft PageModels from ccm_core.page_models
        final PreparedStatement retrievePageModels = connection
            .prepareStatement("select PAGE_MODEL_ID, MODEL_UUID "
                                  + "from CCM_CORE.PAGE_MODELS "
                                  + "where VERSION = 'DRAFT'");


        /* 
        For each PageModel:
            * Create a single container (by inserting the data into 
             ccm_core.page_model_container_models)
            * set container_id of each component_model of the page_model
              to the ID of the new container
            * if the PageModel has a public version do the same but reuse the 
              containerUuid
         */
        final PreparedStatement createContainerId = connection
            .prepareStatement("select nextval('hibernate_sequence')");
        final PreparedStatement createContainer = connection
            .prepareStatement(
                "insert into CCM_CORE.PAGE_MODEL_CONTAINER_MODELS ("
                    + "    CONTAINER_ID, "
                    + "    CONTAINER_UUID, "
                    + "    CONTAINER_KEY, "
                    + "    UUID, "
                    + "    PAGE_MODEL_ID"
                    + ") "
                    + "VALUES ("
                    + "    ?,"
                    + "    ?,"
                    + "    ?,"
                    + "    ?,"
                    + "    ?"
                    + ")");
        final PreparedStatement checkForLivePageModel = connection
            .prepareStatement("select count(PAGE_MODEL_ID) "
                                  + "from CCM_CORE.PAGE_MODELS "
                                  + "where VERSION = 'LIVE' "
                                  + "and model_uuid = ?");
        final PreparedStatement retrieveLivePage = connection
            .prepareStatement("select PAGE_MODEL_ID "
                                  + "from CCM_CORE.PAGE_MODELS "
                                  + "where VERSION = 'LIVE' "
                                  + "and model_uuid = ?");
        final PreparedStatement updateComponents = connection
            .prepareStatement("update PAGE_MODEL_COMPONENT_MODELS "
                                  + "set CONTAINER_ID = ? "
                                  + "where COMPONENT_MODEL_ID in ("
                                  + "    select COMPONENT_MODEL_ID "
                                  + "    from PAGE_MODEL_COMPONENT_MODELS "
                                  + "    where PAGE_MODEL_ID = ?"
                                  + ")");
        try (final ResultSet pageModelsResultSet
                                 = retrievePageModels.executeQuery()) {
            
            while (pageModelsResultSet.next()) {

                final long pageModelId = pageModelsResultSet
                    .getLong("PAGE_MODEL_ID");
                final String modelUuid = pageModelsResultSet
                    .getString("MODEL_UUID");

                final String containerKey = "container";
                final String containerUuid = UUID.randomUUID().toString();

                final long containerId;
                try (final ResultSet containerIdResultSet
                                         = createContainerId.executeQuery()) {

                    containerIdResultSet.next();
                    containerId = containerIdResultSet.getLong("nextval");
                }

                createContainer.setLong(1, containerId);
                createContainer.setString(2, containerUuid);
                createContainer.setString(3, containerKey);
                createContainer.setString(4, containerUuid);
                createContainer.setLong(5, pageModelId);
                createContainer.executeUpdate();

                updateComponents.setLong(1, containerId);
                updateComponents.setLong(2, pageModelId);
                updateComponents.executeUpdate();

                checkForLivePageModel.setString(1, modelUuid);
                final long liveCount;
                try (final ResultSet liveCountResultSet
                                         = checkForLivePageModel.executeQuery()) {
                    
                    liveCountResultSet.next();
                    liveCount = liveCountResultSet.getLong("COUNT");
                }
                if (liveCount > 0) {

                    retrieveLivePage.setString(1, modelUuid);
                    final long livePageModelId;
                    try (final ResultSet liveResultSet
                                             = retrieveLivePage.executeQuery()) {
                        liveResultSet.next();
                        livePageModelId = liveResultSet.getLong("PAGE_MODEL_ID");
                    }

                    final long liveContainerId;
                    try (final ResultSet liveContainerIdResultSet
                                             = createContainerId.executeQuery()) {
                        liveContainerIdResultSet.next();
                        liveContainerId = liveContainerIdResultSet
                            .getLong("nextval");
                    }

                    createContainer.setLong(1, liveContainerId);
                    createContainer.setString(2, containerUuid);
                    createContainer.setString(3, containerKey);
                    createContainer.setString(4, UUID.randomUUID().toString());
                    createContainer.setLong(5, livePageModelId);
                    createContainer.executeUpdate();

                    updateComponents.setLong(1, liveContainerId);
                    updateComponents.setLong(2, livePageModelId);
                    updateComponents.executeUpdate();
                }
            }
        }
    }

}
