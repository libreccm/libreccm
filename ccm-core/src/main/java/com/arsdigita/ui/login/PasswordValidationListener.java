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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;

/**
 * A NotNullValidationListener that checks whether the string value of a
 * parameter is a strong password.
 *
 * @author Sameer Ajmani
 */

public class PasswordValidationListener
    extends NotNullValidationListener
{
    /**
     * Minimum length for passwords.
     */
    public static final int MIN_LENGTH = 6;

    /**
     * Minimum number of character types for passwords.  Valid values are on
     * range [1,4].
     */
    public static final int MIN_TYPES = 1;

    public PasswordValidationListener() {
        super();
    }

    public PasswordValidationListener(String label) {
        super(label);
    }

    /**
     * Checks whether the string value of the parameter meets minimum-length
     * and composition requirements for strong passwords.
     */
    @Override
    public void validate(ParameterEvent e) {
        super.validate(e);

        ParameterData data = e.getParameterData();
        if (!data.isValid()) {
            // already has errors from superclass
            return;
        }

        // check length
        String password = data.getValue().toString();
        if (password.length() < MIN_LENGTH) {
            data.addError(LoginHelper.getMessage
                          ("login.passwordValidation.minLengthError",
                           new Object[] { new Integer(MIN_LENGTH) }));
            return;
        }

        // check for whitespace
        if (contains(password, whites)) {
            data.addError(LoginHelper.getMessage
                          ("login.passwordValidation.whitespaceError"));
            return;
        }

        // check character mix
        int charSets = 0;
        if (contains(password, uppers)) {
            charSets++;
        }
        if (contains(password, lowers)) {
            charSets++;
        }
        if (contains(password, digits)) {
            charSets++;
        }
        if (contains(password, others)) {
            charSets++;
        }
        if (charSets < MIN_TYPES) {
            data.addError(LoginHelper.getMessage
                          ("login.passwordValidation.minTypesError",
                           new Object[] { MIN_TYPES}));
        }
    }
    private static interface CharSet {
        public boolean contains(char c);
    }
    private static boolean contains(String s, CharSet set) {
        for (int i = 0; i < s.length(); i++) {
            if (set.contains(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    private static CharSet whites = new CharSet() {
            @Override
            public boolean contains(char c) {
                return Character.isWhitespace(c);
            }
        };
    private static CharSet uppers = new CharSet() {
            @Override
            public boolean contains(char c) {
                return (Character.isLetter(c)
                        && Character.isUpperCase(c));
            }
        };
    private static CharSet lowers = new CharSet() {
            @Override
            public boolean contains(char c) {
                return (Character.isLetter(c)
                        && Character.isLowerCase(c));
            }
        };
    private static CharSet digits = new CharSet() {
            @Override
            public boolean contains(char c) {
                return Character.isDigit(c);
            }
        };
    private static CharSet others = new CharSet() {
            @Override
            public boolean contains(char c) {
                return !(Character.isLetterOrDigit(c)
                         || Character.isISOControl(c)
                         || Character.isWhitespace(c));
            }
        };
}
