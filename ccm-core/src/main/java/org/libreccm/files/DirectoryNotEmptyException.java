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
 * Thrown if a non empty directory is not deleted recursively.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DirectoryNotEmptyException extends Exception {

    private static final long serialVersionUID = -8515711805034123260L;

    /**
     * Creates a new instance of <code>DirectoryNotEmptyException</code> without
     * detail message.
     */
    DirectoryNotEmptyException() {
        super();
    }

    /**
     * Constructs an instance of <code>DirectoryNotEmptyException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    DirectoryNotEmptyException(final String msg) {
        super(msg);
    }
    
    DirectoryNotEmptyException(final Exception ex) {
        super(ex);
    }
    
    DirectoryNotEmptyException(final String msg, final Exception ex) {
        super(msg, ex);
    }
}
