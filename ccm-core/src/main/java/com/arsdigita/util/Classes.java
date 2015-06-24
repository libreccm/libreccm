/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * A collection of static utility methods for dealing with Java
 * classes.
 *
 * @author Justin Ross
 * @version $Id$
 */
public final class Classes {

    private static final Logger s_log = Logger.getLogger(Classes.class);

    /**
     * Loads a class from its fully qualified string name.
     *
     * @param clacc A fully qualified <code>String</code> naming
     * the class to be loaded
     */
    public static final Class loadClass(final String clacc) {
        Assert.exists(clacc, String.class);

        try {
            return Class.forName(clacc);
        } catch (ClassNotFoundException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    /**
     * Constructs a new instance of a class using the given
     * parameters.
     *
     * @param clacc The <code>Class</code> of which to make a new
     * instance
     * @param params A <code>Class[]</code> representing the arguments
     * of the desired constructor
     * @param values An <code>Object[]</code> of values to fill the
     * parameters
     */
    public static final Object newInstance(final Class clacc,
                                           final Class[] params,
                                           final Object[] values) {
        if (Assert.isEnabled()) {
            Assert.exists(clacc, Class.class);
            Assert.exists(params, Class.class);
            Assert.exists(values, Object.class);
            Assert.isTrue(params.length == values.length);
        }

        try {
            final Constructor constructor = clacc.getConstructor(params);

            return constructor.newInstance(values);
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (IllegalAccessException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (InvocationTargetException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        } catch (InstantiationException ex) {
            throw new UncheckedWrapperException
                (message(clacc, params, values), ex);
        }
    }

    private static String message(Class klass, Class[] params,
                                  Object[] values) {
        return "class = " + klass +
            ", params = " + message(params) +
            ", values = " + message(values);
    }

    private static String message(Object[] array) {
        if (array == null) {
            return "" + null;
        } else {
            return Arrays.asList(array).toString();
        }
    }

    /**
     * Constructs a new instance of the class referred to by
     * <code>clacc</code>.
     *
     * @param clacc The fully qualified <code>String</code>
     * clacc of the object you wish to instantiate
     * @param params A <code>Class[]</code> representing the arguments
     * of the desired constructor
     * @param values An <code>Object[]</code> of values to fill the
     * parameters
     */
    public static final Object newInstance(final String clacc,
                                           final Class[] params,
                                           final Object[] values) {
        return newInstance(loadClass(clacc), params, values);
    }

    /**
     * Creates a new instance of <code>clacc</code> using its no-args
     * constructor.  If the class has no such constructor, it throws a
     * runtime exception.
     *
     * @param clacc The class of which to create a new instance
     */
    public static final Object newInstance(final Class clacc) {
        return newInstance(clacc, new Class[0], new Object[0]);
    }

    /**
     * Creates a new instance of the class represented by
     * <code>clacc</code> using its no-args constructor.  If the class
     * has no such constructor, it throws a runtime exception.
     *
     * @param clacc The fully-qualified <code>String</code> name of
     * the class
     */
    public static final Object newInstance(final String clacc) {
        return newInstance(loadClass(clacc));
    }
}
