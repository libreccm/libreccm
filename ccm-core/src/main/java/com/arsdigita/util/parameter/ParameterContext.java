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
 * A container of parameters.  
 * 
 * A parameter context binds together a set of parameters and keeps their values.
 * 
 *
 * Subject to change.
 *
 * @see com.arsdigita.util.parameter.Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public interface ParameterContext {

    /**
     * Returns all the parameters registered on the parameter context.
     *
     * @return A <code>Parameter[]</code> of all the parameters; it
     * cannot be null
     */
    Parameter[] getParameters();

    /**
     * Gets the unmarshaled value of <code>param</code>.  If the
     * loaded value is null, <code>param.getDefaultValue()</code> is
     * returned.
     *
     * @param param The named <code>Parameter</code> whose value to
     * retrieve; it cannot be null
     * @return The unmarshaled Java object value of <code>param</code>
     */
    Object get(Parameter param);

    /**
     * Gets the unmarshaled value of <code>param</code>, returning
     * <code>dephalt</code> if <code>param</code>'s value is null.
     *
     * @param param The <code>Parameter</code> whose value to
     * retrieve; it cannot be null
     * @param dephalt The fallback default value; it may be null
     * @return The unmarshaled Java object value of <code>param</code>
     * or <code>dephalt</code> if the former is null
     */
    Object get(Parameter param, Object dephalt);

    /**
     * Sets the value of <code>param</code> to <code>value</code>.
     *
     * @param param The <code>Parameter</code> whose value to set; it
     * cannot be null
     * @param value The new value of <code>param</code>; it may be
     * null
     */
    void set(Parameter param, Object value);

    /**
     * Reads and unmarshals all values associated with the registered
     * parameters from <code>reader</code>.  If any errors are
     * encountered, they are added to <code>errors</code>.
     *
     * @param reader The <code>ParameterReader</code> from which to
     * fetch the values; it cannot be null
     * @param errors The <code>ErrorList</code> that captures any
     * errors while loading; it cannot be null
     */
    void load(ParameterReader reader, ErrorList errors);

    /**
     * Marshals and writes all values associated with the registered
     * parameters to <code>writer</code>.
     *
     * @param writer The <code>ParameterWriter</code> to which values
     * are written; it cannot be null
     */
    void save(ParameterWriter writer);

    /**
     * Validates all values associated with the registered parameters.
     * Any errors encountered are added to <code>errors</code>.
     *
     * @param errors The <code>ErrorList</code> that captures
     * validation errors; it cannot be null
     */
    void validate(ErrorList errors);
}
