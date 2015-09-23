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

import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Lockable;
import com.arsdigita.util.URLRewriter;
import com.arsdigita.web.RedirectSignal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A container for two classes of
 * objects: <tt>ParameterModels</tt> and <tt>ValidationListeners</tt>.</p>
 * <ul>
 * <li><tt>ParameterModels</tt> are associated
 * with the data objects that the user submits with the form.</li>
 * <li><tt>ValidationListeners</tt> provide custom
 * cross-checking of parameter values.</li>
 * </ul>
 * <p>Instances of this class provide a specification for transforming a
 * set of key-value string pairs into a set of validated Java data
 * objects.
 * A single instance of this
 * class can handle all submissions to a particular form.
 * <p>The most common usage for this class is
 * is to use a private variable in a servlet to store the
 * model, and to construct it in the servlet <code>init</code> method.
 * That way, the model persists for the lifetime of the servlet, reducing
 * the memory and processing overhead for each request.
 * <p>See the
 * Forms API Developer Guide for details on using the
 * <code>FormModel</code> class.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Stas Freidin
 * @author Rory Solomon
 * @version $Id: FormModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FormModel implements Lockable {

    private static final Logger s_log = Logger.getLogger(FormModel.class);

    private static final String MAGIC_TAG_PREFIX = "form.";

    private String m_name = null;
    private List m_parameterModels = null;
    private List m_parametersToExclude = null;
    private boolean m_locked = false;
    private boolean m_defaultOverridesNull;

    protected EventListenerList m_listenerList;

    /**
     * Constructs a new form model.
     *
     * @param name a URL-encoded keyword used to identify this form model
     * */
    public FormModel(String name) {
        this(name, false);
    }

    /**
     * Construct a new form model. The <code>defaultOverridesNull</code>
     * parameter is passed on to all parameter models that are added to the
     * form model. If it is <code>true</code>, the parameter model will use
     * the default value whenever it would normally set the parameter's value
     * to null, for example if the parameter is missing from the request. If
     * this value is <code>false</code>, the default parameter value will
     * only be used if the request being processed is not a submission, but
     * an initial request for the form model.
     *
     * <p> This method is only package-friendly since it is only useful to
     * the Page class. Everybody else should be happy with the public
     * constructor.
     *
     * @param name a URL-encoded keyword used to identify this form model
     *
     * @param defaultOverridesNull <code>true</code> if the default value for
     * parameters should be used whenever the value would be
     * <code>null</code> ordinarily.
     */
    FormModel(String name, boolean defaultOverridesNull) {
        Assert.exists(name, "Name");
        m_parameterModels = new LinkedList();
        m_parametersToExclude = new LinkedList();
        m_listenerList = new EventListenerList();
        m_name = name;
        m_defaultOverridesNull = defaultOverridesNull;
        m_parameterModels.addAll(URLRewriter.getGlobalModels());
    }

    /**
     * Returns the name of this form model.
     *
     * @return a URL-encoded keyword used to identify requests
     * conforming to this form model.
     * */
    public final String getName() {
        return m_name;
    }

    public final void setName(String name) {
        m_name = name;
    }

    String getMagicTagName() {
        return MAGIC_TAG_PREFIX + getName();
    }

    /**
     * Adds a parameter model to the form model. The parameter model
     * should be fully configured before adding it to the form model.
     *
     * @param parameter a parameter model object
     * */
    public final void addFormParam(ParameterModel parameter) {
        Assert.exists(parameter, "Parameter");
        Assert.isUnlocked(this);
        parameter.setDefaultOverridesNull(m_defaultOverridesNull);
        m_parameterModels.add(parameter);

        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Added parameter: " + parameter.getName() + "[" +
                         parameter.getClass().getName() + "]" );
        }
    }


    /**
     * Adds a parameter model to the list of parameters that should
     * not be exported when the form is rendered.  Useful examples
     * of this are for forms that loop back on themselves such as
     * search forms or a control bar. The parameter model
     * should be fully configured and have been added to the form model
     * before adding it to the list of items to exclude
     *
     * @param parameter a parameter model object
     * */
    public final void excludeFormParameterFromExport(ParameterModel parameter) {
        Assert.exists(parameter, "Parameter");
        Assert.isUnlocked(this);
        m_parametersToExclude.add(parameter);
    }


    /**
     * Determines whether the form model contains the specified parameter
     * model.
     * @param p the parameter model to check for
     * @return <code>true</code> if the form model contains the specified
     * parameter model; <code>false</code> otherwise.
     */
    public final boolean containsFormParam(ParameterModel p) {
        Assert.exists(p, "Parameter");
        return m_parameterModels.contains(p);
    }

    /**
     * Returns an iterator over the parameter models contained within
     * the form model.
     *
     * @return an iterator over the parameter models contained within
     * the form model.
     * */
    public final Iterator getParameters() {
        return m_parameterModels.iterator();
    }


    /**
     *  Returns an iterator over the parameter models that are
     *  contained within the form model but should not be exported
     *  as part of the form's state.  This is important for situations
     *  where the form loops back on itself (e.g. a ControlBar or
     *  a Search form).
     */
    public final Iterator getParametersToExclude() {
        return m_parametersToExclude.iterator();
    }

    /**
     * Adds a listener that is called as soon as the {@link FormData} has been
     * initialized with the request parameters, but before any of the init,
     * validation, or process listeners are run. The listener's
     * <code>submitted</code> method may throw a
     * <code>FormProcessException</code> to signal that any further
     * processing of the form should be aborted.
     *
     * @param listener a <code>FormSubmissionListener</code> value
     */
    public void addSubmissionListener(FormSubmissionListener listener) {
        Assert.exists(listener, "Submission Listener");
        m_listenerList.add(FormSubmissionListener.class, listener);
    }

    /**
     * Adds a validation listener, implementing a custom validation
     * check that applies to the form as a whole.  Useful for checks
     * that require examination of the values of more than one parameter.
     *
     * @param listener an instance of a class that implements the
     * <code>FormValidationListener</code> interface
     * */
    public void addValidationListener(FormValidationListener listener) {
        Assert.exists(listener, "FormValidationListener");
        Assert.isUnlocked(this);
        m_listenerList.add(FormValidationListener.class, listener);
    }

    /**
     * Adds a listener for form initialization events.
     * <p>Initialization events occur when a form is initially
     * requested by the user, but not when the form is subsequently
     * submitted. They typically
     * perform actions such as querying the database for existed values
     * to set up an edit form or obtaining a sequence value to set up a
     * create form.
     * @param listener an instance of a class that implements the
     * <code>FormInitListener</code> interface
     * */
    public void addInitListener(FormInitListener listener) {
        Assert.exists(listener, "FormInitListener");
        Assert.isUnlocked(this);
        m_listenerList.add(FormInitListener.class, listener);
    }

    /**
     * Adds a listener for form processing events.  <p>Process events
     * only occur after a form submission has been successfully
     * validated.  They are typically used to perform a database
     * transaction or other operation based on the submitted data.
     * <p>Process listeners are executed in the order in which they are
     * added.
     *
     * @param listener an instance of a class that implements the
     * <code>FormProcessListener</code> interface
     * */
    public void addProcessListener(FormProcessListener listener) {
        Assert.exists(listener, "FormProcessListener");
        Assert.isUnlocked(this);
        m_listenerList.add(FormProcessListener.class, listener);
    }

    /**
     * Creates a new FormData object that is populated with default values
     * (for an initial request) or values from the request (for
     * a submission).
     * <P>If this is a submission, validates the data and (if the
     * data is valid) calls the process listeners. Returns a FormData object.
     *
     * @param state the PageState object holding request-specific information
     * @return a FormData object.
     * */
    public FormData process(PageState state) throws FormProcessException {
        Assert.isLocked(this);
        boolean isSubmission =
            state.getRequest().getParameter(getMagicTagName()) != null;
        return process(state, isSubmission);
    }

    /**
     * Creates a new FormData object that is populated with default values
     * (for an initial request) or values from the request (for a
     * submission).
     * <P>If this is a submission, validates the data and (if the
     * data is valid) calls the process listeners. Returns a FormData object.
     *
     * @param state the PageState object holding request specific information
     * @param isSubmission <code>true</code> if the request is a submission;
     *        <code>false</code> if this is the first request to the form data.
     */
    public FormData process(PageState state, boolean isSubmission)
        throws FormProcessException {
        Assert.isLocked(this);
        FormData data = new FormData(this, state.getRequest(), isSubmission);
        try {
            process(state, data);
        } finally {
        }
        return data;
    }


    /**
     * Do the work for the public process method.  Uses the
     * prepopulated <code>FormData</code> and runs listeners on it as
     * needed.
     *
     * @throws FormProcessException if an error occurs
     */
    void process(final PageState state, final FormData data)
            throws FormProcessException {
        s_log.debug("Processing the form model");

        final FormSectionEvent e = new FormSectionEvent(this, state, data);

        if (data.isSubmission()) {
            s_log.debug("The request is a form submission; running " +
                        "submission listeners");

            try {
                fireSubmitted(e);
            } catch (FormProcessException fpe) {
                s_log.debug("A FormProcessException was thrown while firing " +
                            "submit; aborting further processing");
                return;
            } finally {
            }

            
            try {
                s_log.debug("Validating parameters");
                fireParameterValidation(e);
                
                s_log.debug("Validating form");
                fireFormValidation(e);
            } finally {
            }

            if (data.isValid()) {
                s_log.debug("The form data is valid; running process " +
                            "listeners");

                try {
                    fireFormProcess(e);
                } catch (FormProcessException fpe) {
                s_log.debug("A FormProcessException was thrown while " +
                            "initializing the form; storing the error", fpe);

                data.addError("Initialization Aborted: " + fpe.getMessages());
                } finally {
                }
            } else {
                s_log.debug("The form data was not valid; this form " +
                            "will not run its process listeners");
            }
        } else {
            s_log.debug("The request is not a form submission; " +
                        "running init listeners");

            try {
                fireFormInit(e);
            } catch (FormProcessException fpe) {
                s_log.debug("A FormProcessException was thrown while " +
                            "initializing the form; storing the error", fpe);

                data.addError("Initialization Aborted: " + fpe.getMessages());
            } finally {
            }
        }
    }

    protected void fireSubmitted(FormSectionEvent e)
        throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        Assert.isLocked(this);
        FormProcessException delayedException = null;

        Iterator i = m_listenerList.getListenerIterator(FormSubmissionListener.class);
        while (i.hasNext()) {
            try {
                ((FormSubmissionListener) i.next()).submitted(e);
            } catch (FormProcessException ex) {
                delayedException = ex;
            }
        }
        if ( delayedException != null ) {
            throw delayedException;
        }
    }

    /**
     * Calls a form initialization listener.
     *
     * @param e a FormSectionEvent originating from the form
     */
    protected void fireFormInit(FormSectionEvent e) throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        Assert.isLocked(this);
        Iterator i = m_listenerList.getListenerIterator(FormInitListener.class);
        while (i.hasNext()) {
            ((FormInitListener) i.next()).init(e);
        }
    }

    /**
     * Private helper method that validates the individual parameters by
     * calling ParameterValidationListeners from the individual
     * parameterModels.
     *
     * @param e a FormSectionEvent originating from the form
     * */
    protected void fireParameterValidation(FormSectionEvent e) {
        FormData data = e.getFormData();
        Assert.exists(data, "FormData");
        Iterator parameters = getParameters();
        ParameterModel parameterModel;
        ParameterData parameterData;
        while (parameters.hasNext()) {
            parameterModel = (ParameterModel) parameters.next();
            parameterData = (ParameterData) data.getParameter(parameterModel.getName());
            try {
                parameterData.validate();
                if (!parameterData.isValid()) {
                    data.invalidate();
                }
            } catch (FormProcessException fpe) {
                data.addError("Processing Listener Error: " + fpe.getMessage());
            }
        }
    }

    /**
     * Private helper method. Validates the form by calling
     * FormValidationListeners
     *
     * @param e a FormSectionEvent originating from the Form
     * */
    private void fireFormValidation(FormSectionEvent e) {
        FormData data = e.getFormData();
        Assert.exists(data, "FormData");
        Iterator i = m_listenerList.getListenerIterator(FormValidationListener.class);
        while (i.hasNext()) {
            try {
                ((FormValidationListener) i.next()).validate(e);
            } catch (FormProcessException fpe) {
                data.addError(fpe.getMessage());
            }
        }
    }

    /**
     * Call form process listeners. <p>Form processing is performed
     * <em>after</em> the form has been validated.</p>
     *
     * @param e a FormSectionEvent originating from the form
     * */
    private void fireFormProcess(FormSectionEvent e)
        throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        if (!e.getFormData().isValid()) {
            throw new IllegalStateException("Request data must be valid " + "prior to running processing filters.");
        }
        Iterator i = m_listenerList.getListenerIterator(FormProcessListener.class);

        RedirectSignal redirect = null;
        while (i.hasNext()) {
            try {
                ((FormProcessListener) i.next()).process(e);
            } catch( RedirectSignal signal ) {
                if( s_log.isDebugEnabled() ) {
                    s_log.debug( "Delaying redirect to " +
                                 signal.getDestinationURL() );
                }

                if( null != redirect ) {
                    s_log.error( "Non-deterministic redirect. Ignoring earlier occurrence.", redirect );
                }

                redirect = signal;
            }
        }

        if( null != redirect ) throw redirect;
    }

    /**
     * Call form validation listeners. Listeners that encounter
     * validation errors report them directly to the
     * <code>FormData</code> object. <p>Form validation is performed
     * <em>after</em> the initial transformation of key-value string
     * pairs into Java data objects is complete.
     *
     * @param state the page state for this request
     *
     * @param data the FormData object to validate
     *
     * @pre data != null
     * */
    void validate(PageState state, FormData data) {
        Assert.exists(data, "FormData");
        if (!data.isTransformed()) {
            throw new IllegalStateException("Request data must be transformed " + "prior to running validation filters.");
        }
        fireParameterValidation(new FormSectionEvent(this, state, data));
        fireFormValidation(new FormSectionEvent(this, state, data));
    }

    /**
     * Merge the parameterModels and Listeners from the supplied
     * FormModel into the current FormModel. This method is useful when
     * registering FormSections in Forms.
     *
     * @param m The FormModel to be merged into this FormModel
     * */
    void mergeModel(FormModel m) {
        Assert.isUnlocked(this);
        Assert.exists(m, "FormSection's FormModel");
        m_parameterModels.addAll(m.m_parameterModels);
        m_listenerList.addAll(m.m_listenerList);
    }

    /**
     * Locks this FormModel and all of its ParameterModels.
     * */
    public void lock() {
        for (Iterator i = getParameters(); i.hasNext(); ) {
            ((ParameterModel) i.next()).lock();
        }
        m_locked = true;
    }

    /**
     * Checks whether this FormModel is locked.
     *
     * @return <code>true</code> if this FormModel is locked;
     * <code>false</code> otherwise.
     * */
    public final boolean isLocked() {
        return m_locked;
    }
}
