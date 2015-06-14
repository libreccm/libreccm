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
package org.libreccm.testutils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.beans.IntrospectionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * A base class for verifying the implementation of the {@code toString()}
 * method of an object using the {@link Parameterized}
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
 * public class FooBarTest extends ToStringVerifier {
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
public class ToStringVerifier {
    
    private final Class<?> entityClass;
    
    public ToStringVerifier(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Test
    public void verifyToString() throws IntrospectionException,
                                        InstantiationException,
                                        IllegalAccessException,
                                        IllegalArgumentException,
                                        InvocationTargetException {
        final Object obj = entityClass.newInstance();

        final Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && !field.getType().isPrimitive()) {
                field.setAccessible(true);
                field.set(obj, null);
            }
        }

        try {
            obj.toString();
        } catch (NullPointerException ex) {
            final StringWriter strWriter = new StringWriter();
            final PrintWriter writer = new PrintWriter(strWriter);
            ex.printStackTrace(writer);
            Assert.fail(String.format(
                "toString() implemention of class \"%s\" "
                    + "is not null safe:%n %s",
                entityClass.getName(),
                strWriter.toString()));

        }
    }
    
}
