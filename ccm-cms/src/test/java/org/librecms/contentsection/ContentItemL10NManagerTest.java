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

import static org.libreccm.testutils.DependenciesHelpers.*;

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
import org.libreccm.tests.categories.IntegrationTest;

import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Verifies the methods of the {@link ContentItemL10NManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@org.junit.experimental.categories.Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_cms_schema.sql"})
public class ContentItemL10NManagerTest {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private ContentItemL10NManager l10nManager;

    public ContentItemL10NManagerTest() {
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
            .addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.Configuration.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmCore.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class
                .getPackage())
            .addPackage(org.libreccm.security.Permission.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.bebop.Component.class.getPackage())
            .addPackage(com.arsdigita.bebop.util.BebopConstants.class
                .getPackage())
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(com.arsdigita.runtime.CCMResourceManager.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.AbstractAppSettingsPane.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationInstanceForm.class)
            .addClass(
                com.arsdigita.ui.admin.applications.DefaultApplicationSettingsPane.class)
            .addClass(com.arsdigita.cms.dispatcher.ItemResolver.class)
            .addPackage(com.arsdigita.util.Lockable.class.getPackage())
            .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
            .addPackage(org.librecms.Cms.class.getPackage())
            .addPackage(org.librecms.assets.Asset.class.getPackage())
            .addPackage(org.librecms.attachments.AttachmentList.class
                .getPackage())
            .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
            .addPackage(org.librecms.contentsection.ContentSection.class
                .getPackage())
            .addPackage(org.librecms.contenttypes.Article.class.getPackage()).
            addClass(com.arsdigita.kernel.security.SecurityConfig.class)
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            //            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/librecms/contentsection/ContentItemManagerTest/log4j2.xml",
                "log4j2.xml")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        assertThat(l10nManager.hasLanguage(item.get(), Locale.ENGLISH),
                   is(true));
        assertThat(l10nManager.hasLanguage(item.get(), Locale.FRENCH),
                   is(true));
        assertThat(l10nManager.hasLanguage(item.get(), Locale.GERMAN),
                   is(false));
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        assertThat(l10nManager.hasLanguage(item.get(), null),
                   is(true));

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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.addLanguage(item.get(), Locale.GERMAN);
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.addLanguage(item.get(), Locale.FRENCH);

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
        final ContentItem item = null;

        l10nManager.addLanguage(item, Locale.GERMAN);
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.addLanguage(item.get(), null);
    }

    /**
     * Tries to remove language from a content item by using
     * {@link ContentItemL10NManager#removeLangauge(org.librecms.contentsection.ContentItem, java.util.Locale)}.
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.removeLangauge(item.get(), Locale.FRENCH);
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLangauge(org.librecms.contentsection.ContentItem, java.util.Locale)}
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.removeLangauge(item.get(), Locale.GERMAN);
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLangauge(org.librecms.contentsection.ContentItem, java.util.Locale)}
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
        final ContentItem item = null;

        l10nManager.removeLangauge(item, Locale.GERMAN);
    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#removeLangauge(org.librecms.contentsection.ContentItem, java.util.Locale)}
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.removeLangauge(item.get(), null);

    }

    /**
     * Tries to normalise the languages of a content item by using null null
     * null null null null null null null null null null     {@link ContentItemL10NManager#normalizedLanguages(org.librecms.contentsection.ContentItem) 
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
        final Optional<ContentItem> item = itemRepo.findById(-10200L);
        assertThat(item.isPresent(), is(true));

        l10nManager.normalizedLanguages(item.get());
    }

    /**
     * Verifies that calling
     * {@link ContentItemL10NManager#normalizedLanguages(org.librecms.contentsection.ContentItem)}
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
        final Optional<ContentItem> item = itemRepo.findById(-10100L);
        assertThat(item.isPresent(), is(true));

        l10nManager.normalizedLanguages(item.get());

    }

    /**
     * Verifies that
     * {@link ContentItemL10NManager#normalizedLanguages(org.librecms.contentsection.ContentItem)}
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
        final ContentItem item = null;

        l10nManager.normalizedLanguages(item);
    }

}
