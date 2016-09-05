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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;
import org.libreccm.security.Role;
import org.librecms.contentsection.ContentSection;

import java.util.Collection;
import java.util.TooManyListenersException;

/**
 * For more detailed information see {@link com.arsdigita.bebop.Form}.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BaseRoleForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class BaseRoleForm extends BaseForm {

    private static final Logger s_log = Logger.getLogger(BaseRoleForm.class);

    final Name m_name;
    final Description m_description;
    CheckboxGroup m_privileges;

    private boolean m_useViewersGroup;

    public BaseRoleForm(final String key,
                        final GlobalizedMessage message,
                        final boolean useViewersGroup) {
        super(key, message);

        m_useViewersGroup = useViewersGroup;

        m_name = new Name("label", 200, true);
        addField(gz("cms.ui.name"), m_name);

        m_description = new Description("description", 4000, false);
        addField(gz("cms.ui.description"), m_description);

        m_privileges = new CheckboxGroup("privileges");
        addField(gz("cms.ui.role.privileges"), m_privileges);

        try {
            m_privileges.addPrintListener(new PrivilegePrinter());
        } catch (TooManyListenersException tmle) {
            throw new UncheckedWrapperException(tmle);
        }

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(SecurityConstants.STAFF_ADMIN);
    }

    private class PrivilegePrinter implements PrintListener {
	    @Override
        public final void prepare(final PrintEvent e) {
            /*final CheckboxGroup target = (CheckboxGroup) e.getTarget();
            final PageState state = e.getPageState();

            final DataQuery query = SessionManager.getSession().retrieveQuery
                (RoleFactory.CMS_PRIVILEGES);
            query.addOrder(RoleFactory.SORT_ORDER);

            while (query.next()) {
                target.addOption
                    (new Option((String) query.get(RoleFactory.PRIVILEGE),
                                (String) query.get(RoleFactory.PRETTY_NAME)));
            }

            query.close();
            */
        }
    }

    class NameUniqueListener implements ParameterListener {
        private final RoleRequestLocal m_role;

        NameUniqueListener(final RoleRequestLocal role) {
            m_role = role;
        }

	    @Override
        public final void validate(final ParameterEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final ContentSection section =
                CMS.getContext().getContentSection();
            final String name = (String) m_name.getValue(state);

            Collection<Role> roles;

            /*
            if (m_useViewersGroup) {
                roles = section.getViewersGroup().getRoles();
            } else {
                roles = section.getStaffGroup().getRoles();
            }



            while (roles.next()) {
                final Role role = roles.getRole();

                if (roles.getRole().getName().equalsIgnoreCase(name)
                        && (m_role == null
                            || !m_role.getRole(state).equals(role))) {
                    roles.close();

                    throw new FormProcessException
                        (GlobalizationUtil.globalize("cms.ui.role.name_not_unique"));
                }
            }

            roles.close();
            */
        }
    }
}
