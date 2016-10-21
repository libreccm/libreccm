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
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.CMS;

import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.LifecycleDefinition;

import java.util.Locale;

/**
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
class BaseLifecycleForm extends BaseForm {

    private static final Logger s_log = Logger
        .getLogger(BaseLifecycleForm.class);

    final TextField m_name;
    final TextArea m_description;

    BaseLifecycleForm(final String key, final GlobalizedMessage message) {
        super(key, message);

        m_name = new TextField(new TrimmedStringParameter("label"));
        addField(gz("cms.ui.name"), m_name);

        m_name.addValidationListener(new NotEmptyValidationListener());
        m_name.setSize(40);
        m_name.setMaxLength(1000);

        m_description = new TextArea(new TrimmedStringParameter("description"));
        addField(gz("cms.ui.description"), m_description);

        m_description.addValidationListener(new StringLengthValidationListener(
            4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);

        addAction(new Finish());
        addAction(new Cancel());

        addSubmissionListener(new FormSecurityListener(
            AdminPrivileges.ADMINISTER_LIFECYLES));
    }

    class NameUniqueListener implements ParameterListener {

        private final LifecycleDefinitionRequestLocal m_definition;

        NameUniqueListener(final LifecycleDefinitionRequestLocal definition) {
            m_definition = definition;
        }

        @Override
        public final void validate(final ParameterEvent e)
            throws FormProcessException {
            
            final PageState state = e.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager confManager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();
            final String label = (String) m_name.getValue(state);

            final java.util.List<LifecycleDefinition> definitions = CMS
                .getContext().getContentSection().getLifecycleDefinitions();

            for (final LifecycleDefinition definition : definitions) {
                if (definition.getLabel().getValue(defaultLocale)
                    .equalsIgnoreCase(label)
                        && (m_definition == null
                            || !m_definition.getLifecycleDefinition(state)
                            .equals(definition))) {
                    throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.lifecycle.name_not_unique",
                        CmsConstants.CMS_BUNDLE));
                }
            }
        }

    }

}
