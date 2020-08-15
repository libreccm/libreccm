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
package org.libreccm.workflow;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.core.CcmObject;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.testutils.EqualsVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
public class EqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Task.class,
            TaskComment.class,
            TaskAssignment.class,
            AssignableTask.class,
            Workflow.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }

    @Override
    protected void addPrefabValues(final SingleTypeEqualsVerifierApi<?> verifier) {

        super.addPrefabValues(verifier);

        final AssignableTask userTask1 = new AssignableTask();
        userTask1.setTaskId(-10);

        final AssignableTask userTask2 = new AssignableTask();
        userTask2.setTaskId(-20);

        final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role2.setName("role2");

        final Task task1 = new Task();
        task1.setTaskId(-10);

        final Task task2 = new Task();
        task2.setTaskId(-20);

        final Group group1 = new Group();
        group1.setName("group1");

        final Group group2 = new Group();
        group2.setName("group2");

        final User user1 = new TestUser();
        user1.setName("user1");

        final User user2 = new TestUser();
        user2.setName("user2");

        final Workflow workflow1 = new Workflow();
        workflow1.getName().addValue(Locale.ENGLISH, "Workflow 1");

        final Workflow workflow2 = new Workflow();
        workflow2.getName().addValue(Locale.ENGLISH, "Workflow 2");
        
        final CcmObject object1 = new CcmObject();
        object1.setDisplayName("Object 1");
        
        final CcmObject object2 = new CcmObject();
        object2.setDisplayName("Object 2");

        verifier
            .withPrefabValues(AssignableTask.class, userTask1, userTask2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Task.class, task1, task2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Workflow.class, workflow1, workflow2)
            .withPrefabValues(CcmObject.class, object1, object2);
    }

    /**
     * {@link User} has a protected constructor, so have have do this to create
     * users for the test...
     */
    private class TestUser extends User {

        private static final long serialVersionUID = -9052762220990453621L;

        protected TestUser() {
            super();
        }

    }

}
