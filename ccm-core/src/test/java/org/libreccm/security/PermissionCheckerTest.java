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

import java.io.File;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
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
public class PermissionCheckerTest {

    @Inject
    private Subject subject;

    @Inject
    private Shiro shiro;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private CcmObjectRepository objectRepository;

    public PermissionCheckerTest() {
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
        final PomEquippedResolveStage dependencies = pom.
            importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.PermissionCheckerTest.war")
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
            .addPackage(UncheckedWrapperException.class.getPackage())
            .addPackage(CCMApplicationContextListener.class.getPackage())
            .addPackage(XML.class.getPackage())
            .addPackage(DateTimeFormatter.class.getPackage())
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
    public void isPermittedAuthenticatedUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        assertThat(permissionChecker.isPermitted("privilege1"), is(false));
        assertThat(permissionChecker.isPermitted("privilege2"), is(false));
        assertThat(permissionChecker.isPermitted("privilege3"), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(200)
    public void isPermittedUnAuthenticatedUser() {
        assertThat(permissionChecker.isPermitted("privilege1"), is(false));
        assertThat(permissionChecker.isPermitted("privilege2"), is(false));
        assertThat(permissionChecker.isPermitted("privilege3"), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(200)
    public void isPermittedSystemUser() {
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);
        final CcmObject object3 = objectRepository.findById(-20003L);

        shiro.getSystemUser().execute(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                assertThat(permissionChecker.isPermitted("privilege1"),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege2"),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege3"),
                           is(true));

                assertThat(permissionChecker.isPermitted("privilege1",
                                                         object2),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege2",
                                                         object1),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege3",
                                                         object3),
                           is(true));

                return false;
            }

        });

    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(400)
    public void isPermittedObjectAuthenticatedUser() {
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);

        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        assertThat(permissionChecker.isPermitted("privilege1", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object2),
                   is(true));

    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(500)
    public void isPermittedObjectUnAuthenticatedUser() {
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);

        assertThat(permissionChecker.isPermitted("privilege1", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object2),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege3", object1),
                   is(true));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkPermissionAuthenticatedUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        permissionChecker.checkPermission("privilege1");
    }

    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(600)
    public void checkPermissionUnAuthenticatedUser() {
        permissionChecker.checkPermission("privilege1");
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkPermissionObjectAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L);

        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        permissionChecker.checkPermission("privilege2", object2);
    }

    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(600)
    public void checkPermissionObjectUnAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L);

        permissionChecker.checkPermission("privilege2", object2);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkPermissionObjectPublicUser() {
        final CcmObject object1 = objectRepository.findById(-20001L);

        permissionChecker.checkPermission("privilege3", object1);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkPermissionObjectSystemUser() {
        final CcmObject object1 = objectRepository.findById(-20001L);
        final CcmObject object2 = objectRepository.findById(-20002L);
        final CcmObject object3 = objectRepository.findById(-20003L);

        shiro.getSystemUser().execute(new Callable<Boolean>() {

            @Override
            public Boolean call() {
                permissionChecker.checkPermission("privilege1");
                permissionChecker.checkPermission("privilege2");
                permissionChecker.checkPermission("privilege3");

                permissionChecker.checkPermission("privilege1", object3);
                permissionChecker.checkPermission("privilege2", object1);
                permissionChecker.checkPermission("privilege3", object2);

                return false;
            }

        });
    }

}
