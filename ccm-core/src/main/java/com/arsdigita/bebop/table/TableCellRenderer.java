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
package com.arsdigita.bebop.table;


import com.arsdigita.bebop.util.GlobalizationUtil ; 

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;

/**
 * Render one cell in a table. The renderer returns a component whose
 * {@link com.arsdigita.bebop.Component#generateXML generateXML} method
 * will be called by the table to include the data for the cell in the
 * table's output.
 *
 * <p> The table uses the returned component only until it calls the cell
 * renderer again, so that cell renderers may reuse the same object in
 * subsequent calls to {@link #getComponent getComponent}.
 *
 * <p> As an example, consider the following implementation of a table cell
 * renderer, which simply converts the passed in <code>value</code> to a
 * string and encloses it in a label. The cell renderer converts the passed
 * in value to a string and uses that to set the text to display for a
 * label. If the value is selected, the label is bolded. As an added twist,
 * the table cell renderer uses only one label for each thread from which
 * it is accessed (rather than creating a new <code>Label</code> for each
 * call) by storing the label in a <code>ThreadLocal</code> variable.
 *
 * <pre>
 * public class MyTableCellRenderer implements TableCellRenderer {
 *
 *   private ThreadLocal m_label;
 *
 *   public MyTableCellRenderer() {
 *     m_label = new ThreadLocal() {
 *  protected Object initialValue() {
 *    return new Label("");
 *  }
 *       };
 *   }
 *
 *   public Component getComponent(Table table, PageState state, Object value,
 *              boolean isSelected, Object key,
 *              int row, int column) {
 *     Label l = (Label) m_label.get();
 *     l.setLabel(value.toString());
 *     l.setFontWeight( isSelected ? Label.BOLD : null );
 *     return l;
 *   }
 * }
 * </pre>
 *
 * @author David Lutterkort
 * @see com.arsdigita.bebop.Table Table
 * @version $Id$
 */
public interface TableCellRenderer {

    /**
     * Return a component with the visual representation for the passed in
     * <code>key</code> and <code>value</code>.
     *
     * <p> The table sets the control event prior to calling this method, so
     * that any control link returned as the component will, when clicked,
     * cause the table to fire a <code>TableActionEvent</code> whose
     * <code>getRowKey()</code> and <code>getColumn()</code> return the
     * values of <code>key</code> and <code>column</code>. A simple cell
     * renderer that achieves this would implement this method in the
     * following way:
     * <pre>
     *   public Component getComponent(Table table, PageState state, Object value,
     *              boolean isSelected, Object key,
     *              int row, int column) {
     *     return new ControlLink(value.toString());
     *   }
     * </pre>
     *
     * <p> The <code>column</code> refers to a column in the table's {@link
     * TableColumnModel}, i.e. the visual column on the screen, and not the
     * table's representation of the underlying data in the {@link
     * TableModel}.
     *
     * @param table the table requesting the rendering.
     * @param state represents the state of the current request.
     * @param value the data element to render as returned by the table
     * model's {@link TableModel#getElementAt getElementAt(column)}.
     * @param isSelected true if this item is selected.
     * @param key the key identifying this row (and possibly column) as
     * returned by the table model's {@link TableModel#getKeyAt
     * getKeyAt(column)}
     * @param row the number of the row in the table, the first row has
     * number <code>0</code>.
     * @param column the number of the table column.
     * @return the component that should be used to render the
     * <code>value</code>.
     * @pre table != null
     * @pre state != null
     * @pre value != null
     * @pre key != null
     * @pre row >= 0
     * @pre column >= 0 && column < table.getColumnModel().size()
     * @post return != null
     * @see TableColumnModel
     */
    Component getComponent(Table table, PageState state, Object value,
                           boolean isSelected, Object key,
                           int row, int column);
}
