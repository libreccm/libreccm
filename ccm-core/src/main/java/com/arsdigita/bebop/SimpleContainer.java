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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * A basic implementation of the {@link Container} interface which, by default,
 * renders all of its children directly, without wrapping them in any kind of
 * tag.
 *
 * However, the {@link #SimpleContainer(String, String)} constructor and/or the
 * {@link #setTag(String)} method can be used to cause the container to wrap
 * the XML for its children in an arbitrary tag. This functionality is useful
 * for XSL templating.
 *
 * For example, a template rule might be written to arrange the children of this
 * component in paragraphs:
 *
 * <blockquote><pre><code>
 * // Java Code:
 * m_container = new SimpleContainer("cms:foo", CMS_XML_NS);
 *
 * // XSL code:
 * &lt;xsl:template match="cms:foo"&gt;
 *   &lt;xsl:for-each select="*"&gt;
 *     &lt;p&gt;
 *     &lt;xsl:apply-templates select="."/&gt;
 *     &lt;/p&gt;
 *   &lt;/xsl:for-each&gt;
 * &lt;/xsl:template&gt;
 * </code></pre></blockquote>
 *
 * @author David Lutterkort  
 * @author Stanislav Freidin 
 * @author Rory Solomon 
 * @author Uday Mathur 
 *
 * @version $Id: SimpleContainer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class SimpleContainer extends SimpleComponent implements Container {

    private List m_components;
    private String m_tag, m_ns;

    /**
     * Constructs a new, empty <code>SimpleContainer</code>.
     */
    public SimpleContainer() {
        this(null, null);
    }

    /**
     * Constructs a new, empty <code>SimpleContainer</code> that will
     * wrap its children in the specified tag.
     *
     * @param tag the name of the XML element that will be used to wrap the
     *   children of this container
     * @param ns the namespace for the tag
     */
    public SimpleContainer(String tag, String ns) {
        super();
        m_components = new ArrayList();
        m_tag = tag;
        m_ns = ns;
    }

    /**
     * Adds a component to this container.
     *
     * @param pc the component to be added
     */
    public void add(Component pc) {
        Assert.isUnlocked(this);
        m_components.add(pc);
    }

    /**
     * Adds a component to this container.
     *
     * @param pc the component to be added
     * @param constraints this parameter is ignored. Child classes should
     *   override the add method if they wish to provide special handling
     *   of constraints.
     */
    public void add(Component c, int constraints) {
        add(c);
    }

    /**
     * Determines membership.
     * @return <code>true</code> if the specified object is in this container;
     * <code>false</code> otherwise.
     * @param o the object type, typically a component. Type 
     * Object allows slicker code when o comes from any kind of collection.
     */
    public boolean contains(Object o) {
        return m_components.contains(o);
    }

    /**
     * Determines whether the container is empty.
     *
     * @return <code>false</code> if the container has any children;
     * <code>true</code> otherwise.
     */
    public boolean isEmpty() {
        return m_components.isEmpty();
    }

    /**
     *
     * 
     *
     */
    public int indexOf(Component pc) {
        return m_components.indexOf(pc);
    }

    /**
     * Returns the number of children inside this container.
     * @return the number of children inside this container.
     */
    public int size() {
        return m_components.size();
    }

    /**
     *
     * 
     *
     */
    public Component get(int index) {
        return (Component) m_components.get(index);
    }

    /**
     * Returns all the components of this container.
     * @return all the components of this container.
     */
    @Override
    public Iterator children() {
        return m_components.iterator();
    }

    /**
     * Sets the XML tag that will be used to wrap the children of
     * this container.
     *
     * @param tag the XML tag, or null if children will not be wrapped
     *   in any manner.
     */
    protected final void setTag(String tag) {
        Assert.isUnlocked(this);
        m_tag = tag;
    }

    /**
     * Sets the XML namespace for the tag that will be used to wrap
     * the children of this container.
     *
     * @param ns the XML namespace
     */
    protected final void setNamespace(String ns) {
        Assert.isUnlocked(this);
        m_ns = ns;
    }

    /**
     * Retrieves the name of the XML tag that will be used to
     * wrap the child components.
     *
     * @return the name of the XML tag that will be used to
     *   wrap the child components, or null if no tag was specified.
     */
    public final String getTag() {
        return m_tag;
    }

    /**
     * Retrieves the name of the XML namespace for the tag that will be used to
     * wrap the child components.
     *
     * @return the name of the XML namespace for the tag that will be used to
     *   wrap the child components, or null if no namespace was specified.
     */
    public final String getNamespace() {
        return m_ns;
    }

    /**
     * Generates the containing element.  It is added with this
     * component's tag below the specified parent element. If the passed in
     * element is null, the method
     * passes through p.

     * @param p the parent XML element
     * @return the element to which the children will be added.
     */
    protected Element generateParent(Element p) {
        String tag = getTag();
        if (tag == null) {
            return p;
        }
        Element parent = p.newChildElement(tag, getNamespace());
        exportAttributes(parent);
        return parent;
    }

    /**
     * Generates the XML for this container. If the tag property
     * is nonempty, wraps the children in the specified XML tag.
     *
     * @param state represents the current request
     * @param p the parent XML element
     * @see #setTag(String)
     * @see #setNamespace(String)
     */
    public void generateChildrenXML(PageState state, Element p) {
        for (Iterator i = children(); i.hasNext(); ) {
            Component c = (Component) i.next();
            
            // XXX this seems to be a redundant vis check
            if ( c.isVisible(state) ) {
                c.generateXML(state, p);
            }
        }
    }

    /**
     * Generates the XML for this container. If the tag property
     * is nonempty, wraps the children in the specified XML tag.
     *
     * @param state represents the current request
     * @param p the parent XML element
     * @see #setTag(String)
     * @see #setNamespace(String)
     */
    @Override
    public void generateXML(PageState state, Element p) {
        if ( isVisible(state) ) {
            Element parent = generateParent(p);
            generateChildrenXML(state, parent);
        }
    }
}
