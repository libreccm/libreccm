/*
 * Copyright (C) Permeance Technologies Pty Ltd. All Rights Reserved.
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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

/**
 * Validates that a {@link String} property of a {@link DomainObject} is unique.
 *
 * @author
 * <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class UniqueStringValidationListener extends GlobalizedParameterListener {

    public UniqueStringValidationListener(String baseDataObjectType,
                                          String propertyKey,
                                          ParameterModel domainParameter) {

        this.setError(new GlobalizedMessage("parameter_not_unique", this
                                            .getBundleBaseName()));
        Assert.exists(baseDataObjectType);
        Assert.exists(propertyKey);
        Assert.exists(domainParameter);
        m_baseDataObjectType = baseDataObjectType;
        m_propertyKey = propertyKey;
        m_domainParameter = domainParameter;
    }

    public void validate(ParameterEvent e) throws FormProcessException {

//        ParameterData data = e.getParameterData();
//        String propertyValue = (data.getValue() == null ? null : String.valueOf(
//                                data.getValue()));
//
//        if (propertyValue != null && propertyValue.length() > 0) {
//            // Get the current domain object
//            DomainObject domainObject = (DomainObject) e.getPageState()
//                .getValue(m_domainParameter);
//
//            // Check if there are any existing matches
//            DataCollection collection = SessionManager.getSession().retrieve(
//                m_baseDataObjectType);
//            collection.addEqualsFilter(m_propertyKey, propertyValue);
//            while (collection.next()) {
//                if (domainObject == null || !collection.getDataObject().getOID()
//                    .equals(domainObject.getOID())) {
//                    data.addError(this.getError());
//                    break;
//                }
//            }
//        }
    }

    private final String m_baseDataObjectType;

    private final String m_propertyKey;

    private final ParameterModel m_domainParameter;

}
