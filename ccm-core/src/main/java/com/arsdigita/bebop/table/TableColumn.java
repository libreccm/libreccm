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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;

import com.arsdigita.util.Assert;
import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.util.Lockable;
import com.arsdigita.xml.Element;

/**
 * One column in a table. The <code>TableColumn</code> stores important
 * display-related information about a table column, such as the column
 * header, the renderers for the column header and ordinary cells in this
 * column and from which column in the table model values should be taken
 * when rendering table cells. The set of table columns for a table is
 * maintained by a {@link TableColumnModel}.
 *
 * <p> <code>TableColumn</code> allows the column ordering to be different
 * between the underlying {@link TableModel} and the view presented by the
 * <code>Table</code>: each column contains a <code>modelIndex</code>
 * property. This is the column that is retrieved from the
 * <code>TableModel</code> when the values are displayed, regardless of the
 * position of the <code>TableColumn</code> in the
 * <code>TableColumnModel</code>. This makes it possible to display the
 * same table model in several tables with reordered or omitted columns.
 *
 * <p> The <code>TableColumn</code> stores also the value and key used for
 * the header of the column. These objects are passed to the header cell
 * renderer when the header of the table is rendered. The value is usually
 * used to generate the visible information for the table header, and is
 * often a string. The key is usually used to identify the underlying
 * object, or to just identify the column, and can be any object whose
 * <code>toString()</code> method returns a representation that can be
 * included in a URL. In the simplest case, this may just be an
 * <code>Integer</code> containing the index of the column in the column
 * model.
 *
 * @author David Lutterkort
 * @see com.arsdigita.bebop.Table
 * @see TableColumnModel
 *
 * @version $Id$ */
