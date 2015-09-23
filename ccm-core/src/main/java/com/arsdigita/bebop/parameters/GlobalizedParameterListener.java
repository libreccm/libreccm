/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Abstract class to be extended by globalized parameters.
 *
 * @version $Id$
 */
public abstract class GlobalizedParameterListener
                      implements Globalized, ParameterListener {

    /** Name of the resource file to map keys to localized output.            */
    private final static String BUNDLE_NAME =
        "com.arsdigita.bebop.parameters.ParameterResources";

    /** Property to hold the globalized user information about the error
     * encountered.                                                           */
    private GlobalizedMessage m_error = null;

    /**
     * Provide client classes with the ResourceBundle to use. By default that
     * one contained in this package's ParameterResources.properties.
     * @return String target ResourceBundle base name.
     */
    public String getBundleBaseName() {
        return BUNDLE_NAME;
    }

    /**
     * Set the error message for this parameter.
     *
     * @param error The error message to use for this parameter.
     */
    protected void setError(GlobalizedMessage error) {
        m_error = error;
    }

    /**
     * Get the error message for this parameter.
     *
     * @return GlobalizedMessage The error.
     */
    protected GlobalizedMessage getError() {
        return m_error;
    }
}
