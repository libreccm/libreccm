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

import java.util.Iterator;

import com.arsdigita.util.Lockable;
import com.arsdigita.xml.Element;

/**
 * The common interface implemented by all Bebop components. 
 * 
 * During its lifetime, a component receives the following calls 
 * from the containing page.
 *
 * <ul>
 *   <li> During initialization/creation of the containing page:
 *     <ul>
 *       <li> {@link #register(Page)} is called to register
 *           state parameters that need to be preserved between requests to
 *           the same page.
 *       </li>
 *       <li> {@link #register(Form, FormModel)} is called if
 *           the component is contained
 *           in a {@link Form} or {@link FormSection}. Typically, only form
 *           widgets like text controls have parameters that need to be
 *           registered with the <code>FormSection</code>.
 *       </li>
 *       <li> {@link Lockable#lock lock} is called to lock the component and
 *           inform it that no further structural modifications will be
 *           made.
 *       </li>
 *    </ul>
 *  </li>
 *  <li> For each request made to the containing page:
 *    <ul>
 *       <li> If the request originated from
 *          this component, {@link #respond(PageState) respond} is called.
 *       </li>
 * <li> To produce output, {@link #generateXML(PageState, Element)  generateXML} is
 *            called to produce XML output that will be transformed by the
 *            templating system.
 *       </li>
 *     </ul>
 *  </li>
 * </ul>
 *
 *
 * <h4><a name="visibility">Visibility</a></h4>
 *
 * <p>A component can be either <i>visible</i> or
 * <i>invisible</i>. Invisible components do not produce any output and
 * containers should be careful to completely hide their presence. The
 * visibility of a component can be influenced in a number of ways:
 * <UL>
 * <LI>When a
 * component is first added to the hierarchy of a {@link Page}, it is
 * visible.</LI>
 * <LI> A component's default (request-independent) visibility can be changed
 * with a call to {@link Page#setVisibleDefault
 * Page.setVisibleDefault} during setup of the page.</LI>
 * <LI>A component
 * can be made visible or invisible during the serving of a request with a
 * call to {@link #setVisible setVisible}.</LI>
 * </UL>
 * <p> The {@link Page} makes sure that the visibility of components is
 * preserved across repeated requests to the same page.
 *
 * <h4><a name="standard">Standard Attributes</a></h4>
 * <p> Each component supports a few standard attributes that are copied
 * through to the output when producing either HTML or XML
 * output. These attributes are not used internally in any way, and setting
 * them is entirely optional.
 *
 * <p> The standard attributes appear in the output as attributes in
 * the element generated from this component. They correspond directly to
 * properties with setters and getters. The standard attributes are as folows.
 * <center><table cellspacing=5 cellpadding=2 border=0>
 *    <tr>
 *      <th>Attribute</th> <th>Java property</th> <th>Purpose</th>
 *    </tr>
 *    <tr>
 *      <td valign='top'><tt>id</tt></td>
 *      <td valign='top'>
 *         <tt>{@link #getIdAttr getIdAttr}/{@link #setIdAttr setIdAttr}</tt>
 *      </td>
 *      <td>Use to uniquely identify a component within the page. Uniqueness is
 * not enforced. The <tt>id</tt> attribute allows stylesheet designers to
 * access individual components.</td>
 *    </tr>
 *    <tr>
 *      <td valign='top'><tt>class</tt></td>
 *      <td valign='top'><tt>{@link #getClassAttr()}/{@link
 *         #setClassAttr(String)}</tt></td>
 *
 *      <td>Use as the generic name for a
 *      component. For example, if you have a UserInfoDisplay
 *      component, the class attribute can be used to name the
 *      component "UserInfoDisplay" so that a generic template rule
 *      can style all UserInfoDisplay components.</td>
 *
 *    </tr>
 *    <tr>
 *      <td valign='top'><tt>style</tt></td>
 *      <td valign='top'><tt>{@link #getStyleAttr getStyleAttr}/{@link
 *        #setStyleAttr setStyleAttr}</tt></td>
 *      <td>Use to add CSS style to an individual element.</td>
 *    </tr>
 *
 * </table></center>
 *
 * <h4><a name="caveat">Caveat: Race Conditions</a></h4>
 * <p>
 *   When extending any <code>Component</code>, honor the
 *   <em>Lockable</em> contract indicated by <code>extends {@link
 *   com.arsdigita.util.Lockable}</code>.  Beware that member variables
 *   are not inherently threadsafe, because you may be circumventing
 *   the contract. For variables that might be different for each
 *   request, use {@link RequestLocal}.  If you must add member
 *   variables in the derived class, as a minimum be sure to safeguard
 *   any write access to instance variables with {@link
 *   com.arsdigita.util.Assert#assertNotLocked}.
 * </p>
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @author Rory Solomon 
 *
 * @version $Id$
 */
public interface Component extends Lockable {

    /**
     * The XML namespace used by all the Bebop components.
     */
    String BEBOP_XML_NS =
        "http://www.arsdigita.com/bebop/1.0";

    /**
     * The name for the class attribute.
     * @see #setClassAttr(String)
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    String CLASS = "class";

    /**
     * The name for the style attribute.
     * @see #setStyleAttr(String)
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    String STYLE = "style";

    /**
     * The name for the ID attribute.
     * @see #setIdAttr
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    String ID = "id";

    // HTML 4 event names

    /**
     * The onClick event.
     */
    String ON_CLICK = "onclick";

