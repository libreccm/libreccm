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
package org.libreccm.core;

import static org.hamcrest.CoreMatchers.*;

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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;

import java.util.Optional;

/**
 * Tests for the {@link CcmObjectRepository} which is the foundation for many
 * other repositories in LibreCCM.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript({"cleanup.sql"})
public class CcmObjectRepositoryTest {

    @Inject
    private CcmObjectRepository ccmObjectRepository;

    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;

    public CcmObjectRepositoryTest() {
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
                    "LibreCCM-org.libreccm.core.CcmObjectRepositoryTest.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class.
                getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.PermissionChecker.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Verify that an {@link CcmObjectRepository} instance is injected.
     */
    @Test
    @InSequence(1)
    public void repoIsInjected() {
        assertThat(ccmObjectRepository, is(not((nullValue()))));
    }

    /**
     * Verify that an {@link EntityManager} is injected.
     */
    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    /**
     * Verify that the basic dataset loads successfully.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @InSequence(3)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    /**
     * Verify that the {@code after-save-changed.yml} dataset loads
     * successfully.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/"
                      + "after-save-changed.yml")
    @InSequence(4)
    public void datasetOnly2() {
        System.out.println("Dataset loaded successfully.");
    }

    /**
     * Tries to find several objects by using
     * {@link EntityManager#find(java.lang.Class, java.lang.Object)} with a
     * value of type {@code long}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @InSequence(5)
    public void entityManagerFindCcmObjectByLongPrimitive() {
        final CcmObject obj1 = entityManager.find(CcmObject.class, -10L);
        final CcmObject obj2 = entityManager.find(CcmObject.class, -20L);
        final CcmObject obj3 = entityManager.find(CcmObject.class, -30L);
        final CcmObject none = entityManager.find(CcmObject.class, -999L);

        assertThat(obj1, is(not(nullValue())));
        assertThat(obj1.getObjectId(), is(-10L));
        assertThat(obj1.getDisplayName(), is(equalTo("Test Object 1")));

        assertThat(obj2, is(not(nullValue())));
        assertThat(obj2.getObjectId(), is(-20L));
        assertThat(obj2.getDisplayName(), is(equalTo("Test Object 2")));

        assertThat(obj3, is(not(nullValue())));
        assertThat(obj3.getObjectId(), is(-30L));
        assertThat(obj3.getDisplayName(), is(equalTo("Test Object 3")));

        assertThat(none, is(nullValue()));
    }

    /**
     * Tries to find several objects by using
     * {@link EntityManager#find(java.lang.Class, java.lang.Object)} with a
     * value of type {@link Long}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @InSequence(6)
    public void entityManagerFindCcmObjectByLongClass() {
        final Long id1 = -10L;
        final Long id2 = -20L;
        final Long id3 = -30L;
        final Long id4 = -999L;

        final CcmObject obj1 = entityManager.find(CcmObject.class, id1);
        final CcmObject obj2 = entityManager.find(CcmObject.class, id2);
        final CcmObject obj3 = entityManager.find(CcmObject.class, id3);
        final CcmObject none = entityManager.find(CcmObject.class, id4);

        assertThat(obj1, is(not(nullValue())));
        assertThat(obj1.getObjectId(), is(-10L));
        assertThat(obj1.getDisplayName(), is(equalTo("Test Object 1")));

        assertThat(obj2, is(not(nullValue())));
        assertThat(obj2.getObjectId(), is(-20L));
        assertThat(obj2.getDisplayName(), is(equalTo("Test Object 2")));

        assertThat(obj3, is(not(nullValue())));
        assertThat(obj3.getObjectId(), is(-30L));
        assertThat(obj3.getDisplayName(), is(equalTo("Test Object 3")));

        assertThat(none, is(nullValue()));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @InSequence(10)
    public void findCcmObjectById() {
        final Optional<CcmObject> obj1 = ccmObjectRepository.findById(-10L);
        final Optional<CcmObject> obj2 = ccmObjectRepository.findById(-20L);
        final Optional<CcmObject> obj3 = ccmObjectRepository.findById(-30L);
        final Optional<CcmObject> none = ccmObjectRepository.findById(-999L);

        assertThat(obj1.isPresent(), is(true));
        assertThat(obj1.get().getObjectId(), is(-10L));
        assertThat(obj1.get().getDisplayName(), is(equalTo("Test Object 1")));

        assertThat(obj2.isPresent(), is(true));
        assertThat(obj2.get().getObjectId(), is(-20L));
        assertThat(obj2.get().getDisplayName(), is(equalTo("Test Object 2")));

        assertThat(obj3.isPresent(), is(true));
        assertThat(obj3.get().getObjectId(), is(-30L));
        assertThat(obj3.get().getDisplayName(), is(equalTo("Test Object 3")));

        assertThat(none.isPresent(), is(false));
    }

    /**
     * Tries to find all {@link CcmObject}s in the test database by using
     * {@link CcmObjectRepository#findAll()}.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @InSequence(10)
    public void findAllCcmObjects() {
        final List<CcmObject> objects = ccmObjectRepository.findAll();

        assertThat(objects.size(), is(3));
    }

    /**
     * Tries to save a new {@link CcmObject} using
     * {@link CcmObjectRepository#save(java.lang.Object)} and verifes that it is
     * saved to the * database.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/core/CcmObjectRepositoryTest/"
                    + "after-save-new.yml",
        excludeColumns = {"object_id", "uuid"})
    @InSequence(300)
    public void saveNewCcmObject() {
        final CcmObject obj = new CcmObject();
        obj.setDisplayName("Test Object 4");

        ccmObjectRepository.save(obj);
    }

    /**
     * Changes some values of one of the {@link CcmObject}s in the test
     * database, saves the changes to the database by using
     * {@link CcmObjectRepository#save(java.lang.Object)} and verifies that the
     * changes have been written to the database.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/core/CcmObjectRepositoryTest/"
                    + "after-save-changed.yml",
        excludeColumns = {"object_id"})
    @InSequence(400)
    public void saveChangedCcmObject() {
        final CcmObject obj = ccmObjectRepository.findById(-20L).get();
        obj.setDisplayName("Second Test Object");

        ccmObjectRepository.save(obj);
    }

    /**
     * Verifies that {@link CcmObjectRepository#save(java.lang.Object)} throws a
     * {@link IllegalArgumentException} if called with {@code null} as the
     * object to save.
     */
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(500)
    public void saveNullValue() {
        ccmObjectRepository.save(null);
    }

    /**
     * Deletes one of the {@link CcmObject}s in the database by using 
     * {@link CcmObjectRepository#delete(java.lang.Object)} and verifies that
     * the object has been removed from the test database.
     */
    @Test
    @UsingDataSet("datasets/org/libreccm/core/CcmObjectRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/core/CcmObjectRepositoryTest/"
                    + "after-delete.yml",
        excludeColumns = {"object_id"})
    @InSequence(600)
    public void deleteCcmObject() {
        final CcmObject obj = ccmObjectRepository.findById(-20L).get();

        ccmObjectRepository.delete(obj);
    }

    /**
     * Verifies that {@link CcmObjectRepository#delete(java.lang.Object)} throws
     * a {@link IllegalArgumentException} if called with {@link null} for the 
     * object to delete.
     */
    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void deleteNullValue() {
        ccmObjectRepository.delete(null);
    }

}
