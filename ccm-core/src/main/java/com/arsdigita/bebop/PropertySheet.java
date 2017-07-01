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
 * Displays a list of label-value pairs that represent the properties of some
 * object. For example, a typical <code>PropertySheet</code> may look like this:
 * <p>
 * <table cellpadding=4 cellspacing=0 border=0>
 * <tr><th>First Name:</th><td>Stanislav</td></tr>
 * <tr><th>Last Name:</th><td>Freidin</td></tr>
 * <tr><th>Mission:</th><td>Sleep</td></tr>
 * </table>
 * <p>
 * This class relies on the {@link PropertySheetModelBuilder} to supply it with
 * the right {@link PropertySheetModel} during each request. It is up to the
 * user to provide the right builder.
 * <p>
 *
 * @author Stanislav Freidin
 *
 */
public class PropertySheet extends Table {

    /**
     * Constructs a new <code>PropertySheet</code>.
     *
     * @param modelBuilder the property sheet model builder that is responsible
     *                     for building the property sheet model
     *
     */
    public PropertySheet(final PropertySheetModelBuilder modelBuilder) {

        this(modelBuilder, false);
    }

    /**
     * Constructs a new <code>PropertySheet</code> and sets the output escape
     * value.
     *
     * @param modelBuilder      the property sheet model builder that is
     *                          responsible for building the property sheet
     *                          model
     * @param valueOutputEscape the value of the label-value pair's
     *                          output-escaping
     *
     */
    public PropertySheet(final PropertySheetModelBuilder modelBuilder,
                         final boolean valueOutputEscape) {

        super(new PSTMBAdapter(modelBuilder), new Object[]{"Label", "Value"});
        super.setHeader(null);

        super
            .getColumn(0)
            .setCellRenderer(new GlobalizedLabelCellRenderer(Label.BOLD));
        super.getColumn(1).setCellRenderer(new StringLabelCellRenderer(
            valueOutputEscape));
    }

    // Convert a PropertySheetModelBuilder to a TableModelBuilder
    private static class PSTMBAdapter
        extends LockableImpl implements TableModelBuilder {

        private PropertySheetModelBuilder modelBuilder;

        public PSTMBAdapter(final PropertySheetModelBuilder modelBuilder) {
            this.modelBuilder = modelBuilder;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            return new TableModelAdapter(
                modelBuilder.makeModel((PropertySheet) table, state)
            );
        }

        @Override
        public void lock() {
            modelBuilder.lock();
            super.lock();
        }

    }

    // Wraps a PropertySheetModel
    private static class TableModelAdapter implements TableModel {

        private final PropertySheetModel propertySheetModel;
        private int currentRow;

        public TableModelAdapter(final PropertySheetModel propertySheetModel) {
            this.propertySheetModel = propertySheetModel;
            currentRow = -1;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            currentRow++;
            return propertySheetModel.nextRow();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            if (columnIndex == 0) {
                return propertySheetModel.getGlobalizedLabel();
            } else {
                return propertySheetModel.getValue();
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return currentRow;
        }

        public PropertySheetModel getPSModel() {
            return propertySheetModel;
        }

    };

}
