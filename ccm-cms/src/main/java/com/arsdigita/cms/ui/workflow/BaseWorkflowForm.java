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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @author Uday Mathur
 * @author Michael Pih
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
class BaseWorkflowForm extends BaseForm {

    final Name m_title;
    final Description m_description;

    BaseWorkflowForm(final String key, final GlobalizedMessage message) {
        super(key, message);

        m_title = new Name("name", 200, true);
        addField(gz("cms.ui.name"), m_title);

        m_description = new Description("desc", 4000, true);
        addField(gz("cms.ui.description"), m_description);

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(AdminPrivileges.ADMINISTER_WORKFLOW);
        addValidationListener(new ValidationListener());
    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public final void validate(final FormSectionEvent event)
            throws FormProcessException {
            final String name = (String) m_title.getValue(event.getPageState());

            // XXX do a dupe check here ala commented out code below
        }

    }

    /*
    protected void addValidationListener(Form f) {
        f.addValidationListener(new DataQueryExistsListener(ERROR_MSG) {
                private final String QUERY_NAME =
                    "com.arsdigita.workflow.simple.getProcessDefinitions";


                public void validate(FormSectionEvent event)
                    throws FormProcessException {

                    String name = (String) m_title.getValue(event.getPageState());
                    if ( name != null ) {
                        super.validate(event);
                    } else {
                        // Do nothing. The NotNullValidation listener should kick in.
                    }
                }

                public DataQuery getDataQuery(FormSectionEvent e) {
                    PageState s = e.getPageState();
                    Session session = SessionManager.getSession();
                    DataQuery query = session.retrieveQuery(QUERY_NAME);
                    Filter listenerFilter = query.addFilter("lower(processDefinitionLabel) = lower(:label)");
                    listenerFilter.set("label", ((String) m_title.getValue(s)).trim());
                    Filter itemFilter = query.addNotEqualsFilter
                        ("processDefinitionId", m_id.getValue(s));
                    return query;
                }
            });
    }
     */
}
