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
 * Reads an encoded string value for a parameter from storage.  Any
 * errors encountered while reading are added to an error list.
 * This class is counterpart to <code>ParameterWriter</code>.
 *
 * Subject to change.
 *
 * @see Parameter#write(ParameterWriter, Object)
 * @see ErrorList
 * @see ParameterWriter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public interface ParameterReader {

    /**
     * Reads an encoded <code>String</code> value for
     * <code>param</code> from storage.  If there are errors, they are
     * added to <code>errors</code>.
     *
     * @param param The <code>Parameter</code> being read; it cannot
     * be null
     * @param errors The <code>ErrorList</code> that will collect any
     * errors; it cannot be null
     * @return The marshaled <code>String</code> value for
     * <code>param</code>; it may be null
     */
    String read(Parameter param, ErrorList errors);
}
