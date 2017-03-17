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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.lifecycle.LifecycleDefinition;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class LifecycleNameUniqueListener implements ParameterListener {

    private final BaseLifecycleForm form;
    private final LifecycleDefinitionRequestLocal selectedDefinition;

    protected LifecycleNameUniqueListener(final BaseLifecycleForm form) {
        this.form = form;
        selectedDefinition = null;
    }

    protected LifecycleNameUniqueListener(
        final BaseLifecycleForm form,
        final LifecycleDefinitionRequestLocal selectedDefinition) {

        this.form = form;
        this.selectedDefinition = selectedDefinition;
    }

    @Override
    public final void validate(final ParameterEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);
        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        final String label = (String) form.getLifecycleName().getValue(state);

        final java.util.List<LifecycleDefinition> definitions = controller
            .getLifecyclesForContentSection(CMS.getContext().getContentSection());

        for (final LifecycleDefinition definition : definitions) {
            if (definition.getLabel().getValue(defaultLocale)
                .equalsIgnoreCase(label)
                    && (selectedDefinition == null
                        || !selectedDefinition.getLifecycleDefinition(state)
                        .equals(definition))) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.lifecycle.name_not_unique",
                    CmsConstants.CMS_BUNDLE));
            }
        }
    }

}
