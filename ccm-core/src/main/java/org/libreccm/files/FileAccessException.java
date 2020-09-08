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
 * Generic exception thrown if an error occurs while accessing the local file
 * system which is not covered by other exceptions.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAccessException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final String MESSAGE_TEMPLATE = "Error accessing file '%s'.";

    FileAccessException(final String path) {
        super(String.format(MESSAGE_TEMPLATE, path));
    }

    FileAccessException(final String path, final Exception ex) {
        super(String.format(MESSAGE_TEMPLATE, path), ex);
    }

}
