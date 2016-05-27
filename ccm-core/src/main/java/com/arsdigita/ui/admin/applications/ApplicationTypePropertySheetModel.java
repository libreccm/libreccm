/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;

import com.fasterxml.jackson.databind.deser.CreatorProperty;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationType;

import java.util.Arrays;
import java.util.Iterator;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationTypePropertySheetModel implements PropertySheetModel {

    private static enum AppTypeProperty {
        NAME,
        DESC,
        APP_CLASS,
        CREATOR,
        SINGLETON,
        SERVLET_CLASS,
        SERVLET_PATH,
    }

    private final ApplicationType applicationType;
    private final Iterator<AppTypeProperty> propertyIterator;
    private AppTypeProperty currentProperty;

    public ApplicationTypePropertySheetModel(
        final ApplicationType applicationType) {

        this.applicationType = applicationType;
        propertyIterator = Arrays.asList(AppTypeProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (applicationType == null) {
            return false;
        }

        if (propertyIterator.hasNext()) {
            currentProperty = propertyIterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getLabel() {
        return currentProperty.toString();
    }

    private GlobalizedMessage generateGlobalizedLabel(
        final AppTypeProperty property) {

        final String key = String.join(
            "",
            "ui.admin.applications.type.property_sheet.",
            property.toString().toLowerCase());
        
        return new GlobalizedMessage(key, ADMIN_BUNDLE);
    }

    @Override
    public GlobalizedMessage getGlobalizedLabel() {
        return generateGlobalizedLabel(currentProperty);
    }

    @Override
    public String getValue() {
        switch(currentProperty) {
            case NAME: 
                return applicationType.name();
            case DESC: 
                return getAppTypeDesc();
            case APP_CLASS:
                return applicationType.applicationClass().getName();
            case CREATOR:
                return applicationType.creator().getName();
            case SINGLETON:
                return Boolean.toString(applicationType.singleton());
            case SERVLET_CLASS:
                return applicationType.servlet().getName();
            case SERVLET_PATH:
                return applicationType.servletPath();
            default: 
                return "";
        }
    }

    private String getAppTypeDesc() {
        final org.libreccm.web.ApplicationManager appManager = CdiUtil.createCdiUtil().findBean(
            org.libreccm.web.ApplicationManager.class);
        
        return appManager.getApplicationTypeDescription(applicationType);
    }
}
