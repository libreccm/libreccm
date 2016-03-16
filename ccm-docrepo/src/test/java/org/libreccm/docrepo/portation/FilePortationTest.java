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
package org.libreccm.docrepo.portation;

import org.apache.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
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
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.FileMarshaller;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.portation.Format;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.workflow.Workflow;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 *
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_docrepo_schema.sql"})
public class FilePortationTest {

    private static final Logger log = Logger.getLogger(FilePortationTest.class);

    @Inject
    private FileMarshaller fileMarshaller;

    private static File file;
    private static String filePath =
        "src/test/resources/datasets/org/libreccm/docrepo/FilePortationTest/";

    @BeforeClass
    public static void setUpClass() {
    }

    @BeforeClass
    public static void createResource() {
        file = new File();
        file.setName("testname");
        file.setDescription("this is a text description");
        file.setPath("test/path");
        file.setCreationDate(new Date());
        file.setLastModifiedDate(new Date());
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
        final java.io.File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (java.io.File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                    lib.getName());
        }

        return ShrinkWrap
                .create(WebArchive.class,
                        "LibreCCM-org.libreccm.docrepo.ArquillianExampleTest.war")
                .addPackage(org.libreccm.core.CcmObject.class.getPackage())
                .addPackage(org.libreccm.security.Permission.class.getPackage())
                .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
                .addPackage(org.libreccm.categorization.Categorization.class.getPackage())
                .addPackage(LocalizedString.class.getPackage())
                .addPackage(Workflow.class.getPackage())
                .addPackage(EntityManagerProducer.class.getPackage())
                .addPackage(MimeTypeConverter.class.getPackage())
                .addPackage(EqualsVerifier.class.getPackage())
                .addPackage(IntegrationTest.class.getPackage())
                .addAsLibraries(libs)
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }


    @Test
    public void xmlShouldBeCreated() {
        fileMarshaller.prepare(Format.XML, filePath + "test1.xml");
        List<File> fileList = Collections.singletonList(file);

        fileMarshaller.exportList(fileList);
    }

    @Test
    public void docrepoFileShouldBeCreated() {
        // TODO: test file import
    }

    @Test
    @InSequence(10)
    public void repoIsInjected() {
        assertThat(fileMarshaller, is(not(nullValue())));
    }
}
