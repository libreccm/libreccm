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
package org.libreccm.wildfly.swarm;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.config.datasources.DataSource;
import org.wildfly.swarm.config.datasources.DataSourceConfigurator;
import org.wildfly.swarm.config.datasources.JDBCDriver;
import org.wildfly.swarm.config.datasources.JDBCDriverConfigurator;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.datasources.DatasourcesFraction;
import org.wildfly.swarm.jpa.JPAFraction;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Bundle {

    public static void main(final String[] args) throws Exception {

        final Properties config = getConfiguration();

        final Container container = new Container();

        final JDBCDriverConfigurator configurator
                                     = (driver) -> {
                    driver.driverDatasourceClassName(config.getProperty(
                            "db.driver.datasource.classname"));
                    driver.xaDatasourceClass(config.getProperty(
                            "db.xa.datasource.classname"));
                    driver.driverModuleName("db.driver.module.name");
                };

        final DataSourceConfigurator<?> dsConfigurator = (dataSource) -> {
            dataSource.driverName("db-driver");
            dataSource.connectionUrl(
                    config.getProperty("db.connection.url"));
            dataSource.userName(config.getProperty("db.user.name"));
            dataSource.password(config.getProperty("db.password"));
        };

        container.fraction(new DatasourcesFraction().jdbcDriver("db-driver",
                                                                configurator)
                .dataSource("java:/comp/env/jdbc/ccm-core/db", dsConfigurator));

        container.fraction(new JPAFraction().inhibitDefaultDatasource()
                .defaultDatasource("java:/comp/env/jdbc/ccm-core/db"));

        //Remove when CCM installer is available
        setup(config);

        container.start();

        final WARArchive deployment = ShrinkWrap.create(WARArchive.class);
        deployment.addAsWebInfResource(
                new ClassLoaderAsset(
                        "META-INF/persistence.xml",
                        Bundle.class.getClassLoader()),
                "classes/META-INF/persistence.xml");
        deployment.addAllDependencies();
        deployment.addAsResource("/themes");
        container.deploy(deployment);
    }

    private static Properties getConfiguration() throws IOException {
        final String defaultConfig = String.format(
                "%s/configuration.properties", System.getProperty("user.dir"));
        final String config = System.getProperty("ccm.config", defaultConfig);

        final FileInputStream stream = new FileInputStream(config);
        final Properties properties = new Properties();
        properties.load(stream);

        return properties;
    }

    private static Properties getSetupParameters() throws IOException {
        final String defaultParameters = String.format(
                "%s/setup.properties", System.getProperty("user.dir"));
        final String parameters = System.getProperty("ccm.setup.parameters",
                                                     defaultParameters);

        final FileInputStream stream = new FileInputStream(parameters);
        final Properties properties = new Properties();
        properties.load(stream);

        return properties;

    }

    private static void setup(final Properties config) throws
            ClassNotFoundException, SQLException, IOException {

        final Properties setupParameters = getSetupParameters();

        Class.forName("org.h2.Driver");
        try (final Connection connection = DriverManager.getConnection(config
                .getProperty("db.connection.url"));
             final Statement statement = connection.createStatement()) {

            final ResultSet result = statement.executeQuery(
                    "SELECT COUNT(*) FROM USERS WHERE NAME NOT LIKE 'public-user'");
            result.next();
            final int numberOfUsers = result.getInt(1);
            result.close();

            if (numberOfUsers <= 0) {
                final String adminName = setupParameters.getProperty(
                        "admin.name");
                final String adminEmail = setupParameters.getProperty(
                        "admin.email");
                final String adminGivenName = setupParameters.getProperty(
                        "admin.givenname");
                final String adminFamilyName = setupParameters.getProperty(
                        "admin.familyname");
                final String adminPassword = setupParameters.getProperty(
                        "admin.password");

                statement.executeUpdate(String.format(
                        "INSERT INTO PARTIES(PARTY_ID, NAME) "
                                + "VALUES(-10, '%s')",
                        adminName));
                statement.executeUpdate(String.format(
                        "INSERT INTO USERS(PARTY_ID, GIVEN_NAME, FAMILY_NAME, EMAIL_ADDRESS, PASSWORD) "
                        + "VALUES (-10, '%s', '%s', '%s', '%s'),",
                        adminGivenName,
                        adminFamilyName,
                        adminEmail,
                        adminPassword
                ));
                statement.executeUpdate("INSERT INTO CCM_ROLES(roleId, name) "
                                                + "VALUES(-10, 'admin'");
                statement.executeUpdate("INSERT INTO ROLE_MEMBERSHIPS("
                                                + "MEMBERSHIP_ID, MEMBER_ID, ROLE_ID) "
                                        + "VALUES(-10, -10, 10)");

                statement.close();
            }

            //} catch(SQLException ex) {
        } finally {

        }

    }

}
