/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.imexport;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.files.CcmFilesConfiguration;
import org.libreccm.security.Shiro;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"001_create_schema.sql", 
               "002_create_ccm_core_tables.sql", 
               "003_init_hibernate_sequence.sql"})
@CleanupUsingScript(value = {"999_cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class GroupImportIT {

    private static final String IMPORT_MANIFEST_SOURCE = "/imports"
                                                             + "/org.libreccm.imexport.GroupImportTest"
                                                         + "/ccm-export.json";
    private static final String IMPORT_GROUPS_TOC_SOURCE = "/imports"
                                                               + "/org.libreccm.imexport.GroupImportTest"
                                                           + "/org.libreccm.security.Group"
                                                           + "/org.libreccm.security.Group.json";
    private static final String IMPORT_DATA_SOURCE = "/imports"
                                                         + "/org.libreccm.imexport.GroupImportTest"
                                                     + "/org.libreccm.security.Group"
                                                     + "/696592cd-db19-4aca-bb14-06490cfde243.json";

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String CCM_TESTS_DIR = TMP_DIR + "/ccm-tests";
    private static final String IMPORTS_DIR = CCM_TESTS_DIR + "/imports";
    private static final String GROUP_IMPORT_TEST_DIR = IMPORTS_DIR
                                                            + "/org.libreccm.imexport.GroupImportTest";
    private static final String IMPORT_DATA_DIR = GROUP_IMPORT_TEST_DIR
                                                      + "/org.libreccm.security.Group";

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private ImportExport importExport;

    @Inject
    private Shiro shiro;

    public GroupImportIT() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws IOException, URISyntaxException {

        final CcmFilesConfiguration filesConf = confManager
            .findConfiguration(CcmFilesConfiguration.class);
        filesConf.setDataPath(CCM_TESTS_DIR);

//        final Path tmpDirPath = Paths.get(TMP_DIR);
        final Path ccmTestsDirPath = Paths.get(CCM_TESTS_DIR);
        final Path importsPath = Paths.get(IMPORTS_DIR);
        final Path groupImportsTestDirPath = Paths.get(GROUP_IMPORT_TEST_DIR);
        final Path importDataPath = Paths.get(IMPORT_DATA_DIR);

        if (Files.exists(ccmTestsDirPath)) {
            Files.walkFileTree(ccmTestsDirPath,
                               new GroupImportIT.DeleteDirectoryVisitor());
        }

        Files.createDirectory(ccmTestsDirPath);
        Files.createDirectory(importsPath);
        Files.createDirectory(groupImportsTestDirPath);
        Files.createDirectory(importDataPath);

        final InputStream manifestInputStream = getClass()
            .getResourceAsStream(IMPORT_MANIFEST_SOURCE);
        final InputStream groupsTocInputStream = getClass()
            .getResourceAsStream(IMPORT_GROUPS_TOC_SOURCE);
        final InputStream group1DataInputStream = getClass()
            .getResourceAsStream(IMPORT_DATA_SOURCE);

        final Path manifestTargetPath = groupImportsTestDirPath
            .resolve("ccm-export.json");
        final Path groupsTocTargetPath = importDataPath
            .resolve("org.libreccm.security.Group.json");
        final Path group1DataTargetPath = importDataPath
            .resolve("696592cd-db19-4aca-bb14-06490cfde243.json");

        copy(manifestInputStream, manifestTargetPath);
        copy(groupsTocInputStream, groupsTocTargetPath);
        copy(group1DataInputStream, group1DataTargetPath);
    }

    private void copy(final InputStream source, final Path destination) {

        try (final OutputStream outputStream = new FileOutputStream(
            destination.toFile())) {

            int data = source.read();
            while (data != -1) {
                outputStream.write(data);
                data = source.read();
            }
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    @After
    public void tearDown() throws IOException {

        final Path ccmTestsDirPath = Paths.get(CCM_TESTS_DIR);
        Files.walkFileTree(ccmTestsDirPath, new DeleteDirectoryVisitor());
    }

    private class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(
            final Path file, final BasicFileAttributes attrs)
            throws IOException {

            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(
            final Path dir, final IOException exc)
            throws IOException {

            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }

    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.imexport.GroupImportTest.war")
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.files.CcmFiles.class.getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.imexport.Exportable.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.security.Group.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("imports", "imports")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(100)
    public void checkIfFilesAreAvailable() {

        final Path importDataPath = Paths.get(
            TMP_DIR,
            "ccm-tests",
            "imports",
            "org.libreccm.imexport.GroupImportTest");
        final Path manifestPath = importDataPath.resolve("ccm-export.json");
        final Path typeDirPath = importDataPath
            .resolve("org.libreccm.security.Group");
        final Path dataFile1Path = typeDirPath
            .resolve("696592cd-db19-4aca-bb14-06490cfde243.json");

        assertThat(String.format("Path %s does not exist.",
                                 manifestPath.toString()),
                   Files.exists(manifestPath),
                   is(true));
        assertThat(String.format("Path %s does not exist.",
                                 typeDirPath.toString()),
                   Files.exists(typeDirPath),
                   is(true));
        assertThat(String.format("Path %s does not exist.",
                                 dataFile1Path.toString()),
                   Files.exists(dataFile1Path),
                   is(true));
    }

    @Test
    @InSequence(150)
    public void importsAvailable() {

        final List<ImportManifest> imports = importExport
            .listAvailableImportArchivies();

        assertThat(imports.size(), is(1));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/imexport/GroupImportTest/data.yml")
    @ShouldMatchDataSet(
        excludeColumns = {"party_id"},
        orderBy = {"groups.group_id", "parties.party_id"},
        value = "datasets/org/libreccm/imexport/GroupImportTest"
                    + "/after-import-single-group.yml"
    )
    @InSequence(200)
    public void importSingleGroup() {

        shiro.getSystemUser().execute(()
            -> importExport.importEntities(
                "org.libreccm.imexport.GroupImportTest")
        );
    }

}
