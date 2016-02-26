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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Lockable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import java.util.Objects;

/**
 *    Represents the abstract model for a form parameter object.  This
 *    class must be subclassed for each specific data type.
 *
 *    @author Karl Goldstein
 *    @author Uday Mathur
 *
 *    @version $Id$
 */
public abstract class ParameterModel implements Lockable {

    private static final Logger s_log = Logger
                                        .getLogger(ParameterModel.class.getName());

    /**
     * The name of this ParameterModel. The constructor will throw an
     * exception if the specified name is null
     */
    protected String m_name;

    /**
     * The default value of this ParameterModel. This value is used when
     * the request is not a submission, and the transformValue returns
     * null.
     */
    protected Object m_defaultValue;

    /**
     * A List of Listeners to validate this parameter.
     */
    protected List m_parameterListeners;

    /**
     * A boolean indicating if this ParameterModel is locked, as per the
     * Lockable interface
     */
    protected boolean m_locked;


    /**
     * <code>true</code> if the parameter value in a request should be set
     * to the default value when it would ordinarily be set
     * <code>null</code>. If this is <code>false</code>, the default value
     * is only ever used for parameter values during requests that are not
     * submissions (as indicated by the <code>isSubmission</code> parameter
     * to {@link #createParameterData createParameterData}.
     *
     * This defaults to <code>true</code>.
     */
    private boolean m_defaultOverridesNull = false;

    private boolean m_passIn = true;

    /**
     * Construct an unlocked ParameterModel
     */
    protected ParameterModel() {
        m_locked = false;
        m_parameterListeners = new LinkedList();
    }

    /**
     * Construct a new Parameter Model with the specified name
     *
     * @param name String used to identify this parameter. Name is used
     *             as the name of the associated widget, and is the name of the
     *             variable in the request.
     */
    protected ParameterModel(String name) {
        this();
        Assert.isUnlocked(this);
        Assert.exists(name, "Name");
        Assert.assertNotEmpty(name, "Name");
        m_name = name;
    }

    /**
     * Controls how default values are used. If this property is
     * <code>false</code> (which it is initially), default values are only
     * used for requests that are <em>not</em> user submissions as
     * indicated by the <code>isSubmission</code> parameter to {@link
     * #createParameterData}. This is the behavior that is useful for
     * forms, since it makes it possible for the user to get rid of default
     * form entries by erasing them.
     *
     * <p> If this property is set to <code>true</code>, the default value
     * is used whenever {@link #transformValue} returns <code>null</code>.
     * This behavior is mainly useful for processing requests that are
     * generated automatically, and is used by {@link
     * com.arsdigita.bebop.PageState} to maintain state information across
     * requests.
     *
     * @param v <code>true</code> if default values should be used
     * when the value in the request is <code>null</code>.
     * @see #setDefaultValue
     */
    public void setDefaultOverridesNull(boolean v) {
        Assert.isUnlocked(this);
        m_defaultOverridesNull = v;
    }

    /**
     * The "pass in" property determines whether the value for this parameter is
     * generally passed in from the outside. If this property is
     * <code>true</code>, the model always tries to get the parameter value from
     * the request, no matter whether the <code>isSubmission</code> parameter to
     * {@link #createParameterData} is true or not.
     *
     * <p>If this property is <code>false</code>, the parameter value is only
     * read from the request if the <code>isSubmission</code> parameter to
     * {@link #createParameterData} is true.
     *
     * <p> By default, this property is <code>true</code>.
     *
     * @return <code>true</code> if an attempt should always be made to retrieve
     * the parameter value from the request.
     */
    public boolean isPassIn() {
        return m_passIn;
    }

    /**
     * Set whether this parameter should be treated as a "pass in" parameter.
     *
     * <p>This defaults to <code>true</code>; if a passed-in value is available,
     * it should be used. </p>
     *
     * @see #isPassIn()
     * @param v <code>true</code> if this parameter is a pass in parameter.
     */
    public void setPassIn(boolean v) {
        Assert.isUnlocked(this);
        m_passIn = v;
    }

    /**
     * Return <code>true</code> if default values are used when the value
     * in the request is <code>null</code>. This property is initially
     * <code>false</code>.
     *
     * @return <code>true</code> if default values are used when the value
     * in the request is <code>null</code>.
     * @see #setDefaultOverridesNull
     * @see #setDefaultValue
     */
    public boolean getDefaultOverridesNull() {
        return m_defaultOverridesNull;
    }

    /**
     * Returns the name of this parameterModel
     *
     * @return The name of this parameter model.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name of this ParameterModel.
     * Asserts that this ParameterModel is not locked.
     *
     * @param name The name of this parameter model.
     */
    public void setName(String name) {
        Assert.isUnlocked(this);
        m_name = name;
    }

