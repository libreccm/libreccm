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

import java.util.Collections;
import java.util.Iterator;

import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * A simple implementation of the Component interface.
 *
 *
 * @author David Lutterkort
 * @author Stanislav Freidin
 * @author Rory Solomon
 * @author Uday Mathur
 *
 * @version $Id: SimpleComponent.java 1498 2007-03-19 16:22:15Z apevec $
 */
public class SimpleComponent extends Completable
                             implements Component, Cloneable {

    private boolean m_locked;

    /**
     * The Attribute object is protected to make it easier for the Form Builder 
     * service to persist the SimpleComponent.
     * Locking violation is not a problem since if the SimpleComponent is locked
     * then the Attribute object will also be locked.
     */
    protected Attributes m_attr;

    private String m_key = null;        // name mangling key

    /**
     * Clones a component. The clone is not locked and has its own set of
     * attributes.
     * @return the clone of a component.
     * @throws java.lang.CloneNotSupportedException
     * @post ! ((SimpleComponent) return).isLocked()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        SimpleComponent result = (SimpleComponent) super.clone();
        if ( m_attr != null ) {
            result.m_attr = (Attributes) m_attr.clone();
        }
        result.m_locked = false;
        return result;
    }

    /**
     * Registers state parameters for the page with its model. Documentation
     * from Interface Componment:
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
     * @pre p != null 
     * @param p 
     */
    @Override
    public void register(Page p) {
        return;
    }

    /**
     * Registers form parameters with the form model for this form.
     * This method is only important for {@link FormSection form sections}
     * and {@link com.arsdigita.bebop.form.Widget widgets} (components that
     * have a connection to an HTML form). Other components can implement it
     * as a no-op.
     *
     * @param f
     * @param m
     * @pre f != null
     * @pre m != null 
     */
    @Override
    public void register(Form f, FormModel m) {
        return;
    }

    /**
     * Does processing that is special to the component
     * receiving the click.
     * 
     * @param state the current page state
     * @throws javax.servlet.ServletException
     */
    @Override
    public void respond(PageState state)
        throws javax.servlet.ServletException { }

    @Override
    public Iterator children() {
        return Collections.EMPTY_LIST.iterator();
    }

    /** Adds [J]DOM nodes for this component.  Specifically for
     *  base class SimpleComponent, does nothing.
     * @param p
     */
    @Override
    public void generateXML(PageState state, Element p) {
        return;
    }

    @Override
    public final boolean isLocked() {
        return m_locked;
    }

    @Override
    public void lock () {
        if (m_attr != null) {
            m_attr.lock();
        }
        m_locked = true;
    }

    /**
     * Unlocks this component.  Package visibility is intentional; the
     * only time a component should be unlocked is when it's pooled and
     * gets locked because it's put into a page.  It needs to be unlocked
     * when the instance is recycled.
     */
    void unlock() {
        m_locked = false;

    }

    /* Working with standard component attributes */

    /**
     * Gets the class attribute.
     * @return the class attribute.
     */
    @Override
    public String getClassAttr() {
        return getAttribute(CLASS);
    }

    /**
     * Sets the class attribute.
     * @param theClass a valid <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name">XML name</a>
     */
    @Override
    public void setClassAttr(String theClass) {
        Assert.isUnlocked(this);
        setAttribute(CLASS, theClass);
    }

    /**
     * Gets the style attribute.
     * @return the style attribute.
     */
    @Override
    public String getStyleAttr() {
        return getAttribute(STYLE);
    }

    /**
     * Sets the style attribute. <code>style</code> should be a valid CSS
     * style, since its value will be copied verbatim to the output and
     * appear as a <tt>style</tt> attribute in the top level XML or HTML
     * output element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="#standard">Standard Attributes</a>
     */
    @Override
    public void setStyleAttr(String style) {
        Assert.isUnlocked(this);
        setAttribute(STYLE, style);
    }

    /**
     * Gets the <tt>id</tt> attribute.
     * @return the <tt>id</tt> attribute.
     * @see #setIdAttr(String id)
     */
    @Override
    public String getIdAttr() {
        return getAttribute(ID);
    }

    /**
     * Sets the <tt>id</tt> attribute. <code>id</code>
     * should be an <a
     * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Name">XML name</a>
     * that is unique within the {@link Page Page} in which this component is
     * contained. The value of <code>id</code> is copied literally to the
     * output and not used for internal processing.
     *
     * @param id a valid XML identifier
     * @see <a href="#standard">Standard Attributes</a>
     */
    @Override
    public void setIdAttr(String id) {
        Assert.isUnlocked(this);
        setAttribute(ID, id);
    }

    /* Methods for attribute management */

    /**
     * Sets an attribute. Overwrites any old values. These values are used to
     * generate attributes for the top level XML or HTML element that is
     * output from this component with {@link #generateXML generateXML}.
     *
     * @pre name != null
     * @post getAttribute(name) == value
     *
     * @param name attribute name, case insensitive
     * @param value new attribute value
     */
    final protected void setAttribute(String name, String value) {
        Assert.isUnlocked(this);
        if (m_attr == null) {
            m_attr = new Attributes();
        }
        m_attr.setAttribute(name, value);
    }

    /**
     * Gets the value of an attribute.
     *
     * @pre name != null
     *
     * @param name attribute name, case insensitive
     * @return the string value previously set with {@link #setAttribute
     *   setAttribute}, or <code>null</code> if none was set.
     * @see #setAttribute
     */
    final protected String getAttribute(String name) {
        return (m_attr == null) ? null : m_attr.getAttribute(name);
    }

    /**
     * Adds the attributes set with {@link #setAttribute setAttribute} to the
     * element <code>target</code>. The attributes set with
     * <code>exportAttributes</code> overwrite attributes with identical names
     * that <code>target</code> might already have.
     *
     * @pre target != null
     *
     * @param target element to which attributes are added
     * @see #setAttribute
     */
    final protected void exportAttributes(com.arsdigita.xml.Element target) {
        if (m_attr != null) {
            m_attr.exportAttributes(target);
        }
        if (KernelConfig.getConfig().isDebugEnabled() ||
                BebopConfig.getConfig().getShowClassName()) {
            target.addAttribute("bebop:classname", getClass().getName(),
                                BEBOP_XML_NS);
        }
    }

    /**
     * Returns <code>true</code> if any attributes have been set.
     * @return <code>true</code> if any attributes have been set;
     * <code>false</code> otherwise.
     */
    final protected boolean hasAttributes() {
        return m_attr != null;
    }

    /*
     * Set an arbitrary meta data attribute on the component.
     * The name of the attribute in the XML will be prefixed
     * with the string 'metadata.'
     */
    final public void setMetaDataAttribute(String name, String value) {
        setAttribute("metadata." + name, value);
    }

    final public String getMetaDataAttribute(String name) {
        return getAttribute("metadata." + name);
    }

    /**
     * Supplies a key for parameter name mangling.
     * 
     * @param key the key to mangle
     * @return 
     */
    @Override
    public Component setKey(String key) {
        Assert.isUnlocked(this);
        if (key.charAt(0) >= 0 && key.charAt(0) <= 9) {
            throw new IllegalArgumentException("key \"" + key + "\" must not start with a digit.");
        }
        m_key = key;
        return this;
    }

    /**
     * Retrieves a key for parameter name mangling.
     * @return a key for parameter name mangling.
     */
    @Override
    public final String getKey() {
        return m_key;
    }

    @Override
    public boolean isVisible(PageState s) {
        return s.isVisible(this);
    }

    @Override
    public void setVisible(PageState s, boolean  v) {
        s.setVisible(this, v);
    }

}
