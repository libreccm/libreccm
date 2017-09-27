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

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.TestExecutionPhase;
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
import org.libreccm.security.Role;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
public class ContentItemPermissionTest {

    private static final String QUERY = "SELECT DISTINCT i FROM ContentItem i "
                                            + "JOIN i.permissions p "
                                            + "WHERE (p.grantee IN :roles "
                                            + "AND p.grantedPrivilege = 'view_draft_items')"
                                        + "OR true = :isSystemUser "
                                            + "ORDER BY i.displayName";

    @Inject
    private EntityManager entityManager;

    @Inject
    private Shiro shiro;

    @Inject
    private Subject subject;

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
                    "LibreCCM-org.librecms.contentsection.ContentItemPermissionTest.war") //Classes imported by this class
            .
            addClass(Role.class)
            .addClass(Shiro.class)
            .addClass(User.class)
            .addClass(ContentItem.class)
            .addClass(IntegrationTest.class)
            //Classes used by Role.class
            .addClass(org.libreccm.l10n.LocalizedString.class)
            .addClass(org.libreccm.security.Permission.class)
            .addClass(org.libreccm.security.RoleMembership.class)
            .addClass(org.libreccm.workflow.TaskAssignment.class)
            //Classes used by Role.class, User.class, 
            //org.libreccm.core.CcmObject.class
            .addClass(org.libreccm.portation.Portable.class)
            //Classes used by Shiro.class
            .addClass(org.libreccm.security.UserRepository.class)
            //Class used by User.class
            .addClass(org.libreccm.core.EmailAddress.class)
            .addClass(org.libreccm.security.GroupMembership.class)
            .addClass(org.libreccm.security.Party.class)
            //Classes required by org.libreccm.security.Permission.class
            .addClass(org.libreccm.core.CcmObject.class)
            //Classes required by org.libreccm.workflow.TaskAssignment.class
            .addClass(org.libreccm.workflow.AssignableTask.class)
            //Classes required by org.libreccm.security.UserRepository
            .addClass(org.libreccm.core.AbstractEntityRepository.class)
            .addClass(org.libreccm.security.AuthorizationRequired.class)
            .addClass(org.libreccm.security.RequiresPrivilege.class)
            //Classes required by org.libreccm.secuirty.GroupMembership
            .addClass(org.libreccm.security.Group.class)
            //Classes required by org.libreccm.core.CcmObject
            .addClass(org.libreccm.categorization.Categorization.class)
            .addClass(org.libreccm.core.Identifiable.class)
            //Classes required by org.libreccm.workflow.AssignableTask
            .addClass(org.libreccm.workflow.Task.class)
            //Classes required by org.libreccm.categorization.Categorization.class
            .addClass(org.libreccm.categorization.Category.class)
            .addClass(org.libreccm.security.Relation.class)
            //Classes required by org.libreccm.workflow.Task.class
            .addClass(org.libreccm.workflow.TaskComment.class)
            .addClass(org.libreccm.workflow.TaskState.class)
            .addClass(org.libreccm.workflow.Workflow.class)
            .addClass(org.libreccm.workflow.AbstractWorkflowException.class)
            //Classes required by org.libreccm.categorization.Category
            .addClass(org.libreccm.security.RecursivePermissions.class)
            //Classes required by org.libreccm.workflow.Workflow.class
            .addClass(org.libreccm.workflow.WorkflowState.class)
            //Classes required by org.librecms.contentsection.ContentItem
            .addClass(org.librecms.contentsection.AttachmentList.class)
            .addClass(org.librecms.contentsection.ContentItemVersion.class)
            .addClass(org.librecms.contentsection.ContentType.class)
            .addClass(org.librecms.lifecycle.Lifecycle.class)
            //Classes required by org.librecms.contentsection.AttachmentList
            .addClass(org.librecms.contentsection.ItemAttachment.class)
            //Classes required by org.librecms.contentsection.ContentType
            .addClass(org.librecms.contentsection.ContentSection.class)
            .addClass(org.librecms.contentsection.ContentTypeMode.class)
            .addClass(org.librecms.lifecycle.LifecycleDefinition.class)
            //Classes required by org.librecms.lifecycle.Lifecycle
            .addClass(org.librecms.lifecycle.Phase.class)
            //Classes required by org.librecms.contentsection.ItemAttachment
            .addClass(org.librecms.contentsection.Asset.class)
            //Classes required by org.librecms.contentsection.ContentSection
            .addClass(org.libreccm.web.CcmApplication.class)
            .addClass(org.librecms.contentsection.Folder.class)
            //Classes required by org.librecms.lifecycle.LifecycleDefinition
            .addClass(org.librecms.lifecycle.PhaseDefinition.class)
            //Classes required by org.librecms.contentsection.Asset
            .addClass(org.librecms.CmsConstants.class)
            //Classes required by org.librecms.contentsection.Folder
            .addClass(org.librecms.contentsection.FolderType.class)
            //Classes required by org.libreccm.web.CcmApplication
            .addClass(org.libreccm.categorization.DomainOwnership.class)
            .addClass(org.libreccm.core.Resource.class)
            //Classes required by org.libreccm.categorization.DomainOwnership
            .addClass(org.libreccm.categorization.Domain.class)
            //Classes required by org.libreccm.core.Resource
            .addClass(org.libreccm.core.ResourceType.class)
            //Required for CDI injection of EntityManager
            .addClass(org.libreccm.jpa.EntityManagerProducer.class)
            .addClass(org.libreccm.jpa.AuditReaderProducer.class)
            //Required for Authentication and Authorization
            .addClass(org.libreccm.security.CcmShiroRealm.class)
            //Required by org.libreccm.security.CcmShiroRealm
            .addClass(org.libreccm.cdi.utils.CdiUtil.class)
            .addClass("org.libreccm.security.CcmShiroRealmController")
            .addClass(org.libreccm.core.CcmObject.class)
            //Required by org.libreccm.security.CcmShiroRealmController
            .addClass(com.arsdigita.kernel.KernelConfig.class)
            .addClass(org.libreccm.security.GroupRepository.class)
            .addClass(org.libreccm.security.PermissionManager.class)
            .addClass(org.libreccm.security.RoleRepository.class)
            .addClass(org.libreccm.security.UserRepository.class)
            .addClass(org.libreccm.security.Shiro.class)
            .addClass(org.libreccm.core.UnexpectedErrorException.class)
            //Required by org.libreccm.kernel.KernelConfig
            .addClass(org.libreccm.configuration.Configuration.class)
            .addClass(org.libreccm.configuration.ConfigurationManager.class)
            .addClass(org.libreccm.configuration.Setting.class)
            //Required by org.libreccm.configuration.ConfigurationManager
            .addClass(org.libreccm.configuration.AbstractSetting.class)
            .addClass(org.libreccm.configuration.ConfigurationInfo.class)
            .addClass("org.libreccm.configuration.SettingConverter")
            .addClass(org.libreccm.configuration.SettingInfo.class)
            .addClass(org.libreccm.configuration.SettingManager.class)
            .addClass(org.libreccm.modules.CcmModule.class)
            //Required by org.libreccm.configuration.SettingConverter.class
            .addClass(org.libreccm.configuration.BigDecimalSetting.class)
            .addClass(org.libreccm.configuration.BooleanSetting.class)
            .addClass(org.libreccm.configuration.DoubleSetting.class)
            .addClass(org.libreccm.configuration.EnumSetting.class)
            .addClass(org.libreccm.configuration.LocalizedStringSetting.class)
            .addClass(org.libreccm.configuration.LongSetting.class)
            .addClass(org.libreccm.configuration.StringListSetting.class)
            .addClass(org.libreccm.configuration.StringSetting.class)
            //Required by org.libreccm.modules.CcmModule
            .addClass(org.libreccm.modules.ModuleEvent.class)
            .addClass(org.libreccm.modules.InitEvent.class)
            .addClass(org.libreccm.modules.InstallEvent.class)
            .addClass(org.libreccm.modules.ShutdownEvent.class)
            .addClass(org.libreccm.modules.UnInstallEvent.class)
            //Required by org.libreccm.security.PermissionManager
            .addClass(org.libreccm.core.CcmObjectRepository.class)
            //Required by org.libreccm.core.CcmObjectRepository
            .addClass(org.libreccm.core.CoreConstants.class)
            //Dependencies from other modules and resources
            .addAsLibraries(getCcmCoreDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @InSequence(10)
    public void checkInjections() {
        assertThat(entityManager, is(not(nullValue())));
        assertThat(shiro, is(not(nullValue())));
    }

    /**
     * Verify that Shiro is working.
     */
    @Test
    @InSequence(20)
    public void checkShiro() {
        assertThat(shiro.getSecurityManager(), is(not(nullValue())));
        assertThat(shiro.getSystemUser(), is(not(nullValue())));
    }

    @Test
    @InSequence(100)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemPermissionTest/data.xml")
    public void accessByNoUser() {
        final List<Role> roles;
        final Optional<User> user = shiro.getUser();
        if (user.isPresent()) {
            roles = user.get().getRoleMemberships().stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList());
        } else {
            roles = Collections.emptyList();
        }

        final TypedQuery<ContentItem> query = entityManager.createQuery(
            QUERY, ContentItem.class);
        query.setParameter("roles", roles);
        query.setParameter("isSystemUser", shiro.isSystemUser());
        final List<ContentItem> result = query.getResultList();

        assertThat(result.isEmpty(), is(true));
    }

    @Test
    @InSequence(200)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemPermissionTest/data.xml")
    public void accessByUser1() {

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "user1@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<Role> roles = shiro.getUser().get().getRoleMemberships()
            .stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
            QUERY, ContentItem.class);
        query.setParameter("roles", roles);
        query.setParameter("isSystemUser", shiro.isSystemUser());
        final List<ContentItem> result = query.getResultList();

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(result.get(1).getDisplayName(), is(equalTo("news1")));
    }

    @Test
    @InSequence(300)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemPermissionTest/data.xml")
    public void accessByUser2() {

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "user2@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<Role> roles = shiro.getUser().get().getRoleMemberships()
            .stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
            QUERY, ContentItem.class);
        query.setParameter("roles", roles);
        query.setParameter("isSystemUser", shiro.isSystemUser());
        final List<ContentItem> result = query.getResultList();

        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDisplayName(), is(equalTo("article2")));
    }

    @Test
    @InSequence(400)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemPermissionTest/data.xml")
    public void accessByUser3() {

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "user3@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<Role> roles = shiro.getUser().get().getRoleMemberships()
            .stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
            QUERY, ContentItem.class);
        query.setParameter("roles", roles);
        query.setParameter("isSystemUser", shiro.isSystemUser());
        final List<ContentItem> result = query.getResultList();

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(result.get(1).getDisplayName(), is(equalTo("article2")));
        assertThat(result.get(2).getDisplayName(), is(equalTo("article3")));
    }

    @Test
    @InSequence(500)
    @UsingDataSet("datasets/org/librecms/contentsection/"
                      + "ContentItemPermissionTest/data.xml")
    public void accessBySystemUser() {

        final UsernamePasswordToken token = new UsernamePasswordToken(
            "user3@example.org", "foo123");
        token.setRememberMe(true);
        subject.login(token);

        final List<ContentItem> result = shiro
            .getSystemUser()
            .execute(new ItemRetriever());

        assertThat(result.size(), is(4));
        assertThat(result.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(result.get(1).getDisplayName(), is(equalTo("article2")));
        assertThat(result.get(2).getDisplayName(), is(equalTo("article3")));
        assertThat(result.get(3).getDisplayName(), is(equalTo("news1")));
    }

    private class ItemRetriever implements Callable<List<ContentItem>> {

        @Override
        public List<ContentItem> call() throws Exception {

            final Optional<User> user = shiro.getUser();
            final List<Role> roles;
            if (user.isPresent()) {
                roles = shiro.getUser().get().getRoleMemberships()
                    .stream()
                    .map(membership -> membership.getRole())
                    .collect(Collectors.toList());
            } else {
                roles = Collections.emptyList();
            }

            final TypedQuery<ContentItem> query = entityManager.createQuery(
                QUERY, ContentItem.class);
            query.setParameter("roles", roles);
            query.setParameter("isSystemUser", shiro.isSystemUser());
            return query.getResultList();
        }

    }

}
