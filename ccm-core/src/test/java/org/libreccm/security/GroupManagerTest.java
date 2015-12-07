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

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateTimeFormatter;

import java.io.File;

import javax.inject.Inject;

import nl.jqno.equalsverifier.EqualsVerifier;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.jpa.EntityManagerProducer;
import org.libreccm.jpa.utils.MimeTypeConverter;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.tests.categories.IntegrationTest;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
public class GroupManagerTest {

    @Inject
    private GroupManager groupManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private UserRepository userRepository;

    public GroupManagerTest() {
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
        final PomEquippedResolveStage dependencies = pom.
            importCompileAndRuntimeDependencies();
        final File[] libs = dependencies.resolve().withTransitivity().asFile();

        for (File lib : libs) {
            System.err.printf("Adding file '%s' to test archive...%n",
                              lib.getName());
        }

        return ShrinkWrap
            .create(WebArchive.class,
                    "LibreCCM-org.libreccm.security.GroupManagerTest.war")
            .addPackage(User.class.getPackage())
            .addPackage(CcmObject.class.getPackage())
            .addPackage(Categorization.class.getPackage())
            .addPackage(LocalizedString.class.getPackage())
            .addPackage(CcmApplication.class.getPackage())
            .addPackage(Workflow.class.getPackage())
            .addPackage(EntityManagerProducer.class.getPackage())
            .addPackage(MimeTypeConverter.class.getPackage())
            .addPackage(EqualsVerifier.class.getPackage())
            .addPackage(IntegrationTest.class.getPackage())
            .addPackage(KernelConfig.class.getPackage())
            .addPackage(SecurityConfig.class.getPackage())
            .addPackage(AbstractConfig.class.getPackage())
            .addPackage(AbstractParameterContext.class.getPackage())
            .addPackage(UncheckedWrapperException.class.getPackage())
            .addPackage(CCMApplicationContextListener.class.getPackage())
            .addPackage(XML.class.getPackage())
            .addPackage(DateTimeFormatter.class.getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("com/arsdigita/kernel/security/"
                               + "SecurityConfig_parameter.properties",
                           "com/arsdigita/kernel/security/"
                               + "SecurityConfig_parameter.properties")
            .addAsWebInfResource(
                "configs/org/libreccm/security/UserManagerTest/"
                    + "registry.properties",
                "conf/registry/registry.properties")
            .addAsResource(
                "configs/org/libreccm/security/UserManagerTest/ccm-core.config",
                "ccm-core.config")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    @InSequence(100)
    public void groupManagerIsInjected() {
        assertThat(groupManager, is(not(nullValue())));
    }

    @Test
    @InSequence(110)
    public void groupRepositoryIsInjected() {
        assertThat(groupRepository, is(not(nullValue())));
    }

    @Test
    @InSequence(120)
    public void userRepositoryIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/after-add.yml",
        excludeColumns = {"membership_id"})
    @InSequence(200)
    public void addUserToGroup() {
        final Group admins = groupRepository.findByName("admins");
        final Group editors = groupRepository.findByName("editors");

        final User jdoe = userRepository.findByName("jdoe");
        final User mmuster = userRepository.findByName("mmuster");

        groupManager.addMemberToGroup(mmuster, admins);
        groupManager.addMemberToGroup(jdoe, editors);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(210)
    public void addNullUserToGroup() {
        final Group admins = groupRepository.findByName("admins");

        groupManager.addMemberToGroup(null, admins);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(220)
    public void addUserToGroupNull() {
        final User jdoe = userRepository.findByName("jdoe");

        groupManager.addMemberToGroup(jdoe, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @InSequence(230)
    public void addUserToGroupAgain() {
        final Group admins = groupRepository.findByName("admins");
        final User jdoe = userRepository.findByName("jdoe");

        groupManager.addMemberToGroup(jdoe, admins);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet("datasets/org/libreccm/security/GroupManagerTest/"
                            + "after-remove.yml")
    @InSequence(300)
    public void removeUserFromGroup() {
        final Group admins = groupRepository.findByName("admins");
        final Group users = groupRepository.findByName("users");

        final User jdoe = userRepository.findByName("jdoe");
        final User mmuster = userRepository.findByName("mmuster");

        assertThat(admins.getMemberships().size(), is(1));
        assertThat(users.getMemberships().size(), is(2));

        groupManager.removeMemberFromGroup(jdoe, admins);
        groupManager.removeMemberFromGroup(mmuster, users);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(310)
    public void removeUserNullFromGroup() {
        final Group admins = groupRepository.findByName("admins");
        
        groupManager.removeMemberFromGroup(null, admins);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(320)
    public void removeUserFromGroupNull() {
        final User jdoe = userRepository.findByName("jdoe");
        
        groupManager.removeMemberFromGroup(jdoe, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/GroupManagerTest/data.yml")
    @InSequence(330)
    public void removeUserGroupNotAMember() {
        final Group admins = groupRepository.findByName("admins");
        final User mmuster = userRepository.findByName("mmuster");
        
        groupManager.removeMemberFromGroup(mmuster, admins);
    }
}
