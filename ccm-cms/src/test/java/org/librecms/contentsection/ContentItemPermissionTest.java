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
import org.libreccm.security.Role;
import org.libreccm.security.Shiro;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.jboss.arquillian.persistence.CleanupUsingScript;

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
@CleanupUsingScript({"cleanup.sql"})
public class ContentItemPermissionTest {

    private static final String QUERY = "SELECT i FROM ContentItem i "
                                                + "JOIN i.permissions p "
                                                + "WHERE p.grantee IN :roles "
                                                + "AND p.grantedPrivilege = 'view_draft_items' "
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
                        "LibreCCM-org.librecms.contentsection.ContentItemPermissionTest.war").
                addPackage(org.libreccm.auditing.CcmRevision.class.getPackage())
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
                .addClass(com.arsdigita.dispatcher.RequestContext.class)
                .addClass(com.arsdigita.dispatcher.AccessDeniedException.class)
                .addClass(
                        com.arsdigita.cms.dispatcher.ContentItemDispatcher.class).
                addClass(com.arsdigita.dispatcher.Dispatcher.class)
                .addClass(
                        com.arsdigita.ui.admin.applications.AbstractAppInstanceForm.class).
                addClass(
                        com.arsdigita.ui.admin.applications.AbstractAppSettingsPane.class).
                addClass(
                        com.arsdigita.ui.admin.applications.DefaultApplicationInstanceForm.class).
                addClass(
                        com.arsdigita.ui.admin.applications.DefaultApplicationSettingsPane.class)
                .addClass(org.librecms.dispatcher.ItemResolver.class)
                .addClass(org.libreccm.portation.Portable.class)
                .addPackage(com.arsdigita.util.Lockable.class.getPackage())
                .addPackage(com.arsdigita.web.BaseServlet.class.getPackage())
                .addPackage(org.librecms.Cms.class.getPackage())
                .addPackage(org.librecms.contentsection.Asset.class.getPackage()).
                addPackage(org.librecms.contentsection.AttachmentList.class
                        .getPackage())
                .addPackage(org.librecms.lifecycle.Lifecycle.class.getPackage())
                .addPackage(org.librecms.contentsection.ContentSection.class
                        .getPackage())
                .addPackage(org.librecms.contenttypes.Article.class.getPackage()).
                addPackage(org.libreccm.tests.categories.IntegrationTest.class
                        .getPackage())
                //            .addAsLibraries(getModuleDependencies())
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
        if (shiro.getUser() == null) {
            roles = new ArrayList<>();
        } else {
            roles = shiro.getUser().getRoleMemberships().stream()
                    .map(membership -> membership.getRole())
                    .collect(Collectors.toList());
        }

        final TypedQuery<ContentItem> query = entityManager.createQuery(
                QUERY, ContentItem.class);
        query.setParameter("roles", roles);
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

        final List<Role> roles = shiro.getUser().getRoleMemberships().stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
                QUERY, ContentItem.class);
        query.setParameter("roles", roles);
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

        final List<Role> roles = shiro.getUser().getRoleMemberships().stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
                QUERY, ContentItem.class);
        query.setParameter("roles", roles);
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

        final List<Role> roles = shiro.getUser().getRoleMemberships().stream()
                .map(membership -> membership.getRole())
                .collect(Collectors.toList());

        final TypedQuery<ContentItem> query = entityManager.createQuery(
                QUERY, ContentItem.class);
        query.setParameter("roles", roles);
        final List<ContentItem> result = query.getResultList();

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getDisplayName(), is(equalTo("article1")));
        assertThat(result.get(1).getDisplayName(), is(equalTo("article2")));
        assertThat(result.get(2).getDisplayName(), is(equalTo("article3")));
    }

}
