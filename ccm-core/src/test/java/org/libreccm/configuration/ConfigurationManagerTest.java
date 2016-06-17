/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.configuration;

import com.example.TestConfiguration;

import java.io.File;
import java.math.BigDecimal;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
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
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class ConfigurationManagerTest {

    @Inject
    private ConfigurationManager configurationManager;

    @Inject
    private Shiro shiro;

    public ConfigurationManagerTest() {

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
                    "LibreCCM-org.libreccm.configuration."
                        + "ConfigurationManagerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addClass(com.example.TestConfiguration.class)
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource(
                "configs/org/libreccm/configuration/ConfigurationManagerTest/"
                    + "log4j2.xml",
                "log4j2.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(1)
    public void managerIsInjected() {
        assertThat(configurationManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/data.yml")
    @InSequence(2)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/data.yml")
    @InSequence(1100)
    public void loadConfiguration() {
        final ExampleConfiguration configuration = configurationManager
            .findConfiguration(ExampleConfiguration.class);

        assertThat(configuration, is(not(nullValue())));
        assertThat(configuration.getPrice(),
                   is(equalTo(new BigDecimal("98.99"))));
        assertThat(configuration.isEnabled(), is(true));
        assertThat(configuration.getMinTemperature(), is(23.5));
        assertThat(configuration.getItemsPerPage(), is(20L));
        assertThat(configuration.getHelpUrl(),
                   is(equalTo("http://www.example.org")));
        assertThat(configuration.getLanguages().size(), is(2));
        assertThat(configuration.getLanguages(), hasItem("de"));
        assertThat(configuration.getLanguages(), hasItem("en"));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/data.yml")
    @ShouldMatchDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/"
            + "after-save-changed.yml")
    @InSequence(1200)
    public void saveConfiguration() {
        final ExampleConfiguration configuration = configurationManager
            .findConfiguration(ExampleConfiguration.class);

        configuration.setPrice(new BigDecimal("109.99"));
        configuration.setItemsPerPage(30L);
        configuration.addLanguage("es");

        shiro.getSystemUser().execute(
            () -> configurationManager.saveConfiguration(configuration));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/data.yml")
    @InSequence(2100)
    public void loadNewConfiguration() {
        final TestConfiguration configuration = configurationManager
            .findConfiguration(TestConfiguration.class);

        assertThat(configuration, is(not(nullValue())));
        assertThat(configuration.getEnabled(), is(false));
        assertThat(configuration.getItemsPerPage(), is(40L));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/configuration/ConfigurationManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/configuration/"
                    + "ConfigurationManagerTest/after-save-new.yml",
        excludeColumns = {"setting_id"})
    @InSequence(2200)
    public void saveNewConfiguration() {
        shiro.getSystemUser().execute(
            () -> configurationManager.saveConfiguration(
                new TestConfiguration()));
    }

}
