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
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An exception to indicate invalid parameter states. This exception should only
 * be used when the client code of a parameter opts in to using exceptions
 * rather than handling parameter errors itself. See
 * {@link com.arsdigita.util.parameter.ErrorList#check()}.
 *
 * @see com.arsdigita.util.parameter.ErrorList
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public final class ParameterException extends RuntimeException {

    private static final Logger s_log = Logger.getLogger(
        ParameterException.class);
    private static final long serialVersionUID = 1726920836531266365L;

    private final ErrorList m_errors;

    /**
     * Constructs a new parameter exception with the content
     * <code>message</code>.
     *
     * @param message A <code>String</code> describing what's wrong; it cannot
     *                be null
     * @param errors  The <code>ErrorList</code> containing the errors that
     *                prompted this exception; it cannot be null
     */
    public ParameterException(final String message, final ErrorList errors) {
        super(message);

        if (Assert.isEnabled()) {
            Assert.exists(message, String.class);
            Assert.exists(errors, List.class);
        }

        m_errors = errors;
    }

    /**
     * Gets the set of errors associated with the exception.
     *
     * @return The <code>ErrorList</code> of errors; it cannot be null
     */
    public final ErrorList getErrors() {
        return m_errors;
    }

}
