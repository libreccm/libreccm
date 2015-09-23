/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui;


import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;

import com.arsdigita.util.Assert;
import com.arsdigita.bebop.SimpleComponent;


/**
 * The SimplePageLayout class stores the list of 
 * components on a page. Each component has an
 * associated layout tag which is interpreted by
 * the XSL when formatting the page. If the XSLT
 * template in question were implementing a four
 * area border layout (header, footer, left & right
 * margins), this tag might be one of the strings
 * 'top', 'bottom', 'left', 'right'. The Java code
 * places no constraints or intpretation of these
 * layout tags.
 */
class SimplePageLayout {

    private HashMap m_tags = new HashMap();

    /**
     * Adds a component to the layout with a particular
     * position tag.
     *
     * @param component A subclass of Component with a no-arg constructor
     * @param tag the layout position tag
     */
    public void addComponent(Class component,
                             String tag) {
        Assert.exists(component, Class.class);
        Assert.exists(tag, String.class);
        
        Assert.isTrue(SimpleComponent.class.isAssignableFrom(component),
                     "component is a subclass of SimpleComponent");
        
        List list = (List)m_tags.get(tag);
        if (list == null) {
            list = new ArrayList();
            m_tags.put(tag, list);
        }

        list.add(component);
    }
    
    /** 
     * Retrieves an iterator for all the known position tags
     * in this layout.
     * 
     * @return an iterator of position tags
     */
    public Iterator getPositionTags() {
        return m_tags.keySet().iterator();
    }
    
    /**
     * Retrieves an iterator for all components with
     * a specified tag.
     */
    public Iterator getComponents(String tag) {
        List list = (List)m_tags.get(tag);
        Assert.exists(list, List.class);
        return list.iterator();
    }
}
