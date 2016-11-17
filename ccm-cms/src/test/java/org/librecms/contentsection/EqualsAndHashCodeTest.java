/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.Domain;
import org.libreccm.core.CcmObject;
import org.libreccm.core.Resource;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.lifecycle.LifecycleDefinition;

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
            //ContentItem.class,
            ContentSection.class,
            ContentType.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void addPrefabValues(
        final nl.jqno.equalsverifier.EqualsVerifier<?> verifier) {

        final ContentItem item1 = new ContentItem();
        item1.setObjectId(-1100);
        item1.setDisplayName("Object 1");

        final ContentItem item2 = new ContentItem();
        item2.setObjectId(-1200);
        item2.setDisplayName("Object 2");

        final ContentSection section1 = new ContentSection();
        section1.setObjectId(-2100);
        section1.setDisplayName("Section 1");

        final ContentSection section2 = new ContentSection();
        section2.setObjectId(-2200);
        section2.setDisplayName("Section 2");

        final CcmObject object1 = new CcmObject();
        object1.setObjectId(-3100);
        object1.setDisplayName("Object 1");

        final CcmObject object2 = new CcmObject();
        object2.setObjectId(-3200);
        object2.setDisplayName("Object 2");

        final Category category1 = new Category();
        category1.setObjectId(-4100);
        category1.setDisplayName("Category 1");

        final Category category2 = new Category();
        category2.setObjectId(-4200);
        category2.setDisplayName("Category 2");

        final ContentType contentType1 = new ContentType();
        contentType1.setObjectId(-5100);
        contentType1.setDisplayName("Content Type 1");

        final ContentType contentType2 = new ContentType();
        contentType2.setObjectId(-5200);
        contentType2.setDisplayName("Content Type 2");

        final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role2.setName("role2");

        final User user1 = new TestUser();
        user1.setName("user1");

        final User user2 = new TestUser();
        user2.setName("user2");

        final Group group1 = new Group();
        group1.setName("group1");

        final Group group2 = new Group();
        group2.setName("group2");

        final CcmApplication application1 = new CcmApplication();
        application1.setDisplayName("Application 1");

        final CcmApplication application2 = new CcmApplication();
        application2.setDisplayName("Application 2");

        final Domain domain1 = new Domain();
        domain1.setDisplayName("Domain 1");

        final Domain domain2 = new Domain();
        domain2.setDisplayName("Domain 2");

        final Resource resource1 = new Resource();
        resource1.setDisplayName("Resource 1");

        final Resource resource2 = new Resource();
        resource2.setDisplayName("Resource 2");

        final LifecycleDefinition lifecycleDef1 = new LifecycleDefinition();
        lifecycleDef1.setDefinitionId(-100);

        final LifecycleDefinition lifecycleDef2 = new LifecycleDefinition();
        lifecycleDef2.setDefinitionId(-110);

        final WorkflowTemplate workflowTemplate1 = new WorkflowTemplate();
        workflowTemplate1.getName().addValue(Locale.ENGLISH,
                                             "Workflow Template 1");

        final WorkflowTemplate workflowTemplate2 = new WorkflowTemplate();
        workflowTemplate2.getName().addValue(Locale.ENGLISH, 
                                             "Workflow Template 2");

        verifier
            .withPrefabValues(ContentItem.class, item1, item2)
            .withPrefabValues(ContentSection.class, section1, section2)
            .withPrefabValues(CcmObject.class, object1, object2)
            .withPrefabValues(Category.class, category1, category2)
            .withPrefabValues(ContentType.class, contentType1, contentType2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(CcmApplication.class, application1, application2)
            .withPrefabValues(Domain.class, domain1, domain2)
            .withPrefabValues(Resource.class, resource1, resource2)
            .withPrefabValues(LifecycleDefinition.class,
                              lifecycleDef1,
                              lifecycleDef2)
            .withPrefabValues(WorkflowTemplate.class,
                              workflowTemplate1,
                              workflowTemplate2);
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
