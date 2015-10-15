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

import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.LockableImpl;


/**
 * Displays a list of label-value pairs that
 * represent the properties of some object. For example, a
 * typical <code>PropertySheet</code> may look like this:
 * <p>
 * <table cellpadding=4 cellspacing=0 border=0>
 * <tr><th>First Name:</th><td>Stanislav</td></tr>
 * <tr><th>Last Name:</th><td>Freidin</td></tr>
 * <tr><th>Mission:</th><td>Sleep</td></tr>
 * </table>
 * <p>
 * This class relies on the {@link PropertySheetModelBuilder} to
 * supply it with the right {@link PropertySheetModel} during
 * each request. It is up to the user to provide the right
 * builder.
 * <p>
 *
 * @author Stanislav Freidin
 * @version $Id: PropertySheet.java 287 2005-02-22 00:29:02Z sskracic $
 *
 */
public class PropertySheet extends Table {


    private PropertySheetModelBuilder m_builder;

    /**
     * Constructs a new <code>PropertySheet</code>.
     *
     * @param modelBuilder the property sheet model builder
     *   that is responsible for building the property
     *   sheet model
     *
     */
    public PropertySheet(PropertySheetModelBuilder modelBuilder) {
        super(new PSTMBAdapter(modelBuilder), new Object[]{"Label", "Value"});
        super.setHeader(null);

        super.getColumn(0).setCellRenderer(new GlobalizedLabelCellRenderer(Label.BOLD));
        super.getColumn(1).setCellRenderer(new StringLabelCellRenderer(null));
    }

    /**
     * Constructs a new <code>PropertySheet</code> and sets the output
     * escape value.
     *
     * @param modelBuilder the property sheet model builder
     *   that is responsible for building the property
     *   sheet model
     * @param valueOutputEscape the value of the label-value
     *   pair's output-escaping
     *
     */
    public PropertySheet(PropertySheetModelBuilder modelBuilder, boolean valueOutputEscape) {
        super(new PSTMBAdapter(modelBuilder), new Object[]{"Label", "Value"});
        super.setHeader(null);

        super.getColumn(0).setCellRenderer(new GlobalizedLabelCellRenderer(Label.BOLD));
        super.getColumn(1).setCellRenderer(new StringLabelCellRenderer(valueOutputEscape));
    }

    /**
     * Returns the {@link PropertySheetModelBuilder}.
     * @return the {@link PropertySheetModelBuilder}.
     */
    public PropertySheetModelBuilder getPropertySheetModelBuilder() {
        return m_builder;
    }

    // Convert a PropertySheetModelBuilder to a TableModelBuilder
    private static class PSTMBAdapter
        extends LockableImpl implements TableModelBuilder {

        private PropertySheetModelBuilder m_builder;

        public PSTMBAdapter(PropertySheetModelBuilder b) {
            m_builder = b;
        }

        public TableModel makeModel(Table t, PageState s) {
            return new TableModelAdapter(
                                         m_builder.makeModel((PropertySheet)t, s)
                                         );
        }

        public void lock() {
            m_builder.lock();
            super.lock();
        }
    }

    // Wraps a PropertySheetModel
    private static class TableModelAdapter implements TableModel {

        private PropertySheetModel m_model;
        private int m_row;

        public TableModelAdapter(PropertySheetModel model) {
            m_model = model;
            m_row = -1;
        }

        public int getColumnCount() { return 2; }

        public boolean nextRow() {
            m_row++;
            return m_model.nextRow();
        }

        public Object getElementAt(int columnIndex) {
            if(columnIndex == 0) {
                return m_model.getGlobalizedLabel();
            } else {
                return m_model.getValue();
            }
        }

        public Object getKeyAt(int columnIndex) {
            return new Integer(m_row);
        }

        public PropertySheetModel getPSModel() {
            return m_model;
        }
    };

    // Renders strings as labels
    public static class StringLabelCellRenderer implements TableCellRenderer {

        private String m_weight;
        private boolean m_outputEscape = false;

        public StringLabelCellRenderer(String weight) {
            m_weight = weight;
        }

        public StringLabelCellRenderer(boolean outputEscape) {
            m_outputEscape = outputEscape;
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            Label t = getLabel(value);
            //t.setOutputEscaping(false);
            t.setOutputEscaping(m_outputEscape);
            if(m_weight != null) {
                t.setFontWeight(m_weight);
            }
            return t;
        }

        protected Label getLabel(Object value) {
            return new Label((String)value);
        }
    }

    // Renders strings as labels
    public static class GlobalizedLabelCellRenderer extends StringLabelCellRenderer {

        public GlobalizedLabelCellRenderer(String weight) {
            super(weight);
        }

        public GlobalizedLabelCellRenderer(boolean outputEscape) {
            super(outputEscape);
        }

        protected Label getLabel(Object value) {
            return new Label((GlobalizedMessage)value);
        }
    }

    /**
     * An empty {@link PropertySheetModel}.
     */
    public static final PropertySheetModel EMPTY_MODEL =
        new PropertySheetModel() {
            public boolean nextRow() { return false; }
            public String getLabel() {
                throw new IllegalStateException("The model is empty");
            }
            public GlobalizedMessage getGlobalizedLabel() {
                throw new IllegalStateException((String) GlobalizationUtil.globalize("bebop.the_model_is_empty").localize());
            }
            public String getValue() {
                throw new IllegalStateException("The model is empty");
            }
        };

}
