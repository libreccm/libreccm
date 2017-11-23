/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.admin.AdminUiConstants;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.pagemodel.PageModel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

/**
 * Implementation of {@link PropertySheetModel} for the the property sheet used
 * in {@link PageModelDetails} for displaying the basic properties of a
 * {@link PageModel}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelPropertySheetModel implements PropertySheetModel {

    private static enum PageModelProperty {
        MODEL_NAME,
        MODEL_TITLE,
        MODEL_APPLICATION,
        MODEL_DESC
    }

    private final PageModel pageModel;
    private final Iterator<PageModelProperty> propertyIterator;
    private PageModelProperty currentProperty;

    public PageModelPropertySheetModel(final PageModel pageModel) {

        this.pageModel = pageModel;
        propertyIterator = Arrays
            .asList(PageModelProperty.values())
            .iterator();
    }

    @Override
    public boolean nextRow() {
        if (pageModel == null) {
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

    @Override
    public GlobalizedMessage getGlobalizedLabel() {

        final String key = String
            .join("",
                  "ui.admin.pagemodels.details.",
                  currentProperty.toString().toLowerCase());
        return new GlobalizedMessage(key, AdminUiConstants.ADMIN_BUNDLE);
    }

    @Override
    public String getValue() {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        switch (currentProperty) {
            case MODEL_APPLICATION:
                return pageModel.getApplication().getPrimaryUrl();
            case MODEL_DESC:
                return pageModel.getDescription().getValue(defaultLocale);
            case MODEL_NAME:
                return pageModel.getName();
            case MODEL_TITLE:
                return pageModel.getTitle().getValue(defaultLocale);
            default:
                return "";
        }
    }

}
