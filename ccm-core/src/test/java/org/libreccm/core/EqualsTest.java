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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.tests.categories.UnitTest;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class EqualsTest {

    public EqualsTest() {
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

    @Test
    public void verifyCcmObject() {
        EqualsVerifier
            .forClass(CcmObject.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyEmailAddress() {
        EqualsVerifier
            .forClass(EmailAddress.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyGroupMembership() {
        EqualsVerifier
            .forClass(GroupMembership.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyParty() {
        EqualsVerifier
            .forClass(Party.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyPermission() {
        EqualsVerifier
            .forClass(Permission.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyPersonName() {
        EqualsVerifier
            .forClass(PersonName.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyPrivilege() {
        EqualsVerifier
            .forClass(Privilege.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyResource() {
        EqualsVerifier
            .forClass(Resource.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyRole() {
        EqualsVerifier
            .forClass(Role.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyUser() {
        EqualsVerifier
            .forClass(User.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyUserGroup() {
        EqualsVerifier
            .forClass(UserGroup.class)
            .suppress(Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

}