public class TableColumn extends SimpleComponent
                         implements Lockable {


    /**
     * The name of the width attribute used in the XML.
     */
    private static final String WIDTH_ATTR = "width";

    /**
     * The name of the align attribute used in the XML.
     */
    private static final String ALIGN_ATTR = "align";

    /**
     * The name of the valign attribute used in the XML.
     */
    private static final String VALIGN_ATTR = "valign";

    /**
     * The number of the column in the table model from which to get values.
     */
    private int m_modelIndex;

    /**
     * The renderer used for ordinary cells in this column. Null by default,
     * which instructs the <code>Table</code> to use its default renderer.
     */
    private TableCellRenderer m_cellRenderer;

    /**
     * The renderer used for the header of the column. Null by default, which
     * instructs the <code>TableHeader</code> to use its default renderer.
     */
    private TableCellRenderer m_headerRenderer;

    /**
     * The key for identifying the header. Will be passed to the header cell
     * renderer.
     */
    private Object m_headerKey;

    /**
     * The display value for identifying the header. Will be passed to the
     * header cell renderer. 
     * Usually this will be a {@link Label} passed in by a pattern like 
     * {@code new Label(GlobalizedMessage)}. But it could be any object, 
     * e.g.an image as well. The use of a string is possible but strongly 
     * discouraged because it results in non-localizable UI.
     */
    private Object m_headerValue;

    /**
     * The display attributes for each cell in this column
     */
    private Attributes m_cellAttrs;

    /**
     * Creates a new table column with <code>modelIndex</code> 0 and header
     * value and key equal to <code>null</code>.
     */
    public TableColumn() {
        this(0);
    }

    /**
     * Creates a new table column with the given <code>modelIndex</code> and
     * header value and key equal to <code>null</code>.
     *
     * @param modelIndex the index of the column in the table model from
     * which to retrieve values
     * @pre modelIndex >= 0
     */
    public TableColumn(int modelIndex) {
        this(modelIndex, null);
    }

    /**
     * Creates a new table column with the given <code>modelIndex</code> and
     * header value. The header key is equal to <code>null</code>.
     *
     * @param modelIndex the index of the column in the table model from
     *                   which to retrieve values.
     * @param value      the value for the column header.
     * @pre modelIndex >= 0
     */
    public TableColumn(int modelIndex, Object value) {
        this(modelIndex, value, null);
    }

    /**
     * Creates a new table column with the given <code>modelIndex</code> and
     * header value and key.
     *
     * @param modelIndex the index of the column in the table model from
     *                   which to retrieve values.
     * @param value      the value for the column header.
     * @param key        the key for the column header.
     * @pre modelIndex >= 0
     */
    public TableColumn(int modelIndex, Object value, Object key) {
        super();
        m_modelIndex = modelIndex;
        m_headerValue = value;
        m_headerKey = key;

        m_cellAttrs = new Attributes();
    }

    /**
     * Return the renderer used for the column header. This is
     * <code>null</code> by default, in which case the default renderer for
     * the {@link TableHeader} of the table to which this column belongs is
     * used.
     *
     * @return the renderer used for the column header.
     */
    public final TableCellRenderer getHeaderRenderer() {
        return m_headerRenderer;
    }

    /**
     * Set the renderer used for the column header. The header key and value
     * objects are passed to the renderer when the column header will be
     * rendererd.
     *
     * @param v the new renderer for the column header.
     * @see #getHeaderRenderer
     * @see #getCellRenderer
     */
    public void setHeaderRenderer(TableCellRenderer  v) {
        Assert.isUnlocked(this);
        m_headerRenderer = v;
    }

    /**
     * Return the renderer used for the cells in this column. This is
     * <code>null</code> by default, in which case the default renderer of
     * the {@link com.arsdigita.bebop.Table#getDefaultCellRenderer() table} to which this column
     * belongs is used.
     *
     * @return the renderer used for the cells in this column.
     */
    public final TableCellRenderer getCellRenderer() {
        return m_cellRenderer;
    }

    /**
     * Set the renderer used for cells in this column.
     *
     * @param v the new renderer for the cells in this column.
     * @see #getCellRenderer
     * @see #getHeaderRenderer
     */
    public void setCellRenderer(TableCellRenderer  v) {
        Assert.isUnlocked(this);
        m_cellRenderer = v;
    }

    /**
     * Get the display value used for the header. This is the object that is
     * passed to the renderer. Usually this will be a {@link Label} previously 
     * passed in by a pattern like {@code new Label(GlobalizedMessage)}.
     * The use of a string is possible but strongly discouraged.
     *
     * @return the display value for the header.
     */
    public final Object getHeaderValue() {
        return m_headerValue;
    }

    /**
     * Set the display value for the header. This object is passed through to
     * the header renderer without any modifications.
     * Usually this will be a {@link Label} passed in by a pattern like 
     * {@code new Label(GlobalizedMessage)}. The use of a string is possible 
     * but strongly discouraged because it results in non-localizable UI.
     *
     * @param value the new display value for the header.
     * @see #getHeaderValue
     */
    public void setHeaderValue(Object value) {
        Assert.isUnlocked(this);
        m_headerValue = value;
    }

    /**
     * Get the key used to identify the header of this column. In the
     * simplest case, this is an <code>Integer</code> containing the index of
     * the column.
     *
     * @return the key used to identify the header of this column.
     */
    public final Object getHeaderKey() {
        return m_headerKey;
    }

    /**
     * Set the key used to identify the header of this column.
     *
     * @param key  the new key for identifying the header of this column.
     * @see #getHeaderKey
     */
    public void setHeaderKey(Object key) {
        Assert.isUnlocked(this);
        m_headerKey = key;
    }

    /**
     * Get the index of the column from which values are taken in the {@link
     * TableModel}.
     *
     * @return the index of the column in the table model from which values
     * are taken.
     * @see #setModelIndex setModelIndex
     */
    public final int getModelIndex() {
        return m_modelIndex;
    }

    /**
     * Set the index of the column in the {@link TableModel} from which the
     * values are taken when this column is rendered.
     *
     * @param v the new index of the column in the table model from which to
     * take values.
     */
    public void setModelIndex(int  v) {
        Assert.isUnlocked(this);
        m_modelIndex = v;
    }

    /**
     * Get the width for this column.
     *
     * @return the width of this column.
     * @see #setWidth setWidth
     */
    public String getWidth() {
        return getAttribute(WIDTH_ATTR);
    }

    /**
     * Set the width of this column. The string <code>v</code> is added as an
     * attribute to the XML element for this column in the table header.
     *
     * @param v the width of this column
     */
    public void setWidth(String  v) {
        Assert.isUnlocked(this);
        setAttribute(WIDTH_ATTR, v);
    }

    /**
     * Set the horizontal alignment this column. The string <code>v</code>
     * is added as an attribute to the XML element for each cell in this column
     *
     * @param v the width of this column
     */
    public void setAlign(String  v) {
        Assert.isUnlocked(this);
        m_cellAttrs.setAttribute(ALIGN_ATTR, v);
    }


    /**
     * Set the horizontal alignment this column's header. The string
     * <code>v</code> is added as an attribute to the XML element for
     * the column's header cell.
     *
     * @param v the width of this column */
    public void setHeadAlign(String v) {
        Assert.isUnlocked(this);
        setAttribute(ALIGN_ATTR, v);
    }

    /**
     * Set the vertical alignment this column. The string <code>v</code>
     * is added as an attribute to the XML element for each cell in this column
     *
     * @param v the width of this column
     */
    public void setVAlign(String  v) {
        Assert.isUnlocked(this);
        m_cellAttrs.setAttribute(VALIGN_ATTR, v);
    }

    /**
     * Set the vertical alignment this column's header. The string
     * <code>v</code> is added as an attribute to the XML element for
     * this column's header cell.
     *
     * @param v the width of this column */
    public void setHeadVAlign(String  v) {
        Assert.isUnlocked(this);
        setAttribute(VALIGN_ATTR, v);
    }


    /**
     * Sets the style attribute for the column's
     * cells. <code>style</code> should be a valid CSS style, since
     * its value will be copied verbatim to the output and appear as a
     * <tt>style</tt> attribute in the top level XML or HTML output
     * element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="#standard">Standard Attributes</a> */
    public void setStyleAttr(String style) {
        Assert.isUnlocked(this);
        m_cellAttrs.setAttribute(STYLE, style);
    }

    /**
     * Sets the style attribute for the column's header
     * cell. <code>style</code> should be a valid CSS style, since its
     * value will be copied verbatim to the output and appear as a
     * <tt>style</tt> attribute in the top level XML or HTML output
     * element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="#standard">Standard Attributes</a> */
    public void setHeadStyleAttr(String style) {
        Assert.isUnlocked(this);
        setAttribute(STYLE, style);
    }

    /**
     * Sets the class attribute for the column's
     * cells. <code>style</code> should be the name of a defined CSS
     * class, since its value will be copied verbatim to the output
     * and appear as a <tt>class</tt> attribute in the top level XML
     * or HTML output element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="#standard">Standard Attributes</a> */
    public void setClassAttr(String c) {
        Assert.isUnlocked(this);
        m_cellAttrs.setAttribute(CLASS, c);
    }


    /**
     * Sets the class attribute for the column's header
     * cell. <code>style</code> should be the name of a defined CSS
     * class, since its value will be copied verbatim to the output
     * and appear as a <tt>class</tt> attribute in the top level XML
     * or HTML output element.
     *
     * @param style a valid CSS style description for use in the
     *   <tt>style</tt> attribute of an HTML tag
     * @see <a href="#standard">Standard Attributes</a> */
    public void setHeadClassAttr(String c) {
        Assert.isUnlocked(this);
        setAttribute(CLASS, c);
    }



    /**
     * Add all the XML attributes for this column.
     *
     * @param e the XML element to which attributes will be added.
     */
    public void exportCellAttributes(Element e) {
        m_cellAttrs.exportAttributes(e);
    }

    /**
     * Add all the XML attributes for this column to this
     * element. Package-friendly since it is only used by {@link
     * TableHeader}.
     *
     * @param e the XML element to which attributes will be added.
     */
    final void exportHeadAttributes(Element e) {
        super.exportAttributes(e);
    }

    /**
     * Throw an <code>UnsupportedOperationException</code>. This method can
     * only be called if the table column is not properly contained in a
     * table.
     *
     * @param s represents the current request
     * @param e the parent element
     */
    public void generateXML(PageState s, Element e) {
        throw new UnsupportedOperationException("TableColumn used outside of a Table");
    }

}
