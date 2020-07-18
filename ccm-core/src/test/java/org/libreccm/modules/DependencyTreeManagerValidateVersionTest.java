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
package org.libreccm.modules;


import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DependencyTreeManagerValidateVersionTest {

    public DependencyTreeManagerValidateVersionTest() {
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
    public void validateVersions() throws NoSuchMethodException,
                                          IllegalAccessException,
                                          IllegalArgumentException,
                                          InvocationTargetException {
        final DependencyTreeManager treeManager = new DependencyTreeManager();
        final Method validate = treeManager.getClass().getDeclaredMethod(
            "validateVersion",
            String.class,
            String.class,
            String.class);
        
        validate.setAccessible(true);

        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             "1.0.0", 
                                             "1.0.0"),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             "1.0.0", 
                                             ""),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             "1.0.0", 
                                             null),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             "", 
                                             "1.0.0"),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             null, 
                                             "1.0.0"),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             "", 
                                             ""),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.0.0", 
                                             null, 
                                             null),
                   is(true));
        
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.7.5", 
                                             "1.0.0", 
                                             ""),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.7.5-beta.3", 
                                             "1.0.0", 
                                             ""),
                   is(true));
        
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.7.5", 
                                             "1.0.0", 
                                             "1.7.9"),
                   is(true));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.7.5-final", 
                                             "1.7.5", 
                                             ""),
                   is(true));
        
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.5.3",
                                             "1.6.0",
                                             ""),
                   is(false));
        assertThat((boolean) validate.invoke(treeManager, 
                                             "1.6.0",
                                             "1.5.0",
                                             "1.5.9"),
                   is(false));
    }

}
