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
package com.arsdigita.bebop.util;

import com.arsdigita.util.Assert;
import com.arsdigita.util.Lockable;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;

/**
 * This class represents a set of key-value pairs, for use in
 * extending the XML attributes of Bebop components.
 *
 * @version $Id: Attributes.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Attributes implements Lockable, Cloneable {

    /**
     * Map of attributes.
     */
    private HashMap m_attributes;

    private boolean m_locked;

    /**
     * Creates an Attributes object.
     */
    public Attributes() {
        m_attributes = new HashMap();
        m_locked = false;
    }

    /**
     * Clone the attributes. The clone is not locked and has its own set of
     * attributes and values.
     * @post ! ((Attributes) return).isLocked()
     */
    public Object clone() throws CloneNotSupportedException {
        Attributes result = (Attributes) super.clone();
        result.m_attributes = (HashMap) m_attributes.clone();
        result.m_locked = false;
        return result;
    }

    /**
     * <p>Sets an arbitrary attribute for inclusion in the HTML tags that
     * compose element.  For standard attributes in the HTML 4.0
     * specification, use of this method has the same effect as the
     * specific mutator method provided for each attribute.</p>
     *
     * <p>Setting an attribute <code>name</code> to <code>null</code>
     * removes it.</p>
     *
     * @param name The name of the attribute
     * @param value The value to assign the named attribute
     */
    public void setAttribute(String name, String value) {
        Assert.isUnlocked(this);
        name = name.toLowerCase();
        m_attributes.put(name, value);
    }

    /**
     * Return the value of an attribute.
     *
     * @pre name != null
     *
     * @param name the name of the attribute
     * @return the value set previously with
     * {@link #setAttribute setAttribute}
     */
    public String getAttribute(String name) {
        return (String) m_attributes.get(name.toLowerCase());
    }

    /**
     *  Return a collection of all of the attribute keys represented.
     *  This, along with {@link #getAttribute(String name)} allows
     *  you to iterate through all of the attributes.  All elements
     *  of the Collection are Strings
     */
    public Collection getAttributeKeys() {
        return m_attributes.keySet();
    }


    /**
     * Copy all attributes into the given DOM Element.  This will
     * override any preexisting Element attributes of the same names.
     */
    public void exportAttributes(com.arsdigita.xml.Element target) {
        Iterator attributesIterator = m_attributes.entrySet().iterator();

        while (attributesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) attributesIterator.next();

            if (entry.getValue() != null) {
                target.addAttribute((String) entry.getKey(),
                                    (String) entry.getValue());
            }
        }
    }

    public void lock() {
        m_locked = true;
    }

    public final boolean isLocked() {
        return m_locked;
    }
}
