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
import org.libreccm.tests.categories.IntegrationTest;

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

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
@CleanupUsingScript({"cleanup.sql"})
@RequestScoped
public class CoreDataImportTest {

    @Inject
    private ImportHelper importHelper;

    public CoreDataImportTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @BeforeClass
    public static void createResource() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class,
                        "LibreCCM-org.libreccm.portation." +
                                "CoreDataImportTest.war")
                .addPackage(org.libreccm.categorization.Category
                        .class.getPackage())
                .addPackage(org.libreccm.core.CcmObject
                        .class.getPackage())
                .addPackage(org.libreccm.l10n.LocalizedString
                        .class.getPackage())
                .addPackage(org.libreccm.portation.AbstractMarshaller
                        .class.getPackage())
                .addPackage(org.libreccm.security.Group
                        .class.getPackage())
                .addPackage(org.libreccm.workflow.AssignableTask
                        .class.getPackage())
                .addPackage(org.libreccm.configuration.ConfigurationManager
                        .class.getPackage())
                .addPackage(org.libreccm.tests.categories.IntegrationTest
                        .class.getPackage())
                .addPackage(org.libreccm.testutils.EqualsVerifier
                        .class.getPackage())
                .addPackage(org.libreccm.web.CcmApplication
                        .class.getPackage())
                .addPackage(org.libreccm.jpa.EntityManagerProducer
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
    @InSequence(105)
    public void categoriesShouldBeImported() {
        importHelper.importRoleMemberships();
    }

    @Test
    @InSequence(110)
    public void categorizationsShouldBeImported() {
        importHelper.importGroupMemberships();
    }

    @Test
    @InSequence(115)
    public void usersShouldBeImported() {
        importHelper.importGroups();
    }

    @Test
    @InSequence(120)
    public void groupsShouldBeImported() {
        importHelper.importUsers();
    }

    @Test
    @InSequence(125)
    public void groupMembershipsShouldBeImported() {
        importHelper.importTaskAssignments();
    }

    @Test
    @InSequence(130)
    public void rolesShouldBeImported() {
        importHelper.importAssignableTasks();
    }

    @Test
    @InSequence(135)
    public void roleMembershipsShouldBeImported() {
        importHelper.importWorkflowTemplates();
    }

    @Test
    @InSequence(140)
    public void workflowTemplatesShouldBeImported() {
        importHelper.importWorkflows();
    }

    @Test
    @InSequence(145)
    public void workflowsShouldBeImported() {
        importHelper.importCategorizations();
    }

    @Test
    @InSequence(150)
    public void assignableTasksShouldBeImported() {
        importHelper.importPermissions();
    }

    @Test
    @InSequence(155)
    public void taskAssignmentsShouldBeImported() {
        importHelper.importCategories();
    }

    @Test
    @InSequence(160)
    public void permissionsShouldBeImported() {
        importHelper.importRoles();
    }


}
