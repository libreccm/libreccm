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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.libreccm.testutils.DependenciesHelpers.getCcmCoreDependencies;
import static org.libreccm.testutils.DependenciesHelpers.getModuleDependencies;

/**
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
    private static final Logger log = LogManager.getLogger(FilePortationTest.class);

    @Inject
    private FileRepository fileRepository;

    @Inject
    @Marshals(File.class)
    private FileMarshaller fileMarshaller;

    private static File file;
    private static String filePath =
            "/home/tosmers/Svn/libreccm/ccm_ng/ccm-docrepo/src/test/resources/datasets/org/libreccm/docrepo/FilePortationTest/";

    private static final String f1Txt = "test1.txt";
    private static final String f2Xml = "test2.xml";
    private static final String f3Xml = "test3.xml";
    private static final String f4Xml = "test4.xml";

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
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
//        final PomEquippedResolveStage pom = Maven
//            .resolver()
//            .loadPomFromFile("pom.xml");
//        final PomEquippedResolveStage dependencies = pom
//            .importCompileAndRuntimeDependencies();
//        final java.io.File[] libs = dependencies.resolve().withTransitivity()
//            .asFile();
//
//        for (java.io.File lib : libs) {
//            System.err.printf("Adding file '%s' to test archive...%n", lib
//                              .getName());
//        }
//
//        final PomEquippedResolveStage corePom = Maven.resolver()
//            .loadPomFromFile("../ccm-core/pom.xml");
//        final PomEquippedResolveStage coreDependencies = corePom
//            .importCompileAndRuntimeDependencies();
//        final java.io.File[] coreLibs = coreDependencies.resolve()
//            .withTransitivity().asFile();
//        for (java.io.File lib : coreLibs) {
//            System.err.printf("Adding file '%s' to test archive...%n", lib
//                              .getName());
//        }
//
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
            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
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
    @InSequence(11)
    public void fileRepositoryIsInjected() {
         assertThat(fileRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void fileShouldBeCreated() {
        file = new File();
        file.setName("testname");
        file.setDescription("this is a text description");
        file.setPath(filePath + "filename.txt");
        file.setCreationDate(new Date());
        file.setLastModifiedDate(new Date());
        if (fileRepository != null && file != null) {
            log.info("A dummy for Docrepo.File has been prepared...");
            fileRepository.save(file);
        }
    }

    @Test
    @InSequence(30)
    public void initialCleanUp() {
        java.io.File file1 = new java.io.File(filePath + f1Txt);
        java.io.File file2 = new java.io.File(filePath + f2Xml);
        file1.delete();
        file2.delete();
        assertTrue(!file1.exists()
                && !file2.exists());
    }

    @Test
    @InSequence(100)
    public void aFileShouldBeCreated() {
        java.io.File file = new java.io.File(filePath + f1Txt);
        if (!file.exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                log.info(String.format("%s has successfully been created.",
                        f1Txt));
                fileWriter.write("bloß ein test! - tosmers");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                log.error(String.format("%s could not be created.", f1Txt));
            }
        }
        assertTrue(file.exists());
    }

    @Test
    @InSequence(200)
    public void xmlShouldBeCreated() {
        fileMarshaller.prepare(Format.XML, filePath + f2Xml, false);
        List<File> fileList = Collections.singletonList(file);
        fileMarshaller.exportList(fileList);

        fileMarshaller.prepare(Format.XML, filePath + f3Xml, true);
        fileMarshaller.exportList(fileList);
    }


    @Test
    @InSequence(300)
    public void objectShouldBeImported() {
        log.info("\n\n\n" + file.toString() + "\n\n\n");
        fileMarshaller.prepare(Format.XML, filePath + f2Xml, false);
        fileMarshaller.importFile();
//      objects.forEach(l -> log.info("\n\n\n" + l.toString() + "\n\n\n"));

        fileMarshaller.prepare(Format.XML, filePath + f4Xml, true);
//        fileMarshaller.exportList(objects);
    }
}
