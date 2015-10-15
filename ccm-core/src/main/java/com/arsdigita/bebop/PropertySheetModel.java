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

import com.arsdigita.globalization.GlobalizedMessage;

/**
 * An abstraction that the
 * {@link PropertySheet} class uses to display a
 * 2-column table of label-value pairs.
 *
 * @author Stanislav Freidin 
 * @version $Id: PropertySheetModel.java 287 2005-02-22 00:29:02Z sskracic $
 * @see PropertySheetModelBuilder
 */

public interface PropertySheetModel {


    /**
     * Advances to the next property, if possible.
     *
     * @return <code>false</code> if there are no more properties;
     * <code>true</code> otherwise.
     */
    boolean nextRow();

    /**
     * Returns the label for the current property.
     *
     * @return the current label.
     * @deprecated use getGlobalizedLabel() instead 
     */
    String getLabel();

    /**
     *  Returns the GlobalizedMessage for the current property
     *  @return the current GlobalizedMessage
     */
    GlobalizedMessage getGlobalizedLabel();


    /**
     * Returns the string representation of the current property.
     *
     * @return the current value formatted as a string.
     */
    String getValue();

}
