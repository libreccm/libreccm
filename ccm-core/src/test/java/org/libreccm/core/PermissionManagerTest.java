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
package org.libreccm.core;

import static org.hamcrest.Matchers.*;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
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
import org.libreccm.tests.categories.IntegrationTest;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class PermissionManagerTest {

    private static final String TEST_OBJECT_1 = "Test Object 1";
    private static final String TEST_OBJECT_2 = "Test Object 2";
    private static final String TEST_OBJECT_3 = "Test Object 3";
    private static final String TEST_OBJECT_4 = "Test Object 4";
    private static final String TEST_OBJECT_5 = "Test Object 5";
    private static final String TEST_OBJECT_6 = "Test Object 6";
    private static final String TEST_OBJECT_7 = "Test Object 7";
    private static final String TEST_OBJECT_8 = "Test Object 8";

    private static final String ADMIN = "admin";
    private static final String READ = "read";
    private static final String WRITE = "write";

    @Inject
    private transient PermissionManager permissionManager;

    @Inject
    private transient PrivilegeRepository privilegeRepository;

    @Inject
    private transient CcmObjectRepository ccmObjectRepository;

    @Inject
    private transient UserRepository userRepository;

    @Inject
    private transient GroupRepository groupRepository;

    public PermissionManagerTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
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
                    String.format("LibreCCM-%s.war",
                                  PermissionManagerTest.class.getName()))
            .addPackage(User.class.getPackage())
            .addPackage(org.libreccm.web.Application.class.getPackage())
            .addPackage(org.libreccm.categorization.Category.class.
                getPackage())
            .addPackage(org.libreccm.l10n.LocalizedString.class.getPackage()).
            addPackage(org.libreccm.jpa.EntityManagerProducer.class
                .getPackage())
            .addPackage(org.libreccm.jpa.utils.MimeTypeConverter.class
                .getPackage())
            .addPackage(org.libreccm.testutils.EqualsVerifier.class.
                getPackage())
            .addPackage(org.libreccm.tests.categories.IntegrationTest.class
                .getPackage())
            .addAsLibraries(libs)
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "WEB-INF/web.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "WEB-INF/beans.xml");
    }

    private Map<String, CcmObject> retrieveTestObjects() {
        final long[] objectIds = {-10, -20, -30, -40, -50, -60, -70, -80};

        final Map<String, CcmObject> objects = new LinkedHashMap<>();

        for (final long objectId : objectIds) {
            final CcmObject object = ccmObjectRepository.findById(objectId);
            objects.put(object.getDisplayName(), object);
        }

        return objects;
    }

    private Map<String, Privilege> retrievePrivileges() {
        final String[] privilegLabels = {"admin", "read", "write"};

        final Map<String, Privilege> privileges = new LinkedHashMap<>();

        for (final String label : privilegLabels) {
            final Privilege privilege = privilegeRepository.retrievePrivilege(
                label);
            privileges.put(label, privilege);
        }

        return privileges;
    }

    private void verifyIsPermitted(final Subject subject,
                                   final Privilege privilege,
                                   final Map<CcmObject, Boolean> expected) {
        final String subjectName;
        if (subject instanceof User) {
            subjectName = ((User) subject).getScreenName();
        } else if (subject instanceof Group) {
            subjectName = ((Group) subject).getName();
        } else {
            subjectName = "???";
        }
        for (Map.Entry<CcmObject, Boolean> entry : expected.entrySet()) {
            assertThat(String.format("isPermitted should return %b for subject "
                                         + "%s and privilege %s on object %s.",
                                     entry.getValue(),
                                     subjectName,
                                     privilege.getLabel(),
                                     entry.getKey().getDisplayName()),
                       permissionManager.isPermitted(privilege,
                                                     entry.getKey(),
                                                     subject),
                       is(entry.getValue()));
        }
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(10)
    public void isPermittedWebmaster() {
        final User webmaster = userRepository.findByScreenName("webmaster");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expected = new LinkedHashMap<>();
        expected.put(testObjects.get(TEST_OBJECT_1), true);
        expected.put(testObjects.get(TEST_OBJECT_2), true);
        expected.put(testObjects.get(TEST_OBJECT_3), true);
        expected.put(testObjects.get(TEST_OBJECT_4), true);
        expected.put(testObjects.get(TEST_OBJECT_5), true);
        expected.put(testObjects.get(TEST_OBJECT_6), true);
        expected.put(testObjects.get(TEST_OBJECT_7), true);
        expected.put(testObjects.get(TEST_OBJECT_8), true);

        verifyIsPermitted(webmaster, privileges.get(ADMIN), expected);
        verifyIsPermitted(webmaster, privileges.get(READ), expected);
        verifyIsPermitted(webmaster, privileges.get(WRITE), expected);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(20)
    public void isPermittedJdoe() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expectedRead = new LinkedHashMap<>();
        expectedRead.put(testObjects.get(TEST_OBJECT_1), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_2), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_3), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_4), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_5), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_6), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_7), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_8), true);

        final Map<CcmObject, Boolean> expectedWrite = new LinkedHashMap<>();
        expectedWrite.put(testObjects.get(TEST_OBJECT_1), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_2), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_3), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_4), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_5), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_6), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_7), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_8), true);

        verifyIsPermitted(jdoe, privileges.get(READ), expectedRead);
        verifyIsPermitted(jdoe, privileges.get(WRITE), expectedWrite);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(30)
    public void isPermittedMmuster() {
        final User mmuster = userRepository.findByScreenName("mmuster");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expectedRead = new LinkedHashMap<>();
        expectedRead.put(testObjects.get(TEST_OBJECT_1), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_2), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_3), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_4), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_5), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_6), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_7), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_8), true);

        final Map<CcmObject, Boolean> expectedWrite = new LinkedHashMap<>();
        expectedWrite.put(testObjects.get(TEST_OBJECT_1), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_2), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_3), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_4), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_5), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_6), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_7), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_8), true);

        verifyIsPermitted(mmuster, privileges.get(READ), expectedRead);
        verifyIsPermitted(mmuster, privileges.get(WRITE), expectedWrite);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(40)
    public void isPermittedPublicUser() {
        final User publicUser = userRepository.findByScreenName("public-user");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expectedRead = new LinkedHashMap<>();
        expectedRead.put(testObjects.get(TEST_OBJECT_1), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_2), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_3), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_4), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_5), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_6), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_7), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_8), true);

        final Map<CcmObject, Boolean> expectedWrite = new LinkedHashMap<>();
        expectedWrite.put(testObjects.get(TEST_OBJECT_1), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_2), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_3), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_4), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_5), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_6), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_7), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_8), false);

        verifyIsPermitted(publicUser, privileges.get(READ), expectedRead);
        verifyIsPermitted(publicUser, privileges.get(WRITE), expectedWrite);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(50)
    public void isPermittedUsers() {
        final Group users = groupRepository.findByGroupName("users");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expectedRead = new LinkedHashMap<>();
        expectedRead.put(testObjects.get(TEST_OBJECT_1), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_2), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_3), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_4), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_5), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_6), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_7), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_8), true);

        final Map<CcmObject, Boolean> expectedWrite = new LinkedHashMap<>();
        expectedWrite.put(testObjects.get(TEST_OBJECT_1), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_2), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_3), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_4), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_5), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_6), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_7), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_8), false);

        verifyIsPermitted(users, privileges.get(READ), expectedRead);
        verifyIsPermitted(users, privileges.get(WRITE), expectedWrite);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(60)
    public void isPermittedAuthors() {
        final Group authors = groupRepository.findByGroupName("authors");
        final Map<String, CcmObject> testObjects = retrieveTestObjects();
        final Map<String, Privilege> privileges = retrievePrivileges();

        final Map<CcmObject, Boolean> expectedRead = new LinkedHashMap<>();
        expectedRead.put(testObjects.get(TEST_OBJECT_1), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_2), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_3), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_4), true);
        expectedRead.put(testObjects.get(TEST_OBJECT_5), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_6), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_7), false);
        expectedRead.put(testObjects.get(TEST_OBJECT_8), true);

        final Map<CcmObject, Boolean> expectedWrite = new LinkedHashMap<>();
        expectedWrite.put(testObjects.get(TEST_OBJECT_1), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_2), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_3), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_4), true);
        expectedWrite.put(testObjects.get(TEST_OBJECT_5), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_6), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_7), false);
        expectedWrite.put(testObjects.get(TEST_OBJECT_8), true);

        verifyIsPermitted(authors, privileges.get(READ), expectedRead);
        verifyIsPermitted(authors, privileges.get(WRITE), expectedWrite);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(70)
    public void isPermittedNullPrivilege() {
        final CcmObject object = ccmObjectRepository.findById(-10L);
        final User user = userRepository.findByScreenName("webmaster");

        permissionManager.isPermitted(null, object, user);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(80)
    public void isPermittedNullObject() {
        final Privilege privilege = privilegeRepository
            .retrievePrivilege(READ);
        final User user = userRepository.findByScreenName("webmaster");

        permissionManager.isPermitted(privilege, null, user);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @InSequence(100)
    public void checkPermissionValid() throws UnauthorizedAcccessException {
        final Privilege privilege = privilegeRepository
            .retrievePrivilege(READ);
        final CcmObject object = ccmObjectRepository.findById(-10L);
        final User user = userRepository.findByScreenName("jdoe");

        permissionManager.checkPermission(privilege, object, user);
    }

    @Test(expected = UnauthorizedAcccessException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(UnauthorizedAcccessException.class)
    @InSequence(110)
    public void checkPermissionInValid() throws UnauthorizedAcccessException {
        final Privilege privilege = privilegeRepository
            .retrievePrivilege(READ);
        final CcmObject object = ccmObjectRepository.findById(-60L);
        final User user = userRepository.findByScreenName("jdoe");

        permissionManager.checkPermission(privilege, object, user);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(120)
    public void checkPermissionNullPrivilege() throws
        UnauthorizedAcccessException {
        final CcmObject object = ccmObjectRepository.findById(-10L);
        final User user = userRepository.findByScreenName("webmaster");

        permissionManager.checkPermission(null, object, user);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(130)
    public void checkPermissionNullObject() throws UnauthorizedAcccessException {
        final Privilege privilege = privilegeRepository
            .retrievePrivilege(READ);
        final User user = userRepository.findByScreenName("webmaster");

        permissionManager.checkPermission(privilege, null, user);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(140)
    public void checkPermissionNullSubject() throws UnauthorizedAcccessException {
        final Privilege privilege = privilegeRepository
            .retrievePrivilege(READ);
        final CcmObject object = ccmObjectRepository.findById(-10L);

        permissionManager.checkPermission(privilege, object, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldMatchDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                            + "after-grant.json")
    @InSequence(150)
    public void grantPermission() {
        final Privilege read = privilegeRepository.retrievePrivilege(READ);
        final Privilege write = privilegeRepository.retrievePrivilege(WRITE);

        final User jdoe = userRepository.findByScreenName("jdoe");
        final User mmuster = userRepository.findByScreenName("mmuster");

        final CcmObject object6 = ccmObjectRepository.findById(-60L);
        final CcmObject object7 = ccmObjectRepository.findById(-70L);

        permissionManager.grantPermission(read, object6, jdoe);

        permissionManager.grantPermission(read, object7, mmuster);
        permissionManager.grantPermission(write, object7, mmuster);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldMatchDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                            + "after-grant-wildcard.json")
    @InSequence(160)
    public void grantWildcardPermission() {
        final Privilege read = privilegeRepository.retrievePrivilege(READ);
        final User jdoe = userRepository.findByScreenName("jdoe");

        permissionManager.grantPermission(read, null, jdoe);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(170)
    public void grantPermissionNullPrivilege() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final CcmObject object6 = ccmObjectRepository.findById(-60L);

        permissionManager.grantPermission(null, object6, jdoe);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(180)
    public void grantPermissionNullSubject() {
        final Privilege read = privilegeRepository.retrievePrivilege(READ);
        final CcmObject object6 = ccmObjectRepository.findById(-60L);

        permissionManager.grantPermission(read, object6, null);
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldMatchDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                            + "after-revoke.json")
    @InSequence(190)
    public void revokePermission() {
        final Privilege read = privilegeRepository.retrievePrivilege(READ);
        final Privilege write = privilegeRepository.retrievePrivilege(WRITE);

        final User jdoe = userRepository.findByScreenName("jdoe");
        final User mmuster = userRepository.findByScreenName("mmuster");

        final CcmObject object5 = ccmObjectRepository.findById(-50L);
        final CcmObject object6 = ccmObjectRepository.findById(-60L);

        permissionManager.revokePermission(read, object5, jdoe);
        permissionManager.revokePermission(write, object6, mmuster);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(200)
    public void revokePermissionNullPrivilege() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final CcmObject object5 = ccmObjectRepository.findById(-50L);

        permissionManager.revokePermission(null, object5, jdoe);
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet("datasets/org/libreccm/core/PermissionManagerTest/"
                      + "data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(210)
    public void revokePermissionNullSubject() {
        final Privilege read = privilegeRepository.retrievePrivilege(READ);
        final CcmObject object6 = ccmObjectRepository.findById(-60L);

        permissionManager.revokePermission(read, object6, null);
    }

}
