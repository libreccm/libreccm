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
import org.librecms.assets.File;

import java.util.Optional;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for the {@link ItemAttachmentManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
//@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
public class ItemAttachmentManagerTest {

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

    public ItemAttachmentManagerTest() {
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
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage()).
            addPackage(org.libreccm.categorization.Categorization.class
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
            .addPackage(org.librecms.assets.BinaryAsset.class.getPackage())
            .addPackage(org.librecms.contentsection.Asset.class.getPackage()).
            addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

//        final File file = new File();
//        file.setDisplayName("assets510-2a");
//        file.setFileName("asset-510-2a.pdf");
//        file.setMimeType(new MimeType("application/pdf"));
        final Asset file = assetRepo.findById(-720L);

        attachmentManager.attachAsset(file, item.get().getAttachments().get(1));
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final Asset shared = assetRepo.findById(-610L);

        attachmentManager.attachAsset(shared,
                                      item.get().getAttachments().get(1));
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final Asset shared = assetRepo.findById(-620L);

        attachmentManager.attachAsset(shared,
                                      item.get().getAttachments().get(1));
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final Asset asset = null;

        attachmentManager.attachAsset(asset,
                                      item.get().getAttachments().get(1));
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
        final AttachmentList list = null;
        final Asset shared = assetRepo.findById(-610L);

        attachmentManager.attachAsset(shared, list);
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
        final Asset asset = assetRepo.findById(-610L);
        final Optional<ContentItem> item = itemRepo.findById(-510L);

        assertThat(asset, is(not(nullValue())));
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.unattachAsset(asset, list);
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
        final Asset asset = assetRepo.findById(-720L);
        final Optional<ContentItem> item = itemRepo.findById(-510L);

        assertThat(asset, is(not(nullValue())));
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.unattachAsset(asset, list);
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
        final Asset asset = assetRepo.findById(-720L);
        final Optional<ContentItem> item = itemRepo.findById(-510L);

        assertThat(asset, is(not(nullValue())));
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(1);

        attachmentManager.unattachAsset(asset, list);
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
        final Asset asset = null;

        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.unattachAsset(asset, list);
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
        final Asset asset = assetRepo.findById(-720L);
        assertThat(asset, is(not(nullValue())));

        final AttachmentList list = null;

        attachmentManager.unattachAsset(asset, list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveUp(list.getAttachments().get(0).getAsset(),
                                 list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveUp(list.getAttachments().get(2).getAsset(),
                                 list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveUp(null, list);
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
        final Asset asset = assetRepo.findById(-720L);
        assertThat(asset, is(not(nullValue())));

        final AttachmentList list = null;

        attachmentManager.moveUp(asset, list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveDown(list.getAttachments().get(2).getAsset(),
                                   list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveDown(list.getAttachments().get(0).getAsset(),
                                   list);
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
        final Optional<ContentItem> item = itemRepo.findById(-510L);
        assertThat(item.isPresent(), is(true));

        final AttachmentList list = item.get().getAttachments().get(0);

        attachmentManager.moveDown(null, list);
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
        final Asset asset = assetRepo.findById(-720L);
        assertThat(asset, is(not(nullValue())));

        final AttachmentList list = null;

        attachmentManager.moveDown(asset, list);
    }

}
