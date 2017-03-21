/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.list;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.PageState;

/**
 * The default renderer for list items in a {@link
 * List}. Used by the <code>List</code> component if no renderer is given
 * explicitly.
 *
 * @author David Lutterkort
 */
public class DefaultListCellRenderer implements ListCellRenderer {

    /**
     * Return a component that has been configured to display
     * the specified value. If <code>isSelected</code> is true, returns a
     * bolded <code>Label</code> containing <code>value.toString()</code>. If
     * <code>isSelected</code> is not true, returns a
     * <code>ControlLink</code> labelled with
     * <code>value.toString()</code>. When the user clicks on the link, that
     * item becomes selected.  
     */
    @Override
    public Component getComponent(final List list, 
                                  final PageState state, 
                                  final Object value,
                                  final String key, 
                                  final int index, 
                                  final boolean isSelected)
    {
        final Label label = new Label(value.toString());
        if (isSelected) {
            label.setFontWeight(Label.BOLD);
            return label;
        }
        return new ControlLink(label);
    }
}
