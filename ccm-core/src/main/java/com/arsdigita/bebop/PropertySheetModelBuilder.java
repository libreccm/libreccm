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

import com.arsdigita.bebop.PageState;
import com.arsdigita.util.Lockable;

/**
 * Constructs a new {@link PropertySheetModel} for the {@link PropertySheet}.
 * The model will be used to get a list of properties at runtime.
 *
 * @author Stanislav Freidin 
 * @version $Id: PropertySheetModelBuilder.java 287 2005-02-22 00:29:02Z sskracic $
 * @see PropertySheetModel
 */

public interface PropertySheetModelBuilder extends Lockable  {


    /**
     * Constructs a new {@link PropertySheetModel}.
     *
     * @param sheet the {@link PropertySheet}
     * @param state the page state
     * @return a {@link PropertySheetModel}.
     */
    PropertySheetModel makeModel(PropertySheet sheet, PageState state);

}
