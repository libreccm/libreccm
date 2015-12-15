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
import com.arsdigita.util.parameter.AbstractParameterContext;
import com.arsdigita.web.CCMApplicationContextListener;
import com.arsdigita.xml.XML;
import com.arsdigita.xml.formatters.DateTimeFormatter;

import java.io.File;

import javax.inject.Inject;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.hibernate.exception.ConstraintViolationException;

import static org.hamcrest.Matchers.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.CreateSchema;
import org.jboss.arquillian.persistence.PersistenceTest;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.test.spi.ArquillianProxyException;
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
public class UserManagerTest {
    
    @Inject
    private UserManager userManager;
    
    @Inject
    private UserRepository userRepository;
    
    public UserManagerTest() {
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
                    "LibreCCM-org.libreccm.security.UserManagerTest.war")
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
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/libreccm/security/UserManagerTest/ccm-core.config",
                "ccm-core.config")
            //                .addAsWebInfResource(
            //                        "datasets/org/libreccm//security/UserManagerTest/"
            //                                + "security.properties",
            //                        "conf/registry/ccm-core/security.properties")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Test
    @InSequence(100)
    public void userManagerIsInjected() {
        assertThat(userManager, is(not(nullValue())));
    }
    
    @Test
    @InSequence(110)
    public void userRepositoryIsInjected() {
        assertThat(userRepository, is(not(nullValue())));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @InSequence(200)
    public void verifyPassword() {
        final User jdoe = userRepository.findByName("jdoe");
        final User mmuster = userRepository.findByName("mmuster");
        final User joe = userRepository.findByName("joe");
        
        assertThat(userManager.verifyPassword(jdoe, "foo123"), is(true));
        assertThat(userManager.verifyPassword(mmuster, "foo123"), is(true));
        assertThat(userManager.verifyPassword(joe, "foo123"), is(true));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldMatchDataSet(
        value = "datasets/org/libreccm/security/UserManagerTest/"
                    + "after-create-user.yml",
        excludeColumns = {"party_id", "password"})
    @InSequence(300)
    public void createUser() {
        userManager.createUser("Jane",
                               "Doe",
                               "jane",
                               "jane.doe@example.org",
                               "foo456");
        
        final User jane2 = userRepository.findByName("jane");
        assertThat(userManager.verifyPassword(jane2, "foo456"), is(true));
    }
    
    @Test(expected = ArquillianProxyException.class)
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldThrowException(ConstraintViolationException.class)
    @InSequence(400)
    public void createUserWithInValidName() {
        userManager.createUser("Jane",
                               "Doe",
                               "j#ne",
                               "jane.doe@example.org",
                               "foo456");
        fail();
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @InSequence(500)
    public void updatePassword() {
        final User jdoe = userRepository.findByName("jdoe");
        userManager.updatePassword(jdoe, "foo456");
        
        final User jdoe2 = userRepository.findByName("jdoe");
        assertThat(userManager.verifyPassword(jdoe, "foo456"), is(true));
        assertThat(userManager.verifyPassword(jdoe2, "foo456"), is(true));
        
        assertThat(userManager.verifyPassword(jdoe, "foo123"), is(false));
        assertThat(userManager.verifyPassword(jdoe2, "foo123"), is(false));
    }
    
    @Test(expected = ArquillianProxyException.class)
    @UsingDataSet("datasets/org/libreccm/security/UserManagerTest/data.yml")
    @ShouldThrowException(ConstraintViolationException.class)
    @InSequence(600)
    public void updatePasswordNullUser() {
        userManager.updatePassword(null, "foo");
        fail();
    }
    
}
