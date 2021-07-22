/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.ui;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class BaseUrl {

    public String getBaseUrl(final HttpServletRequest request) {
        final StringBuilder baseUrlBuilder = new StringBuilder();
        return baseUrlBuilder
            .append(request.getScheme())
            .append("://")
            .append(request.getServerName())
            .append(addServerPortToBaseUrl(request))
            .append(addContextPathToBaseUrl(request))
            .toString();
    }

    private String addServerPortToBaseUrl(final HttpServletRequest request) {
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            return "";
        } else {
            return String.format(":%d", request.getServerPort());
        }
    }

    private String addContextPathToBaseUrl(final HttpServletRequest request) {
        if (request.getServletContext().getContextPath() == null
                || request.getServletContext().getContextPath().isEmpty()) {
            return "/";
        } else {
            return request.getServletContext().getContextPath();
        }
    }

}
