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

import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.parameters.BitSetParameter;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.util.SystemInformation;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The top-level container for all Bebop components and containers.
 *
 * <UL>
 * <LI>Holds references to the components of a page.</LI>
 * <LI>Provides methods for servicing requests and for notifying other
 * components that a request for this page has been received through
 * {@link ActionListener ActionListeners}.</LI>
 * <LI>Tracks request parameters for stateful components, such as tabbed panes
 * and sortable tables.</LI>
 * </UL>
 *
 * A typical <code>Page</code> may be created as follows: null <blockquote><pre><code>
 * Page p = new Page("Hello World");
 * p.add(new Label("Hello World");
 * p.lock();
 * </code></pre></blockquote> </p>
 *
 * @author David Lutterkort
 * @author Stanislav Freidin
 * @author Uday Mathur
 */
public class Page extends SimpleComponent implements Container {

    /**
     * Class specific logger instance.
     */
    private static final Logger LOGGER = LogManager.getLogger(Page.class);
    /**
     * The delimiter character for components naming
     */
    private static final String DELIMITER = ".";
    /**
     * The prefix that gets prepended to all state variables. Components must
     * not use variables starting with this prefix. This guarantees that the
     * page state and variables individual components wish to pass do not
     * interfere with each other.
     */
    private static final String COMPONENT_PREFIX = "bbp" + DELIMITER;
    private static final String INTERNAL = COMPONENT_PREFIX;
    /**
     * The name of the special parameter that indicates which component has been
     * selected.
     */
    static final String SELECTED = INTERNAL + "s";
    static final String CONTROL_EVENT = INTERNAL + "e";
    static final String CONTROL_VALUE = INTERNAL + "v";
    static final Collection CONTROL_EVENT_KEYS;

    static {
        LOGGER.debug("Static initalizer is starting...");
        CONTROL_EVENT_KEYS = new ArrayList(3);
        CONTROL_EVENT_KEYS.add(SELECTED);
        CONTROL_EVENT_KEYS.add(CONTROL_EVENT);
        CONTROL_EVENT_KEYS.add(CONTROL_VALUE);
        LOGGER.debug("Static initalizer finished.");
    }

    /**
     * The name of the request parameter used for the visibility state of
     * components stored in m_invisible.
     */
    static final String INVISIBLE = INTERNAL + "i";
    /**
     * Map of stateful components (id --> Component) SortedMap used because
     * component based hash for page is based on concatenation of component ids,
     * and so need to guarantee that they are returned in the same order for the
     * same page - cg.
     */
    private SortedMap m_componentMap;
    private List m_components;
    /**
     * Map of component -> owned parameter collection
     */
    private Map m_componentParameterMap = new HashMap();
    private FormModel m_stateModel;
    /**
     * <code>Container</code> that renders this <code>Page</code>.
     */
    protected Container m_panel;
    private List m_actionListeners;
    private List m_requestListeners;
    /**
     * The title of the page to be added in the head of HTML output. The title
     * is wrapped in a Label to allow developers to add PrintListeners to
     * dynamically change the value of the title.
     */
    private Label m_title;
    /**
     * Stores the actual title for the current request. The title may be
     * generated with a PrintListener of the m_title Label.
     */
    private RequestLocal m_currentTitle;
    /**
     * A list of all the client-side stylesheets. The elements of the list are
     * of type Page.Stylesheet, defined at the end of this file.
     */
    private List m_clientStylesheets;
    private StringParameter m_selected;
    private StringParameter m_controlEvent;
    private StringParameter m_controlValue;
    /**
     * The default (initial) visibility of components. The encoding is identical
     * to that for PageState.m_invisible.
     *
     * This variable is package-friendly since it needs to be accessed by
     * PageState.
     */
    protected BitSet m_invisible;
    /**
     * The PageErrorDisplay component that will display page state validation
     * errors on this page
     */
    private Component m_errorDisplay;
    /**
     * Indicates whether finish() has been called on this Page.
     */
    private boolean m_finished = false;
    /**
     * indicates whether pageState.stateAsURL() should export the entire state
     * for this page, or whether it should only export the control event as a
     * URL and use the HttpSession for the rest of the page state.
     */
    private boolean m_useHttpSession = false;

    /**
     * Returns <code>true</code> if this page should export state through the
     * HttpSession instead of the URL query string.
     * <P>
     * If this returns <code>true</code>, then PageState.stateAsURL() will only
     * export the control event as a URL query string. If this returns
     * <code>false</code>, then stateAsURL() will export the entire page state.
     *
     * @see PageState#stateAsURL
     *
     * @return <code>true</code> if this page should export state through the
     *         HttpSession; <code>false</code> if it should export using the URL
     *         query string.
     */
    public boolean isUsingHttpSession() {
        return m_useHttpSession;
    }

    /**
     * Indicates to this page whether it should export its entire state to
     * subsequent requests through the URL query string, or if it should use the
     * HttpSession instead and only use the URL query string for the control
     * event.
     *
     * @see PageState#stateAsURL
     *
     * @param b <code>true</code> if PageState.stateAsURL() will export only the
     *          control event as a URL query string. <code>false</code> if
     *          stateAsURL() will export the entire page state.
     */
    public void setUsingHttpSession(boolean b) {
        m_useHttpSession = b;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Constructor Section
    // ////////////////////////////////////////////////////////////////////////
    /**
     * Constructor, creates an empty page with the specified title and panel.
     *
     * @param title title for this page
     * @param panel container for this page
     *
     * @deprecated use Page(Lab el, Container) instead.
     */
    public Page(String title, Container panel) {
        this(new Label(title), panel);
    }

    /**
     * Constructor, creates an empty page with the specified title and panel.
     *
     * @param title title for this page as (globalized) Label
     * @param panel container for this page
     */
    public Page(Label title, Container panel) {
        super();
        m_actionListeners = new LinkedList();
        m_requestListeners = new LinkedList();
        m_panel = panel;
        m_clientStylesheets = new ArrayList();
        m_components = new ArrayList();
        m_componentMap = new TreeMap();
        setErrorDisplay(new PageErrorDisplay());
        m_title = title;

        // Initialize the RequestLocal where the title for the current
        // request will be kept
        m_currentTitle = new RequestLocal() {

            @Override
            protected Object initialValue(PageState state) {
                return m_title.firePrintEvent(state);
            }

        };

        // Initialize the set of state parameters to hold
        // the ones necessary for keeping track of the selected component and
        // the name and value of a 'control event'
        m_selected = new StringParameter(SELECTED);
        m_controlEvent = new StringParameter(CONTROL_EVENT);
        m_controlValue = new StringParameter(CONTROL_VALUE);

        m_stateModel = new FormModel("stateModel", true);
        m_stateModel.addFormParam(m_selected);
        m_stateModel.addFormParam(m_controlEvent);
        m_stateModel.addFormParam(m_controlValue);

        // Set up the visibility tracking parameters
        m_invisible = new BitSet(32);
        BitSetParameter p = new BitSetParameter(INVISIBLE,
                                                BitSetParameter.ENCODE_DGAP);
        m_stateModel.addFormParam(p);
    }

    /**
     * Creates an empty page with default title and implicit BoxPanel container.
     */
    public Page() {
        this("");
    }

    /**
     * Creates an empty page with the specified title and implicit BoxPanel
     * container.
     *
     * @param title title for this page
     */
    public Page(Label title) {
        this(title, new BoxPanel());
        BoxPanel bp = (BoxPanel) m_panel;
        bp.setWidth("100%");
    }

    /**
     * Creates an empty page with the specified title and implicit BoxPanel
     * container.
     *
     * @param title title for this page
     */
    public Page(String title) {
        this(new Label(title));
    }

    /**
     * Adds a component to this container.
     *
     * @param c component to add to this container
     */
    @Override
    public void add(Component c) {
        m_panel.add(c);

    }

    /**
     * Adds a component with the specified layout constraints to this container.
     * Layout constraints are defined in each layout container as static ints.
     * To specify multiple constraints, use bitwise OR.
     *
     * @param c           component to add to this container
     * @param constraints layout constraints (a bitwise OR of static ints in the
     *                    particular layout)
     */
    @Override
    public void add(Component c, int constraints) {
        m_panel.add(c, constraints);
    }

    /**
     * Returns <code>true</code> if this list contains the specified element.
     * More formally, returns <code>true</code> if and only if this list
     * contains at least one element e such that (o==null ? e==null :
     * o.equals(e)).
     * <P>
     * This method returns <code>true</code> only if the component has been
     * directly added to this container. If this container contains another
     * container that contains this component, this method returns
     * <code>false</code>.
     *
     * @param o element whose presence in this container is to be tested
     *
     * @return <code>true</code> if this Container contains the specified
     *         component directly; <code>false</code> otherwise.
     */
    @Override
    public boolean contains(Object o) {
        return m_panel.contains(o);
    }

    /**
     * Returns the component at the specified position. Each call to the add
     * method increments the index. Since the user has no control over the index
     * of added components (other than counting each call to add), this method
     * should be used in conjunction with indexOf.
     *
     * @param index the index of the item to be retrieved from this Container
     *
     * @return the component at the specified position in this container.
     */
    @Override
    public Component get(int index) {
        return m_panel.get(index);
    }

    /**
     * Gets the index of a component.
     *
     * @param c component to search for
     *
     * @return the index in this list of the first occurrence of the specified
     *         element, or -1 if this list does not contain this element.
     *
     * @pre c != null
     * @post contains(c) implies (return >= 0) && (return < size()) @pos t
     * !contains(c) implies return == -1
     */
    @Override
    public int indexOf(Component c) {
        return m_panel.indexOf(c);
    }

    /**
     * Returns <code>true</code> if the container contains no components.
     *
     * @return <code>true</code> if this container contains no components;
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean isEmpty() {
        return m_panel.isEmpty();
    }

    /**
     * Returns the number of elements in this container. This does not
     * recursively count the components that are indirectly contained in this
     * container.
     *
     * @return the number of components directly in this container.
     */
    @Override
    public int size() {
        return m_panel.size();
    }

    @Override
    public Iterator children() {
        return Collections.singletonList(m_panel).iterator();
    }

    /**
     * Returns the panel that the <code>Page</code> uses for rendering its
     * components.
     *
     * @return the panel.
     */
    public final Container getPanel() {
        return m_panel;
    }

    /**
     * Set the Container used for rendering components on this page. Caution
     * should be used with this function, as the existing container is simply
     * overwritten.
     *
     * @param c
     *
     * @author Matthew Booth (mbooth@redhat.com)
     */
    public void setPanel(Container c) {
        m_panel = c;
    }

    /**
     * Retrieves the title of this page.
     *
     * @return the static title of this page.
     */
    public final Label getTitle() {
        return m_title;
    }

    /**
     * Retrieves the title of this page as a Bebop label component.
     *
     * @param state the state of the current request
     *
     * @return the title of the page for the current request.
     */
    public final Label getTitle(PageState state) {
        return (Label) m_currentTitle.get(state);
    }

    /**
     * Sets the title for this page from the passed in string.
     *
     * @param title title for this page
     */
    public void setTitle(String title) {
        Assert.isUnlocked(this);
        setTitle(new Label(title));
    }

    /**
     * Set the title for this page from the passed in label.
     *
     * @param title title for this page
     */
    public void setTitle(Label title) {
        Assert.isUnlocked(this);
        m_title = title;
    }

    /**
     * Sets the {@link Component} that will display the validation errors in the
     * current {@link PageState}. Any validation error in the
     * <code>PageState</code> will cause the <code>Page</code> to completely
     * ignore all other components and render only the error display component.
     * <p>
     * By default, a {@link PageErrorDisplay} component is used to display the
     * validation errors.
     *
     * @param c the component that will display the validation errors in the
     *          current <code>PageState</code>
     */
    public final void setErrorDisplay(Component c) {
        Assert.isUnlocked(this);
        m_errorDisplay = c;
    }

    /**
     * Gets the {@link Component} that will display the validation errors in the
     * current {@link PageState}. Any validation error in the
     * <code>PageState</code> will cause the <code>Page</code> to completely
     * ignore all other components and render only the error display component.
     * <p>
     * By default, a {@link PageErrorDisplay} component is used to display the
     * validation errors.
     *
     * @return the component that will display the validation errors in the
     *         current <code>PageState</code>.
     */
    public final Component getErrorDisplay() {
        return m_errorDisplay;
    }

    /**
     * Adds a client-side stylesheet that should be used in HTML output.
     * Arbitrarily many client-side stylesheets can be added with this method.
     * To use a CSS stylesheet, call something like
     * <code>setStyleSheet("style.css", "text/css")</code>.
     *
     * <p>
     * These values will ultimately wind up in a <tt>&lt;link&gt;</tt>
     * tag in the head of the HTML page.
     *
     * <p>
     * Note that the stylesheet set with this call has nothing to do with the
     * XSLT stylesheet (transformer) that is applied to the XML generated from
     * this page!
     *
     * @param styleSheetURI the location of the stylesheet
     * @param mimeType      the MIME type of the stylesheet, usually
     * <tt>text/css</tt>
     *
     * @pre ! isLocked()
     */
    public void addClientStylesheet(String styleSheetURI, String mimeType) {
        m_clientStylesheets.add(new Stylesheet(styleSheetURI, mimeType));
    }

    /**
     * Adds a global state parameter to this page. Global parameters are values
     * that need to be preserved between requests, but that have no special
     * connection to any of the components on the page. For a page that displays
     * details about an item, a global parameter would be used to identify the
     * item.
     *
     * If the parameter was previously added as a component state parameter, its
     * name is unmangled and stays unmangled.
     *
     * @see #addComponentStateParam
     *
     * @param p the global parameter to add
     *
     * @pre ! isLocked()
     * @pre parameter != null
     */
    public void addGlobalStateParam(ParameterModel p) {
        Assert.isUnlocked(this);
        p.setName(unmangle(p.getName()));
        m_stateModel.addFormParam(p);
    }

    /**
     * Constructs the top nodes of the DOM or JDOM tree. Used by
     * generateXML(PageState, Document) below.
     * <p>
     * Generates DOM fragment:
     * <pre>
     * &lt;bebop:page>
     *   &lt;bebop:title> ... value set with <i>setTitle</i> ... &lt;/bebop:title>
     *   &lt;bebop:stylesheet href='styleSheetURI' type='mimeType'>
     *   ... page content gnerated by children ...
     * &lt;/bebop:page></pre> The content of the <tt>&lt;title&gt;</tt>
     * element can be set by calling {@link #setTitle setTitle}. The
     * <tt>&lt;stylesheet&gt;</tt> element will only be present if a stylesheet
     * has been set with {@link
     * #setStyleSheet setStyleSheet}.
     *
     * @param ps     the page state for the current page
     * @param parent the DOM node for the whole Document
     *
     * @return
     *
     * @pre isLocked()
     */
    protected Element generateXMLHelper(PageState ps, Document parent) {
        Assert.isLocked(this);

        Element page = parent.createRootElement("bebop:page", BEBOP_XML_NS);
        exportAttributes(page);

        /* Generator information */
        exportSystemInformation(page);

        Element title = page.newChildElement("bebop:title", BEBOP_XML_NS);
        title.setText(getTitle(ps).getLabel(ps));

        for (Iterator i = m_clientStylesheets.iterator(); i.hasNext();) {
            ((Stylesheet) i.next()).generateXML(page);
        }

        return page;
    }

    /**
     * Constructs a DOM or JDOM tree with all components on the page. The tree
     * represents the page that results from the
     * {@link javax.servlet.http.HttpServletRequest} kept in the
     * <code>state</code>.
     *
     * @param state  the page state produced by {@link #process}
     * @param parent the DOM node for the whole Document
     *
     * @see #process process
     * @pre isLocked()
     * @pre state != null
     */
    public void generateXML(PageState state, Document parent) {
        // always export page state as HTTP session
        if (m_useHttpSession) {
            state.stateAsHttpSession();
        }

        Element page = generateXMLHelper(state, parent);

        // If the page state has errors, ignore all the components and
        // render only the error display component
        if (state.getErrors().hasNext()) {
            m_errorDisplay.generateXML(state, page);
        } else {
            m_panel.generateXML(state, page);
        }

        if (KernelConfig.getConfig().isDebugEnabled()
                && debugStructure(state.getRequest())) {

            Element structure = page.newChildElement("bebop:structure",
                                                     BEBOP_XML_NS);

            showStructure(state, structure);
        }
    }

    private static boolean debugStructure(HttpServletRequest req) {
        return "transform".equals(req.getParameter("debug"));
    }

    /**
     * Do nothing. Top-level add nodes is meaningless.
     *
     * @param elt
     */
    @Override
    public void generateXML(PageState state, Element elt) {
    }

    /**
     * Creates a PageState object and processes it by calling the respond method
     * on the selected component. Processes a request by notifying the component
     * from which the process originated and {@link #fireActionEvent
     * broadcasts} an {@link ActionEvent} to all the listeners that registered
     * with {@link #addActionListener addActionListener}.
     *
     * @see #generateXML(PageState,Document) generateXML
     *
     * @param request
     * @param response
     *
     * @return
     *
     * @throws javax.servlet.ServletException
     * @pre isLocked()
     * @pre request != null
     * @pre response != null
     */
    public PageState process(HttpServletRequest request,
                             HttpServletResponse response)
        throws ServletException {

        PageState result = new PageState(this, request, response);
        try {
            process(result);
        } finally {
        }
        return result;
    }

    /**
     * Processes the supplied PageState object according to this PageModel.
     * Calls the respond method on the selected Bebop component.
     */
    public void process(PageState state) throws ServletException {
        Assert.isLocked(this);
        try {
            fireRequestEvent(state);
        } finally {
        }

        // Validate the state; any errors in the state will be displayed
        // by generateXML
        state.forceValidate();

        if (state.isValid()) {
            try {
                state.respond();
            } finally {
            }
            try {
                fireActionEvent(state);
            } finally {

            }
        }
    }

    /**
     * Builds a DOM Document from the current request state by doing a
     * depth-first tree walk on the current set of components in this Page,
     * calling generateXML on each. Does NOT do the rendering. If the HTTP
     * response has already been committed, does not build the XML document.
     *
     * @param req
     * @param res
     * @return a DOM ready for rendering, or null if the response has already
     *         been committed.
     * @throws javax.servlet.ServletException
     *
     */
    public Document buildDocument(final HttpServletRequest req,
                                  final HttpServletResponse res)
        throws ServletException {
        try {
            Document doc = new Document();
            
            final ServletRequest request = unwrapRequest(req);
            if (!(request instanceof HttpServletRequest)) {
                throw new ServletException("Request is not a HttpServletRequest.");
            }
            final PageState state = process((HttpServletRequest) request, res);

            // only generate XML document if the response is not already
            // committed
            if (!res.isCommitted()) {
                try {
                    generateXML(state, doc);
                } finally {
                }
                return doc;
            } else {
                return null;
            }
        } catch (ParserConfigurationException e) {
            throw new ServletException(e);
        }
    }

    private ServletRequest unwrapRequest(final HttpServletRequest request) {

        ServletRequest current = request;
        while (current instanceof ServletRequestWrapper) {
            current = ((ServletRequestWrapper) current).getRequest();
        }

        return current;
    }
    
    /**
     * Finishes building the page. The tree of components is traversed and each
     * component is told to add its state parameters to the page's state model.
     *
     * @pre ! isLocked()
     */
    private void finish() {
        if (!m_finished) {
            Assert.isUnlocked(this);

            Traversal componentRegistrar = new Traversal() {

                @Override
                protected void act(Component c) {
                    addComponent(c);
                    c.register(Page.this);
                }

            };
            if (m_panel == null) {
                LOGGER.warn("m_panel is null");
            }
            componentRegistrar.preorder(m_panel);
            if (m_errorDisplay != null) {
                addComponent(m_errorDisplay);
                m_errorDisplay.register(Page.this);
            }

            m_finished = true;
        }
    }

    /**
     * Locks the page and all its components against further modifications.
     *
     * <p>
     * Locking a page helps in finding mistakes that result from modifying a
     * page's structure.</P>
     */
    @Override
    public void lock() {
        if (!m_finished) {
            finish();
        }
        m_stateModel.lock();
        Traversal componentLocker = new Traversal() {

            @Override
            protected void act(Component c) {
                c.lock();
            }

        };

        componentLocker.preorder(m_panel);

        super.lock();
    }

    @Override
    public void respond(PageState state) throws javax.servlet.ServletException {
        throw new UnsupportedOperationException();
    }

    /**
     * Registers a listener that is notified whenever a request to this page is
     * made, after the selected component has had a chance to respond.
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void addActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_actionListeners.add(l);
    }

    /**
     * Remove a previously registered action listener.
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void removeActionListener(ActionListener l) {
        Assert.isUnlocked(this);
        m_actionListeners.remove(l);
    }

    /**
     * Registers a listener that is notified whenever a request to this page is
     * made, before the selected component has had a chance to respond.
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void addRequestListener(RequestListener l) {
        Assert.isUnlocked(this);
        m_requestListeners.add(l);
    }

    /**
     * Removes a previously registered request listener.
     *
     * @param 1 the listener to remove
     *
     * @pre l != null
     * @pre ! isLocked()
     */
    public void removeRequestListener(RequestListener l) {
        Assert.isUnlocked(this);
        m_requestListeners.remove(l);
    }

    /**
     * Broadcasts an {@link ActionEvent} to all registered listeners. The source
     * of the event is this page, and the state recorded in the event is the one
     * resulting from processing the current request.
     *
     * @param the state for this event
     *
     * @pre state != null
     */
    protected void fireActionEvent(PageState state) {
        ActionEvent e = null;

        for (Iterator i = m_actionListeners.iterator(); i.hasNext();) {
            if (e == null) {
                e = new ActionEvent(this, state);
            }

            final ActionListener listener = (ActionListener) i.next();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Firing action listener " + listener);
            }

            listener.actionPerformed(e);
        }
    }

    /**
     * Broadcasts a {@link RequestEvent} to all registered listeners. The source
     * of the event is this page, and the state recorded in the event is the one
     * resulting from processing the current request.
     *
     * @param state the state for this event
     *
     * @pre state != null
     */
    protected void fireRequestEvent(PageState state) {
        RequestEvent e = null;

        for (Iterator i = m_requestListeners.iterator(); i.hasNext();) {
            if (e == null) {
                e = new RequestEvent(this, state);
            }

            final RequestListener listener = (RequestListener) i.next();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Firing request listener " + listener);
            }

            listener.pageRequested(e);
        }
    }

    /**
     * Export page generator information if set. The m_pageGenerator is a
     * HashMap containing the information as key value. In general this should
     * include generator name and generator version.
     *
     * @param page parent element - should be bebeop:page
     *
     * @pre m_pageGenerator != null && !m_pageGenerator.isEmpty()
     */
    final protected void exportSystemInformation(Element page) {
        SystemInformation sysInfo = SystemInformation.getInstance();
        if (!sysInfo.isEmpty()) {
            Element gen = page.newChildElement("bebop:systemInformation",
                                               BEBOP_XML_NS);

            Iterator<Map.Entry<String, String>> keyValues = sysInfo.iterator();
            while (keyValues.hasNext()) {
                Map.Entry<String, String> entry = keyValues.next();
                gen.addAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    // Client-side stylesheet storage
    private class Stylesheet {

        String m_URI;
        String m_type;

        public Stylesheet(String stylesheetURI, String mimeType) {
            m_URI = stylesheetURI;
            m_type = mimeType;
        }

        public void generateXML(Element parent) {
            Element style = parent.newChildElement("bebop:stylesheet",
                                                   BEBOP_XML_NS);
            style.addAttribute("href", m_URI);
            if (m_type != null) {
                style.addAttribute("type", m_type);
            }
        }

    }

    /**
     * Adds a component to the page model.
     */
    public void addComponent(Component c) {
        Assert.isUnlocked(this);

        if (!stateContains(c)) {
            if (c == null) {
                LOGGER.error("c is null");
            } /*else {
             s_log.error("c: " + c.toString());
             }*/

            String key = c.getKey();
            if (key == null) {
                key = Integer.toString(m_components.size());
            }
            if (m_componentMap.get(key) != null) {
                throw new IllegalArgumentException(
                    "Component key must not be duplicated.  The key " + key
                        + " is shared by more than one component.");
            }
            m_componentMap.put(key, c);
            m_components.add(c);
        }
    }

    /**
     * Registers a state parameter for a component. It is permissible to
     * register the same state parameter several times, from the same or
     * different components. The name of the parameter will be changed to ensure
     * that it won't clash with any other component's parameter. If the
     * parameter is added more than once, the name is only changed the first
     * time it is added.
     *
     * @param c the component to register the parameter for
     * @param p the state parameter to register
     *
     * @see #addGlobalStateParam
     *
     * @pre stateContains(c)
     * @pre ! isLocked()
     * @pre p != null
     */
    public void addComponentStateParam(Component c, ParameterModel p) {
        Assert.isUnlocked(this);

        if (!stateContains(c)) {
            throw new IllegalArgumentException(
                "Component must be registered in Page");
        }
        if (!m_stateModel.containsFormParam(p)) {
            String name = parameterName(c, p.getName());
            LOGGER.debug(String
                .format("Setting name of parameter to add to '%s'",
                        name));
            p.setName(name);
            m_stateModel.addFormParam(p);

            Collection params = (Collection) m_componentParameterMap.get(c);
            if (params == null) {
                params = new ArrayList();
                m_componentParameterMap.put(c, params);
            }
            params.add(p);
        }
    }

    /**
     * <p>
     * Get the parameters registered for a given component.</p>
     */
    public Collection getComponentParameters(Component c) {
        return (Collection) m_componentParameterMap.get(c);
    }

    /**
     * Gets the state index of a component. This is the number assigned to the
     * component in the register traveral
     *
     * @param c the component to search for
     *
     * @return the index in this list of the first occurrence of the specified
     *         element, or -1 if this list does not contain this element.
     *
     * @pre c != null
     * @post contains(c) implies (return >= 0) && (return < size()) @pos t
     * !contains(c) implies return == -1
     */
    public int stateIndex(Component c) {
        return m_components.indexOf(c);
    }

    /**
     * The number of components in the page model.
     *
     * @post return >= 0
     */
    public int stateSize() {
        return m_components.size();
    }

    /**
     * Checks whether this component is already in the page model.
     *
     * @pre c != null
     */
    public boolean stateContains(Component c) {
        return m_components.contains(c);
    }

    /**
     * Gets a page component by index.
     *
     * @pre (i >= 0) && (i < size()) @pos t return != null
     */
    public Component getComponent(int i) {
        return (Component) m_components.get(i);
    }

    /**
     * Gets a page component by key.
     *
     * @pre s != null
     */
    Component getComponent(String s) {
        return (Component) m_componentMap.get(s);
    }

    /**
     * Gets the form model that contains the parameters for the page's state.
     */
    public final FormModel getStateModel() {
        return m_stateModel;
    }

    /**
     * Gets the ParameterModels held in this Page.
     *
     * @return an iterator of ParameterModels.
     */
    public Iterator getParameters() {
        return m_stateModel.getParameters();
    }

    /**
     * Checks whether the specified component is visible by default on the page.
     *
     * @param c a component contained in the page
     *
     * @return <code>true</code> if the component is visible by default;
     *         <code>false</code> otherwise.
     *
     * @see #setVisibleDefault setVisibleDefault
     * @see Component#setVisible Component.setVisible
     */
    public boolean isVisibleDefault(Component c) {
        Assert.isTrue(stateContains(c));

        return !m_invisible.get(stateIndex(c));
    }

    /**
     * Sets whether the specified component is visible by default. The default
     * visibility is used when a page is displayed for the first time and on
     * subsequent requests until the visibility of a component is changed
     * explicitly with {@link Component#setVisible
     * Component.setVisible}.
     *
     * <p>
     * When a component is first added to a page, it is visible.
     *
     * @param c a component whose visibility is to be set
     * @param v <code>true</code> if the component is visible;
     *          <code>false</code> otherwise.
     *
     * @see Component#setVisible Component.setVisible
     * @see Component#register Component.register
     */
    public void setVisibleDefault(Component c, boolean v) {
        Assert.isUnlocked(this);

        addComponent(c);
        int i = stateIndex(c);
        if (v) {
            m_invisible.clear(i);
        } else {
            m_invisible.set(i);
        }
    }

    /**
     * The global name of the parameter <code>name</code> in the component
     * <code>c</code>.
     */
    public String parameterName(Component c, String name) {
        if (c == null || !stateContains(c)) {
            return name;
        }

        return componentPrefix(c) + name;
    }

    /**
     * The global name of the parameter <code>name</code>.
     */
    public String parameterName(String name) {
        return parameterName(null, name);
    }

    void reset(final PageState ps, Component cmpnt) {
        Traversal resetter = new Traversal() {

            @Override
            protected void act(Component c) {
                Collection cp = getComponentParameters(c);
                if (cp != null) {
                    Iterator iter = cp.iterator();
                    while (iter.hasNext()) {
                        ParameterModel p = (ParameterModel) iter.next();
                        ps.setValue(p, null);
                    }
                }
                c.setVisible(ps, isVisibleDefault(c));
            }

        };
        resetter.preorder(cmpnt);
    }

    /**
     * Return the prefix that is prepended to each component's state parameters
     * to keep them unique.
     */
    private final String componentPrefix(Component c) {
        if (c == null) {
            return COMPONENT_PREFIX + "g" + DELIMITER;
        } else {
            // WRS: preferentially use key if it exists
            String key = c.getKey();
            if (key == null) {
                if (stateContains(c)) {
                    key = String.valueOf(stateIndex(c));
                } else {
                    throw new IllegalArgumentException(
                        "Cannot generate prefix for component: key is null "
                            + "and component " + c.toString() + "/" + c.getKey()
                            + " did not register with page.");
                }
            }
            return COMPONENT_PREFIX + key + DELIMITER;
        }
    }

    /**
     * Undo the name change that {@link #parameterName} does.
     *
     * @param name a possibly mangled name
     *
     * @return the unmangled name.
     */
    private static final String unmangle(String name) {
        if (!name.startsWith(COMPONENT_PREFIX)) {
            return name;
        }
        // Find the second occurence of delimiter
        int prefix = name.indexOf(DELIMITER, name.indexOf(DELIMITER) + 1);
        if (prefix >= 0 && prefix < name.length()) {
            return name.substring(prefix + 1);
        }
        return name;
    }

    // Procs for debugging output
    private static String NAME = "name";

    /**
     * Produces an XML fragment that captures the layout of this page.
     */
    private void showStructure(PageState s, Element root) {
        final HttpServletRequest req = s.getRequest();
        Element state = root.newChildElement("bebop:state", BEBOP_XML_NS);
        // Selected component
        String sel = req.getParameter(m_selected.getName());
        Element selected = state.newChildElement("bebop:selected", BEBOP_XML_NS);

        selected.addAttribute(NAME, m_selected.getName());
        selected.setText(sel);

        // Control event
        Element eventName = state.newChildElement("bebop:eventName",
                                                  BEBOP_XML_NS);
        eventName.addAttribute(NAME, m_controlEvent.getName());
        eventName.setText(req.getParameter(m_controlEvent.getName()));
        Element eventValue = state.newChildElement("bebop:eventValue",
                                                   BEBOP_XML_NS);
        eventValue.addAttribute(NAME, m_controlValue.getName());
        eventValue.setText(req.getParameter(m_controlValue.getName()));

        // Global parameters
        Element globalState = root.newChildElement("bebop:params", BEBOP_XML_NS);
        for (Iterator ii = getStateModel().getParameters(); ii.hasNext();) {
            ParameterModel p = (ParameterModel) ii.next();
            if (!p.getName().startsWith(COMPONENT_PREFIX)) {
                Element param = globalState.newChildElement("bebop:param",
                                                            BEBOP_XML_NS);
                param.addAttribute(NAME, p.getName());
                param.setText(String.valueOf(s.getValue(p)));
            }
        }

        showVisibility(s, this, root);
    }

    /**
     * @see showStructure(PageState, Element)
     */
    private void showVisibility(PageState s, Component c, Element parent) {
        HttpServletRequest req = s.getRequest();

        Element cmp = parent.newChildElement("bebop:component", BEBOP_XML_NS);
        cmp.addAttribute(NAME, getDebugLabel(c));
        cmp.addAttribute("idx", String.valueOf(stateIndex(c)));
        cmp.addAttribute("isVisible", (s.isVisible(c) ? "yes" : "no"));
        cmp.addAttribute("class", c.getClass().getName());

        if (c.getKey() != null) {
            String prefix = componentPrefix(c);
            for (Iterator i = getStateModel().getParameters(); i.hasNext();) {
                ParameterModel p = (ParameterModel) i.next();
                if (!p.getName().startsWith(prefix)) {
                    continue;
                }

                Element param = parent.newChildElement("bebop:param",
                                                       BEBOP_XML_NS);
                param.addAttribute(NAME, unmangle(p.getName()));
                param.addAttribute("defaultValue",
                                   String.valueOf(req.getParameter(p.getName())));
                param
                    .addAttribute("currentValue", String.valueOf(s.getValue(p)));
            }
        }
        for (Iterator i = c.children(); i.hasNext();) {
            showVisibility(s, ((Component) i.next()), cmp);
        }
    }

    private static String getDebugLabel(Component c) {
        if (c.getKey() != null) {
            return c.getKey();
        }

        String klass = c.getClass().getName();
        return klass.substring(klass.lastIndexOf(".") + 1, klass.length());
    }

    /**
     * return a string that represents an ordered list of component ids used on
     * the page. For situations where only the components present is of
     * importance, this may be used by implementations of hashCode & equals
     *
     * @return
     */
    public String getComponentString() {
        Iterator it = m_componentMap.keySet().iterator();
        /*int hash = 0;
         while (it.hasNext()) {
         String componentId = (String)it.next();
         s_log.debug("component id = " + componentId);
         hash = hash | componentId.hashCode();
         s_log.debug("hash so far = " + hash);
         }*/
        Date start = new Date();

        StringBuilder hashString = new StringBuilder();
        while (it.hasNext()) {
            String componentId = (String) it.next();
            hashString.append(componentId);
        }
        LOGGER.debug("Time to create hashCode for page: " + (new Date().getTime()
                                                            - start.
                                                            getTime()));
        return hashString.toString();

    }

}
