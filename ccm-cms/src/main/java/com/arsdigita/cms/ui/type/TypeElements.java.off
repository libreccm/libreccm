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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * This class contains the component to generate a table
 * of elements for a particular content type
 */
public class TypeElements extends BoxPanel {

    private static Logger s_log =
                          Logger.getLogger(TypeElements.class);
    private SingleSelectionModel m_types;
    private Table m_elementsTable;
    private TableColumn m_removeColumn;

    public TypeElements(SingleSelectionModel m) {
        super();

        m_types = m;

        m_elementsTable = makeElementsTable();

        m_removeColumn = m_elementsTable.getColumn(3);
        m_removeColumn.setCellRenderer(new RemoveCellRenderer());
        m_removeColumn.setAlign("center");

        m_elementsTable.addTableActionListener(new ElementRemover());

        add(m_elementsTable);
    }

    public void register(Page p) {
        p.addComponent(this);
    }

    public Table getTable() {
        return m_elementsTable;
    }

    /**
     * creates and returns a content type
     */
    private ContentType makeType(BigDecimal typeId) {
        ContentType type = null;
        try {
            type = new ContentType(typeId);
        } catch (DataObjectNotFoundException e) {
            UncheckedWrapperException.throwLoggedException(getClass(), "Unable to make content type for id: "
                                                                       + typeId,
                                                           e);
        }
        return type;
    }

    /**
     * Produce remove links.
     */
    private static class RemoveCellRenderer implements TableCellRenderer {

        private static final Logger logger = Logger.getLogger(RemoveCellRenderer.class);
        private static Label s_noAction;
        private static ControlLink s_link;

        static {
            logger.debug("Static initializer is starting...");
            s_noAction = new Label("&nbsp;", false);
            s_noAction.lock();
            s_link = new ControlLink(new Label(GlobalizationUtil.globalize(
                    "cms.ui.type.element.delete")));
            s_link.setConfirmation("Permanently remove this element?");
            logger.debug("Static initalizer finished.");
        }

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if (((Boolean) value).booleanValue()) {
                return s_link;
            } else {
                return s_noAction;
            }
        }
    }

    // Removes an element
    private class ElementRemover extends TableActionAdapter {

        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn().intValue();

            if (m_removeColumn != m_elementsTable.getColumn(col)) {
                return;
            }

            PageState s = e.getPageState();
            DynamicObjectType dot = getDynamicObjectType(s);
            String element = e.getRowKey().toString();

            dot.removeAttribute(element);
            dot.save();
        }
    }

    private DynamicObjectType getDynamicObjectType(PageState s) {
        BigDecimal typeId = new BigDecimal(m_types.getSelectedKey(s).toString());
        ContentType type = makeType(typeId);
        return new DynamicObjectType(type.getAssociatedObjectType());
    }

    /**
     *  creates and returns the list of elements of this udct
     *  by iterating through the declared properties of the associated
     *  dynamic object type
     *
     *  return  the table of elements of this type
     */
    private Table makeElementsTable() {

        final String[] headers = {"Name", "Element Type", "Multiplicity",
                                  "Remove"};

        TableModelBuilder b = new TableModelBuilder() {

            private boolean m_locked;

            public TableModel makeModel(final Table t, final PageState s) {

                return new TableModel() {

                    DynamicObjectType dot = getDynamicObjectType(s);
                    //NOTE: this only gets the non-inherited properties of
                    //      the object type
                    Iterator declaredProperties = dot.getObjectType().
                            getDeclaredProperties();
                    Property currentProperty = null;

                    public int getColumnCount() {
                        return headers.length;
                    }

                    public boolean nextRow() {
                        boolean next = declaredProperties.hasNext();
                        if (next) {
                            currentProperty =
                            (Property) declaredProperties.next();
                        }
                        return next;
                    }

                    public Object getElementAt(int columnIndex) {
                        if (currentProperty == null) {
                            throw new IllegalArgumentException();
                        }

                        switch (columnIndex) {
                            case 0:
                                return currentProperty.getName();
                            case 1:
                                String dataType = currentProperty.getType().
                                        getName();
                                if (dataType.equals("String")) {
                                    return "text";
                                } else if (dataType.equals("BigDecimal")) {
                                    return "number";
                                } else if (dataType.equals("Date")) {
                                    return "date";
                                } else {
                                    return dataType;
                                }
                            case 2:
                                if (currentProperty.isNullable()) {
                                    return "0 or 1";
                                } else if (currentProperty.isRequired()) {
                                    return "1";
                                } else if (currentProperty.isCollection()) {
                                    return "0 to n";
                                } else {
                                    return new Integer(currentProperty.
                                            getMultiplicity());
                                }
                            case 3:
                                return new Boolean(isRemovable());
                            default:
                                throw new IllegalArgumentException(
                                        "columnIndex exceeds "
                                        + "number of columns available");
                        }
                    }

                    public Object getKeyAt(int columnIndex) {
                        if (currentProperty == null) {
                            throw new IllegalArgumentException();
                        } else {
                            //uses the element name as key, unique for each row
                            return currentProperty.getName();
                        }
                    }

                    private boolean isRemovable() {
                        return true;
                    }
                };

            }

            public void lock() {
                m_locked = true;
            }

            public boolean isLocked() {
                return m_locked;
            }
        };

        Table result = new Table(b, headers);
        CMSContainer ifemptable = new CMSContainer();
        ifemptable.setClassAttr("emptyTypeElementsTable");
        result.setEmptyView(ifemptable);
        //result.getColumn(0).setCellRenderer(new DefaultTableCellRenderer(true));
        result.setClassAttr("ContentTypeElementsTable");
        return result;

    }
}
