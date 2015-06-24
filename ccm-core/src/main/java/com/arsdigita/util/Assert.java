/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import org.apache.log4j.Logger;

/**
 * Utility functions for assertions.
 *
 * <p>The static methods in this class provide a standard way of
 * asserting certain conditions.</p>
 *
 * <p>Though it is not right now, this class <em>should</em> be final.
 * Do not subclass it.  In a future revision of this software, this
 * class will be made final.</p>
 *
 * @author David Lutterkort &lt;dlutter@redhat.com&gt;
 * @author Uday Mathur
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class Assert {

    /** Class specific logger instance. */
    private static final Logger s_log = Logger.getLogger(Assert.class);
        
    private static final String DEFAULT_MESSAGE = "Assertion failure";

    /**
     * Configuration by a system wide / servlet container wide environment
     * variable is evil and no longer supported API.
     * Currently Assertion is set alway on. The non-deprecated methods of this
     * class don't check for isEnabled anyway. So there is no effect.
     * If configurability is really needed, configuration by config registry
     * during system setup (eg. @link com.arsdigita.runtime.CCMResourceManager)
     * has to be implemented
     */
    private static boolean s_enabled = true;
//    private static boolean s_enabled;
//
//    static {
//        final String enabled = System.getProperty
//	                                     (Assert.class.getName() + ".enabled");
//
//        if (enabled == null) {
//            //s_enabled = false;
//            //default value is true
//            s_enabled = true;
//        } else {
//            // s_enabled = enabled.equals("true");
//            // Test: set to true anyway.
//            // TODO: read from config registry, any dependency from a
//            // environment variable is deprecated!
//            s_enabled = true;
//        }
//    }

    /**
     * Tells whether asserts are turned on.  Use this to wrap code
     * that should be optimized away if assertions are disabled.
     *
     * By default, assertions are disabled
     */
    public static final boolean isEnabled() {
        return s_enabled;
    }

    static final void setEnabled(final boolean enabled) {
        s_enabled = enabled;
    }

    /**
     * Throws an error.
     *
     * @param message A <code>String</code> describing the failure
     * condition
     * @throws AssertionError
     */
    public static final void fail(final String message) {
        error(message);

        throw new AssertionError(message);
    }

    /**
     * Throws an error.
     *
     * @throws AssertionError
     */
    public static final void fail() {
        error(DEFAULT_MESSAGE);

        throw new AssertionError(DEFAULT_MESSAGE);
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws AssertionError if the condition is false
     */
    public static final void isTrue(final boolean condition,
                                    final String message) {
        if (!condition) {
            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws AssertionError if the condition is false
     * @deprecated use Assert.isTrue(condition, message) instead
     *             (we will follow the standard naming scheme)
     */
    public static final void truth(final boolean condition,
                                   final String message) {
        Assert.isTrue(condition, message);


        // if (!condition) {
        //     error(message);
        //
        //     throw new AssertionError(message);
        // }
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @throws AssertionError if the condition is false
     */
    public static final void isTrue(final boolean condition) {
        if (!condition) {
            error(DEFAULT_MESSAGE);

            throw new AssertionError(DEFAULT_MESSAGE);
        }
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @throws AssertionError if the condition is false
     * @deprecated use Assert.isTrue(final boolean condition) instead
     *             (we will follow the standard naming scheme)
     */
    public static final void truth(final boolean condition) {
        Assert.isTrue(condition);

        // if (!condition) {
        //     error(DEFAULT_MESSAGE);
        //
        //    throw new AssertionError(DEFAULT_MESSAGE);
        // }
    }

    /**
     * Asserts that an arbitrary condition is false and throws an
     * error if the condition is true.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws AssertionError if the condition is false
     */
    public static final void isFalse(final boolean condition,
                                     final String message) {
        if (condition) {
            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that an arbitrary condition is false and throws an
     * error if the condition is true.
     *
     * @param condition The condition asserted
     * @throws AssertionError if the condition is false
     */
    public static final void isFalse(final boolean condition) {
        if (condition) {
            error(DEFAULT_MESSAGE);

            throw new AssertionError(DEFAULT_MESSAGE);
        }
    }

    /**
     * Asserts that an object is not null.
     *
     * @param object The object that must not be null
     * @param clacc The <code>Class</code> of parameter
     * <code>object</code>
     * @throws AssertionError if the object is null
     */
    public static void exists(final Object object,
                              final Class clacc) {
        if (object == null) {
            final String message = clacc.getName() + " is null";

            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that an object is not null.
     *
     * @param object The <code>Object</code> that must not be null
     * @param label A text to describe <code>Object</code>
     *
     * @throws AssertionError if the object is null
     */
    public static void exists(final Object object,
                              final String label) {
        if (object == null) {
            final String message =
                    label != null && label.trim().length() > 0
                        ? "Value of " + label + " is null." 
                        : DEFAULT_MESSAGE ;

            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that an object is not null.
     *
     * @param object The object that must not be null
     * @throws AssertionError if the object is null
     */
    public static final void exists(final Object object) {
        if (object == null) {
            error(DEFAULT_MESSAGE);

            throw new AssertionError(DEFAULT_MESSAGE);
        }
    }

    /**
     * Verifies that <code>Lockable</code> is locked and throws an
     * error if it is not.
     *
     * @param lockable The object that must be locked
     * @see com.arsdigita.util.Lockable
     */
    public static final void isLocked(final Lockable lockable) {
        if (lockable != null && !lockable.isLocked()) {
            final String message = "Illegal access: " + lockable +
                                   " is not locked";

            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Verifies that <code>lockable</code> is <em>not</em> locked and
     * throws an error if it is.
     *
     * @param lockable The object that must not be locked
     * @see com.arsdigita.util.Lockable
     */
    public static final void isUnlocked(final Lockable lockable) {
        if (lockable != null && lockable.isLocked()) {
            final String message = "Illegal access: " + lockable + " is locked.";

            error(message);

            throw new AssertionError(message);
        }
    }

    /**
     * Verifies that two values are equal (according to their equals
     * method, unless <code>value1</code> is null, then according to
     * <code>==</code>).
     *
     * @param value1 The first value to be compared
     * @param value2 The second
     * @throws AssertionError if the arguments are unequal
     */
    public static final void isEqual(final Object value1,
                                   final Object value2) {
        if (value1 == null) {
            if (value1 != value2) {
                final String message = value1 + " does not equal " + value2;

                error(message);

                throw new AssertionError(message);
            }
        } else {
            if (!value1.equals(value2)) {
                final String message = value1 + " does not equal " + value2;

                error(message);

                throw new AssertionError(message);
            }
        }
    }

    /**
     * Verifies that two values are equal (according to their equals
     * method, unless <code>value1</code> is null, then according to
     * <code>==</code>).
     *
     * @param value1 The first value to be compared
     * @param value2 The second
     * @throws AssertionError if the arguments are unequal
     */
    public static final void isEqual(final Object value1,
                                   final Object value2,
                                   final String message) {
        if (value1 == null) {
            if (value1 != value2) {
                error(message);

                throw new AssertionError(message);
            }
        } else {
            if (!value1.equals(value2)) {
                error(message);

                throw new AssertionError(message);
            }
        }
    }

    /**
     * Verifies that two values are not equal (according to their
     * equals method, unless <code>value1</code> is null, then
     * according to <code>==</code>).
     *
     * @param value1 The first value to be compared
     * @param value2 The second
     * @throws AssertionError if the arguments are isNotEqual
     */
    public static final void isNotEqual(final Object value1,
                                     final Object value2) {
        if (value1 == null) {
            if (value1 == value2) {
                final String message = value1 + " equals " + value2;

                error(message);

                throw new AssertionError(message);
            }
        } else {
            if (value1.equals(value2)) {
                final String message = value1 + " equals " + value2;

                error(message);

                throw new AssertionError(message);
            }
        }
    }
    
    // Utility methods

    private static void error(final String message) {
        // Log the stack trace too, since we still have code that
        // manages to hide exceptions.
        s_log.error(message, new AssertionError(message));
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    // The methods below are all deprecated.                                 //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @deprecated in favor of {@link #isEnabled()}
     *
     * pboy Jan.09: not used by any package in trunk
     */
    // public static final boolean ASSERT_ON = true;

    /**
     * Indicates state of the ASSERT_ON flag.
     *
     * pboy Jan.09: not used by any package in trunk
     *
     * @deprecated Use {@link #isEnabled()} instead
     */
    // public static final boolean isAssertOn() {
    //     return isEnabled();
    // }

//  /**
//   * Tells whether asserts are turned on.  Use this to wrap code
//   * that should be optimized away if asserts are disabled.
//   *
//   * @deprecated Use {@link #isEnabled()} instead
//   */
//  public static final boolean isAssertEnabled() {
//      return isEnabled();
//  }

    /**
     * Assert that an arbitrary condition is true, and throw an
     * exception if the condition is false.
     *
     * @param cond condition to assert
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #truth(boolean, String)} instead
     */
/*  public static final void isTrue(boolean cond) {
        isTrue(cond, "");
    }
*/
    /**
     * Assert that an arbitrary condition is true, and throw an
     * exception with message <code>msg</code> if the condition is
     * false.
     *
     * @param cond condition to assert
     * @param msg failure message
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #truth(boolean,String)} instead
     */
/*  public static final void assertTrue(boolean cond, String msg) {
        if (!cond) {
            error(msg);

            throw new IllegalStateException(msg);
        }
    }
*/
    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws AssertionError if the condition is false
     * @deprecated use Assert.isTrue(condition, message) instead
     *             (we will follow the standard naming scheme)
     */
/*  public static final void truth(final boolean condition,
                                   final String message) {
        Assert.isTrue(condition, message);

        // if (!condition) {
        //     error(message);
        //
        //     throw new AssertionError(message);
        // }
    }
*/

        /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @throws AssertionError if the condition is false
     * @deprecated use Assert.isTrue(final boolean condition) instead
     *             (we will follow the standard naming scheme)
     */
/*  public static final void truth(final boolean condition) {
        Assert.isTrue(condition);

        // if (!condition) {
        //     error(DEFAULT_MESSAGE);
        //
        //    throw new AssertionError(DEFAULT_MESSAGE);
        // }
    }
*/

    /**
     * Asserts that an arbitrary condition is false and throws an
     * error if the condition is true.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws AssertionError if the condition is false
     */
/*  public static final void falsity(final boolean condition,
                                     final String message) {
        if (condition) {
            error(message);

            throw new AssertionError(message);
        }
    }
*/

    /**
     * Asserts that an arbitrary condition is false and throws an
     * error if the condition is true.
     *
     * @param condition The condition asserted
     * @throws AssertionError if the condition is false
     */
/*  public static final void falsity(final boolean condition) {
        if (condition) {
            error(DEFAULT_MESSAGE);

            throw new AssertionError(DEFAULT_MESSAGE);
        }
    }
*/
    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     *
     * @deprecated Use {@link #exists(Object)} instead
     */
/*  public static final void assertNotNull(Object o) {
        assertNotNull(o, "");
    }
*/
    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     *
     * @deprecated Use {@link #exists(Object,String)} instead
     */
/*  public static final void assertNotNull(Object o, String label) {
        if (isEnabled()) {
            isTrue(o != null, "Value of " + label + " is null.");
        }
    }
*/
//  /**
//   * Verify that a string is not empty and throw a runtime exception
//   * if so.  A parameter is considered empty if it is null, or if it
//   * does not contain any characters that are non-whitespace.
//   *
//   * @deprecated with no replacement
//   */
//  public static final void assertNotEmpty(String s) {
//      assertNotEmpty(s, "");
//  }

    /**
     * Verify that a string is not empty and throw a runtime exception
     * if so.  A parameter is considered empty if it is null, or if it
     * does not contain any characters that are non-whitespace.
     *
     * @deprecated with no replacement
     */
    public static final void assertNotEmpty(String s, String label) {
        if (isEnabled()) {
            isTrue(s != null && s.trim().length() > 0,
                       "Value of " + label + " is empty.");
        }
    }

    /**
     * Verify that two values are equal (according to their equals method,
     * unless expected is null, then according to ==).
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #isEqual(Object,Object)} instead
     */
    public static final void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "expected", "actual");
    }

    /**
     * Verify that two values are equal (according to their equals method,
     * unless expected is null, then according to ==).
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @param expectedLabel Label for first (generally expected) value.
     * @param actualLabel Label for second (generally actual) value.
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertEquals(Object expected, 
                                          Object actual,
                                          String expectedLabel,
                                          String actualLabel) {
        if (isEnabled()) {
            if (expected == null) {
                isTrue(expected == actual,
                           "Values not equal, " +
                           expectedLabel + " '" + expected + "', " +
                           actualLabel + " '" + actual + "'");
            } else {
                isTrue(expected.equals(actual),
                           "Values not equal, " +
                           expectedLabel + " '" + expected + "', " +
                           actualLabel + " '" + actual + "'");
            }
        }
    }

    /**
     * Verify that two values are equal.
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #truth(boolean, String)} instead
     */
    public static final void assertEquals(int expected, int actual) {
        assertEquals(expected, actual, "expected", "actual");
    }

    /**
     * Verify that two values are equal.
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @param expectedLabel Label for first (generally expected) value.
     * @param actualLabel Label for second (generally actual) value.
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #truth(boolean, String)} instead
     */
    public static final void assertEquals(int expected, int actual,
                                          String expectedLabel,
                                          String actualLabel) {
        if (isEnabled()) {
            isTrue(expected == actual,
                       "Values not equal, " +
                       expectedLabel + " '" + expected + "', " +
                       actualLabel + " '" + actual + "'");
        }
    }

    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is not locked.
     *
     * @deprecated Use {@link #isLocked(Lockable)} instead
     */
/*  public static void assertLocked(Lockable l) {
        if (isEnabled()) {
            isTrue(l.isLocked(),
                       "Illegal access to an unlocked " +
                       l.getClass().getName());
        }
    }
*/
    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is locked.
     *
//   * @deprecated Use {@link #isUnlocked(Lockable)} instead
     */
/*  public static void assertUnlocked(Lockable l) {
        if (isEnabled()) {
            isTrue(!l.isLocked(),
                       "Illegal access to a isLocked " +
                       l.getClass().getName());
        }
    }
*/
    /**
     * Verifies that <code>lockable</code> is <em>not</em> isLocked and
     * throws an error if it is.
     *
     * @param lockable The object that must not be isLocked
     * @see com.arsdigita.util.Lockable
//   * @deprecated  use isUnlocked(Lockable) instead
     */
/*  public static final void unlocked(final Lockable lockable) {
        if (lockable != null && lockable.isLocked()) {
            final String message = "Illegal access: " + lockable + " is isLocked.";

            error(message);

            throw new AssertionError(message);
        }
    }
*/


}
