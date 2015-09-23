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

import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * This class is used to manage the data associated with a single parameter.
 * A ParameterData object contains errors and values associated with a given 
 * parameter.
 *
 * @author Uday Mathur 
 * @version $Id$
 */
public final class ParameterData implements Map.Entry, Cloneable {

    /**
     * Value associated with this ParameterData. This value is null if
     * ParameterModel.transformValue() fails.
     */
    private Object m_value;

    /**
     * List of errors for this parameter. Errors are generated during
     * ParameterModel.transformValue() and in ParameterListeners.
     */
    private LinkedList m_errors;

    /**
     * This flag is true if this ParameterData has no errors.
     */
    private boolean m_isValid;

    /**
     * This flag is true if ParameterModel.transformValue() runs without
     * errors. This flag is separate from the valid flag since
     * transformation and validation are separate steps. Without this
     * flag a call to validate() would overwrite errors from
     * transformation. Manually setting a value in ParameterData will
     * mark this flag as true. This flag is useful when using
     * ParameterModels and ParameterData outside of the form
     * rendering/processing pipeline.
     */
    private boolean m_isTransformed;

    /**
     * The ParameterModel that specifies type and validation listeners
     * for this ParameterData
     */
    private ParameterModel m_model;

    /**
     * Construct a new ParameterData object. Create the errors list. By
     * assumption this parameterData is valid and transformed. The
     * ParameterModel will change these flags if it encounters errors in
     * transformation or validation. A model is responsible for
     * performing the transformation and validation actions on a
     * ParameterData.
     */
    private ParameterData() {
        m_errors = new LinkedList();
        m_isValid=true; //by assumption true. invalidated by errors
        m_isTransformed=true; //by assumption true. invalidated by errors
    }

    /**
     * Construct a new ParameterData object with the specified name and
     * value.
     *
     * @param model
     * @param name name of the parameter model associated with this
     * parameter data
     *
     * @param value value of this parameter.
     */
    public ParameterData(ParameterModel model, Object value) {
        this();
        m_model = model;
        m_value = value;
    }

    /**
     * Clones this object making a new reference to the errors list.
     * 
     * @return 
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        ParameterData result = (ParameterData) super.clone();
        result.m_errors = (LinkedList) m_errors.clone();
        return result;
    }

    /**
     *
     * Return the value of this parameter.
     *
     * @return the value of this parameter. If isArray() is true, then the
     *         return value is an array
     */
    @Override
    public final Object getValue() {
        return m_value;
    }

    /**
     * Return the ParameterModel underlying this parameter.
     * 
     * @return 
     */
    public final ParameterModel getModel() {
        return m_model;
    }

    /**
     * Return the name of this parameter.
     *
     * @return the name of this parameter. This should be the same name as the
     * ParameterModel
     */
    public Object getName() {
        return m_model.getName();
    }

    /**
     * Return the name of this parameter.
     *
     * @return getName()
     **/
    @Override
    public Object getKey() {
        return getName();
    }

    /**
     * Sets the value of this parameter. Marks this parameterData as
     * transformed.  Does NOT write through to any underlying Map, as
     * specified in the Map.Entry interface.
     *
     * @param value the value of this parameter
     *
     * @return the previous value of this parameter
     **/
    @Override
    public final Object setValue(Object value) {
        Object old = m_value;
        m_value=value;
        m_isTransformed=true;
        return old;
    }

    /**
     * Produce a string representation of the current value. The underlying
     * parameter model has to be able to read the resulting string back
     * into the right object <code>o</code> so that
     * <code>getValue().equals(o)</code>.
     *
     * @return the value as a readable string
     * @see ParameterModel#marshal
     */
    public final String marshal() {
        return getModel().marshal(getValue());
    }

    /**
     * Set the value to the unmarshalled object represented by
     * <code>encoded</code>.
     *
     * @param encoded the marshalled version of a parameter value.
     * @see ParameterModel#unmarshal
     */
    public final void unmarshal(String encoded) {
        setValue(getModel().unmarshal(encoded));
    }

    /**
     * Adds a an error to this parameter. This method is called by
     * FormData.addError and by ParameterListeners.
     *
     * @param message The error message to add to this parameter
     * @deprecated use addError(GlobalizedMessage message) instead.
     */
    public void addError(String message) {
        if(message != null && message.length() > 0)
            addError(new GlobalizedMessage(message));
    }

    /**
     * Adds an error to this parameter. This method is called by
     * FormData.addError and by ParameterListeners.
     *
     * @param message GlobalizedMessage representing the error to add to this
     *                parameter.
     */
    public void addError(GlobalizedMessage message) {
        m_errors.add(message);
        m_isValid = false;
    }

    /**
     * Copy all erors from <code>p</code> to this parameter data object.
     *
     * @param p the object from which to copy errors
     */
    // Package scope intentional
    void copyErrors(ParameterData p) {
        if ( p.m_errors.size() > 0 ) {
            m_errors.addAll(p.m_errors);
            m_isValid = false;
        }
    }

    /**
     * Remove errors for this parameter. This method is called by
     * FormData.resetParameterErrors()
     */
    // Package scope intentional
    void removeErrors() {
        m_errors.clear();
    }

    /**
     * Gets the errors in this particular parameter.
     *
     * @return in Iterator of error message strings for this parameter
     */
    public Iterator getErrors() {
        return m_errors.iterator();
    }

    /**
     * Revalidate this ParameterData. If transformation failed, and the
     * value has not been reset, this will be a no-op.
     * 
     * @throws com.arsdigita.bebop.FormProcessException
     */
    public void validate() throws FormProcessException {
        if (isTransformed()) {
            removeErrors();
        } else {
            return;
        }
        m_isValid = true;
        m_model.validate(this);
    }

    /**
     * Return true if the value is an array of values.
     *
     * @return boolean value depending on whether the
     * value of this parameter is a single value or an array
     */
    public boolean isArray() {
        return (m_value instanceof Object[]);
    }

    /**
     * @return true if this ParameterData has no errors
     */
    public final boolean isValid() {
        return m_isValid;
    }

    /**
     * @return true if this ParameterData tranformed without error, or
     * the value was manually set with setValue()
     */
    public final boolean isTransformed() {
        return m_isTransformed;
    }

    /**
     * Mark this ParameterData as invalid.
     *
     * @deprecated Use {@link #invalidate()}.
     */
    public final void setInvalid() {
        invalidate();
    }

    /**
     * Mark this ParameterData as invalid.
     */
    public final void invalidate() {
        m_isValid = false;
    }

    /**
     * Mark this ParameterData as untransformed.
     */
    public final void setUntransformed() {
        m_isTransformed = false;
    }

    /** Convert to a String.
     *  @return a human-readable representation of <code>this</code>.
     */
    @Override
    public String toString() {
        StringBuilder to = new StringBuilder();
        to.append("{")
            .append(m_value)
            .append(", ").append(m_errors);
        if (!m_isValid      ) to.append(", not valid");
        if (!m_isTransformed) to.append(", not transformed");
        // m_model = " + m_model + newLine
        to.append("}");
        return to.toString();
    }
}
