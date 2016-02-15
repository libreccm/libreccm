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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.kernel.security.Util;

import com.arsdigita.web.Web;
import com.arsdigita.web.LoginSignal;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;

/**
 * A RequestListener that redirects the user to register if not logged in. The
 * redirection URL includes a return_url parameter to send the user back to this
 * page after logging in. Pages must not continue processing if this listener
 * redirects the user, since the response has already been committed
 * (isLoggedIn() returns false in this case). In a future version, this listener
 * will abort processing of the request if the user is not logged in.
 *
 * @author Phong Nguyen
 * @author Sameer Ajmani
 * @version 1.0
 * @version $Id: UserAuthenticationListener.java 287 2005-02-22 00:29:02Z
 * sskracic $
 */
public class UserAuthenticationListener implements RequestListener {

    private static final Logger s_log = Logger.getLogger(
        UserAuthenticationListener.class);

    /**
     * If the user is logged in, returns the User object.
     *
     * @param state
     *
     * @return the User object for the logged in user
     *
     * @throws IllegalStateException if user is not logged in. Call isLoggedIn()
     *                               to check for this case.
     */
    public Subject getUser(final PageState state) {
        if (!isLoggedIn(state)) {
            throw new IllegalStateException("User is not logged in");
        }

        final Subject subject = CdiUtil.createCdiUtil().findBean(Subject.class);

        return subject;
    }

    /**
     * Determines whether the user is logged in.
     *
     * @param state
     *
     * @return true if the user is logged in
     */
    public boolean isLoggedIn(final PageState state) {
        return getUser(state).isAuthenticated();
    }

    /**
     * Checks whether the user is logged in. If not, redirects the client to the
     * login page.
     *
     * @param event
     */
    @Override
    public void pageRequested(final RequestEvent event) {
        PageState state = event.getPageState();

        if (!isLoggedIn(state)) {
            s_log.debug("User is not logged in");
            redirectToLoginPage(state);

        }
    }

    /**
     * Redirects the client to the login page.
     *
     * @param state
     */
    private void redirectToLoginPage(final PageState state) {
        HttpServletRequest req = state.getRequest();
        String urlBase = Util.getSecurityHelper().getLoginURL(req);

        // first make sure we're not already looking at the login
        // page -- if we are, don't redirect!
        if (urlBase.equals(Web.getWebContext().getRequestURL().getRequestURI())) {
            s_log.debug("preventing cyclic redirect to: " + urlBase);
            // return without redirect
            return;
        }

        throw new LoginSignal(req);
    }

}
