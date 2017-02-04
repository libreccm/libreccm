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
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.ReturnSignal;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides helper functions for the login UI.
 *
 * @author Sameer Ajmani
 */
public class LoginHelper {

    private static final Logger LOGGER = LogManager.getLogger(LoginHelper.class);

    public static final String RETURN_URL_PARAM_NAME = "return_url";

    /**
     * Returns the name of the login UI resource bundle
     *
     * @return the name of the login UI resource bundle
     **/
    static String getBundleBaseName() {
        return "com.arsdigita.ui.login.LoginResources";
    }

    /**
     * Returns a new GlobalizedMessage constructed with the given
     * parameters and the login UI resource bundle.
     *
     * @return a new GlobalizedMessage
     **/
    static GlobalizedMessage getMessage(String key, Object[] args) {
        return new GlobalizedMessage(key, getBundleBaseName(), args);
    }

    /**
     * Returns a new GlobalizedMessage constructed with the given
     * parameters and the login UI resource bundle.
     *
     * @return a new GlobalizedMessage
     **/
    static GlobalizedMessage getMessage(String key) {
        return new GlobalizedMessage(key, getBundleBaseName());
    }

    /**
     * Constructs a new GlobalizedMessage with the given parameters and the
     * login UI resource bundle, then localizes the message with the given
     * request.
     *
     * @return the localized String
     **/
    static String localize(String key, Object[] args,
                           HttpServletRequest request) {
        return (String)getMessage(key, args).localize(request);
    }

    /**
     * Constructs a new GlobalizedMessage with the given parameters and the
     * login UI resource bundle, then localizes the message with the given
     * request.
     *
     * @return the localized String
     **/
    static String localize(String key, HttpServletRequest request) {
        return (String)getMessage(key).localize(request);
    }

    /**
     * Redirect the client to the URL stored in the return_url request
     * parameter, or, if that parameter is not set, redirect to the given
     * default URL.
     *
     * @param state the current page state
     * @param def the default URL
     **/
    public static void doReturn(PageState state, String def) {
        throw new ReturnSignal(state.getRequest(), def);
    }

    /**
     * Redirect the client to the given URL unless the response has
     * already been committed. Wrapper for {@link
     * #sendRedirect(HttpServletRequest, HttpServletResponse, String)}
     * that pulls out the request and response from the PageState.
     *
     * @throws IOException if the redirect fails.
     **/
    public static void sendRedirect(PageState state, String url)
        throws IOException {
        sendRedirect(state.getRequest(), state.getResponse(), url);
    }

    /**
     * Redirect the client to the given URL unless the response has already
     * been committed.  Aborts further request processing.
     *
     * @throws IOException if the redirect fails.
     **/
    public static void sendRedirect(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String url)
        throws IOException {

        if (!response.isCommitted()) {
            LOGGER.debug("Redirecting to: "+url);
            DispatcherHelper.sendRedirect(request, response, url);
            response.flushBuffer();
            DispatcherHelper.abortRequest();
        } else {
            LOGGER.debug("Redirect failed because "
                        +"response already committed");
        }
    }
}