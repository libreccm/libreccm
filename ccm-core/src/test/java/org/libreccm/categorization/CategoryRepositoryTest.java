/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.categorization;

import java.io.File;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.Permission;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class CategoryRepositoryTest {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private DomainRepository domainRepo;

    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;

    public CategoryRepositoryTest() {
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
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.categorization.CategoryRepositoryTest.war")
            .addPackage(CcmObject.class.getPackage())
            .addPackage(Permission.class.getPackage())
            .addPackage(CcmApplication.class.getPackage())
            .addPackage(Categorization.class.getPackage())
            .addPackage(LocalizedString.class.getPackage())
            .addPackage(Workflow.class.getPackage())
            .addPackage(EntityManagerProducer.class.getPackage())
            .addPackage(MimeTypeConverter.class.getPackage())
            .addPackage(EqualsVerifier.class.getPackage())
            .addPackage(IntegrationTest.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(1)
    public void repoIsInjected() {
        assertThat(categoryRepo, is(not(nullValue())));
    }

    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @InSequence(3)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @InSequence(1100)
    public void findByPathString() {
        final Category category1 = categoryRepo.findByPath("test:/foo/bar/");
        final Category category2 = categoryRepo.findByPath("test:/foo/bar");
        final Category category3 = categoryRepo.findByPath("test:/foo/");

        final Category notFound = categoryRepo
            .findByPath("test:/does/not/exist");

        assertThat(category1, is(not(nullValue())));
        assertThat(category1.getName(), is(equalTo("bar")));

        assertThat(category2, is(not(nullValue())));
        assertThat(category2.getName(), is(equalTo("bar")));

        assertThat(category3, is(not(nullValue())));
        assertThat(category3.getName(), is(equalTo("foo")));

        assertThat(notFound, is(nullValue()));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @InSequence(1150)
    public void findByPathStringNotExisting() {
        final Category doesNotExist = categoryRepo.findByPath(
            "test:/does/not/exist");

        assertThat(doesNotExist, is(nullValue()));
    }

    @Test(expected = InvalidCategoryPathException.class)
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @ShouldThrowException(InvalidCategoryPathException.class)
    @InSequence(1200)
    public void findByPathStringInvalidDomain() {
        categoryRepo.findByPath("invalid:/foo/bar/");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @InSequence(2100)
    public void findByPathDomainString() {
        final Domain domain = domainRepo.findByDomainKey("test");

        final Category category1 = categoryRepo.findByPath(domain, "/foo/bar/");
        final Category category2 = categoryRepo.findByPath(domain, "foo/bar/");
        final Category category3 = categoryRepo.findByPath(domain, "/foo/bar");
        final Category category4 = categoryRepo.findByPath(domain, "foo/bar");

        final Category notFound = categoryRepo.findByPath(domain,
                                                          "/does/not/exist");

        assertThat(category1, is(not(nullValue())));
        assertThat(category1.getName(), is(equalTo("bar")));

        assertThat(category2, is(not(nullValue())));
        assertThat(category2.getName(), is(equalTo("bar")));

        assertThat(category3, is(not(nullValue())));
        assertThat(category3.getName(), is(equalTo("bar")));

        assertThat(category4, is(not(nullValue())));
        assertThat(category4.getName(), is(equalTo("bar")));

        assertThat(notFound, is(nullValue()));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @InSequence(1150)
    public void findByPathDomainStringNotExisting() {
        final Domain domain = domainRepo.findByDomainKey("test");

        final Category doesNotExist = categoryRepo.findByPath(domain,
                                                              "/does/not/exist");

        assertThat(doesNotExist, is(nullValue()));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryRepositoryTest/"
                    + "after-save-new-category.yml",
        excludeColumns = {"object_id"})
    @InSequence(3100)
    public void saveNewCategory() {
        final Category category = new Category();
        category.setDisplayName("new-category");
        category.setName("new-category");
        category.setUniqueId("new0001");

        categoryRepo.save(category);
    }

}
