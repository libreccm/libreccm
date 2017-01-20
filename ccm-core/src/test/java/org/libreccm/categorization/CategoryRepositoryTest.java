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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.libreccm.testutils.DependenciesHelpers.*;

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

    @Inject
    private Shiro shiro;

    @Inject
    private Subject subject;

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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.categorization.CategoryRepositoryTest.war")
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
            .addClass(org.libreccm.portation.Portable.class)
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
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
        excludeColumns = {"object_id", "uuid"})
    @InSequence(3100)
    public void saveNewCategory() {
        final Category category = new Category();
        category.setDisplayName("new-category");
        category.setName("new-category");
        category.setUniqueId("new0001");

        shiro.getSystemUser().execute(() -> categoryRepo.save(category));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/categorization/CategoryRepositoryTest/"
                    + "after-save-new-category.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(3200)
    public void saveNewCategoryGlobalAuth() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final Category category = new Category();
        category.setDisplayName("new-category");
        category.setName("new-category");
        category.setUniqueId("new0001");

        categoryRepo.save(category);

        subject.logout();
    }

    @Test(expected = UnauthorizedException.class)
    @UsingDataSet(
        "datasets/org/libreccm/categorization/CategoryRepositoryTest/data.yml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(3400)
    public void saveNewCategoryNotAuthorized() {
        final Category category = new Category();
        category.setDisplayName("new-category");
        category.setName("new-category");
        category.setUniqueId("new0001");

        categoryRepo.save(category);
    }

}
