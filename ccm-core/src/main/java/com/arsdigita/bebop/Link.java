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
import javax.servlet.http.HttpServletResponse;

import com.arsdigita.xml.Element;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.util.Assert;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A URL on a page. May contain a label, an image, or any other component. A
 * Link is a BaseLink that manages URL variables.
 *
 * <p>
 * <b>Example:</b> The common usage for a Link component is illustrated in the
 * code fragment below:
 *
 * <pre>
 *  Page p = new Page("Link Example");
 *  Link link = new Link(new Label(GlobalizedMessage),"path/to/target/");
 *  link.setVar("foo","1");
 *  p.add(link);
 * </pre>
 *
 * <p>
 * The target of the link above will be rendered in HTML as:
 * <tt>href="path/to/target/?foo=1"</tt>
 * If either the link text or the URL needs to be changed for a link within a
 * locked page, a {@link PrintListener} should be used.
 */
public class Link extends BaseLink {

    private static final Logger LOGGER = LogManager
        .getLogger(ParameterMap.class);

    private static final String FRAME_TARGET_ATTR = "target";

    private URL m_webURL = null;
    private ParameterMap m_params = new ParameterMap();

    /**
     * The value for the XML type attribute for a plain link.
     */
    protected final String TYPE_LINK = "link";

    /**
     * <p>
     * Passing this value to {@link #setTargetFrame setTargetFrame} will create
     * a link that opens a new browser window whenever it is clicked.
     * </p>
     *
     * @see #setTargetFrame
     */
    public static final String NEW_FRAME = "_blank";

    /**
     * initialization steps common to all constructors
     */
    private void init() {
        setTypeAttr(TYPE_LINK);
    }

    /**
     * Constructor creates a link taking url as the target and display it to the
     * user at the same time. It is the only allowed way to present the user
     * with a not globlized information. The implementation currently miss-uses
     * the Label component to display just a not globalized String which is
     * deprecated.
     *
     * @param url
     *
     * @deprecated use BaseLink(Component,url) instead with a Label using a
     * GlobalizedMessage instead
     */
    public Link(final String url) {
        super(new Label(url), url);
    }

    /**
     * Constructor, creates a link with a globalized label or an image as label.
     *
     * @param child The <tt>Component</tt> parameter in this constructor is
     *              usually a {@link Label} or {@link Image}.
     * @param url   Starting with release 5.2, this method prefixes the
     *              passed-in url with the path to the CCM dispatcher. Code
     *              using this constructor should not prefix <code>url</code>
     *              with the webapp context path or the dispatcher servlet path.
     *
     * The vast majority of CCM UI code expects to link through the dispatcher.
     * Code that does not should use the <code>Link</code> constructor taking a
     * <code>URL</code>.
     *
     * @see #Link(String,URL)
     */
    public Link(Component child, String url) {
        super(child, url);
        init();
    }

    /**
     * Constructors with <tt>PrintListener</tt> parameters allow for a
     * {@link PrintListener} to be set for the Link, without the need to make a
     * separate call to the <tt>addPrintListener</tt> method. PrintListeners are
     * a convenient way to alter underlying Link attributes such as Link text or
     * target URL within a locked page on a per request basis.
     *
     * @param child
     * @param l
     */
    public Link(Component child, PrintListener l) {
        super(child, l);

        init();
    }

    /**
     * Constructors with <tt>PrintListener</tt> parameters allow for a
     * {@link PrintListener} to be set for the Link, without the need to make a
     * separate call to the <tt>addPrintListener</tt> method. PrintListeners are
     * a convenient way to alter underlying Link attributes such as Link text or
     * target URL within a locked page on a per request basis.
     *
     * @deprecated refactor to use Link(Component,PrintListener) to provide a
     * globalized label for the link.
     */
    public Link(String label, PrintListener l) {
        super(label, l);

        init();
    }

    /**
     * Constructors with <tt>PrintListener</tt> parameters allow for a
     * {@link PrintListener} to be set for the Link, without the need to make a
     * separate call to the <tt>addPrintListener</tt> method. PrintListeners are
     * a convenient way to alter underlying Link attributes such as Link text or
     * target URL within a locked page on a per request basis.
     *
     * @param listener PrintListener, may be used to change either the Display
     *                 text or the url within a locked page.
     */
    public Link(PrintListener listener) {
        super(listener);

        init();
    }

