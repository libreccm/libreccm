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
 * Thrown if the user which is used to run the application server has not the
 * necessary permissions to access the requested file.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class InsufficientPermissionsException extends Exception {

    private static final long serialVersionUID = -7496839503615573013L;
    
    private static final String MESSAGE_TEMPLATE = "Insufficient permissions for accessing file '%s'.";

//    /**
//     * Creates a new instance of <code>InsufficientPermissionsException</code>
//     * without detail message.
//     */
//    InsufficientPermissionsException() {
//        super();
//    }

    
    InsufficientPermissionsException(final String path) {
        super(String.format(MESSAGE_TEMPLATE, path));
    }

//    InsufficientPermissionsException(final Exception ex) {
//        super(ex);
//    }

    InsufficientPermissionsException(final String path, final Exception ex) {
        super(String.format(MESSAGE_TEMPLATE, path), ex);
    }
}
