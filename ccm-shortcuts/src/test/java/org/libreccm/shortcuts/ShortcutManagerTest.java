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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
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
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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
        final PomEquippedResolveStage pom = Maven
                .resolver()
                .loadPomFromFile("pom.xml");
        final PomEquippedResolveStage dependencies = pom
                .importCompileAndRuntimeDependencies();
        dependencies.addDependency(MavenDependencies.createDependency(
                "org.libreccm:ccm-core", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
                "org.libreccm:ccm-testutils", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
                "net.sf.saxon:Saxon-HE", ScopeType.RUNTIME, false));
        dependencies.addDependency(MavenDependencies.createDependency(
                "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-maven",
                ScopeType.RUNTIME, false));
        final File[] libsWithCcmCore = dependencies.resolve().withTransitivity().
                asFile();

        final List<File> libsList = new ArrayList<>(libsWithCcmCore.length - 1);
        IntStream.range(0, libsWithCcmCore.length).forEach(i -> {
            final File lib = libsWithCcmCore[i];
            if (!lib.getName().startsWith("ccm-core")) {
                libsList.add(lib);
            }
        });
        final File[] libs = libsList.toArray(new File[libsList.size()]);

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap.create(
                WebArchive.class,
                "LibreCCM-org.libreccm.shortcuts.ShortcutTest-web.war")
                .addPackage(org.libreccm.categorization.Categorization.class
                        .getPackage())
                .addPackage(org.libreccm.configuration.Configuration.class
                        .getPackage())
                .addPackage(org.libreccm.core.CcmCore.class.getPackage())
                .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                        .getPackage())
                .addPackage(org.libreccm.l10n.LocalizedString.class
                        .getPackage())
                .addPackage(org.libreccm.security.Permission.class.getPackage())
                .addPackage(org.libreccm.shortcuts.Shortcuts.class.getPackage())
                .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
                .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
                .addAsLibraries(libs)
                .addAsResource("test-persistence.xml",
                               "META-INF/persistence.xml")
                .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
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
            value = "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml",
            excludeColumns = {"shortcut_id"})
    @InSequence(100)
    public void createShortcutStringParams() {
        shortcutManager.createShortcut("datenschutz",
                                       "/ccm/navigation/privacy");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(110)
    public void createShortcutStringParamsNullUrlKey() {
        shortcutManager.createShortcut(null, "http://www.example.org");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(120)
    public void createShortcutStringParamsNullRedirect() {
        shortcutManager.createShortcut("example", null);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(140)
    public void createShortcutStringParamsEmptyUrlKey() {
        shortcutManager.createShortcut("  ", "http://www.example.org");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(150)
    public void createShortcutStringParamsEmptyRedirect() {
        shortcutManager.createShortcut("example", " ");
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
            "datasets/org/libreccm/shortcuts/ShortcutManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(160)
    public void createShortcutStringParamsEmptyParams() {
        shortcutManager.createShortcut("", "");
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
