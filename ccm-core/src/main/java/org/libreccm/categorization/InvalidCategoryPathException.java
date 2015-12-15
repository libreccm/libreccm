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
package org.libreccm.categorization;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class InvalidCategoryPathException extends RuntimeException {

    private static final long serialVersionUID = -428910047165112592L;

    /**
     * Creates a new instance of <code>InvalidCategoryPathException</code>
     * without detail message.
     */
    public InvalidCategoryPathException() {
    }

    /**
     * Constructs an instance of <code>InvalidCategoryPathException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidCategoryPathException(final String msg) {
        super(msg);
    }
    
    public InvalidCategoryPathException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidCategoryPathException(final String msg, 
                                        final Throwable cause) {
        super(msg, cause);
    }
}
