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

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.core.CcmObject;
import org.libreccm.security.Group;
import org.libreccm.security.Role;
import org.libreccm.tests.categories.UnitTest;
import org.libreccm.testutils.EqualsVerifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

/**
 * Verifies the {@code equals} and {@code hashCode} methods of the classes
 * {@link AttachmentsConfig}, {@link AttachmentList} and {@link ItemAttachment}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@org.junit.experimental.categories.Category(UnitTest.class)
public class AttachmentsEqualsAndHashCodeTest extends EqualsVerifier {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            AttachmentsConfig.class,
            ItemAttachment.class,
            AttachmentList.class
        });
    }

    public AttachmentsEqualsAndHashCodeTest(final Class<?> clazz) {
        super(clazz);
    }

    @Override
    protected void addPrefabValues(final SingleTypeEqualsVerifierApi<?> verifier) {

        final ContentSection section1 = new ContentSection();
        section1.setDisplayName("section1");

        final ContentSection section2 = new ContentSection();
        section1.setDisplayName("section2");

        final ContentItem item1 = new ContentItem();
        item1.setDisplayName("item1");

        final ContentItem item2 = new ContentItem();
        item2.setDisplayName("item2");

        final ItemAttachment<Asset> itemAttachment1 = new ItemAttachment<>();
        itemAttachment1.setUuid(UUID.randomUUID().toString());

        final ItemAttachment<Asset> itemAttachment2 = new ItemAttachment<>();
        itemAttachment2.setUuid(UUID.randomUUID().toString());

        final CcmObject object1 = new CcmObject();
        object1.setDisplayName("object1");

        final CcmObject object2 = new CcmObject();
        object2.setDisplayName("object2");

        final Role role1 = new Role();
        role1.setName("role1");

        final Role role2 = new Role();
        role1.setName("role2");
        
        final Group group1 = new Group();
        group1.setName("group1");
        
        final Group group2 = new Group();
        group1.setName("group2");
        
        final Asset asset1 = new Asset();
        asset1.setDisplayName("asset1");
        
        final Asset asset2 = new Asset();
        asset2.setDisplayName("asset2");

        verifier
            .withPrefabValues(ContentSection.class, section1, section2)
            .withPrefabValues(ContentItem.class, item1, item2)
            .withPrefabValues(ItemAttachment.class,
                              itemAttachment1,
                              itemAttachment2)
            .withPrefabValues(CcmObject.class, object1, object2)
            .withPrefabValues(Role.class, role1, role2)
            .withPrefabValues(Group.class, group1, group2)
            .withPrefabValues(Asset.class, asset1, asset2);
    }

}
