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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class CcmObjectRepositoryTest {

    @Inject
    private transient CcmObjectRepository ccmObjectRepository;

    @PersistenceContext(name = "LibreCCM")
    private transient EntityManager entityManager;

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
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...\n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.core.CcmObjectRepositoryTest.war").
            addPackage(CcmObject.class.getPackage())
            .addPackage(org.libreccm.web.Application.class.getPackage())
            .addPackage(org.libreccm.categorization.Category.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    public void repoIsInjected() {
        assertThat(ccmObjectRepository, is(not((nullValue()))));
    }

    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @InSequence(4)
    public void datasetOnly() {
        System.out.println("Dataset loaded successfully.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/after-save-changed.json")
    @InSequence(4)
    public void datasetOnly2() {
        System.out.println("Dataset loaded successfully.");
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
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

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @InSequence(6)
    public void entityManagerFindCcmObjectByLongClass() {
        final CcmObject obj1 = entityManager.find(CcmObject.class,
                                                  new Long(-10L));
        final CcmObject obj2 = entityManager.find(CcmObject.class,
                                                  new Long(-20L));
        final CcmObject obj3 = entityManager.find(CcmObject.class,
                                                  new Long(-30L));
        final CcmObject none = entityManager.find(CcmObject.class, new Long(
                                                  -999L));

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
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @InSequence(10)
    public void findCcmObjectById() {
        final CcmObject obj1 = ccmObjectRepository.findById(-10L);
        final CcmObject obj2 = ccmObjectRepository.findById(-20L);
        final CcmObject obj3 = ccmObjectRepository.findById(-30L);
        final CcmObject none = ccmObjectRepository.findById(-999L);

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
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @InSequence(10)
    public void findAllCcmObjects() {
        final List<CcmObject> objects = ccmObjectRepository.findAll();

        assertThat(objects.size(), is(3));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/CcmObjectRepositoryTest/after-save-new.json",
                        excludeColumns = {"object_id"})
    @InSequence(300)
    public void saveNewCcmObject() {
        final CcmObject obj = new CcmObject();
        obj.setDisplayName("Test Object 4");

        ccmObjectRepository.save(obj);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/CcmObjectRepositoryTest/after-save-changed.json",
                        excludeColumns = {"object_id"})
    @InSequence(400)
    public void saveChangedCcmObject() {
        final CcmObject obj = ccmObjectRepository.findById(-20L);
        obj.setDisplayName("Second Test Object");

        ccmObjectRepository.save(obj);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(500)
    public void saveNullValue() {
        ccmObjectRepository.save(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/CcmObjectRepositoryTest/data.json")
    @ShouldMatchDataSet(value
                            = "datasets/org/libreccm/core/CcmObjectRepositoryTest/after-delete.json",
                        excludeColumns = {"object_id"})
    @InSequence(600)
    public void deleteCcmObject() {
        final CcmObject obj = ccmObjectRepository.findById(-20L);

        ccmObjectRepository.delete(obj);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(700)
    public void deleteNullValue() {
        ccmObjectRepository.delete(null);
    }

}
