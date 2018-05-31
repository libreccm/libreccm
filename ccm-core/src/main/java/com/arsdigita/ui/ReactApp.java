/*
 * Copyright (C) 2018 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import javax.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ReactApp extends SimpleComponent {

    private final String appId;
    private final String scriptPath;

    public ReactApp(final String appId, final String scriptPath) {
        super();

        this.appId = appId;
        this.scriptPath = scriptPath;
    }

    public String getAppId() {
        return appId;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        final Element reactAppElem = parent.newChildElement("bebop:reactApp",
                                                            BEBOP_XML_NS);
        reactAppElem.addAttribute("appId", appId);
        reactAppElem.addAttribute("scriptPath",
                                  String.format("%s/%s",
                                                Web
                                                    .getServletContext()
                                                    .getContextPath(),
                                                scriptPath));

        final String primaryUrl = getPrimaryUrl();

        reactAppElem
            .addAttribute("ccmApplication", primaryUrl);
        
        reactAppElem
            .addAttribute("dispatcherPrefix", Web.getWebappContextPath());
    }

    private String getPrimaryUrl() {

        final String primaryUrl = Web
            .getWebContext()
            .getApplication()
            .getPrimaryUrl();

        if (primaryUrl.matches("^/(.*)/$")) {
            return primaryUrl.substring(1, primaryUrl.length() - 1);
        } else if (primaryUrl.matches("^/(.*)$")) {
            return primaryUrl.substring(1);
        } else if (primaryUrl.matches("^(.*)/$")) {
            return primaryUrl.substring(0, primaryUrl.length() - 1);
        } else {
            return primaryUrl;
        }
    }

}
