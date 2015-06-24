/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.parameter;

/**
 * Subject to change.
 *
 * Metadata for a parameter that is of use for building documentation
 * or user interfaces for parameters.  The fields are not required and
 * thus the methods of this class may return null.
 *
 * @see Parameter#setInfo(ParameterInfo)
 * @see Parameter#getInfo()
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public interface ParameterInfo {

    /**
     * Gets the pretty name of the parameter.
     *
     * @return The <code>String</code> title of the parameter; it may
     * be null
     */
    String getTitle();

    /**
     * Gets the parameter's reason for being.
     *
     * @return The <code>String</code> purpose of the parameter; it
     * may be null
     */
    String getPurpose();

    /**
     * Gets an example value for the parameter.
     *
     * @return A <code>String</code> example value; it may be null
     */
    String getExample();

    /**
     * Gets a format description.
     *
     * @return A format <code>String</code>; it may be null
     */
    String getFormat();
}
