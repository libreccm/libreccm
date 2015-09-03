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
package org.libreccm;

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

import static org.hamcrest.CoreMatchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.CreateSchema;
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmCore;
import org.libreccm.core.modules.ModuleStatus;
import org.libreccm.tests.categories.IntegrationTest;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"clean_schema.sql"})
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
                        "LibreCCM-org.libreccm.CcmModulesTest.war")
                .addAsLibraries(libs)
                .addAsWebInfResource("web.xml")
                .addAsWebInfResource("test-persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
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
                         + "Table does not exist?");
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
        final List<InstalledModuleData> modulesData = new ArrayList<>();
        while (installedModules.next()) {
            createInstalledModuleListEntry(installedModules, modulesData);
        }

        assertThat(modulesData.size(), is(1));

        assertThat(Integer.toString(modulesData.get(0).getModuleId()),
                   is(equalTo(Integer.toString(CcmCore.class.getName().
                                           hashCode()))));
        assertThat(modulesData.get(0).getModuleClassName(),
                   is(equalTo(CcmCore.class.getName())));
        assertThat(modulesData.get(0).getStatus(),
                   is(equalTo(ModuleStatus.INSTALLED.toString())));
    }

    private void createInstalledModuleListEntry(
            final ResultSet resultSet, final List<InstalledModuleData> modulesData)
            throws SQLException {

        final InstalledModuleData moduleData = new InstalledModuleData();
        moduleData.setModuleId(resultSet.getInt("module_id"));
        moduleData.setModuleClassName(resultSet.getString("module_class_name"));
        moduleData.setStatus(resultSet.getString("status"));
        
        modulesData.add(moduleData);
    }
    
    private class InstalledModuleData {
        
        private int moduleId;
        private String moduleClassName;
        private String status;

        public int getModuleId() {
            return moduleId;
        }

        public void setModuleId(final int moduleId) {
            this.moduleId = moduleId;
        }

        public String getModuleClassName() {
            return moduleClassName;
        }

        public void setModuleClassName(final String moduleClassName) {
            this.moduleClassName = moduleClassName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }
        
    }
}
