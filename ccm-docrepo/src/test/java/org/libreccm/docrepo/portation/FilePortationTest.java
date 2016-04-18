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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.FileMarshaller;
import org.libreccm.docrepo.FileRepository;
import org.libreccm.portation.Format;
import org.libreccm.portation.Marshals;
import org.libreccm.tests.categories.IntegrationTest;

import javax.inject.Inject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 *
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 13/01/2016
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_docrepo_schema.sql"})
public class FilePortationTest {

    @Inject
    private FileRepository fileRepository;

    @Inject
    @Marshals(File.class)
    private FileMarshaller fileMarshaller;

    private static File file;
    private static String filePath =
            "/home/tosmers/Svn/libreccm/ccm_ng/ccm-docrepo/src/test/resources/datasets/org/libreccm/docrepo/FilePortationTest/";

    @BeforeClass
    public static void setUpClass() {
    }

    @BeforeClass
    public static void createResource() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
//        file = new File();
//        file.setName("testname");
//        file.setDescription("this is a text description");
//        file.setPath("test/path");
//        file.setCreationDate(new Date());
//        file.setLastModifiedDate(new Date());
//        fileRepository.save(file);
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
        final java.io.File[] libs = dependencies.resolve().withTransitivity()
            .asFile();

        for (java.io.File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n", lib
                              .getName());
        }

        final PomEquippedResolveStage corePom = Maven.resolver()
            .loadPomFromFile("../ccm-core/pom.xml");
        final PomEquippedResolveStage coreDependencies = corePom
            .importCompileAndRuntimeDependencies();
        final java.io.File[] coreLibs = coreDependencies.resolve()
            .withTransitivity().asFile();
        for (java.io.File lib : coreLibs) {
            System.err.printf("Adding file '%s' to test archive...%n", lib
                              .getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.docrepo.FilePortationTest.war")
            .addPackage(
                org.libreccm.auditing.AbstractAuditedEntityRepository.class
                .getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.ConfigurationManager.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.docrepo.FileMarshaller.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.portal.Portlet.class.getPackage())
            .addPackage(org.libreccm.portation.AbstractMarshaller.class
                .getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(com.arsdigita.xml.CCMTransformerFactory.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsLibraries(coreLibs)
            .addAsResource("META-INF/jboss-deployment-structure.xml", 
                           "META-INF/jboss-deployment-structure.xml")
            .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(10)
    public void fileMarshallerIsInjected() {
        assertThat(fileMarshaller, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void fileRepositoryIsInjected() {
         assertThat(fileRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(100)
    public void xmlShouldBeCreated() {
//        fileMarshaller.prepare(Format.XML, filePath + "test1.xml");
//        List<File> fileList = Collections.singletonList(file);
//
//        fileMarshaller.exportList(fileList);
    }

    @Test
    @InSequence(200)
    public void aFileShouldBeCreated() {
        java.io.File file = new java.io.File(filePath + "test.txt");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            System.out.print("\n\n\n\n\n\n\n\n\n\n Fehler \n\n\n\n\n\n\n\n\n\n");
            fileWriter.write("bloß ein test! - tosmers");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.print("\n\n\n\n\n\n\n\n\n\n Fehler \n\n\n\n\n\n\n\n\n\n");
        }
        assertTrue(file.exists());
    }

}
