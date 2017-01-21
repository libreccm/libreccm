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
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_shortcuts_schema.sql"})
public class ShortcutRepositoryTest {

    @Inject
    private ShortcutRepository shortcutRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ShortcutRepositoryTest() {
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
            "LibreCCM-org.libreccm.shortcuts.ShortcutRepositoryTest-web.war")
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addClass(org.libreccm.shortcuts.Shortcut.class)
            .addClass(org.libreccm.shortcuts.ShortcutRepository.class)
            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(1)
    public void repoIsInjected() {
        assertThat(shortcutRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(2)
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not((nullValue()))));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutRepositoryTest/data.xml")
    @InSequence(10)
    public void findByUrlKey() {

        final Optional<Shortcut> members = shortcutRepository.findByUrlKey(
            "members");
        final Optional<Shortcut> mitglieder = shortcutRepository.findByUrlKey(
            "mitglieder");
        final Optional<Shortcut> shop = shortcutRepository.findByUrlKey("shop");

        assertThat(members.isPresent(), is(true));
        assertThat(members.get().getUrlKey(), is(equalTo("/members/")));
        assertThat(members.get().getRedirect(),
                   is(equalTo("/ccm/navigation/members")));

        assertThat(mitglieder.isPresent(), is(true));
        assertThat(mitglieder.get().getUrlKey(), is(equalTo("/mitglieder/")));
        assertThat(mitglieder.get().getRedirect(),
                   is(equalTo("/ccm/navigation/members")));

        assertThat(shop.isPresent(), is(true));
        assertThat(shop.get().getUrlKey(),
                   is(equalTo("/shop/")));
        assertThat(shop.get().getRedirect(),
                   is(equalTo("http://www.example.com")));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutRepositoryTest/data.xml")
    @InSequence(10)
    public void findByUrlKeyNotExisting() {
        final Optional<Shortcut> result = shortcutRepository.findByUrlKey(
            "foo");

        assertThat(result, is(not(nullValue())));
        assertThat(result.isPresent(), is(false));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutRepositoryTest/data.xml")
    @InSequence(30)
    public void findByRedirect() {

        final List<Shortcut> toMembers = shortcutRepository.findByRedirect(
            "/ccm/navigation/members");
        assertThat(toMembers.size(), is(2));
        assertThat(toMembers.get(0).getUrlKey(), is(equalTo("/members/")));
        assertThat(toMembers.get(0).getRedirect(),
                   is(equalTo("/ccm/navigation/members")));
        assertThat(toMembers.get(1).getUrlKey(), is(equalTo("/mitglieder/")));
        assertThat(toMembers.get(1).getRedirect(),
                   is(equalTo("/ccm/navigation/members")));

        final List<Shortcut> toExampleCom = shortcutRepository.findByRedirect(
            "http://www.example.com");
        assertThat(toExampleCom.size(), is(1));
        assertThat(toExampleCom.get(0).getUrlKey(), is(equalTo("/shop/")));
        assertThat(toExampleCom.get(0).getRedirect(),
                   is(equalTo("http://www.example.com")));
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/shortcuts/ShortcutRepositoryTest/data.xml")
    @InSequence(30)
    public void findByRedirectNotExisting() {
        final List<Shortcut> result = shortcutRepository.findByRedirect(
            "http://www.example.org");

        assertThat(result, is(not(nullValue())));
        assertThat(result.isEmpty(), is(true));
    }

}
