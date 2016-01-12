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

import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateTimeFormatter;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.File;

import javax.inject.Inject;

import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

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
public class ShiroTest {

    @Inject
    private Subject subject;

    public ShiroTest() {
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
                    "LibreCCM-org.libreccm.security.ShiroTest.war")
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
            .addPackage(SecurityConfig.class.getPackage())
            .addPackage(AbstractConfig.class.getPackage())
            .addPackage(AbstractParameterContext.class.getPackage())
            .addPackage(CCMApplicationContextListener.class.getPackage())
            .addPackage(XML.class.getPackage())
            .addPackage(DateTimeFormatter.class.getPackage())
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
    @InSequence(100)
    public void subjectIsInjected() {
        assertThat(subject, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(200)
    public void loginUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);

        subject.login(token);

        assertThat(subject.isAuthenticated(), is(true));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void logoutUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);

        subject.login(token);

        assertThat(subject.isAuthenticated(), is(true));

        subject.logout();

        assertThat(subject.isAuthenticated(), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void checkRolesAndPermissionsJdoe() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);

        subject.login(token);

        assertThat(subject.isAuthenticated(), is(true));

        assertThat(subject.hasRole("role1"), is(false));
        assertThat(subject.hasRole("role2"), is(true));

        assertThat(subject.isPermitted("privilege1"), is(false));
        assertThat(subject.isPermitted("privilege2:-20001"), is(false));
        assertThat(subject.isPermitted("privilege2:-20002"), is(true));

        assertThat(subject.isPermitted("privilege2"), is(false));
        assertThat(subject.isPermitted("privilege1:999"), is(false));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void checkRolesAndPermissionsMmuster() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);

        subject.login(token);

        assertThat(subject.isAuthenticated(), is(true));

        assertThat(subject.hasRole("role1"), is(true));
        assertThat(subject.hasRole("role2"), is(false));

        assertThat(subject.isPermitted("privilege1"), is(true));
        assertThat(subject.isPermitted("privilege2:-20001"), is(true));
        assertThat(subject.isPermitted("privilege2:-20002"), is(false));

        assertThat(subject.isPermitted("privilege2"), is(false));
        assertThat(subject.isPermitted("privilege1"), is(true));
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(400)
    public void userCantLoginWithWrongPassword() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "pw");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(500)
    public void userCantLoginWithEmptyPassword() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(600)
    public void userWithoutPasswordCantLogin() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "public-user",
            "foo123");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(700)
    public void userWithoutPasswordCantLoginWithEmptyPassword() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "public-user",
            "");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(800)
    public void unknownUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "unknown-user",
            "foo123");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(810)
    public void nullUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            null,
            "foo123");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test(expected = AuthenticationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthenticationException.class)
    @InSequence(820)
    public void emptyUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "",
            "foo123");
        token.setRememberMe(true);

        subject.login(token);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(900)
    public void publicUser() {
        final PrincipalCollection principals = new SimplePrincipalCollection(
            "public-user", "CcmShiroRealm");
        final Subject publicUser = new Subject.Builder()
            .principals(principals)
            .authenticated(true)
            .buildSubject();

        assertThat(publicUser.hasRole("role1"), is(false));
        assertThat(publicUser.hasRole("role2"), is(false));
        assertThat(publicUser.hasRole("public-role"), is(true));

        assertThat(publicUser.isPermitted("privilege1"), is(false));
        assertThat(publicUser.isPermitted("privilege2:-20001"), is(false));
        assertThat(publicUser.isPermitted("privilege2:-20002"), is(false));
        assertThat(publicUser.isPermitted("privilege3:-20001"), is(true));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(910)
    public void systemUser() {
        final PrincipalCollection principals = new SimplePrincipalCollection(
            "system-user", "CcmShiroRealm");
        final Subject publicUser = new Subject.Builder()
            .principals(principals)
            .authenticated(true)
            .buildSubject();

        assertThat(publicUser.hasRole("role1"), is(true));
        assertThat(publicUser.hasRole("role2"), is(true));
        assertThat(publicUser.hasRole("public-role"), is(true));

        assertThat(publicUser.isPermitted("privilege1"), is(true));
        assertThat(publicUser.isPermitted("privilege2:-20001"), is(true));
        assertThat(publicUser.isPermitted("privilege2:-20002"), is(true));
        assertThat(publicUser.isPermitted("privilege3:-20001"), is(true));

    }

}
