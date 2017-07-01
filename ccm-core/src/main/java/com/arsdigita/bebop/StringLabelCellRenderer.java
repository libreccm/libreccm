/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.bebop;

import com.arsdigita.bebop.table.TableCellRenderer;

// Renders strings as labels

public class StringLabelCellRenderer implements TableCellRenderer {

    private String weight;
    private boolean outputEscape = false;

    public StringLabelCellRenderer(final String weight) {
        this.weight = weight;
    }

    public StringLabelCellRenderer(boolean outputEscape) {
        this.outputEscape = outputEscape;
    }

    @Override
    public Component getComponent(final Table table, 
                                  final PageState state, Object value,
                                  final boolean isSelected, 
                                  final Object key, 
                                  final int row,
                                  final int column) {
        final Label target = getLabel(value);
        target.setOutputEscaping(outputEscape);
        if (weight != null) {
            target.setFontWeight(weight);
        }
        return target;
    }

    protected Label getLabel(Object value) {
        return new Label((String) value);
    }
    
}
