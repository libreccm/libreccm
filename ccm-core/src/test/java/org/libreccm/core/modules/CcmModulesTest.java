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

import static org.hamcrest.CoreMatchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmCore;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class CcmModulesTest {

    @PersistenceContext(name = "LibreCCM")
    private transient EntityManager entityManager;

    public CcmModulesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.core.modules.CcmModulesTest.war")
            .addPackage(CcmCore.class.getPackage())
            .addPackage(CcmModule.class.getPackage())
            .addPackage(org.libreccm.web.Application.class.getPackage())
            .addPackage(org.libreccm.categorization.Category.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml")
            .addAsManifestResource(
                "META-INF/services/org.hibernate.integrator.spi.Integrator")
            .addAsManifestResource(
                "META-INF/services/org.libreccm.core.modules.CcmModule")
            .addAsResource("module-info/org.libreccm.core.CcmCore.properties")
            .addAsResource("ccm-core.config");

    }

    @Test
    public void verifyModules() throws SQLException {
        final Object dataSourceObj = entityManager.getEntityManagerFactory()
            .getProperties().get("javax.persistence.jtaDataSource");

        assertThat(dataSourceObj, is(instanceOf(DataSource.class)));

        final DataSource dataSource = (DataSource) dataSourceObj;
        final Connection connection = dataSource.getConnection();

        assertThat(connection, is(instanceOf(Connection.class)));

        final ResultSet ccmObjectTable = connection.getMetaData()
            .getTables(null, "ccm_core", "ccm_objects", null);
        if (!ccmObjectTable.next()) {
            fail("No metadata for table ccm_core.ccm_objects returned. "
                     + "Table does exist?");
        }

        final ResultSet installedModulesTable = connection.getMetaData()
            .getTables(null, "ccm_core", "installed_modules", null);
        if (!installedModulesTable.next()) {
            fail("No metadata for table ccm_core.installed_modules returned. "
                     + "Table does not exist?");
        }

        final Statement statement = connection.createStatement();
        final ResultSet installedModules = statement.executeQuery(
            "SELECT module_id, module_class_name, status "
                + "FROM ccm_core.installed_modules"
                + " ORDER BY module_class_name");
        final List<String[]> modulesList = new ArrayList<>();
        while (installedModules.next()) {
            createInstalledModuleListEntry(installedModules, modulesList);
        }
        
        assertThat(modulesList.size(), is(1));
        
        assertThat(modulesList.get(0)[0], 
                   is(equalTo(Integer.toString(CcmCore.class.getName().hashCode()))));
        assertThat(modulesList.get(0)[1],
                   is(equalTo(CcmCore.class.getName())));
        assertThat(modulesList.get(0)[2],
                   is(equalTo(ModuleStatus.INSTALLED.toString())));

    }

    private void createInstalledModuleListEntry(
        final ResultSet resultSet, final List<String[]> modulesList)
        throws SQLException {

        final String[] moduleData = new String[3];
        moduleData[0] = Integer.toString(resultSet.getInt("module_id"));
        moduleData[1] = resultSet.getString("module_class_name");
        moduleData[2] = resultSet.getString("status");
        
        modulesList.add(moduleData);
    }

}
