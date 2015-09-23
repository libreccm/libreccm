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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterListener;
import javax.servlet.http.HttpServletRequest;

/**
 *  A convenience class for implementing a ParameterModel which wraps another
 *  ParameterModel.
 *
 *    @author Matthew Booth
 */
public class ParameterModelWrapper extends ParameterModel {
    private ParameterModel m_model;

    public ParameterModelWrapper( ParameterModel model ) {
        m_model = model;
    }

    public void setDefaultOverridesNull(boolean v) {
        m_model.setDefaultOverridesNull( v );
    }

    public boolean isPassIn() {
        return m_model.isPassIn();
    }

    public void setPassIn(boolean  v) {
        m_model.setPassIn( v );
    }

    public boolean getDefaultOverridesNull() {
        return m_model.getDefaultOverridesNull();
    }

    public String getName() {
        return m_model.getName();
    }

    public void setName(String name) {
        m_model.setName( name );
    }

    public void addParameterListener(ParameterListener listener) {
        m_model.addParameterListener( listener );
    }

    public void setDefaultValue(Object defaultValue) {
        m_model.setDefaultValue( defaultValue );
    }

    public Object getDefaultValue() {
        return m_model.getDefaultValue();
    }

    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException
    {
        return m_model.transformValue( request );
    }

    protected Object transformSingleValue(HttpServletRequest request) {
        return m_model.transformSingleValue( request );
    }

    public ParameterData createParameterData(HttpServletRequest request) {
        return m_model.createParameterData( request );
    }

    public ParameterData createParameterData(HttpServletRequest request,
                                             boolean isSubmission) {
        return m_model.createParameterData(request, null, isSubmission);
    }

    public ParameterData createParameterData(HttpServletRequest request,
                                             Object defaultValue,
                                             boolean isSubmission) {
        return m_model.createParameterData( request, defaultValue, isSubmission );
    }

    public void validate(ParameterData data) throws FormProcessException {
        m_model.validate( data );
    }

    public String marshal(Object value) {
        return m_model.marshal( value );
    }

    public Object unmarshal(String encoded) {
        return m_model.unmarshal( encoded );
    }

    public Class getValueClass() {
        return m_model.getValueClass();
    }

    public void lock() {
        m_model.lock();
    }

    public boolean isLocked() {
        return m_model.isLocked();
    }
}
