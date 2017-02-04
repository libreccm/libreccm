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
import com.arsdigita.util.SequentialMap;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;

import java.util.Iterator;

import org.apache.logging.log4j.Logger;

public abstract class ComponentMap extends SimpleComponent
    implements Resettable {

    private static final Logger LOGGER = LogManager
        .getLogger(ComponentMap.class);

    private final SequentialMap m_components;

    public ComponentMap() {
        m_components = new SequentialMap();
    }

    public final Iterator children() {
        return m_components.values().iterator();
    }

    public void reset(final PageState state) {
        LOGGER.debug("Resetting my children");

        final Iterator iter = children();

        while (iter.hasNext()) {
            final Object component = iter.next();

            if (component instanceof Resettable) {
                ((Resettable) component).reset(state);
            }
        }
    }

    public final void put(final Object key, final Component component) {
        Assert.isUnlocked(this);
        Assert.exists(key, Object.class);

        m_components.put(key, component);
    }

    public final Component get(final Object key) {
        return (Component) m_components.get(key);
    }

    public final boolean containsKey(final Object key) {
        return m_components.containsKey(key);
    }

    public final boolean containsValue(final Component component) {
        return m_components.containsValue(component);
    }

    public abstract void generateXML(final PageState state,
                                     final Element parent);

}
