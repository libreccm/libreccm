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

import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;
import java.util.TooManyListenersException;

/**
 * The parent of all Bebop Link classes, this class represents a URL on a page.
 * It may contain a label, an image, or any other component.
 *
 * <p> The following table lists all Bebop Link classes and suggests
 * when they might be used.
 * <p>
 * <table BORDER=3>
 * <tr>
 *  <th>Link Class</th>
 *  <th>Usage</th>
 * </tr>
 * <tr>
 *   <td>{@link BaseLink}</td>
 *   <td>Parent class of Bebop Link classes. Extend this class to
 * build your own Link class.</td>
 * </tr>
 * <tr>
 *   <td>{@link Link}</td>
 *   <td>Link class that manages its own URL variables. Session information
 * is added to the target URL for this type.</td>
 * </tr>
 * <tr>
 *   <td>{@link ExternalLink}</td>
 *   <td>Link that does not encode the URL with any session information.
 * Used for a link to a page outside the site.</td>
 * </tr>
 * <tr>
 *   <td>{@link ControlLink}</td>
 *   <td> Used for references within its own page (often
 *  as fields in a table header for sorting a column).</td>
 * </tr>
 * <tr>
 *    <td>{@link ActionLink}</td>
 *    <td>Sets its own control event and runs its own 
 * {@link com.arsdigita.bebop.event.ActionListener}s. When the link is clicked,
 * the code in the Listener's <tt>actionPerformed</tt> method runs.</td>
 * </tr>
 * <tr>
 *    <td>{@link ToggleLink}</td>
 *    <td>A link that turns into label when it is selected and
 * turns back into a link when it is unselected.</td>
 * </tr>
 * </table>
 *
 * @version $Id: BaseLink.java 998 2005-11-15 22:27:13Z sskracic $
 */
