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
 * Thrown if by methods that require that the file accessed exists if the
 * requested file does not exist.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileDoesNotExistException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of <code>FileDoesNotExistException</code> without
     * detail message.
     */
    FileDoesNotExistException() {
    }

    /**
     * Constructs an instance of <code>FileDoesNotExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    FileDoesNotExistException(String msg) {
        super(msg);
    }

    FileDoesNotExistException(final Exception ex) {
        super(ex);
    }

    FileDoesNotExistException(final String msg, final Exception ex) {
        super(msg, ex);
    }
}
