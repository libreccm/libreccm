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

import java.util.List;
import java.util.Optional;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.apache.shiro.subject.ExecutionException;
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

import javax.inject.Inject;

import java.util.Locale;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.jboss.arquillian.persistence.CleanupUsingScript;

import org.librecms.assets.FileAsset;

import java.util.concurrent.Callable;

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
@CleanupUsingScript({"cleanup.sql"})
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
            .addPackage(org.librecms.assets.BinaryAsset.class.getPackage())
            .addPackage(org.librecms.contentsection.AttachmentList.class
                .getPackage())
            .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
            .addPackage(org.librecms.contentsection.ContentSection.class
                .getPackage())
            .addPackage(org.librecms.contenttypes.Article.class.getPackage())
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(org.libreccm.imexport.Exportable.class)
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
     * Tries to share an {@link Asset} using
     * {@link AssetManager#shareAsset(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     *
     * @throws javax.activation.MimeTypeParseException
     */
    @Test
    @InSequence(100)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value
            = "datasets/org/librecms/contentsection/AssetManagerTest/after-share.xml",
        excludeColumns = {"asset_id",
                          "categorization_id",
                          "id",
                          "object_id",
                          "object_order",
                          "rev",
                          "timestamp",
                          "uuid"})
    public void shareAsset() throws MimeTypeParseException {

        shiro.getSystemUser().execute(() -> {
            final Folder folder = folderRepo.findById(-420L).get();
            assertThat(folder, is(not(nullValue())));

            final FileAsset file = new FileAsset();
            file.setDisplayName("datasheet.pdf");
            file.setFileName("datasheet.pdf");
            file.setMimeType(new MimeType("application/pdf"));
            file.getTitle().addValue(Locale.ENGLISH, "datasheet.pdf");
            assetRepo.save(file);

            assetManager.shareAsset(file, folder);
            assertThat(file.getDisplayName(), is(equalTo("datasheet.pdf")));

            return null;
        });

    }

    /**
     * Verifies that
     * {@link AssetManager#shareAsset(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(110)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void shareAssetNull() {

        shiro.getSystemUser().execute(() -> {
            final Folder folder = folderRepo.findById(-420L).get();

            assetManager.shareAsset(null, folder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#shareAsset(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code folder}
     * is null.
     *
     * @throws javax.activation.MimeTypeParseException
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(120)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void shareAssetFolderIsNull() throws Throwable {

        try {
            shiro.getSystemUser().execute(() -> {
                final FileAsset file = new FileAsset();
                file.setDisplayName("datasheet.pdf");
                file.setFileName("datasheet.pdf");
                file.setMimeType(new MimeType("application/pdf"));

                assetManager.shareAsset(file, null);
                return null;
            });
        } catch (ExecutionException ex) {
            //We need the original exception
            throw ex.getCause();
        }
    }

    /**
     * Verifies that
     * {@link AssetManager#shareAsset(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is already shared.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(130)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void shareAlreadySharedAsset() {

        shiro.getSystemUser().execute(() -> {
            final Folder folder = folderRepo.findById(-420L).get();

            final Asset asset = assetRepo.findById(-700L).get();

            assetManager.shareAsset(asset, folder);
        });
    }

    /**
     * Verifies that {@link AssetManager} finds all orphaned {@link Asset}s and
     * deletes them.
     */
    @Test
    @InSequence(300)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-clean-orphaned.xml",
        excludeColumns = {"timestamp", "object_order"})
    public void cleanOrphanedAssets() {
        shiro.getSystemUser().execute(() -> {
            assetManager.cleanOrphanedAssets();
        });
    }

    /**
     * Tries to move an asset to an other asset folder in the same content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(400)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-move-to-other-folder.xml",
        excludeColumns = {"categorization_id",
                          "object_id",
                          "object_order",
                          "uuid"})
    public void moveAssetToOtherFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-900L).get();

            shiro.getSystemUser().execute(() -> {
                final Folder folder = folderRepo.findById(-410L).get();

                assetManager.move(asset, folder);
            });
            return null;
        });
    }

    /**
     * Tries to move an asset to an other asset folder in another content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(410)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-move-to-other-contentsection.xml",
        excludeColumns = {"categorization_id",
                          "object_id",
                          "object_order",
                          "uuid"})
    public void moveAssetToFolderInOtherContentSection() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-900L).get();

            final Folder folder = folderRepo.findById(-1600L).get();

            assetManager.move(asset, folder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(420)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetNull() {
        final Asset asset = null;

        shiro.getSystemUser().execute(() -> {
            final Folder folder = folderRepo.findById(-410L).get();

            assetManager.move(asset, folder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetTargetFolderIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-900L).get();

            final Folder targetFolder = null;

            assetManager.move(asset, targetFolder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#move(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is not an asset folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(430)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void moveAssetTargetFolderIsNotAssetFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-900L).get();

            final Folder folder = folderRepo.findById(-200L).get();

            assetManager.move(asset, folder);
        });
    }

    /**
     * Tries to copy an an {@link Asset} to another folder in the same using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(500)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-copy-to-other-folder.xml",
        excludeColumns = {"object_id",
                          "uuid",
                          "asset_id",
                          "id",
                          "timestamp",
                          "rev",
                          "categorization_id",
                          "object_order"})
    public void copyAssetToOtherFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-1100L).get();

            final Folder targetFolder = folderRepo.findById(-400L).get();

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Tries to copy an an {@link Asset} to same folder using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(510)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-copy-to-same-folder.xml",
        excludeColumns = {"object_id",
                          "uuid",
                          "asset_id",
                          "id",
                          "timestamp",
                          "rev",
                          "categorization_id",
                          "object_order"})
    public void copyAssetToSameFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-1100L).get();

            final Folder targetFolder = folderRepo.findById(-420L).get();

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Tries to copy an an {@link Asset} to another folder in another content
     * section using
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(520)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetManagerTest/"
                    + "after-copy-to-other-contentsection.xml",
        excludeColumns = {"object_id",
                          "uuid",
                          "asset_id",
                          "id",
                          "timestamp",
                          "rev",
                          "categorization_id",
                          "object_order"})
    public void copyAssetToOtherContentSection() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-1100L).get();

            final Folder targetFolder = folderRepo.findById(-1600L).get();

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided {@code asset}
     * is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(530)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = null;

            final Folder targetFolder = folderRepo.findById(-420L).get();

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is {@code null}.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(540)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetTargetFolderIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-1100L).get();

            final Folder targetFolder = null;

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Verifies that
     * {@link AssetManager#copy(org.librecms.assets.Asset, org.librecms.contentsection.Folder)}
     * throws an {@link IllegalArgumentException} if the provided
     * {@code targetFolder} is not an asset folder.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(550)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void copyAssetTargetFolderIsNotAssetFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(-1100L).get();

            final Folder targetFolder = folderRepo.findById(-200L).get();

            assetManager.copy(asset, targetFolder);
        });
    }

    /**
     * Verifies the result of
     * {@link AssetManager#isAssetInUse(org.librecms.assets.Asset)} for various
     * {@link Asset}s.
     */
    @Test
    @InSequence(600)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    public void verifyIsAssetInUse() {

        shiro.getSystemUser().execute(() -> {
            final Asset header = assetRepo.findById(-700L).get();
            final Asset phb = assetRepo.findById(-800L).get();
            final Asset servicesHeader = assetRepo.findById(-900L).get();
            final Asset product1Datasheet = assetRepo.findById(-1000L).get();
            final Asset catalog = assetRepo.findById(-1100L).get();

            assertThat(assetManager.isAssetInUse(header), is(true));
            assertThat(assetManager.isAssetInUse(phb), is(false));
            assertThat(assetManager.isAssetInUse(servicesHeader), is(true));
            assertThat(assetManager.isAssetInUse(product1Datasheet), is(true));
            assertThat(assetManager.isAssetInUse(catalog), is(true));

            return null;
        });
    }

    /**
     * Verifies the result of
     * {@link AssetManager#getAssetPath(org.librecms.assets.Asset)} for various
     * {@link Asset}s.
     */
    @Test
    @InSequence(700)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    public void verifyGetAssetPathWithoutContentSection() {

        shiro.getSystemUser().execute(() -> {
            final Asset header = assetRepo.findById(-700L).get();
            final Asset phb = assetRepo.findById(-800L).get();
            final Asset servicesHeader = assetRepo.findById(-900L).get();
            final Asset product1Datasheet = assetRepo.findById(-1000L).get();
            final Asset catalog = assetRepo.findById(-1100L).get();

            assertThat(assetManager.getAssetPath(header),
                       is(equalTo("/media/images/header.png")));
            assertThat(assetManager.getAssetPath(phb),
                       is(equalTo("/media/images/the-phb.png")));
            assertThat(assetManager.getAssetPath(servicesHeader),
                       is(equalTo("/media/services-header.png")));
            assertThat(assetManager.getAssetPath(product1Datasheet),
                       is(equalTo("")));
            assertThat(assetManager.getAssetPath(catalog),
                       is(equalTo("/media/downloads/catalog.pdf")));
        });
    }

    /**
     * Verifies the result of {@link AssetManager#getAssetPath(org.librecms.assets.Asset, boolean)
     * }
     * for various {@link Asset}s.
     */
    @Test
    @InSequence(800)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    public void verifyGetAssetPathWithContentSection() {

        shiro.getSystemUser().execute(() -> {
            final Asset header = assetRepo.findById(-700L).get();
            final Asset phb = assetRepo.findById(-800L).get();
            final Asset servicesHeader = assetRepo.findById(-900L).get();
            final Asset product1Datasheet = assetRepo.findById(-1000L).get();
            final Asset catalog = assetRepo.findById(-1100L).get();

            assertThat(assetManager.getAssetPath(header, true),
                       is(equalTo("info:/media/images/header.png")));
            assertThat(assetManager.getAssetPath(phb, true),
                       is(equalTo("info:/media/images/the-phb.png")));
            assertThat(assetManager.getAssetPath(servicesHeader, true),
                       is(equalTo("info:/media/services-header.png")));
            assertThat(assetManager.getAssetPath(product1Datasheet, true),
                       is(equalTo("")));
            assertThat(assetManager.getAssetPath(catalog, true),
                       is(equalTo("info:/media/downloads/catalog.pdf")));
        });
    }

    /**
     * Verifies that result of
     * {@link AssetManager#getAssetFolder(org.librecms.assets.Asset)} for
     * various {@link Asset}s.
     */
    @Test
    @InSequence(900)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    public void verifyGetAssetFolder() {

        shiro.getSystemUser().execute(() -> {
            final Asset header = assetRepo.findById(-700L).get();
            final Asset phb = assetRepo.findById(-800L).get();
            final Asset servicesHeader = assetRepo.findById(-900L).get();
            final Asset product1Datasheet = assetRepo.findById(-1000L).get();
            final Asset catalog = assetRepo.findById(-1100L).get();

            final Folder media = folderRepo.findById(-400L).get();
            final Folder images = folderRepo.findById(-410L).get();
            final Folder downloads = folderRepo.findById(-420L).get();

            final Optional<Folder> headerFolder = assetManager
                .getAssetFolder(header);
            final Optional<Folder> phbFolder = assetManager
                .getAssetFolder(phb);
            final Optional<Folder> servicesHeaderFolder = assetManager
                .getAssetFolder(servicesHeader);
            final Optional<Folder> product1DatasheetFolder = assetManager
                .getAssetFolder(product1Datasheet);
            final Optional<Folder> catalogFolder = assetManager
                .getAssetFolder(catalog);

            assertThat(headerFolder.isPresent(), is(true));
            assertThat(phbFolder.isPresent(), is(true));
            assertThat(servicesHeaderFolder.isPresent(), is(true));
            assertThat(product1DatasheetFolder.isPresent(), is(false));
            assertThat(catalogFolder.isPresent(), is(true));

            assertThat(headerFolder.get(), is(equalTo(images)));
            assertThat(phbFolder.get(), is(equalTo(images)));
            assertThat(servicesHeaderFolder.get(), is(equalTo(media)));
            assertThat(catalogFolder.get(), is(equalTo(downloads)));
        });
    }

    /**
     * Verifies that result of {@link AssetManager#getAssetFolders(org.librecms.assets.Asset)
     * }
     * for various {@link Asset}s.
     */
    @Test
    @InSequence(1000)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetManagerTest/data.xml")
    public void verifyGetAssetFolders() {

        shiro.getSystemUser().execute(() -> {
            final Asset header = assetRepo.findById(-700L).get();
            final Asset phb = assetRepo.findById(-800L).get();
            final Asset servicesHeader = assetRepo.findById(-900L).get();
            final Asset product1Datasheet = assetRepo.findById(-1000L).get();
            final Asset catalog = assetRepo.findById(-1100L).get();

            final Folder infoAssets = folderRepo.findById(-300L).get();
            final Folder media = folderRepo.findById(-400L).get();
            final Folder images = folderRepo.findById(-410L).get();
            final Folder downloads = folderRepo.findById(-420L).get();

            final List<Folder> headerFolders = assetManager.getAssetFolders(
                header);
            final List<Folder> phbFolders = assetManager.getAssetFolders(phb);
            final List<Folder> servicesHeaderFolders = assetManager
                .getAssetFolders(
                    servicesHeader);
            final List<Folder> product1DatasheetFolders = assetManager.
                getAssetFolders(product1Datasheet);
            final List<Folder> catalogFolders = assetManager.
                getAssetFolders(catalog);

            assertThat(headerFolders, is(not(nullValue())));
            assertThat(phbFolders, is(not(nullValue())));
            assertThat(servicesHeaderFolders, is(not(nullValue())));
            assertThat(product1DatasheetFolders, is(not(nullValue())));
            assertThat(catalogFolders, is(not(nullValue())));

            assertThat(headerFolders.isEmpty(), is(false));
            assertThat(phbFolders.isEmpty(), is(false));
            assertThat(servicesHeaderFolders.isEmpty(), is(false));
            assertThat(product1DatasheetFolders.isEmpty(), is(true));
            assertThat(catalogFolders.isEmpty(), is(false));

            assertThat(headerFolders.size(), is(3));
            assertThat(phbFolders.size(), is(3));
            assertThat(servicesHeaderFolders.size(), is(2));
            assertThat(product1DatasheetFolders.size(), is(0));
            assertThat(catalogFolders.size(), is(3));

            assertThat(headerFolders.get(0), is(equalTo(infoAssets)));
            assertThat(headerFolders.get(1), is(equalTo(media)));
            assertThat(headerFolders.get(2), is(equalTo(images)));

            assertThat(phbFolders.get(0), is(equalTo(infoAssets)));
            assertThat(phbFolders.get(1), is(equalTo(media)));
            assertThat(phbFolders.get(2), is(equalTo(images)));

            assertThat(servicesHeaderFolders.get(0), is(equalTo(infoAssets)));
            assertThat(servicesHeaderFolders.get(1), is(equalTo(media)));

            assertThat(catalogFolders.get(0), is(equalTo(infoAssets)));
            assertThat(catalogFolders.get(1), is(equalTo(media)));
            assertThat(catalogFolders.get(2), is(equalTo(downloads)));
        });

    }

}
