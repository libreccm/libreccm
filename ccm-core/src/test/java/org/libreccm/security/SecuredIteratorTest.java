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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.libreccm.testutils.DependenciesHelpers.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class SecuredIteratorTest {

    private static final String ACCESS_DENIED = "Access denied";

    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    @Inject
    private CcmObjectRepository objectRepository;

    //private List<CcmObject> list;
    private Iterator<CcmObject> iterator1;
    private Iterator<CcmObject> iterator2;
    private Iterator<CcmObject> iterator3;

    public SecuredIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);
        final CcmObject object3 = objectRepository.findById(-20003L);

        final List<CcmObject> list = new ArrayList<>();
        list.add(object1);
        list.add(object2);
        list.add(object3);

        iterator1 = new SecuredIterator<>(list.iterator(),
                                          CcmObject.class,
                                          "privilege1");
        iterator2 = new SecuredIterator<>(list.iterator(),
                                          CcmObject.class,
                                          "privilege2");
        iterator3 = new SecuredIterator<>(list.iterator(),
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
                    "LibreCCM-org.libreccm.security.SecuredIteratorTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.util.UncheckedWrapperException.class
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
    public void checkSecuredIteratorJdoe() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<CcmObject> list1 = new ArrayList<>();
        while (iterator1.hasNext()) {
            list1.add(iterator1.next());
        }
        assertThat(list1.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list1.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list1.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

        final List<CcmObject> list2 = new ArrayList<>();
        while (iterator2.hasNext()) {
            list2.add(iterator2.next());
        }
        assertThat(list2.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list2.get(1).getDisplayName(), is(equalTo("object2")));
        assertThat(list2.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

        final List<CcmObject> list3 = new ArrayList<>();
        while (iterator3.hasNext()) {
            list3.add(iterator3.next());
        }
        assertThat(list3.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list3.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list3.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(200)
    public void checkSecuredIteratorMmuster() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<CcmObject> list1 = new ArrayList<>();
        while (iterator1.hasNext()) {
            list1.add(iterator1.next());
        }
        assertThat(list1.get(0).getDisplayName(), is(equalTo("object1")));
        assertThat(list1.get(1).getDisplayName(), is(equalTo("object2")));
        assertThat(list1.get(2).getDisplayName(), is(equalTo("object3")));

        final List<CcmObject> list2 = new ArrayList<>();
        while (iterator2.hasNext()) {
            list2.add(iterator2.next());
        }
        assertThat(list2.get(0).getDisplayName(), is(equalTo("object1")));
        assertThat(list2.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list2.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

        final List<CcmObject> list3 = new ArrayList<>();
        while (iterator3.hasNext()) {
            list3.add(iterator3.next());
        }
        assertThat(list3.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list3.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list3.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void checkSecuredIteratorPublicUser() {
        final List<CcmObject> list1 = new ArrayList<>();
        while (iterator1.hasNext()) {
            list1.add(iterator1.next());
        }
        assertThat(list1.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list1.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list1.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

        final List<CcmObject> list2 = new ArrayList<>();
        while (iterator2.hasNext()) {
            list2.add(iterator2.next());
        }
        assertThat(list2.get(0).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list2.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list2.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));

        final List<CcmObject> list3 = new ArrayList<>();
        while (iterator3.hasNext()) {
            list3.add(iterator3.next());
        }
        assertThat(list3.get(0).getDisplayName(), is(equalTo("object1")));
        assertThat(list3.get(1).getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(list3.get(2).getDisplayName(), is(equalTo(ACCESS_DENIED)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(400)
    public void checkSecuredIteratorSystemUser() {
        shiro.getSystemUser().execute(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                final List<CcmObject> list1 = new ArrayList<>();
                while (iterator1.hasNext()) {
                    list1.add(iterator1.next());
                }
                assertThat(list1.get(0).getDisplayName(), is(equalTo("object1")));
                assertThat(list1.get(1).getDisplayName(), is(equalTo("object2")));
                assertThat(list1.get(2).getDisplayName(), is(equalTo("object3")));

                final List<CcmObject> list2 = new ArrayList<>();
                while (iterator2.hasNext()) {
                    list2.add(iterator2.next());
                }
                assertThat(list2.get(0).getDisplayName(), is(equalTo("object1")));
                assertThat(list2.get(1).getDisplayName(), is(equalTo("object2")));
                assertThat(list2.get(2).getDisplayName(), is(equalTo("object3")));

                final List<CcmObject> list3 = new ArrayList<>();
                while (iterator3.hasNext()) {
                    list3.add(iterator3.next());
                }
                assertThat(list3.get(0).getDisplayName(), is(equalTo("object1")));
                assertThat(list3.get(1).getDisplayName(), is(equalTo("object2")));
                assertThat(list3.get(2).getDisplayName(), is(equalTo("object3")));

                return false;
            }

        });
    }

}
