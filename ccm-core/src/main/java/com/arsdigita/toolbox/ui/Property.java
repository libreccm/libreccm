/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class Property {

    private final String title;
    private final String value;

    public Property(final String title, final String value) {
        super();
        Assert.exists(title, "String title");
        this.title = title;
        this.value = value;
    }

    public Property(final GlobalizedMessage title, final String value) {
        this(title.localize().toString(), value);
    }

    public Property(final GlobalizedMessage title, final GlobalizedMessage value) {
        this(title.localize().toString(), value.localize().toString());
    }

    public Property(final String title, final GlobalizedMessage value) {
        this(title, value.localize().toString());
    }

    public void generateXML(final PageState state, final Element parent) {
        final Element elem = parent.newChildElement("bebop:property",
                                                    Component.BEBOP_XML_NS);
        elem.addAttribute("title", title);
        elem.addAttribute("value", value);
    }

}
