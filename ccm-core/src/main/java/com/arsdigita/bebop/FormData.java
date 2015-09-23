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
package com.arsdigita.bebop;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.URLRewriter;

/**
 * Manages the data associated with forms and other remote sources.

 * <p>The basic task of a <code>FormData</code> object is to transform
 * a set of key-value string pairs into a validated set of Java data
 * objects for use in subsequent processing.  In most cases the original
 * data is an HTTP request.

 * <p>To perform the transformation, a separate instance of
 * <code>FormModel</code> is used to specify the name and basic data
 * type of each expected parameter in the set, as well as any
 * additional validation steps required.  The <code>FormData</code>
 * stores both the transformed data objects and any validation
 * error messages associated with an individual parameter or the
 * form as a whole.  Once the data has been validated, individual data
 * objects may be queried from a <code>FormData</code> using the
 * standard <code>get</code> method of the <code>Map</code> interface.
 *
 * <p><code>FormData</code> objects may also be used to control the
 * entire lifecycle of self-validating forms, which report errors to
 * the user in the context of the form itself, rather than on a
 * separate page.
 *
 * <p>See the Forms API Developer Guide for details on using the
 * <code>FormData</code> class.
 *
 * @author Karl Goldstein 
 * @author Uday Mathur 
 * @author Stas Freidin 
 * @version $Id: FormData.java 287 2005-02-22 00:29:02Z sskracic $ */

public class FormData implements Map, Cloneable {


    private HashMap m_parameterDataValues = new HashMap();
    private LinkedList m_formErrors;
    private FormModel m_model;

    private Locale m_locale;
    private boolean m_isTransformed;
    private boolean m_isValid;

    private boolean m_isSubmission;

    /**
     * Ensure that no one can create this object from outside the package
     * without supplying meaningful parameters
     */
    private FormData() {}

    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     *
     * @param model a <code>FormModel</code> describing the parameters
     * and validation constraints for this request
     *
     * @param request an HTTP request object passed from the servlet
     * container
     *
     * @pre model != null
     * @pre request != null
     *
     * @throws FormProcessException if an error occurs.
     */

    public FormData(FormModel model, HttpServletRequest request)
        throws FormProcessException {

        this(model, request, Locale.getDefault());
    }

    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     *
     * @param model a <code>FormModel</code> describing the parameters
     * and validation constraints for this request
     *
     * @param request an HTTP request object passed from the servlet
     * container
     *
     * @param isSubmission <code>true</code> if the request should be treated
     * as a form submission by the user
     *
     * @pre model != null
     * @pre request != null
     *
     * @throws FormProcessException if an error occurs.
     */
    public FormData(FormModel model, HttpServletRequest request,
                    boolean isSubmission)
        throws FormProcessException {

        this(model, request, Locale.getDefault(), isSubmission);
    }

    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     *
     * @param model a <code>FormModel</code> describing the parameters
     * and validation constraints for this request
     *
     * @param request an HTTP request object passed from the servlet
     * container
     *
     * @param isSubmission <code>true</code> if the request should be treated
     * as a form submission by the user
     *
     * @param fallback a fallback FormData object.  If a value for
     * a parameter in the form model <code>model</code> is not in
     * the current request parameters, the <code>fallback</code> object is
     * searched.
     *
     * @pre model != null
     * @pre request != null
     *
     * @throws FormProcessException if an error occurs
     */
    public FormData(FormModel model, HttpServletRequest request,
                    boolean isSubmission, FormData fallback)
        throws FormProcessException {

        this(model, request, Locale.getDefault(), isSubmission, fallback);
    }

    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     * Error messages are provided in the specified locale.
     *
     * @param model A <code>FormModel</code> describing the parameters
     * and validation constraints for this request.
     *
     * @param request An HTTP request object passed from the servlet
     * container.
     *
     * @param locale The locale for which all error messages will be
     * prepared.  This may be used in a multilingual environment to
     * tailor the output to the preferences or geographic location of
     * individual users on a per-request basis.
     *
     * @pre model != null
     * @pre request != null
     * @pre locale != null
     *
     * @throws FormProcessException if an error occurs
     */
    public FormData(FormModel model, HttpServletRequest request, Locale locale)
        throws FormProcessException {
        this(model, request, locale,
             request.getParameter(model.getMagicTagName()) != null);
    }

    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     * Error messages are provided in the specified locale.
     *
     * @param model A <code>FormModel</code> describing the parameters
     * and validation constraints for this request.
     *
     * @param request An HTTP request object passed from the servlet
     * container.
     *
     * @param locale The locale for which all error messages will be
     * prepared.  This may be used in a multilingual environment to
     * tailor the output to the preferences or geographic location of
     * individual users on a per-request basis.
     *
     * @param isSubmission <code>true</code> if the request should be treated
     * as a form submission by the user.
     *
     * @throws FormProcessException if an error occurs
     * @pre model != null
     * @pre request != null
     * @pre locale != null
     */
    public FormData(FormModel model, HttpServletRequest request,
                    Locale locale, boolean isSubmission)
        throws FormProcessException {
        this(model, request, locale, isSubmission, null);
    }


