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

/**
 * <p>A class for Bebop size parameters.</p>
 *
 * <blockquote><pre>
 * private Page buildSomePage() {
 *     Page page = new Page("Some Page");
 *
 *     // Put a 10-pixel margin around the contents of this page.
 *     page.setMargin(new Size(10));
 *
 *     // Or, instead, put a 10% margin around it.
 *     page.setMargin(new Size(10, UNIT_PERCENT));
 *
 *     page.lock();
 *
 *     return page;
 * }
 * </pre></blockquote>
 *
 * @author Justin Ross
 * @author Jim Parsons
 * @author Christian
 * Brechb&uuml;hler
 * @version $Id: Size.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Size {

    private float m_scalar;
    private String m_unitAbbreviation = "";

    /**
     * Constant for describing sizes in pixels.
     */
    public static final int UNIT_PIXEL = 1;

    /**
     * Constant for describing a component in terms of percent size
     * relative to its container.
     */
    public static final int UNIT_PERCENT = 2;

    /**
     * Construct a new Size.  Classes extending Size should call super
     * with the abbreviation of their unit.
     *
     * @param scalar a simple magnitude.  Note that this value may
     * be negative.
     * @param unitAbbreviation an unit abbreviation for use when the
     * size is printed.
     * @pre unitAbbreviation != null
     */
    protected Size(float scalar, String unitAbbreviation) {
        m_scalar = scalar;
        m_unitAbbreviation = unitAbbreviation;
    }

    /**
     * Construct a new Size in pixels.
     *
     * @param numPixels a simple magnitude.  Note that this value may
     * be negative.
     */
    public Size(int numPixels) {
        this((float)(numPixels), "");
    }

    /**
     * Construct a new Size using the type indicated in unitEnum.
     * unitEnum is any of the UNIT_* constants defined in this class.
     *
     * @param scalar a simple magnitude.  Note that this value may be
     * negative.
     * @param unitEnum a unit type.
     */
    public Size(float scalar, int unitEnum) {
        m_scalar = scalar;

        if (unitEnum == UNIT_PIXEL) {
            m_unitAbbreviation = "";
        } else if (unitEnum == UNIT_PERCENT) {
            m_unitAbbreviation = "%";
        } else {
            throw new IllegalArgumentException
                ("Bad argument for unitEnum in Size constructor.");
        }
    }

    /**
     * Return the size as a string.  This string will be used in
     * writing the style attributes of Bebop XML.
     *
     * @return this Size as a string for inclusion in XML.
     * @post return != null */
    public String toString() {
        String sizeAsString = Float.toString(m_scalar) + m_unitAbbreviation;

        return sizeAsString;
    }
}
