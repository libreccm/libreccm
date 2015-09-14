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
 * Thrown by the {@link ModuleManager} if something goes wrong.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ModuleManagerException extends RuntimeException {

    private static final long serialVersionUID = 1426939919890655697L;

    /**
     * Creates a new instance of <code>ModuleManagerException</code> without
     * detail message.
     */
    public ModuleManagerException() {
        super();
        //Nothing
    }

    /**
     * Constructs an instance of <code>ModuleManagerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ModuleManagerException(final String msg) {
        super(msg);
    }

    public ModuleManagerException(final Throwable cause) {
        super(cause);
    }

    public ModuleManagerException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

}
