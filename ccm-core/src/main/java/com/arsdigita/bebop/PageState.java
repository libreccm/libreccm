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

import com.arsdigita.bebop.parameters.BitSetParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.Traversal;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>The request-specific data (state) for a {@link Page}. All
 * methods that need access to the state of the associated page during
 * the processing of an HTTP request are passed a
 * <code>PageState</code> object. Since <code>PageState</code>
 * contains request-specific data, it should only be live during the
 * servicing of one HTTP request. This class has several related
 * responsibilites:</p>
 *
 * <h3>State Management</h3>
 *
 * <p><code>PageState</code> objects store the values for global and
 * component state parameters and are responsible for retrieving these
 * values from the HTTP request. Components can access the values of
 * their state parameters through the {@link #getValue getValue} and
 * {@link #setValue setValue} methods.</p>
 *
 * <p>This class is also responsible for serializing the current state of
 * the page in a variety of ways for inclusion in the output: {@link
 * #generateXML generateXML} adds the state to an XML element and {@link
 * #stateAsURL stateAsURL} encodes the page's URL. </p>
 *
 * <p>The serialized form of the current page state can be quite large
 * so the page state can also be preserved in the HttpSession, on the
 * server.  The Page object specifies this by calling {@link
 * Page#setUsingHttpSession setUsingHttpSession(true)}.  When this
 * flag is set, then the page state will be preserved in the
 * HttpSession, and the {@link #stateAsURL stateAsURL} method will
 * only serialize the current URL and the control event.  It will also
 * include a URL variable that the subsequent request uses to retrieve
 * the correct page state from the HttpSession.  If the page state for
 * a particular request cannot be found, the constructor will throw a
 * {@link SessionExpiredException SessionExpiredException}.</p>
 *
 * <p>Up to {@link #getMaxStatesInSession getMaxStatesInSession()}
 * independent copies of the page state may be stored in the
 * HttpSession, to preserve the behavior of the browser's "back"
 * button.</p>
 *
 * <p><strong>Note:</strong> As a convention, only the component to which a
 * state parameter belongs should modify it by calling
 * <code>getValue</code> and <code>setValue</code>. All other objects
 * should manipulate the state of a component <i>only</i> through
 * well-defined methods on the component.</p>
 *
 * <h3>Control Events</h3>
 *
 * <p>The control event consists of a pair of strings, the <i>name</i>
 * of the event and its associated <i>value</i>. Components use the
 * control event to send themselves a "delayed signal", i.e. a signal
 * that is triggered when the same page is requested again through a
 * link that has been generated with the result of {@link #stateAsURL}
 * as the target. The component can then access the name and value of
 * the event with calls to {@link #getControlEventName} and {@link
 * #getControlEventValue}. </p>
 *
 * <p> Typically, a component will contain code corresponding to the following
 * in its <code>generateXML</code> method:
 * <pre>
 *  public void generateXML(PageState state, Element parent) {
 *  if ( isVisible(state) ) {
 *      <em>MyComponent</em> target = firePrintEvent(state);
 *      Element link = new Element ("bebop:link", BEBOP_XML_NS);
 *      parent.addContent(link);
 *      target.generateURL(state, link);
 *      target.exportAttributes(link);
 *      target.generateExtraXMLAttributes(state, link);
 *      target.getChild().generateXML(state, link);
 *  }
 *  }
 * </pre>
 * (In reality, the component would not write a <code>bebop:link</code> element
 * directly, but use a {@link ControlLink} to do that automatically.)
 * <p> When the user clicks on the link that the above <code>generateXML</code>
 * method produces, the component's <code>respond</code> method is called
 * and can access the name and value of the event with code similar to the
 * following:
 * <pre>
 *   public void respond(PageState state) {
 *     String name = state.getControlEventName();
 *     String value = state.getControlEventValue();
 *     if ( "name".equals(name) ) {
 *       doSomeStateChange(value);
 *     } else {
 *       throw new IllegalArgumentException("Can't understand event " + name);
 *     }
 *   }
 * </pre>
 *
 *
 * <h3>Temporary Storage</h3>
 *
 * <p> Request local variables make it possible to store arbitrary objects
 * in the page state and allow components (and other objects) to cache
 * results of previous computations. For example, the {@link Form}
 * component uses a request local variable to store the {@link FormData} it
 * generates from the request and make it acessible to the widgets it
 * contains, and to other components through calling {@link
 * Form#getFormData Form.getFormData(state)}. See the documentation for
 * {@link RequestLocal} on how to use your own request local variables.
 *
 * <h3>Convenience Access to Related Objects</h3>
 *
 * <p> <code>PageState</code> objects store references to the HTTP request
 * and response that is currently being served. Components are free to
 * manipulate these objects as they see fit.
 * </p>
 *
 * @author David Lutterkort
 * @author Uday Mathur
 */
public class PageState {

    /** Class specific logger instance. */
    private static final Logger LOGGER = LogManager.getLogger(PageState.class);

    /** The underlying Page object. */
    private Page m_page;

    /**
     * The request to which this object corresponds
     */
    private HttpServletRequest m_request;

    /**
     * The response to which results will be sent.
     */
    private HttpServletResponse m_response;

    /**
     * The values of global and component specific state parameters extracted
     * from the request.
     */
    private FormData m_pageState;

    /**
     * Temporary storage of arbitrary objects.
     */
    private Map m_attributes;

    /**
     * The component that currently holds exclusive access to the control
     * event. Usually null, unlesss a component calls {@link
     * #grabControlEvent}.
     */
    private Component m_grabbingComponent;

    /**
     * The visibility state of components. For a component with n =
     * Page.stateIndex, the n-th bit is set if the component is <i>not</i>
     * visible.
     *
     * <p> Initially, this variable refers to Page.m_invisible. Only when a
     * call to {@link #setVisible} is made, is that value
     * copied. (Copy-on-write)
     */
    private BitSet m_invisible;
    private boolean m_visibilityDirty = true;

    private int m_nextSession;

    private final static String SESSION_ATTRIBUTE =
        "com.arsdigita.bebop.FormData";
    private final static String SESSION_COUNTER_ATTRIBUTE =
        "com.arsdigita.bebop.FormData.counter";
    private final static String CURRENT_SESSION_PARAMETER =
        "bbp.session";
    private final static String PAGE_STATE_ATTRIBUTE =
        "com.arsdigita.bebop.PageState";

    private static int s_maxSessions = 10;

    private Traversal m_visibilityTraversal = new VisibilityTraversal();
    private List m_visibleComponents;

    /**
     * Returns the maximum number of independent page states that may be stored
     * in the HttpSession, for preserving the behavior of the "back" button.
     * @return the maximum number of independent page states that may
     * be stored in the HttpSession.
     */
    public static int getMaxStatesInSession() {
        return s_maxSessions;
    }

    /**
     * Sets the maximum number of independent page states that may be stored in
     * the HttpSession, for preserving the behavior of the "back" button.
     * @param x the maximum number of independent page states to store
     * in the HttpSession.
     */
    public static void setMaxStatesInSession(int x) {
        s_maxSessions = x;
    }

    /**
     * Returns the page state object for the given request, or null if none
     * exists yet.
     *
     * @param request The servlet request.
     *
     * @return The page state object for the given request, or null if none
     *         exists yet.
     **/
    public static PageState getPageState(HttpServletRequest request) {
        return (PageState) request.getAttribute(PAGE_STATE_ATTRIBUTE);
    }

    /**
     * Returns the page state object for the current request, or null if none
     * exists yet.
     *
     * @return The page state object for the current request, or null if none
     *         exists yet.
     **/

    public static PageState getPageState() {
        HttpServletRequest request = DispatcherHelper.getRequest();

        if (request == null) {
            return null;
        } else {
            return getPageState(request);
        }
    }

    /**
     * Construct the PageState for an HTTP request.
     *
     * Calls {@link FormModel#process process} on the form model underlying
     * the page model and calls {@link Component#respond respond} on the
     * component from which the request originated.
     *
     * @param page The model of the page
     * @param request       The request being served
     * @param response       Where the response should be sent
     *
     * @pre request != null && request.get(page.PAGE_SELECTED) != null
     * @pre response != null && ! response.isCommitted()
     * @pre page != null
     *
     */
    public PageState(Page page, HttpServletRequest request,
                     HttpServletResponse response)
            throws ServletException {
        m_page = page;

        if ( m_page == null ) {
            m_page = new Page();
            m_page.lock();
        }

        m_request = request;
        m_response = response;

        FormData pageStateFromSession = null;
        if (m_page.isUsingHttpSession()) {
            pageStateFromSession = getStateFromSession();
        }

        // always treat the request as a submission
        m_pageState = new FormData(m_page.getStateModel(), request, true,
                                   pageStateFromSession);
        m_page.getStateModel().process(this, m_pageState);
        m_invisible = decodeVisibility();

        // Add the PageState to the request
        m_request.setAttribute(PAGE_STATE_ATTRIBUTE, this);
    }


    protected BitSet decodeVisibility() {
        BitSet difference = (BitSet)m_pageState.get(Page.INVISIBLE);
        BitSet current = (BitSet)m_page.m_invisible.clone();
        if (difference != null) {
            current.xor(difference);
        }
        return current;
    }
    
    protected BitSet encodeVisibility(BitSet current) {
        BitSet difference = (BitSet)m_page.m_invisible.clone();
        difference.xor(current);
        return difference;
    }

    /**
     * Helper function that returns the PageState object from the
     * HttpSession.
     */
    private FormData getStateFromSession() throws SessionExpiredException {
        HttpSession session = m_request.getSession(true);
        FormData state = null;

        // have to bootstrap this manually from the request
        String key =
            m_request.getParameter(CURRENT_SESSION_PARAMETER);
        if (key != null) {
            state = (FormData)session.getAttribute(SESSION_ATTRIBUTE + key);
            // session is expired if we're looking for a particular
            // page state and couldn't find it.
            if (state == null) {
                throw new SessionExpiredException();
            }
        }
        return state;
    }

    /**
     * Process the PageState and fire necessary events. Call respond on the
     * selected bebop component.
     */
    public void respond() throws ServletException {
        String compKey = (String) m_pageState.get(Page.SELECTED);

        if ( compKey != null ) {
            Component c = m_page.getComponent(compKey);
            if ( c == null ) {
                throw new ServletException("Selected component not on page.");
            }
            try {
                c.respond(this);
            } finally {
            }
        }
    }

    /**
     * Return the page for which this object holds the state.
     *
     * @return the page for which this object holds state.
     * @post return != null
     */
    public final Page getPage() {
        return m_page;
    }


    /**
     * Return the request object for the HTTP request.
     *
     * @return The HTTP request being currently served.
     */
    public final HttpServletRequest getRequest() {
        return m_request;
    }

    /**
     * Return the response object for the HTTP response.
     *
     * @return The response for the HTTP request being served
     */
    public final HttpServletResponse getResponse() {
        return m_response;
    }

    /**
     * The index of a component in the page model
     *
     * @pre c != null
     */
    private int indexOf(Component c) {
        return m_page.stateIndex(c);
    }

    /**
     * Return <code>true</code> is the comonent <code>c</code> is currently
     * visible. This method should only be used by components
     * internally. All other objects should call {@link Component#isVisible
     * Component.isVisible} on the component.
     *
     * @param c the components whose visibility should be returned
     * @return <code>true</code> if the component is visible
     */
    public boolean isVisible(Component c) {
        if ( ! getPage().stateContains(c)) {
            return true;
        }
        if ( m_invisible == null ) {
            return m_page.isVisibleDefault(c);
        } else {
            return ! m_invisible.get(indexOf(c));
        }
    }

    /**
     * Return <code>true</code> is the component <code>c</code> is currently
     * visible on the page displayed to the end user.  This is true
     * if the component's visibility flag is set, and it is in a
     * container visible to the end user.
     *
     * <p>This method is different than <code>isVisible</code> which
     * only returns the visibility flag for the individual component.
     *
     * @param c the components whose visibility should be returned
     * @return <code>true</code> if the component is visible
     */
    public boolean isVisibleOnPage(Component c) {
        if (m_visibleComponents == null) {
            m_visibleComponents = new ArrayList();
            m_visibilityTraversal.preorder(getPage().getPanel());
        }

        return m_visibleComponents.contains(c);
    }

    private class VisibilityTraversal extends Traversal {
        protected void act(Component c) {
            m_visibleComponents.add(c);
        }

        protected int test(Component c) {
            if (isVisible(c)) {
                return PERFORM_ACTION;
            } else {
                // not visible, so neither are children
                return SKIP_SUBTREE;
            }
        }
    }


    /**
     * Set the visibility of a component. Calls to this method change the
     * default visibility set with {@link Page#setVisibleDefault
     * Page.setVisibleDefault}. This method should only be used by
     * components internally. All other objects should call {@link
     * Component#setVisible Component.setVisible} on the component.
     *
     * <p> Without explicit changes, a component is visible.
     *
     * @param c the component whose visibility is to be changed
     * @param v <code>true</code> if the component should be visible
     */
    public void setVisible(final Component c, final boolean v) {
        if (Assert.isEnabled()) {
            Assert.isTrue(getPage().stateContains(c),
                         "Component" + c + " is not registered on Page " +
                         getPage());
        }

        if (m_invisible == null || m_invisible == getPage().m_invisible) {
            // copy on write
            m_invisible = (BitSet) getPage().m_invisible.clone();
        }

        int i = indexOf(c);

        if (v) {
	    if (!m_invisible.get(i))
		return;
            m_invisible.clear(i);
        } else {
	    if (m_invisible.get(i))
		return;
            m_invisible.set(i);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Marking visibility parameter as dirty " + m_request + " because of component " + c);
        }
        // Do this only in toURL since the RLE is expensive
        //m_pageState.put(Page.INVISIBLE, encodeVisibility(m_invisible));
        m_visibilityDirty = true;
        m_visibleComponents = null;
    }

    /**
     * Resets the given component and its children to their default
     * visibility. Also resets the state parameters of the given
     * component and its children to null. This is not a speedy
     * method. Do not call gratuitously.
     *
     * @param c the parent component whose state parameters and
     * visibility you wish to reset.
     * */
    public void reset(Component c) {
        getPage().reset(this, c);
    }

//  /**
//   * Store an attribute keyed on the object <code>key</code>.  The
//   * <code>PageState</code> puts no restrictions on what can be stored as
//   * an attribute or how they are managed.
//   *
//   * To remove an attribute, call <code>setAttribute(key, null)</code>.
//   *
//   * The attributes are only accessible as long as the
//   * <code>PageState</code> is alive, typically only for the duration of
//   * the request.
//   *
//   * @deprecated Use either <code>setAttribute</code> on {@link
//   * HttpServletRequest the HTTP request object}, or, preferrably, use a
//   * {@link RequestLocal request local} variable. Will be removed on
//   * 2001-06-13.
//   *
//   */
//  public void setAttribute(Object key, Object value) {
//      if ( m_attributes == null ) {
//          m_attributes = new HashMap();
//      }
//      m_attributes.put(key, value);
//  }

//  /**
//   * Get the value of an attribute stored with the same key with {@link
//   * #setAttribute setAttribute}.
//   *
//   * @deprecated Use either <code>getAttribute</code> on {@link
//   * HttpServletRequest the HTTP request object}, or, preferrably, use a
//   * {@link RequestLocal request local} variable. Will be removed on
//   * 2001-06-13.
//   *
//   */
//  public Object getAttribute(Object key) {
//      if ( m_attributes == null ) {
//          return null;
//      }
//      return m_attributes.get(key);
//  }

    /**
     * Set the value of the state parameter <code>p</code>. The concrete
     * type of <code>value</code> must be compatible with the type of the
     * state parameter.
     *
     * <p> The parameter must have been previously added with a call to
     * {@link Page#addComponentStateParam
     * Page.getComponentStateParam}. This method should only be called by
     * the component that added the state parameter to the page. Users of
     * the component should manipulate the parameter through methods the
     * component provides.
     *
     * @param p a state parameter
     * @param value the new value for this state parameter. The concrete
     * type depends on the type of the parameter being used.
     */
    public void setValue(ParameterModel p, Object value) {
        m_pageState.put(p.getName(), value);
    }

    /**
     * Get the value of state parameter <code>p</code>. The concrete type of
     * the return value depends on the type of the parameter being
     * used.
     *
     * <p> The parameter must have been previously added with a call to
     * {@link Page#addComponentStateParam
     * Page.addComponentStateParam}. This method should only be called by
     * the component that added the state parameter to the page. Users of
     * the component should manipulate the parameter through methods the
     * component provides.
     *
     * @param p a state parameter
     * @return the current value for this state parameter. The concrete
     * type depends on the type of the parameter being used.
     */
    public Object getValue(ParameterModel p) {
        return m_pageState.get(p.getName());
    }

    /**
     * Get the value of a global state parameter.
     * @deprecated Use {@link #getValue(ParameterModel m)} instead. If you
     * don't have a reference to the parameter model, you should not be
     * calling this method. Instead, the component that registered the
     * parameter should provide methods to manipulate it. Will be removed
     * 2001-06-20.
     */
    public Object getGlobalValue(String name) {
        // WRS 9/7/01
        // work-around: m_pageState will throw an exception when we get
        // a named parameter that's not in the form model.  But for
        //  API stability, we need to trap this and return null; since
        // there's no way to tell what set of globalValues are legal
        // in any particular page--that's what ParameterModels are for.
        try {
            return m_pageState.get(m_page.parameterName(name));
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

//  /**
//   * Change the value of a global parameter
//   *
//   * @deprecated Use {@link #setValue(ParameterModel m, Object o)}
//   * instead. If you don't have a reference to the parameter model, you
//   * should not be calling this method. Instead, the component that
//   * registered the parameter should provide methods to manipulate
//   * it. Will be removed 2001-06-20.
//   */
//  public void setGlobalValue(String name, Object value) {
//      m_pageState.put(m_page.parameterName(name), value);
//  }

    // Handling the control event

    /**
     * Grab the control event. Until {@link #releaseControlEvent
     * releaseControlEvent(c)} is called, only the component <code>c</code>
     * can be used in calls to {@link #setControlEvent setControlEvent}.
     * @pre c != null
     */
    public void grabControlEvent(Component c) {
        if ( m_grabbingComponent != null && m_grabbingComponent != c ) {
            throw new IllegalStateException
                ("Component " + m_grabbingComponent.toString() +
                 " already holds the control event");
        }
        m_grabbingComponent = c;
    }

    /**
     * Set the control event. The control event is a <i>delayed</i> event
     * that only gets acted on when another request to this <code>Page</code>
     * is made. It is used to set which component should receive the
     * submission and lets the component set one component-specific name-value
     * pair to be used in the submission.
     * <p>
     * After calling this method links and hidden form controls generated
     * with {@link #stateAsURL} have been amended so that if the user clicks such a
     * link or submits a form containing those hidden controls, the exact
     * same values can be retrieved with {@link #getControlEventName} and
     * {@link #getControlEventValue}.
     * <p>
     * Stateful components can use the control event to change their
     * state. For example, a tabbed pane <code>t</code> might call
     * <code>setControlEvent(t, "select", "2")</code> just prior to
     * generating the link for its second tab.
     * <p>
     * The values of <code>name</code> and <code>value</code> have no
     * meaning to the page state, they are simply passed through without
     * modifications. It is up to specific components what values of
     * <code>name</code> and <code>value</code> are meaningful for it.
     *
     * @param c
     * @param name The component specific name of the event, may be
     * <code>null</code>
     * @param value The component specific value of the event, may be
     * <code>null</code>
     * @pre c == null || getPage().stateContains(c)
     */
    public void setControlEvent(Component c, String name, String value) {
        Assert.isTrue(c == null || getPage().stateContains(c),
                          "c == null || getPage().stateContains(c)");
        if ( m_grabbingComponent != null && m_grabbingComponent != c ) {
            throw new IllegalStateException
                ("Component " + m_grabbingComponent.toString() +
                 " holds the control event");
        }
        // FIXME: This needs to take named components into account
        String key = null;
        if (c != null) {
            key = c.getKey();
            if (key == null) {
                key = Integer.toString(m_page.stateIndex(c));
            }
        }
        m_pageState.put(Page.SELECTED,
                        (c == null) ? null : key);
        m_pageState.put(Page.CONTROL_EVENT, name);
        m_pageState.put(Page.CONTROL_VALUE, value);
    }

    /**
     * Set the control event. Both the event name and its value will be
     * <code>null</code>.
     */
    public void setControlEvent(Component c) {
        setControlEvent(c, null, null);
    }

    /**
     * Clear the control event. Links and hidden form variables generated
     * after this call will not cause any component's respond method to be
     * called.
     *
     * @throws IllegalStateException if any component has grabbed the
     * control event but not released it yet.
     */
    public void clearControlEvent() {
        setControlEvent(null);
    }

    /**
     * Get the name of the control event.
     */
    public String getControlEventName() {
        return (String) m_pageState.get(Page.CONTROL_EVENT);
    }

    /**
     * Get the value associated with the control event.
     */
    public String getControlEventValue() {
        return (String) m_pageState.get(Page.CONTROL_VALUE);
    }

    /**
     * Release the control event.
     * @param c The component that was passed to the last call to
     * <code>grabControlEvent</code>
     * @pre getPage().stateContains(c)
     */
    public void releaseControlEvent(Component c) {
        if ( m_grabbingComponent == null ) {
            throw new IllegalStateException
                ("No component holds the control event, but " + c.toString() +
                 " tries to release it.");
        }
        if ( c != m_grabbingComponent ) {
            throw new IllegalStateException
                ("Component " + m_grabbingComponent.toString() +
                 " holds the control event, but " + c.toString() +
                 " tries to release it.");
        }
        m_grabbingComponent = null;
    }

    /**
     * Add elements to <code>parent</code> that represent the current page
     * state. For each component or global state parameter on the page, a
     * <tt>&lt;bebop:pageState></tt> element is added to
     * <code>parent</code>. The <tt>name</tt> and <tt>value</tt> attributes
     * of the element contain the name and value of the state parameters as
     * they should appear in an HTTP request made back to this page.
     *
     * <p>Generates DOM fragment:
     * <p><code><pre>
     * &lt;bebop:pageState name=... value=.../>
     * </code></pre>
     *
     * @param form This is the form in which the hidden variables will exist
     *
     * @see #setControlEvent setControlEvent
     */
    public void generateXML(Element parent) {
        synchronizeVisibility();

        for ( Iterator i = m_pageState.getParameters().iterator();
              i.hasNext(); ) {
            ParameterData p = (ParameterData) i.next();

            String key = (String) p.getName();
            String value = p.marshal();

            if ( value != null ) {
                Element hidden = parent.newChildElement("bebop:pageState",
                                                        Component.BEBOP_XML_NS);
                hidden.addAttribute("name", key);
                hidden.addAttribute("value", value);
            }
        }
    }

    public void generateXML(Element parent, Iterator models) {
        synchronizeVisibility();

        List excludeParams = new ArrayList();
        if (models != null) {
            while (models.hasNext()) {
                excludeParams.add(((ParameterModel)models.next()).getName());
            }
        }

        for ( Iterator i = m_pageState.getParameters().iterator();
              i.hasNext(); ) {
            ParameterData p = (ParameterData) i.next();

            String key = (String) p.getName();
            String value = p.marshal();

            if (value == null || excludeParams.contains(key)) {
                continue;
            }

            Element hidden = parent.newChildElement("bebop:pageState",
                                                    Component.BEBOP_XML_NS);
            hidden.addAttribute("name", key);
            hidden.addAttribute("value", value);
        }
    }

    /**
     * Export the current page state into the HttpSession by putting the entire
     * m_pageState (type FormData) object into the HttpSession.
     *
     * <p>
     * Package visibility is intentional.
     *
     * @see #setControlEvent setControlEvent */
    void stateAsHttpSession() {
        // create session if we need to
        HttpSession session = m_request.getSession(true);
        // get + increment counter counter
        Integer counterObj =
            (Integer)session.getAttribute(SESSION_COUNTER_ATTRIBUTE);
        if (counterObj == null) {
            m_nextSession = 0;
        } else {
            m_nextSession = counterObj.intValue() + 1;
        }

        session.setAttribute(SESSION_ATTRIBUTE + m_nextSession, m_pageState);
        session.setAttribute(SESSION_COUNTER_ATTRIBUTE,
                             new Integer(m_nextSession));
        // remove an old session
        int toRemove = m_nextSession - s_maxSessions;
        if (toRemove >= 0) {
            session.removeAttribute(SESSION_ATTRIBUTE + toRemove);
        }
    }

    /**
     * <p>Write the current state of the page as a URL.</p>
     *
     * <p>The URL representing the state points to the same URL that
     * the current request was made from and contains a query string
     * that represents the page state.</p>
     *
     * <p>If the current page has the useHttpSession flag set, then
     * the URL query string that we generate will only contain the
     * current value of the control event, and the rest of the page
     * state is preserved via the HttpSession.  Otherwise, the query
     * string contains the entire page state.</p>
     *
     * @return a string containing the current state of a page.
     * @see #setControlEvent setControlEvent
     * @see Page#isUsingHttpSession Page.isUsingHttpSession
     * @see Page#setUsingHttpSession Page.setUsingHttpSession
     */
    public String stateAsURL() throws IOException {
        return m_response.encodeURL(toURL().toString());
    }

    public final URL toURL() {
        synchronizeVisibility();

        final ParameterMap params = new ParameterMap();        

        if (LOGGER.isDebugEnabled()) {
            dumpVisibility();
        }

        final Iterator iter = m_pageState.getParameters().iterator();

        while (iter.hasNext()) {
            final ParameterData data = (ParameterData) iter.next();

            final String key = (String) data.getName();

            if (!m_page.isUsingHttpSession()
                || Page.CONTROL_EVENT_KEYS.contains(key)) {
                final String value = data.marshal();

                if (value != null) {
                    params.setParameter(key, value);
                }
            }

        }

        if (m_page.isUsingHttpSession()) {
            params.setParameter(CURRENT_SESSION_PARAMETER,
                                new Integer(m_nextSession));
        }

        return URL.request(m_request, params);
    }


    private void synchronizeVisibility() {
        if (m_visibilityDirty) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Encoding visibility parameter " + m_request);
            }
            m_pageState.put(Page.INVISIBLE, encodeVisibility(m_invisible));
            m_visibilityDirty = false;
        }
    }

    private void dumpVisibility() {
        BitSetParameter raw = new BitSetParameter("raw", BitSetParameter.ENCODE_RAW);
        BitSetParameter dgap = new BitSetParameter("dgap", BitSetParameter.ENCODE_DGAP);

        BitSet current = (BitSet)m_invisible;
        BitSet base = (BitSet)m_page.m_invisible;
        
        BitSet difference = (BitSet)current.clone();
        difference.xor(base);
        
        LOGGER.debug("Current: " + current.toString());
        LOGGER.debug("Default: " + base.toString());
        LOGGER.debug("Difference: " + difference.toString());

        LOGGER.debug("Current RAW: " + raw.marshal(current));
        LOGGER.debug("Default RAW: " + raw.marshal(base));
        LOGGER.debug("Difference RAW: " + raw.marshal(difference));

        LOGGER.debug("Current DGAP: " + dgap.marshal(current));
        LOGGER.debug("Default DGAP: " + dgap.marshal(base));
        LOGGER.debug("Difference DGAP: " + dgap.marshal(difference));
        
        LOGGER.debug("Current Result: " + dgap.unmarshal(dgap.marshal(current)));
        LOGGER.debug("Default Result: " + dgap.unmarshal(dgap.marshal(base)));
        LOGGER.debug("Difference Result: " + dgap.unmarshal(dgap.marshal(difference)));
        
        if (!current.equals(dgap.unmarshal(dgap.marshal(current)))) {
            LOGGER.debug("Broken marshal/unmarshal for current");
        }
        if (!base.equals(dgap.unmarshal(dgap.marshal(base)))) {
            LOGGER.debug("Broken marshal/unmarshal for default");
        }
        if (!difference.equals(dgap.unmarshal(dgap.marshal(difference)))) {
            LOGGER.debug("Broken marshal/unmarshal for difference");
        }
    }

    /**
     * Get the URI to which the current request was made. Copes with the
     * black magic that is needed to get the URI if the request was handled
     * through a dispatcher. If no dispatcher was involved in the request,
     * returns the request URI from the HTTP request.
     *
     * @post return != null
     *
     * @return the URI to which the current request was made
     */
    public String getRequestURI() {
        final URL url = Web.getWebContext().getRequestURL();

        if (url == null) {
            return m_request.getRequestURI();
        } else {
            return url.getRequestURI();
        }
    }

    /**
     * Return true if all the global and component state parameters
     * extracted from the HTTP request were successfully validated against
     * their parameter models in the {@link Page}.
     *
     * @return true if the values of all global and component state
     * parameters are valid with respect to their parameter models.
     */
    public boolean isValid() {
        return m_pageState.isValid();
    }

    /**
     * Return an iterator over the errors that occurred in trying to
     * validate the state parameters against their parameter models in
     * {@link Page}.
     *
     * @return an iterator over validation errors
     * @see FormData#getErrors
     */
    public Iterator getErrors() {
        return m_pageState.getAllErrors();
    }

    /**
     * Return a string with all the errors that occurred in trying to
     * validate the state parameters against their parameter models in
     * {@link Page}. The string consists simply of the concatenation of all
     * error messages that the result of {@link #getErrors} iterates over.
     *
     * @return all validation errors concatenated into one string
     */
    public String getErrorsString() {
        StringBuffer s = new StringBuffer();
        for (Iterator i = m_pageState.getAllErrors(); i.hasNext(); ) {
            s.append(i.next().toString());
            s.append(System.getProperty("line.separator"));
        }
        return s.toString();
    }

    /**
     * Force the validation of all global and component state parameters
     * against their parameter models. This method only needs to be called if
     * the values of the parameters have been changed with {@link #setValue
     * setValue} or {@link #setGlobalValue setGlobalValue} and may now
     * contain invalid values.
     */
    public void forceValidate() {
        m_pageState.forceValidate(this);
    }

    /** Convert to a String.
     *  @return a human-readable representation of <code>this</code>.
     */
    public String toString() {
        String newLine = System.getProperty("line.separator");
        String result =
            super.toString() + " = {" + newLine
            + "m_page = "              + m_page                      + "," + newLine
            + "m_request = "           + m_request                   + "," + newLine
            + "m_response = "          + m_response                  + "," + newLine
            // FormData
            + "m_pageState = "         + m_pageState.asString() + "," + newLine
            + "m_attributes = "        + m_attributes           + "," + newLine
            // Map to FormData
            + "," + newLine
            + "m_grabbingComponent = " + m_grabbingComponent         + "," + newLine
            + "m_invisible = "         + m_invisible                       + newLine
            + "}";
        return result;
    }
    
    /**
     * Clear the control event then redirect to the new page state.
     * 
     * @param isCommitRequested indicates if a commit required before the redirect
     * 
     * @throws RedirectSignal to the new page state
     * 
     * @see RedirectSignal#RedirectSignal(String, boolean)
     */
    public void redirectWithoutControlEvent(boolean isCommitRequested) {
        clearControlEvent();
        try {
            throw new RedirectSignal(stateAsURL(), true);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }
}
