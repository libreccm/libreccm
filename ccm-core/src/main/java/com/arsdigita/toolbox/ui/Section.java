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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * <p>A simple layout panel with top, bottom, left, right, and body
 * sections.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class Section extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(Section.class);

    private final ArrayList m_children;
    private Component m_heading;
    private Component m_body;

    public Section(final Component heading, final Component body) {
        m_children = new ArrayList(2);
        m_heading = heading;
        m_body = body;
    }

    public Section(final GlobalizedMessage heading, final Component body) {
        this(new Label(heading), body);
    }

    public Section(final Component heading) {
        this(heading, new NullComponent());
    }

    public Section(final GlobalizedMessage heading) {
        this(heading, new NullComponent());
    }

    public Section() {
        this(new NullComponent(), new NullComponent());
    }

    public final void setHeading(final Component heading) {
        Assert.exists(heading, "Component header");
        Assert.isUnlocked(this);

        m_heading = heading;
    }

    public final void setHeading(final GlobalizedMessage message) {
        setHeading(new Label(message));
    }

    public final void setBody(final Component body) {
        Assert.exists(body, "Component body");
        Assert.isUnlocked(this);

        m_body = body;
    }

    public void register(final Page page) {
        super.register(page);

        m_children.add(m_heading);
        m_children.add(m_body);
    }

    public void register(final Form form, final FormModel model) {
        super.register(form, model);

        m_children.add(m_heading);
        m_children.add(m_body);
    }

    public final Iterator children() {
        return m_children.iterator();
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element section = parent.newChildElement
                ("bebop:section", BEBOP_XML_NS);

            final Element heading = section.newChildElement
                ("bebop:heading", BEBOP_XML_NS);

            m_heading.generateXML(state, heading);

            final Element body = section.newChildElement
                ("bebop:body", BEBOP_XML_NS);

            m_body.generateXML(state, body);
        }
    }
}
