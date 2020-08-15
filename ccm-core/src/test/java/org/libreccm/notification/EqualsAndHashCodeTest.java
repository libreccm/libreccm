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
package org.libreccm.notification;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.categorization.Category;
import org.libreccm.core.CcmObject;
import org.libreccm.messaging.Message;
import org.libreccm.security.Party;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.testutils.EqualsVerifier;

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
            Digest.class,
            Notification.class,
            QueueItem.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityClass) {
        super(entityClass);
    }

    @Override
    protected void addPrefabValues(final SingleTypeEqualsVerifierApi<?> verifier) {

        final Message message1 = new Message();
        message1.setSubject("Message One");

        final Message message2 = new Message();
        message2.setSubject("Message Two");

        final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role2.setName("role2");

        final Digest digest1 = new Digest();
        digest1.setDisplayName("digest1");

        final Digest digest2 = new Digest();
        digest2.setDisplayName("digest2");

        final Party party1 = new TestParty();
        party1.setName("party1");

        final Party party2 = new TestParty();
        party2.setName("party2");

        final CcmObject ccmObject1 = new CcmObject();
        ccmObject1.setObjectId(-100);
        ccmObject1.setDisplayName("Object 1");

        final CcmObject ccmObject2 = new CcmObject();
        ccmObject1.setObjectId(-200);
        ccmObject1.setDisplayName("Object 2");

        final User user1 = new TestUser();
        user1.setName("user1");

        final User user2 = new TestUser();
        user2.setName("user2");

        final Category category1 = new Category();
        category1.setName("Category One");

        final Category category2 = new Category();
        category2.setName("Category Two");

        verifier
            .withPrefabValues(Message.class, message1, message2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Digest.class, digest1, digest2)
            .withPrefabValues(Party.class, party1, party2)
            .withPrefabValues(CcmObject.class, ccmObject1, ccmObject2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Category.class, category1, category2);

    }

    /**
     * {@link Party} has a protected constructor, so have have do this to create
     * users for the test...
     */
    private class TestParty extends Party {

        private static final long serialVersionUID = 2021200151554200503L;

        protected TestParty() {
            super();
        }

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
