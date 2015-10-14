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

import static com.arsdigita.bebop.Component.*;

import java.util.Iterator;

import javax.servlet.ServletException;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;

import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

/**
 * This class is used by {@link Table} in order to maintain its headers.
 * 
 * <code>TableHeader</code> is responsible for setting the control event 
 * in order to notify the {@link Table} when one of the column headers 
 * is clicked.
 *
 * @author David Lutterkort
 * @version $Id$
 */
public class TableHeader extends SimpleComponent {


    /**
     * The control event when the user clicks on a column header.
     */
    public static final String HEAD_EVENT = "head";

    private TableCellRenderer m_defaultRenderer;

    private TableColumnModel m_columnModel;

    private Table m_table;

    private EventListenerList m_listeners;

    /**
     * Create a new <code>TableHeader</code>
     */
    public TableHeader() {
        this(new DefaultTableColumnModel());
    }

    /**
     * Create a new <code>TableHeader</code>
     *
     * @param model the {@link TableColumnModel} that the header
     *              will use in order to generate and maintain the
     *              column headers.
     */
    public TableHeader(TableColumnModel model) {
        m_columnModel = model;
        m_defaultRenderer = new DefaultTableCellRenderer();
        m_listeners = new EventListenerList();
    }

    /**
     * Add an {@link TableActionListener} to the header.
     * The listener will be fired whenever this header is
     * selected by the user.
     *
     * @param l the {@link TableActionListener} to add
     */
    public void addTableActionListener(TableActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.add(TableActionListener.class, l);
    }

    /**
     * Remove a {@link TableActionListener} from the header
     *
     *@param l the {@link TableActionListener} to remove
     */
    public void removeTableActionListener(TableActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(TableActionListener.class, l);
    }


    /**
     * Notify all listeners that the header was selected
     *
     * @param state the page state
     * @param rowKey the key of the selected row, as returned by
     *   <code>Table.getRowSelectionModel().getSelectedKey(state)</code>.
     *   this key may be null.
     * @param column The index of the selected column
     *
     */
    protected void fireHeadSelected(PageState state,
                                    Object rowKey, Integer column) {
        Iterator
            i=m_listeners.getListenerIterator(TableActionListener.class);
        TableActionEvent e = null;

        while (i.hasNext()) {
            if ( e == null ) {
                e = new TableActionEvent(this, state, rowKey, column);
            }
            ((TableActionListener) i.next()).headSelected(e);
        }
    }


    /**
     * Respond to the current event by selecting the current
     * column
     *
     * @param s the page state
     */
    public void respond(PageState s) throws ServletException {
        String event = s.getControlEventName();
        if ( HEAD_EVENT.equals(event) ) {
            String value = s.getControlEventValue();
            // FIXME: ParameterData allows its value to be set to anything, even
            // if it isn't compatible with the ParameterModel
            // We need to change ParameterModel/Data to fail earlier on bad data
            Integer col = new Integer(value);
            getColumnModel().getSelectionModel().setSelectedKey(s, col);
            fireHeadSelected(s, null, col);
        } else {
            throw new ServletException("Unknown event '" + event + "'");
        }
    }

    /**
     * @return the parent {@link Table}
     */
    public final Table getTable() {
        return m_table;
    }

    /**
     * Set the parent {@link Table}
     *
     * @param v the parent table
     */
    public void setTable(Table  v) {
        Assert.isUnlocked(this);
        m_table = v;
    }

    /**
     * @return the {@link TableColumnModel} which maintains the headers
     */
    public final TableColumnModel getColumnModel() {
        return m_columnModel;
    }

    /**
     * Set the {@link TableColumnModel} which will maintain the headers
     *
     * @param v the new {@link TableColumnModel}
     */
    public void setColumnModel(TableColumnModel  v) {
        Assert.isUnlocked(this);
        m_columnModel = v;
    }

    /**
     *  @return the default {@link TableCellRenderer} for this header
     */
    public final TableCellRenderer getDefaultRenderer() {
        return m_defaultRenderer;
    }

    /**
     * Set the default {@link TableCellRenderer} for this header.
     * Header cells will be rendered with this renderer unless
     * the column model specifies an alternative renderer.
     *
     * @param v the new default renderer
     */
    public void setDefaultRenderer(TableCellRenderer  v) {
        Assert.isUnlocked(this);
        m_defaultRenderer = v;
    }

    /**
     * Generate the XML for this header. The XML will be of the form
     * <blockquote><pre><code>
     * &lt;bebop:thead&gt;
     *   &lt;bebop:cell&gt;...&lt;/bebop:cell&gt;
     *   ...
     * &lt;/bebop:thead&gt;
     * </code><pre></blockquote>
     *
     * @param s the page state
     * @param p the parent element
     */
    public void generateXML(PageState s, Element p) {
        if ( isVisible(s) ) {
            Element thead = p.newChildElement("bebop:thead", BEBOP_XML_NS);
            exportAttributes(thead);

            for (int i=0; i < m_columnModel.size(); i++) {
                TableColumn t = m_columnModel.get(i);

                if ( t.isVisible(s) ) {
                    TableCellRenderer r = t.getHeaderRenderer();

                    if ( r == null ) {
                        r = getDefaultRenderer();
                    }

                    boolean isSel = isSelected(s, t.getHeaderKey(), i);

                    Component c = r.getComponent(getTable(), s, t.getHeaderValue(), isSel,
                                                 t.getHeaderKey(), -1, i);

                    if (c != null) {
                        // supports having a table header disappear
                        // completely, mainly useful for the odd special case
                        // where a second-row element is being displayed.

                        Element cell = thead.newChildElement("bebop:cell", BEBOP_XML_NS);
                        t.exportHeadAttributes(cell);

                        // Mark the cell as selected if it is selected
                        if(isSel) {
                            cell.addAttribute("selected", "1");
                        }

                        // I added this check so that a table which is not
                        // added to the Page can still be used to render
                        // table XML.

                        boolean tableIsRegisteredWithPage =
                            s.getPage().stateContains(getControler());

                        if (tableIsRegisteredWithPage) {
                            s.setControlEvent(getControler(), HEAD_EVENT,
                                              String.valueOf(i));
                        }

                        c.generateXML(s, cell);

                        if (tableIsRegisteredWithPage) {
                            s.clearControlEvent();
                        }
                    }
                }
            }
        }
    }

    protected Component getControler() {
        return this;
    }

    /**
     * Determine whether the given column is selected. This information
     * will be passed to the {@link TableCellRenderer} for this header.
     *
     * @param s the page state
     * @param key the header key for the column as returned by
     *   <code>TableColumn.getHeaderKey()</code>
     * @param column the index of the column to test
     */
    protected boolean isSelected(PageState s, Object key, int column) {
    	if (getTable().getColumnSelectionModel() == null) {
            return false;
    	}
        Object sel = getTable()
            .getColumnSelectionModel().getSelectedKey(s);
        if(sel == null) {
            return false;
        }
        return (column == ((Integer)sel).intValue());
    }
}
