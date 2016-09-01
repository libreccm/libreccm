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

import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>A context bar.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public abstract class PropertyList extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(PropertyList.class);

    private static final RequestLocal s_props = new RequestLocal() {
            protected final Object initialValue(final PageState state) {
                return new ArrayList();
            }
        };

    public PropertyList() {
        super();
    }

    protected List properties(final PageState state) {
        return (List) s_props.get(state);
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element nav = parent.newChildElement
                ("bebop:propertyList", BEBOP_XML_NS);

            for (Iterator iter = properties(state).iterator();
                 iter.hasNext(); ) {
                ((Property) iter.next()).generateXML(state, nav);
            }
        }
    }

    public static final class Property {
        private final String m_title;
        private final String m_value;

        public Property(final String title, final String value) {
            super();

            Assert.exists(title, "String title");

            m_title = title;
            m_value = value;
        }

        public Property(final GlobalizedMessage title, final String value) {
            this(title.localize().toString(), value);
        }

        public Property(final GlobalizedMessage title,
                        final GlobalizedMessage value) {
            this(title.localize().toString(), value.localize().toString());
        }

        public Property(final String title, final GlobalizedMessage value) {
            this(title, value.localize().toString());
        }

        public void generateXML(final PageState state,
                                final Element parent) {
            final Element elem = parent.newChildElement
                ("bebop:property", BEBOP_XML_NS);

            elem.addAttribute("title", m_title);
            elem.addAttribute("value", m_value);
        }
    }
}
