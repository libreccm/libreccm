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
import org.libreccm.core.CcmObject;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.security.User;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Verifies that the {@code equals} and {@code hashCode} methods of the {@link Asset}
 * class are working properly.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class AssetEqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Asset.class,
        });
    }

    public AssetEqualsAndHashCodeTest(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void addPrefabValues(
        final nl.jqno.equalsverifier.EqualsVerifier<?> verifier) {

        final CcmObject object1 = new CcmObject();
        object1.setDisplayName("Object 1");

        final CcmObject object2 = new CcmObject();
        object2.setDisplayName("Object 2");

        final Role role1 = new Role();
        role1.setName("Role 1");

        final Role role2 = new Role();
        role2.setName("Role 2");

        final User user1 = new TestUser();
        user1.setName("user1");

        final User user2 = new TestUser();
        user2.setName("user2");

        final Group group1 = new Group();
        group1.setName("group1");

        final Group group2 = new Group();
        group2.setName("group2");
        
          final Category category1 = new Category();
        category1.setObjectId(-4100);
        category1.setDisplayName("Category 1");

        final Category category2 = new Category();
        category2.setObjectId(-4200);
        category2.setDisplayName("Category 2");
        
        final ContentItem item1 = new ContentItem();
        item1.setDisplayName("item1");
        
        final ContentItem item2 = new ContentItem();
        item2.setDisplayName("item2");
        
        final ItemAttachment<Asset> itemAttachment1 = new ItemAttachment<>();
        itemAttachment1.setUuid(UUID.randomUUID().toString());

        final ItemAttachment<Asset> itemAttachment2 = new ItemAttachment<>();
        itemAttachment2.setUuid(UUID.randomUUID().toString());

        verifier
            .withPrefabValues(CcmObject.class, object1, object2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(User.class, user1, user2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(Category.class, category1, category2)
            .withPrefabValues(ContentItem.class, item1, item2)
            .withPrefabValues(ItemAttachment.class, 
                              itemAttachment1, 
                              itemAttachment2);

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
