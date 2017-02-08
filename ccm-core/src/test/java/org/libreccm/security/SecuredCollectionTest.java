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
package org.libreccm.security;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
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
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.tests.categories.IntegrationTest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript({"cleanup.sql"})
public class SecuredCollectionTest {

    private static final String ACCESS_DENIED = "Access denied";

    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    @Inject
    private CcmObjectRepository objectRepository;

    private SecuredCollection<CcmObject> collection1;
    private SecuredCollection<CcmObject> collection2;
    private SecuredCollection<CcmObject> collection3;

    public SecuredCollectionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        final CcmObject object3 = objectRepository.findById(-20003L).get();

        final List<CcmObject> list = new ArrayList<>();
        list.add(object1);
        list.add(object2);
        list.add(object3);

        collection1 = new SecuredCollection<>(list,
                                              CcmObject.class,
                                              "privilege1");
        collection2 = new SecuredCollection<>(list,
                                              CcmObject.class,
                                              "privilege2");
        collection3 = new SecuredCollection<>(list,
                                              CcmObject.class,
                                              "privilege3");
    }

    @After
    public void tearDown() {
    }

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.SecuredCollectionTest.war")
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/libreccm/security/ShiroTest/log4j2.xml",
                "log4j2.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(100)
    public void checkToArrayJdoe() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final Object[] array1 = collection1.toArray();
        assertThat(array1.length, is(3));
        assertThat(array1[0], is(instanceOf(CcmObject.class)));
        assertThat(array1[1], is(instanceOf(CcmObject.class)));
        assertThat(array1[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array1[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array1[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array1[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));

        final Object[] array2 = collection2.toArray();
        assertThat(array2.length, is(3));
        assertThat(array2[0], is(instanceOf(CcmObject.class)));
        assertThat(array2[1], is(instanceOf(CcmObject.class)));
        assertThat(array2[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array2[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array2[1]).getDisplayName(),
                   is(equalTo("object2")));
        assertThat(((CcmObject) array2[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));

        final Object[] array3 = collection3.toArray();
        assertThat(array3.length, is(3));
        assertThat(array3[0], is(instanceOf(CcmObject.class)));
        assertThat(array3[1], is(instanceOf(CcmObject.class)));
        assertThat(array3[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array3[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array3[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array3[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(200)
    public void checkToArrayMmuster() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final Object[] array1 = collection1.toArray();
        assertThat(array1.length, is(3));
        assertThat(array1[0], is(instanceOf(CcmObject.class)));
        assertThat(array1[1], is(instanceOf(CcmObject.class)));
        assertThat(array1[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array1[0]).getDisplayName(),
                   is(equalTo("object1")));
        assertThat(((CcmObject) array1[1]).getDisplayName(),
                   is(equalTo("object2")));
        assertThat(((CcmObject) array1[2]).getDisplayName(),
                   is(equalTo("object3")));

        final Object[] array2 = collection2.toArray();
        assertThat(array2.length, is(3));
        assertThat(array2[0], is(instanceOf(CcmObject.class)));
        assertThat(array2[1], is(instanceOf(CcmObject.class)));
        assertThat(array2[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array2[0]).getDisplayName(),
                   is(equalTo("object1")));
        assertThat(((CcmObject) array2[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array2[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));

        final Object[] array3 = collection3.toArray();
        assertThat(array3.length, is(3));
        assertThat(array3[0], is(instanceOf(CcmObject.class)));
        assertThat(array3[1], is(instanceOf(CcmObject.class)));
        assertThat(array3[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array3[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array3[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array3[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void checkToArrayPublicUser() {
        final Object[] array1 = collection1.toArray();
        assertThat(array1.length, is(3));
        assertThat(array1[0], is(instanceOf(CcmObject.class)));
        assertThat(array1[1], is(instanceOf(CcmObject.class)));
        assertThat(array1[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array1[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array1[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array1[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));

        final Object[] array2 = collection2.toArray();
        assertThat(array2.length, is(3));
        assertThat(array2[0], is(instanceOf(CcmObject.class)));
        assertThat(array2[1], is(instanceOf(CcmObject.class)));
        assertThat(array2[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array2[0]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array2[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array2[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));

        final Object[] array3 = collection3.toArray();
        assertThat(array3.length, is(3));
        assertThat(array3[0], is(instanceOf(CcmObject.class)));
        assertThat(array3[1], is(instanceOf(CcmObject.class)));
        assertThat(array3[2], is(instanceOf(CcmObject.class)));
        assertThat(((CcmObject) array3[0]).getDisplayName(),
                   is(equalTo("object1")));
        assertThat(((CcmObject) array3[1]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
        assertThat(((CcmObject) array3[2]).getDisplayName(),
                   is(equalTo(ACCESS_DENIED)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(400)
    public void checkToArraySystemUser() {
        shiro.getSystemUser().execute(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final Object[] array1 = collection1.toArray();
                assertThat(array1.length, is(3));
                assertThat(array1[0], is(instanceOf(CcmObject.class)));
                assertThat(array1[1], is(instanceOf(CcmObject.class)));
                assertThat(array1[2], is(instanceOf(CcmObject.class)));
                assertThat(((CcmObject) array1[0]).getDisplayName(),
                           is(equalTo("object1")));
                assertThat(((CcmObject) array1[1]).getDisplayName(),
                           is(equalTo("object2")));
                assertThat(((CcmObject) array1[2]).getDisplayName(),
                           is(equalTo("object3")));

                final Object[] array2 = collection2.toArray();
                assertThat(array2.length, is(3));
                assertThat(array2[0], is(instanceOf(CcmObject.class)));
                assertThat(array2[1], is(instanceOf(CcmObject.class)));
                assertThat(array2[2], is(instanceOf(CcmObject.class)));
                assertThat(((CcmObject) array2[0]).getDisplayName(),
                           is(equalTo("object1")));
                assertThat(((CcmObject) array2[1]).getDisplayName(),
                           is(equalTo("object2")));
                assertThat(((CcmObject) array2[2]).getDisplayName(),
                           is(equalTo("object3")));

                final Object[] array3 = collection3.toArray();
                assertThat(array3.length, is(3));
                assertThat(array3[0], is(instanceOf(CcmObject.class)));
                assertThat(array3[1], is(instanceOf(CcmObject.class)));
                assertThat(array3[2], is(instanceOf(CcmObject.class)));
                assertThat(((CcmObject) array3[0]).getDisplayName(),
                           is(equalTo("object1")));
                assertThat(((CcmObject) array3[1]).getDisplayName(),
                           is(equalTo("object2")));
                assertThat(((CcmObject) array3[2]).getDisplayName(),
                           is(equalTo("object3")));

                return false;
            }

        });
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(500)
    public void checkToArrayTypeSafeJdoe() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        CcmObject[] array1 = new CcmObject[3];
        array1 = collection1.toArray(array1);
        assertThat(array1[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array1[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array1[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));

        CcmObject[] array2 = new CcmObject[3];
        array2 = collection2.toArray(array2);
        assertThat(array2[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array2[1].getDisplayName(), (is(equalTo("object2"))));
        assertThat(array2[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));

        CcmObject[] array3 = new CcmObject[3];
        array3 = collection3.toArray(array3);
        assertThat(array3[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array3[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array3[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkToArrayTypeSafeMmuster() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        CcmObject[] array1 = new CcmObject[3];
        array1 = collection1.toArray(array1);
        assertThat(array1[0].getDisplayName(), (is(equalTo("object1"))));
        assertThat(array1[1].getDisplayName(), (is(equalTo("object2"))));
        assertThat(array1[2].getDisplayName(), (is(equalTo("object3"))));

        CcmObject[] array2 = new CcmObject[3];
        array2 = collection2.toArray(array2);
        assertThat(array2[0].getDisplayName(), (is(equalTo("object1"))));
        assertThat(array2[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array2[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));

        CcmObject[] array3 = new CcmObject[3];
        array3 = collection3.toArray(array3);
        assertThat(array3[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array3[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array3[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(700)
    public void checkToArrayTypeSafePublicUser() {
        CcmObject[] array1 = new CcmObject[3];
        array1 = collection1.toArray(array1);
        assertThat(array1[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array1[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array1[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));

        CcmObject[] array2 = new CcmObject[3];
        array2 = collection2.toArray(array2);
        assertThat(array2[0].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array2[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array2[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));

        CcmObject[] array3 = new CcmObject[3];
        array3 = collection3.toArray(array3);
        assertThat(array3[0].getDisplayName(), (is(equalTo("object1"))));
        assertThat(array3[1].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
        assertThat(array3[2].getDisplayName(), (is(equalTo(ACCESS_DENIED))));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(700)
    public void checkToArrayTypeSystemUser() {
        shiro.getSystemUser().execute(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                CcmObject[] array1 = new CcmObject[3];
                array1 = collection1.toArray(array1);
                assertThat(array1[0].getDisplayName(),
                           is(equalTo("object1")));
                assertThat(array1[1].getDisplayName(),
                           is(equalTo("object2")));
                assertThat(array1[2].getDisplayName(),
                           is(equalTo("object3")));

                CcmObject[] array2 = new CcmObject[3];
                array2 = collection2.toArray(array2);
                assertThat(array2[0].getDisplayName(),
                           is(equalTo("object1")));
                assertThat(array2[1].getDisplayName(),
                           is(equalTo("object2")));
                assertThat(array2[2].getDisplayName(),
                           is(equalTo("object3")));

                CcmObject[] array3 = new CcmObject[3];
                array3 = collection3.toArray(array3);
                assertThat(array3[0].getDisplayName(),
                           is(equalTo("object1")));
                assertThat(array3[1].getDisplayName(),
                           is(equalTo("object2")));
                assertThat(array3[2].getDisplayName(),
                           is(equalTo("object3")));

                return false;
            }

        });
    }

}
