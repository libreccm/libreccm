/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.core;

/**
 * An unchecked Exception to indicate unexpected errors.
 *
 * This exception should be used for the following purposes:
 *
 * <ul>
 * <li>To indicate that something unexpected has happened and the problem is not
 * recoverable <strong>and</strong> there is not other, better way to inform the
 * user about the problem. One example cloud that can object which was selected
 * in a form is not found in the database although it was there only seconds
 * ago.</li>
 * <li>To wrap an checked exception if the error is not recoverable and there is
 * no better way for informing the user about the problem.</li>
 * </ul>
 *
 * In general you should avoid using this exception in the action listeners of
 * Bebop forms. Bebop forms have an built-in error display function which should
 * be used instead of simply throwing an exception.
 *
 * This exception also replaces
 * {@code com.arsdigita.util.UncheckedWrapperException}. Especially in new code
 * {@code UncheckedWrapperException} should not be used anymore..
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UnexpectedErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>UnexpectedErrorException</code> without
     * detail message.
     */
    public UnexpectedErrorException() {
        super();
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> with the
     * specified detail message.
     *
     * @param msg The detail message.
     */
    public UnexpectedErrorException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> which
     * wraps the specified exception.
     *
     * @param exception The exception to wrap.
     */
    public UnexpectedErrorException(final Exception exception) {
        super(exception);
    }

    /**
     * Constructs an instance of <code>UnexpectedErrorException</code> with the
     * specified message which also wraps the specified exception.
     *
     * @param msg       The detail message.
     * @param exception The exception to wrap.
     */
    public UnexpectedErrorException(final String msg, final Exception exception) {
        super(msg, exception);
    }

}
