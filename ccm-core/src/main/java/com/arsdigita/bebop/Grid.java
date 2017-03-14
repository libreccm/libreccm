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
package com.arsdigita.bebop;


import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.util.LockableImpl;


/**
 * Displays a {@link ListModel} as a grid (that is, a  {@link Table})
 * of given width.
 *
 * @version $Id: Grid.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Grid extends Table {

    private int m_cols;

    /**
     * Constructs a new <code>Grid</code>.
     * <p>
     *
     * @param builder the {@link ListModelBuilder} that provides
     *  the grid with data
     *
     * @param numCols the number of columns in the grid
     */
    public Grid(ListModelBuilder builder, int numCols) {
        super(new GridModelBuilder(builder, numCols), new DefaultTableColumnModel());
        m_cols = numCols;

        setHeader(null);

        TableColumnModel cols = getColumnModel();
        for(int i=0; i<numCols; i++) {
            cols.add(new TableColumn(i));
        }

        setClassAttr("grid");
        setWidth("100%");
        // Ignore null values
        setDefaultCellRenderer(new DefaultTableCellRenderer(true) {
                @Override
                public Component getComponent(Table table, PageState state, Object value,
                                              boolean isSelected, Object key,
                                              int row, int column) {
                    if(value == null)
                        return new Label("&nbsp;", false);
                    else
                        return super.getComponent(table, state, value, isSelected, key, row, column);
                }
            });
    }

    /**
     * @param builder the {@link ListModelBuilder} that provides
     *  the grid with data
     */
    public void setModelBuilder(ListModelBuilder builder) {
        super.setModelBuilder(new GridModelBuilder(builder, getColumnCount()));
    }

    /**
     * @return the number of columns in the grid.
     */
    public int getColumnCount() {
        return m_cols;
    }

    /**
     * Converts a ListModel to a TableModel
     */
    private static class GridModelBuilder extends LockableImpl
        implements TableModelBuilder {

        private ListModelBuilder m_builder;
        private int m_cols;

        public GridModelBuilder(ListModelBuilder builder, int cols) {
            super();
            m_builder = builder;
            m_cols = cols;
        }

        public TableModel makeModel(Table t, PageState s) {
            //XXX FIXME: The creation of a new List() below is a
            //Hack to compile all...remove and fix.
            //This is because makeModel requires a List arg.
            //Should add a List setter function to Grid class, and
            //initialize to null, then pass in null below if necessary...
            //Christian: I will let your review team ponder this
            //proposed change, and if approved, please assign the
            //ticket to me!  -jbp
            List l = new List();
            ListModel m = m_builder.makeModel(l,s);
            return new GridTableModel(m, m_cols);
        }

        @Override
        public void lock() {
            m_builder.lock();
            super.lock();
        }

    }


}
