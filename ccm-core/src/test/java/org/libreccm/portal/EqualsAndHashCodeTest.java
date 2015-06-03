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
package org.libreccm.portal;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.libreccm.core.Resource;
import org.libreccm.tests.categories.UnitTest;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RunWith(Parameterized.class)
@Category(UnitTest.class)
public class EqualsAndHashCodeTest {

    private final Class<?> entityClass;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Class<?>> data() {
        return Arrays.asList(new Class<?>[]{
            Portal.class,
            Portlet.class
        });
    }

    public EqualsAndHashCodeTest(final Class<?> entityTest) {
        this.entityClass = entityTest;
    }

    @Test
    public void verifyEqualsAndHashCode() {
        final Portal portal1 = new Portal();
        portal1.setDisplayName("Portal One");
        
        final Portal portal2 = new Portal();
        portal2.setDisplayName("Portal Two");
        
        final Resource resource1 = new Resource();
        resource1.setDisplayName("Resource One");
        
        final Resource resource2 = new Resource();
        resource2.setDisplayName("Resource Two");
        
        EqualsVerifier
            .forClass(entityClass)
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withRedefinedSuperclass()
            .withPrefabValues(Portal.class, portal1, portal2)
            .withPrefabValues(Resource.class, resource1, resource2)
            .verify();
    }

}
