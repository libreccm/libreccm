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

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateTimeFormatter;

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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.categorization.Categorization;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom.
            importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.SecuredIteratorTest.war")
            .addPackage(User.class.getPackage())
            .addPackage(CcmObject.class.getPackage())
            .addPackage(Categorization.class.getPackage())
            .addPackage(LocalizedString.class.getPackage())
            .addPackage(CcmApplication.class.getPackage())
            .addPackage(Workflow.class.getPackage())
            .addPackage(EntityManagerProducer.class.getPackage())
            .addPackage(MimeTypeConverter.class.getPackage())
            .addPackage(EqualsVerifier.class.getPackage())
            .addPackage(IntegrationTest.class.getPackage())
            .addPackage(KernelConfig.class.getPackage())
            .addPackage(SecurityConfig.class.getPackage())
            .addPackage(AbstractConfig.class.getPackage())
            .addPackage(AbstractParameterContext.class.getPackage())
            .addPackage(CCMApplicationContextListener.class.getPackage())
            .addPackage(XML.class.getPackage())
            .addPackage(DateTimeFormatter.class.getPackage())
            .addPackage(CdiUtil.class.getPackage())
            .addPackage(UncheckedWrapperException.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("com/arsdigita/kernel/"
                               + "KernelConfig_parameter.properties",
                           "com/arsdigita/kernel/"
                               + "KernelConfig_parameter.properties")
            .addAsResource("com/arsdigita/kernel/security/"
                               + "SecurityConfig_parameter.properties",
                           "com/arsdigita/kernel/security/"
                               + "SecurityConfig_parameter.properties")
            .addAsWebInfResource(
                "configs/org/libreccm/security/UserManagerTest/"
                    + "registry.properties",
                "conf/registry/registry.properties")
            .addAsResource(
                "configs/org/libreccm/security/UserManagerTest/ccm-core.config",
                "ccm-core.config")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/libreccm/security/ShiroTest/log4j2.xml",
                "log4j2.xml")
            .addAsWebInfResource(
                "configs/org/libreccm/security/ShiroTest/"
                    + "kernel.properties",
                "conf/registry/ccm-core/kernel.properties")
            .addAsWebInfResource(
                "configs/org/libreccm//security/ShiroTest/"
                    + "security.properties",
                "conf/registry/ccm-core/security.properties")
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