    /**
     * Adds a validation listener, implementing a custom validation
     * check that applies to this Parameter.  Useful for checks that
     * require examination of the values of only this parameter.
     * Asserts that this ParameterModel is not locked.
     *
     * @param listener An instance of a class that implements the
     * <code>FormValidationListener</code> interface.
     */
    public void addParameterListener(ParameterListener listener) {
        Assert.isUnlocked(this);
        m_parameterListeners.add(listener);
    }

    /**
     * Sets a default value for this parameter. This default value is
     * superceded by values set in the initialization listeners and in
     * the request object.
     * Asserts that this ParameterModel is not locked.
     *
     * @param defaultValue a default value for this parameter that
     * appears if there is no value in the request or specified by an
     * initialization listener
     */
    public void setDefaultValue(Object defaultValue) {
        Assert.isUnlocked(this);
        m_defaultValue = defaultValue;
    }

    /**
     * Get the default value for this parameter. This value is used if
     * this request is not a submission and transformValue returns null.
     *
     * @return a default value for this parameter
     */
    public Object getDefaultValue() {
        return m_defaultValue;
    }

    /**
     * Transform string parameter values in the HTTP request into an
     * appropriate Java object associated with the particular
     * implementing class.  If there is an error transforming the
     * passed-in URL/form variables into a Java object, then
     * implementing classes should throw IllegalArgumentException.
     *
     * @param request The HttpServletRequest of the form submission.
     * @exception java.lang.IllegalArgumentException if there is an
     * error transforming form/URL variables to an object.
     */
    public abstract Object transformValue(HttpServletRequest request)
            throws IllegalArgumentException;

    /**
     * Helper method for implementing {@link #transformValue
     * transformValue}. Calls {@link #unmarshal unmarshal}, passing in the
     * request parameter, if there is a nonempty request parameter with the
     * models name, and returns <code>null</code> otherwise.
     *
     * @param request the current request
     * @return the value returned by {@link #unmarshal unmarshalling} the
     * request parameter, or <code>null</code> if there is no nonempty
     * request parameter with the model's name.
     */
    protected Object transformSingleValue(HttpServletRequest request) {
        String requestValue = Globalization.decodeParameter(request, getName());
        if (requestValue == null) {
            return null;
        }

        return unmarshal(requestValue);
    }

    public ParameterData createParameterData(HttpServletRequest request) {
        return createParameterData(request, null, false);
    }


    /**
     * Create a ParameterData for this ParameterModel with the supplied
     * request If this tranformation throws an exception, mark the
     * corresponding ParameterData as unTransformed and set its value to
     * null. If this request is not a submission and transformValue
     * returns null then construct the ParameterData with the default
     * value for this ParameterModel.
     *
     * @param request the HttpServletRequest from which to extract and
     * transform the value
     *
     * @param isSubmission boolean indicating whether this ParameterData
     * is being created as part of a form submission. If this is part of
     * a form submission, we do not want default values to overwrite
     * null values from the submission.
     */
    public ParameterData createParameterData(HttpServletRequest request,
            boolean isSubmission) {
        return createParameterData(request, null, isSubmission);
    }

    /**
     * Create a ParameterData for this ParameterModel with the supplied
     * request If this tranformation throws an exception, mark the
     * corresponding ParameterData as unTransformed and set its value to
     * null. If this request is not a submission and transformValue
     * returns null then construct the ParameterData with the default
     * value for this ParameterModel.
     *
     * @param request the HttpServletRequest from which to extract and
     * transform the value
     *
     * @param defaultValue an additional default value that may
     * be specified dynamically, to override the static default
     * value associated with this ParameterModel object.
     *
     * @param isSubmission boolean indicating whether this ParameterData
     * is being created as part of a form submission. If this is part of
     * a form submission, we do not want default values to overwrite
     * null values from the submission.
     */
    public ParameterData createParameterData(HttpServletRequest request,
                                             Object defaultValue,
                                             boolean isSubmission) {
        // This method can work in one of two modes, which influences how
        // default values are used.
        // (0) if transformValue() returns null, we *always* try
        //     the dynamic defaultValue parameter.
        // (1) If getDefaultOverridesNull() is false, the default value is
        //     only used if isSubmission is false.  In all other cases, the
        //     value returned from transformValue(), or null if that throws
        //     an error, is used.
        // (2) Otherwise, the default value is used if (a) isSubmission is
        //     false or (b) if transformValue() returned null or threw an
        //     error. This mode is solely there to allow PageState and
        //     Bebop state parameters 'do the right thing', and should be
        //     irrelevant to the processing of 'real' forms.

        ParameterData result = new ParameterData(this, null);

        // DEE update 1/9/01: We are breaking isPassIn and isSubmission into different
        // logic, to make defaults work right.
        //isSubmission = isSubmission || isPassIn();

        if (isSubmission) {
            try {
                result.setValue(transformValue(request));
                if (result.getValue() == null && defaultValue != null) {
                    result.setValue(defaultValue);
                }
                if (getDefaultOverridesNull() && result.getValue() == null) {
                    result.setValue(getDefaultValue());
                }
            } catch (IllegalArgumentException e) {
                if (getDefaultOverridesNull()) {
                    result.setValue(getDefaultValue());
                } else {
                    result.setValue(null);
                }
                result.addError(e.getMessage());
                result.setUntransformed();
            }
        } else if (isPassIn()) {
            // the diff between this and isSubmission, is that isSubmission
            // only falls back to getDefaultValue if setDefaultOverridesNull
            // is true.  This one always falls back to getDefaultValue.
            try {
                result.setValue(transformValue(request));
                if (result.getValue() == null && defaultValue != null) {
                    result.setValue(defaultValue);
                }
                if (result.getValue() == null) {
                    result.setValue(getDefaultValue());
                }
            } catch (IllegalArgumentException e) {
                result.setValue(getDefaultValue());
                result.addError(e.getMessage());
                result.setUntransformed();
            }
        } else {
            // neither a submission or a passin, so we should always
            // just use the default value.
            if (defaultValue != null) {
                result.setValue(defaultValue);
            } else {
                result.setValue(getDefaultValue());
            }
        }
        return result;
    }