    /**
     * Constructs a new <code>FormData</code> object containing the
     * transformed and validated query parameters from an HTTP request.
     * Error messages are provided in the specified locale.
     *
     * @param model A <code>FormModel</code> describing the parameters
     * and validation constraints for this request.
     *
     * @param request An HTTP request object passed from the servlet
     * container.
     *
     * @param locale The locale for which all error messages will be
     * prepared.  This may be used in a multilingual environment to
     * tailor the output to the preferences or geographic location of
     * individual users on a per-request basis.
     *
     * @param isSubmission <code>true</code> if the request should be treated
     * as a form submission by the user.
     *
     * @param fallback a fallback FormData object.  If a value for
     * a parameter in the form model <code>model</code> is not in
     * the current request parameters, the <code>fallback</code> object is
     * searched.
     *
     * @throws FormProcessException if an error occurs
     * @pre model != null
     * @pre request != null
     * @pre locale != null
     */
    public FormData(FormModel model, HttpServletRequest request,
                    Locale locale, boolean isSubmission,
                    FormData fallback)
        throws FormProcessException {

        Assert.exists(model, "FormModel");
        Assert.exists(request, "HttpServletRequest");
        Assert.exists(locale, "Locale");

        m_locale = locale;
        m_model = model;
        m_isTransformed = false;

        m_isSubmission = isSubmission;
        m_isValid = m_isSubmission;

        createParameterData(request, fallback);

        Iterator params = URLRewriter.getGlobalParams(request).iterator();
        while (params.hasNext()) {
            ParameterData param = (ParameterData)params.next();
            setParameter(param.getModel().getName(), param);
        }
    }

    /**
     * Validates this <code>FormData</code> object according to its form model.
     * If the <code>FormData</code> is already valid, does nothing.
     *
     * @param state describes the current page state
     * @pre state != null
     */
    public void validate(PageState state) {

        if (isValid()) {
            return;
        }

        m_isValid = true;

        if (m_formErrors != null) {
            m_formErrors.clear();
        }

        m_model.validate(state, this);
    }

    /**
     * Validates this <code>FormData</code> object against its form model,
     * regardless of whether the object is currently valid.
     *
     * @param state describes the current page state
     * @pre state != null
     */
    public void forceValidate(PageState state) {
        invalidate();
        validate(state);
    }

    /**
     * Reports a validation error on the form as a whole.
     *
     * @param message a String of the error message
     * @pre message != null
     * @deprecated refactor and use addError(GlobalizedMessage) instead
     */
    public void addError(String message) {
        addError(new GlobalizedMessage(message));
    }

