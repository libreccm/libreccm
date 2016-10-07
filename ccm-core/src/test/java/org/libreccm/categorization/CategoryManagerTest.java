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

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 * Tests for the {@link CategoryManager}.
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

    @Inject
    private Shiro shiro;

    @Inject
    private Subject subject;

    @Inject
    private TestCategoryController controller;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.categorization.CategoryManagerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.PermissionChecker.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class
                .getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Verifies that a {@link CategoryManager} instance is injected.
     */
    @Test
    @InSequence(1)
    public void managerIsInjected() {
        assertThat(categoryManager, is(not(nullValue())));
    }

    /**
     * Verifies that an {@link EntityManager} instance is injected.
     */
    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    /**
     * Verifies that basic dataset for this test is loadable.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @InSequence(3)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    /**
     * Verifies that Shiro is setup properly.
     */
    @Test
    @InSequence(20)
    public void checkShiro() {
        assertThat(shiro.getSecurityManager(), is(not(nullValue())));
        assertThat(shiro.getSystemUser(), is(not(nullValue())));
    }

    /**
     * Verifies that
     * {@link CategoryManager#addObjectToCategory(org.libreccm.core.CcmObject, org.libreccm.categorization.Category)}
     * adds an object to a category.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-add-obj-to-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(1100)
    public void addObjectToCategoryBySystemUser() {
        final CcmObject object2 = ccmObjectRepo.findById(-3200L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object2, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        shiro.getSystemUser().execute(() -> categoryManager.addObjectToCategory(
            object2, foo));
    }

    /**
     * Verifies that the authorisation and permission inheritance from the
     * domain to the categories works.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-add-obj-to-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(1200)
    public void addObjectToCategoryAuthByDomain() {
        final CcmObject object2 = ccmObjectRepo.findById(-3200L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object2, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "jane.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.addObjectToCategory(object2, foo);

        subject.logout();
    }

    /**
     * Verifies that the authorisation and permission inheritance works for
     * categories.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-add-obj-to-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(1300)
    public void addObjectToCategoryAuthByCategory() {
        final CcmObject object2 = ccmObjectRepo.findById(-3200L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object2, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "mmuster@example.com", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.addObjectToCategory(object2, foo);

        subject.logout();
    }

    /**
     * Verifies that the authorisation and permission inheritance works for
     * categories, this time with an unauthorised access.
     */
    @Test(expected = UnauthorizedException.class)
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(1400)
    public void addObjectToCategoryNotAuthorized() {
        final CcmObject object2 = ccmObjectRepo.findById(-3200L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object2, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        categoryManager.addObjectToCategory(object2, foo);
    }

    /**
     * Tries to remove an object from a category by using
     * {@link CategoryManager#removeObjectFromCategory(org.libreccm.core.CcmObject, org.libreccm.categorization.Category)}
     *
     * The operation is executed in the system users context.
     *
     * @throws ObjectNotAssignedToCategoryException
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-remove-obj-from-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(2000)
    public void removeObjectFromCategoryBySystemUser()
        throws ObjectNotAssignedToCategoryException {

        final CcmObject object1 = ccmObjectRepo.findById(-3100L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object1, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        shiro.getSystemUser().execute(() -> {
            categoryManager.removeObjectFromCategory(object1, foo);
            return null;
        });
    }

    /**
     * Tries to remove an object from a category by using
     * {@link CategoryManager#removeObjectFromCategory(org.libreccm.core.CcmObject, org.libreccm.categorization.Category)}
     *
     * This time we are using an author which has been authorised to do so for
     * the category by a permission set on the {@link Domain} to which the
     * category belongs.
     *
     * @throws ObjectNotAssignedToCategoryException
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-remove-obj-from-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(2100)
    public void removeObjectFromCategoryAuthByDomain()
        throws ObjectNotAssignedToCategoryException {

        final CcmObject object1 = ccmObjectRepo.findById(-3100L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object1, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "jane.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.removeObjectFromCategory(object1, foo);

        subject.logout();
    }

    /**
     * Tries to remove an object from a category by using
     * {@link CategoryManager#removeObjectFromCategory(org.libreccm.core.CcmObject, org.libreccm.categorization.Category)}
     *
     * This time we are using an author which has been authorised to do so for
     * the category by a permission set on the {@link Category} itself.
     *
     * @throws ObjectNotAssignedToCategoryException
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-remove-obj-from-category.yml",
        excludeColumns = {"categorization_id"})
    @InSequence(2200)
    public void removeObjectFromCategoryAuthByCategory()
        throws ObjectNotAssignedToCategoryException {

        final CcmObject object1 = ccmObjectRepo.findById(-3100L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object1, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "mmuster@example.com", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.removeObjectFromCategory(object1, foo);

        subject.logout();
    }

    /**
     * Tries to remove an object from a category and verifies that a
     * {@link UnauthorizedException} is thrown if the current user is not
     * authorised for the operation.
     *
     * @throws ObjectNotAssignedToCategoryException
     */
    @Test(expected = UnauthorizedException.class)
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(2300)
    public void removeObjectFromCategoryNotAuthorized()
        throws ObjectNotAssignedToCategoryException {

        final CcmObject object1 = ccmObjectRepo.findById(-3100L);
        final Category foo = categoryRepo.findById(-2100L);

        assertThat(object1, is(not(nullValue())));
        assertThat(foo, is(not(nullValue())));

        categoryManager.removeObjectFromCategory(object1, foo);
    }

    /**
     * Tries to add a sub category to another category.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-add-subcategory.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(3000)
    public void addSubCategoryToCategoryBySystemUser() {
        final Category category = new Category();
        category.setName("category-new");
        category.setDisplayName("category-new");
        category.setUniqueId("catnew");
        shiro.getSystemUser().execute(() -> categoryRepo.save(category));

        final TypedQuery<Category> query = entityManager.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name",
            Category.class);
        query.setParameter("name", "category-new");
        final Category sub = query.getSingleResult();

        final Category foo = categoryRepo.findById(-2100L);

        shiro.getSystemUser().execute(
            () -> categoryManager.addSubCategoryToCategory(sub, foo));
    }

    /**
     * Tries to add a subcategory to category with authorisation by a permission
     * set of the {@link Domain} to which the category belongs.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-add-subcategory.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(3000)
    public void addSubCategoryToCategoryAuthByDomain() {
        final Category category = new Category();
        category.setName("category-new");
        category.setDisplayName("category-new");
        category.setUniqueId("catnew");
        shiro.getSystemUser().execute(() -> categoryRepo.save(category));

        final TypedQuery<Category> query = entityManager.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name",
            Category.class);
        query.setParameter("name", "category-new");
        final Category sub = query.getSingleResult();

        final Category foo = categoryRepo.findById(-2100L);

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "jane.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.addSubCategoryToCategory(sub, foo);

        subject.logout();
    }

    /**
     * Tries to add a subcategory to category with authorisation by a permission
     * set of the category.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-add-subcategory.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(3000)
    public void addSubCategoryToCategoryAuthByCategory() {
        final Category category = new Category();
        category.setName("category-new");
        category.setDisplayName("category-new");
        category.setUniqueId("catnew");
        shiro.getSystemUser().execute(() -> categoryRepo.save(category));

        final TypedQuery<Category> query = entityManager.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name",
            Category.class);
        query.setParameter("name", "category-new");
        final Category sub = query.getSingleResult();

        final Category foo = categoryRepo.findById(-2100L);

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "mmuster@example.com", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.addSubCategoryToCategory(sub, foo);

        subject.logout();
    }

    /**
     * Tries to add a subcategory to a category and verifies that
     * {@link CategoryManager#addSubCategoryToCategory(org.libreccm.categorization.Category, org.libreccm.categorization.Category)}
     * throws an {@link UnauthorizedException} if the current user is not
     * authorised to do that.
     */
    @Test(expected = UnauthorizedException.class)
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(3000)
    public void addSubCategoryToCategoryNotAuthorized() {
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

        categoryManager.addSubCategoryToCategory(sub, foo);
    }

    /**
     * Tries to remove a subcategory from a category by using
     * {@link CategoryManager#removeSubCategoryFromCategory(org.libreccm.categorization.Category, org.libreccm.categorization.Category)}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-remove-subcategory.yml",
        excludeColumns = {"categorization_id", "object_id"})
    @InSequence(4000)
    public void removeSubCategoryFromCategoryBySystemUser() {
        final Category foo = categoryRepo.findById(-2100L);
        final Category bar = categoryRepo.findById(-2200L);

        shiro.getSystemUser().execute(
            () -> categoryManager.removeSubCategoryFromCategory(bar, foo));
    }

    /**
     * Tries to remove a subcategory from a category. The user is authorised to
     * do by a permission set on the {@link Domain} to which the category
     * belongs.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-remove-subcategory.yml",
        excludeColumns = {"categorization_id", "object_id"})
    @InSequence(4000)
    public void removeSubCategoryFromCategoryAuthByDomain() {
        final Category foo = categoryRepo.findById(-2100L);
        final Category bar = categoryRepo.findById(-2200L);

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "jane.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.removeSubCategoryFromCategory(bar, foo);

        subject.logout();
    }

    /**
     * Tries to remove a subcategory from a category. The user is authorised to
     * do by a permission set on the category.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/"
                    + "CategoryManagerTest/after-remove-subcategory.yml",
        excludeColumns = {"categorization_id", "object_id"})
    @InSequence(4000)
    public void removeSubCategoryFromCategoryAuthByCategory() {
        final Category foo = categoryRepo.findById(-2100L);
        final Category bar = categoryRepo.findById(-2200L);

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "mmuster@example.com", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        categoryManager.removeSubCategoryFromCategory(bar, foo);

        subject.logout();
    }

    /**
     * Verifies that
     * {@link CategoryManager#removeSubCategoryFromCategory(org.libreccm.categorization.Category, org.libreccm.categorization.Category)}
     * throws an {@link UnauthorizedException} if the current user does not have
     * sufficient permissions to execute the operation.
     */
    @Test(expected = UnauthorizedException.class)
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(4000)
    public void removeSubCategoryFromCategoryNotAuthorized() {
        final Category foo = categoryRepo.findById(-2100L);
        final Category bar = categoryRepo.findById(-2200L);

        categoryManager.removeSubCategoryFromCategory(bar, foo);
    }

    /**
     * Verifies that multiple categories (a complete category hierarchy) can be
     * created without saving each category after the creation of each category.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryManagerTest/"
                    + "after-create-multiple-categories.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(5000)
    public void createMultipleCategories() {

        shiro.getSystemUser().execute(() -> {
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
        });
    }

    /**
     * Verifies that
     * {@link CategoryManager#hasIndexObject(org.libreccm.categorization.Category)}
     * returns the expected value.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/categorization/CategoryManagerTest/"
                      + "data.yml")
    @InSequence(6000)
    public void hasIndexObject() {
        final Category category1 = categoryRepo.findById(-2100L);
        final Category category2 = categoryRepo.findById(-2200L);

        assertThat(categoryManager.hasIndexObject(category1), is(false));
        assertThat(categoryManager.hasIndexObject(category2), is(true));
    }

    /**
     * Tries to retrieve the index object from several categories and verifies
     * that the expected object is returned by 
     * {@link CategoryManager#getIndexObject(org.libreccm.categorization.Category).
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @InSequence(6500)
    public void getIndexObject() {
        final Category category1 = categoryRepo.findById(-2100L);
        final Category category2 = categoryRepo.findById(-2200L);

        assertThat(categoryManager.getIndexObject(category1).isPresent(),
                   is(false));

        final Optional<CcmObject> index2 = categoryManager.getIndexObject(
            category2);
        assertThat(index2.isPresent(), is(true));
        assertThat(index2.get().getDisplayName(), is(equalTo("object3")));
    }

    /**
     * This is a test to check if accessing multiple lazy fetched collections in
     * a single transaction (provided by
     * {@link TestCategoryController#getData(long)}) works.
     */
    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryManagerTest/data.yml")
    @InSequence(7000)
    public void retrieveMultipleLazyCollections() {
        assertThat(controller, is(not(nullValue())));

        final Map<String, List<String>> data = controller.getData(-2100L);

        final List<String> subCategories = data.get("subCategories");
        final List<String> objects = data.get("objects");

        assertThat(subCategories.size(), is(1));
        assertThat(subCategories.get(0), is(equalTo("bar")));
        assertThat(subCategories, hasItem("bar"));

        assertThat(objects.size(), is(1));
        assertThat(objects, hasItem("object1"));
    }

}
