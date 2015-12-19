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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.Permission;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import java.io.File;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
public class CategoryManagerTest {

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Inject
    private DomainRepository domainRepo;

    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;

    public CategoryManagerTest() {
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
                    "LibreCCM-org.libreccm.categorization.CategoryManagerTest.war")
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
    public void managerIsInjected() {
        assertThat(categoryManager, is(not(nullValue())));
    }

    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @InSequence(3)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-add-obj-to-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(1100)
    public void addObjectToCategory() {
        final CcmObject object2 = ccmObjectRepo.findById(-3200L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object2, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        categoryManager.addObjectToCategory(object2, foo);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/categorization/CategoryManagerTest/after-remove-obj-from-category.yml",
                        excludeColumns = {"categorization_id"})
    @InSequence(1200)
    public void removeObjectFromCategory()
        throws ObjectNotAssignedToCategoryException {

        final CcmObject object1 = ccmObjectRepo.findById(-3100L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object1, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        categoryManager.removeObjectFromCategory(object1, foo);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-add-subcategory.yml",
        excludeColumns = {"object_id"})
    @InSequence(2100)
    public void addSubCategoryToCategory() {
        final Category category = new Category();
        category.setName("category-new");
        category.setDisplayName("category-new");
        category.setUniqueId("catnew");
        categoryRepo.save(category);

        final TypedQuery<Category> query = entityManager.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name",
            Category.class);
        query.setParameter("name", "category-new");
        final Category sub = query.getSingleResult();

        final Category foo = categoryRepo.findById(-2100L);
//        final Category sub = categoryRepo.findById(-2200L);

        categoryManager.addSubCategoryToCategory(sub, foo);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-remove-subcategory.yml",
        excludeColumns = {"categorization_id", "object_id"})
    @InSequence(2200)
    public void removeSubCategoryFromCategory() {
        final Category foo = categoryRepo.findById(-2100L);
        final Category bar = categoryRepo.findById(-2200L);

        categoryManager.removeSubCategoryFromCategory(bar, foo);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-create-multiple-categories.yml")
    @InSequence(3100)
    public void createMultipleCategories() {
        final Domain domain = domainRepo.findByDomainKey("test");
        final Category root = domain.getRoot();

        final Category com = new Category();
        com.setName("com");
        com.setDisplayName("com");
        com.setUniqueId("com");
        categoryRepo.save(com);
        categoryManager.addSubCategoryToCategory(com, root);

        final Category example = new Category();
        example.setName("example");
        example.setDisplayName("example");
        example.setUniqueId("example");
        categoryRepo.save(example);
        categoryManager.addSubCategoryToCategory(example, com);

        final Category categories = new Category();
        categories.setName("categories");
        categories.setDisplayName("categories");
        categories.setUniqueId("categories");
        categoryRepo.save(categories);
        categoryManager.addSubCategoryToCategory(categories, example);

        final Category test = new Category();
        test.setName("test");
        test.setDisplayName("test");
        test.setUniqueId("test");
        categoryRepo.save(test);
        categoryManager.addSubCategoryToCategory(test, categories);
    }

}
