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
package org.libreccm.categorization;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.core.CcmObject;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.testutils.EqualsVerifier;
import org.libreccm.web.CcmApplication;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
public class EqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Categorization.class,
            org.libreccm.categorization.Category.class,
            Domain.class,
            DomainOwnership.class});
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }

    @Override
    protected void addPrefabValues(
        final SingleTypeEqualsVerifierApi<?> verifier
    ) {
        final Category category1 = new Category();
        category1.setName("Category One");

        final Category category2 = new Category();
        category2.setName("Category Two");

        final Domain domain1 = new Domain();
        domain1.setDomainKey("Domain-One");

        final Domain domain2 = new Domain();
        domain2.setDomainKey("Domain Two");

        final CcmApplication application1 = new CcmApplication();
        application1.setPrimaryUrl("http://application-one.exampl.org");

        final CcmApplication application2 = new CcmApplication();
        application2.setPrimaryUrl("http://application-two.exampl.org");

        final CcmObject ccmObject1 = new CcmObject();
        ccmObject1.setObjectId(-100);
        ccmObject1.setDisplayName("Object 1");

        final CcmObject ccmObject2 = new CcmObject();
        ccmObject1.setObjectId(-200);
        ccmObject1.setDisplayName("Object 2");

        final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role2.setName("role2");

        final Group group1 = new Group();
        group1.setName("group1");

        final Group group2 = new Group();
        group2.setName("group2");

        final User user1 = new TestUser();
        user1.setName("user1");

        final User user2 = new TestUser();
        user2.setName("user2");
        
        verifier
            .withPrefabValues(Category.class, category1, category2)
            .withPrefabValues(Domain.class, domain1, domain2)
            .withPrefabValues(CcmApplication.class, application1, application2)
            .withPrefabValues(CcmObject.class, ccmObject1, ccmObject2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(User.class, user1, user2);
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
