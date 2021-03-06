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

import org.apache.shiro.subject.Subject;
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
import org.libreccm.core.EmailAddress;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import static org.libreccm.testutils.DependenciesHelpers.*;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.TestExecutionPhase;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Arquillian.class)
@PersistenceTest
@Transactional(TransactionMode.COMMIT)
@CreateSchema({"001_create_schema.sql",
               "002_create_ccm_core_tables.sql",
               "003_init_hibernate_sequence.sql"})
@CleanupUsingScript(value = {"999_cleanup.sql"},
                    phase = TestExecutionPhase.BEFORE)
public class PartyRepositoryIT {

    private static final String MMUSTER = "mmuster";

    private static final String JDOE = "jdoe";

    private static final String ADMINS = "admins";

    private static final String MANAGERS = "managers";

    @Inject
    private PartyRepository partyRepository;

    @Inject
    private Shiro shiro;

    @PersistenceContext
    private EntityManager entityManager;

    public PartyRepositoryIT() {
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
                    "LibreCCM-org.libreccm.security.UserRepositoryTest.war")
            .addPackages(true, "com.arsdigita", "org.libreccm")
            .addAsLibraries(getModuleDependencies())
            .addAsResource("configs/shiro.ini", "shiro.ini")
            .addAsResource("test-persistence.xml",
                           "META-INF/persistence.xml")
            .addAsWebInfResource("test-web.xml", "web.xml")
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml");
    }

    @Test
    public void repoIsInjected() {
        assertThat(partyRepository, is(not(nullValue())));
    }

    @Test
    public void entityManagerIsInjected() {
        assertThat(entityManager, is(not(nullValue())));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @InSequence(100)
    public void findPartyById() {
        final User jdoe = (User) partyRepository.findById(10L).get();
        final Group admins = (Group) partyRepository.findById(20L).get();

        assertThat(jdoe, is(not(nullValue())));
        assertThat(jdoe.getPartyId(), is(10L));
        assertThat(jdoe.getName(), is(equalTo(JDOE)));
        assertThat(jdoe.getFamilyName(), is(equalTo("Doe")));
        assertThat(jdoe.getGivenName(), is(equalTo("John")));
        assertThat(jdoe.getPassword(),
                   is(equalTo(
                       "$shiro1$SHA-512$500000$7xkDcZUN0/whJInHIvGsDw==$WhelBVmJU/cLV7lAkMOrE5B/mqCW0bUuid1WX+xBwzzAaekC5bYn9eeOFGJWhiDgmaC50ZCUmM96/iGsRoc4uA==")));

        assertThat(admins, is(not(nullValue())));
        assertThat(admins.getPartyId(), is(20L));
        assertThat(admins.getName(), is(equalTo(ADMINS)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @InSequence(110)
    public void findByName() {
        final User jdoe = (User) partyRepository.findByName(JDOE).get();
        final Group admins = (Group) partyRepository.findByName(ADMINS).get();

        assertThat(jdoe, is(not(nullValue())));
        assertThat(jdoe.getPartyId(), is(10L));
        assertThat(jdoe.getName(), is(equalTo(JDOE)));
        assertThat(jdoe.getFamilyName(), is(equalTo("Doe")));
        assertThat(jdoe.getGivenName(), is(equalTo("John")));
        assertThat(jdoe.getPassword(),
                   is(equalTo(
                       "$shiro1$SHA-512$500000$7xkDcZUN0/whJInHIvGsDw==$WhelBVmJU/cLV7lAkMOrE5B/mqCW0bUuid1WX+xBwzzAaekC5bYn9eeOFGJWhiDgmaC50ZCUmM96/iGsRoc4uA==")));

        assertThat(admins, is(not(nullValue())));
        assertThat(admins.getPartyId(), is(20L));
        assertThat(admins.getName(), is(equalTo(ADMINS)));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @InSequence(200)
    public void findAllParties() {
        final List<Party> parties = partyRepository.findAll();

        assertThat(parties.size(), is(2));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        excludeColumns = {"party_id", "uuid", "password"},
        value = "datasets/org/libreccm/security/"
                    + "PartyRepositoryTest/after-save-new.yml"
    )
    @InSequence(300)
    public void saveNewParty() {
        final User mmuster = new User();

        final EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress("max.mustermann@example.org");
        emailAddress.setBouncing(false);
        emailAddress.setVerified(true);

        mmuster.setName("mmuster");
        mmuster.setGivenName("Max");
        mmuster.setFamilyName("Mustermann");
        mmuster.setPrimaryEmailAddress(emailAddress);
        mmuster.setPassword(
            "$shiro1$SHA-512$500000$Y7CnccN1h25sR7KCElMOXg==$CVLWBhetodaEzzhDfGjRcCFZtSW02xOnjH7xhBx0lbxO66grKIt6LWmXoUhLEydce1JZ7cbzNLYOxIwwTeqi5Q==");
        mmuster.setPasswordResetRequired(false);

        final Subject system = shiro.getSystemUser();
        system.execute(() -> partyRepository.save(mmuster));

        final Group users = new Group();
        users.setName("users");

        system.execute(() -> partyRepository.save(users));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "PartyRepositoryTest/after-save-changed.yml",
                        excludeColumns = {"party_id", "password"}
    )
    @InSequence(400)
    public void saveChangedParty() {
        final Party user = partyRepository.findById(10L).get();
        final Party group = partyRepository.findById(20L).get();

        user.setName("johndoe");
        group.setName("managers");

        shiro.getSystemUser().execute(() -> {
            partyRepository.save(user);
            partyRepository.save(group);
        });
    }

    @Test(expected = NullPointerException.class)
    @ShouldThrowException(NullPointerException.class)
    @InSequence(500)
    public void saveNullValue() {
        shiro.getSystemUser().execute(() -> partyRepository.save(null));
    }

    @Test
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @ShouldMatchDataSet(value = "datasets/org/libreccm/security/"
                                    + "PartyRepositoryTest/after-delete.yml",
                        excludeColumns = {"party_id"})
    @InSequence(600)
    public void deleteParty() {
        final Party user = partyRepository.findById(10L).get();

        shiro.getSystemUser().execute(() -> partyRepository.delete(user));
    }

    @Test(expected = NullPointerException.class)
    @ShouldThrowException(NullPointerException.class)
    @UsingDataSet("datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @ShouldMatchDataSet(
        "datasets/org/libreccm/security/PartyRepositoryTest/data.yml")
    @InSequence(700)
    public void deleteNullValue() {
        shiro.getSystemUser().execute(() -> partyRepository.delete(null));
    }

}
