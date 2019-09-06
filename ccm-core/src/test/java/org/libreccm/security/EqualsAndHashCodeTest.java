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

import nl.jqno.equalsverifier.EqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.core.CcmObject;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.workflow.AssignableTask;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class EqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Group.class,
            GroupMembership.class,
            Party.class,
            OneTimeAuthToken.class,
            Role.class,
            RoleMembership.class,
            User.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }

    @Override
    protected void addPrefabValues(EqualsVerifierApi<?> verifier) {
        
        final Group group1 = new Group();
        group1.setName("group1");

        final Group group2 = new Group();
        group2.setName("group2");
        
         final User user1 = new User();
        user1.setName("user1");

        final User user2 = new User();
        user2.setName("user2");

         final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role2.setName("role2");
        
        final Party party1 = new Party();
        party1.setName("party1");
        
        final Party party2 = new Party();
        party2.setName("party2");
        
         final CcmObject ccmObject1 = new CcmObject();
        ccmObject1.setObjectId(-100);
        ccmObject1.setDisplayName("Object 1");

        final CcmObject ccmObject2 = new CcmObject();
        ccmObject1.setObjectId(-200);
        ccmObject1.setDisplayName("Object 2");
        
        final AssignableTask task1 = new AssignableTask();
        task1.getLabel().addValue(Locale.ENGLISH, "Task 1");
        
        final AssignableTask task2 = new AssignableTask();
        task2.getLabel().addValue(Locale.ENGLISH, "Task 2");
        
        verifier
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Party.class, party1, party2)
            .withPrefabValues(CcmObject.class, ccmObject1, ccmObject2)
            .withPrefabValues(AssignableTask.class, task1, task2);
    }

}
