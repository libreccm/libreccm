/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

import static com.arsdigita.ui.UI.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SiteBanner extends SimpleComponent {

    @Override
    public void generateXML(final PageState state, final Element parentElem) {
        final Element contentElem = parentElem.newChildElement("ui:siteBanner",
                                                               UI_XML_NS);
        
        exportAttributes(contentElem);
        
        contentElem.addAttribute("hostname", getHostname());
        contentElem.addAttribute("sitename", getSiteName());
    }

    protected String getHostname() {
        return Web.getConfig().getServer();
    }
    
    protected String getSiteName() {
        return Web.getConfig().getSiteName();
    }
}
