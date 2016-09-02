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

import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.web.CcmApplication;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationInstanceTreeNode implements TreeNode {

    protected static final String INSTANCE_NODE_KEY_PREFIX = "appinstance_";

    private final CcmApplication application;

    public ApplicationInstanceTreeNode(final CcmApplication application) {
        this.application = application;
    }

    @Override
    public Object getKey() {
        return String.format("%s%d",
                             INSTANCE_NODE_KEY_PREFIX,
                             application.getObjectId());
    }

    @Override
    public Object getElement() {
        final GlobalizationHelper globalizationHelper = CdiUtil.createCdiUtil()
            .findBean(GlobalizationHelper.class);
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final String title;
        if (application.getTitle().hasValue(globalizationHelper
            .getNegotiatedLocale())) {
            title = application.getTitle().getValue(globalizationHelper
                .getNegotiatedLocale());
        } else if (application.getTitle().hasValue(defaultLocale)) {
            title = application.getTitle().getValue(defaultLocale);
        } else if (application.getTitle().hasValue(Locale.getDefault())) {
            title = application.getTitle().getValue(Locale.getDefault());
        } else {
            title = application.getPrimaryUrl();
        }

        return title;
    }

    public CcmApplication getApplication() {
        return application;
    }

}
