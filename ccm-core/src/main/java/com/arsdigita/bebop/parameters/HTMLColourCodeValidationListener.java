/*
 * Copyright (C) 2008 Permeance Technologies Ptd Ltd. All Rights Reserved.
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
 */

package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;

/**
 * Validates a value is a valid HTML hex code for a colour.
 * 
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class HTMLColourCodeValidationListener implements ParameterListener
{
    public void validate(ParameterEvent e) throws FormProcessException
    {
        ParameterData data = e.getParameterData();
        String value = (String) data.getValue();

        if (value != null && !value.toLowerCase().matches("#[0-9a-f]{3}")
                && !value.toLowerCase().matches("#[0-9a-f]{6}"))
        {
            data.addError("Invalid HTML colour code. Must match #xxx or #xxxxxx.");
        }
    }
}
