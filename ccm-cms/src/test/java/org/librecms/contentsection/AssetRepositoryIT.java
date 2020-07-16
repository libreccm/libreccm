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

import org.apache.shiro.subject.ExecutionException;
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
import org.librecms.assets.BinaryAsset;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.arquillian.persistence.CleanupUsingScript;

import org.librecms.assets.FileAsset;
import org.librecms.assets.Image;
import org.librecms.assets.VideoAsset;
import org.librecms.contentsection.rs.Assets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for the {@link AssetRepository}.
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
    {
        "999_cleanup.sql"
    }
)
public class AssetRepositoryIT {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private Shiro shiro;

    public AssetRepositoryIT() {
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
                    "LibreCCM-org.librecms.contentsection.AssetRepositoryTest.war")
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
     * Verify that dependencies have been injected.
     */
    @Test
    @InSequence(1)
    public void checkInjections() {
        assertThat(shiro, is(not(nullValue())));
        assertThat(assetRepo, is(not(nullValue())));
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
     * Tries to delete an unused {@link Asset} using
     * {@link AssetRepository#delete(org.librecms.assets.Asset)} and verifies
     * that the {@link Asset} has been removed from the database.
     */
    @Test
    @InSequence(100)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/AssetRepositoryTest/"
                    + "after-delete.xml",
        excludeColumns = {"timestamp", "object_order"}
    )
    public void deleteUnusedAsset() {

        shiro.getSystemUser().execute(() -> {
            final Asset asset = assetRepo.findById(800L).get();

            assetRepo.delete(asset);
        });
    }

    /**
     * Verifies that an {@link Asset} which is associated to at least one
     * {@link ContentItem} can't be deleted by using
     * {@link AssetRepository#delete(org.librecms.assets.Asset)}.
     *
     * @throws java.lang.Throwable
     */
    @Test(expected = AssetInUseException.class)
    @InSequence(110)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    @ShouldMatchDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/"
            + "data.xml")
    @ShouldThrowException(AssetInUseException.class)
    public void deleteUsedAsset() throws Throwable {

        try {
            shiro.getSystemUser().execute(() -> {
                final Asset asset = assetRepo.findById(700L).get();
                assetRepo.delete(asset);

                return null;
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }

    /**
     * Tries to find various {@link Assets} by their UUID using
     * {@link AssetRepository#findByUuid(java.lang.String)}.
     */
    @Test
    @InSequence(200)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")

    public void findAssetByUuid() {

        final Optional<Asset> header = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuid("4635589f-b87a-46d9-979e-6af14af063e5");
            });
        final Optional<Asset> phb = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuid("0a192e98-3b28-49d0-833f-bc9ff5f9d1d4");
            });
        final Optional<Asset> datasheet = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuid("0393840f-06a6-4ec3-aeb3-a612f845ad60");
            });
        final Optional<Asset> none = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuid("5211bf56-c20b-40b3-8ef8-0c7d35325fda");
            });

        assertThat(header.isPresent(), is(true));
        assertThat(header.get(), is(instanceOf(Asset.class)));
        assertThat(header.get(), is(instanceOf(BinaryAsset.class)));
        assertThat(header.get(), is(instanceOf(Image.class)));
        assertThat(header.get().getDisplayName(), is(equalTo("header.png")));

        assertThat(phb.isPresent(), is(true));
        assertThat(phb.get(), is(instanceOf(Asset.class)));
        assertThat(phb.get(), is(instanceOf(BinaryAsset.class)));
        assertThat(phb.get(), is(instanceOf(Image.class)));
        assertThat(phb.get().getDisplayName(), is(equalTo("the-phb.png")));

        assertThat(datasheet.isPresent(), is(true));
        assertThat(datasheet.get(), is(instanceOf(Asset.class)));
        assertThat(datasheet.get(), is(instanceOf(BinaryAsset.class)));
        assertThat(datasheet.get(), is(instanceOf(FileAsset.class)));
        assertThat(datasheet.get().getDisplayName(), is(equalTo(
                   "product1-datasheet.pdf")));

        assertThat(none.isPresent(), is(false));
    }

    /**
     * Tries to find various {@link Assets} by their UUID and type using
     * {@link AssetRepository#findByUuidAndType(java.lang.String, java.lang.Class)}.
     */
    @Test
    @InSequence(210)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void findAssetByUuidAndType() {

        final Optional<Asset> asset = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuidAndType("4635589f-b87a-46d9-979e-6af14af063e5",
                                       Image.class);
            });
        final Optional<Asset> none = shiro
            .getSystemUser()
            .execute(() -> {
                return assetRepo
                    .findByUuidAndType("4635589f-b87a-46d9-979e-6af14af063e5",
                                       FileAsset.class);
            });

        assertThat(asset.isPresent(), is(true));
        assertThat(asset.get().getDisplayName(), is(equalTo("header.png")));

        assertThat(none.isPresent(), is(false));
    }

    /**
     * Tries to find various {@link Assets} by their type using
     * {@link AssetRepository#findByType(java.lang.Class)}.
     */
    @Test
    @InSequence(300)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void findAssetByType() {

        final List<Asset> images = shiro
            .getSystemUser()
            .execute(() -> assetRepo.findByType(Image.class));
        final List<Asset> files = shiro
            .getSystemUser()
            .execute(() -> assetRepo.findByType(FileAsset.class));

        assertThat(images.isEmpty(), is(false));
        assertThat(files.isEmpty(), is(false));

        assertThat(images.size(), is(3));
        assertThat(files.size(), is(2));

        assertThat(images.get(0).getDisplayName(), is(equalTo("header.png")));
        assertThat(images.get(1).getDisplayName(),
                   is(equalTo("services-header.png")));
        assertThat(images.get(2).getDisplayName(), is(equalTo("the-phb.png")));

        assertThat(files.get(0).getDisplayName(), is(equalTo("catalog.pdf")));
        assertThat(files.get(1).getDisplayName(),
                   is(equalTo("product1-datasheet.pdf")));
    }

    /**
     * Tries all {@link Assets} in a specific {@link Folder} using
     * {@link AssetRepository#findByFolder(org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(400)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void findAssetsByFolder() {

        final Folder media = folderRepo.findById(400L).get();
        final Folder data = folderRepo.findById(500L).get();

        final List<Asset> mediaAssets = shiro
            .getSystemUser()
            .execute(() -> assetRepo.findByFolder(media));
        final List<Asset> dataAssets = shiro
            .getSystemUser()
            .execute(() -> assetRepo.findByFolder(data));

        assertThat(mediaAssets.size(), is(5));
        assertThat(dataAssets.size(), is(0));
    }

    /**
     * Tries to find out the number of {@link Asset}s in a specific
     * {@link Folder} using
     * {@link AssetRepository#countAssetsInFolder(org.librecms.contentsection.Folder)}.
     */
    @Test
    @InSequence(410)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void countAssetsInFolder() {

        final Folder media = folderRepo.findById(400L).get();
        final Folder data = folderRepo.findById(500L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> assetRepo.countAssetsInFolder(media)),
            is(5L));
        assertThat(
            systemUser.execute(() -> assetRepo.countAssetsInFolder(data)),
            is(0L));
    }

    /**
     * Tries to find {@link Asset}s in a {@link Folder} by using
     * {@link AssetRepository#filterByFolderAndTitle(org.librecms.contentsection.Folder, java.lang.String)}.
     */
    @Test
    @InSequence(500)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void filterAssetByFolderAndTitle() {

        final Folder media = shiro
            .getSystemUser()
            .execute(() -> folderRepo.findById(400L).get());

        final List<Asset> result1 = shiro
            .getSystemUser()
            .execute(() -> assetRepo.filterByFolderAndTitle(media, "hea"));
        final List<Asset> result2 = shiro
            .getSystemUser()
            .execute(() -> assetRepo.filterByFolderAndTitle(media, "photo"));

        assertThat(result1.size(), is(2));
        assertThat(result2.size(), is(0));

        assertThat(result1.get(0).getDisplayName(), is(equalTo("header.png")));
        assertThat(
            result1.get(1).getDisplayName(), is(equalTo("services-header.png"))
        );
    }

    /**
     * Tries to count the {@link Asset}s in a {@link Folder} matching a name
     * pattern by using
     * {@link AssetRepository#countFilterByFolderAndTitle(org.librecms.contentsection.Folder, java.lang.String)}.
     */
    @Test
    @InSequence(510)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void countFilterAssetByFolderAndTitle() {

        final Folder media = folderRepo.findById(400L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndTitle(media, "hea");
            }),
            is(2L));
        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndTitle(media, "photo");
            }),
            is(0L));
    }

    /**
     * Tries to filter the {@link Asset}s in a {@link Folder} by their type
     * using
     * {@link AssetRepository#filterByFolderAndType(org.librecms.contentsection.Folder, java.lang.Class)}.
     */
    @Test
    @InSequence(600)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void filterAssetsByFolderAndType() {

        final Folder media = folderRepo.findById(400L).get();

        final Subject systemUser = shiro.getSystemUser();

        final List<Asset> images = systemUser
            .execute(() -> assetRepo.filterByFolderAndType(media,
                                                           Image.class));
        final List<Asset> files = systemUser
            .execute(() -> assetRepo.filterByFolderAndType(media,
                                                           FileAsset.class));
        final List<Asset> videos = systemUser
            .execute(() -> assetRepo.filterByFolderAndType(media,
                                                           VideoAsset.class));

        assertThat(images.size(), is(3));
        assertThat(files.size(), is(2));
        assertThat(videos.size(), is(0));

        assertThat(images.get(0).getDisplayName(), is(equalTo("header.png")));
        assertThat(images.get(1).getDisplayName(),
                   is(equalTo("services-header.png")));
        assertThat(images.get(2).getDisplayName(), is(equalTo("the-phb.png")));

        assertThat(files.get(0).getDisplayName(), is(equalTo("catalog.pdf")));
        assertThat(files.get(1).getDisplayName(),
                   is(equalTo("product1-datasheet.pdf")));

    }

    /**
     * Tries to count the {@link Asset}s of a specific type in a {@link Folder}
     * using
     * {@link AssetRepository#countFilterByFolderAndType(org.librecms.contentsection.Folder, java.lang.Class)}.
     */
    @Test
    @InSequence(610)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void countFilterAssetsByFolderAndType() {
        final Folder media = folderRepo.findById(400L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndType(media, Image.class);
            }),
            is(3L));
        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndType(media,
                                                            FileAsset.class);
            }),
            is(2L));
        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndType(media,
                                                            VideoAsset.class);
            }),
            is(0L));
    }

    /**
     * Tries to filter the {@link Asset}s in a {@link Folder} by their type and
     * name using
     * {@link AssetRepository#filterByFolderAndTypeAndTitle(org.librecms.contentsection.Folder, java.lang.Class, java.lang.String)}.
     */
    @Test
    @InSequence(600)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void filterAssetsByFolderAndTypeAndTitle() {
        final Folder media = folderRepo.findById(400L).get();

        final List<Asset> result1 = shiro
            .getSystemUser()
            .execute(() -> assetRepo.filterByFolderAndTypeAndTitle(media,
                                                                   Image.class,
                                                                   "hea"));
        final List<Asset> result2 = shiro
            .getSystemUser()
            .execute(() -> assetRepo.filterByFolderAndTypeAndTitle(media,
                                                                   FileAsset.class,
                                                                   "hea"));

        assertThat(result1.size(), is(2));
        assertThat(result2.size(), is(0));

    }

    /**
     * Tries to count the {@link Asset}s in a {@link Folder} which are of a
     * specific type and which name matches a specific pattern using
     * {@link AssetRepository#filterByFolderAndTypeAndTitle(org.librecms.contentsection.Folder, java.lang.Class, java.lang.String)}.
     */
    @Test
    @InSequence(610)
    @UsingDataSet(
        "datasets/org/librecms/contentsection/AssetRepositoryTest/data.xml")
    public void countFilterAssetsByFolderAndTypeAndTitle() {
        final Folder media = folderRepo.findById(400L).get();

        final Subject systemUser = shiro.getSystemUser();

        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndTypeAndTitle(
                    media, Image.class, "hea");
            }),
            is(2L));
        assertThat(
            systemUser.execute(() -> {
                return assetRepo.countFilterByFolderAndTypeAndTitle(media,
                                                                    FileAsset.class,
                                                                    "hea");
            }),
            is(0L));
    }

}
