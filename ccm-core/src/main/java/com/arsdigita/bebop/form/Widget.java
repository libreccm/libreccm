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
package com.arsdigita.bebop.form;

import java.util.Collections;
import java.util.Iterator;
import java.util.TooManyListenersException;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.DescriptiveComponent;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.globalization.GlobalizedMessage;
// import com.arsdigita.kernel.Kernel;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * <p>
 * A class representing a widget in the graphical representation of a form.
 * </p>
 *
 * <p>
 * A widget may correspond to a standard HTML form element, or to a more 
 * specific element or set of elements, such as a date widget that allows 
 * input of month, day and year (and possibly time as well).</p>
 *
 * <p>
 * This class and its subclasses provide methods to set all element attributes 
 * except for <code>VALUE</code>, which is typically dependent on the request. 
 * At the time of a request, a widget object merges a dynamically specified 
 * value or set of values with its own set of persistent attributes to render 
 * the final HTML for the widget. Other dynamic attributes may be associated with
 * the form component via a <code>WidgetPeer</code> associated with the widget.
 * </p>
 * <p>
 * The parent class provides the Label (the localized title) for the widget as
 * well as a (localized) hint as a kind of online manual for the user.
 * </p>
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Rory Solomon
 * @version $Id$
 */
public abstract class Widget extends DescriptiveComponent
                             implements Cloneable, BebopConstants {

    private static final Logger s_log = Logger.getLogger(Widget.class);

    private ParameterModel m_parameterModel;
    private final EventListenerList m_listeners = new EventListenerList();
    private ParameterListener m_forwardParameter = null;
    private PrintListener m_printListener;
    private Form m_form;

    private ValidationGuard m_guard = null;

    // This controls whether or not validation listeners are fired when the
    // widget is not visible. By default this is true.
    private boolean m_validateInvisible = true;

    static final String ON_FOCUS = "onFocus";
    static final String ON_BLUR = "onBlur";
    static final String ON_SELECT = "onSelect";
    static final String ON_CHANGE = "onChange";
    static final String ON_KEY_UP = "onKeyUp";

    /**
     * Constructor, creates a new widget.
     *
     * @param name of the widget, used to address the instance.
     */
    protected Widget(String name) {
        this(new StringParameter(name));
    }

    /**
     * Constructor, creates a new widget.
     *
     * <p>
     * Each new widget is associated with a ParameterModel describing the 
     * data object(s) submitted
     * from the widget.
     *
     * @param model
     */
    protected Widget(ParameterModel model) {

        Assert.exists(model, ParameterModel.class);
        m_parameterModel = model;
    }

    /**
     * Returns true if the widget consists of multiple HTML elements.
     *
     * @return
     */
    public abstract boolean isCompound();

    /**
     * Returns a string naming the type of this widget. Must be implemented by subclasses!
     *
     * @return
     */
    protected abstract String getType();

    /**
     *
     * @return
     */
    protected ParameterListener createParameterListener() {
        return new ParameterListener() {

            @Override
            public void validate(ParameterEvent evt)
                throws FormProcessException {
                fireValidation(new ParameterEvent(Widget.this, evt.getParameterData()));
            }

        };
    }

    public void setValidateInvisible(boolean value) {
        Assert.isUnlocked(this);
        m_validateInvisible = value;
    }

    public boolean validateInvisible() {
        return m_validateInvisible;
    }

    protected void fireValidation(ParameterEvent evt)
        throws FormProcessException {
        Assert.isLocked(this);

        PageState ps = evt.getPageState();

        if ((!validateInvisible() && !ps.isVisibleOnPage(this))
                || ((m_guard != null) && m_guard.shouldValidate(ps))) {
            return;
        }

        for (Iterator it
                          = m_listeners.getListenerIterator(ParameterListener.class);
             it.hasNext();) {
            ((ParameterListener) it.next()).validate(evt);
        }
    }

    public void addValidationListener(ParameterListener listener) {
        Assert.exists(listener, "ParameterListener");
        Assert.isUnlocked(this);
        if (m_forwardParameter == null) {
            m_forwardParameter = createParameterListener();
            m_parameterModel.addParameterListener(m_forwardParameter);
        }
        m_listeners.add(ParameterListener.class, listener);
    }

    public void removeValidationListener(ParameterListener listener) {
        Assert.exists(listener, "ParameterListener");
        Assert.isUnlocked(this);
        m_listeners.remove(ParameterListener.class, listener);
    }

    /**
     * Test for existens of a particular type of ValidationListener
     *
     * @param listener Subtype of ParameterListern which is tested for
     *
     * @return true if subtype is in the list
     */
    public boolean hasValidationListener(ParameterListener listener) {
        Assert.exists(listener, "ParameterListener");
        return this.getParameterModel().hasParameterListener(listener);

//        return (m_listeners.getListenerCount(listener.getClass()) > 0);
    }

    /**
     * Adds a print listener for this widget. Only one print listener can be set for a widget, since
     * the <code>PrintListener</code> is expected to modify the target of the
     * <code>PrintEvent</code>.
     *
     * @param listener the print listener
     *
     * @throws IllegalArgumentException  <code>listener</code> is null
     * @throws TooManyListenersException a print listener has previously been added
     * @pre listener != null
     */
    public void addPrintListener(PrintListener listener)
        throws TooManyListenersException, IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Argument listener can not be null");
        }
        if (m_printListener != null) {
            throw new TooManyListenersException();
        }
        m_printListener = listener;
    }

    /**
     * Set the print listener for this widget. Since there can only be one print listener for a
     * widget, this lets you just set it and avoid writing a try/catch block for
     * "TooManyListenersException". Any existing listener will be overwritten.
     *
     * @param listener the print listener
     *
     * @throws IllegalArgumentException <code>listener</code> is null
     * @pre listener != null
     */
    public void setPrintListener(PrintListener listener)
        throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Argument listener can not be null");
        }
        m_printListener = listener;
    }

    /**
     * Remove a previously added print listener. If <code>listener</code> is not the listener that
     * has been added with {@link #addPrintListener addPrintListener}, an IllegalArgumentException
     * will be thrown.
     *
     * @param listener the listener that had been added with <code>addPrintListener</code>
     *
     * @throws IllegalArgumentException <code>listener</code> is not the currently registered print
     *                                  listener or is <code>null</code>.
     * @pre listener != null
     */
    public void removePrintListener(PrintListener listener)
        throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (listener != m_printListener) {
            throw new IllegalArgumentException("listener is not registered with this widget");
        }
        m_printListener = null;
    }

    /**
     * Registers the ParameterModel of this Widget with the containing Form. This method is used by
     * the Bebop framework and should not be used by application developers.
     *
     * @param form
     * @param model
     */
    @Override
    public void register(Form form, FormModel model) {
        model.addFormParam(getParameterModel());

        setForm(form);
    }

    /**
     * Sets the Form Object for this Widget. This method will throw an exception if the _form
     * pointer is already set. To explicity change the m_form pointer the developer must first call
     * setForm(null)
     *
     * @param form The <code>Form</code> Object for this Widget
     *
     * @exception IllegalStateException if form already set.
     */
    public void setForm(final Form form) {
        if (m_form != null && form != null) {
            throw new IllegalStateException("Form " + form.getName()
                                                + " already set for "
                                                + getName());
        }

        m_form = form;
    }

    /**
     * Gets the Form Object for this Widget. Throws an exception if the Widget doesn't belong to a
     * form.
     *
     * @return the {@link Form} Object for this Widget.
     *
     * @post return != null
     */
    public Form getForm() throws RuntimeException {
        if (m_form == null) {
            throw new RuntimeException("Widget " + this + " (" + getName() + ") "
                                           + "isn't associated with any Form");
        }

        return m_form;
    }

    /**
     * Sets the <tt>ONFOCUS</tt> attribute for the HTML tags that compose this element.
     *
     * @param javascriptCode
     */
    public void setOnFocus(String javascriptCode) {
        setAttribute(ON_FOCUS, javascriptCode);
    }

    /**
     * Sets the <tt>ONBLUR</tt> attribute for the HTML tags that compose this element.
     *
     * @param javascriptCode
     */
    public void setOnBlur(String javascriptCode) {
        setAttribute(ON_BLUR, javascriptCode);
    }

    /**
     * Sets the <tt>ONSELECT</tt> attribute for the HTML tags that compose this element.
     *
     * @param javascriptCode
     */
    public void setOnSelect(String javascriptCode) {
        setAttribute(ON_SELECT, javascriptCode);
    }

    /**
     * Sets the <tt>ONCHANGE</tt> attribute for the HTML tags that compose this element.
     *
     * @param javascriptCode
     */
    public void setOnChange(String javascriptCode) {
        setAttribute(ON_CHANGE, javascriptCode);
    }

    /**
     * Sets the <tt>ON_KEY_UP</tt> attribute for the HTML tags that compose this element.
     *
     * @param javascriptCode
     */
    public void setOnKeyUp(String javascriptCode) {
        setAttribute(ON_KEY_UP, javascriptCode);
    }

    /**
     * Sets the default value in the parameter model for this element. This is a static property and
     * this method should not be invoked at request time (not even in a PrintListener).
     *
     * @param value
     */
    public void setDefaultValue(Object value) {
        m_parameterModel.setDefaultValue(value);
    }

    /**
     * Marks this widget as readonly, which has the effect of preventing the user from modifying the
     * widget's contents. This method can only be called on unlocked widgets.
     */
    public void setReadOnly() {
        Assert.isUnlocked(this);
        setAttribute("readonly", "readonly");
    }

    /**
     * Marks this widget as disabled, which has the effect of preventing the widget's value being
     * submitted with the form, and will typically cause the widget to be 'grayed out' on the form.
     * This method can only be called on unlocked widgets.
     */
    public void setDisabled() {
        Assert.isUnlocked(this);
        setAttribute("disabled", "disabled");
    }

    /**
     * Sets a popup hint for the widget.
     *
     * @param hint
     *
     * @deprecated refactor to use a GlobalizedMessage instead and use setHint(GlobalizedMessage
     * hint)
     */
    public void setHint(String hint) {
        Assert.isUnlocked(this);
        setAttribute("hint", hint);
    }

    /**
     * Sets a popup hint for the widget.
     *
     * @param hint
     */
