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
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.News;
import org.librecms.contenttypes.Event;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

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
@CreateSchema({"create_ccm_cms_schema.sql"})
public class ContentTypeRepositoryTest {

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private ContentSectionRepository contentSectionRepo;

    public ContentTypeRepositoryTest() {
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
                    "LibreCCM-org.librecms.contentsection.ContentTypeRepositoryTest.war")
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
            .addPackage(org.librecms.contenttypes.Article.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            //            .addAsLibraries(getModuleDependencies())
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    /**
     * Check if all injected beans are available.
     */
    @Test
    @InSequence(1)
    public void checkInjection() {
        assertThat(contentTypeRepo, is(not(nullValue())));
        assertThat(contentSectionRepo, is(not(nullValue())));
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSection(org.librecms.contentsection.ContentSection)}
     * returns all content types of the given content section.
     */
    @Test
    @InSequence(1100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    public void findByContentSection() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final List<ContentType> types = contentTypeRepo.findByContentSection(
            section);

        assertThat(types, is(not(nullValue())));
        assertThat(types.isEmpty(), is(false));
        assertThat(types.size(), is(2));
        
        assertThat(types.get(0).getContentItemClass(),
                   is(equalTo(Article.class.getName())));
        assertThat(types.get(0).getContentSection().getDisplayName(),
                   is(equalTo("info")));
        
        assertThat(types.get(1).getContentItemClass(),
                   is(equalTo(News.class.getName())));
        assertThat(types.get(1).getContentSection().getDisplayName(),
                   is(equalTo("info")));
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSection(org.librecms.contentsection.ContentSection)}
     * throws all {@link IllegalArgumentException} if called with {@code null}
     * for the content section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1110)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void findByContentSectionNull() {
        contentTypeRepo.findByContentSection(null);
    }

    /**
     * Checks if {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.Class)
     * returns the expected values.
     */
    @Test
    @InSequence(1200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    public void findByContentSectionAndClass() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Optional<ContentType> articleType = contentTypeRepo
            .findByContentSectionAndClass(section, Article.class);
        final Optional<ContentType> newsType = contentTypeRepo
            .findByContentSectionAndClass(section, News.class);
        final Optional<ContentType> eventType = contentTypeRepo
            .findByContentSectionAndClass(section, Event.class);

        assertThat(articleType.isPresent(), is(true));
        assertThat(articleType.get().getContentItemClass(),
                   is(equalTo(Article.class.getName())));
        assertThat(articleType.get().getContentSection().getDisplayName(),
                   is(equalTo("info")));

        assertThat(newsType.isPresent(), is(true));
        assertThat(newsType.get().getContentItemClass(),
                   is(equalTo(News.class.getName())));
        assertThat(newsType.get().getContentSection().getDisplayName(),
                   is(equalTo("info")));

        assertThat(eventType.isPresent(), is(false));
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.Class)}
     * throws a {@link IllegalArgumentException} when called with {@code null}
     * for the content section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1210)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void findByContentSectionNullAndClass() {

        contentTypeRepo.findByContentSectionAndClass(null, Article.class);
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.Class)}
     * throws a {@link IllegalArgumentException} when called with {@code null}
     * for the class.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1220)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void findByContentSectionAndClassNull() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Class<? extends ContentItem> type = null;
        contentTypeRepo.findByContentSectionAndClass(section, type);
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.String)}
     * returns the expected values.
     */
    @Test
    @InSequence(1300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    public void findByContentSectionAndClassName() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Optional<ContentType> articleType = contentTypeRepo
            .findByContentSectionAndClass(section, Article.class.getName());
        final Optional<ContentType> newsType = contentTypeRepo
            .findByContentSectionAndClass(section, News.class.getName());
        final Optional<ContentType> eventType = contentTypeRepo
            .findByContentSectionAndClass(section, Event.class.getName());

        assertThat(articleType.isPresent(), is(true));
        assertThat(articleType.get().getContentItemClass(),
                   is(equalTo(Article.class.getName())));
        assertThat(articleType.get().getContentSection().getDisplayName(),
                   is(equalTo("info")));

        assertThat(newsType.isPresent(), is(true));
        assertThat(newsType.get().getContentItemClass(),
                   is(equalTo(News.class.getName())));
        assertThat(newsType.get().getContentSection().getDisplayName(),
                   is(equalTo("info")));

        assertThat(eventType.isPresent(), is(false));

    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.String) }
     * throws a {@link IllegalArgumentException} when called with {@code null}
     * for the content section.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1210)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void findByContentSectionNullAndClassName() {

        contentTypeRepo.findByContentSectionAndClass(null, Article.class
                                                     .getName());
    }

    /**
     * Checks if
     * {@link ContentTypeRepository#findByContentSectionAndClass(org.librecms.contentsection.ContentSection, java.lang.String) }
     * throws a {@link IllegalArgumentException} when called with {@code null}
     * for the class.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(1220)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void findByContentSectionAndClassNameNull() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final String type = null;
        contentTypeRepo.findByContentSectionAndClass(section, type);
    }

    /**
     * Verifies the return value of
     * {@link ContentTypeRepository#isContentTypeInUse(org.librecms.contentsection.ContentType)}.
     */
    @Test
    @InSequence(2000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    public void verifyIsContentTypeInUse() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Optional<ContentType> articleType = contentTypeRepo
            .findByContentSectionAndClass(section, Article.class);
        final Optional<ContentType> newsType = contentTypeRepo
            .findByContentSectionAndClass(section, News.class);

        assertThat(articleType.isPresent(), is(true));
        assertThat(newsType.isPresent(), is(true));

        assertThat(contentTypeRepo.isContentTypeInUse(articleType.get()),
                   is(true));
        assertThat(contentTypeRepo.isContentTypeInUse(newsType.get()),
                   is(false));
    }

    /**
     * Verifies that an unused content type can be deleted.
     */
    @Test
    @InSequence(2000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentTypeRepositoryTest/after-delete.xml")
    public void deleteUnusedContentType() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Optional<ContentType> newsType = contentTypeRepo
            .findByContentSectionAndClass(section, News.class);
        assertThat(newsType.isPresent(), is(true));

        contentTypeRepo.delete(newsType.get());

    }

    /**
     * Verifies that content types which are in use can't be deleted.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2000)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void deleteContentTypeInUse() {
        final ContentSection section = contentSectionRepo.findById(-1001L);
        final Optional<ContentType> articleType = contentTypeRepo
            .findByContentSectionAndClass(section, Article.class);
        assertThat(articleType.isPresent(), is(true));

        contentTypeRepo.delete(articleType.get());
    }

}
