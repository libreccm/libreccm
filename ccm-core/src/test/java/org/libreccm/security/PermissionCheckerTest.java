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

import static org.libreccm.core.CoreConstants.*;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.tests.categories.IntegrationTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"create_ccm_core_schema.sql"})
@CleanupUsingScript({"cleanup.sql"})
public class PermissionCheckerTest {
    
    @Inject
    private Subject subject;
    
    @Inject
    private Shiro shiro;
    
    @Inject
    private PermissionChecker permissionChecker;
    
    @Inject
    private CcmObjectRepository objectRepository;
    
    public PermissionCheckerTest() {
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
                    "LibreCCM-org.libreccm.security.PermissionCheckerTest.war")
            .addPackage(org.libreccm.categorization.Categorization.class
                .getPackage())
            .addPackage(org.libreccm.cdi.utils.CdiUtil.class.getPackage())
            .addPackage(org.libreccm.configuration.ConfigurationManager.class
                .getPackage())
            .addPackage(org.libreccm.core.CcmObject.class.getPackage())
            .addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage())
            .addPackage(org.libreccm.security.User.class.getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.getPackage())
            .addPackage(org.libreccm.web.CcmApplication.class.getPackage())
            .addPackage(org.libreccm.workflow.Workflow.class.getPackage())
            .addPackage(com.arsdigita.kernel.KernelConfig.class
                .getPackage())
            .addPackage(com.arsdigita.kernel.security.SecurityConfig.class
                .getPackage())
            .addPackage(com.arsdigita.util.UncheckedWrapperException.class
                .getPackage())
            .addClass(org.libreccm.portation.Portable.class)
            .addAsLibraries(getModuleDependencies())
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource(
                "configs/org/libreccm/security/ShiroTest/log4j2.xml",
                "log4j2.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(1100)
    public void isPermittedAuthenticatedUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);
        
        assertThat(permissionChecker.isPermitted("privilege1"), is(false));
        assertThat(permissionChecker.isPermitted("privilege2"), is(false));
        assertThat(permissionChecker.isPermitted("privilege3"), is(false));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(1200)
    public void isPermittedUnAuthenticatedUser() {
        assertThat(permissionChecker.isPermitted("privilege1"), is(false));
        assertThat(permissionChecker.isPermitted("privilege2"), is(false));
        assertThat(permissionChecker.isPermitted("privilege3"), is(false));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(1300)
    public void isPermittedSystemUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        final CcmObject object3 = objectRepository.findById(-20003L).get();
        
        shiro.getSystemUser().execute(new Callable<Boolean>() {
            
            @Override
            public Boolean call() {
                assertThat(permissionChecker.isPermitted("privilege1"),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege2"),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege3"),
                           is(true));
                
                assertThat(permissionChecker.isPermitted("privilege1",
                                                         object2),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege2",
                                                         object1),
                           is(true));
                assertThat(permissionChecker.isPermitted("privilege3",
                                                         object3),
                           is(true));
                
                return false;
            }
            
        });
        
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(1400)
    public void isPermittedObjectAuthenticatedUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);
        
        assertThat(permissionChecker.isPermitted("privilege1", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object2),
                   is(true));
        
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(1500)
    public void isPermittedObjectUnAuthenticatedUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        assertThat(permissionChecker.isPermitted("privilege1", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object1),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege2", object2),
                   is(false));
        assertThat(permissionChecker.isPermitted("privilege3", object1),
                   is(true));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(2100)
    public void checkPermissionAuthenticatedUser() {
        final UsernamePasswordToken token = new UsernamePasswordToken("mmuster",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);
        
        permissionChecker.checkPermission("privilege1");
    }
    
    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(2200)
    public void checkPermissionUnAuthenticatedUser() {
        permissionChecker.checkPermission("privilege1");
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(2300)
    public void checkPermissionObjectAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);
        
        permissionChecker.checkPermission("privilege2", object2);
    }
    
    @Test(expected = AuthorizationException.class)
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @ShouldThrowException(AuthorizationException.class)
    @InSequence(2400)
    public void checkPermissionObjectUnAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        permissionChecker.checkPermission("privilege2", object2);
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(2500)
    public void checkPermissionObjectPublicUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        
        permissionChecker.checkPermission("privilege3", object1);
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(2600)
    public void checkPermissionObjectSystemUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        final CcmObject object3 = objectRepository.findById(-20003L).get();
        
        shiro.getSystemUser().execute(new Callable<Boolean>() {
            
            @Override
            public Boolean call() {
                permissionChecker.checkPermission("privilege1");
                permissionChecker.checkPermission("privilege2");
                permissionChecker.checkPermission("privilege3");
                
                permissionChecker.checkPermission("privilege1", object3);
                permissionChecker.checkPermission("privilege2", object1);
                permissionChecker.checkPermission("privilege3", object2);
                
                return false;
            }
            
        });
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(3100)
    public void checkPermissionReturnObjectAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        final UsernamePasswordToken token = new UsernamePasswordToken("jdoe",
                                                                      "foo123");
        token.setRememberMe(true);
        subject.login(token);
        
        final CcmObject result = permissionChecker.checkPermission(
            "privilege2", object2, CcmObject.class);
        assertThat(result.getDisplayName(), is(equalTo("object2")));
        assertThat(permissionChecker.isAccessDeniedObject(result), is(false));
        assertThat(result, is(equalTo(object2)));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(3200)
    public void checkPermissionReturnObjectUnAuthenticatedUser() {
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        
        final CcmObject result = permissionChecker.checkPermission(
            "privilege2", object2, CcmObject.class);
        assertThat(result.getDisplayName(), is(equalTo(ACCESS_DENIED)));
        assertThat(permissionChecker.isAccessDeniedObject(result), is(true));
        assertThat(result, is(not(equalTo(object2))));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(600)
    public void checkPermissionReturnObjectPublicUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        
        final CcmObject result = permissionChecker.checkPermission(
            "privilege3", object1, CcmObject.class);
        assertThat(result.getDisplayName(), is(equalTo("object1")));
        assertThat(permissionChecker.isAccessDeniedObject(result), is(false));
        assertThat(result, is(equalTo(object1)));
    }
    
    @Test
    @UsingDataSet("datasets/org/libreccm/security/ShiroTest/data.yml")
    @InSequence(3200)
    public void checkPermissionReturnObjectSystemUser() {
        final CcmObject object1 = objectRepository.findById(-20001L).get();
        final CcmObject object2 = objectRepository.findById(-20002L).get();
        final CcmObject object3 = objectRepository.findById(-20003L).get();
        
        final List<CcmObject> results = shiro.getSystemUser().execute(
            new Callable<List<CcmObject>>() {
            
            @Override
            public List<CcmObject> call() {
                permissionChecker.checkPermission("privilege1");
                permissionChecker.checkPermission("privilege2");
                permissionChecker.checkPermission("privilege3");
                
                final CcmObject result3 = permissionChecker.checkPermission(
                    "privilege1", object3, CcmObject.class);
                final CcmObject result1 = permissionChecker.checkPermission(
                    "privilege2", object1, CcmObject.class);
                final CcmObject result2 = permissionChecker.checkPermission(
                    "privilege3", object2, CcmObject.class);
                
                final List<CcmObject> results = new ArrayList<>();
                results.add(result1);
                results.add(result2);
                results.add(result3);
                
                return results;
            }
            
        });
        
        final CcmObject result1 = results.get(0);
        final CcmObject result2 = results.get(1);
        final CcmObject result3 = results.get(2);
        
        assertThat(result1.getDisplayName(), is(equalTo("object1")));
        assertThat(permissionChecker.isAccessDeniedObject(result1), is(false));
        assertThat(result1, is(equalTo(object1)));
        
        assertThat(result2.getDisplayName(), is(equalTo("object2")));
        assertThat(permissionChecker.isAccessDeniedObject(result2), is(false));
        assertThat(result2, is(equalTo(object2)));
        
        assertThat(result3.getDisplayName(), is(equalTo("object3")));
        assertThat(permissionChecker.isAccessDeniedObject(result3), is(false));
        assertThat(result3, is(equalTo(object3)));
    }
    
}
