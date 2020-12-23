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

import org.apache.shiro.subject.Subject;
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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for the {@link AttachmentListManager}.
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
public class AttachmentListManagerIT {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private Shiro shiro;

    public AttachmentListManagerIT() {
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
                    "LibreCCM-org.librecms.assets.AssetManagerTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm", "org.librecms")
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/librecms/contentsection/ContentItemManagerTest/log4j2.xml",
                "log4j2.xml")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Verify that all dependencies have injected.
     */
    @Test
    @InSequence(1)
    public void checkInjections() {
        assertThat(itemRepo, (is(not(nullValue()))));
        assertThat(listManager, is(not(nullValue())));
        assertThat(shiro, is(not(nullValue())));
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
     * Tries to retrieve the names of the {@link AttachmentList}s of some
     * {@link ContentItem}s using
     * {@link AttachmentListManager#getAttachmentListNames(org.librecms.contentsection.ContentItem)}
     * and verifies that the names match the expected values.
     */
    @Test
    @InSequence(100)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void getAttachmentListNames() {
        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> article1 = itemRepo.findById(510);
            final Optional<ContentItem> article2 = itemRepo.findById(520);

            assertThat(article1.isPresent(), is(true));
            assertThat(article2.isPresent(), is(true));

            final List<String> names1 = listManager.getAttachmentListNames(
                article1.get());
            final List<String> names2 = listManager.getAttachmentListNames(
                article2.get());

            assertThat(names1, is(not(nullValue())));
            assertThat(names1.size(), is(3));
            assertThat(names1.get(0), is("list1"));
            assertThat(names1.get(1), is("list1"));
            assertThat(names1.get(2), is("list2"));

            assertThat(names2, is(not(nullValue())));
            assertThat(names2.size(), is(2));
            assertThat(names2.get(0), is("list1"));
            assertThat(names2.get(1), is("list2"));
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#getAttachmentListNames(org.librecms.contentsection.ContentItem)}
     * throws an {@link IllegalArgumentException} if called for {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(110)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void getAttachmentListNamesFromNull() {
        final ContentItem item = null;

        shiro.getSystemUser().execute(() -> {
            listManager.getAttachmentListNames(item);
        });
    }

    /**
     * Tries to retrieve various {@link AttachmentList}s by there name from some
     * {@link ContentItem}s using
     * {@link AttachmentListManager#getAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * and verifies that the list have the expected size.
     */
    @Test
    @InSequence(200)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void getAttachmentList() {
        final Subject systemUser = shiro.getSystemUser();

        final Optional<ContentItem> article1 = systemUser
            .execute(() -> itemRepo.findById(510));
        final Optional<ContentItem> article2 = systemUser
            .execute(() -> itemRepo.findById(520));

        assertThat(article1.isPresent(), is(true));
        assertThat(article2.isPresent(), is(true));

        final List<AttachmentList> article1List1 = systemUser.execute(
            () -> listManager.getAttachmentList(article1.get(), "list1"));
        assertThat(article1List1, is(not(nullValue())));
        assertThat(article1List1.size(), is(2));

        final List<AttachmentList> article1List2 = systemUser.execute(
            () -> listManager.getAttachmentList(article1.get(), "list2"));
        assertThat(article1List2, is(not(nullValue())));
        assertThat(article1List2.size(), is(1));

        final List<AttachmentList> article1List3 = systemUser.execute(
            () -> listManager.getAttachmentList(article1.get(), "list3"));
        assertThat(article1List3, is(not(nullValue())));
        assertThat(article1List3.isEmpty(), is(true));

        final List<AttachmentList> article2List1 = systemUser.execute(
            () -> listManager.getAttachmentList(article2.get(), "list1"));
        assertThat(article2List1, is(not(nullValue())));
        assertThat(article2List1.size(), is(1));

        final List<AttachmentList> article2List2 = systemUser.execute(
            () -> listManager.getAttachmentList(article2.get(), "list2"));
        assertThat(article2List2, is(not(nullValue())));
        assertThat(article2List2.size(), is(1));
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#getAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throws an {@link IllegalArgumentException} if called for item
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(210)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void getAttachmentListFromItemNull() {
        final Subject systemUser = shiro.getSystemUser();

        final ContentItem item = null;

        listManager.getAttachmentList(item, "list1");
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#getAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for then {@code name} of the attachment list to retrieve.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(220)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void getAttachmentListNameIsNull() {
        final Subject systemUser = shiro.getSystemUser();

        final Optional<ContentItem> item = systemUser
            .execute(() -> itemRepo.findById(510));

        assertThat(item.isPresent(), is(true));

        listManager.getAttachmentList(item.get(), null);
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#getAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throws an {@link IllegalArgumentException} if called with and empty
     * string for then {@code name} of the attachment list to retrieve.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(230)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void getAttachmentListWithEmptyName() {
        final Subject systemUser = shiro.getSystemUser();

        final Optional<ContentItem> item = systemUser
            .execute(() -> itemRepo.findById(510));

        assertThat(item.isPresent(), is(true));

        listManager.getAttachmentList(item.get(), "  ");

    }

    /**
     * Tries to create a new {@link AttachmentList} for an {@link ContentItem}
     * using
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}.
     */
    @Test
    @InSequence(300)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value
        = "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
              + "after-create.xml",
        excludeColumns = {"timestamp",
                          "object_id",
                          "list_id",
                          "uuid"})
    public void createAttachmentList() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(520);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "newList");
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throw new an {@link IllegalArgumentException} if the {@code item} is
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(310)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createAttachmentListForItemNull() {

        shiro.getSystemUser().execute(() -> {
            final ContentItem item = null;

            listManager.createAttachmentList(item, "newList");
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throws an {@link IllegalArgumentException} if the name of the new list
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(320)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void createAttachmentListNameIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(520);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), null);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String)}
     * throws an {@link IllegalArgumentException} if the name of the new list an
     * empty string.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(330)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void createAttachmentListNameIsEmpty() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(520);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "  ");
        });
    }

    /**
     * Tries to create a new {@link AttachmentList} for an {@link ContentItem}
     * using
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }.
     */
    @Test
    @InSequence(400)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/after-create-with-position.xml",
        excludeColumns = {"timestamp",
                          "object_id",
                          "list_id",
                          "uuid"})
    public void createAttachmentListWithPosition() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "newList", 1);
        });
    }

    /**
     * Tries to create a new {@link AttachmentList} for an {@link ContentItem}
     * using
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }
     * with a negative position. The new attachment list should be the first one
     * in the list of attachment lists.
     */
    @Test
    @InSequence(410)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/"
                    + "after-create-with-negative-position.xml",
        excludeColumns = {"timestamp",
                          "object_id",
                          "list_id",
                          "uuid"})
    public void createAttachmentListWithNegativePosition() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "newList", -3);
        });
    }

    /**
     * Tries to create a new {@link AttachmentList} for an {@link ContentItem}
     * using
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }
     * with position larger then the number of attachment lists of the item. The
     * new attachment list should be the last one in the list of attachment
     * lists.
     */
    @Test
    @InSequence(420)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/"
                    + "after-create-after-last.xml",
        excludeColumns = {"timestamp",
                          "object_id",
                          "list_id",
                          "uuid"})
    public void createAttachmentListWithPositionAfterLast() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "newList", 10);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }.
     * throw new an {@link IllegalArgumentException} if the {@code item} is
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createAttachmentListWithPositionForItemNull() {

        shiro.getSystemUser().execute(() -> {
            final ContentItem item = null;

            listManager.createAttachmentList(item, "newList", 10);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }.
     * throws an {@link IllegalArgumentException} if the name of the new list
     * {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(440)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createAttachmentListWithPositionNameIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), null, 10);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#createAttachmentList(org.librecms.contentsection.ContentItem, java.lang.String, long) }.
     * throws an {@link IllegalArgumentException} if the name of the new list an
     * empty string.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(450)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createAttachmentListWithPositionNameIsEmpty() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));

            listManager.createAttachmentList(item.get(), "   ", 10);
        });
    }

    /**
     * Tries to remove an {@link AttchmentList} from the owning
     * {@link ContentItem} using
     * {@link AttachmentListManager#removeAttachmentList(org.librecms.attachments.AttachmentList)}.
     * Verifies that all non shared {@link Asset} in the list have been deleted.
     */
    @Test
    @InSequence(500)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/after-remove.xml",
        excludeColumns = {"timestamp"})
    public void removeAttachmentList() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));
            final AttachmentList list = item.get().getAttachments().get(0);

            listManager.removeAttachmentList(list);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#removeAttachmentList(org.librecms.attachments.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called for {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(510)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void removeAttachmentListNull() {
        final AttachmentList list = null;

        listManager.removeAttachmentList(list);

    }

    /**
     * Tries to move an {@link AttachmentList} up one position up (+1) using
     * {@link AttachmentListManager#moveUp(org.librecms.attachments.AttachmentList)}.
     */
    @Test
    @InSequence(600)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/after-move-up.xml",
        excludeColumns = {"timestamp"})
    public void moveUp() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));
            final AttachmentList list = item.get().getAttachments().get(0);

            listManager.moveUp(list);
        });
    }

    /**
     * Tries to move the item of the last position up and verifies that this
     * that cause any changes.
     */
    @Test
    @InSequence(610)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void moveUpLast() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));
            final AttachmentList list = item.get().getAttachments().get(2);

            listManager.moveUp(list);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#moveUp(org.librecms.attachments.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called for {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(620)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveUpListNull() {

        shiro.getSystemUser().execute(() -> {
            final AttachmentList list = null;

            listManager.moveUp(list);
        });
    }

    /**
     * Tries to move an {@link AttachmentList} up one position down using
     * {@link AttachmentListManager#moveUp(org.librecms.attachments.AttachmentList)}.
     */
    @Test
    @InSequence(700)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "AttachmentListManagerTest/after-move-down.xml",
        excludeColumns = {"timestamp"})
    public void moveDown() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));
            final AttachmentList list = item.get().getAttachments().get(2);

            listManager.moveDown(list);
        });
    }

    /**
     * Tries to move the item of the last position up and verifies that this
     * that cause any changes.
     */
    @Test
    @InSequence(710)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    public void moveDownFirst() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510);
            assertThat(item.isPresent(), is(true));
            final AttachmentList list = item.get().getAttachments().get(0);

            listManager.moveDown(list);
        });
    }

    /**
     * Verifies that
     * {@link AttachmentListManager#moveDown(org.librecms.attachments.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called for {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(720)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AttachmentListManagerTest/"
            + "data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveDownListNull() {

        shiro.getSystemUser().execute(() -> {
            final AttachmentList list = null;

            listManager.moveDown(list);
        });
    }

}
