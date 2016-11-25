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

/**
 * Provides properties for the {@link PropertyEditor} during
 * each request. This class is intended for advanced users only.
 *
 * @author Stanislav Freidin 
 * @version $Id: PropertyEditorModel.java 287 2005-02-22 00:29:02Z sskracic $
 * @see PropertyEditorModelBuilder
 */
public interface PropertyEditorModel {


    /**
     * Advances to the next property, if possible.
     *
     * @return <code>false</code> if there are no more properties;
     * <code>true</code> otherwise.
     */
    boolean next();

    /**
     * Returns the component that should act as a "button" for editing the
     * property. Typically, this method returns a {@link ControlLink}
     * of some sort. When the link is clicked, the {@link PropertyEditor}
     * will display the pane for editing the property.
     *
     * @return a component (usually a {@link ControlLink}) that will act
     *   as the "button" for editing the property.
     */
    Component getComponent();

    /**
     * Returns the unique key of the current property, usually
     * a simple String.
     *
     * @return the key of the current property.
     */
    Object getKey();
}