    /**
     * Lock the model, blocking any further modifications.  Cached form
     * models should always be locked to ensure that they remain
     * unchanged across requests.
     */
    public synchronized void lock() {
        m_locked = true;
    }

    /**
     * Call parameter validation listeners that have been added to this
     * <code>ParameterModel</code>.  Note that this is decoupled from
     * the <code>getValue</code> method because the form data object may
     * be initialized with data prior to processing the HTTP request.
     *
     * @param data A ParameterData object associated with this
     * parameterModel for a given request. The ParameterData contains
     * the value and errors of this parameter
     */
    public void validate(ParameterData data)
            throws FormProcessException {
        Assert.isLocked(this);

        ParameterEvent e = null;

        for (Iterator i = m_parameterListeners.iterator(); i.hasNext();) {
            if (e == null) {
                e = new ParameterEvent(this, data);
            }
            ((ParameterListener) i.next()).validate(e);
        }
    }

    /**
     * Produce a string representation of the given value. The parameter
     * model has to be able to read the resulting string back into the
     * right object <code>o</code> so that
     * <code>value.equals(o)</code>.
     *
     * @param value a value previously produced by this parameter model
     * @return the value as a readable string, may be <code>null</code>
     */
    public String marshal(Object value) {
        return (value == null) ? null : value.toString();
    }

    /**
     * Reconstruct the parameter value from an encoded string produced by
     * {@link #marshal marshal}. This is an optional operation, which needs
     * to be implemented by specific subclasses; this implementation just
     * throws an {@link java.lang.UnsupportedOperationException}.
     *
     * <p> The contract between <code>marshal</code> and
     * <code>unmarshal</code> is that for any possible object
     * <code>obj</code> this parameter model can produce
     * <code>obj.equals(unmarshal(marshal(obj))</code>.
     *
     * @param encoded a string produced by {@link #marshal marshal}
     * @return the object represented by <code>encoded</code>
     * @pre encoded != null
     */
    public Object unmarshal(String encoded) {
        throw new UnsupportedOperationException("Not implemented. "
                                                + "This method needs to be implemented by the specific subclasses.");
    }

    /**
     * Return the class that all values produced by the model will
     * have. This should be the most specific common base class of all
     * classes of which the model will ever produce values. This
     * implementation returns <code>Object.class</code>
     *
     * @return the class that all values produced by the model will
     * have.
     */
    public Class getValueClass() {
        return Object.class;
    }

    /**
     * @return true if this ParameterModel is locked to prevent
     * modification.
     */
    public boolean isLocked() {
        return m_locked;
    }

    /**
     * Test m_parameterKisteners for member of a given subtype
     *
     * @param listener Subtype of the ParameterListener
     * @return true if a ParameterListener of given type is in the list
     */
    public boolean hasParameterListener(ParameterListener listener) {
        Iterator lListIter = m_parameterListeners.iterator();

        while (lListIter.hasNext()) {
            ParameterListener pl = (ParameterListener) lListIter.next();
            if (pl.getClass().equals(listener.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        
        if (!(other instanceof ParameterModel)) {
            return false;
        }
        
        return m_name.equals(((ParameterModel) other).getName());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.m_name);
        return hash;
    }
}