public abstract class BaseLink extends DescriptiveComponent
                               implements Cloneable {

    /** The name of the attribute used in XML to indicate which type of link 
     *  this link represents.                                                 */
    private final static String TYPE_ATTR = "type";
    private final static String HREF_NO_JAVASCRIPT = "href_no_javascript";
    private final static String HREF = "href";

    /** Component used to display the link. Typically a Label, may be 
     *  e.g. an image as well.                                                */
    protected Component m_child;

    /** Property to store the url the Link points to.                         */
    protected String m_url;

// Use the parent class' property!
//  /** Property to store informational text for the user about the Link, e.g. 
//   *  how to use it, or when to use it (or not to use it).                  */
//  private GlobalizedMessage m_hint;

    protected String m_noJavascriptURL = null;

    private PrintListener m_printListener;

    private String m_sConfirmMsg = "";
    private GlobalizedMessage m_confirmMsg;

    /**
     * Constructor creates a link taking url as the target and display it to
     * the user at the same time. It is the only allowed way to present the
     * user with a not globlized information. The implementation currently
     * miss-uses the Label component to display just a not globalized String
     * which is deprecated.
     *
     * @param url
     * @deprecated use BaseLink(Component,url) instead with a Label using a
     *             GlobalizedMessage instead
     */
    public BaseLink(final String url) {
        this(new Label(url), url);
    }

    /**
     * Constructor
     *
     * @param child display component (Label, Image, etc.)
     * @param url URL to point at
     */
    public BaseLink(final Component child, final String url) {
        super();
        m_url = url;
        m_child = child;
    }

    /**
     * Constructor.
     *
     * @param child display component (Label, Image, etc.)
     * @param listener PrintListener, may be used to change either the Display
     *                 text or the url within a locked page.
     */
    public BaseLink(final Component child, final PrintListener listener) {
        this(child, "");
        try {
            addPrintListener(listener);
        } catch (TooManyListenersException e) {
            // Can't happen
            throw new UncheckedWrapperException("Too many listeners: " + e.getMessage(), e);
        }
    }

    /**
     * Constructor.
     *
     * @param listener
     */
    public BaseLink(final PrintListener listener) {
        this("", listener);
    }

    // DEPRECATED constructors
    
    /**
     * Constructor.
     *
     * @param label as text
     * @param url
     * @deprecated use BaseLink(Component,url) instead with a Label using a
     *             GlobalizedMessage instead
     */
    public BaseLink(final String label, final String url) {
        this(new Label(label), url);
    }

    /**
     * Constructor.
     *
     * @param label as text
     * @param listener PrintListener, may be used to change either the Display
     *                 text or the url within a locked page.
     * @deprecated use BaseLink(Component,listener) instead with a Label using
     *             a GlobalizedMessage instead
     */
    public BaseLink(final String label, final PrintListener listener) {
        this(new Label(label), listener);
    }

    // Class Methods
    
    /**
     * Clone. 
     * @return
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        final BaseLink result = (BaseLink) super.clone();
        result.m_printListener = null;
        return result;
    }

    /**
     * Adds a print listener.
     * Since the <code>PrintListener</code> is expected to modify the target
     * of the <code>PrintEvent</code>, only one print listener can be set
     * for a link.
     * 
     * @param listener The print listener. Must not <code>null</code>.
     * @throws IllegalArgumentException if <code>listener</code> is null.
     * @throws TooManyListenersException if a print listener has previously been
     *         added.
     */
    public void addPrintListener(final PrintListener listener)
            throws IllegalStateException, TooManyListenersException {
        if (listener == null) {
            throw new IllegalArgumentException("Argument listener can not be null");
        }
        if (m_printListener != null) {
            throw new TooManyListenersException("Too many listeners. Can only have one");
        }
        m_printListener = listener;
    }

    /**
     * Removes a previously added print listener. If the passed in
     * <code>listener</code> is not the listener that was added with 
     * {@link #addPrintListener addPrintListener}, an IllegalArgumentException 
     * will be thrown.
     *
     * @param listener The listener that was previously added with 
     *                 <code>addPrintListener</code>. 
     *                 Must not be <code>null</code>.
     * @throws IllegalArgumentException if <code>listener</code> is not the 
     *         currently registered  print listener or is <code>null</code>. 
     */
    public void removePrintListener(final PrintListener listener)
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
     * 
     * @param state
     * @return 
     */
    protected BaseLink firePrintEvent(final PageState state) {
        BaseLink l = this;
        if (m_printListener != null) {
            try {
                l = (BaseLink) this.clone();
                m_printListener.prepare(new PrintEvent(this, state, l));
            } catch (CloneNotSupportedException e) {
                l = this;
                throw new UncheckedWrapperException(e);
            }
        }
        return l;
    }

    /**
     * Retrieves the label component used to display the Link. Typically a Label,
     * but may be an other type, e.g. an Image, as well.
     * 
     * @return Component used to display the Link.
     */
    public final Component getChild() {
        return m_child;
    }

    public void setChild(final Component child) {
        Assert.isUnlocked(this);
        m_child = child;
    }

    /**
     * Use a GlobalizedMessage to be used to display the link. It's primary 
     * purpose is to hide the parent class' method to prevent its usage because
     * Labels and GlobalizedMessages are used here differently (a 
     * GlobalizedMessage is here not directly used as a Label by specifying it
     * as an attribugte, inside a Label component).
     * @param message 
     */
    @Override
    public void setLabel(final GlobalizedMessage  message) {
        Assert.isUnlocked(this);
        Label label = new Label(message);
        setChild( (Component)label);

    }

    /**
     * 
     * @return 
     */
    public final String getTarget() {
        return m_url;
    }

    public final void setTarget(final String url) {
        Assert.isUnlocked(this);

        m_url = url;
    }

    /**
     * Sets the type of link this link represents.
     *
     * @param type the type of link
     */
    protected void setTypeAttr(final String type) {
        Assert.isUnlocked(this);
        setAttribute(TYPE_ATTR, type);
    }

    /**
     * 
     * @param state
     * @param parent 
     */
    protected abstract void generateURL(final PageState state, final Element parent);

    /**
     * <p>Generates a DOM fragment:
     * <p><pre>
     * &lt;bebop:link href="..." type="..." %bebopAttr;/>
     * </pre>
     * The <code>href</code> attribute contains the target the link should point
     * to. The <code>type</code> attribute is used to give more fine grained
     * control over which kind of link this element represents. The types are
     * <code>link</code> for a <code>Link</code>, <code>control</code> for a 
     * {@link ControlLink}, and <code>toggle</code> for a {@link ToggleLink}.
     * There may be additional attributes depending on what type of link this 
     * link represents.
     * 
     * @see ControlLink#generateXML
     * @see ToggleLink#generateXML
     * 
     * @param state The current {@link PageState}.
     * @param parent The XML element to attach the XML to.
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            BaseLink target = firePrintEvent(state);
 
            Element link = parent.newChildElement("bebop:link", BEBOP_XML_NS);
 
            target.generateURL(state, link);
            target.exportConfirmAttributes(state, link);            
            //setup the link without javascript
            target.setupNoJavascriptURL(state, link);
            target.exportAttributes(link);
            target.generateExtraXMLAttributes(state, link);
            target.generateDescriptionXML(state, link);
            target.getChild().generateXML(state, link);
        }
    }

    /**
     * 
     * @param state
     * @param sUrl
     * @return 
     */
    private String getAbsoluteUrl(final PageState state, final String sUrl) {
        String sReturn = "";

        if ((sUrl.indexOf(":") != -1) || sUrl.indexOf("/") == 0) {
            //if sUrl contains a ":" or begins with a "/", then it is an absolute URL
            sReturn = sUrl;
        } else {
            //otherwise, it is a relative URL, so we need to make it an absolute URL

            //get the current URL
            String sThisURL = "";
            try {
                sThisURL = state.stateAsURL();
            } catch (java.io.IOException ioe) {
                //ignore
            }
            //trim the current URL back to the last "/" character
            int iIndex = sThisURL.lastIndexOf("/");

            //if there is no "/" character, then assume we are at server root
            if (iIndex == -1) {
                sReturn = "/" + sUrl;
            } else {
                sReturn = sThisURL.substring(0, iIndex) + "/" + sUrl;
            }
        }

        return sReturn;
    }

    /**
     * Sets up no-JavaScript fallback HTML
     *
     * @param state The current {@link PageState}. 
     * @param link The link element.
     */
    protected void setupNoJavascriptURL(final PageState state, final Element link) {
        String sURL = null;

        if (m_sConfirmMsg.length() > 0
                || (m_confirmMsg != null && m_confirmMsg.localize().toString().length() > 0)) {

            //if we want the confirm link, create the link
            String sOkUrl = getAbsoluteUrl(state, link.getAttribute(HREF));
            String sCancelUrl = null;
            try {
                sCancelUrl = state.stateAsURL();
            } catch (java.io.IOException e) {
                Assert.fail("Could not get current page state as URL");
            }

            if (m_sConfirmMsg.length() > 0) {
                sURL = ConfirmPage.getConfirmUrl(m_sConfirmMsg, sOkUrl, sCancelUrl);
            } else if (m_confirmMsg != null) {
                sURL = ConfirmPage.getConfirmUrl(m_confirmMsg.localize().toString(), sOkUrl, sCancelUrl);
            }

        } else {
            //don't want confirm link--just no javascript link
            if (m_noJavascriptURL == null) {
                //get the generatedURL and make it the default noJavaScript link
                sURL = link.getAttribute(HREF);
            } else {
                sURL = m_noJavascriptURL;
            }
        }
        link.addAttribute(HREF_NO_JAVASCRIPT, sURL);
        exportAttributes(link);
    }

    /**
     * Adds type-specific XML attributes to the XML element representing
     * this link. Subclasses should override this method if they introduce
     * more attributes than the ones {@link #generateXML generateXML}
     * produces by default.
     *
     * @param state The current request
     * @param link The XML element representing this link
     */
    protected void generateExtraXMLAttributes(final PageState state, final Element link) {
    }

    /**
     * Sets onClick event and <em>disables the javascript-based double-click
     * protection for this link</em>. Not for confirmation messages; Should call
     * setConfirmation for that.
     *
     * @param value The confirmation link. To not use the value {@code return confirm(} with this
     * method. 
     *
     * @see #setConfirmation
     */
    public void setOnClick(final String value) {
        //should not use this method to set confirmation messages--should
        //use setConfirmation() instead, or else the javascript will break
        if (value != null) {
            Assert.isTrue(!value.toLowerCase().startsWith("return confirm("),
                    "Do not use setOnClick() to set confirmation messages. "
                    + "Use setCofirmation() instead.");
        }

        setAttribute(ON_CLICK, value);
    }

    /**
     * Forces the user to click through a confirmation dialog before this link
     * is followed. The user is prompted with the specified message. If the
     * user does not does not confirm, the link is not followed. The current
     * implementation uses the JavaScript confirm function and the onClick
     * attribute.
     * If JavaScript is not enabled in the client browser, this method will
     * redirect the browser to a Bebop confirmation page rather than use
     * a JavaScript confirmation.
     * Subsequent calls to setOnClick will undo the effect of this method.
     *
     * @param message the confirmation message presented to the user. This
     * message cannot have an apostrophe or back slash.
     * @deprecated Use setConfirmation(final GlobalizedMessage msg) instead
     */
    public void setConfirmation(final String message) {
        //make sure that the message doesn't have any apostrophe's
        //or back slashes

        if (Assert.isEnabled()) {
            final boolean isGoodMessage = message.indexOf("'") == -1 && message.indexOf("\\") == -1;
            Assert.isTrue(isGoodMessage,
                    "confirmation message cannot contain apostrophe or back slash");
        }

        m_sConfirmMsg = message;
    }

    /**
     * Set a GlobalizedMessage as confirmation message
     * @param msg 
     */
    public void setConfirmation(final GlobalizedMessage msg) {
        m_confirmMsg = msg;
    }

    /**
     * Generate XML output for confirmation links
     * 
     * @param state PageState
     * @param link Parent element
     */
    private void exportConfirmAttributes(final PageState state, final Element link) {

        // If a confirmation message is set
        if (m_sConfirmMsg.length() > 0 || m_confirmMsg != null) {

            // then add the needed attributes to link
            link.addAttribute("confirm", "confirm");

            // If m_sConfirmMsg is not empty
            if (m_sConfirmMsg.length() > 0) {

                // then set the onclick attribute for the link with the static message
                link.addAttribute(ON_CLICK, "return confirm(\\'" + m_sConfirmMsg + "\\');");

            // else if m_configMsg is set
            } else if (m_confirmMsg != null) {

                //then set the onclick attribute for the link with a globalized message
                link.addAttribute(ON_CLICK, "return confirm(\\'" + m_confirmMsg.localize() + "\\');");

            }
        }
    }

    public final void setNoJavascriptTarget(final String sURL) {
        Assert.isUnlocked(this);
        m_noJavascriptURL = sURL;
    }

    public final String getNoJavascriptTarget() {
        return m_noJavascriptURL;
    }
}
