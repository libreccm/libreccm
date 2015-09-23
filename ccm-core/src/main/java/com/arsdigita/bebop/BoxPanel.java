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


import com.arsdigita.xml.Element;

import com.arsdigita.bebop.form.Hidden;

// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.util.PanelConstraints;

/**
 * A container that prints its components in one row, either horizontally or
 * vertically.
 *
 * @author David Lutterkort 
 * @version $Id: BoxPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BoxPanel extends SimpleContainer 
                      implements BebopConstants, PanelConstraints {

    /** Specifies that components should be laid out left to right.           */
    public final static int HORIZONTAL = 1;

    /** Specifies that components should be laid out top to bottom.           */
    public final static int VERTICAL = 2;
    
    /** XML attribute for width                                               */
    private static final String WIDTH_ATTR = "width";
    
    /** XML attribute wether to draw a border.                                */
    private static final String BORDER_ATTR = "border";
    
    /** Property to whether to draw a HORIZONTAL or VERTICAL box panel.       */
    private int m_axis;
    
    /** Property to store whether to to center alignment.                     */
    private boolean m_centering;


    /**
     * Constructor, creates a box panel that lays out its components from 
     * top to bottom. The components are not centered.
     */
    public BoxPanel() {
        this(VERTICAL);
    }

    /**
     * Constructor, creates a box panel that lays out its components in the given
     * direction. The components are not centered.
     *
     * @param axis the axis to use to lay out the components
     */
    public BoxPanel(int axis) {
        this(axis, false);
    }

    /**
     * Creates a box panel that lays out its components in the given
     * direction and centers them if that is specified.
     *
     * @param axis the axis to use to lay out the components
     * @param centering <code>true</code> if the layout should be centered
     */
    public BoxPanel(int axis, boolean centering) {
        m_axis = axis;
        m_centering = centering;
    }

    // Instance methods

    /**
     * Sets the width attribute of the box panel. The given width should be in
     * a form that is legal as the <code>width</code> attribute of an HTML
     * <code>table</code> element.
     *
     * @param w the width of the box panel
     */
    public void setWidth(String w) {
        setAttribute(WIDTH_ATTR, w);
    }

//  /**
//   * Sets whether a border should be drawn.
//   *
//   * @param isBorder <code>true</code> if a border should be drawn
//   * @deprecated Use {@link #setBorder(int border)} instead.
//   */
//  public void setBorder(boolean isBorder) {
//      if (isBorder) {
//          setAttribute(BORDER_ATTR, "1");
//      } else {
//          setAttribute(BORDER_ATTR, "0");
//      }
//  }

    /**
     * 
     * Sets the width of the border to draw around the components. This value
     * will be used for the <code>border</code> attribute in an HTML
     * <code>table</code> element.
     *
     * @param border the width of the border
     */
    public void setBorder(int border) {
        setAttribute(BORDER_ATTR, String.valueOf(border));
    }

    /**
     * Adds nodes for the panel and its child components to be rendered,
     * usually in a table. Any hidden widgets directly contained in the box
     * panel are added directly to <code>parent</code> and are not in any
     * of the cells that the box panel generates.
     *
     * <p>Generates DOM fragment:
     * <p><code>&lt;bebop:boxPanel [width=...] border=... center... axis...>
     *   &lt;bebop:cell> cell contents &lt;/bebop:cell>
     * &lt;/bebop:boxPanel></code>
     * 
     * @param parent
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        if (isVisible(state)) {
            Element panel = parent.newChildElement(BEBOP_BOXPANEL, BEBOP_XML_NS);
            // or: rowPanel/columPanel?
            panel.addAttribute("center", String.valueOf(m_centering));
            panel.addAttribute("axis", String.valueOf(m_axis));
            exportAttributes(panel);

            for (Iterator i = children(); i.hasNext();) {
                Component c = (Component) i.next();

                if (c.isVisible(state)) {
                    if (c instanceof Hidden) {
                        c.generateXML(state, parent);
                    } else {
                        Element cell = panel.newChildElement(BEBOP_CELL, BEBOP_XML_NS);
                        c.generateXML(state, cell);
                    }
                }
            }
        }
    }

}
