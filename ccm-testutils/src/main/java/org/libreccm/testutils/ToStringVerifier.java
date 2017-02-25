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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * A base class for verifying the implementation of the {@code toString()}
 * method of an object using the {@link Parameterized} test runner from JUnit.
 *
 * To use this class create a new JUnit test class which extends this class and
 * which uses the {@link Parameterized} test runner. The class must have a
 * static method which provides the classes to be tested. Example for testing
 * the classes {@code Foo} and {@code Bar} (imports have been omitted):
 *
 * <pre>
 * <code>
 * &#x40;RunWith(Parameterized.class)
 * &#x40;Category(UnitTest.class) public class FooBarTest extends ToStringVerifier {
 *
 *     &#x40;Parameterized.Parameters(name = "{0}") public static Collection&lt;Class&lt;?&gt;&gt; data() {
 *         return Arrays.asList(new Class&lt;?&gt;[] { Foo.class, Bar.class });
 *     }
 *
 *     public FooBarTest(final Class&lt;?&gt; entityClass) {
 *         super(entityClass);
 *     }
 * }
 * </code>
 * </pre>
 *
 * An example can be found in the ccm-core module:
 * <a href="../../../../../ccm-core/xref-test/org/libreccm/core/ToStringTest.html"><code>ToStringTest</code></a>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ToStringVerifier {
    
    private final Class<?> entityClass;
    
    public ToStringVerifier(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }
    
    @Test
    //We want to test if there occurs an NPE therefore we need catch the NPE.
    @SuppressWarnings({"PMD.AvoidCatchingNPE",
                       "PMD.AvoidCatchingGenericException"})
    public void verifyToString() throws IntrospectionException,
                                        InstantiationException,
                                        IllegalAccessException,
                                        IllegalArgumentException,
                                        InvocationTargetException {
        final Object obj;
        try {
            final Constructor<?> constructor = entityClass.asSubclass(
                entityClass).getDeclaredConstructor();
            constructor.setAccessible(true);
            obj = constructor.newInstance();
        } catch (NoSuchMethodException ex) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);
            
            Assert.fail(String.format("Class \"%s\" does not provide a "
                                          + "parameterless constructor:%n%s",
                                      entityClass.getName(),
                                      stringWriter.toString()));
            return;
        }
        
        final Field[] fields = entityClass.getDeclaredFields();
        for (final Field field : fields) {
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
