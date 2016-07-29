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
import com.arsdigita.web.URL;
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
 * @version $Id$
 */
public abstract class ContextBar extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(ContextBar.class);

    private static final RequestLocal s_entries = new RequestLocal() {
            protected final Object initialValue(final PageState state) {
                return new ArrayList();
            }
        };

    public ContextBar() {
        super();
    }

    protected List entries(final PageState state) {
        return (List) s_entries.get(state);
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element nav = parent.newChildElement
                ("bebop:contextBar", BEBOP_XML_NS);

            for (Iterator iter = entries(state).iterator(); iter.hasNext(); ) {
                ((Entry) iter.next()).generateXML(state, nav);
            }
        }
    }

    public static final class Entry {
        private final String m_title;
        private final String m_href;

        public Entry(final String title, final String href) {
            super();

            Assert.exists(title, "String title");

            m_title = title;
            m_href = href;
        }

        public Entry(final String title, final URL url) {
            this(title, url.toString());
        }

        public void generateXML(final PageState state,
                                final Element parent) {
            final Element elem = parent.newChildElement
                ("bebop:entry", BEBOP_XML_NS);

            elem.addAttribute("title", m_title);
            elem.addAttribute("href", m_href);
        }
    }
}
