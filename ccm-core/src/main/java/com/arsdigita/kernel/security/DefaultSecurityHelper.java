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
package com.arsdigita.kernel.security;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation of SecurityHelper interface.
 *
 * @author Sameer Ajmani
 * @see SecurityHelper
 */
public class DefaultSecurityHelper implements SecurityHelper {

    /**
     * Determines whether the request is secure by calling
     * <code>req.isSecure()</code>.
     *
     * @param request The current {@link HttpServletRequest}
     *
     * @return req.isSecure().
     *
     */
    @Override
    public boolean isSecure(final HttpServletRequest request) {
        return request.isSecure();
    }

    /**
     * Determines whether the current request requires that the user be logged
     * in.
     *
     * @param request The current {@link HttpServletRequest}
     *
     * @return <code>true</code> if the request is secure and the page is not on
     *         a list of allowed pages (such as the login page and the
     *         bad-password page), <code>false</code> otherwise.
     *
     */
    @Override
    public boolean requiresLogin(final HttpServletRequest request) {
        // XXX workaround, old is broken anyway,
        //     it doesn't take into account dispatcher prefix ( /ccm )
        return false;
    }

    /**
     * Returns the full URL of the login page stored in the page map.
     *
     * @param request The current {@link HttpServletRequest}
     *
     * @return the full URL of the login page.
     *
     */
    @Override
    public String getLoginURL(final HttpServletRequest request) {
        //ToDo: Add correct method call here.

        //return UI.getLoginPageURL();
        return "";
    }

}
