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
package org.libreccm.admin.ui;

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelDetails extends Window {

    private static final long serialVersionUID = -3617001410191320596L;

    PageModelDetails(final PageModel pageModel,
                     final AdminViewController controller) {

        super();

        final GlobalizationHelper globalizationHelper = controller
            .getGlobalizationHelper();
        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(AdminUiConstants.ADMIN_BUNDLE);
        final ConfigurationManager configurationManager = controller
            .getConfigurationManager();
        final KernelConfig kernelConfig = configurationManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        super.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.heading",
                     new String[]{pageModel.getName()}));

        final Label nameLabel = new Label(pageModel.getName());
        nameLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_name"));

        final Label titleLabel = new Label(pageModel
            .getTitle().getValue(defaultLocale));
        titleLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_title"));

        final Label applicationLabel = new Label(pageModel
            .getApplication().getPrimaryUrl());
        applicationLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_application"));

        final Label descLabel = new Label(pageModel
            .getDescription().getValue(defaultLocale));
        descLabel.setCaption(textsUtil
            .getText("ui.admin.pagemodels.details.model_desc"));

        final FormLayout propertiesSheetLayout = new FormLayout(
            nameLabel, titleLabel, applicationLabel, descLabel);

        
        
        super.setContent(new VerticalLayout(propertiesSheetLayout));
    }

}
