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
package com.arsdigita.bebop;

import com.arsdigita.globalization.GlobalizedMessage;
import javax.servlet.ServletException;

/**
 * This class represents exceptions that occur within the processing methods
 * of any of the form event listeners. Typically the code will catch specific
 * exceptions such as <code>SQLException</code> and rethrow them as instances
 * of this class to pass the message to the controller in a standard fashion.
 *
 * <p>Since this class is a subclass of <code>ServletException</code>, servlets
 * that do form processing within a <code>doPost</code> or <code>doGet</code>
 * methods do not need to explicitly catch instances of this class. However, 
 * they may wish to do so for special error reporting to the user, or to notify 
 * the webmaster via e-mail of the problem.
 *
 * @version $Id$
 */

public class FormProcessException extends ServletException {

    /** Globalized version of the exception message, intended for output in the UI */
    private GlobalizedMessage m_globalizedMessage;
    
    /**
     * Constructor using a String as message presented to the user.
     * @param message
     * @deprecated Use FormProcessException(GlobalizedMessage) instead. The
     *             error message for the user should always be globalized so it
     *             can be transformed to the current users requested language.
     */
    public FormProcessException(String message) {
        super(message);
    }
    
    /**
     * Constructor using both types of messages which may be presented to the
     * user. It's a kind of fallback just in kind we really need a non-
     * globalized message. Usage is stropngly discouraged.
     * @param message
     * @param globalizedMessage 
     */
    public FormProcessException(String message, 
                                GlobalizedMessage globalizedMessage) {
        super(message);
        m_globalizedMessage = globalizedMessage;
    }
    
    /**
     * Constructor using a GlobalizedMessage as the error text presented to the
     * user. Using this constructor is the strongly recommended way!
     * 
     * @param globalizedMessage 
     */
    public FormProcessException(GlobalizedMessage globalizedMessage) {
        super();
        m_globalizedMessage = globalizedMessage;
    }

    /**
     * 
     * @param message
     * @param rootCause 
     * @deprecated use FormProcessException(String,GlobalizedMessage,Throwable)
     *             instead 
     */
    public FormProcessException(String message, 
                                Throwable rootCause) {
        super(message, rootCause);
    }

    public FormProcessException(String message, 
                                GlobalizedMessage globalizedMessage,
                                Throwable rootCause) {
        super(message, rootCause);
        m_globalizedMessage = globalizedMessage;
    }

    public FormProcessException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * Add a globalized version of the exception message just in case a non-
     * globalized message enabled constructor has been used.
     * 
     * @param globalizedMessage the globalized message intended for output in UI
     */
    public void setGlobalizedMessage(GlobalizedMessage globalizedMessage) {
        m_globalizedMessage = globalizedMessage;
    }

    /**
     * Retrieve the globalized version of the exception message, intended for
     * use in the UI widgets.
     * The standard non-globalizatin enabled exception message is for use in
     * log entries only!
     * 
     * @return the globalized message intended for output in UI
     */
    GlobalizedMessage getGlobalizedMessage() {
        return m_globalizedMessage;
    }
    /**
     * In addition to printing the stack trace for this exception, also prints
     * the stack trace for the root cause, if any.  This is a workaround for
     * those implementations of {@link ServletException} that don't implement
     * <code>printStackTrace</code> correctly.  If you happen to use an
     * implementation that does, the stack trace for the root cause may be
     * printed twice, which is not that big of a deal in the grand scheme of
     * things.
     */
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (getRootCause() != null) {
            System.err.print("Root cause: ");
            getRootCause().printStackTrace();
        }
    }

    /**
     * @param s
     * @see #printStackTrace()
     */
    @Override
    public void printStackTrace(java.io.PrintStream s) {
        super.printStackTrace(s);
        if (getRootCause() != null) {
            s.println("Root cause: ");
            getRootCause().printStackTrace(s);
        }
    }

    /**
     * @param s
     * @see #printStackTrace()       
     */
    @Override
    public void printStackTrace(java.io.PrintWriter s) {
        super.printStackTrace(s);
        if (getRootCause() != null) {
            s.println("Root cause: ");
            getRootCause().printStackTrace(s);
        }
    }

    /**
     * <p>Returns the concatenation of {@link #getMessage()} and {@link
     * #getRootCause()}.<code>getMessage()</code>.</p>
     * @return 
     **/
    public String getMessages() {
        StringBuilder result = new StringBuilder(getMessage());
        if ( getRootCause() != null ) {
            result.append(" (root cause: ")
                .append(getRootCause().getMessage())
                .append(")");
        }
        return result.toString();
    }
}
