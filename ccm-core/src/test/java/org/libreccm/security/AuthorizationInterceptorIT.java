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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.security.authorization.LabBean;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"001_create_schema.sql",
               "002_create_ccm_core_tables.sql",
               "003_init_hibernate_sequence.sql"})
@CleanupUsingScript(value = {"999_cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class AuthorizationInterceptorIT {

    @Inject
    private Subject subject;

    @Inject
    private CcmObjectRepository objectRepository;

    @Inject
    private LabBean labBean;

    public AuthorizationInterceptorIT() {
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
                    "LibreCCM-org.libreccm.security.AuthorizationInterceptorTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm")
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/libreccm/security/ShiroTest/log4j2.xml",
                "log4j2.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    @InSequence(10)
    public void labBeanIsInjected() {
        assertThat(labBean, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(100)
    public void checkRequiresRoleAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        labBean.doSomethingWhichRequiresRole();
    }

    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(200)
    public void checkRequiresRoleNotAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        labBean.doSomethingWhichRequiresRole();
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(300)
    public void checkRequiresPermissionAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        labBean.doSomethingWhichRequiresPermission();
    }

    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(400)
    public void checkRequiresPermissionNotAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        labBean.doSomethingWhichRequiresPermission();
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(500)
    public void checkRequiresPermissionOnObjectAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final CcmObject object1 = objectRepository.findById(20001L).get();

        labBean.doSomethingWhichRequiresPermissionOnObject(object1);
    }

    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(600)
    public void checkRequiresPermissionOnObjectNotAuthorized() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final CcmObject object1 = objectRepository.findById(20001L).get();

        labBean.doSomethingWhichRequiresPermissionOnObject(object1);
    }

}
