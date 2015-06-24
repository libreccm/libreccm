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
package com.arsdigita.util.parameter;

import com.arsdigita.util.Assert;

/**
 * Subject to change.
 *
 * Information about an error for a parameter.  Parameter implementors
 * will add <code>ParameterError</code>s to the passed in
 * <code>ErrorList</code> when their parameters encounter error
 * conditions.
 *
 * @see ErrorList
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public final class ParameterError {

    private final Parameter m_param;
    private final String m_message;
    private Throwable m_throwable;

    /**
     * Constructs a parameter error for <code>param</code>.
     *
     * @param param The <code>Parameter</code> whose value is in
     * error; it cannot be null
     * @param message A <code>String</code> description of the error
     */
    public ParameterError(final Parameter param,
                          final String message) {
        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(message, String.class);
        }

        m_param = param;
        m_message = message;
    }

    /**
     * Constructs a parameter error for <code>param</code>, drawing
     * its error message from <code>throwable</code>.
     *
     * @param param The <code>Parameter</code> whose value is in
     * error; it cannot be null
     * @param throwable The <code>Throwable</code> for the error; it
     * cannot be null
     */
    public ParameterError(final Parameter param,
                          final Throwable throwable) {
        this(param, throwable.getMessage());

        m_throwable = throwable;
    }

    /**
     * Gets the parameter associated with this error.
     *
     * @return The <code>Parameter</code> in error; it cannot be null
     */
    public final Parameter getParameter() {
        return m_param;
    }

    /**
     * Gets the message associated with this error.
     *
     * @return The <code>String</code> message for the error; it
     * cannot be null
     */
    public final String getMessage() {
        // XXX this actually can be null, so need to prevent that
        return m_message;
    }

    /**
     * Gets the throwable, if present, that corresponds to the error.
     *
     * @return The <code>Throwable</code> of this error; it may be
     * null
     */
    public final Throwable getThrowable() {
        return m_throwable;
    }

    /**
     * Returns a string representation of the error suitable for
     * debugging.
     *
     * @return <code>super.toString() + "," + param.getName()</code>
     */
    @Override
    public String toString() {
        return super.toString() + "," + m_param.getName();
    }
}
