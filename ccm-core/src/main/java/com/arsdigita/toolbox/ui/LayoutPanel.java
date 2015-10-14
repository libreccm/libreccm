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

import static com.arsdigita.bebop.Component.*;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;

/**
 * <p>A simple layout panel with top, bottom, left, right, and body
 * sections.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class LayoutPanel extends ComponentMap {

    private static final Logger s_log = Logger.getLogger(LayoutPanel.class);

    public final void setTop(final Component top) {
        put("top", top);
    }

    public final void setLeft(final Component left) {
        put("left", left);
    }

    public final void setBody(final Component body) {
        put("body", body);
    }

    public final void setRight(final Component right) {
        put("right", right);
    }

    public final void setBottom(final Component bottom) {
        put("bottom", bottom);
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element layout = parent.newChildElement
                ("bebop:layoutPanel", BEBOP_XML_NS);

            section(state, layout, "top");
            section(state, layout, "left");
            section(state, layout, "body");
            section(state, layout, "right");
            section(state, layout, "bottom");
        }
    }

    private void section(final PageState state,
                         final Element parent,
                         final String key) {
        final Component section = get(key);

        if (section != null) {
            final Element elem = parent.newChildElement
                ("bebop:" + key, BEBOP_XML_NS);

            section.generateXML(state, elem);
        }
    }
}
