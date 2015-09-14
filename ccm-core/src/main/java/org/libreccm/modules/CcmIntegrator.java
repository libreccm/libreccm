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
package org.libreccm.modules;

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
import java.util.Locale;
import java.util.ServiceLoader;

import javax.sql.DataSource;

/**
 * Manages the database schema for new and updated modules.
 *
 * This implementation of Hibernate's {@code Integrator} interface which manages
 * the database schema of LibreCCM. It uses the
 * <a href="http://www.flywaydb.org">Flyway</a> framework to execute migrations
 * on the database if necessary. To find the modules the Java service loader is
 * used. Because the integrator is called in a very early phase of the
 * application lifecycle we can't use CDI here.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmIntegrator implements Integrator {

    private static final Logger LOGGER = LogManager.getLogger(
            CcmIntegrator.class);

    /**
     * Service loader containing all modules. Initialised by the
     * {@link #integrate(Configuration, SessionFactoryImplementor, SessionFactoryServiceRegistry)}
     * method.
     */
    private ServiceLoader<CcmModule> modules;

    /**
     * Checks for new and updated modules when the persistence unit is started.
     * If there are updates the necessary database migrations are executed.
     *
     * @param configuration
     * @param sessionFactory
     * @param registry
     */
    @Override
    public void integrate(final Configuration configuration,
                          final SessionFactoryImplementor sessionFactory,
                          final SessionFactoryServiceRegistry registry) {
        //Find all modules in the classpath
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
            //Create dependency tree for the modules
            final DependencyTreeManager treeManager
                                        = new DependencyTreeManager();
            final List<TreeNode> tree = treeManager.generateTree(modules);
            final List<TreeNode> orderedNodes = treeManager.orderModules(tree);

            //Get DataSource and Connection from the sessionFactory of 
            //Hibernate.
            final DataSource dataSource = (DataSource) sessionFactory.
                    getProperties().get("javax.persistence.jtaDataSource");
            connection = dataSource.getConnection();

            //Migrate tables and sequences which don't belong to a module 
            //for instance the hibernate_sequence
            final Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            final StringBuffer buffer = new StringBuffer(
                    "db/migrations/org/libreccm/base");
            appendDbLocation(buffer, connection);
            flyway.setLocations(buffer.toString());
            flyway.migrate();

            //Migrate the modules
            for (final TreeNode node : orderedNodes) {
                migrateModule(node.getModule().getClass(), dataSource);

                for (Class<?> entity : node.getModuleInfo().getModuleEntities()) {
                    configuration.addAnnotatedClass(entity);
                }
            }

            //Build Hibernate mappings for the entities.
            configuration.buildMappings();

        } catch (DependencyException | SQLException ex) {
            throw new IntegrationException("Failed to integrate modules", ex);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

        LOGGER.info("All modules integrated successfully.");
    }

    /**
     * Private helper method to get the database schema name of a module. The
     * name is then name of the module in lower case with all hyphens replaced
     * with underscores.
     *
     * @param moduleInfo The module info object for the module
     * @return The database schema name of the module.
     */
    private String getSchemaName(final ModuleInfo moduleInfo) {
        return moduleInfo.getModuleName().toLowerCase().replace("-", "_");
    }

    /**
     * Private helper method to append the name of the database in use to the
     * location of the migrations. The value is determined using the return
     * value of {@link Connection#getMetaData().getDatabaseProductName()} in
     * lower case. The current supported values are:
     *
     * <table>
     * <tr>
     * <th>Database Product Name</th>
     * <th>Location</th>
     * </tr>
     * <tr>
     * <td><code>H2</code></td>
     * <td><code>/h2</code></td>
     * </tr>
     * <tr>
     * <td><code>PostgreSQL</code></td>
     * <td><code>/pgsql</code></td>
     * </tr>
     * </table>
     *
     *
     * If the database is not supported an {@link IntegrationException} will be
     * thrown.
     *
     * @param buffer Buffer for the location string.
     * @param connection The JDBC connection object.
     * @throws SQLException If an error occurs while accessing the database.
     * @throws IntegrationException If the database is not supported yet.
     */
    private void appendDbLocation(final StringBuffer buffer,
                                  final Connection connection)
            throws SQLException {

        switch (connection.getMetaData().getDatabaseProductName()) {
            case "H2":
                buffer.append("/h2");
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
    }

    /**
     * Helper method to determine the location of the migrations for a module.
     *
     * @param moduleInfo The module info object of the module.
     * @param connection The database connection.
     * @return The location of the database migrations for a specific module.
     * @throws SQLException If an error on the JDBC site occurs.
     */
    private String getLocation(final ModuleInfo moduleInfo,
                               final Connection connection)
            throws SQLException {

        final StringBuffer buffer = new StringBuffer(
                "classpath:/db/migrations/");
        buffer.append(moduleInfo.getModuleDataPackage());
        appendDbLocation(buffer, connection);

        return buffer.toString();
    }

    /**
     * Helper method for executing the migrations for a module.
     *
     * @param module The module for which the migrations are executed.
     * @param dataSource The JDBC data source for connecting to the database.
     * @throws SQLException If an error occurs while applying the migrations.
     */
    private void migrateModule(final Class<? extends CcmModule> module,
                               final DataSource dataSource) throws SQLException {
        //Get the JDBC connection from the DataSource
        final Connection connection = dataSource.getConnection();

        //Load the module info for the module
        final ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.load(module);

        //Create a Flyway instance for the the module.
        final Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        //Set schema correctly for the different databases. Necessary because
        //different RDBMS handle case different.
        if ("H2".equals(connection.getMetaData().getDatabaseProductName())) {
            flyway
                    .setSchemas(getSchemaName(moduleInfo).toUpperCase(
                                    Locale.ROOT));
        } else {
            flyway.setSchemas(getSchemaName(moduleInfo));
        }
        flyway.setLocations(getLocation(moduleInfo, connection));

        //Get current migrations info
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

        //Execute migrations. Flyway will check if there any migrations to apply.
        flyway.migrate();

        LOGGER.info("Migrated schema {} in database to version {}",
                    getSchemaName(moduleInfo),
                    flyway.info().current().getVersion());

        //If a new module was installed register the module in the 
        //installed_modules table with the new status. The ModuleManager will
        //call the install method of them module.
        if (newModule) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(String.format(
                        "INSERT INTO ccm_core.installed_modules "
                                + "(module_id, module_class_name, status) "
                                + "VALUES (%d, '%s', 'NEW')",
                        module.getName().hashCode(),
                        module.getName()));
            } catch (SQLException ex) {
                throw new IntegrationException("Failed to integrate.", ex);
            }
        }
    }

    @Override
    public void integrate(final MetadataImplementor metadata,
                          final SessionFactoryImplementor sessionFactory,
                          final SessionFactoryServiceRegistry registry) {
        //Nothing
    }

    /**
     * Called when the application is shutdown. Used to remove the database
     * schema of uninstalled modules.
     *
     * @param sessionFactory
     * @param registry
     */
    @Override
    public void disintegrate(final SessionFactoryImplementor sessionFactory,
                             final SessionFactoryServiceRegistry registry) {
        LOGGER.info("Hibernate desintegrating...");

        Connection connection = null;
        LOGGER.info("Removing schemas for modules scheduled for uninstall...");
        try {

            //Get JDBC connection
            final DataSource dataSource = (DataSource) sessionFactory
                    .getProperties().get("javax.persistence.jtaDataSource");
            connection = dataSource.getConnection();
            System.out.println("checking modules...");
            LOGGER.info("Checking modules...");

            for (final CcmModule module : modules) {
                final ModuleInfo moduleInfo = new ModuleInfo();
                moduleInfo.load(module);

                try (Statement query = connection.createStatement();
                     //Check status of each module
                     ResultSet result = query.executeQuery(
                             String.format("SELECT module_class_name, status "
                                                   + "FROM ccm_core.installed_modules "
                                           + "WHERE module_class_name = '%s'",
                                           module.getClass().getName()))) {

                    System.out.printf("Checking status of module %s...%n",
                                      module.getClass().getName());

                    //If there modules marked for uninstall remove the schema 
                    //of the module from the database.
                    if (result.next() && ModuleStatus.UNINSTALL.toString()
                            .equals(result.getString("status"))) {

                        LOGGER.info("Removing schema for module %s...",
                                    module.getClass().getName());
                        final Flyway flyway = new Flyway();
                        flyway.setDataSource(dataSource);
                        flyway.setSchemas(getSchemaName(moduleInfo));
                        flyway.setLocations(getLocation(moduleInfo, connection));
                        LOGGER.warn("Deleting schema for module {}...",
                                    moduleInfo.getModuleName());
                        flyway.clean();

                        //Delete the module from the installed modules table.
                        try (final Statement statement = connection
                                .createStatement()) {
                            statement.addBatch(String.format(
                                    "DELETE FROM ccm_core.installed_modules "
                                            + "WHERE module_class_name = '%s'",
                                    module.getClass().getName()));
                            statement.executeBatch();
                            LOGGER.info("Done.");
                        } catch (SQLException ex) {
                            throw new IntegrationException(
                                    "Failed to desintegrate", ex);
                        }
                    }

                } catch (SQLException ex) {
                    throw new IntegrationException("Failed to desintegrate.");
                }

            }
        } catch (SQLException ex) {
            LOGGER.error("Desintegration failed: ", ex);
            System.err.println("Desintegration failed");
            ex.printStackTrace(System.err);
            System.err.println();
            SQLException next = ex.getNextException();
            while (next != null) {
                next.printStackTrace(System.err);
                System.err.println();
                next = next.getNextException();
            }

            throw new IntegrationException("Failed to desintegrate.");

        } finally {
            JdbcUtils.closeConnection(connection);
        }
    }

}
