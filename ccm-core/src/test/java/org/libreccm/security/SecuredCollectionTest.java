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
import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateTimeFormatter;

import java.io.File;
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
public class SecuredCollectionTest {

    private static final String ACCESS_DENIED = "Access denied";

    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    @Inject
    private CcmObjectRepository objectRepository;

    //private List<CcmObject> list;
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
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);
        final CcmObject object3 = objectRepository.findById(-20003L);

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
                    "LibreCCM-org.libreccm.security.SecuredCollectionTest.war").
            addPackage(User.class.getPackage())
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
