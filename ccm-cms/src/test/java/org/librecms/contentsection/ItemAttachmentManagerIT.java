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

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
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

import java.util.Optional;

import javax.activation.MimeTypeParseException;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


/**
 * Tests for the {@link ItemAttachmentManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
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
public class ItemAttachmentManagerIT {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AttachmentListManager listManager;

    @Inject
    private ItemAttachmentManager attachmentManager;

    @Inject
    private Shiro shiro;

    @Inject
    private EntityManager entityManager;

    public ItemAttachmentManagerIT() {
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
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Verify that all dependencies have injected.
     */
    @Test
    @InSequence(1)
    public void checkInjections() {
        assertThat(itemRepo, (is(not(nullValue()))));
        assertThat(assetRepo, is(not(nullValue())));
        assertThat(listManager, is(not(nullValue())));
        assertThat(attachmentManager, is(not(nullValue())));
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
     * Tries to attach some {@link Asset}s to some {@link AttachmentList}s using
     * {@link ItemAttachmentManager#attachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}.
     *
     * @throws javax.activation.MimeTypeParseException
     */
    @Test
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/after-attach-nonshared.xml",
        excludeColumns = {"timestamp",
                          "uuid",
                          "attachment_id"})
    public void attachNonSharedAsset() throws MimeTypeParseException {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final Asset file = assetRepo.findById(720L).get();

            attachmentManager.attachAsset(file, item.get().getAttachments().get(
                                          1));
        });
    }

    /**
     * Tries to attach some {@link Asset}s to some {@link AttachmentList}s using
     * {@link ItemAttachmentManager#attachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}.
     *
     * @throws javax.activation.MimeTypeParseException
     */
    @Test
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/after-attach-shared.xml",
        excludeColumns = {"timestamp",
                          "uuid",
                          "attachment_id"})
    public void attachSharedAsset() throws MimeTypeParseException {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final Asset shared = assetRepo.findById(610L).get();

            attachmentManager.attachAsset(shared,
                                          item.get().getAttachments().get(1));
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#attachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * does nothing when the provided {@code asset} is already part of the
     * provided {@code list}.
     */
    @Test
    @InSequence(110)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    public void attachAssetAlreadyAttached() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final Asset shared = assetRepo.findById(620L).get();

            attachmentManager.attachAsset(shared,
                                          item.get().getAttachments().get(1));
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#attachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the asset to attach.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(120)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void attachAssetNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final Asset asset = null;

            attachmentManager.attachAsset(asset,
                                          item.get().getAttachments().get(1));
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#attachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the list..
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(130)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void attachAssetToListNull() {

        shiro.getSystemUser().execute(() -> {
            final AttachmentList list = null;
            final Asset shared = assetRepo.findById(610L).get();

            attachmentManager.attachAsset(shared, list);
        });
    }

    /**
     * Tries to unattach a shared {@link Asset} using
     * {@link ItemAttachmentManager#unattachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}.
     */
    @Test
    @InSequence(210)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/"
                    + "after-unattach-shared.xml",
        excludeColumns = {"timestamp"})
    public void unattachSharedAsset() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(610L).get();
            final Optional<ContentItem> item = itemRepo.findById(510L);

            assertThat(asset, is(not(nullValue())));
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.unattachAsset(asset, list);
        });
    }

    /**
     * Tries to unattach a non shared {@link Asset} using
     * {@link ItemAttachmentManager#unattachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * and verifies that assets has been unattached <strong>and</strong>
     * deleted.
     */
    @Test
    @InSequence(220)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/"
                    + "after-unattach-nonshared.xml",
        excludeColumns = {"timestamp"})
    public void unattachNonSharedAsset() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(720L).get();
            final Optional<ContentItem> item = itemRepo.findById(510L);

            assertThat(asset, is(not(nullValue())));
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.unattachAsset(asset, list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#unattachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * does nothing if the provided {@code asset} is not part of the provided
     * {@code attachmentList}.
     */
    @Test
    @InSequence(220)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    public void unattachAssetNotAttached() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(720L).get();
            final Optional<ContentItem> item = itemRepo.findById(510L);

            assertThat(asset, is(not(nullValue())));
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(1);

            attachmentManager.unattachAsset(asset, list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#unattachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the asset to unattach.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(230)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void unattachAssetNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = null;

            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.unattachAsset(asset, list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#unattachAsset(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the list.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(240)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void unattachAssetFromListNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(720L).get();
            assertThat(asset, is(not(nullValue())));

            final AttachmentList list = null;

            attachmentManager.unattachAsset(asset, list);
        });
    }

    /**
     * Tries to move an {@link Asset} up one position in an
     * {@link AttachmentList} using
     * {@link ItemAttachmentManager#moveUp(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}.
     */
    @Test
    @InSequence(300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/after-move-up.xml",
        excludeColumns = {"timestamp"})
    public void moveUp() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveUp(list.getAttachments().get(0).getAsset(),
                                     list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveUp(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * does nothing if called for the last item in a list.
     */
    @Test
    @InSequence(310)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    public void moveUpLast() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveUp(list.getAttachments().get(2).getAsset(),
                                     list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveUp(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the attachment to move.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(320)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveUpNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveUp(null, list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveUp(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the list.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(330)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveUpInListNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(720L).get();
            assertThat(asset, is(not(nullValue())));

            final AttachmentList list = null;

            attachmentManager.moveUp(asset, list);
        });
    }

    /**
     * Tries to move an {@link Asset} down one position in an
     * {@link AttachmentList} using
     * {@link ItemAttachmentManager#moveUp(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}.
     */
    @Test
    @InSequence(400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ItemAttachmentManagerTest/after-move-down.xml",
        excludeColumns = {"timestamp"})
    public void moveDown() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveDown(list.getAttachments().get(2).getAsset(),
                                       list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveDown(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * does nothing if called for the first item in a list.
     */
    @Test
    @InSequence(410)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    public void moveDownFirst() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveDown(list.getAttachments().get(0).getAsset(),
                                       list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveDown(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the attachment to move.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(420)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveDownNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(510L);
            assertThat(item.isPresent(), is(true));

            final AttachmentList list = item.get().getAttachments().get(0);

            attachmentManager.moveDown(null, list);
        });
    }

    /**
     * Verifies that
     * {@link ItemAttachmentManager#moveDown(org.librecms.contentsection.Asset, org.librecms.contentsection.AttachmentList)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the list.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ItemAttachmentManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ItemAttachmentManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveDownInListNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(720L).get();
            assertThat(asset, is(not(nullValue())));

            final AttachmentList list = null;

            attachmentManager.moveDown(asset, list);
        });
    }

}
