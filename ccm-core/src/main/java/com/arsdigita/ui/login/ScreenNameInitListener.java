/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;

import org.apache.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;

// Note: Previously used SiteNodeRequestContext, nows using KernelRequestContext
//       may be one cause that Login doesn't survive if the brwoser window is
//       closed.
/**
 * Initializes the value of the given parameter to the current user's screen
 * name. Strangely similar to <code>EmailInitListener</code>.
 *
 * @author <a href="mailto:cwolfe@redhat.com">Crag Wolfe</a>
 * @version $Id$
 */
public class ScreenNameInitListener implements FormInitListener {

    private static Logger s_log = Logger.getLogger(ScreenNameInitListener.class
        .getName());
    private StringParameter m_param;

    /**
     *
     * @param param
     */
    public ScreenNameInitListener(StringParameter param) {
        m_param = param;
    }

    /**
     *
     * @param event
     */
    public void init(FormSectionEvent event) {
        PageState state = event.getPageState();
        FormData data = event.getFormData();
        s_log.debug("START");

        final CdiUtil cdiUtil = new CdiUtil();
        final Subject subject = cdiUtil.findBean(Subject.class);
        final Shiro shiro = cdiUtil.findBean(Shiro.class);

        if (!subject.isAuthenticated()) {
            s_log.debug("FAILURE not logged in");
            return;
        }

        final User user = shiro.getUser();
        if (user.getName() == null) {
            s_log.debug("FAILURE null screen name");
            return;
        }

        data.put(m_param.getName(), user.getName());
        s_log.debug("SUCCESS");
    }

}
