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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.Event;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
public class ContentItemManagerTest {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private CategoryRepository categoryRepo;

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
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        dependencies.addDependency(MavenDependencies.createDependency(
            "org.libreccm:ccm-core", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
            "org.libreccm:ccm-testutils", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
            "net.sf.saxon:Saxon-HE", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
            "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven",
            ScopeType.RUNTIME, false));
        final File[] libsWithCcmCore = dependencies.resolve().withTransitivity()
            .asFile();

        final List<File> libsList = new ArrayList<>(libsWithCcmCore.length - 1);
        IntStream.range(0, libsWithCcmCore.length).forEach(i -> {
            final File lib = libsWithCcmCore[i];
            if (!lib.getName().startsWith("ccm-core")) {
                libsList.add(lib);
            }
        });
        final File[] libs = libsList.toArray(new File[libsList.size()]);

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

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
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.bebop.Component.class.getPackage())
            .addPackage(com.arsdigita.bebop.util.BebopConstants.class
                .getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.runtime.CCMResourceManager.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppSettingsPane.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationSettingsPane.class)
            .addClass(com.arsdigita.cms.dispatcher.ItemResolver.class)
            .addPackage(com.arsdigita.util.Lockable.class.getPackage())
            .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
            .addPackage(org.librecms.Cms.class.getPackage())
            .addPackage(org.librecms.assets.Asset.class.getPackage())
            .addPackage(org.librecms.attachments.AttachmentList.class
                .getPackage())
            .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
            .addPackage(org.librecms.contentsection.ContentSection.class
                .getPackage())
            .addPackage(org.librecms.contenttypes.Article.class.getPackage())
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addAsLibraries(libs)
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/librecms/contentsection/ContentItemManagerTest/log4j2.xml",
                "log4j2.xml")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(10)
    public void checkInjections() {
        assertThat(sectionRepo, is(not(nullValue())));
        assertThat(itemManager, is(not(nullValue())));
        assertThat(shiro, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void checkShiro() {
        assertThat(shiro.getSecurityManager(), is(not(nullValue())));
        assertThat(shiro.getSystemUser(), is(not(nullValue())));
    }

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
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

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
                   workflowCount, is(3L));
    }

    /**
     * Checks if an {@link IllegalArgumentException} is thrown when the content
     * type of the item to create is not registered with the provided content
     * section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemTypeNotInSection() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        itemManager.createContentItem("Test", section, folder, Event.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNull() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        itemManager.createContentItem(null, section, folder, Article.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsEmpty() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        itemManager.createContentItem("", section, folder, Article.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(1500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemFolderIsNull() {
        final ContentSection section = sectionRepo.findByLabel("info");

        itemManager.createContentItem("Test", section, null, Article.class);
    }

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
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        final WorkflowTemplate workflowTemplate = workflowTemplateRepo
            .findById(-110L);

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
                   workflowCount, is(3L));
    }

    /**
     * Checks if an {@link IllegalArgumentException} is thrown when the content
     * type of the item to create is not registered with the provided content
     * section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemTypeNotInSectionWithWorkflow() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        final WorkflowTemplate workflowTemplate = workflowTemplateRepo
            .findById(-110L);

        itemManager.createContentItem("Test",
                                      section,
                                      folder,
                                      workflowTemplate,
                                      Event.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(2300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNullWithWorkflow() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        final WorkflowTemplate workflowTemplate = workflowTemplateRepo
            .findById(-110L);

        itemManager.createContentItem(null,
                                      section,
                                      folder,
                                      workflowTemplate,
                                      Article.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(2400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemNameIsNullWorkflowIsNull() {
        final ContentSection section = sectionRepo.findByLabel("info");
        final Category folder = section.getRootDocumentsFolder();

        itemManager.createContentItem(null,
                                      section,
                                      folder,
                                      null,
                                      Article.class);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(2600)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createItemFolderIsNullWithWorkflow() {
        final ContentSection section = sectionRepo.findByLabel("info");

        final WorkflowTemplate workflowTemplate = workflowTemplateRepo
            .findById(-110L);

        itemManager.createContentItem("Test",
                                      section,
                                      null,
                                      workflowTemplate,
                                      Article.class);
    }

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

        final Category targetFolder = categoryRepo.findById(-2120L);
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(item.get(), targetFolder);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(3200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveItemNull() {
        final Category targetFolder = categoryRepo.findById(-2120L);
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.move(null, targetFolder);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(3200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveItemFolderNull() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        itemManager.move(item.get(), null);
    }

    @Test
    @InSequence(4100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-copy-to-other-folder.xml",
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
    public void copyToOtherFolder() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final Category targetFolder = categoryRepo.findById(-2120L);
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(item.get(), targetFolder);
    }

    @Test
    @InSequence(4200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-copy-to-same-folder.xml",
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
    public void copyToSameFolder() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final Category targetFolder = categoryRepo.findById(-2110L);
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(item.get(), targetFolder);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(4300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyItemNull() {
        final Category targetFolder = categoryRepo.findById(-2120L);
        assertThat(targetFolder, is(not(nullValue())));

        itemManager.copy(null, targetFolder);
    }

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

    @Test
    @InSequence(5100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-publish.xml",
        excludeColumns = {"categorization_id",
                          "id",
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
    public void publishItem() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        final ContentItem live = itemManager.publish(item.get());
        assertThat(live, is(not(nullValue())));
        assertThat(live.getVersion(), is(ContentItemVersion.LIVE));
    }

    @Test
    @InSequence(5200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-publish.xml",
        excludeColumns = {"categorization_id",
                          "id",
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
    public void publishItemWithLifecycle() {
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        final LifecycleDefinition lifecycleDef = lifecycleDefinitionRepo
            .findById(-200L);
        assertThat(item.isPresent(), is(true));
        assertThat(lifecycleDef, is(not(nullValue())));

        final ContentItem live = itemManager.publish(item.get(), lifecycleDef);
        assertThat(live, is(not(nullValue())));
        assertThat(live.getVersion(), is(ContentItemVersion.LIVE));
    }

    @Test
    @InSequence(5300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemManagerTest/after-republish.xml",
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

    @Test(expected = IllegalArgumentException.class)
    @InSequence(5400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void publishItemNull() {
        itemManager.publish(null);
    }

    @Test(expected = IllegalArgumentException.class)
    @InSequence(5500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void publishItemLifecycleNull() {
        final Optional<ContentItem> draft = itemRepo.findById(-10200L);

        itemManager.publish(draft.get(), null);
    }

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

    @Test(expected = IllegalArgumentException.class)
    @InSequence(6200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentItemManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void unpublishItemNull() {
        itemManager.unpublish(null);
    }

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