    /**
     * <p>Adds a DOM subtree representing this component under the given
     * parent node.  Uses the request values stored in <code>state</code>.</p>
     *
     * @param state represents the current request
     * @param parent the node under which the DOM subtree should be added
     *
     * @pre state  != null
     * @pre parent != null
     */
    void generateXML(PageState state, Element parent);


    /**
     * <p>Responds to the request. This method is only called if the request
     * was made from a link or form that the component put on the page in the
     * {@link PageState#stateAsURL} previous request.</p>
     *
     * <p>No output should be generated on the HTTP response. The component
     * can store intermediate results in the <code>state</code> by calling
     * {@link PageState#setAttribute setAttribute}.</p>
     *
     * <p> This method is called before any output is printed to the HTTP
     * response so that the component can forward to a different page and
     * thereby commit the response.</p>
     *
     * @param state represents the current request
     *
     * @pre state != null
     */
    void respond(PageState state)
        throws javax.servlet.ServletException;

    /**
     * Returns an iterator over the children of this component. If the
     * component has no children, returns an empty (not
     * <code>null</code>) iterator.
     *
     * @return an iterator over the children of this component.
     *
     * @post return != null
     */
    Iterator children();

    /**
     * Registers state parameters for the page with its model.
     *
     * A simple component with a state parameter <code>param</code> would do
     * the following in the body of this method:
     * <pre>
     *   p.addComponent(this);
     *   p.addComponentStateParam(this, param);
     * </pre>
     *
     * You should override this method to set the default visibility
     * of your component:
     *
     * <pre>
     * public void register(Page p) {
     *     super.register(p);
     *     p.setVisibleDefault(childNotInitiallyShown,false);
     *     p.setVisibleDefault(anotherChild, false);
     * }
     * </pre>
     *
     * Always call <code>super.register</code> when you override
     * <code>register</code>.  Otherwise your component may
     * malfunction and produce errors like "Widget ... isn't
     * associated with any Form"
     *
     * @param p
     * @pre p != null 
     */
    void register(Page p);

    /**
     * Registers form parameters with the form model for this
     * form. This method is only important for {@link FormSection form
     * sections} and {@link com.arsdigita.bebop.form.Widget widgets}
     * (components that have a connection to an HTML form). Other
     * components can implement it as a no-op.
     *
     * @param f
     * @param m
     * @pre f != null
     * @pre m != null 
     */
    void register(Form f, FormModel m);

    /* Properties that will get copied straight to the output,
       both in HTML and in XML
    */

    /**
     * Gets the class attribute.
     *
     * @return the class attribute.
     *
     * @see #setClassAttr(String)
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    String getClassAttr();

    /**
     * Sets the class attribute.
     * @param theClass a valid <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name">XML name</a>
     * @see <a href="Component#standard">Standard Attributes</a>
     * @see #getClassAttr
     */
    void setClassAttr(String theClass);

    /**
     * Gets the style attribute.
     *
     * @return the style attribute.
     *
     * @see #setStyleAttr
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    String getStyleAttr();

    /**
     * Sets the style attribute. <code>style</code> should be a valid CSS
     * style, because its value will be copied verbatim to the output and
     * appear as a <tt>style</tt> attribute in the top level XML or HTML
     * output element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    void setStyleAttr(String style);

    /**
     * Gets the <tt>id</tt> attribute.
     *
     * @return the id attribute.
     *
     * @see <a href="Component#standard">Standard Attributes</a>
     * @see #setIdAttr(String id)
     */
    String getIdAttr();

    /**
     * Sets the <tt>id</tt> attribute. <code>id</code>
     * should be an <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name">XML name</a>
     * that is unique within the {@link Page Page} in which this component is
     * contained. The value of <code>id</code> is copied literally to the
     * output and not used for internal processing.
     *
     * @param id a valid XML identifier
     * @see <a href="Component#standard">Standard Attributes</a>
     */
    void setIdAttr(String id);

    /**
     * Supplies a key for making parameter names unique.  To be used
     * instead of the component's index (see <a
     * href="PageModel#componentPrefix">Component Prefix</a>).
     * To avoid collision with indexOf, it
     * should (1) be a legal fragment of a cgi parameter, (2) differ from "g",
     * and (3) not start with a digit.
     * 
     * @param key
     * @return 
     */
    Component setKey(String key);

    /**
     * Retrieves the programmer-supplied key.  Normally, there is no
     * such key and the method returns null.
     *
     * @return the programmer-supplied key.
     */
    String getKey();

    /**
     * Determines whether the component is visible in the request
     * represented by <code>state</code>.
     * @see #setVisible setVisible
     * @see <a href="Component.html#visibility">Description of Visibility
     * above</a>
     *
     *
     * @param state represents the current request
     * @return <code>true</code> if the component is visible; <code>false</code>
     *         otherwise.
     * @pre state  != null
     */
    boolean isVisible(PageState state);

    /**
     * Changes the visibility of the component. The component will keep the
     * visibility that is set with this method in subsequent requests to this page.
     *
     * @param state represents the current request
     * @param v <code>true</code> if the component should be visible
     * @pre state  != null
     * @see <a href="Component.html#visibility">Description of Visibility
     * above</a>
     */
    void setVisible(PageState state, boolean  v);

}
