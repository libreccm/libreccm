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

import com.arsdigita.bebop.form.Hidden;

import javax.servlet.ServletException;

import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Represents the visual structure of an HTML form. Forms can be constructed with a Container
 * argument to specify the type of layout this form will adhere to. The default is a column panel.
 *
 * <p>
 * As an example, a form that accepts a first and last name may be set up as follows:
 *
 * <pre style="background: #cccccc">
 * public class MyForm extends Form implements FormProcessListener {
 *
 * private Text m_firstName; private Text m_lastName;
 *
 * public MyForm() { super("myform"); add(new Label("First Name:")); m_firstName = new
 * Text("firstName"); m_firstName.setDefaultValue("John"); add(m_firstName);
 *
 * add(new Label("Last Name:")); m_lastName = new Text("lastName");
 * m_lastName.setDefaultValue("Doe"); m_lastName.addValidationListener(new NotNullValidationListener
 * ("The last name")); add(m_lastName);
 *
 * add(new Submit("save", "Save")); addProcessListener(this); }
 *
 * public void process(FormSectionEvent e) { PageState s = e.getPageState();
 *
 * System.out.println("You are " + m_firstName.getValue(s) + " " + m_lastName.getValue(s)); } }
 * </pre>
 *
 * <p>
 * This form automatically checks that the user supplied a last name. Only then does it call the
 * <code>process</code> method, which prints the user-supplied values.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Stas Freidin
 * @author Rory Solomon
 * @author David Lutterkort
 *
 */
public class Form extends FormSection implements BebopConstants {

    /**
     * Internal logger instance to faciliate debugging. Enable logging output by editing
     * /WEB-INF/conf/log4j.properties int hte runtime environment and set
     * com.arsdigita.bebop.Form=DEBUG by uncommenting or adding the line.
     */
    private static final Logger LOGGER = LogManager.getLogger(Form.class);

    /**
     * Constant for specifying a <code>get</code> submission method for this form. See the <a href=
     * "http://www.w3.org/TR/html4/interact/forms.html#submit-format">W3C HTML specification</a> for
     * a description of what this attribute does.
     */
    public final static String GET = "get";

    /**
     * Constant for specifying a <code>post</code> submission method for this form. See the <a href=
     * "http://www.w3.org/TR/html4/interact/forms.html#submit-format">W3C HTML specification</a> for
     * a description of what this attribute does.
     */
    public final static String POST = "post";

    /**
     * The name of the <code>name</code> attribute for the form.
     */
    private final static String NAME = "name";

    /**
     * The name of the <code>method</code> attribute for the form.
     */
    private final static String METHOD = "method";

    private String m_action;
    private boolean m_processInvisible;

    /**
     * Hold the FormData for one request.
     */
    private RequestLocal m_formData;

    /**
     * Determines whether or not a form is 'redirecting', meaning that it will clear the control
     * event and redirect to the resulting state after form processing, so that a page reload won't
     * cause the form to be resubmitted.
     */
    private boolean m_isRedirecting = false;

    /**
     * Constructs a new form with the specified name. At the time of creation, instantiates a new
     * form model for the form and instantiates a default ColumnPanel to contain the components.
     *
     * @param name the name of the form
     */
    public Form(String name) {
        this(name, new GridPanel(2));
    }

    /**
     * Constructs a new form with the specified name and container. At the time of creation,
     * instantiates a new form model for the form and replaces the default ColumnPanel with the
     * specified container as the implicit container of the components.
     *
     * @param name  the name attribute of the form
     * @param panel the implicit container that will hold the components
     */
    public Form(String name, Container panel) {
        super(panel, new FormModel(name));
        initFormData();
        setName(name);
        setProcessInvisible(false);
        addMagicTag();
    }

    /**
     * Writes the output to a DOM to be used with the XSLT template to produce the appropriate
     * output. If the form is not visible, no output is generated.
     *
     * <p>
     * Generates a DOM fragment:
     * <p>
     * <code><pre>
     * &lt;bebop:form action=%url; %bebopAttr;>
     *   .. XML for panel ..
     *   .. XML for page state ..
     * &lt;/bebop:form>
     * </pre></code>
     *
     * @param s      the page state used to determine the values of form widgets and page state
     *               attributes
     * @param parent the XML element to which the form adds its XML representation
     *
     * @see PageState#generateXML
     */
    @Override
    public void generateXML(PageState s, Element parent) {
        if (isVisible(s)) {
            Element form = generateXMLSansState(s, parent);

            s.setControlEvent(this);
            s.generateXML(form, getModel().getParametersToExclude());
            s.clearControlEvent();
        }
    }

