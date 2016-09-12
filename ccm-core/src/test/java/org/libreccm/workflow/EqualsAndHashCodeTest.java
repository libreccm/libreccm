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

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class EqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Task.class,
            TaskAssignment.class,
            UserTask.class,
            Workflow.class,
            WorkflowTemplate.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }
    
    @Override
    protected void addPrefabValues(
        final nl.jqno.equalsverifier.EqualsVerifier<?> verifier) {
        
        super.addPrefabValues(verifier);
        
        final UserTask userTask1 = new UserTask();
        userTask1.setTaskId(-10);
        
        final UserTask userTask2 = new UserTask();
        userTask2.setTaskId(-20);
        
        final Role role1 = new Role();
        role1.setName("role1");
        
        final Role role2 = new Role();
        role2.setName("role2");
        
        final Task task1 = new Task();
        task1.setTaskId(-10);
        
        final Task task2  = new Task();
        task2.setTaskId(-20);
        
        final Group group1 = new Group();
        group1.setName("group1");
        
        final Group group2 = new Group();
        group2.setName("group2");
        
        final User user1 = new TestUser();
        user1.setName("user1");
        
        final User user2 = new TestUser();
        user2.setName("user2");
        
        final WorkflowTemplate template1 = new WorkflowTemplate();
        template1.getName().addValue(Locale.ENGLISH, "Template 1");
        
        final WorkflowTemplate template2 = new WorkflowTemplate();
        template1.getName().addValue(Locale.ENGLISH, "Template 2");
        
        verifier
            .withPrefabValues(UserTask.class, userTask1, userTask2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Task.class, task1, task2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(WorkflowTemplate.class, template1, template2);
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