// Use parent's class method instead
//  @Override
//  public void setHint(GlobalizedMessage hint) {
//      Assert.isUnlocked(this);
//      setAttribute("hint", (String) hint.localize());
//  }
    /**
     * Sets a Label for the widget.
     *
     * @param label
     */
// Use parent's class method instead
//  public void setLabel(GlobalizedMessage label) {
//      m_label = label;
//  }
    /**
     * Sets a Label for the widget.
     *
     * @return
     */
// Use parent's class method instead
//  public GlobalizedMessage getLabel() {
//      return m_label;
//  }
    /**
     * Gets the default value in the parameter model for this element.
     *
     * @return
     */
    public String getDefaultValue() {
        Object o = m_parameterModel.getDefaultValue();
        if (o == null) {
            return null;
        }
        return o.toString();
    }

    public String getName() {
        return m_parameterModel.getName();
    }

    /**
     * The "pass in" property determines whether the value for this parameter is generally passed in
     * from the outside. If this property is <code>true</code>, the model always tries to get the
     * parameter value from the request, no matter whether the request is the initial request or a
     * submission of the form to which the widget belongs.
     *
     * <p>
     * If this property is <code>false</code>, the parameter value is only read from the request if
     * it is a submission of the form containing the widget.
     *
     * <p>
     * By default, this property is <code>false</code>.
     *
     * @return <code>true</code> if an attempt should always be made to retrieve the parameter value
     *         from the request.
     */
    public final boolean isPassIn() {
        return getParameterModel().isPassIn();
    }

    /**
     * Set whether this parameter should be treated as a "pass in" parameter. This is a static
     * property of the ParameterModel and this method should not be invoked at request-time.
     *
     * @see #isPassIn
     * @param v <code>true</code> if this parameter is a pass in parameter.
     */
    public final void setPassIn(boolean v) {
        Assert.isUnlocked(this);
        getParameterModel().setPassIn(v);
    }

    /**
     * The ParameterModel is normally set via the constructors. This method is only rarely needed.
     * Please note that the previous ParameterModel and all its listeners will be lost.
     *
     * @param parameterModel
     */
    public final void setParameterModel(ParameterModel parameterModel) {
        Assert.isUnlocked(this);
        m_parameterModel = parameterModel;
    }

    /**
     * Allows access to underlying parameterModel. The ParameterModel contains static
     * (request-independent) properties of a Widget such as its name, default value and its
     * listeners. The ParameterModel can not be modified once Page.lock() has been invoked (not even
     * in a PrintListener). This is done after the Page has been built, normally at server startup.
     *
     * @return
     */
    public final ParameterModel getParameterModel() {
        return m_parameterModel;
    }

    /**
     * <p>
     * This method creates the DOM for the widget. The method is called by the Bebop framework and
     * should not be invoked by application developers.
     * </p>
     *
     * <p>
     * The method first fires the print event allowing application developers to set certain
     * properties of the Widget at request time in a PrintListener. The methods generateWidget and
     * generateErrors will then be invoked to generate either of the following
     * </p>
     *
     * <p>
     * <code>&lt;bebop:formErrors message=...&lt;/bebop:formErrors></code>
     * </p>
     *
     * <p>
     * <code>
     * &lt;bebop:formWidget name=... type=... label=... value=...
     * [hint=...]  [onXXX=...] &lt;/bebop:formWidget>
     * </code>
     * </p>
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {

        if (isVisible(state)) {
            Widget w = firePrintEvent(state);

            w.generateWidget(state, parent);
            w.generateErrors(state, parent);
        }
    }

    protected Widget firePrintEvent(PageState state) {
        Widget w = this;
        if (m_printListener != null) {
            try {
                w = (Widget) this.clone();
                w.setForm(m_form);

                m_printListener.prepare(new PrintEvent(this, state, w));
            } catch (CloneNotSupportedException e) {
                // FIXME: Failing silently here isn't so great
                //   It probably indicates a serious programming error
                w = this;
            }
        }
        return w;
    }

    /**
     * The XML tag.
     *
     * @return The tag to be used for the top level DOM element generated for this type of Widget.
     */
    protected String getElementTag() {
        return BEBOP_FORMWIDGET;
    }

    /**
     * Generates the DOM for the given widget on a per request basis.
     * <p>
     * Generates DOM fragment:
     * <p>
     * <code>&lt;bebop:formWidget name=... type=... value=... [onXXX=...]>
     * &lt;/bebop:formWidget></code>
     *
     * @param state
     * @param parent
     */
    protected void generateWidget(PageState state, Element parent) {

        Element widget = parent.newChildElement(getElementTag(), BEBOP_XML_NS);

        widget.addAttribute("type", getType());
        widget.addAttribute("name", getName());
        widget.addAttribute("class", getName().replace(".", " "));
        generateDescriptionXML(state, widget);
  //    if (m_label != null) {
        //        widget.addAttribute("label",
        //                            (String) m_label.localize(state.getRequest()));
        //    }
        exportAttributes(widget);
        String value = null;
        ParameterData p = getParameterData(state);
        if (p != null) {
            value = p.marshal();
        }
        if (value == null) {
            value = "";
        }
        widget.addAttribute("value", value);
    }

    /**
     * Generates the XML for the given widget.
     * <p>
     * Generates XML fragment:      <code>&lt;bebop:formErrors message=... id=name>
     * &lt;/bebop:formErrors></code>
     * </p>
     *
     * @param state
     * @param parent
     */
    protected void generateErrors(PageState state, Element parent) {
        Iterator i = getErrors(state);

        while (i.hasNext()) {
            Element errors = parent.newChildElement(BEBOP_FORMERRORS, BEBOP_XML_NS);
            errors.addAttribute("message",
                                (String) ((GlobalizedMessage) i.next()).localize(state.getRequest())
            );
            errors.addAttribute("id", getName());
        }
    }

    /**
     * Get the value associated with this widget in the request described by <code>ps</code>. The
     * type of the returned object depends on the <code>ParameterModel</code> underlying this
     * widget. This method is typically called in a FormProcessListener to access the value that was
     * submitted for a Widget in the Form.
     *
     * @param ps describes the request currently being processed
     *
     * @return
     *
     * @pre ps != null
     * @post may return null
     */
    public Object getValue(PageState ps) {
        Assert.exists(ps);
        Object data = null;
        ParameterData p = getParameterData(ps);
        if (p == null || p.getValue() == null) {
            // check if value is in session
            HttpSession session = ps.getRequest().getSession(false);
            if (session != null) {
                data = session.getAttribute(getName());
            }
        } else {
            data = p.getValue();
        }
        return (data == null) ? getDefaultValue() : data;

    }

    /**
     * Set the value of the parameter associated with this widget to a new value. The exact type of
     * <code>value</code> depends on the <code>ParameterModel</code> underlying the widget. This
     * method is typically called in a FormInitListener to initialize the value of a Widget in the
     * Form at request time.
     *
     * @param ps
     * @param value
     *
     * @pre ps != null
     * @post value == getValue(ps)
     *
     * @throws IllegalStateException the form to which the widget belongs has not been processed
     *                               yet.
     */
    public void setValue(PageState ps, Object value)
        throws IllegalStateException {
        Assert.exists(ps, "PageState");
        ParameterData p = getParameterData(ps);
        // set value in session if it is being held - allows 
        // updates in wizard forms where init is not called each 
        // step 
        HttpSession session = ps.getRequest().getSession(false);
        if (session != null && session.getAttribute(getName()) != null) {
            session.setAttribute(getName(), value);
        }
        if (p != null) {
            p.setValue(value);

        } else {
            throw new IllegalStateException("Cannot set value for widget '" + getName()
                                                + "': corresponding form '"
                                                + getForm().getName()
                                                + "' has not been processed yet.");
        }
    }

    protected Iterator getErrors(PageState ps) {
        Assert.exists(ps, "PageState");
        FormData f = getForm().getFormData(ps);
        if (f != null) {
            return f.getErrors(getName());
        }
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     *
     * @param ps
     *
     * @return the parameter value for this widget
     *
     * @post returns null if the FormData are missing
     */
    protected ParameterData getParameterData(PageState ps) {
        Assert.exists(ps, "PageState");
        FormData fd = getForm().getFormData(ps);
        if (fd != null) {
            return fd.getParameter(getName());
        }
        return null;
    }

    /**
     * Respond to an incoming request by calling <code>respond</code> on the form to which the
     * widget belongs. This method is called by the Bebop framework and should not be invoked by
     * application developers. It is somewhat questionable that this method should ever be called,
     * rather than having {@link Form#respond Form.respond()} called directly.
     *
     * @throws javax.servlet.ServletException
     * @pre state != null
     */
    @Override
    public void respond(PageState state) throws javax.servlet.ServletException {
        getForm().respond(state);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Widget cloned = (Widget) super.clone();
        cloned.setForm(null);
        return cloned;
    }

    /**
     * Specify a Widget. ValidationGuard implementation to use to determine if this widget should
     * run its validation listeners.
     *
     * @param guard the Widget.ValidationGuard.
     */
    public void setValidationGuard(ValidationGuard guard) {
        Assert.isUnlocked(this);
        m_guard = guard;
    }

    /**
     * Inner interface used to determine if the validation listeners should be run for this widget
     * or not.
     */
    public interface ValidationGuard {

        boolean shouldValidate(PageState ps);

    }

    /**
     * Adds an error to be displayed with this parameter.
     *
     * @param msg A GlobalizedMessage that will resolve to the error for the user.
     */
    public void addError(GlobalizedMessage msg) {
        PageState state = PageState.getPageState();
        getParameterData(state).addError(msg);
    }

    /**
     * Adds an error to be displayed with this parameter.
     *
     * @param error A string showing the error to the user.
     *
     * @deprecated refactor to use addError(GlobalizedMessage) instead.
     */
    public void addError(String error) {
        getParameterData(PageState.getPageState()).addError(error);
    }

    @Override
    public String toString() {
        return super.toString() + " [" + getName() + "]";
    }

}
