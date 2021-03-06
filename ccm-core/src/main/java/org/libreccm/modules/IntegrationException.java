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
package org.libreccm.modules;

/**
 * Thrown if an error occurs in the {@link CcmIntegrator}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IntegrationException extends RuntimeException {

    private static final long serialVersionUID = -8505205543077310805L;

    /**
     * Creates a new instance of <code>IntegrationException</code> without
     * detail message.
     */
    public IntegrationException() {
        super();
    }

    /**
     * Constructs an instance of <code>IntegrationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public IntegrationException(final String msg) {
        super(msg);
    }

    public IntegrationException(final Throwable cause) {
        super(cause);
    }

    public IntegrationException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
