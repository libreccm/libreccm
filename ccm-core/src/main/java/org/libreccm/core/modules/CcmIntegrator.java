/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.core.modules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.internal.util.jdbc.JdbcUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ServiceLoader;

import javax.sql.DataSource;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmIntegrator implements Integrator {

    private static final Logger LOGGER = LogManager.getLogger(
        CcmIntegrator.class);

    private ServiceLoader<CcmModule> modules;

    @Override
    public void integrate(final Configuration configuration,
                          final SessionFactoryImplementor sessionFactory,
                          final SessionFactoryServiceRegistry registry) {
        LOGGER.info("Retrieving modules...");
        modules = ServiceLoader.load(CcmModule.class);
        for (final CcmModule module : modules) {
            LOGGER.info("Found module class {}...", module.getClass().getName());
            final ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.load(module);
            LOGGER.info("Found module {}.", moduleInfo.getModuleName());
        }

        Connection connection = null;
        try {
            final DependencyTreeManager treeManager
                                            = new DependencyTreeManager();
            final List<TreeNode> tree = treeManager.generateTree(modules);
            final List<TreeNode> orderedNodes = treeManager.orderModules(tree);

            final DataSource dataSource = (DataSource) sessionFactory.
                getProperties().get("javax.persistence.jtaDataSource");
            connection = dataSource.getConnection();

            for (final TreeNode node : orderedNodes) {
                migrateModule(node.getModule().getClass(), dataSource);

                for (Class<?> entity : node.getModuleInfo().getModuleEntities()) {
                    configuration.addAnnotatedClass(entity);
                }
            }

        } catch (DependencyException | SQLException ex) {
            throw new IntegrationException("Failed to integrate modules", ex);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

        LOGGER.info("All modules integrated successfully.");
    }

    private String getSchemaName(final ModuleInfo moduleInfo) {
        return moduleInfo.getModuleName().toLowerCase().replace("-", "_");
    }

    private String getLocation(final ModuleInfo moduleInfo,
                               final Connection connection) throws SQLException {
        final StringBuffer buffer = new StringBuffer(
            "classpath:/db/migrations/");
        //buffer.append(ModuleUtil.getModulePackageName(module));
        buffer.append(moduleInfo.getModuleDataPackage());
        switch (connection.getMetaData().getDatabaseProductName()) {
            case "MySQL":
                buffer.append("/mysql");
                break;
            case "PostgreSQL":
                buffer.append("/pgsql");
                break;
            default:
                throw new IntegrationException(String.format(
                    "Integration failed. Database \"%s\" is not supported yet.",
                    connection.getMetaData().
                    getDatabaseProductName()));
        }

        return buffer.toString();
    }

    private void migrateModule(final Class<? extends CcmModule> module,
                               final DataSource dataSource) throws SQLException {
        final Connection connection = dataSource.getConnection();

        final ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.load(module);

        final Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setSchemas(getSchemaName(moduleInfo));
        flyway.setLocations(getLocation(moduleInfo, connection));

        final MigrationInfo current = flyway.info().current();
        boolean newModule;
        if (current == null) {
            LOGGER.info("No version, database schema is considered empty.");
            newModule = true;
        } else {
            LOGGER.info("Current version of schema {} in database is {}",
                        getSchemaName(moduleInfo),
                        current.getVersion());
            newModule = false;
        }

        flyway.migrate();

        LOGGER.info("Migrated schema {} in database to version {}",
                    getSchemaName(moduleInfo),
                    flyway.info().current().getVersion());

        if (newModule) {
            final Statement statement = connection.createStatement();
            statement.execute(String.format(
                "INSERT INTO ccm_core.installed_modules "
                    + "(module_id, module_class_name, status) "
                    + "VALUES (%d, '%s', 'NEW')",
                module.getName().hashCode(),
                module.getName()));
        }
    }

    @Override
    public void integrate(final MetadataImplementor metadata,
                          final SessionFactoryImplementor sessionFactory,
                          final SessionFactoryServiceRegistry registry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void disintegrate(final SessionFactoryImplementor sessionFactory,
                             final SessionFactoryServiceRegistry registry) {
        LOGGER.info("Hibernate desintegrating...");

        Connection connection = null;
        LOGGER.info("Removing schemas for modules scheduled for uninstall...");
        try {

            final DataSource dataSource = (DataSource) sessionFactory
                .getProperties().get("javax.persistence.jtaDataSource");
            connection = dataSource.getConnection();
            System.out.println("checking modules...");
            LOGGER.info("Checking modules...");

            for (final CcmModule module : modules) {
                final ModuleInfo moduleInfo = new ModuleInfo();
                moduleInfo.load(module);

                final Statement query = connection.createStatement();
                final ResultSet result = query.executeQuery(
                    String.format("SELECT module_class_name, status "
                                      + "FROM ccm_core.installed_modules "
                                      + "WHERE module_class_name = '%s'",
                                  module.getClass().getName()));
                System.out.printf("Checking status of module %s...\n",
                                  module.getClass().getName());

                if (result.next() && ModuleStatus.UNINSTALL.toString().equals(
                    result.getString("status"))) {

                    LOGGER.info("Removing schema for module %s...",
                                module.getClass().getName());
                    final Flyway flyway = new Flyway();
                    flyway.setDataSource(dataSource);
                    flyway.setSchemas(getSchemaName(moduleInfo));
                    flyway.setLocations(getLocation(moduleInfo, connection));
                    LOGGER.warn("Deleting schema for module {}...",
                                moduleInfo.getModuleName());
                    flyway.clean();

                    final Statement statement = connection.createStatement();
                    statement.addBatch(String.format(
                        "DELETE FROM ccm_core.installed_modules "
                            + "WHERE module_class_name = '%s'",
                        module.getClass().getName()));
                    statement.executeBatch();
                    LOGGER.info("Done.");
                }

            }
        } catch (SQLException ex) {
            LOGGER.error("Desintegration failed: ", ex);
            System.err.println("Desintration failed");
            ex.printStackTrace(System.err);
            System.err.println();
            SQLException next = ex.getNextException();
            while(next != null) {
                next.printStackTrace(System.err);
                System.err.println();
                next = next.getNextException();
            }
            throw new IntegrationException("Failed to desintegrate.", next);
        } finally {
            JdbcUtils.closeConnection(connection);
        }
    }

}
