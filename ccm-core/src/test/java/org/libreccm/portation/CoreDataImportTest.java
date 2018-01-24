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
package org.libreccm.portation;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static org.libreccm.testutils.DependenciesHelpers.getModuleDependencies;


/**
 * Test class. Tests the import capabilities of the core module
 * {@code Portation} with data from the trunk implementations
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created on 12/1/16
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript(value = {"cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class CoreDataImportTest {

    @Inject
    private ImportHelper importHelper;

    private enum Types {
        user, group, groupMem, role, roleMem,
        category, categorization, resourceType,
        ccmAppl, domain, domainOwn,
        permission, workflow, taskComment,
        assignableTask, taskDep, taskAssign
    }

    private static final Map<Types, String> fileNames;
    static {
        fileNames = new HashMap<>();
        fileNames.put(Types.user,           "users.xml");
        fileNames.put(Types.group,          "groups.xml");
        fileNames.put(Types.groupMem,       "groupMemberships.xml");
        fileNames.put(Types.role,           "roles.xml");
        fileNames.put(Types.roleMem,        "roleMemberships.xml");
        fileNames.put(Types.category,       "categories.xml");
        fileNames.put(Types.categorization, "categorizations.xml");
        fileNames.put(Types.resourceType,   "resourceTypes.xml");
        fileNames.put(Types.ccmAppl,        "ccmApplications.xml");
        fileNames.put(Types.domain,         "domains.xml");
        fileNames.put(Types.domainOwn,      "domainOwnerships.xml");
        fileNames.put(Types.permission,     "permissions.xml");
        fileNames.put(Types.workflow,       "workflows.xml");
        fileNames.put(Types.taskComment,    "taskComments.xml");
        fileNames.put(Types.assignableTask, "assignableTasks.xml");
        fileNames.put(Types.taskDep,        "taskDependencies.xml");
        fileNames.put(Types.taskAssign,     "taskAssignments.xml");
    }

    public CoreDataImportTest() {
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        final String tmpDirPath = System.getProperty("java.io.tmpdir");
        final Path filesPath = Paths
                .get(tmpDirPath, "libreccm-test", "CoreDataImportTest");
        Files.createDirectories(filesPath);

        for (String fileName : fileNames.values()) {
            final InputStream inputStream = CoreDataImportTest
                    .class
                    .getResourceAsStream(String
                            .format("/portation/trunk-iaw-exports/%s",
                                    fileName));
            try {
                Files.copy(inputStream, filesPath.resolve(fileName));
            } catch(FileAlreadyExistsException e) {
                //destination file already exists
            }
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        final String tmpDirPath = System.getProperty("java.io.tmpdir");
        final Path filesPath = Paths
                .get(tmpDirPath,"libreccm-test", "CoreDataImportTest");
        Files.walkFileTree(filesPath, new SimpleFileVisitor<Path>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes
                                             attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class,
                        "LibreCCM-org.libreccm.portation." +
                                "CoreDataImportTest.war")
                .addPackage(org.libreccm.categorization.Category
                        .class.getPackage())
                .addPackage(org.libreccm.cdi.utils.CdiUtil
                        .class.getPackage())
                .addPackage(org.libreccm.configuration.ConfigurationManager
                        .class.getPackage())
                .addPackage(org.libreccm.core.CcmObject
                        .class.getPackage())
                .addPackage(org.libreccm.jpa.EntityManagerProducer
                        .class.getPackage())
                .addPackage(org.libreccm.l10n.LocalizedString
                        .class.getPackage())
                .addPackage(org.libreccm.portation.AbstractMarshaller
                        .class.getPackage())
                .addPackage(org.libreccm.security.Group
                        .class.getPackage())
                .addPackage(org.libreccm.workflow.AssignableTask
                        .class.getPackage())
                .addPackage(org.libreccm.web.CcmApplication
                        .class.getPackage())
                .addPackage(org.libreccm.tests.categories.IntegrationTest
                        .class.getPackage())
                .addPackage(org.libreccm.testutils.EqualsVerifier
                        .class.getPackage())
                .addAsLibraries(getModuleDependencies())
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TEST SECTION

    @Test
    @InSequence(100)
    public void objectsShouldBeImported() throws IOException {
        final String tmpDirPath = System.getProperty("java.io.tmpdir");
        final Path filesPath = Paths
                .get(tmpDirPath, "libreccm-test", "CoreDataImportTest");
        Files.createDirectories(filesPath);

        // assert for no errors
        Assert.assertFalse(importHelper.importUsers(
                filesPath.resolve(fileNames.get(Types.user))));
        Assert.assertFalse(importHelper.importGroups(
                filesPath.resolve(fileNames.get(Types.group))));
        Assert.assertFalse(importHelper.importGroupMemberships(
                filesPath.resolve(fileNames.get(Types.groupMem))));
        Assert.assertFalse(importHelper.importRoles(
                filesPath.resolve(fileNames.get(Types.role))));
        Assert.assertFalse(importHelper.importRoleMemberships(
                filesPath.resolve(fileNames.get(Types.roleMem))));

        Assert.assertFalse(importHelper.importCategories(
                filesPath.resolve(fileNames.get(Types.category))));
        Assert.assertFalse(importHelper.importCategorizations(
                filesPath.resolve(fileNames.get(Types.categorization))));
        Assert.assertFalse(importHelper.importResourceTypes(
                filesPath.resolve(fileNames.get(Types.resourceType))));
        Assert.assertFalse(importHelper.importCcmApplications(
                filesPath.resolve(fileNames.get(Types.ccmAppl))));
        Assert.assertFalse(importHelper.importDomains(
                filesPath.resolve(fileNames.get(Types.domain))));
        Assert.assertFalse(importHelper.importDomainOwnerships(
                filesPath.resolve(fileNames.get(Types.domainOwn))));

        Assert.assertFalse(importHelper.importPermissions(
                filesPath.resolve(fileNames.get(Types.permission))));

        Assert.assertFalse(importHelper.importWorkflows(
                filesPath.resolve(fileNames.get(Types.workflow))));
        Assert.assertFalse(importHelper.importTaskComments(
                filesPath.resolve(fileNames.get(Types.taskComment))));
        Assert.assertFalse(importHelper.importAssignableTasks(
                filesPath.resolve(fileNames.get(Types.assignableTask))));
        Assert.assertFalse(importHelper.importTaskDependencies(
                filesPath.resolve(fileNames.get(Types.taskDep))));
        Assert.assertFalse(importHelper.importTaskAssignments(
                filesPath.resolve(fileNames.get(Types.taskAssign))));
    }

}
