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

import com.arsdigita.kernel.KernelConfig;

import org.jboss.arquillian.container.test.api.Deployment;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
public class ContentSectionManagerTest {

    @Inject
    private ContentSectionRepository repository;

    @Inject
    private ContentSectionManager manager;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private CategoryRepository categoryRepo;

    public ContentSectionManagerTest() {
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
                    "LibreCCM-org.libreccm.cms.contentsection.ContentSectionManagerTest.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
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
            .addPackage(com.arsdigita.util.Lockable.class.getPackage())
            .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
            .addPackage(org.librecms.Cms.class.getPackage())
            .addPackage(org.librecms.assets.Asset.class.getPackage())
            .addPackage(org.librecms.attachments.AttachmentList.class
                .getPackage())
            .addPackage(ContentSection.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");

    }

    @Test
    @InSequence(10)
    public void isRepositoryInjected() {
        assertThat(repository, is(not(nullValue())));
    }

    @Test
    @InSequence(20)
    public void isManagerInjected() {
        assertThat(manager, is(not(nullValue())));
    }

    @Test
    @InSequence(30)
    public void isRoleRepositoryInjected() {
        assertThat(roleRepository, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-create.xml",
        excludeColumns = {"object_id",
                          "root_assets_folder_id",
                          "root_documents_folder_id",
                          "permission_id",
                          "role_id",
                          "grantee_id",
                          "unique_id",
                          "uuid",
                          "created",
                          "section_id",
                          "creation_date"})
    @InSequence(100)
    public void createSection() {
        manager.createContentSection("test");
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-rename.xml",
        excludeColumns = {"object_id"})
    @InSequence(200)
    public void renameSection() {
        final ContentSection section = repository.findByLabel("info");

        manager.renameContentSection(section, "content");

        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = new Locale(kernelConfig
            .getDefaultLanguage());

        section.getTitle().addValue(defaultLocale, "content");
        repository.save(section);

        section.getRootDocumentsFolder().setName("content_root");
        section.getRootDocumentsFolder().setDisplayName("content_root");
        section.getRootDocumentsFolder().getTitle().addValue(
            defaultLocale, "content_root");

        section.getRootAssetsFolder().setName("content_assets");
        section.getRootAssetsFolder().setDisplayName("content_assets");
        section.getRootAssetsFolder().getTitle().addValue(
            defaultLocale, "content_assets");

        categoryRepo.save(section.getRootDocumentsFolder());
        categoryRepo.save(section.getRootAssetsFolder());
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-add-role.xml",
        excludeColumns = {"object_id",
                          "role_id",
                          "permission_id",
                          "creation_date",
                          "grantee_id"})
    @InSequence(300)
    public void addRole() {
        final ContentSection section = repository.findByLabel("info");

        manager.addRoleToContentSection(section,
                                        "reviewer",
                                        PRIVILEGE_ITEMS_VIEW_PUBLISHED,
                                        PRIVILEGE_ITEMS_PREVIEW,
                                        PRIVILEGE_ITEMS_APPROVE);
    }

    @Test
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentSectionManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentSectionManagerTest/after-remove-role.xml",
        excludeColumns = {"object_id"})
    @InSequence(300)
    public void removeRole() {
        final ContentSection section = repository.findByLabel("info");
        final Role role = roleRepository.findByName("info_publisher");

        manager.removeRoleFromContentSection(section, role);
    }

}