    /**
     * Generates the XML representing the form and its widgets, but not the state information from
     * <code>s</code>.
     *
     * @param s      represents the curent request
     * @param parent
     *
     * @return the top-level element for the form
     */
    protected Element generateXMLSansState(PageState s, Element parent) {
        Element form = parent.newChildElement("bebop:form", BEBOP_XML_NS);

        // Encode the URL with the servlet session information;
        // do not use DispatcherHelper.encodeURL because the
        // ACS global parameters are provided via the FormData.
        String url = null;

        if (m_action == null) {
            final URL requestURL = Web.getWebContext().getRequestURL();

            if (requestURL == null) {
                url = s.getRequest().getRequestURI();
            } else {
                url = requestURL.getRequestURI();
            }
        } else {
            url = m_action;
        }

        form.addAttribute("action", s.getResponse().encodeURL(url));

        exportAttributes(form);

        m_panel.generateXML(s, form);

        generateErrors(s, form);

        return form;
    }

    /**
     *
     * @param ps
     * @param parent
     */
    protected void generateErrors(PageState ps, Element parent) {

        for (Iterator it = getFormData(ps).getErrors(); it.hasNext();) {
            Element errors = parent.newChildElement(BEBOP_FORMERRORS,
                                                    BEBOP_XML_NS);
            Object msg = it.next();

            if (msg == null) {
                errors.addAttribute("message", "Unknown error");
            } else {
                errors.addAttribute("message",
                                    (String) ((GlobalizedMessage) msg).localize(ps.getRequest()));
            }
            errors.addAttribute("id", getName());
        }

    }

    /**
     * <p>
     * Determine whether or not this Form will redirect after its process listeners are fired.</p>
     *
     * @return
     */
    public boolean isRedirecting() {
        return m_isRedirecting;
    }

    /**
     * Setting the redirecting flag will cause the Form to clear the control event and redirect back
     * to the current URL, after firing all process listeners. Doing so means that a user reload
     * will not cause the form to be resubmitted. The default value for this flag is false.
     *
     * @param isRedirecting
     */
    public void setRedirecting(boolean isRedirecting) {
        Assert.isUnlocked(this);
        m_isRedirecting = isRedirecting;
    }

    /**
     * Responds to the request by processing this form with the HTTP request given in
     * <code>state</code>.
     *
     * @see #process process(...)
     *
     * @param state represents the current request
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public void respond(PageState state) throws ServletException {
        final FormData data = process(state);

        if (m_isRedirecting && data.isValid()) {
            state.clearControlEvent();

            throw new RedirectSignal(state.toURL(), true);
        }
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    // Methods to set the HTML attributes of the FORM element
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    /**
     * Sets the <code>name</code> attribute for the form.
     *
     * @param name the name for the form
     *
     * @pre ! isLocked()
     */
    public void setName(String name) {
        Assert.isUnlocked(this);
        setAttribute(NAME, name);
    }

    /**
     * Gets the <code>name</code> attribute for this form.
     *
     * @return the name for this form.
     */
    public String getName() {
        return (String) getAttribute(NAME);
    }

    /**
     * Sets the <code>enctype</code> attribute used in the <code>form</code> element. No encoding
     * type is specified by default.
     *
     * @param encType the encoding type
     *
     * @pre ! isLocked()
     */
    public void setEncType(String encType) {
        Assert.isUnlocked(this);
        setAttribute("enctype", encType);
    }

    /**
     * Sets the <code>onSubmit</code> attribute used in the <code>form</code> element. No onsubmit
     * handler is specified by default.
     *
     * @param javascriptCode the javascript code associated with this attribute
     *
     * @pre ! isLocked()
     */
    public void setOnSubmit(String javascriptCode) {
        Assert.isUnlocked(this);
        setAttribute("onSubmit", javascriptCode);
    }

    /**
     * Sets the <code>ONRESET</code> attribute used in the <code>FORM</code> element. No onreset
     * handler is specified by default.
     *
     * @param javascriptCode the javascript code associated with this attribute
     *
     * @pre ! isLocked()
     */
    public void setOnReset(String javascriptCode) {
        Assert.isUnlocked(this);
        setAttribute("onReset", javascriptCode);
    }

    /**
     * Sets the HTTP method used to submit the form.
     *
     * @param method either <code>GET</code> or <code>POST</code>
     *
     * @pre ! isLocked()
     */
    public void setMethod(String method) {
        Assert.isUnlocked(this);
        setAttribute(METHOD, method);
    }

    private String getMethod() {
        return getAttribute(METHOD);
    }

    /**
     * Returns true if form processing is turned on when the form is invisible.
     *
     * @return true if the form listeners should be processed even when the form is not visible on
     *         the page, false otherwise
     */
    protected boolean getProcessInvisible() {
        return m_processInvisible;
    }

