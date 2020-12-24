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
package org.librecms.contentsection;

import static org.libreccm.testutils.DependenciesHelpers.getCcmCoreDependencies;

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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libreccm.security.Shiro;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Verifies the methods of the {@link ContentItemL10NManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema(
    {
        "001_create_schema.sql",
        "002_create_ccm_cms_tables.sql",
        "003_init_hibernate_sequence.sql"
    }
)
@CleanupUsingScript(
    value = {
        "999_cleanup.sql"
    },
    phase = TestExecutionPhase.BEFORE
)
public class ContentItemL10NManagerIT {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemL10NManager l10nManager;

    @Inject
    private Shiro shiro;

    public ContentItemL10NManagerIT() {
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
                    "LibreCCM-org.librecms.contentsection.ContentItemManagerTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm", "org.librecms")
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/librecms/contentsection/ContentItemManagerTest/log4j2.xml",
                "log4j2.xml")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
             .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    @Test
    @InSequence(1)
    public void checkInjections() {
        assertThat(itemRepo, is(not(nullValue())));
        assertThat(itemManager, is(not(nullValue())));
        assertThat(l10nManager, is(not(nullValue())));
    }

    /**
     * Verifies
     * {@link ContentItemL10NManager#hasLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * with several content items.
     */
    @Test
    @InSequence(10)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    public void verifyHasLanguage() {
        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            assertThat(l10nManager.hasLanguage(item.get(), Locale.ENGLISH),
                       is(true));
            assertThat(l10nManager.hasLanguage(item.get(), Locale.FRENCH),
                       is(true));
            assertThat(l10nManager.hasLanguage(item.get(), Locale.GERMAN),
                       is(false));
        });

    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#hasLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(20)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void hasLanguageItemIsNull() {
        final ContentItem item = null;

        l10nManager.hasLanguage(item, Locale.ENGLISH);
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#hasLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the language.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(30)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void hasLanguageLanguageIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            assertThat(l10nManager.hasLanguage(item.get(), null),
                       is(true));
        });

    }

    /**
     * Tries to add language to a content item by using
     * {@link ContentItemL10NManager#addLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}.
     */
    @Test
    @InSequence(40)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/after-add-language.xml",
        excludeColumns = {"timestamp"})
    public void addLanguage() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.addLanguage(item.get(), Locale.GERMAN);
        });
    }

    /**
     * Verifies that calling
     * {@link ContentItemL10NManager#addLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * with an existing language has not effect.
     */
    @Test
    @InSequence(50)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    public void addLanguageAlreadyPresent() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.addLanguage(item.get(), Locale.FRENCH);
        });

    }

    /**
     * Verifies that {@link ContentItemL10NManager#addLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)
     * throws an {@link IllegalArgumentException} is called with
     * {@code null} for the item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(60)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void addLanguageItemIsNull() {

        shiro.getSystemUser().execute(() -> {
            final ContentItem item = null;

            l10nManager.addLanguage(item, Locale.GERMAN);
        });
    }

    /**
     * Verifies that {@link ContentItemL10NManager#addLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)
     * throws an {@link IllegalArgumentException} is called with
     * {@code null} for the language.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(20)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void addLanguageLanguageIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.addLanguage(item.get(), null);
        });
    }

    /**
     * Tries to remove language from a content item by using
     * {@link ContentItemL10NManager#removeLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}.
     */
    @Test
    @InSequence(70)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/after-remove-language.xml",
        excludeColumns = {"timestamp"})
    public void removeLanguage() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.removeLanguage(item.get(), Locale.FRENCH);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * has not effect if called for not present language.
     */
    @Test
    @InSequence(80)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    public void removeNotPresentLanguage() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.removeLanguage(item.get(), Locale.GERMAN);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(90)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void removeLanguageItemIsNull() {

        shiro.getSystemUser().execute(() -> {
            final ContentItem item = null;

            l10nManager.removeLanguage(item, Locale.GERMAN);
        });
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLanguage(org.librecms.contentsection.ContentItem, java.util.Locale)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the language.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void removeLanguageLanguageIsNull() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.removeLanguage(item.get(), null);
        });

    }

    /**
     * Tries to normalise the languages of a content item by using
     * {@link ContentItemL10NManager#normalizeLanguages(org.librecms.contentsection.ContentItem)}
     */
    @Test
    @InSequence(120)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/after-normalize.xml",
        excludeColumns = {"timestamp"})
    public void normalizeItem() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10200L);
            assertThat(item.isPresent(), is(true));

            l10nManager.normalizeLanguages(item.get());
        });
    }

    /**
     * Verifies that calling
     * {@link ContentItemL10NManager#normalizeLanguages(org.librecms.contentsection.ContentItem)}
     * for already normalised item has not effect.
     */
    @Test
    @InSequence(130)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    public void normalizeItemAlreadyNormalized() {

        shiro.getSystemUser().execute(() -> {
            final Optional<ContentItem> item = itemRepo.findById(10100L);
            assertThat(item.isPresent(), is(true));

            l10nManager.normalizeLanguages(item.get());
        });

    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#normalizeLanguages(org.librecms.contentsection.ContentItem)}
     * throws an {@link IllegalArgumentException} if called with {@code null}
     * for the item.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(140)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemL10NManagerTest/data.xml")
    @ShouldMatchDataSet(
        value = "datasets/org/librecms/contentsection/"
                    + "ContentItemL10NManagerTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void normalizeItemNull() {

        shiro.getSystemUser().execute(() -> {
            final ContentItem item = null;

            l10nManager.normalizeLanguages(item);
        });
    }

}
