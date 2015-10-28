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

import java.util.Iterator;


/**
 * Delimited dimensional navbar.
 *
 * @author Michael Pih 
 * @version $Revision$ $Date$
 * @version $Id$
 */
public class DimensionalNavbar extends SimpleContainer {

    private final static String BEBOP_XML_NS = "http://www.arsdigita.com/bebop/1.0";

    public final static String LEFT   = "left";
    public final static String RIGHT  = "right";
    public final static String CENTER = "center";

    private String m_startTag;
    private String m_endTag;
    private String m_delimiter;
    private String m_align;

    public DimensionalNavbar() {
        super();
        setAlign(RIGHT);
    }

    public void setStartTag(String s) {
        m_startTag = s;
    }

    public void setEndTag(String s) {
        m_endTag = s;
    }

    public void setDelimiter(String s) {
        m_delimiter = s;
    }

    public void setAlign(String s) {
        if ( s.equals(LEFT) || s.equals(RIGHT) || s.equals(CENTER) ) {
            m_align = s;
        } else {
            throw new IllegalArgumentException
                ("Align must be DimensionalNavbar.RIGHT, " +
                 "DimensionalNavbar.LEFT, or DimensionalNavbar.CENTER");
        }
    }


    public void generateXML(PageState state, Element parent) {

        Element navbar = parent.newChildElement("bebop:dimensionalNavbar",
                                                BEBOP_XML_NS);
        navbar.addAttribute("startTag", m_startTag);
        navbar.addAttribute("endTag", m_endTag);
        navbar.addAttribute("delimiter", m_delimiter);
        navbar.addAttribute("align", m_align);
        exportAttributes(navbar);

        Iterator children = children();
        Component child;
        while ( children.hasNext() ) {
            child = (Component) children.next();

            child.generateXML(state, navbar);
        }
    }
}