    /**
     * Turns form processing on/off when the form is invisible.
     *
     * @param processInvisible true if the form listeners should be processed even when the form is
     *                         not visible on the page
     */
    protected void setProcessInvisible(boolean processInvisible) {
        m_processInvisible = processInvisible;
    }

    /**
     * Sets the URL for the form's <code>action</code> attribute. This is the URL to which
     * submissions will be sent when the user clicks a submit button on the form. By default, the
     * action is <code>null</code>, instructing the form to set the action to the URL of the page in
     * which it is used. If the action is set to a different URL, none of the listeners registered
     * with this form will be run.
     *
     * @param action the URL to submit this form to
     *
     * @pre ! isLocked()
     */
    public void setAction(String action) {
        Assert.isUnlocked(this);
        m_action = action;
    }

    /**
     * Returns the URL for the form's <code>action</code> attribute.
     *
     * @return the URL to which to submit this form.
     *
     * @see #setAction setAction
     */
    public final String getAction() {
        return m_action;
    }

    /**
     * Processes this form, creating a <code>FormData</code> object. Runs the right set of init,
     * validation, and process listeners, depending on whether this is an initial request to the
     * form and whether the form submission was valid. Submission listeners are always run.
     *
     * @see #getFormData
     *
     * @param state represents the current request
     *
     * @return the values extracted from the HTTP request contained in <code>state</code>.
     *
     * @throws com.arsdigita.bebop.FormProcessException
     * @pre state != null
     * @post return != null
     */
    @Override
    public FormData process(PageState state) throws FormProcessException {
        Assert.exists(state, "PageState");
        FormData result = new FormData(getModel(), state.getRequest());
        setFormData(state, result);

        // Unless invisible form processing is turned on, don't run any
        // listeners if this form is not visible.
        if (getProcessInvisible() || state.isVisibleOnPage(this)) {
            getModel().process(state, result);
        }
        return result;
    }

    /**
     * Returns the form data constructed by the {@link #process
     * process} method for the request described by <code>state</code>. Processes the form if it has
     * not already been processed.
     *
     * @param state describes the current request
     *
     * @return the values extracted from the HTTP request contained in <code>state</code>, or
     *         <code>null</code> if the form has not been processed yet.
     *
     * @pre state != null
     * @post return != null
     */
    public FormData getFormData(PageState state) {
        return (FormData) m_formData.get(state);
    }

    /**
     * Adds a Hidden Tag to this form so that our controller can determine if this is an initial
     * request.
     */
    protected void addMagicTag() {
        Hidden h = new Hidden(getModel().getMagicTagName());
        h.setDefaultValue("visited");
        add(h);
    }

    /**
     * Traverses the components contained in this form, collecting parameterModels and Listeners
     * into this form's FormModel.
     */
    protected void traverse() {
        Traversal formRegistrar = new Traversal() {

            @Override
            protected void act(Component c) {
                if (c == Form.this) {
                    return;
                }
                if (c instanceof Form) {
                    throw new IllegalStateException("Forms cannot contain other Forms");
                }
                c.register(Form.this, getModel());
            }

        };
        formRegistrar.preorder(this);
    }

    /**
     * Adds this form to the page and traverses the components contained in this form, collecting
     * parameterModels and Listeners into this form's FormModel.
     *
     * @param p page in which to register this form
     */
    @Override
    public void register(Page p) {
        traverse();
        p.addComponent(this);
    }

    /**
     * TODO
     *
     * @param model
     */
    public void excludeParameterFromExport(ParameterModel model) {
        getModel().excludeFormParameterFromExport(model);
    }

    /**
     * Initialize <code>m_formData</code> so that accessing the per-request form data forces the
     * form to be processed on the first access and caches the form data for subsequent requests.
     */
    private void initFormData() {
        m_formData = new RequestLocal() {

            @Override
            protected Object initialValue(PageState s) {
                    // TODO: We need to come up with the right strategy for
                // how we deal with FormProcessExceptions. Are they fatal
                // ? Do we just add them to the form validation errors ?
                try {
                    return process(s);
                } catch (FormProcessException e) {
                    LOGGER.error("Form Process exception", e);
                    throw new UncheckedWrapperException("Form Process error: "
                                                            + e.getMessage(), e);
                }
            }

        };
    }

    /**
     * Converts to a String.
     *
     * @return a human-readable representation of <code>this</code>.
     */
    @Override
    public String toString() {
        return super.toString() + " " + "[" + getName() + "," + getAction() + "," + getMethod()
               + "," + isRedirecting() + "]";
    }

    /**
     * Protected access to set the formdata request local. This method is required if a subclass
     * wishes to override the process method.
     *
     * @param state
     * @param data
     */
    protected void setFormData(PageState state, FormData data) {
        m_formData.set(state, data);
    }

}
