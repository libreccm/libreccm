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
package com.arsdigita.templating;

import com.arsdigita.util.UncheckedWrapperException;

import javax.xml.transform.TransformerException;

/**
 * A simple wrapper around <code>TransformerException</code> that provides a
 * more useful {@link #getMessage()} method.
 *
 * @author  Vadim Nasardinov
 * @since   2003-11-21
 * @version $Id$
 */
public final class WrappedTransformerException extends UncheckedWrapperException {
    private TransformerException m_trex;

    /**
     * The passed in <code>TransformerException</code> is retrievable later via
     * {@link #getRootCause()}.
     *
     * @pre ex != null
     **/
    public WrappedTransformerException(TransformerException ex) {
        super(ex);
        if ( ex == null ) { throw new NullPointerException("ex"); }
        m_trex = ex;
    }

    /**
     * @see #WrappedTransformerException(TransformerException)
     * @pre ex != null
     **/
    public WrappedTransformerException(String msg, TransformerException ex) {
        super(msg, ex);
        if ( ex == null ) { throw new NullPointerException("ex"); }
        m_trex = ex;
    }

    /**
     * The returned message includes the location information.
     **/
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        possiblyAppend(sb, super.getMessage());
        appendMessage(m_trex, sb);
        return sb.toString();
    }

    private static void appendMessage(TransformerException ex,
                                        StringBuffer sb) {

        if ( !possiblyAppend(sb, ex.getMessageAndLocation()) ) {
            possiblyAppend(sb, ex.getMessage());
            possiblyAppend(sb, ex.getLocationAsString());
        }
        if ( ex.getCause() instanceof TransformerException ) {
            appendMessage((TransformerException) ex.getCause(), sb);
        } else {
            if ( ex.getCause() != null ) {
                possiblyAppend(sb, ex.getCause().getMessage());
            }
        }
    }

    private static boolean possiblyAppend(StringBuffer sb, String str) {
        if ( str == null ) { return false; }
        sb.append(str).append("; ");
        return true;
    }
}
