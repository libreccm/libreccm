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
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

/** 
 *
 * @version $Id$
 */
public class ComponentSet extends SimpleComponent
        implements Resettable {

    private static final Logger s_log = Logger.getLogger(ComponentSet.class);

    private final ArrayList m_components;

    public ComponentSet() {
        m_components = new ArrayList();
    }

    public void reset(final PageState state) {
        s_log.debug("Resetting children");

        final Iterator iter = children();

        while (iter.hasNext()) {
            final Object component = iter.next();

            if (component instanceof Resettable) {
                ((Resettable) component).reset(state);
            }
        }
    }

    public final Iterator children() {
        return m_components.iterator();
    }

    public final void add(final Component component) {
        Assert.exists(component, "Component component");

        synchronized (m_components) {
            final int index = m_components.indexOf(component);

            if (index == -1) {
                m_components.add(component);
            } else {
                m_components.set(index, component);
            }
        }
    }

    public final Component get(final int index) {
        return (Component) m_components.get(index);
    }

    public final int indexOf(final Component component) {
        return m_components.indexOf(component);
    }

    public final boolean contains(final Component component) {
        return m_components.contains(component);
    }

    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Iterator iter = children();

            while (iter.hasNext()) {
                ((Component) iter.next()).generateXML(state, parent);
            }
        }
    }
}
