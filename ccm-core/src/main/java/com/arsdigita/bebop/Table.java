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

import static com.arsdigita.bebop.Component.*;

import com.arsdigita.bebop.event.EventListenerList;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableHeader;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;

import static com.arsdigita.bebop.util.BebopConstants.*;

import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.Iterator;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * Displays statically or dynamically generated data in tabular form. 
 * The data is retrieved from a <code>TableModel</code>.  
 * 
 * <p>
 * This class is similar to the {@link List} class, but it has two dimensions.
 * The table consists of a {@link TableModelBuilder}, a {@link TableColumnModel},
 * a {@link TableHeader} and a {@link TableCellRenderer} for each column.
 * <p>
 *
 * A table that represents a static matrix can be created fairly quickly:
 * <blockquote><pre><code> String[][] data = {
 *   {"Stas", "Freidin"},
 *   {"David", "Lutterkort"}
 * };
 *
 * String[] headers = {"First Name", "Last Name"};
 *
 * Table myTable = new Table(data, headers);</code></pre></blockquote>
 * <p>
 *
 * However, tables are most often used to represent database queries, not static
 * data. For these tables, the {@link TableModelBuilder} class should be used
 * to supply the <code>Table</code> class with data.
 * The {@link TableModelBuilder} class will execute the database query and
 * return a {@link TableModel}, which wraps the query.
 * <p>
 *
 * The content in the cells is rendered by the {@link TableCellRenderer} that
 * is set for the {@link TableColumn} to which the cell belongs.
 * 
 * If the <code>TableCellRenderer</code> has not been set, the 
 * <code>TableCellRenderer</code> for the <code>Table</code> is used.
 * By default, the <code>Table</code> class uses an inactive instance of the
 * {@link DefaultTableCellRenderer} (cell content is displayed as {@link Label}s).
 * However, if an active <code>DefaultTableCellRenderer</code> is used, the
 * cells in the table appear as links. When the user clicks a link, the
 * <code>Table</code>'s action listeners will be fired.
 * 
 * <P>
 * The currently selected cell is represented by two {@link SingleSelectionModel}s -
 * one model for the row and one model for the column. Typically, the selected
 * row is identified by a string key and the selected column is identified by
 * an integer.
 *
 * @see TableModel
 * @see TableColumnModel
 *
 * @author David Lutterkort 
 * @version $Id$
 */
public class Table extends SimpleComponent {

    private static final Logger logger = Logger.getLogger(Table.class);
    // Names for HTML Attributes
    private static final String WIDTH = "width";
    private static final String CELL_SPACING = "cellspacing";
    private static final String CELL_PADDING = "cellpadding";
    private static final String BORDER = "border";
    private static final String SELECTED_ROW = "row";
    /**
     * The control event when the user selects one table cell.
     * This control event will only be used when
     */
    protected static final String CELL_EVENT = "cell";
    protected static final char SEP = ' ';
    private TableModelBuilder m_modelBuilder;
    private TableColumnModel m_columnModel;
    private TableHeader m_header;
    private RequestLocal m_tableModel;
    private SingleSelectionModel m_rowSelectionModel;
    /**
     * A listener to forward headSelected events originating from the
     * TableHeader. This will be null until somebody actually registers a
     * TableActionListener from the outside.
     */
    private TableActionListener m_headerForward;
    private EventListenerList m_listeners;
    private TableCellRenderer m_defaultCellRenderer;
    private Component m_emptyView;
    private boolean m_striped = false;

    /**
     * Constructs a new, empty table.
     */
    public Table() {
        this(new Object[0][0], new Object[0]);
    }

    /**
     * Constructs a static table with the specified column headers,
     * and pre-fills it with data.
     *
     * @param data a matrix of objects that will serve as static data
     *   for the table cells
     *
     * @param headers an array of string labels for the table headers
     */
    public Table(Object[][] data, Object[] headers) {
        this(new MatrixTableModelBuilder(data), headers);
    }

