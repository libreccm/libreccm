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
package org.libreccm.core.modules;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DependencyException extends Exception {

    private static final long serialVersionUID = 10938361383494729L;

    /**
     * Creates a new instance of <code>DependencyException</code> without detail
     * message.
     */
    public DependencyException() {
        super();
        //Nothin more
    }

    /**
     * Constructs an instance of <code>DependencyException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DependencyException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>DependencyException</code> which wraps
     * the specified exception.
     *
     * @param exception The exception to wrap.
     */
    public DependencyException(final Exception exception) {
        super(exception);
    }

    /**
     * Constructs an instance of <code>DependencyException</code> with the
     * specified message which also wraps the specified exception.
     *
     * @param msg The detail message.
     * @param exception The exception to wrap.
     */
    public DependencyException(final String msg, final Exception exception) {
        super(msg, exception);
    }

}
