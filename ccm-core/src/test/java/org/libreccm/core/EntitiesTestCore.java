/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                                        InvocationTargetException {
        final Object obj = entityClass.class.getDeclaredConstructor().newInstance();

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
