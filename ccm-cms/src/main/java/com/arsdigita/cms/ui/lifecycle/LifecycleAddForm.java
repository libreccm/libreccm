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
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.lifecycle.LifecycleDefinition;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.util.Locale;

/**
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Michael Pih
 * @author Jack Chung
 * @author <a href="mailto:xdmoon@redhat.com">Xixi D'Moon</a>
 * @author <a href="jross@redhat.com">Justin Ross</a>
 */
class LifecycleAddForm extends BaseLifecycleForm {

    private static final Logger LOGGER = LogManager.getLogger(LifecycleAddForm.class);

    private final SingleSelectionModel<Long> m_model;

    LifecycleAddForm(final SingleSelectionModel<Long> model) {
        super("LifecycleDefinition", gz("cms.ui.lifecycle.add"));

        m_model = model;

        m_name.addValidationListener(new NameUniqueListener(null));

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil.findBean(
                LifecycleDefinitionRepository.class);
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = new Locale(kernelConfig
                .getDefaultLanguage());

            final LifecycleDefinition definition = new LifecycleDefinition();

            definition.getLabel().addValue(defaultLocale,
                                           (String) m_name.getValue(state));
            definition.getDescription().addValue(
                defaultLocale,
                (String) m_description.getValue(state));
            lifecycleDefRepo.save(definition);

            final ContentSection section = CMS.getContext().getContentSection();
            sectionManager.addLifecycleDefinitionToContentSection(definition,
                                                                  section);

            m_model.setSelectedKey(state, 
                                   definition.getDefinitionId());
        }

    }

}