    /**
     * Constructs a table using a {@link TableModelBuilder}. The table
     * data will be generated dynamically during each request.
     *
     * @param b the {@link TableModelBuilder} that is responsible for
     *    instantiating a {@link TableModel} during each request
     *
     * @param headers an array of string labels for the table headers
     */
    public Table(TableModelBuilder b, Object[] headers) {
        this(b, new DefaultTableColumnModel(headers));
    }

    /**
     * Constructs a table using a {@link TableModelBuilder}. The table
     * data will be generated dynamically during each request. The
     * table's columns and headers will be provided by a
     * {@link TableColumnModel}.
     *
     * @param b the {@link TableModelBuilder} that is responsible for
     *          instantiating a {@link TableModel} during each request
     *
     * @param c the {@link TableColumnModel} that will maintain the
     *          columns and headers for this table
     */
    public Table(TableModelBuilder b, TableColumnModel c) {
        super();
        m_modelBuilder = b;
        m_columnModel = c;
        setHeader(new TableHeader(m_columnModel));
        m_rowSelectionModel =
        new ParameterSingleSelectionModel(new StringParameter(SELECTED_ROW));
        m_listeners = new EventListenerList();
        m_defaultCellRenderer = new DefaultTableCellRenderer();
        initTableModel();
    }

    // Events and listeners

    /**
     * Adds a {@link TableActionListener} to the table. The listener is
     * fired whenever a table cell is clicked.
     *
     * @param l the {@link TableActionListener} to be added
     */
    public void addTableActionListener(TableActionListener l) {
        Assert.isUnlocked(this);
        if (m_headerForward == null) {
            m_headerForward = createTableActionListener();
            if (m_header != null) {
                m_header.addTableActionListener(m_headerForward);
            }
        }
        m_listeners.add(TableActionListener.class, l);
    }

    /**
     * Removes a {@link TableActionListener} from the table.
     *
     * @param l the {@link TableActionListener} to be removed
     */
    public void removeTableActionListener(TableActionListener l) {
        Assert.isUnlocked(this);
        m_listeners.remove(TableActionListener.class, l);
    }

