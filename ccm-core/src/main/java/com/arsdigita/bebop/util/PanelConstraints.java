/*
 * Copyright (C) 2014 Peter Boy, University of Bremen. All Rights Reserved.
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
 * An interface that contains positional constraints used by the panel classes
 * (BoxPanel, ColumnPanel and GridPanel) in generating the output XML.
 * 
 * Used by some other classes as well which have to position box elements (e.g.
 * c.ad.bebop.form.ImageSubmit)
 *
 * @author Peter Boy (pb@zes.uni-bremen.de)
 * @version $Id: PanelConstraints.java 1224 2014-06-18 22:28:30Z  $
 */
public interface PanelConstraints {

    /**
     * Left-align a component.
     */
    public static final int LEFT = 1 ; // << 0;

    /**
     * Center a component.
     */
    public static final int CENTER = 1 << 1;

    /**
     * Right-align a component.
     */
    public static final int RIGHT = 1 << 2;

    /**
     * Align the top of a component.
     */
    public static final int TOP = 1 << 3;

    /**
     * Align the middle of a component.
     */
    public static final int MIDDLE = 1 << 4;

    /**
     * Align the bottom of a component.
     */
    public static final int BOTTOM = 1 << 5;

    /**
     * Lay out a component across the full width of the panel.
     */
    public static final int FULL_WIDTH = 1 << 6;

    /**
     * Insert the child component assuming it is printed in a table with the
     * same number of columns.
     */
    public static final int INSERT = 1 << 7;

    /**
     * Constant for specifying ABSMIDDLE alignment of this image input. See the
     * <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     * W3C HTML 4.01 Specification</a> for a description of this attribute.
     */
    public static final int ABSMIDDLE = 1 << 8;

    /**
     * Constant for specifying ABSBOTTOM alignment of this image input. See the 
     * <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     * W3C HTML 4.01 Specification</a> for a description of this attribute.
     */
    public static final int ABSBOTTOM = 1 << 9;

    /**
     * Constant for specifying ABSBOTTOM alignment of this image input. See the 
     * <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     * W3C HTML 4.01 Specification</a> for a description of this attribute.
     */
    public static final int TEXTTOP = 1 << 10;

    /**
     * Constant for specifying ABSBOTTOM alignment of this image input. See the 
     * <a href="http://www.w3.org/TR/html4/present/graphics.html#alignment">
     * W3C HTML 4.01 Specification</a> for a description of this attribute.
     */
    public static final int BASELINE = 1 << 11;

}
