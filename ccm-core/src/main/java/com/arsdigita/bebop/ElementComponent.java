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

import com.arsdigita.xml.Element;

/**
 * A component that gets its text entirely from a single XSL element.
 *
 * @author Sameer Ajmani
 * @version $Id$
 **/
public class ElementComponent extends SimpleComponent {

    private String m_name;
    private String m_uri;

    /**
     * Constructs an ElementComponent that uses the element with the given
     * name under the given XSL namespace URI.
     *
     * @param name the name of the element to use
     * @param uri the URI of the XSL namespace
     **/
    public ElementComponent(String name, String uri) {
        m_name = name;
        m_uri = uri;
    }

    /**
     * Constructs a new element with the name and namespace given in this
     * component's constructor, and adds the element to the parent element.
     * @param state the current page state
     * @param parent the parent element for this new element
     **/
    public void generateXML(PageState state, Element parent) {
        parent.newChildElement(m_name, m_uri);
    }
}
