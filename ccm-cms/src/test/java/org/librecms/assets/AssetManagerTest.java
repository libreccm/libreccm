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
package org.librecms.assets;

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
import org.librecms.attachments.AttachmentList;
import org.librecms.contentsection.FolderRepository;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Tests for the {@link AssetManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
public class AssetManagerTest {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetManager assetManager;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private Shiro shiro;

    public AssetManagerTest() {
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
     * Verify that dependencies have been injected.
     */
    @Test
    @InSequence(1)
    public void checkInjections() {
        assertThat(shiro, is(not(nullValue())));
        assertThat(assetRepo, is(not(nullValue())));
        assertThat(assetManager, is(not(nullValue())));
        assertThat(folderRepo, is(not(nullValue())));
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
     * Tries to generate various non shared {@link Asset}s of different types
     * using
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}.
     * and puts them into an {@link AttachmentList}.
     */
    @Test
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-create-nonshared.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void createNonSharedAssets() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code name}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(110)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createNonSharedAssetNameIsNull() {

    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code name}
     * is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(120)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createNonSharedAssetNameIsEmpty() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code attachmentList} is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(130)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createNonSharedAssetAttachmentListNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.attachments.AttachmentList, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code type}
     * is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(140)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createNonSharedAssetTypeIsNull() {
        fail();
    }

    /**
     * Tries to generate various shared {@link Asset}s of different types using
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}.
     */
    @Test
    @InSequence(200)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-create-shared.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void createSharedAssets() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code name}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(210)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createSharedAssetNameIsNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code name}
     * is empty.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(220)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createSharedAssetNameIsEmpty() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code folder}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(230)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createSharedAssetFolderIsNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#createAsset(java.lang.String, org.librecms.contentsection.Folder, java.lang.Class)}
     * throws an {@link IllegalArgumentException} if the provided {@code type}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(240)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void createSharedAssetTypeIsNull() {
        fail();
    }

    /**
     * Verifies that {@link AssetManager} finds all orphaned {@link Asset}s and
     * deletes them.
     */
    @Test
    @InSequence(300)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-clean-orphaned.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void cleanOrphanedAssets() {
        fail();
    }

    /**
     * Tries to move an asset to an other asset folder in the same content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @InSequence(400)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-move-to-other-folder.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void moveAssetToOtherFolder() {
        fail();
    }

    /**
     * Tries to move an asset to an other asset folder in another content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @InSequence(410)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-move-to-other-contentsection.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void moveAssetToFolderInOtherContentSection() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(420)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetTargetFolderIsNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is not an asset folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetTargetFolderNotAssetFolder() {
        fail();
    }

    /**
     * Tries to copy an an {@link Asset} to another folder in the same using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(500)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-copy-to-other-folder.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void copyAssetToOtherFolder() {
        fail();
    }

    /**
     * Tries to copy an an {@link Asset} to same folder using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(510)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-copy-to-same-folder.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void copyAssetToSameFolder() {
        fail();
    }

    /**
     * Tries to copy an an {@link Asset} to another folder in another content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(520)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/assets/AssetManagerTest/"
                    + "after-copy-to-other-contentsection.xml",
        excludeColumns = {"object_id",
                          "uuid"})
    public void copyAssetToOtherContentSection() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(530)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(540)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetTargetFolderIsNull() {
        fail();
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is not an asset folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(550)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetTargetFolderIsNotAssetFolder() {
        fail();
    }

    /**
     * Verifies the result of
     * {@link AssetManager#isAssetInUse(org.librecms.assets.Asset)} for various
     * {@link Asset}s.
     */
    @InSequence(600)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    public void verifyIsAssetInUse() {
        fail();
    }

    /**
     * Verifies the result of
     * {@link AssetManager#getAssetPath(org.librecms.assets.Asset)} for various
     * {@link Asset}s.
     */
    @InSequence(700)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    public void verifyGetAssetPathWithoutContentSection() {
        fail();
    }

    /**
     * Verifies the result of {@link AssetManager#getAssetPath(org.librecms.assets.Asset, boolean)
     * }
     * for various {@link Asset}s.
     */
    @InSequence(800)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    public void verifyGetAssetPathWithContentSection() {
        fail();
    }

    /**
     * Verifies that result of
     * {@link AssetManager#getAssetFolder(org.librecms.assets.Asset)} for
     * various {@link Asset}s.
     */
    @InSequence(900)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    public void verifyGetAssetFolder() {
        fail();
    }

    /**
     * Verifies that result of {@link AssetManager#getAssetFolders(org.librecms.assets.Asset)
     * }
     * for various {@link Asset}s.
     */
    @InSequence(1000)
    @UsingDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/assets/AssetManagerTest/data.xml")
    public void verifyGetAssetFolders() {
        fail();
    }

}
