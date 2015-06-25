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
package com.arsdigita.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.libreccm.tests.categories.UnitTest;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Category(UnitTest.class)
public class AssertTest {
    
    public AssertTest() {
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

    /**
     * Check if {@link Assert#setEnabled(boolean)} works correctly.
     */
    @Test
    public void assertDisable() {
        Assert.setEnabled(false);
        
        assertThat(Assert.isEnabled(), is(false));
        
        Assert.setEnabled(true);
    }
    
    /**
     * Check if {@link Assert#isTrue(boolean)} fails if condition evaluates to
     * {@code false}.
     */
    @Test(expected = AssertionError.class)
    public void checkIfIsTrueFails() {
        Assert.isTrue(false);
    }
    
    /**
     * Check if {@link Assert#isTrue(boolean, java.lang.String)} fails if 
     * condition evaluates to {@code false}.
     */
    @Test(expected = AssertionError.class)
    public void checkIfIsTrueWithMessageFails() {
        Assert.isTrue(false, "Expected true");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsFalseFails() {
        Assert.isFalse(true);
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsFalseWithMessageFails() {
        Assert.isFalse(true, "Expected false");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfExistsFailsForNull() {
        Assert.exists(null);
    }
    
    @Test
    public void checkExists() {
        Assert.exists("foo");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfExistsWithClassFailsForNull() {
        Assert.exists(null, Object.class);
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfExistsFailsForNullWithMessage() {
        Assert.exists(null, "None null object extected");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsLockedFails() {
        final Lockable unlocked = new LockableImpl();
        
        Assert.isLocked(unlocked);
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsUnLockedFails() {
        final Lockable locked = new LockableImpl();
        locked.lock();
        
        Assert.isUnlocked(locked);
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsEqualFailsForUnequalObjects() {
        Assert.isEqual("foo", "bar");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsEqualFailsForNullAndObject() {
        Assert.isEqual(null, "bar");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsEqualFailsForObjectAndNull() {
        Assert.isEqual("foo", null);
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsNotEqualFailsForEqualObjects() {
        Assert.isNotEqual("foo", "foo");
    }
    
    @Test(expected = AssertionError.class)
    public void checkIfIsNotEqualFailsIfBothAreNull() {
        Assert.isNotEqual(null, null);
    }
}
