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

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.ShiroTest.war")
            .addPackage(org.libreccm.categorization.Category.class.getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addClass(org.libreccm.modules.CcmModule.class)
            .addClass(org.libreccm.modules.ModuleEvent.class)
            .addClass(org.libreccm.modules.InitEvent.class)
            .addClass(org.libreccm.modules.InstallEvent.class)
            .addClass(org.libreccm.modules.UnInstallEvent.class)
            .addClass(org.libreccm.modules.ShutdownEvent.class)
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class.getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addClass(org.libreccm.security.authorization.LabBean.class)
            .addClass(com.arsdigita.util.UncheckedWrapperException.class)
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
