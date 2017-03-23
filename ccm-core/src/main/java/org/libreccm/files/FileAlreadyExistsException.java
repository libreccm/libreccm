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
package org.libreccm.files;

/**
 * Thrown if a method requires that a file does not exist already but the file
 * exists.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 2237027823060973043L;

    /**
     * Creates a new instance of <code>FileAlreadyExistsException</code> without
     * detail message.
     */
    FileAlreadyExistsException() {
        super();
    }

    /**
     * Constructs an instance of <code>FileAlreadyExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    FileAlreadyExistsException(final String msg) {
        super(msg);
    }

    FileAlreadyExistsException(final Exception ex) {
        super(ex);
    }

    FileAlreadyExistsException(final String msg, final Exception ex) {
        super(msg, ex);
    }
}
