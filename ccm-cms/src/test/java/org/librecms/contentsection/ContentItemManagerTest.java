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
package org.librecms.contentsection;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
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
import org.junit.runner.RunWith;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.Event;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 * Tests for the {@link ContentItemManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
@CleanupUsingScript(value = {"cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class ContentItemManagerTest {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private Shiro shiro;

    @Inject
    private WorkflowTemplateRepository workflowTemplateRepo;

    @Inject
    private LifecycleDefinitionRepository lifecycleDefinitionRepo;

    @Inject
    private EntityManager entityManager;

    public ContentItemManagerTest() {
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
                    "LibreCCM-org.librecms.contentsection.ContentItemManagerTest.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addClass(org.libreccm.portation.Portable.class)
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
            .addClass(org.librecms.dispatcher.ItemResolver.class)
            .addPackage(com.arsdigita.util.Lockable.class.getPackage())
            .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
            .addPackage(org.librecms.Cms.class.getPackage())
            .addPackage(org.librecms.contentsection.Asset.class.getPackage())
            .addPackage(org.librecms.contentsection.AttachmentList.class
                .getPackage())
            .addPackage(org.librecms.assets.BinaryAsset.class.getPackage())
            .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
            .addPackage(org.librecms.contentsection.ContentSection.class
                .getPackage())
            .addPackage(org.librecms.contenttypes.Article.class.getPackage()).
            addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            //            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/librecms/contentsection/ContentItemManagerTest/log4j2.xml",
                "log4j2.xml")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Checks if the injection points work.
     */
    @Test
    @InSequence(10)
    public void checkInjections() {
        assertThat(sectionRepo, is(not(nullValue())));
        assertThat(itemRepo, is(not(nullValue())));
        assertThat(itemManager, is(not(nullValue())));
        assertThat(folderRepo, is(not(nullValue())));
        assertThat(shiro, is(not(nullValue())));
        assertThat(workflowTemplateRepo, is(not(nullValue())));
        assertThat(lifecycleDefinitionRepo, is(not(nullValue())));
        assertThat(entityManager, is(not(nullValue())));
    }

    /**
     * Verify that Shiro is working.
     */
    @Test
    @InSequence(20)
    public void checkShiro() {
        assertThat(shiro.getSecurityManager(), is(not(nullValue())));
        assertThat(shiro.getSystemUser(), is(not(nullValue())));
    }

    /**
     * Tries to create a new content item using
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, java.lang.Class)}.
     */
    @Test
    @InSequence(1100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-create-contentitem.xml",
        excludeColumns = {"categorization_id",
                          "id",
                          "item_uuid",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "rev",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void createContentItem() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            final Article article = itemManager.createContentItem("new-article",
                                                                  section,
                                                                  folder,
                                                                  Article.class);

            assertThat("DisplayName has not the expected value.",
                       article.getDisplayName(), is(equalTo("new-article")));
            final Locale locale = new Locale("en");
            assertThat("Name has not the expected value.",
                       article.getName().getValue(locale),
                       is(equalTo("new-article")));
            assertThat("workflow was not added to content item.",
                       article.getWorkflow(), is(not(nullValue())));

            final TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(w) FROM Workflow w", Long.class);
            final long workflowCount = query.getSingleResult();
            assertThat("Expected three workflows in database.",
                       workflowCount, is(4L));
        });
    }

    /**
     * Checks if an {@link IllegalArgumentException} is thrown if
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, java.lang.Class)}
     * is called for a content type which is not registered for the provided
     * content section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemTypeNotInSection() {
        final ContentSection section = sectionRepo
            .findByLabel("info")
            .get();
        final Folder folder = section.getRootDocumentsFolder();

        itemManager.createContentItem("Test", section, folder, Event.class);
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, java.lang.Class)}
     * is called with {@code null} for the name of the new item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNull() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            itemManager.createContentItem(null, section, folder, Article.class);
        });
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, java.lang.Class)}
     * is called with an empty string for the name of the new content item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsEmpty() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            itemManager.createContentItem(" ", section, folder, Article.class);
        });
    }

    /**
     * Verifies that an {@link IllegalArgumentException} is thrown if
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, java.lang.Class)}
     * is called with {@code null} for the folder in which the new item is
     * created.
     */
    @Test(expected = NullPointerException.class)
    @InSequence(1500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(NullPointerException.class)
    public void createItemFolderIsNull() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();

            itemManager.createContentItem("Test", section, null, Article.class);
        });
    }

    /**
     * Tries to create a new content item with an alternative workflow using
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, org.libreccm.workflow.WorkflowTemplate, java.lang.Class)}.
     */
    @Test
    @InSequence(2100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/"
                    + "after-create-contentitem-with-workflow.xml",
        excludeColumns = {"categorization_id",
                          "id",
                          "item_uuid",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "rev",
                          "task_id",
                          "timestamp",
                          "uuid",
                          "workflow_id"
        })
    public void createContentItemWithWorkflow() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            final WorkflowTemplate workflowTemplate = workflowTemplateRepo
                .findById(-110L).get();

            final Article article = itemManager.createContentItem(
                "new-article",
                section,
                folder,
                workflowTemplate,
                Article.class);

            assertThat("DisplayName has not the expected value.",
                       article.getDisplayName(), is(equalTo("new-article")));
            final Locale locale = new Locale("en");
            assertThat("Name has not the expected value.",
                       article.getName().getValue(locale),
                       is(equalTo("new-article")));
            assertThat("workflow was not added to content item.",
                       article.getWorkflow(), is(not(nullValue())));

            final TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(w) FROM Workflow w", Long.class);
            final long workflowCount = query.getSingleResult();
            assertThat("Expected three workflows in database.",
                       workflowCount, is(4L));
        });
    }

    /**
     * Verifies that
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, org.libreccm.workflow.WorkflowTemplate, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided type of the
     * item to create is not registered with the provided content section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemTypeNotInSectionWithWorkflow() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            final WorkflowTemplate workflowTemplate = workflowTemplateRepo
                .findById(-110L).get();

            itemManager.createContentItem("Test",
                                          section,
                                          folder,
                                          workflowTemplate,
                                          Event.class);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, org.libreccm.workflow.WorkflowTemplate, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the name of the new item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNullWithWorkflow() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            final WorkflowTemplate workflowTemplate = workflowTemplateRepo
                .findById(-110L).get();

            itemManager.createContentItem(null,
                                          section,
                                          folder,
                                          workflowTemplate,
                                          Article.class);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, org.libreccm.workflow.WorkflowTemplate, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the name of the new item and for thw workflow of the new item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNullWorkflowIsNull() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();
            final Folder folder = section.getRootDocumentsFolder();

            itemManager.createContentItem(null,
                                          section,
                                          folder,
                                          null,
                                          Article.class);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemManager#createContentItem(java.lang.String, org.librecms.contentsection.ContentSection, org.librecms.contentsection.Folder, org.libreccm.workflow.WorkflowTemplate, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the folder in which the new item is created.
     */
    @Test(expected = NullPointerException.class)
    @InSequence(2600)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(NullPointerException.class)
    public void createItemFolderIsNullWithWorkflow() {
        shiro.getSystemUser().execute(() -> {
            final ContentSection section = sectionRepo
                .findByLabel("info")
                .get();

            final WorkflowTemplate workflowTemplate = workflowTemplateRepo
                .findById(-110L).get();

            itemManager.createContentItem("Test",
                                          section,
                                          null,
                                          workflowTemplate,
                                          Article.class);
        });
    }

    /**
     * Tries to move an item a folder in the same content section using
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(3100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-move.xml",
        excludeColumns = {"categorization_id",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "workflow_id"
        })
    public void moveItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final Folder targetFolder = folderRepo.findById(-2120L).get();
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(item.get(), targetFolder);
    }

    /**
     * Tries to move an item a folder in another content section using
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(3110)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-move-to-other-section.xml",
        excludeColumns = {"categorization_id",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "workflow_id"
        })
    public void moveItemToOtherContentSection() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        final Folder targetFolder = folderRepo.findById(-2300L).get();

        assertThat(item.isPresent(), is(true));
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(item.get(), targetFolder);
    }

    /**
     * Verifies that null null null null null null null null null null null null
     * null null null null null null null null null     {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder) 
     * throws an {@link IllegalArgumentException} if the type of the item to
     * copy has not been registered in content section to which the target
     * folder belongs.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(4120)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveToOtherContentSectionTypeNotPresent() {
        final Optional<ContentItem> item = itemRepo.findById(-10400L);
        final Folder targetFolder = folderRepo.findById(-2300L).get();

        assertThat(item.isPresent(), is(true));
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(item.get(), targetFolder);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item to move.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(3200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveItemNull() {
        final Folder targetFolder = folderRepo.findById(-2120L).get();
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(null, targetFolder);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for target folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(3200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveItemTargetFolderIsNull() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        itemManager.move(item.get(), null);
    }

    /**
     * Tries to copy an item to another folder in the same content section using
     * {@link ContentItemManager#copy(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(4100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-copy-to-other-folder.xml",
        excludeColumns = {"asset_id",
                          "attachment_id",
                          "attachment_list_id",
                          "categorization_id",
                          "id",
                          "item_id",
                          "item_uuid",
                          "lifecycle_id",
                          "list_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void copyToOtherFolder() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final Folder targetFolder = folderRepo.findById(-2120L).get();
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(item.get(), targetFolder);
    }

    /**
     * Tries to copy an item to another folder in another content section using
     * {@link ContentItemManager#copy(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(4110)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-copy-to-folder-in-other-section.xml",
        excludeColumns = {"asset_id",
                          "attachment_id",
                          "attachment_list_id",
                          "categorization_id",
                          "id",
                          "item_id",
                          "item_uuid",
                          "lifecycle_id",
                          "list_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void copyToFolderInOtherSection() {
        final Optional<ContentItem> source = itemRepo.findById(-10100L);
        final Folder targetFolder = folderRepo.findById(-2300L).get();

        assertThat(source.isPresent(), is(true));
        assertThat(targetFolder, is(not(nullValue())));

        final ContentItem target = itemManager.copy(source.get(), targetFolder);

        assertThat(target.getUuid(), is(not(equalTo(source.get().getUuid()))));
        assertThat(target.getItemUuid(), is(equalTo(target.getUuid())));
    }

    /**
     * Verifies that
     * {@link ContentItemManager#copy(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the type of the item to
     * copy has not been registered in content section to which the target
     * folder belongs.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(4120)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyToFolderInOtherSectionTypeNotPresent() {
        final Optional<ContentItem> source = itemRepo.findById(-10400L);
        final Folder targetFolder = folderRepo.findById(-2300L).get();

        assertThat(source.isPresent(), is(true));
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(source.get(), targetFolder);
    }

    /**
     * Tries to create a copy of an item it its folder by using
     * {@link ContentItemManager#copy(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}.
     * The method should append a number as suffix to the name of the item to
     * make the name unique inside the folder.
     */
    @Test
    @InSequence(4200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-copy-to-same-folder.xml",
        excludeColumns = {"asset_id",
                          "attachment_id",
                          "attachment_list_id",
                          "categorization_id",
                          "id",
                          "item_id",
                          "item_uuid",
                          "lifecycle_id",
                          "list_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void copyToSameFolder() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final Folder targetFolder = folderRepo.findById(-2110L).get();
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(item.get(), targetFolder);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item to copy.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(4300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyItemNull() {
        final Folder targetFolder = folderRepo.findById(-2120L).get();
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(null, targetFolder);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#move(org.librecms.contentsection.ContentItem, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the target folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(4400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyItemToFolderNull() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        itemManager.copy(item.get(), null);
    }

    /**
     * Tries to publish an item using
     * {@link ContentItemManager#publish(org.librecms.contentsection.ContentItem)}.
     * This test only verifies to basic functionality of the {@code publish}
     * method. For complex content items it is recommended to create additional
     * tests to verify that the item is published correctly.
     */
    @Test
    @InSequence(5100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-publish.xml",
        excludeColumns = {"asset_id",
                          "attachment_id",
                          "attachment_list_id",
                          "categorization_id",
                          "id",
                          "item_id",
                          "item_uuid",
                          "lifecycle_id",
                          "list_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void publishItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final ContentItem live = itemManager.publish(item.get());
        assertThat(live, is(not(nullValue())));
        assertThat(live.getVersion(), is(ContentItemVersion.LIVE));
    }

    /**
     * Tries to publish an item with an alternative lifecycle using
     * {@link ContentItemManager#publish(org.librecms.contentsection.ContentItem, org.librecms.lifecycle.LifecycleDefinition)}.
     * This test only verifies to basic functionality of the {@code publish}
     * method. For complex content items it is recommended to create additional
     * tests to verify that the item is published correctly.
     */
    @Test
    @InSequence(5200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-publish.xml",
        excludeColumns = {"asset_id",
                          "attachment_id",
                          "attachment_list_id",
                          "categorization_id",
                          "id",
                          "item_id",
                          "item_uuid",
                          "lifecycle_id",
                          "list_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "uuid",
                          "timestamp",
                          "workflow_id"
        })
    public void publishItemWithLifecycle() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        final LifecycleDefinition lifecycleDef = lifecycleDefinitionRepo
            .findById(-200L).get();
        assertThat(item.isPresent(), is(true));
        assertThat(lifecycleDef, is(not(nullValue())));

        final ContentItem live = itemManager.publish(item.get(), lifecycleDef);
        assertThat(live, is(not(nullValue())));
        assertThat(live.getVersion(), is(ContentItemVersion.LIVE));
    }

    /**
     * Tries to republish an item using
     * {@link ContentItemManager#publish(org.librecms.contentsection.ContentItem)}.
     */
    @Test
    @InSequence(5300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-republish.xml",
        excludeColumns = {"categorization_id",
                          "id",
                          "item_uuid",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "task_id",
                          "timestamp",
                          "uuid",
                          "workflow_id"})
    public void republishItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10200L);
        assertThat(item.isPresent(), is(true));

        item.get().getName().addValue(Locale.ENGLISH, "article2-edited");
        item.get().getTitle()
            .addValue(Locale.ENGLISH, "Article has been edited");
        itemRepo.save(item.get());

        final Optional<ContentItem> draft = itemRepo.findById(-10200L);
        assertThat(draft.get().getName().getValue(Locale.ENGLISH),
                   is(equalTo("article2-edited")));
        itemManager.publish(draft.get());
    }

    /**
     * Verifies that
     * {@link ContentItemManager#publish(org.librecms.contentsection.ContentItem)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item to publish.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(5400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void publishItemNull() {
        final ContentItem item = null;

        itemManager.publish(item);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#publish(org.librecms.contentsection.ContentItem, org.librecms.lifecycle.LifecycleDefinition)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the lifecycle to use.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(5500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void publishItemLifecycleIsNull() {
        final Optional<ContentItem> draft = itemRepo.findById(-10200L);

        itemManager.publish(draft.get(), null);
    }

    /**
     * Tries to unpublish (delete the live version) an item using
     * {@link ContentItemManager#unpublish(org.librecms.contentsection.ContentItem)}.
     */
    @Test
    @InSequence(6000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-unpublish.xml",
        excludeColumns = {"categorization_id",
                          "id",
                          "lifecycle_id",
                          "object_id",
                          "object_order",
                          "phase_id",
                          "rev",
                          "revend",
                          "task_id",
                          "timestamp",
                          "uuid",
                          "workflow_id"})
    public void unpublishItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10200L);
        assertThat(item.isPresent(), is(true));

        itemManager.unpublish(item.get());
    }

    /**
     * Verifies that
     * {@link ContentItemManager#unpublish(org.librecms.contentsection.ContentItem)}
     * does nothing if called for an item which is not live.
     */
    @Test
    @InSequence(6100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    public void unpublishNonLiveItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10300L);
        assertThat(item.isPresent(), is(true));

        itemManager.unpublish(item.get());
    }

    /**
     * Verifies that
     * {@link ContentItemManager#unpublish(org.librecms.contentsection.ContentItem)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item to unpublish.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(6200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void unpublishItemNull() {
        final ContentItem item = null;
        itemManager.unpublish(item);
    }

    /**
     * Verifies that
     * {@link ContentItemManager#isLive(org.librecms.contentsection.ContentItem)}
     * returns the expected value for the items in the test database.
     */
    @Test
    @InSequence(7000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    public void isLive() {
        final Optional<ContentItem> item1 = itemRepo.findById(-10100L);
        final Optional<ContentItem> item2 = itemRepo.findById(-10200L);
        final Optional<ContentItem> item3 = itemRepo.findById(-10300L);
        final Optional<ContentItem> item4 = itemRepo.findById(-10400L);

        assertThat(item1.isPresent(), is(true));
        assertThat(item2.isPresent(), is(true));
        assertThat(item3.isPresent(), is(true));
        assertThat(item4.isPresent(), is(true));

        assertThat(itemManager.isLive(item1.get()), is(false));
        assertThat(itemManager.isLive(item2.get()), is(true));
        assertThat(itemManager.isLive(item3.get()), is(false));
        assertThat(itemManager.isLive(item4.get()), is(false));
    }

    /**
     * Tries to retrieve the live versions of the items in the test database
     * using
     * {@link ContentItemManager#getLiveVersion(org.librecms.contentsection.ContentItem, java.lang.Class)}.
     * The method returns an {@link Optional} which is empty if there is not
     * live version.
     */
    @Test
    @InSequence(8000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    public void getLiveVersion() {
        final Optional<ContentItem> draft1 = itemRepo.findById(-10100L);
        final Optional<ContentItem> draft2 = itemRepo.findById(-10200L);
        final Optional<ContentItem> draft3 = itemRepo.findById(-10300L);
        final Optional<ContentItem> draft4 = itemRepo.findById(-10400L);

        final Optional<ContentItem> live2 = itemRepo.findById(-99200L);

        assertThat(itemManager.getLiveVersion(draft1.get(),
                                              ContentItem.class).isPresent(),
                   is(false));
        final Optional<Article> liveVersion = itemManager.getLiveVersion(
            draft2.get(), Article.class);
        assertThat(liveVersion.isPresent(),
                   is(true));
        assertThat(liveVersion.get().getObjectId(),
                   is(-99200L));
        assertThat(liveVersion.get().getItemUuid(),
                   is(equalTo("acae860f-2ffa-450d-b486-054292f0dae6")));
        assertThat(liveVersion.get().getVersion(),
                   is(ContentItemVersion.LIVE));
        assertThat(itemManager.getLiveVersion(draft3.get(),
                                              ContentItem.class).isPresent(),
                   is(false));
        assertThat(itemManager.getLiveVersion(draft4.get(),
                                              ContentItem.class).isPresent(),
                   is(false));

        final Optional<ContentItem> fromLive = itemManager.getLiveVersion(
            live2.get(), ContentItem.class);
        assertThat(fromLive.isPresent(),
                   is(true));
        assertThat(fromLive.get().getObjectId(),
                   is(-99200L));
        assertThat(fromLive.get().getItemUuid(),
                   is(equalTo("acae860f-2ffa-450d-b486-054292f0dae6")));
        assertThat(fromLive.get().getVersion(),
                   is(ContentItemVersion.LIVE));
    }

    /**
     * Tries to retrieve the draft version of the items in the test database
     * from draft or live versions of the items in the test database using
     * {@link ContentItemManager#getDraftVersion(org.librecms.contentsection.ContentItem, java.lang.Class)}.
     */
    @Test
    @InSequence(8100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    // getDraftVersion
    public void getDraftVersion() {
        final Optional<ContentItem> draft1 = itemRepo.findById(-10100L);
        final Optional<ContentItem> draft2 = itemRepo.findById(-10200L);
        final Optional<ContentItem> draft3 = itemRepo.findById(-10300L);
        final Optional<ContentItem> draft4 = itemRepo.findById(-10400L);

        assertThat(itemManager.getDraftVersion(draft1.get(),
                                               ContentItem.class).getObjectId(),
                   is(-10100L));
        assertThat(itemManager.getDraftVersion(draft2.get(),
                                               ContentItem.class).getObjectId(),
                   is(-10200L));
        assertThat(itemManager.getDraftVersion(draft3.get(),
                                               ContentItem.class).getObjectId(),
                   is(-10300L));
        assertThat(itemManager.getDraftVersion(draft4.get(),
                                               ContentItem.class).getObjectId(),
                   is(-10400L));

        final Optional<ContentItem> live2 = itemRepo.findById(-99200L);

        final ContentItem draftVersion = itemManager.getDraftVersion(
            live2.get(), ContentItem.class);

        assertThat(draftVersion, is(not(nullValue())));
        assertThat(draftVersion.getObjectId(), is(-10200L));
        assertThat(draftVersion.getItemUuid(),
                   is(equalTo("acae860f-2ffa-450d-b486-054292f0dae6")));
        assertThat(draftVersion.getVersion(),
                   is(ContentItemVersion.DRAFT));

    }

}