    /**
     * <p>
     * This constructor is a common one for a Link component, as it allows for
     * the Link text and the target URL to be set at the same time during
     * construction.</p>
     *
     * <p>
     * Starting with release 5.2, this method prefixes the passed-in
     * <code>url</code> with the path to the CCM dispatcher. Code using this
     * constructor should not prefix <code>url</code> with the webapp context
     * path or the dispatcher servlet path.</p>
     *
     * <p>
     * The vast majority of CCM UI code expects to link through the dispatcher.
     * Code that does not should use the <code>Link</code> constructor taking a
     * <code>URL</code>.</p>
     *
     * @see #Link(String,URL)
     * @deprecated refactor to use Link(Component,PrintListener) to provide a
     * globalized label for the link.
     */
    public Link(String label, String url) {
        super(label, url);

        init();
    }

    /**
     * <p>
     * Constructs a Link using a <code>URL</code>. When this constructor is
     * used, the method {@link #setVar(String,String)} and its deprecated
     * equivalent have no effect on the resulting hyperlink. Instead, use the
     * <code>ParameterMap</code> argument to <code>URL</code>.</p>
     *
     * @see com.arsdigita.web.URL
     * @see com.arsdigita.web.ParameterMap
     * @param label a <code>String</code> of label text
     * @param url   a <code>URL</code> for the link's target
     *
     * @deprecated refactor to use Link(Component,URL) to provide a globalized
     * label for the link.
     */
    public Link(String label, URL url) {
        super(label, url.toString());

        init();

        m_webURL = url;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Link result = (Link) super.clone();

        result.m_params = (ParameterMap) m_params.clone();

        return result;
    }

    /**
     * Sets a query variable and its value. Overwrites any values that may have
     * been set previously under the specified name.
     * <p>
     * All the variables set with this method are appended to the query string
     * in the URL that is output for this <code>Link</code>.
     *
     * @param name  the name of the query
     * @param value the value for the query
     *
     * @pre name != null
     */
    public void setVar(String name, String value) {
        Assert.isUnlocked(this);

        m_params.setParameter(name, value);
    }

//  No longer used anywhere in the code base
//  /**
//   * Set a query variable and its value
//   * @deprecated use {@link #setVar setVar}
//   */
//  public void addURLVars(String name, String value) {
//      setVar(name, value);
//  }
    /**
     *
     * @return may be this method should be deprecated as well as addURLVars?
     */
    public String getURLVarString() {
        return m_params.toString();
    }

    /**
     * <p>
     * Get the "target" attribute of the link, which determines which browser
     * frame will load the new page when this link is clicked.</p>
     */
    public String getTargetFrame() {
        return getAttribute(FRAME_TARGET_ATTR);
    }

    /**
     * <p>
     * Set the "target" attribute of the link, which determines which browser
     * frame will load the new page when this link is clicked.</p>
     */
    public void setTargetFrame(String frameName) {
        setAttribute(FRAME_TARGET_ATTR, frameName);
    }

    /**
     *
     * @param state
     * @param parent
     */
    protected void generateURL(PageState state, Element parent) {
        parent.addAttribute("href", prepareURL(state, getTarget()));

        exportAttributes(parent);
    }

    /**
     * Processes the URL for this link after the print listener runs.
     *
     * @param location the original URL
     *
     * @return the URL appended with ACS-specific URL parameters.
     */
    protected String prepareURL(final PageState state, String location) {

        final HttpServletRequest req = state.getRequest();
        final HttpServletResponse resp = state.getResponse();

        if (m_webURL == null) {
            m_params.runListeners(req);

            if (location.startsWith("/")) {
                location = URL.getDispatcherPath() + location;
            }

            if (location.indexOf("?") == -1) {
                // m_params adds the "?" as needed.

                return resp.encodeURL(location + m_params);
            } else {
                // The location already includes a query string, so
                // append to it without including a "?".

                if (location.endsWith("&")) {
                    return resp.encodeURL(location + m_params.getQueryString());
                } else {
                    return resp.encodeURL(location + "&" + m_params
                        .getQueryString());
                }
            }
        } else {
            return m_webURL.toString();
        }
    }

}
