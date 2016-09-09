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
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
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
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.News;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.inject.Inject;

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
public class ContentItemRepositoryTest {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private CategoryRepository categoryRepo;

    public ContentItemRepositoryTest() {
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
                    "LibreCCM-org.librecms.contentsection.ContentItemRepositoryTest.war")
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
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(10)
    public void isRepositoryInjected() {
        assertThat(itemRepo, is(not(nullValue())));
        assertThat(categoryRepo, is(not(nullValue())));
    }

    @Test
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void findByIdAndType() {
        final Optional<Article> article1 = itemRepo.findById(
            -10100L, Article.class);
        final Optional<Article> article2 = itemRepo.findById(
            -10200L, Article.class);
        final Optional<Article> article3 = itemRepo.findById(
            -10300L, Article.class);
        final Optional<News> news1 = itemRepo.findById(
            -10400L, News.class);

        final Optional<Article> newsAsArticle = itemRepo.findById(
            -10400, Article.class);
        final Optional<News> articleAsNews = itemRepo.findById(
            -10200L, News.class);

        assertThat(article1.isPresent(), is(true));
        assertThat(article1.get().getDisplayName(), is(equalTo("article1")));
        assertThat(article2.isPresent(), is(true));
        assertThat(article2.get().getDisplayName(), is(equalTo("article2")));
        assertThat(article3.isPresent(), is(true));
        assertThat(article3.get().getDisplayName(), is(equalTo("article3")));
        assertThat(news1.isPresent(), is(true));
        assertThat(news1.get().getDisplayName(), is(equalTo("news1")));

        assertThat(newsAsArticle.isPresent(), is(false));
        assertThat(articleAsNews.isPresent(), is(false));
    }

    @Test
    @InSequence(200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void findByUuidAndType() {
        final Optional<Article> article1 = itemRepo.findByUuid(
            "aed4b402-1180-46c6-b42d-7245f4dca248", Article.class);
        final Optional<Article> article2 = itemRepo.findByUuid(
            "acae860f-2ffa-450d-b486-054292f0dae6", Article.class);
        final Optional<Article> article3 = itemRepo.findByUuid(
            "f4b38abb-234b-4354-bc92-e36c068a1ebd", Article.class);
        final Optional<News> news1 = itemRepo.findByUuid(
            "d9ea527d-c6e3-4bdd-962d-c0a1a80c6c72", News.class);

        final Optional<Article> newsAsArticle = itemRepo.findByUuid(
            "d9ea527d-c6e3-4bdd-962d-c0a1a80c6c72", Article.class);
        final Optional<News> articleAsNews = itemRepo.findByUuid(
            "acae860f-2ffa-450d-b486-054292f0dae6", News.class);

        assertThat(article1.isPresent(), is(true));
        assertThat(article1.get().getDisplayName(), is(equalTo("article1")));
        assertThat(article2.isPresent(), is(true));
        assertThat(article2.get().getDisplayName(), is(equalTo("article2")));
        assertThat(article3.isPresent(), is(true));
        assertThat(article3.get().getDisplayName(), is(equalTo("article3")));
        assertThat(news1.isPresent(), is(true));
        assertThat(news1.get().getDisplayName(), is(equalTo("news1")));

        assertThat(newsAsArticle.isPresent(), is(false));
        assertThat(articleAsNews.isPresent(), is(false));
    }

    @Test
    @InSequence(300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void findByType() {
        final List<Article> articles = itemRepo.findByType(Article.class);
        assertThat(articles, is(not(nullValue())));
        assertThat(articles.size(), is(3));

        final List<News> news = itemRepo.findByType(News.class);
        assertThat(news, is(not(nullValue())));
        assertThat(news.size(), is(1));
    }

    @Test
    @InSequence(400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void findByFolder() {
        final Category folder = categoryRepo.findById(-2100L);

        assertThat(folder.getObjects().size(), is(4));

        final List<ContentItem> items = itemRepo.findByFolder(folder);

        assertThat(items, is(not(nullValue())));
        assertThat(items.size(), is(4));
    }

    @Test
    @InSequence(410)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countItemsInFolder() {
        final Category folder = categoryRepo.findById(-2100L);

        assertThat(itemRepo.countItemsInFolder(folder), is(4L));
    }

    @Test
    @InSequence(500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countByNameInFolder() {
        final Category folder = categoryRepo.findById(-2100L);

        assertThat(itemRepo.countByNameInFolder(folder, "article1"), is(1L));
        assertThat(itemRepo.countByNameInFolder(folder, "article2"), is(1L));
        assertThat(itemRepo.countByNameInFolder(folder, "article3"), is(1L));
        assertThat(itemRepo.countByNameInFolder(folder, "article4"), is(0L));
        assertThat(itemRepo.countByNameInFolder(folder, "article"), is(0L));
        assertThat(itemRepo.countByNameInFolder(folder, "news1"), is(1L));
    }

    @Test
    @InSequence(500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void filterByFolderAndName() {
        final Category folder = categoryRepo.findById(-2100L);

        final List<ContentItem> articles = itemRepo.filterByFolderAndName(
            folder, "article");
        final List<ContentItem> news = itemRepo.filterByFolderAndName(folder,
                                                                      "news");

        assertThat(articles.size(), is(3));
        assertThat(news.size(), is(1));

        assertThat(articles.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(articles.get(1).getDisplayName(), is(equalTo("article2")));
        assertThat(articles.get(2).getDisplayName(), is(equalTo("article3")));

        assertThat(news.get(0).getDisplayName(), is(equalTo("news1")));
    }

    @Test
    @InSequence(510)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countFilterByFolderAndName() {
        final Category folder = categoryRepo.findById(-2100L);

        assertThat(itemRepo.countFilterByFolderAndName(folder, "article"),
                   is(3L));
        assertThat(itemRepo.countFilterByFolderAndName(folder, "art"),
                   is(3L));
        assertThat(itemRepo.countFilterByFolderAndName(folder, "article1"),
                   is(1L));
        assertThat(itemRepo.countFilterByFolderAndName(folder, "article2"),
                   is(1L));
        assertThat(itemRepo.countFilterByFolderAndName(folder, "article3"),
                   is(1L));

        assertThat(itemRepo.countFilterByFolderAndName(folder, "news"),
                   is(1L));

        assertThat(itemRepo.countFilterByFolderAndName(folder, "article10"),
                   is(0L));
        assertThat(itemRepo.countFilterByFolderAndName(folder, "foo"),
                   is(0L));
    }

}
