/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.libreccm.core;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Assert;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Verifies the implementions of {@code hashCode}, {@code equals} and
 * {@code toString}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EntitiesTestCore {

    private final Class<?> entityClass;

    public EntitiesTestCore(final Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Test
    public void verifyEqualsAndHashCode() {
        EqualsVerifier
            .forClass(entityClass)
            .suppress(Warning.STRICT_INHERITANCE)
            .suppress(Warning.NONFINAL_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }

    @Test
    public void verifyToString() throws IntrospectionException,
                                        InstantiationException,
                                        IllegalAccessException,
                                        IllegalArgumentException,
                                        NoSuchMethodException,
                                        InvocationTargetException {
        final Object obj = entityClass.getDeclaredConstructor().newInstance();

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
                    + "is not null safe:\n %s",
                entityClass.getName(),
                strWriter.toString()));

        }
    }

}
