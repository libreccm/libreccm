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
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.libreccm.search.SearchConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;

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
     * Name of the property which is used to retrieve the data source in use
     * from Hibernate.
     */
    private static final String DATASOURCE_PROPERTY
                                    = "hibernate.connection.datasource";

    /**
     * Service loader containing all modules. Initialised by the
     * {@link #integrate(Metadata, SessionFactoryImplementor, SessionFactoryServiceRegistry)}
     * method.
     */
    private ServiceLoader<CcmModule> modules;

    /**
     * Checks for new and updated modules when the persistence unit is started.
     * If there are updates the necessary database migrations are executed.
     *
     * @param metadata
     * @param sessionFactory
     * @param registry
     */
    @Override
    public void integrate(final Metadata metadata,
                          final SessionFactoryImplementor sessionFactory,
                          final SessionFactoryServiceRegistry registry) {
        LOGGER.info("Retrieving modules...");
        modules = ServiceLoader.load(CcmModule.class);
        for (final CcmModule module : modules) {
            LOGGER.info("Found module class {}...", module.getClass().getName());
            final ModuleInfo moduleInfo = loadModuleInfo(module);
            LOGGER.info("Found module {}.", moduleInfo.getModuleName());
        }

        Connection connection = null;
        try {
            //Create dependency tree for the modules
            final DependencyTreeManager treeManager
                                            = new DependencyTreeManager();
            final List<TreeNode> tree = treeManager.generateTree(modules);
            final List<TreeNode> orderedNodes = treeManager.orderModules(tree);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ordered list of modules:");
                orderedNodes.forEach(m -> {
                    LOGGER.debug("\t{}-{}",
                                 m.getModuleInfo().getModuleName(),
                                 m.getModuleInfo().getModuleVersion());
                });
            }

//            //Get DataSource and Connection from the sessionFactory of 
//            //Hibernate.
            final DataSource dataSource = (DataSource) sessionFactory.
                getProperties().get(DATASOURCE_PROPERTY);
            if (dataSource == null) {
                throw new IllegalStateException("No data source available.");
            }
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
                LOGGER.debug("Applying migrations for module {}-{}",
                             node.getModuleInfo().getModuleName(),
                             node.getModuleInfo().getModuleVersion());
                migrateModule(node.getModule().getClass(), dataSource);
            }

            configureHibernateSearch(connection, sessionFactory);

        } catch (DependencyException | SQLException ex) {
            throw new IntegrationException("Failed to integrate modules", ex);
        } finally {
            JdbcUtils.closeConnection(connection);
        }

        LOGGER.info("All modules integrated successfully.");

    }

    /**
     * Helper method for loading the module info for a module.
     *
     * @param module The module for which the module is loaded.
     *
     * @return The {@link ModuleInfo} object for the module
     */
    private ModuleInfo loadModuleInfo(final CcmModule module) {
        final ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.load(module);

        return moduleInfo;
    }

    /**
     * Private helper method to get the database schema name of a module. The
     * name is then name of the module in lower case with all hyphens replaced
     * with underscores.
     *
     * @param moduleInfo The module info object for the module
     *
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
     * @param buffer     Buffer for the location string.
     * @param connection The JDBC connection object.
     *
     * @throws SQLException         If an error occurs while accessing the
     *                              database.
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
     *
     * @return The location of the database migrations for a specific module.
     *
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
     * @param module     The module for which the migrations are executed.
     * @param dataSource The JDBC data source for connecting to the database.
     *
     * @throws SQLException If an error occurs while applying the migrations.
     */
    private void migrateModule(final Class<? extends CcmModule> module,
                               final DataSource dataSource) throws SQLException {
        //Get the JDBC connection from the DataSource
        try (final Connection connection = dataSource.getConnection()) {

            //Load the module info for the module
            final ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.load(module);

            //Create a Flyway instance for the the module.
            final Flyway flyway = new Flyway();
            flyway.setDataSource(dataSource);
            //Set schema correctly for the different databases. Necessary because
            //different RDBMS handle case different.
            if ("H2".equals(connection.getMetaData().getDatabaseProductName())) {
                flyway.setSchemas(getSchemaName(moduleInfo).toUpperCase(
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
                .getProperties().get(DATASOURCE_PROPERTY);
            connection = dataSource.getConnection();
            System.out.println("checking modules...");
            LOGGER.info("Checking modules...");

            for (final CcmModule module : modules) {
                final ModuleInfo moduleInfo = loadModuleInfo(module);

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
                        uninstallModule(connection,
                                        dataSource,
                                        module,
                                        moduleInfo);
                    }
                } catch (SQLException ex) {
                    throw new IntegrationException("Failed to desintegrate.",
                                                   ex);
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

            throw new IntegrationException("Failed to desintegrate.", ex);

        } finally {
            JdbcUtils.closeConnection(connection);
        }
    }

    private void uninstallModule(final Connection connection,
                                 final DataSource dataSource,
                                 final CcmModule module,
                                 final ModuleInfo moduleInfo)
        throws SQLException {
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

    private void configureHibernateSearch(
        final Connection connection,
        final SessionFactoryImplementor sessionFactory) throws SQLException {

        LOGGER.info("Configuring Hibernate Search...");

        LOGGER.debug(
            "Checking for Directory Provider setting in configuration...");
        final Optional<String> directoryProvider = getSetting(
            connection,
            SearchConfig.class.getName(),
            SearchConfig.DIRECTORY_PROVIDER);
        if (directoryProvider.isPresent()) {
            LOGGER.debug("Found setting for directory provider: {}",
                         directoryProvider.orElse(""));
            sessionFactory
                .getProperties()
                .put("hibernate.search.default.directory_provider",
                     directoryProvider.get());
        } else {
            LOGGER.debug("No setting for directory provider. "
                             + "Defaulting to RAM directory provider.");
            sessionFactory
                .getProperties()
                .put("hibernate.search.default.directory_provider", "ram");
        }

        final Optional<String> indexBase = getSetting(
            connection,
            SearchConfig.class.getName(),
            SearchConfig.INDEX_BASE);
        if (indexBase.isPresent()) {
            LOGGER.debug("Setting Index Base to \"{}\".", indexBase.get());
            sessionFactory
                .getProperties()
                .put("hibernate.search.default.indexBase",
                     indexBase.get());
        }
    }

    /**
     * A helper method for getting a setting from the configuration database. We
     * can't use JPA/Hibernate in this class because the JPA subsystem is not
     * initialised when this class runs. Therefore we have the fallback to JDBC
     * here.
     *
     * @param connection   Connection to the database.
     * @param settingClass Setting class used to represent the setting.
     * @param settingName  The name of the setting to retrieve.
     *
     * @return The value of the setting.
     *
     * @throws SQLException
     */
    private Optional<String> getSetting(final Connection connection,
                                        final String settingClass,
                                        final String settingName)
        throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
            "SELECT setting_value_string FROM ccm_core.settings "
                + "WHERE configuration_class = ? AND name = ?")) {
            statement.setString(1, settingClass);
            statement.setString(2, settingName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.ofNullable(resultSet.getString(1));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

}
