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
import com.arsdigita.xml.Element;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class PropertyList extends SimpleComponent {

    private static final RequestLocal PROPERTIES = new RequestLocal() {

        @Override
        protected final Object initialValue(final PageState state) {
            return new ArrayList<>();
        }

    };

    public PropertyList() {
        super();
    }

    @SuppressWarnings("unchecked")
    protected List<Property> properties(final PageState state) {
        return (List<Property>) PROPERTIES.get(state);
    }

    @Override
    public final void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Element nav = parent.newChildElement("bebop:propertyList",
                                                       BEBOP_XML_NS);

            properties(state).forEach(property -> property.generateXML(state,
                                                                       nav));
        }
    }

}
