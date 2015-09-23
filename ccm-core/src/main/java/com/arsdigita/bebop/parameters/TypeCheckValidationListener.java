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
package com.arsdigita.bebop.parameters;

import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 *     Verifies that the
 *    parameter's type is the expected type
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Stas Freidin 
 *    @author Rory Solomon
 * @version $Id$ 
 */
public class TypeCheckValidationListener extends GlobalizedParameterListener {

    private Class m_type;

    public TypeCheckValidationListener(Class type) {
        this.m_type = type;
    }

    public TypeCheckValidationListener(Class type, GlobalizedMessage error) {
        this.m_type = type;
        setError(error);
    }

    public void validate (ParameterEvent e) {

        ParameterData data = e.getParameterData();
        Object obj = data.getValue();

        if (obj == null && data.isTransformed()) {
            return;
        }

        if (getError() == null) {
            setError(new GlobalizedMessage(
                                           "type_check",
                                           getBundleBaseName(),
                                           new Object[] {
                                               data.getName(),
                                               m_type.getName(),
                                               obj.toString(),
                                               obj.getClass().getName()
                                           }
                                           ));
        }

        if (!m_type.isInstance(obj)) {
            data.addError(getError());
        }
    }
}
