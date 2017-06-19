/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import java.util.Iterator;

import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.libreccm.l10n.LocalizedString;

/**
 * This extends DomainObjectPropertySheet and has a lot of duplicate code from
 * it because it uses so many private inner classes
 *
 */
public class TextAssetBodyPropertySheet extends DomainObjectPropertySheet {

    /**
     * Construct a new TextAssetBodyPropertySheet
     *
     * @param objModel The selection model which feeds domain objects to this
     *                 property sheet.
     * @param selectedLanguageParam
     *
     */
    public TextAssetBodyPropertySheet(
        final ItemSelectionModel objModel,
        final StringParameter selectedLanguageParam) {

        super(objModel, false, selectedLanguageParam);
        setModelBuilder(new TMBAdapter(new DomainObjectModelBuilder()));
        getColumn(1).setCellRenderer(new TextAssetBodyLabelCellRenderer());
    }

    // Build up the object properties model from the iterator over all properties
    private static class TextAssetBodyPropertiesModel implements
        PropertySheetModel {

//        public final static String MIME_TYPE_KEY
//                                   = TextAssetBodyLabelCellRenderer.MIME_TYPE_KEY;
        private static final String ERROR = "No current property. "
                                                + "Make sure that nextRow() was "
                                            + "called at least once.";

        private LocalizedString textAsset;
        private PageState pageState;
        private Iterator<Property> properties;
        private Property currentProperty;

        public TextAssetBodyPropertiesModel(final LocalizedString textAsset,
                                            final Iterator<Property> properties,
                                            final PageState pageState) {
            this.textAsset = textAsset;
            this.properties = properties;
            this.pageState = pageState;
            currentProperty = null;
        }

        @Override
        public boolean nextRow() {

            if (properties.hasNext()) {
                currentProperty = properties.next();
                return true;
            } else {
                return false;
            }
        }

        /**
         * @deprecated use getGlobalizedLabel() instead
         */
        @Override
        @Deprecated
        public String getLabel() {
            return getGlobalizedLabel().getKey();
        }

        @Override
        public GlobalizedMessage getGlobalizedLabel() {
            if (currentProperty == null) {
                throw new IllegalStateException(ERROR);
            }
            return currentProperty.getGlobalizedLabel();
        }

        @Override
        public String getValue() {
            return getObjectValue().toString();
        }

        public Object getObjectValue() {
            if (currentProperty == null) {
                throw new IllegalStateException(ERROR);
            }

            return textAsset;
        }

        public String getAttribute() {
            return currentProperty.getAttribute();
        }

    }

    // Builds an TextAssetBodyPropertiesModel
    private static class DomainObjectModelBuilder extends LockableImpl
        implements PropertySheetModelBuilder {

        @Override
        public PropertySheetModel makeModel(final PropertySheet sheet,
                                            final PageState state) {

            TextAssetBodyPropertySheet propSheet
                                       = (TextAssetBodyPropertySheet) sheet;
            throw new UnsupportedOperationException("ToDo");
        }

    }

    // These are both from PropertySheet
    // Convert a PropertySheetModelBuilder to a TableModelBuilder
    private static class TMBAdapter
        extends LockableImpl implements TableModelBuilder {

        private final PropertySheetModelBuilder modelBuilder;

        public TMBAdapter(final PropertySheetModelBuilder builder) {
            this.modelBuilder = builder;
        }

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            return new TableModelAdapter(
                (TextAssetBodyPropertiesModel) modelBuilder.makeModel(
                    (PropertySheet) table, state));
        }

        @Override
        public void lock() {
            modelBuilder.lock();
            super.lock();
        }

    }

    // Wraps a PropertySheetModel
    private static class TableModelAdapter implements TableModel {

        private final TextAssetBodyPropertiesModel propertiesModel;
        private int row;

        public TableModelAdapter(
            final TextAssetBodyPropertiesModel propertiesModel) {

            this.propertiesModel = propertiesModel;
            row = -1;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean nextRow() {
            row++;
            return propertiesModel.nextRow();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            if (columnIndex == 0) {
                return propertiesModel.getGlobalizedLabel();
            } else {
                return propertiesModel.getObjectValue();
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return propertiesModel.getAttribute();
        }

        public PropertySheetModel getPSModel() {
            return propertiesModel;
        }

    }

}
