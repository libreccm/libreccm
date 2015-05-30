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
package org.libreccm.jpautils;

import nl.jqno.equalsverifier.Warning;
import org.junit.Test;
import org.junit.runners.Parameterized;

/**
 * A base class for verifying the implementations of the {@code equals()}
 * and {@code hashCode} methods of an object using the {@link Parameterized}
 * test runner from JUnit.
 * 
 * To use this class create a new JUnit test class which extends this class
 * and which uses the {@link Parameterized} test runner. The class must have a
 * static method which provides the classes to be tested. Example for testing
 * the classes {@code Foo} and {@code Bar} (imports have been omitted):
 * 
 * <pre>
 * @RunWith(Parameterized.class)
 * @Category(UnitTest.class)
 * public class FooBarTest extends EqualsVerifier {
 * 
 *     @Parameterized.Parameters(name = "{0}")
 *     public static Collection<Class<?>> data() {
 *         return Arrays.asList(new Class<?>[] {
 *             Foo.class,
 *             Bar.class
 *         }); 
 *     }
 * 
 *     public FooBarTest(final Class<?> entityClass) {
 *         super(entityClass);
 *     }
 * }
 * </pre>
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EqualsVerifier {
    
    private final Class<?> entityClass;
    
    public EqualsVerifier(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Test
    public void verifyEqualsAndHashCode() {
        nl.jqno.equalsverifier.EqualsVerifier
            .forClass(entityClass)
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
