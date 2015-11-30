/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui.login;

import com.arsdigita.web.Web;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.util.UncheckedWrapperException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

/**
 * Initializes the value of the given parameter to the current user's email
 * address. If the user is not logged in or the email address is invalid, the
 * parameter is not modified.
 *
 * @author Sameer Ajmani
 *
 */
public class EmailInitListener implements FormInitListener {

    private static final Logger s_log = Logger.getLogger(EmailInitListener.class
            .getName());

    private EmailParameter m_param;

    public EmailInitListener(EmailParameter param) {
        m_param = param;
    }

    public void init(FormSectionEvent event) {
        FormData data = event.getFormData();

        s_log.debug("START");

        final Subject subject;
        final Shiro shiro;
        try {
            final CdiUtil cdiUtil = new CdiUtil();
            subject = cdiUtil.findBean(Subject.class);
            shiro = cdiUtil.findBean(Shiro.class);
        } catch (CdiLookupException ex) {
            throw new UncheckedWrapperException(ex);
        }

        if (!subject.isAuthenticated()) {
            s_log.debug("FAILURE not logged in");
            return;
        }

        final User user = shiro.getUser();

        if (user == null) {
            s_log.debug("FAILURE no such user");
            return;
        }

        if (user.getPrimaryEmailAddress() == null) {
            s_log.debug("FAILURE null primary email");
            return;
        }
        
        

        data.put(m_param.getName(), user.getPrimaryEmailAddress().getAddress());
        
        s_log.debug("SUCCESS");
    }

}
