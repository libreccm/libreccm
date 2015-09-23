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
import com.arsdigita.bebop.event.FormCancelListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * A standalone section of a <code>Form</code>. A <code>FormSection</code>
 * contains other Bebop components, most importantly
 * <code>Widgets</code> and associated listeners. It serves two purposes:
 * <UL>
 * <LI>Divides a form into visual sections</LI>
 * <LI>Serves as a container for form fragments that can function by themselves 
 *     and can be dropped into other forms</LI>
 * </UL>
 * <p>Since a <code>FormSection</code> has its own init, validation, and
 * process listeners, it can do all of its processing without any intervention
 * from the enclosing form.
 *
 * Although a <code>FormSection</code> contains all the same pieces
 * that a <code>Form</code> does, it can only be used if it is added
 * directly or indirectly to a <code>Form</code>. <code>FormSection</code>s
 * that are not contained in a <code>Form</code> do not exhibit any useful
 * behavior.
 *
 * @see Form
 * @see FormModel
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Stas Freidin
 * @author Rory Solomon
 * @author David Lutterkort
 *
 * @version $Id: FormSection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FormSection extends DescriptiveComponent implements Container {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.subsite.FormSection=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(FormSection.class);

    /** Underlying <code>FormModel</code> that stores
     *  the parameter models for all the widgets in this form section.        */
    protected FormModel m_formModel;

    /** The container to which all children are added. A
     *  <code>ColumnPanel</code> by default.                                  */
    protected Container m_panel;

    /** Contains all the listeners that were added with the various
     *  addXXXListener methods.
     *  We maintain our own list of listeners, so that we can re-send the
     *  events the FormModel generates, but with us as the source, not the
     *  FormModel.                                                            */
    private EventListenerList m_listeners;

    /** Listeners we attach to the FormModel to forward
     * form model events to our listeners with the right source               */
    private FormSubmissionListener m_forwardSubmission;

    private FormInitListener m_forwardInit;
    private FormValidationListener m_forwardValidation;
    private FormProcessListener m_forwardProcess;

    /**
     * Constructs a new form section. Sets the implicit layout Container of
     * this <code>FormSection</code> to two column <code>ColumnPanel</code>
     * by calling the 1-argument constructor.
     **/
    public FormSection() {
        this(new ColumnPanel(2, true));
    }

    /**
     * Constructs a new form section. Sets the form model of this
     * <code>FormSection</code> to a new, anonymous FormModel.
     * 
     * @param panel
     **/
    public FormSection(Container panel) {
        this(panel, new FormModel("anonymous"));
    }

    /**
     * Constructs a new form section. Sets the implicit layout Container of
     * this <code>FormSection</code> to <code>panel</code>.  Sets the form
     * model of this <code>FormSection</code> to <code>model</code>.
     *
     * @param panel the container within this form section that holds the
     * components that are added to the form section with calls to the
     * <code>add</code> methods
     *
     * @param model the form model for this form section
     **/
    protected FormSection(Container panel, FormModel model) {
        super();
        m_panel = panel;
        m_formModel = model;
        m_listeners = new EventListenerList();
    }

    /**
     * Adds a listener that is called as soon as the {@link FormData} has been
     * initialized with the request parameters but before any of the init,
     * validation, or process listeners are run. The listener's
     * <code>submitted</code> method may throw a
     * <code>FormProcessException</code> to signal that any further
     * processing of the form should be aborted.
     *
     * @param listener a submission listener to run every time the form is
     * submitted
     * @see FormModel#addSubmissionListener
     * @pre listener != null
     */
    public void addSubmissionListener(FormSubmissionListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding submission listener " + listener + " to " + this);
        }

        Assert.exists(listener, "Submission Listener");
        Assert.isUnlocked(this);
        forwardSubmission();
        m_listeners.add(FormSubmissionListener.class, listener);
    }

    /**
     * Removes the specified submission listener from the
     * list of submission listeners (if it had previously been added).
     *
     * @param listener the submission listener to remove
     */
    public void removeSubmissionListener(FormSubmissionListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing submission listener " + listener + " from "
                        + this);
        }

        Assert.exists(listener, "Submission Listener");
        Assert.isUnlocked(this);
        m_listeners.remove(FormSubmissionListener.class, listener);
    }

    /**
     * Calls the <code>submitted</code> method on all registered submission
     * listeners.
     *
     * @param e the event to pass to the listeners
     * @throws FormProcessException if one of the listeners throws such an
     * exception.
     */
    protected void fireSubmitted(FormSectionEvent e)
            throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        FormProcessException delayedException = null;

        Iterator i = m_listeners.getListenerIterator(
                FormSubmissionListener.class);
        while (i.hasNext()) {
            final FormSubmissionListener listener = (FormSubmissionListener) i.
                    next();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Firing submission listener " + listener);
            }

            try {
                listener.submitted(e);
            } catch (FormProcessException ex) {
                s_log.debug(ex);
                delayedException = ex;
            }
        }
        if (delayedException != null) {
            throw delayedException;
        }
    }

    /**
     * 
     */
    protected void forwardSubmission() {
        if (m_forwardSubmission == null) {
            m_forwardSubmission = createSubmissionListener();
            getModel().addSubmissionListener(m_forwardSubmission);
        }
    }

    /**
     * Creates the submission listener that forwards submission events to this
     * form section.
     *
     * @return a submission listener that forwards submission events to this
     * form section.
     */
    protected FormSubmissionListener createSubmissionListener() {
        return new FormSubmissionListener() {

            @Override
            public void submitted(FormSectionEvent e)
                    throws FormProcessException {
                fireSubmitted(new FormSectionEvent(FormSection.this,
                                                   e.getPageState(),
                                                   e.getFormData()));
            }
        };
    }

    /**
     * Adds a listener for form initialization events. Initialization
     * events occur when a form is initially requested by the user, but
     * not when the form is subsequently submitted.  They typically
     * perform actions such as querying the database for existing values
     * to set up an edit form, or obtaining a sequence value to set up a
     * create form.
     *
     * @param listener an instance of a class that implements the
     * <code>FormInitListener</code> interface
     * */
    public void addInitListener(FormInitListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding init listener " + listener + " to " + this);
        }

        Assert.exists(listener, "FormInitListener");
        Assert.isUnlocked(this);
        forwardInit();
        m_listeners.add(FormInitListener.class, listener);
    }

    /**
     * Removes the specified init listener from the
     * list of init listeners (if it had previously been added).
     *
     * @param listener the init listener to remove
     */
    public void removeInitListener(FormInitListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing init listener " + listener + " from " + this);
        }

        Assert.exists(listener, "Init Listener");
        Assert.isUnlocked(this);
        m_listeners.remove(FormInitListener.class, listener);
    }

    /**
     * Calls the <code>init</code> method on all registered init
     * listeners.
     *
     * @param e the event to pass to the listeners
     * @throws FormProcessException if one of the listeners throws such an
     * exception.
     */
    protected void fireInit(FormSectionEvent e) throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        Assert.isLocked(this);
        Iterator i = m_listeners.getListenerIterator(FormInitListener.class);
        while (i.hasNext()) {
            final FormInitListener listener = (FormInitListener) i.next();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Firing init listener " + listener);
            }

            listener.init(e);
        }
    }

    /**
     * 
     */
    protected void forwardInit() {
        if (m_forwardInit == null) {
            m_forwardInit = createInitListener();
            getModel().addInitListener(m_forwardInit);
        }
    }

    /**
     * Creates the init listener that forwards init events to this form
     * section.
     *
     * @return an init listener that forwards init events to this
     * form section.
     */
    protected FormInitListener createInitListener() {
        return new FormInitListener() {

            @Override
            public void init(FormSectionEvent e)
                    throws FormProcessException {
                fireInit(new FormSectionEvent(FormSection.this,
                                              e.getPageState(),
                                              e.getFormData()));
            }
        };
    }

    /**
     * Creates the cancel listener that forwards cancel events to this form
     * section
     *
     * @return an cancel listener
     */
    protected FormCancelListener createCancelListener() {
        return new FormCancelListener() {

            @Override
            public void cancel(FormSectionEvent e) throws FormProcessException {
                fireCancel(new FormSectionEvent(FormSection.this,
                                                e.getPageState(),
                                                e.getFormData()));
            }
        };
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
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding validation listener " + listener + " to " + this);
        }

        Assert.exists(listener, "FormValidationListener");
        Assert.isUnlocked(this);
        forwardValidation();
        m_listeners.add(FormValidationListener.class, listener);
    }

    /**
     * Removes the specified validation listener from the
     * list of validation listeners (if it had previously been added).
     *
     * @param listener a validation listener
     */
    public void removeValidationListener(FormValidationListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing validation listener " + listener + " from "
                        + this);
        }

        Assert.exists(listener, "Validation Listener");
        Assert.isUnlocked(this);
        m_listeners.remove(FormValidationListener.class, listener);
    }

    /**
     * Calls the <code>validate</code> method on all registered validation
     * listeners.
     *
     * @param e the event to pass to the listeners
     */
    protected void fireValidate(FormSectionEvent e) {
        FormData data = e.getFormData();
        Assert.exists(data, "FormData");
        Iterator i = m_listeners.getListenerIterator(
                FormValidationListener.class);
        while (i.hasNext()) {
            try {
                final FormValidationListener listener =
                                             (FormValidationListener) i.next();

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Firing validation listener " + listener);
                }

                listener.validate(e);
            } catch (FormProcessException fpe) {
                s_log.debug(fpe);
                data.addError(fpe.getGlobalizedMessage());
            }
        }
    }

    protected void forwardValidation() {
        if (m_forwardValidation == null) {
            m_forwardValidation = createValidationListener();
            getModel().addValidationListener(m_forwardValidation);
        }
    }

    /**
     * Create the validation listener that forwards validation events to this
     * form section.
     *
     * @return a validation listener that forwards validation events to this
     * form section.
     */
    protected FormValidationListener createValidationListener() {
        return new FormValidationListener() {

            @Override
            public void validate(FormSectionEvent e) {
                fireValidate(new FormSectionEvent(FormSection.this,
                                                  e.getPageState(),
                                                  e.getFormData()));
            }
        };
    }

    /**
     * Adds a listener for form processing events.  <p>Process events
     * only occur after a form submission has been successfully
     * validated.  They are typically used to perform a database
     * transaction or other operation based on the submitted data.
     * <p>Process listeners are executed in the order in which
     * they are added.
     *
     * @param listener an instance of a class that implements the
     * <code>FormProcessListener</code> interface
     * */
    public void addProcessListener(final FormProcessListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding process listener " + listener + " to " + this);
        }

        Assert.exists(listener, "FormProcessListener");
        Assert.isUnlocked(this);

        forwardProcess();
        m_listeners.add(FormProcessListener.class, listener);
    }

    /**
     * Removes the specified process listener from the
     * list of process listeners (if it had previously been added).
     *
     * @param listener the process listener to remove
     */
    public void removeProcessListener(FormProcessListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing process listener " + listener + " from "
                        + this);
        }

        Assert.exists(listener, "Process Listener");
        Assert.isUnlocked(this);

        m_listeners.remove(FormProcessListener.class, listener);
    }

    protected void forwardProcess() {
        if (m_forwardProcess == null) {
            m_forwardProcess = createProcessListener();
            getModel().addProcessListener(m_forwardProcess);
        }
    }

    protected FormProcessListener createProcessListener() {
        return new FormProcessListener() {

            @Override
            public void process(FormSectionEvent e)
                    throws FormProcessException {
                fireProcess(new FormSectionEvent(FormSection.this,
                                                 e.getPageState(),
                                                 e.getFormData()));
            }
        };
    }

    /**
     * Calls the <code>process</code> method on all registered process
     * listeners.
     *
     * @param e the event to pass to the listeners
     * @throws FormProcessException if one of the listeners throws such an
     * exception.
     */
    protected void fireProcess(FormSectionEvent e)
            throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        Iterator i = m_listeners.getListenerIterator(FormProcessListener.class);
        while (i.hasNext()) {
            final FormProcessListener listener = (FormProcessListener) i.next();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Firing process listener " + listener);
            }

            listener.process(e);
        }
    }

    /**
     * Since a form section cannot be processed, always throws an error.
     * (Processing of form sections is done by the form in which the
     * section is contained.)
     *
     * @param data
     * @return 
     * @throws javax.servlet.ServletException because processing a form section 
     *         is not meaningful.
     */
    public FormData process(PageState data)
            throws javax.servlet.ServletException {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a listener for form cancellation events.  Cancellation
     * listeners are typically used to clean-up page state and
     * potentially intermediate changes to the database.
     *
     * @param listener an instance of a class that implements the
     * <code>FormCancelListener</code> interface
     * */
    public void addCancelListener(FormCancelListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding cancel listener " + listener + " to " + this);
        }

        Assert.exists(listener, "FormCancelListener");
        Assert.isUnlocked(this);
        m_listeners.add(FormCancelListener.class, listener);
    }

    /**
     * Removes the specified cancellation listener from the
     * list of cancellation listeners (if it had previously been added).
     *
     * @param listener the cancellation listener to remove
     */
    public void removeCancelListener(FormCancelListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing cancel listener " + listener + " from " + this);
        }

        Assert.exists(listener, "Cancel Listener");
        Assert.isUnlocked(this);
        m_listeners.remove(FormCancelListener.class, listener);
    }

    /**
     * Calls the <code>cancel</code> method on all registered cancellation
     * listeners.
     *
     * @param e the event to pass to the listeners
     * @throws FormProcessException if one of the listeners throws such an
     * exception.
     */
    protected void fireCancel(FormSectionEvent e)
            throws FormProcessException {
        Assert.exists(e.getFormData(), "FormData");
        Iterator i = m_listeners.getListenerIterator(FormCancelListener.class);
        while (i.hasNext()) {
            final FormCancelListener listener = (FormCancelListener) i.next();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Firing cancel listener " + listener);
            }

            listener.cancel(e);
        }
    }

    /**
     * Traverses the children this FormSection, collecting parameter models
     * and listeners into the supplied FormModel. Sets implicit pointers
     * of widgets in this FormSection to the supplied Form.
     *
     * @param f pointer to the form that is set inside Widgets within this
     *          FormSection
     * @param m the FormModel in which to merge ParameterModels and
     *          Listeners
     * */
    @Override
    public void register(Form f, FormModel m) {
        m.mergeModel(getModel());
    }

    /**
     * Accessor method for this form's FormModel.
     *
     * @return FormModel The model of this form.
     * */
    protected final FormModel getModel() {
        return m_formModel;
    }

    /**
     * Locks this FormSection, its FormModel, and the implicit Container.
     * */
    @Override
    public void lock() {
        m_formModel.lock();
        m_panel.lock();
        super.lock();
    }

    @Override
    public void respond(PageState state) throws javax.servlet.ServletException {
        //call listeners here.
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the implicit Container of this FormSection.
     *
     * This must not be final, because MetaFrom needs to override it.
     *
     * @return 
     */
    public Container getPanel() {
        return m_panel;
    }

    /**
     * Returns an iterator over the children of this component. If the
     * component has no children, returns an empty iterator (not
     * <code>null</code> !).
     *
     * @post return != null
     * */
    @Override
    public Iterator children() {
        return m_panel.children();
    }

    /**
     * Builds an XML subtree for this component under the specified
     * <code>parent</code>. Uses the request values stored in
     * <code>state</code>.</p>
     *
     * <p> This method generates DOM to be used with the XSLT template
     * to produce the appropriate output.</p>
     *
     * @param pageState the state of the current page
     * @param parent the node that will be used to write to
     * */
    @Override
    public void generateXML(PageState pageState, Element parent) {
        if (isVisible(pageState)) {
            m_panel.generateXML(pageState, parent);
        }
    }

    // Container methods
    /**
     * Adds a component to this container.
     *
     * @param pc the component to add to this container
     * */
    @Override
    public void add(Component pc) {
        m_panel.add(pc);
    }

    /**
     * Adds a component with the specified layout constraints to this
     * container. Layout constraints are defined in each layout container as
     * static ints. Use a bitwise OR to specify multiple constraints.
     *
     * @param pc the component to add to this container
     * @param constraints layout constraints (a bitwise OR of static ints in 
     *                    the particular layout)
     */
    @Override
    public void add(Component pc, int constraints) {
        m_panel.add(pc, constraints);
    }

    /**
     * Returns <code>true</code> if this list contains the
     * specified element. More
     * formally, returns true if and only if this list contains at least
     * one element e such that (o==null ? e==null : o.equals(e)).
     *
     * This method returns <code>true</code> only if the component has
     * been directly
     * added to this container. If this container contains another
     * container that contains this component, this method returns
     * <code>false</code>.
     *
     * @param  o element whose presence in this container is to be tested
     *
     * @return <code>true</code> if this Container contains the
     * specified component directly; <code>false</code> otherwise.
     *
     * */
    @Override
    public boolean contains(Object o) {
        return m_panel.contains(o);
    }

    /**
     *  Returns the
     * Component at the specified position. Each call to add()
     * increments the index. This method should be used in conjunction
     * with indexOf
     *
     * @param index The index of the item to be retrieved from this
     * Container. Since the user has no control over the index of added
     * components (other than counting each call to add), this method
     * should be used in conjunction with indexOf.
     *
     * @return the component at the specified position in this container
     * */
    @Override
    public Component get(int index) {
        return (Component) m_panel.get(index);
    }

    /**
     *
     *
     *
     * @param pc component to search for
     *
     * @return the index in this list of the first occurrence of
     * the specified element, or -1 if this list does not contain this
     * element.
     * */
    @Override
    public int indexOf(Component pc) {
        return m_panel.indexOf(pc);
    }

    /**
     * Determines whether the container contains any components.
     *
     * @return <code>true</code> if this container contains no components
     * <code>false</code> otherwise.
     * */
    @Override
    public boolean isEmpty() {
        return m_panel.isEmpty();
    }

    /**
     * Returns the number of elements in this container. This does not
     * recursively count the components indirectly contained in this container.
     *
     * @return the number of components directly in this container.
     * */
    @Override
    public int size() {
        return m_panel.size();
    }
}