    /**
     * Reports a validation error on the form as a whole. 
     * Uses a GlobalizedMessage for inklusion 
     *
     * @param message the error message
     * @pre message != null
     */
    public void addError(GlobalizedMessage message) {

        if (m_formErrors == null) {
            m_formErrors = new LinkedList();
        }

        m_formErrors.add(message);
        m_isValid = false;
    }

    /**
     * Adds an error message to the ParameterData object associated with
     * the parameter model identified by <code>name</code>.
     *
     * @param name the name of the parameter model to whose
     *             ParameterData the error message will be added
     *
     * @param message the text of the error message to add
     *
     * @pre name != null
     * @pre message != null
     * @deprecated use addError(String name, GlobalizedMessage message) instead
     */
    public void addError(String name, String message) {

        ParameterData parameter;

        if (!m_parameterDataValues.containsKey(name)) {
            throw new IllegalArgumentException
                ("Attempt to set Error in Non-Existant ParameterData");
        }

        parameter = (ParameterData) m_parameterDataValues.get(name);
        parameter.addError(message);
        m_isValid = false;
    }


    /**
     * Adds an error message to the ParameterData object associated with
     * the parameter model identified by <code>name</code>.
     *
     * @param name the name of the parameter model to whose
     * ParameterData the error message will be added
     *
     * @param message the text of the error message to add
     *
     * @pre name != null
     * @pre message != null
     */
    public void addError(String name, GlobalizedMessage message) {

        ParameterData parameter;

        if (!m_parameterDataValues.containsKey(name)) {
            throw new IllegalArgumentException
                ("Attempt to set Error in Non-Existant ParameterData");
        }

        parameter = (ParameterData) m_parameterDataValues.get(name);
        parameter.addError(message);
        m_isValid = false;
    }

    /**
     * Returns the errors associated with the specified parameter.
     *
     * @param name the name of the parameter whose errors we are interested in
     *
     * @return an iterator of errors. Each error is just a string for
     * now.
     *
     * @pre name != null
     * @post return != null
     */
    public Iterator getErrors(String name) {

        ParameterData parameter
            = (ParameterData)m_parameterDataValues.get(name);

        if (parameter == null) {
            return Collections.EMPTY_LIST.iterator();
        }

        return parameter.getErrors();
    }

    /**
     * Returns an iterator over all the errors on this form that are not
     * associated with any particular parameter. Such errors may have
     * been generated by a FormValidationListener.
     *
     * @return an iterator over error messages.
     * @post return != null
     */
    public Iterator getErrors() {

        if (m_formErrors == null) {
            return Collections.EMPTY_LIST.iterator();
        }

        return m_formErrors.iterator();
    }

