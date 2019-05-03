/* Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import com.arsdigita.kernel.KernelConfig;

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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.Locale;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.Event;
import org.librecms.contenttypes.News;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

/**
 * Tests for the {@link ContentSectionManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema(
    {
        "001_create_schema.sql",
        "002_create_ccm_cms_tables.sql",
        "003_init_hibernate_sequence.sql"
    }
)
@CleanupUsingScript(
    value = {
        "999_cleanup.sql"
    },
    phase = TestExecutionPhase.BEFORE
)
public class ContentSectionManagerTest {

    @Inject
    private ContentSectionRepository repository;

    @Inject
    private ContentSectionManager manager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private LifecycleDefinitionRepository lifecycleDefRepo;

    @Inject
    private WorkflowRepository workflowRepo;

    public ContentSectionManagerTest() {
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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.cms.contentsection.ContentSectionManagerTest.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class.
                getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.bebop.Component.class.getPackage())
            .addPackage(com.arsdigita.bebop.util.BebopConstants.class
                .getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.runtime.CCMResourceManager.class)
            .addClass(com.arsdigita.dispatcher.RequestContext.class)
            .addClass(com.arsdigita.dispatcher.AccessDeniedException.class)
            .addClass(com.arsdigita.cms.dispatcher.ContentItemDispatcher.class)
            .addClass(com.arsdigita.dispatcher.Dispatcher.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppSettingsPane.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationSettingsPane.class)
            .addClass(org.libreccm.modules.Module.class)
            .addClass(org.libreccm.modules.RequiredModule.class)
            .addClass(org.libreccm.imexport.Exportable.class)
            .addPackage(org.librecms.dispatcher.ItemResolver.class.getPackage())
            .addPackage(com.arsdigita.util.Lockable.class.getPackage())
            .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
            .addPackage(org.librecms.Cms.class.getPackage())
            .addPackage(org.librecms.contentsection.Asset.class.getPackage())
            .addPackage(org.librecms.contentsection.AttachmentList.class
                .getPackage())
            .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
            .addPackage(ContentSection.class.getPackage())
            .addPackage(org.librecms.contenttypes.Article.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            //.addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Check if all injection points work.
     */
    @Test
    @InSequence(1)
    public void checkInjection() {
        assertThat(repository, is(not(nullValue())));
        assertThat(manager, is(not(nullValue())));
        assertThat(roleRepository, is(not(nullValue())));
        assertThat(confManager, is(not(nullValue())));
        assertThat(categoryRepo, is(not(nullValue())));
        assertThat(typeRepo, is(not(nullValue())));
        assertThat(lifecycleDefRepo, is(not(nullValue())));
        assertThat(workflowRepo, is(not(nullValue())));
    }

    /**
     * Tries to create a new content section.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-create.xml",
        excludeColumns = {"object_id",
                          "root_assets_folder_id",
                          "root_documents_folder_id",
                          "permission_id",
                          "role_id",
                          "grantee_id",
                          "unique_id",
                          "uuid",
                          "created",
                          "section_id",
                          "creation_date",
                          "content_section_id",
                          "folder_id",
                          "inherited_from_id"})
    @InSequence(100)
    public void createSection() {
        manager.createContentSection("test");
    }

    /**
     * Tries to rename a content section and checks if the content section, its
     * root folders and its roles have been renamed to reflect the new name.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-rename.xml",
        excludeColumns = {"object_id"})
    @InSequence(200)
    public void renameSection() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();

        manager.renameContentSection(section, "content");

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = new Locale(kernelConfig
            .getDefaultLanguage());

        section.getTitle().addValue(defaultLocale, "content");
        repository.save(section);

        section.getRootDocumentsFolder().setName("content_root");
        section.getRootDocumentsFolder().setDisplayName("content_root");
        section.getRootDocumentsFolder().getTitle().addValue(
            defaultLocale, "content_root");

        section.getRootAssetsFolder().setName("content_assets");
        section.getRootAssetsFolder().setDisplayName("content_assets");
        section.getRootAssetsFolder().getTitle().addValue(
            defaultLocale, "content_assets");

        categoryRepo.save(section.getRootDocumentsFolder());
        categoryRepo.save(section.getRootAssetsFolder());
    }

    /**
     * Tries to add a new role to a content section.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-add-role.xml",
        excludeColumns = {"object_id",
                          "uuid",
                          "role_id",
                          "permission_id",
                          "creation_date",
                          "grantee_id"})
    @InSequence(300)
    public void addRole() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();

        manager.addRoleToContentSection(section,
                                        "reviewer",
                                        ItemPrivileges.VIEW_PUBLISHED,
                                        ItemPrivileges.PREVIEW,
                                        ItemPrivileges.APPROVE);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addRoleToContentSection(org.librecms.contentsection.ContentSection, java.lang.String, java.lang.String...)}
     * throws a {@link IllegalArgumentException} if the section to which the
     * role should be added is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(301)
    public void addRoleSectionIsNull() {
        manager.addRoleToContentSection(null,
                                        "reviewer",
                                        ItemPrivileges.VIEW_PUBLISHED,
                                        ItemPrivileges.PREVIEW,
                                        ItemPrivileges.APPROVE);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addRoleToContentSection(org.librecms.contentsection.ContentSection, java.lang.String, java.lang.String...)}
     * throws a {@link IllegalArgumentException} if the role to add is
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(302)
    public void addRoleNameIsNull() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();

        manager.addRoleToContentSection(section,
                                        null,
                                        ItemPrivileges.VIEW_PUBLISHED,
                                        ItemPrivileges.PREVIEW,
                                        ItemPrivileges.APPROVE);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addRoleToContentSection(org.librecms.contentsection.ContentSection, java.lang.String, java.lang.String...)}
     * throws a {@link IllegalArgumentException} if the name of the role to add
     * is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(302)
    public void addRoleNameIsEmpty() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();

        manager.addRoleToContentSection(section,
                                        " ",
                                        ItemPrivileges.VIEW_PUBLISHED,
                                        ItemPrivileges.PREVIEW,
                                        ItemPrivileges.APPROVE);
    }

    /**
     * Tries to remove a role from a content section.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-remove-role.xml",
        excludeColumns = {"object_id"})
    @InSequence(350)
    public void removeRole() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();
        final Role role = roleRepository.findByName("info_publisher").get();

        manager.removeRoleFromContentSection(section, role);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeRoleFromContentSection(org.librecms.contentsection.ContentSection, org.libreccm.security.Role)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the role to remove.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(351)
    public void removeRoleNull() {
        final ContentSection section = repository
            .findByLabel("info")
            .get();

        manager.removeRoleFromContentSection(section, null);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeRoleFromContentSection(org.librecms.contentsection.ContentSection, org.libreccm.security.Role)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the section from which to role is removed.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(352)
    public void removeRoleSectionIsNull() {
        final Role role = roleRepository.findByName("info_publisher").get();

        manager.removeRoleFromContentSection(null, role);
    }

    /**
     * Tries to add content type to a content section by using
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-add-contenttype.xml",
        excludeColumns = {"object_id",
                          "uuid",
                          "permission_id",
                          "creation_date"})
    @InSequence(400)
    public void addContentTypeToSection() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L)
            .get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14001L)
            .get();

        manager.addContentTypeToSection(Event.class,
                                        section,
                                        lifecycleDef,
                                        workflowTemplate);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * does nothing if there is already a {@link ContentType} for the provided
     * type in the provided content section.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @InSequence(500)
    public void addAlreadyAddedContentTypeToSection() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L)
            .get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14002L)
            .get();

        manager.addContentTypeToSection(News.class,
                                        section,
                                        lifecycleDef,
                                        workflowTemplate);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with {@code null}
     * for the type to add.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(600)
    public void addContentTypeToSectionTypeIsNull() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L).get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14002L).get();

        manager.addContentTypeToSection(null,
                                        section,
                                        lifecycleDef,
                                        workflowTemplate);

    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with {@code null}
     * for the section of the type to add.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void addContentTypeToSectionSectionIsNull() {
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L).get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14002L).get();

        manager.addContentTypeToSection(Event.class,
                                        null,
                                        lifecycleDef,
                                        workflowTemplate);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with {@code null}
     * for the default lifecycle for the type.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(800)
    public void addContentTypeToSectionLifecycleIsNull() {
        final ContentSection section = repository.findById(1100L).get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14001L).get();

        manager.addContentTypeToSection(Event.class,
                                        section,
                                        null,
                                        workflowTemplate);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with {@code null}
     * for the default workflow of the type to add.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(900)
    public void addContentTypeToSectionWorkflowIsNull() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L).get();

        manager.addContentTypeToSection(Event.class,
                                        section,
                                        lifecycleDef,
                                        null);

    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with a lifecycle
     * which does not belong to the provided {@link ContentSection}.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1000)
    public void addContentTypeToSectionLifecycleNotInSection() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13003L).get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14001L).get();

        manager.addContentTypeToSection(Event.class,
                                        section,
                                        lifecycleDef,
                                        workflowTemplate);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#addContentTypeToSection(java.lang.Class, org.librecms.contentsection.ContentSection, org.librecms.lifecycle.LifecycleDefinition, org.libreccm.workflow.WorkflowTemplate)}.
     * throws an {@link IllegalArgumentException} when called with a workflow
     * which does not belong to the provided {@link ContentSection}.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1100)
    public void addContentTypeToSectionWorkflowNoInSection() {
        final ContentSection section = repository.findById(1100L).get();
        final LifecycleDefinition lifecycleDef = lifecycleDefRepo
            .findById(13002L).get();
        final Workflow workflowTemplate = workflowRepo
            .findById(14003L).get();

        manager.addContentTypeToSection(Event.class,
                                        section,
                                        lifecycleDef,
                                        workflowTemplate);
    }

    /**
     * Verifies the return value of
     * {@link ContentSectionManager#hasContentType(java.lang.Class, org.librecms.contentsection.ContentSection)}.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @InSequence(1200)
    public void verifyHasContentType() {
        final ContentSection section = repository.findById(1100L).get();

        assertThat(manager.hasContentType(Article.class, section), is(true));
        assertThat(manager.hasContentType(News.class, section), is(true));
        assertThat(manager.hasContentType(Event.class, section), is(false));
    }

    /**
     * Tries to remove an unused content type from a section.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-remove-contenttype.xml")
    @InSequence(1300)
    public void removeContentTypeFromSection() {
        final ContentSection section = repository.findById(1100L).get();

        manager.removeContentTypeFromSection(News.class, section);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeContentTypeFromSection(java.lang.Class, org.librecms.contentsection.ContentSection)}
     * does nothing if the provided section has no {@link ContentType} for the
     * provided subclass of {@link ContentItem}.
     */
    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @InSequence(1301)
    public void removeNotExistingContentTypeFromSection() {
        final ContentSection section = repository.findById(1100L).get();

        manager.removeContentTypeFromSection(Event.class, section);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeContentTypeFromSection(java.lang.Class, org.librecms.contentsection.ContentSection)}
     * throws a {@link IllegalArgumentException} if the type to delete is still
     * in use.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void removeContentTypeFromSectionTypeInUse() {
        final ContentSection section = repository.findById(1100L).get();

        manager.removeContentTypeFromSection(Article.class, section);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeContentTypeFromSection(java.lang.Class, org.librecms.contentsection.ContentSection)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the type to remove.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void removeContentTypeFromSectionTypeIsNull() {
        final ContentSection section = repository.findById(1100L).get();

        manager.removeContentTypeFromSection(null, section);
    }

    /**
     * Verifies that
     * {@link ContentSectionManager#removeContentTypeFromSection(java.lang.Class, org.librecms.contentsection.ContentSection)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the section from which the type is removed.
     */
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void removeContentTypeFromSectionSectionIsNull() {
        manager.removeContentTypeFromSection(News.class, null);
    }

}
