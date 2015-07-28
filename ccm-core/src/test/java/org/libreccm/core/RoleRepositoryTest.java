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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.libreccm.tests.categories.IntegrationTest;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(IntegrationTest.class)
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
public class RoleRepositoryTest {

    @Inject
    private transient RoleRepository roleRepository;

    @Inject
    private transient GroupRepository groupRepository;

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
                    "LibreCCM-org.libreccm.core.RoleRepositoryTest.war")
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

    private void verifyAuthor(final Role author) {
        assertThat(author, is(not(nullValue())));
        assertThat(author.getName(), is(equalTo("Author")));
        assertThat(author.getDescription(),
                   is(equalTo("Creates new content")));
        assertThat(author.getImplicitGroup().getName(),
                   is(equalTo("info Administration Author")));
        assertThat(author.getSourceGroup().getName(),
                   is(equalTo("info Administration")));
    }

    private void verifyEditor(final Role editor) {
        assertThat(editor, is(not(nullValue())));
        assertThat(editor.getName(), is(equalTo("Editor")));
        assertThat(editor.getDescription(),
                   is(equalTo("Reviews and approves the author's work")));
        assertThat(editor.getImplicitGroup().getName(),
                   is(equalTo("info Administration Editor")));
        assertThat(editor.getSourceGroup().getName(),
                   is(equalTo("info Administration")));
    }

    private void verifyPublisher(final Role publisher) {
        assertThat(publisher, is(not(nullValue())));
        assertThat(publisher.getName(), is(equalTo("Publisher")));
        assertThat(publisher.getDescription(),
                   is(equalTo("Deploys the content to the web site")));
        assertThat(publisher.getImplicitGroup().getName(),
                   is(equalTo("info Administration Publisher")));
        assertThat(publisher.getSourceGroup().getName(),
                   is(equalTo("info Administration")));
    }

    private void verifyManager(final Role manager) {
        assertThat(manager, is(not(nullValue())));
        assertThat(manager.getName(), is(equalTo("Manager")));
        assertThat(manager.getDescription(),
                   is(equalTo("Manages the overall content section")));
        assertThat(manager.getImplicitGroup().getName(),
                   is(equalTo("info Administration Manager")));
        assertThat(manager.getSourceGroup().getName(),
                   is(equalTo("info Administration")));
    }

    private void verifyTrustedUser(final Role trustedUser) {
        assertThat(trustedUser, is(not(nullValue())));
        assertThat(trustedUser.getName(), is(equalTo("Trusted User")));
        assertThat(trustedUser.getDescription(),
                   is(equalTo("A trusted user is allowed to create and publish "
                                  + "items without review")));
        assertThat(trustedUser.getImplicitGroup().getName(),
                   is(equalTo("info Administration Trusted User")));
        assertThat(trustedUser.getSourceGroup().getName(),
                   is(equalTo("info Administration")));
    }

    private void verifyContentReader(final Role contentReader) {
        assertThat(contentReader, is(not(nullValue())));
        assertThat(contentReader.getName(), is(equalTo("Content Reader")));
        assertThat(contentReader.getDescription(),
                   is(equalTo("Can view published pages within this section")));
        assertThat(contentReader.getImplicitGroup().getName(),
                   is(equalTo("info Viewers Content Reader")));
        assertThat(contentReader.getSourceGroup().getName(),
                   is(equalTo("info Viewers")));
    }

    private void verifyAlertRecipient(final Role alertRecipient) {
        assertThat(alertRecipient, is(not(nullValue())));
        assertThat(alertRecipient.getName(), is(equalTo("Alert Recipient")));
        assertThat(alertRecipient.getDescription(),
                   is(equalTo("Receive alerts regarding expiration of "
                                  + "published content")));
        assertThat(alertRecipient.getImplicitGroup().getName(),
                   is(equalTo("info Administration Alert Recipient")));
        assertThat(alertRecipient.getSourceGroup().getName(),
                   is(equalTo("info Viewers")));
    }

    @Test
    @InSequence(10)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    public void findRoleById() {
        final Role author = roleRepository.findById(-10L);
        final Role editor = roleRepository.findById(-20L);
        final Role publisher = roleRepository.findById(-30L);
        final Role manager = roleRepository.findById(-40L);
        final Role trustedUser = roleRepository.findById(-50L);
        final Role contentReader = roleRepository.findById(-60L);
        final Role alertRecipient = roleRepository.findById(-70L);

        verifyAuthor(author);
        verifyEditor(editor);
        verifyPublisher(publisher);
        verifyManager(manager);
        verifyTrustedUser(trustedUser);
        verifyContentReader(contentReader);
        verifyAlertRecipient(alertRecipient);
    }

    @Test
    @InSequence(20)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    public void findRoleByName() {
        final List<Role> authors = roleRepository.findRolesForName("Author");
        final List<Role> editors = roleRepository.findRolesForName("Editor");
        final List<Role> publishers = roleRepository.findRolesForName(
            "Publisher");
        final List<Role> managers = roleRepository.findRolesForName("Manager");
        final List<Role> trustedUsers = roleRepository.findRolesForName(
            "Trusted User");
        final List<Role> contentReaders = roleRepository.findRolesForName(
            "Content Reader");
        final List<Role> alertRecipients = roleRepository.findRolesForName(
            "Alert Recipient");

        assertThat(authors.size(), is(1));
        assertThat(editors.size(), is(1));
        assertThat(publishers.size(), is(1));
        assertThat(managers.size(), is(1));
        assertThat(trustedUsers.size(), is(1));
        assertThat(contentReaders.size(), is(1));
        assertThat(alertRecipients.size(), is(1));

        final Role author = authors.get(0);
        final Role editor = editors.get(0);
        final Role publisher = publishers.get(0);
        final Role manager = managers.get(0);
        final Role trustedUser = trustedUsers.get(0);
        final Role contentReader = contentReaders.get(0);
        final Role alertRecipient = alertRecipients.get(0);

        verifyAuthor(author);
        verifyEditor(editor);
        verifyPublisher(publisher);
        verifyManager(manager);
        verifyTrustedUser(trustedUser);
        verifyContentReader(contentReader);
        verifyAlertRecipient(alertRecipient);
    }

    @Test
    @InSequence(30)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    public void findRolesForSourceGroup() {
        final Group group = groupRepository.findByGroupName(
            "info Administration");
        
        assertThat(group, is(not(nullValue())));
        
        final List<Role> roles = roleRepository.findRolesForSourceGroup(group);

        assertThat(roles.size(), is(5));

        verifyAuthor(roles.get(0));
        verifyEditor(roles.get(1));
        verifyManager(roles.get(2));
        verifyPublisher(roles.get(3));
        verifyTrustedUser(roles.get(4));
    }

    @Test
    @InSequence(40)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    public void findRolesForImplicitGroup() {
        final Group authorsGroup = groupRepository.findByGroupName(
            "info Administration Author");
        assertThat(authorsGroup, is(not(nullValue())));
        final List<Role> authors = roleRepository.findRolesForImplicitGroup(
            authorsGroup);
        assertThat(authors.size(), is(1));
        verifyAuthor(authors.get(0));

        final Group editorsGroup = groupRepository.findByGroupName(
            "info Administration Editor");
        assertThat(editorsGroup, is(not(nullValue())));
        final List<Role> editors = roleRepository.findRolesForImplicitGroup(
            editorsGroup);
        assertThat(editors.size(), is(1));
        verifyEditor(editors.get(0));

        final Group publisherGroup = groupRepository.findByGroupName(
            "info Administration Publisher");
        assertThat(publisherGroup, is(not(nullValue())));
        final List<Role> publishers = roleRepository.findRolesForImplicitGroup(
            publisherGroup);
        assertThat(publishers.size(), is(1));
        verifyPublisher(publishers.get(0));
    }

    @Test
    @InSequence(50)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/RoleRepositoryTest/"
                                    + "after-save-new.json",
                        excludeColumns = {"role_id"})
    public void saveNewRole() {
        final Group infoAdmin = groupRepository.findByGroupName(
            "info Administration");
        final Group readers = groupRepository.findByGroupName(
            "info Viewers Content Reader");

        final Role role = new Role();
        role.setName("Test");
        role.setDescription("New role for testing");
        role.setImplicitGroup(infoAdmin);
        role.setSourceGroup(readers);

        roleRepository.save(role);
    }

    @Test
    @InSequence(60)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/core/RoleRepositoryTest/"
                                    + "after-save-changed.json",
                        excludeColumns = {"role_id"})
    public void saveChangedRole() {
        final Role role = roleRepository.findById(-60L);
        role.setName("Reader");

        roleRepository.save(role);
    }

    @Test(expected = IllegalArgumentException.class)
    @ShouldThrowException(IllegalArgumentException.class)
    @InSequence(70)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    public void saveNullValue() {
        roleRepository.save(null);
    }

    @Test
    @InSequence(80)
    @UsingDataSet("datasets/org/libreccm/core/RoleRepositoryTest/data.json")
    @ShouldMatchDataSet("datasets/org/libreccm/core/RoleRepositoryTest/"
                            + "after-delete.json")
    public void deleteRole() {
        final Role role = roleRepository.findById(-50L);

        roleRepository.delete(role);
    }

}