    /**
     * Returns an iterator over all of the errors on this form.
     * This includes both errors associated with particular parameters
     * and errors associated with the form as a whole.
     *
     * @return an iterator over all error messages.
     * @post return != null
     */
    public Iterator getAllErrors() {

        return new Iterator() {

                private Iterator params, paramErrors, formErrors;

                {
                    params = m_parameterDataValues.values().iterator();
                    paramErrors = Collections.EMPTY_LIST.iterator();
                    formErrors = getErrors();
                }

                private void seekToNextError() {
                    while (! paramErrors.hasNext() && params.hasNext()) {
                        paramErrors
                            = ((ParameterData)params.next()).getErrors();
                    }
                }

                @Override
                public boolean hasNext() {
                    seekToNextError();
                    return paramErrors.hasNext() || formErrors.hasNext();
                }

                @Override
                public Object next() throws NoSuchElementException {

                    seekToNextError();
                    if (paramErrors.hasNext()) {
                        return paramErrors.next();
                    }

                    // An error will be thrown if !formErrors.hasNext()
                    return formErrors.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }

    /**
     * Gets the specified ParameterData object.
     *
     * @param name the name of the parameterModel to retrieve
     * @return the parameter data object specified.
     *
     */
    public ParameterData getParameter(String name) {
        return (ParameterData)m_parameterDataValues.get(name);
    }

    /**
     * Sets the ParameterData object identified by the name in this FormData 
     * Object.
     *
     * @param name the name of the parameterModel
     * @param value
     */
    public void setParameter(String name, ParameterData value) {
        m_parameterDataValues.put(name,value);
    }

    /**
     * Returns a collection of all the <code>ParameterData</code> objects.
     *
     * @return a collection of all the <code>ParameterData</code> objects.
     */
    public final Collection getParameters() {
        return m_parameterDataValues.values();
    }

    /**
     * Determines whether this request represents a submission event.
     *
     * @return <code>true</code> if this request represents a submission event;
     * <code>false</code> if it represents an initialization event.
     */
    public final boolean isSubmission() {
        return m_isSubmission;
    }

    /**
     * Determines whether the key-value string pairs in the
     * request have been transformed into Java data objects.
     * 
     * @return <code>true</code> if the key-value string pairs have been
     *         transformed into Java data objects;
     *         <code>false</code> otherwise.
     *
     */
    public final boolean isTransformed() {
        return m_isTransformed;
    }

    /**
     * Determines whether any errors were found during validation of
     * a form submission.
     * @return <code>true</code> if no errors were found; <code>false</code>
     * otherwise.
     *
     */
    public final boolean isValid() {
        return m_isValid;
    }

    /**
     * Sets isValid to <code>false</code>. We do not allow programmers
     * to manually toggle the isValid value to <code>true</code>.
     * Hence this method takes no
     * arguments and only sets isValid flag to false
     * @deprecated Use invalidate() instead
     */
    public void setInvalid() {
        invalidate();
    }

    /**
     * Set isValid to <code>false</code>. We do not allow programmers
     * to manually toggle the isValid value to <code>true</code>.
     */
    public final void invalidate() {
        m_isValid = false;
    }

    // --- private helper methods to initialize object ---

    /**
     * Sets the value of a parameter within the associated ParameterData
     * object
     *
     * @param name Name of the parameterModel whose ParameterData object
     * we are setting
     *
     * @param value Value to assign the ParmeterData object
     *
     */
    private void setParameterValue(String name, Object value) {
        ParameterData parameter = (ParameterData) m_parameterDataValues.get(name);
        if (parameter != null) {
            parameter.setValue(value);
        } else {
            throw new IllegalArgumentException("Parameter " + name +
                                               " does not exist");
        }
    }

    /**
     * Iterate through parameterModels extracting values from the
     * request, and transforming the value according to the parameter
     * model This code incorporates
     * ParameterModel.createParameterData(request)
     *
     * @param request the HttpServletRequest
     * @param fallback a fallback FormData object.  If any parameter
     * in the form model does not have a value in the request,
     * try to locate its value in the fallback object.
     */
    private void createParameterData(HttpServletRequest request,
                                     FormData fallback)
        throws FormProcessException {
        ParameterModel parameterModel;
        ParameterData parameterData;
        Iterator parameters = m_model.getParameters();

        while (parameters.hasNext()) {
            parameterModel = (ParameterModel) parameters.next();

            // createParamterData automagically handles default values
            // and errors in tranformation.

            Object defaultValue = null;
            if (fallback != null) {
                parameterData =
                    fallback.getParameter(parameterModel.getName());
                if (parameterData != null) {
                    defaultValue = parameterData.getValue();
                }
            }

            // specify a default from the fallback
            parameterData =
                parameterModel.createParameterData(request,
                                                   defaultValue,
                                                   isSubmission());
            Assert.exists(parameterData);
            setParameter(parameterModel.getName(), parameterData);
        }
        m_isTransformed=true;
    }

    // --- Public methods to satisfy Map interface ---

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return m_parameterDataValues.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        // this is very expensive with ParameterData
        throw new UnsupportedOperationException();
    }

    /*
     * This is just plain wrong. Either you pretend to be a Map of
     * things, or you are a Map of ParameterData-s.
     */
    @Override
    public Set entrySet() {
        return m_parameterDataValues.entrySet();
    }

    /**
     * Returns the value contained by the ParameterData object named
     * by <code>key</code>.
     * If no key is found, throws IllegalArgumentException.
     * @param key the parameter data object to retrieve
     * @return the value in the specified parameter data object.
     * @throws java.lang.IllegalArgumentException thrown when the key
     * is not a valid parameter.
     */
    @Override
    public Object get(Object key) throws IllegalArgumentException {

        ParameterData p = getParameter((String)key);
        if (p != null) {
            return p.getValue();
        }
        throw new IllegalArgumentException("parameter " + key +
                                           " not part of the form model");
    }

    /**
     * @param m
     * @return 
     * @deprecated Use get(m.getName()) instead, and then manually check
     *             for model identity
     */
    public Object get(ParameterModel m) {
        ParameterData p = getParameter(m.getName());

        return ( p.getModel() == m ) ? p : null;
    }


    /**
     * Retrieves a date object for the specified parameter name.
     * @param key the object to retrieve
     * @return  a date object for the specified parameter name.
     *
     */
    public Date getDate(Object key) {
        return (Date) get(key);
    }

    /**
     * Retrieves an integer object for the specified parameter name.
     * @param key the object to retrieve
     * @return  an integer object for the specified parameter name.
     **/

    public Integer getInteger(Object key) {
        return (Integer) get(key);
    }

    /**
     * Retrieves a String object for the specified parameter name.
     * @param key the object to retrieve
     * @return  a string object for the specified parameter name.
     **/

    public String getString(Object key) {
        return (String) get(key);
    }


    @Override
    public boolean isEmpty() {
        return m_parameterDataValues.isEmpty();
    }

    @Override
    public Set keySet() {
        return m_parameterDataValues.keySet();
    }

    @Override
    public Object put(Object key, Object value) {
        Object previousValue = get(key);
        setParameterValue((String)key, value);
        m_isValid = false;
        return previousValue;
    }

    @Override
    public void putAll(Map t) {
        for (Iterator i = t.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            setParameterValue(key, t.get(key));
        }
        m_isValid = false;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return m_parameterDataValues.size();
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        FormData result = (FormData) super.clone();
        result.m_parameterDataValues = new HashMap();
        for (Iterator i= m_parameterDataValues.keySet().iterator();
             i.hasNext(); ) {
            Object key = i.next();
            ParameterData val = (ParameterData) m_parameterDataValues.get(key);
            result.m_parameterDataValues.put(key, val.clone());
        }
        if (m_formErrors != null) {
            result.m_formErrors = (LinkedList) m_formErrors.clone();
        }

        return result;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (Iterator i = getAllErrors(); i.hasNext();) {
            s.append(i.next()).append(System.getProperty("line.separator"));
        }

        return s.toString();
    }

    /**
     *  Converts to a String.
     *  The method {@link #toString()} returns all errors.
     * 
     *  @return a human-readable representation of <code>this</code>.
     */
    public String asString() {
        String newLine = System.getProperty("line.separator");
        StringBuilder to = new StringBuilder();
        to.append(super.toString() + " = {" + newLine);
        //Map
        to.append("m_parameterDataValues = ")
            .append(m_parameterDataValues).append(",").append(newLine);
        //LinkedList
        to.append("m_formErrors = " + m_formErrors + "," + newLine);
        //FormModel
        to.append("m_model = " + m_model + "," + newLine);
        to.append("m_locale = " + m_locale + "," + newLine);
        to.append("m_isTransformed = " + m_isTransformed + "," + newLine);
        to.append("m_isValid = " + m_isValid + "," + newLine);
        to.append("m_isSubmission = " + m_isSubmission + newLine);
        to.append("}");
        return to.toString();
    }
}
