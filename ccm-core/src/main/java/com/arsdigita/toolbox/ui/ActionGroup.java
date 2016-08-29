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
import com.arsdigita.bebop.PageState;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * <p>A simple layout panel with top, bottom, left, right, and body
 * sections.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class ActionGroup extends ComponentSet {

    private static final Logger s_log = Logger.getLogger(ActionGroup.class);

    private Component m_subject;
    private final ArrayList m_actions = new ArrayList();

    public static final String ADD = "add";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String RETURN = "return";

    public final void setSubject(final Component subject) {
        Assert.exists(subject, "Component subject");
        Assert.isUnlocked(this);

        m_subject = subject;
        add(m_subject);
    }

    public final void addAction(final Component action, final String clacc) {
        Assert.exists(action, "Component action");
        Assert.isUnlocked(this);

        m_actions.add(new Object[] {action, clacc});
        add(action);
    }

    public final void addAction(final Component action) {
        addAction(action, null);
    }

    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element layout = parent.newChildElement
                ("bebop:actionGroup", BEBOP_XML_NS);

            final Element subject = layout.newChildElement
                ("bebop:subject", BEBOP_XML_NS);

            if (m_subject != null) {
                m_subject.generateXML(state, subject);
            }

            for (Iterator iter = m_actions.iterator(); iter.hasNext(); ) {
                final Object[] spec = (Object[]) iter.next();
                final Component component = (Component) spec[0];
                final String clacc = (String) spec[1];

                if (component.isVisible(state)) {
                    final Element action = layout.newChildElement
                        ("bebop:action", BEBOP_XML_NS);

                    if (clacc != null) {
                        action.addAttribute("class", clacc);
                    }

                    component.generateXML(state, action);
                }
            }
        }
    }
}
