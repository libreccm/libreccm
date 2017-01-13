/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.librecms.lifecycle.LifecycleDefinition;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.util.Locale;

/**
 * This class contains a form component to edit a lifecycle definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
class LifecycleEditForm extends BaseLifecycleForm {

    private static final Logger LOGGER = LogManager.getLogger(
        LifecycleEditForm.class);

    private final LifecycleDefinitionRequestLocal m_definition;

    LifecycleEditForm(final LifecycleDefinitionRequestLocal definition) {
        super("LifecycleEdit", gz("cms.ui.lifecycle.edit"));

        m_definition = definition;

        m_name.addValidationListener(new NameUniqueListener(m_definition));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        public final void init(final FormSectionEvent e) {
            final PageState state = e.getPageState();
            final LifecycleDefinition cycle = m_definition
                .getLifecycleDefinition(state);

            m_name.setValue(state, cycle.getLabel());
            m_description.setValue(state, cycle.getDescription());
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final LifecycleDefinition definition = m_definition
                .getLifecycleDefinition(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil
                .findBean(LifecycleDefinitionRepository.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();

            definition.getLabel().addValue(defaultLocale,
                                           (String) m_name.getValue(state));
            definition.getDescription().addValue(
                defaultLocale,
                (String) m_description.getValue(state));
            lifecycleDefRepo.save(definition);
        }

    }

}
