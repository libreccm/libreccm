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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.PersistenceTest;
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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;

import java.util.Collections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class PermissionRepositoryTest {

    @Inject
    private transient PermissionRepository permissionRepository;

    @Inject
    private transient UserRepository userRepository;

    @Inject
    private transient GroupRepository groupRepository;

    @Inject
    private transient CcmObjectRepository ccmObjectRepository;

    @Inject
    private transient EntityManager entityManager;

    public PermissionRepositoryTest() {
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
                    "LibreCCM-org.libreccm.core.UserRepositoryTest.war")
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

    @Test
    @UsingDataSet("datasets/org/libreccm/core/PermissionRepositoryTest/"
                      + "data.json")
    @InSequence(10)
    public void findPermissionsForSubject() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final User mmuster = userRepository.findByScreenName("mmuster");
        final Group admins = groupRepository.findByGroupName("admins");
        final Group users = groupRepository.findByGroupName("users");
        final Group authors = groupRepository.findByGroupName("authors");

        assertThat(jdoe, is(not(nullValue())));
        assertThat(mmuster, is(not(nullValue())));
        assertThat(admins, is(not(nullValue())));
        assertThat(users, is(not(nullValue())));
        assertThat(authors, is(not(nullValue())));

        final List<Permission> permissionsJdoe = permissionRepository
            .findPermissionsForSubject(jdoe);
        assertThat(permissionsJdoe.size(), is(1));
        assertThat(permissionsJdoe.get(0).getObject().getDisplayName(),
                   is(equalTo("Test Object 2")));
        assertThat(permissionsJdoe.get(0).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("read")));

        final List<Permission> permissionsMmuster = permissionRepository
            .findPermissionsForSubject(mmuster);
        assertThat(permissionsMmuster.size(), is(0));

        final List<Permission> permissionsAdmins = permissionRepository
            .findPermissionsForSubject(admins);
        assertThat(permissionsAdmins.size(), is(1));
        assertThat(permissionsAdmins.get(0).getObject(), is(nullValue()));
        assertThat(permissionsAdmins.get(0).getGrantedPrivilege().getPrivilege(),
                   is("admin"));

        final List<Permission> permissionsUsers = permissionRepository
            .findPermissionsForSubject(users);
        assertThat(permissionsUsers.size(), is(1));
        assertThat(permissionsUsers.get(0).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));
        assertThat(permissionsUsers.get(0).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("read")));

        final List<Permission> permissionsAuthors = permissionRepository
            .findPermissionsForSubject(authors);
        assertThat(permissionsAuthors.size(), is(2));
        assertThat(permissionsAuthors.get(0).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));
        assertThat(permissionsAuthors.get(1).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));
        final Set<String> privileges = new HashSet<>();
        privileges.add(permissionsAuthors.get(0).getGrantedPrivilege()
            .getPrivilege());
        privileges.add(permissionsAuthors.get(1).getGrantedPrivilege()
            .getPrivilege());
        assertThat(privileges, hasItem("read"));
        assertThat(privileges, hasItem("write"));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(11)
    public void findPermissionsForNullSubject() {
        permissionRepository.findPermissionsForSubject(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @InSequence(20)
    public void findPermissionsForUser() {
        final User jdoe = userRepository.findByScreenName("jdoe");
        final User mmuster = userRepository.findByScreenName("mmuster");
        assertThat(jdoe, is(not(nullValue())));
        assertThat(mmuster, is(not(nullValue())));

        final List<Permission> jdoePermissions = permissionRepository
            .findPermissionsForUser(jdoe);
        assertThat(jdoePermissions.size(), is(4));
        Collections.sort(jdoePermissions, new Comparator<Permission>() {

            @Override
            public int compare(final Permission permission1,
                               final Permission permission2) {
                int result = permission1.getGrantedPrivilege().getPrivilege()
                    .compareToIgnoreCase(permission2.getGrantedPrivilege()
                        .getPrivilege());

                if (result == 0) {
                    result = permission1.getObject().getDisplayName().compareTo(
                        permission2.getObject().getDisplayName());
                } else {
                    return result;
                }

                if (result == 0) {
                    return permission1.getGrantee().getClass().getName()
                        .compareTo(permission2.getGrantee().getClass().
                            getName());
                } else {
                    return result;
                }

            }

        });

        assertThat(jdoePermissions.get(0).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("read")));
        assertThat(jdoePermissions.get(0).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));
        assertThat(jdoePermissions.get(1).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("read")));
        assertThat(jdoePermissions.get(1).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));
        assertThat(jdoePermissions.get(2).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("read")));
        assertThat(jdoePermissions.get(2).getObject().getDisplayName(),
                   is(equalTo("Test Object 2")));
        assertThat(jdoePermissions.get(3).getGrantedPrivilege().getPrivilege(),
                   is(equalTo("write")));
        assertThat(jdoePermissions.get(3).getObject().getDisplayName(),
                   is(equalTo("Test Object 1")));

        final List<Permission> mmusterPermissions = permissionRepository
            .findPermissionsForUser(mmuster);
        assertThat(mmusterPermissions.size(), is(1));
        assertThat(mmusterPermissions.get(0).getGrantedPrivilege()
            .getPrivilege(),
                   is(equalTo("admin")));
        assertThat(mmusterPermissions.get(0).getObject(), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(21)
    public void findPermissionsForNullUser() {
        permissionRepository.findPermissionsForUser(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @InSequence(30)
    public void findPermissionsForCcmObject() {
        final CcmObject object1 = ccmObjectRepository.findById(-10L);
        final CcmObject object2 = ccmObjectRepository.findById(-20L);
        final CcmObject object3 = ccmObjectRepository.findById(-30L);

        final List<Permission> object1Permissions = permissionRepository
            .findPermissionsForCcmObject(object1);
        assertThat(object1Permissions.size(), is(3));
        Collections.sort(object1Permissions, new Comparator<Permission>() {

            @Override
            public int compare(final Permission permission1,
                               final Permission permission2) {
                return Long.compare(permission1.getPermissionId(),
                                    permission2.getPermissionId());
            }

        });
        assertThat(object1Permissions.get(0).getGrantedPrivilege()
            .getPrivilege(),
                   is(equalTo("read")));
        assertThat(object1Permissions.get(0).getGrantee(),
                   is(instanceOf(Group.class)));
        assertThat(((Group) object1Permissions.get(0).getGrantee()).getName(),
                   is(equalTo("authors")));
        assertThat(object1Permissions.get(1).getGrantedPrivilege()
            .getPrivilege(),
                   is(equalTo("write")));
        assertThat(object1Permissions.get(1).getGrantee(),
                   is(instanceOf(Group.class)));
        assertThat(((Group) object1Permissions.get(1).getGrantee()).getName(),
                   is(equalTo("authors")));
        assertThat(object1Permissions.get(2).getGrantedPrivilege()
            .getPrivilege(),
                   is(equalTo("read")));
        assertThat(object1Permissions.get(2).getGrantee(),
                   is(instanceOf(Group.class)));
        assertThat(((Group) object1Permissions.get(2).getGrantee()).getName(),
                   is(equalTo("users")));

        final List<Permission> object2Permissions = permissionRepository
            .findPermissionsForCcmObject(object2);
        assertThat(object2Permissions.size(), is(1));
        assertThat(object2Permissions.get(0).getGrantedPrivilege()
            .getPrivilege(),
                   is(equalTo("read")));
        assertThat(object2Permissions.get(0).getGrantee(),
                   is(instanceOf(User.class)));
        assertThat(((User) object2Permissions.get(0).getGrantee())
            .getScreenName(),
                   is(equalTo("jdoe")));

        final List<Permission> object3Permissions = permissionRepository
            .findPermissionsForCcmObject(object3);
        assertThat(object3Permissions, is(empty()));
    }

    @Test(expected = IllegalArgumentException.class)
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(31)
    public void findPermissionsForNullObject() {
        permissionRepository.findPermissionsForCcmObject(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/"
                                    + "PermissionRepositoryTest/after-save-new.json",
                        excludeColumns = {"permission_id"})
    @InSequence(40)
    public void saveNewPermission() {
        final User mmuster = userRepository.findByScreenName("mmuster");

        final TypedQuery<Privilege> query1 = entityManager.createQuery(
            "SELECT p FROM Privilege p WHERE p.privilege = 'read'",
            Privilege.class);
        final TypedQuery<Privilege> query2 = entityManager.createQuery(
            "SELECT p FROM Privilege p WHERE p.privilege = 'write'",
            Privilege.class);

        final CcmObject object = ccmObjectRepository.findById(-40L);

        final Privilege read = query1.getSingleResult();
        final Privilege write = query2.getSingleResult();

        assertThat(mmuster, is(not(nullValue())));
        assertThat(read, is(not(nullValue())));
        assertThat(write, is(not(nullValue())));
        assertThat(object, is(not(nullValue())));

        final Permission permission1 = new Permission();
        permission1.setGrantee(mmuster);
        permission1.setGrantedPrivilege(read);
        permission1.setObject(object);

        final Permission permission2 = new Permission();
        permission2.setGrantee(mmuster);
        permission2.setGrantedPrivilege(write);
        permission2.setObject(object);

        permissionRepository.save(permission1);
        permissionRepository.save(permission2);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(41)
    public void saveNullPermission() {
        permissionRepository.save(null);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/"
                                    + "PermissionRepositoryTest/after-save-changed.json",
                        excludeColumns = {"permission_id"})
    @InSequence(50)
    public void saveChangedPermission() {
        final Permission permission = entityManager.find(Permission.class, -40L);
        final Group users = groupRepository.findByGroupName("users");

        assertThat(permission, is(not(nullValue())));
        assertThat(users, is(not(nullValue())));

        permission.setGrantee(users);

        permissionRepository.save(permission);
    }

    @Test
    @UsingDataSet(
        "datasets/org/libreccm/core/PermissionRepositoryTest/data.json")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/"
                                    + "PermissionRepositoryTest/after-delete.json",
                        excludeColumns = {"permission_id"})
    @InSequence(60)
    public void deletePermission() {
        final Permission permission = entityManager.find(Permission.class, -35L);

        permissionRepository.delete(permission);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(61)
    public void deleteNullPermission() {
        permissionRepository.delete(null);
    }

}
