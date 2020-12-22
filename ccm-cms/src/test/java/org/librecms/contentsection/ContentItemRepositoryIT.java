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

import static org.libreccm.testutils.DependenciesHelpers.getCcmCoreDependencies;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

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
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.News;

import org.apache.shiro.subject.Subject;
import org.libreccm.security.Shiro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
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
public class ContentItemRepositoryIT {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private Shiro shiro;

    public ContentItemRepositoryIT() {
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
                    "LibreCCM-org.librecms.contentsection.ContentItemRepositoryTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm", "org.librecms")
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
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

        final Optional<Article> article1 = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10100L, Article.class));
        final Optional<Article> article2 = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10200L, Article.class));
        final Optional<Article> article3 = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10300L, Article.class));
        final Optional<News> news1 = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10400L, News.class));

        final Optional<Article> newsAsArticle = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10400, Article.class));
        final Optional<News> articleAsNews = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10200L, News.class));

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

        final Subject systemUser = shiro.getSystemUser();

        final Optional<Article> article1 = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("aed4b402-1180-46c6-b42d-7245f4dca248",
                                Article.class);
            });
        final Optional<Article> article2 = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("acae860f-2ffa-450d-b486-054292f0dae6",
                                Article.class);
            });
        final Optional<Article> article3 = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("f4b38abb-234b-4354-bc92-e36c068a1ebd",
                                Article.class);
            });
        final Optional<News> news1 = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("d9ea527d-c6e3-4bdd-962d-c0a1a80c6c72",
                                News.class);
            });

        final Optional<Article> newsAsArticle = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("d9ea527d-c6e3-4bdd-962d-c0a1a80c6c72",
                                Article.class);
            });
        final Optional<News> articleAsNews = systemUser
            .execute(() -> {
                return itemRepo
                    .findByUuid("acae860f-2ffa-450d-b486-054292f0dae6",
                                News.class);
            });

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

        final List<Article> articles = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findByType(Article.class));
        assertThat(articles, is(not(nullValue())));
        assertThat(articles.size(), is(3));

        final List<News> news = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findByType(News.class));
        assertThat(news, is(not(nullValue())));
        assertThat(news.size(), is(1));
    }

    @Test
    @InSequence(400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void findByFolder() {
        final Category folder = categoryRepo.findById(2100L).get();

        assertThat(folder.getObjects().size(), is(4));

        final List<ContentItem> items = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findByFolder(folder));

        assertThat(items, is(not(nullValue())));
        assertThat(items.size(), is(4));
    }

    @Test
    @InSequence(410)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countItemsInFolder() {

        final Category folder = categoryRepo.findById(2100L).get();

        assertThat(
            shiro.getSystemUser().execute(() -> {
                return itemRepo.countItemsInFolder(folder);
            }),
            is(4L));
    }

    @Test
    @InSequence(500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countByNameInFolder() {

        final Category folder = categoryRepo.findById(2100L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "article1")),
            is(1L));
        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "article2")),
            is(1L));
        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "article3")),
            is(1L));
        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "article4")),
            is(0L));
        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "article")),
            is(0L));
        assertThat(
            systemUser.execute(() -> itemRepo.countByNameInFolder(folder,
                                                                  "news1")),
            is(1L));
    }

    @Test
    @InSequence(510)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void filterByFolderAndName() {
        final Category folder = categoryRepo.findById(2100L).get();

        final List<ContentItem> articles = shiro
            .getSystemUser()
            .execute(() -> itemRepo.filterByFolderAndName(folder, "article"));
        final List<ContentItem> news = shiro
            .getSystemUser()
            .execute(() -> itemRepo.filterByFolderAndName(folder, "news"));

        assertThat(articles.size(), is(3));
        assertThat(news.size(), is(1));

        assertThat(articles.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(articles.get(1).getDisplayName(), is(equalTo("article2")));
        assertThat(articles.get(2).getDisplayName(), is(equalTo("article3")));

        assertThat(news.get(0).getDisplayName(), is(equalTo("news1")));
    }

    @Test
    @InSequence(520)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    public void countFilterByFolderAndName() {
        final Category folder = categoryRepo.findById(2100L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "article")),
            is(3L));
        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "art")),
            is(3L));
        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "article1")),
            is(1L));
        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "article2")),
            is(1L));
        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "article3")),
            is(1L));

        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "news")),
            is(1L));

        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "article10")),
            is(0L));
        assertThat(
            systemUser.execute(() -> itemRepo.countFilterByFolderAndName(folder,
                                                                         "foo")),
            is(0L));
    }

    @Test
    @InSequence(600)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemRepositoryTest/data.xml")
    @ShouldMatchDataSet(value = "datasets/org/librecms/contentsection/"
                                    + "ContentItemRepositoryTest/after-save.xml",
                        excludeColumns = {"object_id",
                                          "uuid",
                                          "item_uuid",
                                          "timestamp"})
    public void saveChangedItem() {

        final Optional<ContentItem> item = shiro
            .getSystemUser()
            .execute(() -> itemRepo.findById(10100L));

        assertThat(item.isPresent(), is(true));

        item.get().getName().addValue(Locale.ENGLISH, "first-article");
        item.get().getTitle().addValue(Locale.ENGLISH, "First Article");

        itemRepo.save(item.get());
    }

}
