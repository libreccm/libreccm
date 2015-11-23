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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.core.EmailAddress;
import org.libreccm.tests.categories.UnitTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class ListContainsTest {
    
    public ListContainsTest() {
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
    public void listOfUsers() {
        final User user1 = new User();
        user1.setName("user1");
        user1.setGivenName("User");
        user1.setFamilyName("One");
        final EmailAddress user1mail = new EmailAddress();
        user1mail.setAddress("user.one@example.org");
        user1.setPrimaryEmailAddress(user1mail);
        
        final User user2 = new User();
        user2.setName("user2");
        user2.setGivenName("User");
        user2.setFamilyName("Two");
        final EmailAddress user2mail = new EmailAddress();
        user2mail.setAddress("user.two@example.org");
        user2.setPrimaryEmailAddress(user1mail);
        
        final User user3 = new User();
        user3.setName("user3");
        user3.setGivenName("User");
        user3.setFamilyName("Three");
        final EmailAddress user3mail = new EmailAddress();
        user3mail.setAddress("user.three@example.org");
        user3.setPrimaryEmailAddress(user1mail);
        
        final List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        
        assertThat(users.size(), is(3));
        assertThat(users.contains(user1), is(true));
        assertThat(users.contains(user2), is(true));
        assertThat(users.contains(user3), is(true));
    }
    
}
