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

import org.apache.shiro.authz.UnauthorizedException;
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
import org.junit.runner.RunWith;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;
import org.librecms.contenttypes.Article;
import org.librecms.contenttypes.News;
import org.librecms.contenttypes.Event;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;

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
@CleanupUsingScript(value = {"cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class ContentTypeRepositoryTest {

    @Inject
    private ContentTypeRepository contentTypeRepo;

    @Inject
    private ContentSectionRepository contentSectionRepo;

    @Inject
    private Shiro shiro;

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
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(org.libreccm.categorization.Categorization.class)
            .addClass(org.libreccm.categorization.Category.class)
            .addClass(org.libreccm.categorization.Domain.class)
            .addClass(org.libreccm.categorization.DomainOwnership.class)
            .addClass(org.libreccm.cdi.utils.CdiUtil.class)
            .addClass(org.libreccm.configuration.AbstractSetting.class)
            .addClass(org.libreccm.configuration.BigDecimalSetting.class)
            .addClass(org.libreccm.configuration.BooleanSetting.class)
            .addClass(org.libreccm.configuration.Configuration.class)
            .addClass(org.libreccm.configuration.ConfigurationManager.class)
            .addClass(org.libreccm.configuration.ConfigurationInfo.class)
            .addClass(org.libreccm.configuration.DoubleSetting.class)
            .addClass(org.libreccm.configuration.EnumSetting.class)
            .addClass(org.libreccm.configuration.LocalizedStringSetting.class)
            .addClass(org.libreccm.configuration.LongSetting.class)
            .addClass(org.libreccm.configuration.Setting.class)
            .addClass("org.libreccm.configuration.SettingConverter")
            .addClass(org.libreccm.configuration.SettingManager.class)
            .addClass(org.libreccm.configuration.SettingInfo.class)
            .addClass(org.libreccm.configuration.StringSetting.class)
            .addClass(org.libreccm.configuration.StringListSetting.class)
            .addClass(org.libreccm.core.AbstractEntityRepository.class)
            .addClass(org.libreccm.core.CcmObject.class)
            .addClass(org.libreccm.core.EmailAddress.class)
            .addClass(org.libreccm.core.Identifiable.class)
            .addClass(org.libreccm.core.Resource.class)
            .addClass(org.libreccm.core.ResourceType.class)
            .addClass(org.libreccm.jpa.EntityManagerProducer.class)
            .addClass(org.libreccm.l10n.LocalizedString.class)
            .addClass(org.libreccm.portation.Portable.class)
            .addClass(org.libreccm.security.AuthorizationRequired.class)
            .addClass(org.libreccm.security.CcmShiroRealm.class)
            .addClass(org.libreccm.security.Group.class)
            .addClass(org.libreccm.security.GroupMembership.class)
            .addClass(org.libreccm.security.Party.class)
            .addClass(org.libreccm.security.Permission.class)
            .addClass(org.libreccm.security.RecursivePermissions.class)
            .addClass(org.libreccm.security.Relation.class)
            .addClass(org.libreccm.security.RequiresPrivilege.class)
            .addClass(org.libreccm.security.Role.class)
            .addClass(org.libreccm.security.RoleMembership.class)
            .addClass(org.libreccm.security.Shiro.class)
            .addClass(org.libreccm.security.User.class)
            .addClass(org.libreccm.security.UserRepository.class)
            .addClass(org.libreccm.tests.categories.IntegrationTest.class)
            .addClass(org.libreccm.web.CcmApplication.class)
            .addClass(org.libreccm.workflow.AssignableTask.class)
            .addClass(org.libreccm.workflow.Task.class)
            .addClass(org.libreccm.workflow.TaskAssignment.class)
            .addClass(org.libreccm.workflow.TaskComment.class)
            .addClass(org.libreccm.workflow.TaskState.class)
            .addClass(org.libreccm.workflow.Workflow.class)
            .addClass(org.libreccm.workflow.WorkflowState.class)
            .addClass(org.libreccm.workflow.WorkflowTemplate.class)
            .addClass(org.librecms.CmsConstants.class)
            .addClass(org.librecms.contentsection.Asset.class)
            .addClass(org.librecms.contentsection.AttachmentList.class)
            .addClass(org.librecms.contentsection.ContentItem.class)
            .addClass(org.librecms.contentsection.ContentItemVersion.class)
            .addClass(org.librecms.contentsection.ContentSection.class)
            .addClass(org.librecms.contentsection.ContentSectionRepository.class)
            .addClass(org.librecms.contentsection.ContentType.class)
            .addClass(org.librecms.contentsection.ContentTypeMode.class)
            .addClass(org.librecms.contentsection.ContentTypeRepository.class)
            .addClass(org.librecms.contentsection.Folder.class)
            .addClass(org.librecms.contentsection.FolderType.class)
            .addClass(org.librecms.contentsection.ItemAttachment.class)
            .addClass(org.librecms.contenttypes.Article.class)
            .addClass(org.librecms.contenttypes.AuthoringKit.class)
            .addClass(org.librecms.contenttypes.AuthoringStep.class)
            .addClass(org.librecms.contenttypes.ContentTypeDescription.class)
            .addClass(org.librecms.contenttypes.Event.class)
            .addClass(org.librecms.contenttypes.News.class)
            .addClass(org.librecms.lifecycle.Lifecycle.class)
            .addClass(org.librecms.lifecycle.LifecycleDefinition.class)
            .addClass(org.librecms.lifecycle.Phase.class)
            .addClass(org.librecms.lifecycle.PhaseDefinition.class)
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
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
        assertThat(shiro, is(not(nullValue())));
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
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
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
        final Optional<ContentType> newsType = contentTypeRepo
            .findByContentSectionAndClass(section, News.class);
        assertThat(newsType.isPresent(), is(true));

        shiro.getSystemUser()
            .execute(() -> contentTypeRepo.delete(newsType.get()));
    }

//    ToDo, does not work at the moment because including AuthorizationInterceptor
//   pulls in to many other dependencies.
//    /**
//     * Verifies that an unused content type can be deleted.
//     */
//    @Test(expected = UnauthorizedException.class)
//    @InSequence(2000)
//    @UsingDataSet("datasets/org/librecms/contentsection/"
//                      + "ContentTypeRepositoryTest/data.xml")
//    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
//                            + "ContentTypeRepositoryTest/data.xml")
//    @ShouldThrowException(UnauthorizedException.class)
//    public void deleteUnusedContentTypeUnauthorized() {
//        
//        final ContentSection section = contentSectionRepo.findById(-1001L).get();
//        final Optional<ContentType> newsType = contentTypeRepo
//            .findByContentSectionAndClass(section, News.class);
//        assertThat(newsType.isPresent(), is(true));
//
//        contentTypeRepo.delete(newsType.get());
//
//    }

    /**
     * Verifies that content types which are in use can't be deleted.
     */
    @Test(expected = IllegalArgumentException.class)
    @InSequence(2200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentTypeRepositoryTest/data.xml")
    @ShouldMatchDataSet("datasets/org/librecms/contentsection/"
                            + "ContentTypeRepositoryTest/data.xml")
    @ShouldThrowException(IllegalArgumentException.class)
    public void deleteContentTypeInUse() {
        final ContentSection section = contentSectionRepo.findById(-1001L).get();
        final Optional<ContentType> articleType = contentTypeRepo
            .findByContentSectionAndClass(section, Article.class);
        assertThat(articleType.isPresent(), is(true));

        shiro.getSystemUser()
            .execute(() -> contentTypeRepo.delete(articleType.get()));
    }

}
