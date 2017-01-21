/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.shortcuts;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_shortcuts_schema.sql"})
public class ShortcutManagerTest {

    @Inject
    private ShortcutManager shortcutManager;

    @Inject
    private Shiro shiro;

    @Inject
    private Subject subject;

    @PersistenceContext
    private EntityManager entityManager;

    public ShortcutManagerTest() {
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
        return ShrinkWrap.create(
            WebArchive.class,
            "LibreCCM-org.libreccm.shortcuts.ShortcutTest-web.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(org.libreccm.shortcuts.Shortcut.class)
            .addClass(org.libreccm.shortcuts.ShortcutManager.class)
            .addClass(org.libreccm.shortcuts.ShortcutRepository.class)
            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource("test-beans.xml", "META-INF/beans.xml");
    }

    @Test
    @InSequence(1)
    public void managerIsInjected() {
        assertThat(shortcutManager, is(not(nullValue())));
    }

    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/shortcuts/ShortcutManagerTest/after-create.xml",
        excludeColumns = {"shortcut_id"})
    @InSequence(100)
    public void createShortcutBySystemUser() {
        final Subject systemUser = shiro.getSystemUser();
        systemUser.execute(() -> {
            shortcutManager.createShortcut("datenschutz",
                                           "/ccm/navigation/privacy");

            return null;
        });

    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/shortcuts/ShortcutManagerTest/after-create.xml",
        excludeColumns = {"shortcut_id"})
    @InSequence(110)
    public void createShortcutByAuthorizedUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut("datenschutz",
                                       "/ccm/navigation/privacy");

        subject.logout();
    }

    @Test(expected = UnauthorizedException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(UnauthorizedException.class)
    @InSequence(120)
    public void createShortcutByUnAuthorizedUser() {
        shortcutManager.createShortcut("datenschutz",
                                       "/ccm/navigation/privacy");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(130)
    public void createShortcutNullUrlKey() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut(null, "http://www.example.org");

        subject.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(120)
    public void createShortcutNullRedirect() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut("example", null);

        subject.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(140)
    public void createShortcutEmptyUrlKey() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut("  ", "http://www.example.org");

        subject.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(150)
    public void createShortcutEmptyRedirect() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut("example", " ");

        subject.logout();
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(160)
    public void createShortcutEmptyParams() {
        final UsernamePasswordToken token = new UsernamePasswordToken(
            "john.doe@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        shortcutManager.createShortcut("", "");

        subject.logout();
    }

//    @Test
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldMatchDataSet(
//            value = "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml",
//            excludeColumns = {"shortcut_id"})
//    @InSequence(200)
//    public void createShortcutUrlParams() throws MalformedURLException {
//        final URL urlKey = new URL("datenschutz");
//        final URL redirect = new URL("/ccm/navigation/privacy");
//
//        shortcutManager.createShortcut(urlKey, redirect);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldThrowException(IllegalArgumentException.class)
//    @InSequence(210)
//    public void createShortcutUrlParamsNullUrlKey() throws MalformedURLException {
//        shortcutManager.createShortcut(null, new URL("http://www.example.org"));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldThrowException(IllegalArgumentException.class)
//    @InSequence(220)
//    public void createShortcutUrlParamsNullRedirect() throws
//            MalformedURLException {
//        shortcutManager.createShortcut(new URL("example"), null);
//    }
//
//    @Test
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldMatchDataSet(
//            value = "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml",
//            excludeColumns = {"shortcut_id"})
//    @InSequence(300)
//    public void createShortcutUriParams() throws URISyntaxException {
//        final URI urlKey = new URI("datenschutz");
//        final URI redirect = new URI("/ccm/navigation/privacy");
//
//        shortcutManager.createShortcut(urlKey, redirect);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldThrowException(IllegalArgumentException.class)
//    @InSequence(310)
//    public void createShortcutUriParamsNullUrlKey() throws URISyntaxException {
//        shortcutManager.createShortcut(null, new URI("http://www.example.org"));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    @UsingDataSet(
//            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
//    @ShouldThrowException(IllegalArgumentException.class)
//    @InSequence(320)
//    public void createShortcutUriParamsNullRedirect() throws URISyntaxException {
//        shortcutManager.createShortcut(new URI("example"), null);
//    }
}
