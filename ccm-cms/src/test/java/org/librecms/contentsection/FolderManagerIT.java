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

import javax.inject.Inject;

import org.jboss.arquillian.persistence.CleanupUsingScript;

import java.util.Optional;

import org.jboss.arquillian.persistence.TestExecutionPhase;

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
public class FolderManagerIT {

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private FolderManager folderManager;

    public FolderManagerIT() {
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
                    "LibreCCM-org.libreccm.cms.contentsection.ContentSectionManagerTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm", "org.librecms")
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(10)
    public void checkInjection() {
        assertThat(folderRepo, is(not(nullValue())));
        assertThat(folderManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/after-create-docs-folder.xml",
        excludeColumns = {"object_id",
                          "folder_id",
                          "uuid",
                          "unique_id",
                          "category_order",
                          "content_section_id"})
    @InSequence(1000)
    public void createDocumentsFolder() {
        final Optional<Folder> parent = folderRepo.findById(2005L);
        assertThat(parent.isPresent(), is(true));

        final Folder test = folderManager.createFolder("test", parent.get());

        assertThat(test, is(not(nullValue())));
        assertThat(test.getName(), is(equalTo("test")));
        assertThat(test.getSection().getObjectId(), is(1100L));
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/after-create-assets-folder.xml",
        excludeColumns = {"object_id",
                          "folder_id",
                          "uuid",
                          "unique_id",
                          "category_order",
                          "content_section_id"})
    @InSequence(1100)
    public void createAssetsFolder() {
        final Optional<Folder> parent = folderRepo.findById(2013L);
        assertThat(parent.isPresent(), is(true));

        final Folder test = folderManager.createFolder("test", parent.get());

        assertThat(test, is(not(nullValue())));
        assertThat(test.getName(), is(equalTo("test")));
        assertThat(test.getSection().getObjectId(), is(1100L));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1400)
    public void createFolderNoParent() {
        folderManager.createFolder("test", null);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1500)
    public void createFolderNullName() {
        final Optional<Folder> parent = folderRepo.findById(2005L);
        assertThat(parent.isPresent(), is(true));

        final Folder test = folderManager.createFolder(null, parent.get());

    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(1500)
    public void createFolderEmptyName() {
        final Optional<Folder> parent = folderRepo.findById(2005L);
        assertThat(parent.isPresent(), is(true));

        final Folder test = folderManager.createFolder(" ", parent.get());

    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/after-delete-folder.xml",
        excludeColumns = {"object_id"})
    @InSequence(2000)
    public void deleteFolder() {
        //docs-1-1-1
        final Folder folder = folderRepo.findById(2007L).get();
        folderManager.deleteFolder(folder);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2100)
    public void deleteNonEmptyFolder() {
        final Folder folder = folderRepo.findById(2008L).get();
        folderManager.deleteFolder(folder);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2100)
    public void deleteNonEmptySubFolder() {
        final Folder folder = folderRepo.findById(2006L).get();
        folderManager.deleteFolder(folder);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2210)
    public void deleteRootFolder() {
        final Folder folder = folderRepo.findById(2003L).get();
        folderManager.deleteFolder(folder);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(2220)
    public void deleteNullFolder() {
        folderManager.deleteFolder(null);
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/after-move-folder.xml",
        excludeColumns = {"category_order"})
    @InSequence(3000)
    public void moveFolder() {
        //docs-1-1-2 to docs-2
        final Folder folder = folderRepo.findById(2008L).get();
        final Folder target = folderRepo.findById(2010L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/after-move-folder-same-name.xml",
        excludeColumns = {"category_order"})
    @InSequence(3010)
    public void moveFolderTargetFolderSameName() {
        //docs-1/downloads to /docs-2/

        final Folder folder = folderRepo.findById(2009L).get();
        final Folder target = folderRepo.findById(2010L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3100)
    public void moveDocumentsFolderToAssetsFolder() {
        final Folder folder = folderRepo.findById(2009L).get();
        final Folder target = folderRepo.findById(2014L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3110)
    public void moveAssetsFolderToDocumentsFolder() {
        final Folder folder = folderRepo.findById(2014L).get();
        final Folder target = folderRepo.findById(2010L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3110)
    public void moveFolderToItself() {
        final Folder folder = folderRepo.findById(2008L).get();
        
        folderManager.moveFolder(folder, folder);
    }
    
    @Test(expected = NullPointerException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(NullPointerException.class)
    @InSequence(3200)
    public void moveFolderNull() {
        final Folder target = folderRepo.findById(2010L).get();
        folderManager.moveFolder(null, target);
    }

    @Test(expected = NullPointerException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(NullPointerException.class)
    @InSequence(3210)
    public void moveFolderTargetNull() {
        final Folder folder = folderRepo.findById(2008L).get();
        
        folderManager.moveFolder(folder, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3300)
    public void moveFolderWithLiveItems() {
        final Folder folder = folderRepo.findById(2011L).get();
        final Folder target = folderRepo.findById(2010L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3310)
    public void moveFolderWithLiveItemsInSubFolder() {
        final Folder folder = folderRepo.findById(2010L).get();
        final Folder target = folderRepo.findById(2005L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3320)
    public void moveFolderToOtherSection() {
        final Folder folder = folderRepo.findById(2008L).get();
        final Folder target = folderRepo.findById(2003L).get();

        folderManager.moveFolder(folder, target);
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @InSequence(3400)
    public void getFolderPath() {
        final Folder infoRoot = folderRepo.findById(2001L).get();
        final Folder infoAssets = folderRepo.findById(2002L).get();
        final Folder projectsRoot = folderRepo.findById(2003L).get();
        final Folder projectsAssets = folderRepo.findById(2004L).get();
        final Folder docs1 = folderRepo.findById(2005L).get();
        final Folder docs11 = folderRepo.findById(2006L).get();
        final Folder docs111 = folderRepo.findById(2007L).get();
        final Folder docs112 = folderRepo.findById(2008L).get();
        final Folder downloads1 = folderRepo.findById(2009L).get();
        final Folder docs2 = folderRepo.findById(2010L).get();
        final Folder docs21 = folderRepo.findById(2011L).get();
        final Folder downloads2 = folderRepo.findById(2012L).get();
        final Folder assets1 = folderRepo.findById(2013L).get();
        final Folder assets11 = folderRepo.findById(2014L).get();
        final Folder assets12 = folderRepo.findById(2015L).get();
        
        assertThat(folderManager.getFolderPath(infoRoot), 
                   is(equalTo("/info_root/")));
        assertThat(folderManager.getFolderPath(infoAssets), 
                   is(equalTo("/info_assets/")));
        assertThat(folderManager.getFolderPath(projectsRoot), 
                   is(equalTo("/projects_root/")));
        assertThat(folderManager.getFolderPath(projectsAssets), 
                   is(equalTo("/projects_assets/")));
        assertThat(folderManager.getFolderPath(docs1), 
                   is(equalTo("/info_root/docs-1/")));
        assertThat(folderManager.getFolderPath(docs11), 
                   is(equalTo("/info_root/docs-1/docs-1-1/")));
        assertThat(folderManager.getFolderPath(docs111), 
                   is(equalTo("/info_root/docs-1/docs-1-1/docs-1-1-1/")));
        assertThat(folderManager.getFolderPath(docs112), 
                   is(equalTo("/info_root/docs-1/docs-1-1/docs-1-1-2/")));
        assertThat(folderManager.getFolderPath(downloads1), 
                   is(equalTo("/info_root/docs-1/downloads/")));
        assertThat(folderManager.getFolderPath(docs2), 
                   is(equalTo("/info_root/docs-2/")));
        assertThat(folderManager.getFolderPath(docs21), 
                   is(equalTo("/info_root/docs-2/docs-2-1/")));
        assertThat(folderManager.getFolderPath(downloads2), 
                   is(equalTo("/info_root/docs-2/downloads/")));
        assertThat(folderManager.getFolderPath(assets1), 
                   is(equalTo("/info_assets/assets-1/")));
        assertThat(folderManager.getFolderPath(assets11), 
                   is(equalTo("/info_assets/assets-1/assets-1-1/")));
        assertThat(folderManager.getFolderPath(assets12), 
                   is(equalTo("/info_assets/assets-1/assets-1-2/")));
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @InSequence(3410)
    public void getFolderPathWithContentSection() {
        final Folder infoRoot = folderRepo.findById(2001L).get();
        final Folder infoAssets = folderRepo.findById(2002L).get();
        final Folder projectsRoot = folderRepo.findById(2003L).get();
        final Folder projectsAssets = folderRepo.findById(2004L).get();
        final Folder docs1 = folderRepo.findById(2005L).get();
        final Folder docs11 = folderRepo.findById(2006L).get();
        final Folder docs111 = folderRepo.findById(2007L).get();
        final Folder docs112 = folderRepo.findById(2008L).get();
        final Folder downloads1 = folderRepo.findById(2009L).get();
        final Folder docs2 = folderRepo.findById(2010L).get();
        final Folder docs21 = folderRepo.findById(2011L).get();
        final Folder downloads2 = folderRepo.findById(2012L).get();
        final Folder assets1 = folderRepo.findById(2013L).get();
        final Folder assets11 = folderRepo.findById(2014L).get();
        final Folder assets12 = folderRepo.findById(2015L).get();
        
        assertThat(folderManager.getFolderPath(infoRoot, true), 
                   is(equalTo("info:/info_root/")));
        assertThat(folderManager.getFolderPath(infoAssets, true), 
                   is(equalTo("info:/info_assets/")));
        assertThat(folderManager.getFolderPath(projectsRoot, true), 
                   is(equalTo("projects:/projects_root/")));
        assertThat(folderManager.getFolderPath(projectsAssets, true), 
                   is(equalTo("projects:/projects_assets/")));
        assertThat(folderManager.getFolderPath(docs1, true), 
                   is(equalTo("info:/info_root/docs-1/")));
        assertThat(folderManager.getFolderPath(docs11, true), 
                   is(equalTo("info:/info_root/docs-1/docs-1-1/")));
        assertThat(folderManager.getFolderPath(docs111, true), 
                   is(equalTo("info:/info_root/docs-1/docs-1-1/docs-1-1-1/")));
        assertThat(folderManager.getFolderPath(docs112, true), 
                   is(equalTo("info:/info_root/docs-1/docs-1-1/docs-1-1-2/")));
        assertThat(folderManager.getFolderPath(downloads1, true), 
                   is(equalTo("info:/info_root/docs-1/downloads/")));
        assertThat(folderManager.getFolderPath(docs2, true), 
                   is(equalTo("info:/info_root/docs-2/")));
        assertThat(folderManager.getFolderPath(docs21, true), 
                   is(equalTo("info:/info_root/docs-2/docs-2-1/")));
        assertThat(folderManager.getFolderPath(downloads2, true), 
                   is(equalTo("info:/info_root/docs-2/downloads/")));
        assertThat(folderManager.getFolderPath(assets1, true), 
                   is(equalTo("info:/info_assets/assets-1/")));
        assertThat(folderManager.getFolderPath(assets11, true), 
                   is(equalTo("info:/info_assets/assets-1/assets-1-1/")));
        assertThat(folderManager.getFolderPath(assets12, true), 
                   is(equalTo("info:/info_assets/assets-1/assets-1-2/")));
    }
    
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3410)
    public void getFolderPathNull() {
        folderManager.getFolderPath(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "FolderManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(3410)
    public void getFolderPathNullWithContentSection() {
        folderManager.getFolderPath(null, true);
    }

}
