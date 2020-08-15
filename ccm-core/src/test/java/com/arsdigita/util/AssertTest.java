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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
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

    @Test(expected = AssertionError.class)
    public void checkFailMessage() {
        Assert.fail("errormessage");
    }

    @Test(expected = AssertionError.class)
    public void checkFail() {
        Assert.fail();
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
     * Check if {@link Assert#isTrue(boolean, java.lang.String)} succeeds if
     * condition evaluates to {@code true}.
     */
    @Test
    public void checkIsTrue() {
        Assert.isTrue(true);
    }

    /**
     * Check if {@link Assert#isTrue(boolean, java.lang.String)} fails if
     * condition evaluates to {@code false}.
     */
    @Test(expected = AssertionError.class)
    public void checkIfIsTrueWithMessageFails() {
        Assert.isTrue(false, "Expected true");
    }

    /**
     * Check if {@link Assert#isTrue(boolean, java.lang.String)} succeeds if
     * condition evaluates to {@code true}.
     */
    @Test
    public void checkIsTrueWithMessage() {
        Assert.isTrue(true, "Expected true");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsFalseFails() {
        Assert.isFalse(true);
    }
    
    @Test
    public void checkIsFalse() {
        Assert.isFalse(false);
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsFalseWithMessageFails() {
        Assert.isFalse(true, "Expected false");
    }
    
    @Test
    public void checkIsFalseWithMessage() {
        Assert.isFalse(false, "Expected true");
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

    @Test
    public void checkExistsWithClass() {
        Assert.exists("foo", Object.class);
    }

    @Test(expected = AssertionError.class)
    public void checkIfExistsFailsForNullWithMessage() {
        Assert.exists(null, "None null object expected");
    }

    @Test
    public void checkExistsWithLabel() {
        Assert.exists("foo", "label");
    }

    @Test
    public void checkExistsWithEmptyLabel() {
        Assert.exists("foo", "");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsLockedFails() {
        final Lockable unlocked = new LockableImpl();

        Assert.isLocked(unlocked);
    }

    @Test
    public void checkIfIsLockedDoesntThrowError() {
        Assert.isLocked(null);
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsUnLockedFails() {
        final Lockable locked = new LockableImpl();
        locked.lock();

        Assert.isUnlocked(locked);
    }

    @Test
    public void checkIfIsUnLockedDoesntThrowError() {
        Assert.isUnlocked(null);
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
    public void checkIfIsEqualFailsForObjectAndNullWithMessage() {
        Assert.isEqual("foo", null, "second object is null");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsEqualFailsForNullAndObjectWithMessage() {
        Assert.isEqual(null, "foo", "first object is null");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsEqualFailsForUnequalObjectsWithMessage() {
        Assert.isEqual("foo", "bar", "unequal ");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsNotEqualFailsForEqualObjects() {
        Assert.isNotEqual("foo", "foo");
    }

    @Test(expected = AssertionError.class)
    public void checkIfIsNotEqualFailsIfBothAreNull() {
        Assert.isNotEqual(null, null);
    }

    @Test(expected = AssertionError.class)
    public void checkIfAssertEqualsFailsIfObjectIsNull() {
        Assert.assertEquals(null, "bar", "expectedLabel", "actualLabel");
    }

    @Test(expected = AssertionError.class)
    public void checkIfAssertEqualsFails() {
        Assert.assertEquals("foo", "bar", "expectedLabel", "actualLabel");
    }

    @Test
    public void checkAssertEqualsIfNotEnabled() {
        Assert.setEnabled(false);
        Assert.assertEquals("foo", "bar", "expectedLabel", "actualLabel");
        Assert.setEnabled(true);
    }

    @Test
    public void checkAssertEqualsIfBothNull() {
        Assert.assertEquals(null, null, "expectedLabel", "actualLabel");
    }

    @Test
    public void checkAssertEqualsWithEqualObjects() {
        Assert.assertEquals("foo", "foo", "expectedLabel", "actualLabel");
    }
}