    /**
     * Fires event listeners to indicate that a new cell has been
     * selected in the table.
     *
     * @param state the page state
     * @param rowKey the key that identifies the selected row
     * @param column the integer index of the selected column
     */
    protected void fireCellSelected(PageState state,
                                    Object rowKey, Integer column) throws FormProcessException {
        Iterator i = m_listeners.getListenerIterator(TableActionListener.class);
        TableActionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new TableActionEvent(this, state, rowKey, column);
            }
            ((TableActionListener) i.next()).cellSelected(e);
        }
    }

    /**
     * Fires event listeners to indicate that a new header cell has been
     * selected in the table.
     *
     * @param state the page state
     * @param rowKey the key that identifies the selected row
     * @param column the integer index of the selected column
     */
    protected void fireHeadSelected(PageState state,
                                    Object rowKey, Integer column) {
        Iterator i = m_listeners.getListenerIterator(TableActionListener.class);
        TableActionEvent e = null;

        while (i.hasNext()) {
            if (e == null) {
                e = new TableActionEvent(this, state, rowKey, column);
            }
            ((TableActionListener) i.next()).headSelected(e);
        }
    }

    /**
     * Instantiates a new {@link TableActionListener} for this table.
     *
     * @return a new {@link TableActionListener} that should be used
     *   only for this table.
     *
     */
    protected TableActionListener createTableActionListener() {
        return new TableActionAdapter() {
            @Override
            public void headSelected(TableActionEvent e) {
                fireHeadSelected(e.getPageState(), e.getRowKey(), e.getColumn());
            }
        };
    }

    /**
     * @return the {@link TableColumnModel} for this table.
     */
    public final TableColumnModel getColumnModel() {
        return m_columnModel;
    }

    /**
     * Sets a new {@link TableColumnModel} for the table.
     *
     * @param v the new {@link TableColumnModel}
     */
    public void setColumnModel(TableColumnModel v) {
        Assert.isUnlocked(this);
        m_columnModel = v;
    }

    /**
     * @return the {@link TableModelBuilder} for this table.
     */
    public final TableModelBuilder getModelBuilder() {
        return m_modelBuilder;
    }

    /**
     * Sets a new {@link TableModelBuilder} for the table.
     *
     * @param v the new {@link TableModelBuilder}
     */
    public void setModelBuilder(TableModelBuilder v) {
        Assert.isUnlocked(this);
        m_modelBuilder = v;
    }

    /**
     * @return the {@link TableHeader} for this table. Could return null
     *    if the header is hidden.
     */
    public final TableHeader getHeader() {
        return m_header;
    }

    /**
     * Sets a new header for this table.
     *
     * @param v the new header for this table. If null, the header will be
     *          hidden.
     */
    public void setHeader(TableHeader v) {
        Assert.isUnlocked(this);
        if (m_headerForward != null) {
            if (m_header != null) {
                m_header.removeTableActionListener(m_headerForward);
            }
            if (v != null) {
                v.addTableActionListener(m_headerForward);
            }
        }
        m_header = v;
        if (m_header != null) {
            m_header.setTable(this);
        }
    }

    /**
     * @param i the numerical index of the column
     * @return the {@link TableColumn} whose index is i.
     */
    public TableColumn getColumn(int i) {
        return getColumnModel().get(i);
    }

    /**
     * Maps the colulumn at a new numerical index. This method
     * is normally used to rearrange the order of the columns in the
     * table.
     *
     * @param i the numerical index of the column
     * @param v the column that is to be mapped at i
     */
    public void setColumn(int i, TableColumn v) {
        getColumnModel().set(i, v);
    }

    /**
     * @return the {@link SingleSelectionModel} that is responsible
     *   for selecting the current row.
     */
    public final SingleSelectionModel getRowSelectionModel() {
        return m_rowSelectionModel;
    }

    /**
     * Specifies the {@link SingleSelectionModel} that will be responsible
     * for selecting the current row.
     *
     * @param v a {@link SingleSelectionModel}
     */
    public void setRowSelectionModel(SingleSelectionModel v) {
        Assert.isUnlocked(this);
        m_rowSelectionModel = v;
    }

    /**
     * @return the {@link SingleSelectionModel} that is responsible
     *   for selecting the current column.
     */
    public SingleSelectionModel getColumnSelectionModel() {
        return (getColumnModel() == null) ? null : getColumnModel().
                getSelectionModel();
    }

    /**
     * Specifies the {@link SingleSelectionModel} that will be responsible
     * for selecting the current column.
     *
     * @param v a {@link SingleSelectionModel}
     */
    public void setColumnSelectionModel(SingleSelectionModel v) {
        Assert.isUnlocked(this);
        // TODO: make sure table gets notified of changes
        getColumnModel().setSelectionModel(v);
    }

    /**
     * Clears the row and column selection models that the table holds.
     *
     * @param s represents the state of the current request
     * @post ! getRowSelectionModel().isSelected(s)
     * @post ! getColumnSelectionModel().isSelected(s)
     */
    public void clearSelection(PageState s) {
        getRowSelectionModel().clearSelection(s);
        getColumnSelectionModel().clearSelection(s);
    }

    /**
     * @return the default {@link TableCellRenderer}.
     */
    public final TableCellRenderer getDefaultCellRenderer() {
        return m_defaultCellRenderer;
    }

    /**
     * Specifies the default cell renderer. This renderer will
     * be used to render columns that do not specify their own
     * {@link TableCellRenderer}.
     *
     * @param v the default {@link TableCellRenderer}
     */
    public final void setDefaultCellRenderer(TableCellRenderer v) {
        m_defaultCellRenderer = v;
    }

    /**
     * @return the component that will be shown if the table is
     *   empty.
     */
    public final Component getEmptyView() {
        return m_emptyView;
    }

    /**
     * Sets the empty view. The empty view is the component that
     * is shown if the table is empty. Usually, the component
     * will be a simple label, such as <code>new Label("The table is empty")</code>.
     *
     * @param v a Bebop component
     */
    public final void setEmptyView(Component v) {
        m_emptyView = v;
    }

    // Set HTML table attributes
    /**
     *  
     * @return the HTML width of the table.
     */
    public String getWidth() {
        return getAttribute(WIDTH);
    }

    /**
     *  
     * @param v the HTML width of the table
     */
    public void setWidth(String v) {
        setAttribute(WIDTH, v);
    }

    /**
     *  
     * @return the HTML border of the table.
     */
    public String getBorder() {
        return getAttribute(BORDER);
    }

    /**
     *  
     * @param v the HTML border of the table
     */
    public void setBorder(String v) {
        setAttribute(BORDER, v);
    }

    public String getCellSpacing() {
        return getAttribute(CELL_SPACING);
    }

    /**
     *  
     * @param v the HTML width of the table
     */
    public void setCellSpacing(String v) {
        setAttribute(CELL_SPACING, v);
    }

    /**
     *  
     * @return the HTML cell spacing of the table.
     */
    public String getCellPadding() {
        return getAttribute(CELL_PADDING);
    }

    /**
     *  
     * @param v the HTML cell padding of the table
     */
    public void setCellPadding(String v) {
        setAttribute(CELL_PADDING, v);
    }

    /**
     * Processes the events for this table. This method will automatically
     * handle all user input to the table.
     *
     * @param s the page state
     * @throws javax.servlet.ServletException
     */
    @Override
    public void respond(PageState s) throws ServletException {
        String event = s.getControlEventName();
        String rowKey = null;
        Integer column = null;

        if (CELL_EVENT.equals(event)) {
            String value = s.getControlEventValue();
            SingleSelectionModel rowSel = getRowSelectionModel();
            SingleSelectionModel colSel = getColumnSelectionModel();
            int split = value.indexOf(SEP);
            rowKey = value.substring(0, split);
            column = new Integer(value.substring(split + 1));
            colSel.setSelectedKey(s, column);
            rowSel.setSelectedKey(s, rowKey);
            fireCellSelected(s, rowKey, column);
        } else {
            throw new ServletException("Unknown event '" + event + "'");
        }
    }

    /**
     * Registers the table with the containing page. The table will add the
     * state parameters of the row and column selection models, if they use
     * them, thus making the selection persist between requests.
     *
     * @param p the page that contains this table
     */
    @Override
    public void register(Page p) {
        ParameterModel m = getRowSelectionModel() == null ? null
                           : getRowSelectionModel().getStateParameter();
        if (m != null) {
            p.addComponentStateParam(this, m);
        }
        m = getColumnSelectionModel() == null ? null : getColumnSelectionModel().
                getStateParameter();
        if (m != null) {
            p.addComponentStateParam(this, m);
        }
    }

    /**
     * Returns an iterator over the header and all the columns. If the table
     * has no header, the iterator lists only the columns.
     *
     * @return an iterator over Bebop components.
     */
    @Override
    public Iterator children() {
        return new Iterator() {

            int pos = (getHeader() == null) ? -1 : -2;

            @Override
            public boolean hasNext() {
                return pos < getColumnModel().size() - 1;
            }

            @Override
            public Object next() {
                pos += 1;
                if (pos == -1) {
                    return getHeader();
                } else {
                    return getColumn(pos);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read-only iterator.");
            }
        };
    }

    /**
     * Determines whether a row is seleted.
     *
     * @param s the page state
     * @param rowKey the key that identifies the row
     * @return <code>true</code> if the row is currently selected;
     * <code>false</code> otherwise.
     */
    public boolean isSelectedRow(PageState s, Object rowKey) {
        if (rowKey == null || getRowSelectionModel() == null) {
            return false;
        }
        return getRowSelectionModel().isSelected(s)
               && rowKey.toString().equals(
                getRowSelectionModel().getSelectedKey(s).toString());
    }

    /**
     * Determines whether a column is selected.
     *
     * @param s the page state
     * @param column a key that identifes the column. Should be consistent
     *   with the type used by the column selection model.
     * @return <code>true</code> if the column is selected;
     * <code>false</code> otherwise.
     */
    public boolean isSelectedColumn(PageState s, Object column) {
        if (column == null || getColumnSelectionModel() == null) {
            return false;
        }
        return getColumnSelectionModel().isSelected(s)
               && column.toString().equals(
                getColumnSelectionModel().getSelectedKey(s).toString());
    }

    /**
     * Determines whether the cell addressed by the specified row key and
     * column number is selected in the request represented by the page
     * state.
     *
     * @param s represents the state of the page in the current request
     * @param rowKey the row key of the cell. The concrete type should agree
     * with the type used by the row selection model.
     * @param column the column of the cell. The concrete type should agree
     * with the type used by the column selection model.
     * @return <code>true</code> if the cell is selected;
     * <code>false</code> otherwise.
     */
    public boolean isSelectedCell(PageState s, Object rowKey, Object column) {
        return isSelectedRow(s, rowKey) && isSelectedColumn(s, column);
    }

    public void setStriped(boolean striped) {
        m_striped = striped;
    }

    public boolean getStriped() {
        return m_striped;
    }

    /**
     * Adds type-specific XML attributes to the XML element representing
     * this link. Subclasses should override this method if they introduce
     * more attributes than the ones {@link #generateXML generateXML}
     * produces by default.
     *
     * @param state represents the current request
     * @param element the XML element representing this table
     */
    protected void generateExtraXMLAttributes(PageState state,
                                              Element element) {
    }

    /**
     * Generates the XML representing the table. Gets a new {@link TableModel}
     * from the {@link TableModelBuilder} and iterates over the model's
     * rows. The value in each table cell is rendered with the help of the
     * column's table cell renderer.
     *
     * <p> Generates an XML fragment:
     * <pre>
     * &lt;bebop:table&gt;
     *   &lt;bebop:thead&gt;
     *     &lt;bebpp:cell&gt;...&lt;/cell&gt; ...
     *   &lt;/bebop:thead&gt;
     *   &lt;bebop:tbody&gt;
     *     &lt;bebop:trow&gt;
     *       &lt;bebpp:cell&gt;...&lt;/cell&gt; ...
     *     &lt;/bebop:trow&gt;
     *       ...
     *   &lt;/bebop:tbody&gt;
     * &lt;/bebop:table&gt;
     *
     * @param s the page state
     * @param p the parent {@link Element}
     */
    @Override
    public void generateXML(PageState s, Element p) {
        TableModel model = getTableModel(s);


        final boolean tableIsRegisteredWithPage =
                      s.getPage().stateContains(getControler());

        if (model.nextRow()) {
            Element table = p.newChildElement(BEBOP_TABLE, BEBOP_XML_NS);
            exportAttributes(table);
            generateExtraXMLAttributes(s, table);
            if (getHeader() != null) {
                getHeader().generateXML(s, table);
            }
            Element tbody = table.newChildElement(BEBOP_TABLEBODY, BEBOP_XML_NS);
            if (m_striped) {
                tbody.addAttribute("striped", "true");
            }

            final int modelSize = getColumnModel().size();
            int row = 0;

            logger.debug("Creating table rows...");
            long start = System.currentTimeMillis();
            do {
                long rowStart = System.currentTimeMillis();
                Element trow = tbody.newChildElement(BEBOP_TABLEROW,
                                                     BEBOP_XML_NS);

                for (int i = 0; i < modelSize; i++) {

                    TableColumn tc = getColumn(i);
                    if (tc.isVisible(s)) {
                        TableCellRenderer r = tc.getCellRenderer();
                        if (r == null) {
                            r = m_defaultCellRenderer;
                        }
                        final int modelIndex = tc.getModelIndex();
                        Object key = model.getKeyAt(modelIndex);
                        Object value = model.getElementAt(modelIndex);
                        boolean selected =
                                isSelectedCell(s, key, new Integer(i));
                        if (tableIsRegisteredWithPage) {
                            /*StringBuffer coords = new StringBuffer(40);
                            coords.append(model.getKeyAt(modelIndex)).append(SEP).
                                    append(i);
                            s.setControlEvent(getControler(), CELL_EVENT,
                                              coords.toString());*/

                            s.setControlEvent(getControler(),
                                              CELL_EVENT,
                                              String.format("%s%s%d",
                                                            model.getKeyAt(
                                    modelIndex),
                                                            SEP,
                                                            i));
                        }

                        Element cell = trow.newChildElement(BEBOP_CELL,
                                                            BEBOP_XML_NS);

                        tc.exportCellAttributes(cell);
                        long begin = System.currentTimeMillis();
                        r.getComponent(this, s, value, selected, key, row, i).
                                generateXML(s, cell);
                        logger.debug(String.format("until here i needed %d ms",
                                                   System.currentTimeMillis()
                                                   - begin));
                    }
                }
                row += 1;
                logger.debug(
                        String.format("Created row in %d ms",
                                      System.currentTimeMillis() - rowStart));
            } while (model.nextRow());
            logger.debug(String.format("Build table rows in %d ms",
                                       System.currentTimeMillis() - start));
        } else if (m_emptyView != null) {
            m_emptyView.generateXML(s, p);
        }
        if (tableIsRegisteredWithPage) {
            s.clearControlEvent();
        }
    }

    protected Component getControler() {
        return this;
    }

    /**
     * Returns the table model in effect for the request represented by the
     * page state.
     *
     * @param s represents the state of the page in the current request
     * @return the table model used for outputting the table.
     */
    public TableModel getTableModel(PageState s) {
        return (TableModel) m_tableModel.get(s);
    }

    /**
     * Initialize the request local <code>m_tableModel</code> field so that
     * it is initialized with whatever model the table model builder returns
     * for the request.
     */
    private void initTableModel() {
        m_tableModel = new RequestLocal() {

            @Override
            protected Object initialValue(PageState s) {
                return m_modelBuilder.makeModel(Table.this, s);
            }
        };
    }

    /**
     * Locks the table against further modifications. This also locks all
     * the associated objects: the model builder, the column model, and the
     * header components.
     * @see com.arsdigita.util.Lockable#lock
     */
    @Override
    public void lock() {
        getModelBuilder().lock();
        getColumnModel().lock();
        if (getHeader() != null) {
            getHeader().lock();
        }
        super.lock();
    }

    /**
     * An internal class that creates a table model around a set of data given
     * as a <code>Object[][]</code>. The table models produced by this builder
     * use row numbers, converted to strings, as the key for each column of a 
     * row.
     */
    public static class MatrixTableModelBuilder
                        extends AbstractTableModelBuilder {

        private final Object[][] m_data;

        /**
         * Constructor.
         * 
         * @param data 
         */
        public MatrixTableModelBuilder(Object[][] data) {
            m_data = data;
        }

        @Override
        public TableModel makeModel(Table t, PageState s) {
            return new TableModel() {

                private int row = -1;

                @Override
                public int getColumnCount() {
                    return m_data[0].length;
                }

                @Override
                public boolean nextRow() {
                    return (++row < m_data.length);
                }

                @Override
                public Object getElementAt(int j) {
                    return m_data[row][j];
                }

                @Override
                public Object getKeyAt(int j) {
                    return String.valueOf(row);
                }
            };
        }
    }

    /**
     * A {@link TableModel} that has no rows.
     */
    public static final TableModel EMPTY_MODEL = new TableModel() {

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public boolean nextRow() {
            return false;
        }

        @Override
        public Object getKeyAt(int column) {
            throw new IllegalStateException("TableModel is empty");
        }

        @Override
        public Object getElementAt(int column) {
            throw new IllegalStateException("TableModel is empty");
        }
    };
}
