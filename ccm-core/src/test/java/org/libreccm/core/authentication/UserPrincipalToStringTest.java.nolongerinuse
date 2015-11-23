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
package org.libreccm.core.authentication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserPrincipalToStringTest {

    public UserPrincipalToStringTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void verifyToString() throws IllegalArgumentException,
                                        IllegalAccessException {
        final UserPrincipal principal = new UserPrincipal(null);

        final Field[] fields = principal.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                        && !field.getType().isPrimitive()) {
                field.setAccessible(true);
                field.set(principal, null);
            }
        }

        try {
            principal.toString();
        } catch (NullPointerException ex) {
            final StringWriter strWriter = new StringWriter();
            final PrintWriter writer = new PrintWriter(strWriter);
            ex.printStackTrace(writer);
            Assert.fail(String.format(
                    "toString() implementation of of class \"%s\" "
                            + "is not null safe:%n %s",
                    principal.getClass().getName(),
                    strWriter.toString()));

        }
    }
}
