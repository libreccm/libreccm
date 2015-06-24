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
 * Writes encoded parameter values to storage.  Implementors define
 * the exact nature of the storage.
 *
 * @see Parameter#write(ParameterWriter,Object)
 * @see ParameterReader
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public interface ParameterWriter {

    /**
     * Writes the marshaled <code>value</code> for parameter
     * <code>param</code> to storage.
     *
     * @param param The <code>Parameter</code> that is being written;
     * it cannot be null
     * @param value The encoded <code>String</code> value to store for
     * <code>param</code>; it may be null
     */
    void write(Parameter param, String value);
}
