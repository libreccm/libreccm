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

import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.util.Attributes;
import com.arsdigita.bebop.util.PanelConstraints;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A container that prints its components in a table. Each child is printed
 * in its own table cell. The number of columns can be specified in the
 * constructor. The components are put into the table in the order in which
 * they were added to the <code>ColumnPanel</code> by filling the table one row
 * at a time (filling each row from left to right), from the top of the table
 * to the bottom.
 *
 * <p> The position of the component within the cell can be influenced with the
 * following constraints.
 * <TABLE border=0>
 *   <tr>
 *      <TD nowrap valign="top">Horizontal alignment</TD>
 *      <Td valign="top">Use <code>LEFT</code>, <code>CENTER</code>, or
 *       <code>RIGHT</code>.</td></tr>
 *   <tr>
 *      <td nowrap valign="top">Vertical alignment</td>
 *      <td valign="top">Use <code>TOP</code>, <code>MIDDLE</code>, or
 *       <code>BOTTOM</code>.</td></tr>
 *   <tr>
 *      <td nowrap valign="top">Full width</td>
 *      <td valign="top">Use <code>FULL_WIDTH</code> to instruct the panel to
 *       put the component in a row by itself, spanning the full width of the
 *       table.</td></tr>
 *   <tr>
 *      <td nowrap valign="top">Inserting children</td>
 *      <td valign="top">Use <code>INSERT</code> to instruct the panel to
 *       insert the corresponding component, assuming that it will also be
 *       laid out by a <code>ColumnPanel</code> with the same number of
 *       columns.</td></tr>
 * </TABLE>
 *
 * <p>Constraints can be combined by OR-ing them together. For example, to print 
 * a component in a row of its own, left-aligned, at the bottom of its cell, 
 * use the constraint <code>FULL_WIDTH | LEFT | BOTTOM</code>.
 *
 * <p> Using the <code>INSERT</code> constraint fuses the current ColumnPanel
 * with the panel of the child to which the constraint is applied. For example,
 * consider a {@link Form} that is to have a 2-column format with labels in the
 * left column and widgets in the right column. If a {@link FormSection} is
 * added to the form, it should be included seamlessly into the parent form. 
 * To do this, set the <code>INSERT</code> constraint when the {@link
 * FormSection} is added to the <code>ColumnPanel</code> of the {@link Form}. At
 * the same time, tell the <code>ColumnPanel</code> used to lay out the {@link
 * FormSection} that it is to be inserted into another panel.
 *
 * <P>The following pseudo-code illustrates the example. (It assumes that
 * Form and FormSection are decorators of the ColumnPanel.)
 *
 * <pre style="background: #cccccc">
 *
 *   Form form = new Form(new ColumnPanel(2));
 *   FormSection sec = new FormSection(new ColumnPanel(2, true));
 *
 *   sec.add(new Label("Basic Item Metadata"), ColumnPanel.FULL_WIDTH);
 *   sec.add(new Label("Title:"), ColumnPanel.RIGHT);
 *   sec.add(new Text("title"));
 *
 *   form.add(sec, ColumnPanel.INSERT);
 * </pre>
 *
 * @author David Lutterkort 
 * @version $Id: ColumnPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ColumnPanel extends SimpleContainer 
                         implements PanelConstraints {

    /** An empty constraint corresponding to the default                      */
    private static final Constraint DEFAULT_CONSTRAINT = new Constraint();

    /** The number of columns in the panel                                    */
    private int m_nCols;

    /** Explicitly registered constraints for child components. Maps
     * <code>Components</code>s to <code>Constraints</code>                   */
    private Map m_constraints;

    /** Is this panel inserted in another one ? If so, do not produce
     * &lt;table&gt; tags                                                     */
    private boolean m_inserted;

    /** Border attributes                                                     */
    private Attributes m_border;
    private Attributes m_padFrame;
    private Attributes m_pad;
    private String[] m_columnWidth;

    
// Instance methods

    /**
     * Creates a table panel with the specified number of columns.
     *
     * @param nCols number of columns in the panel
     */
    public ColumnPanel(int nCols) {
        this(nCols, false);
        makeBorder();
        makePadFrame();
        makePad();
    }

    /**
     * Creates a table panel with the specified number of columns that will
     * be printed as a direct child of a <code>ColumnPanel</code>
     * with the same number of columns.
     * @see #setInserted
     *
     * @param nCols number of columns in the panel
     * @param inserted <code>true</code> if this panel
     *                 is to be printed as a direct child of a
     *                 <code>ColumnPanel</code> with the same number of
     *                 columns
     */
    public ColumnPanel(int nCols, boolean inserted) {
        m_nCols = nCols;
        setInserted(inserted);
        m_constraints = new HashMap();
        m_columnWidth=new String[nCols];
    }

    /**
     * Adds a component, specifying its constraints.
     * 
     * @param c the component to add
     * @param constraints the constraints for this component
     */
    @Override
    public void add(Component c, int constraints) {
        super.add(c);
        setConstraint(c, constraints);
    }

    /**
     * Sets whether this panel will be printed inside a
     * <code>ColumnPanel</code> with the same number of columns. If
     * <code>inserted</code> is true, no &lt;table&gt; tags will be produced
     * to enclose the child components.
     * @param inserted <code>true</code> if this panel is to be printed
     * inside a <code>ColumnPanel</code> with the same number of columns
     */
    public void setInserted(boolean inserted) {
        Assert.isUnlocked(this);
        m_inserted = inserted;
    }

    /**
     * Determines whether this panel is to be inserted into another panel.
     * @return <code>true</code> if this panel is to be inserted
     * into another panel; <code>false</code> otherwise.
     *
     * @see #setInserted
     */
    public final boolean isInserted() {
        return m_inserted;
    }

    /**
     * Returns the number of columns for this ColumnPanel
     * @return the number of columns
     *
     */
    public final int getNumCols() {
        return m_nCols;
    }

    /**
     * Adds child components as a subtree under table-style nodes. If any of the
     * direct children are hidden form widgets, they are added directly to
     * <code>parent</code> rather than included in any of the
     * <code>cell</code> elements of the panel.
     *
     * <p>Generates a DOM fragment:
     * <p><code><pre>
     * &lt;bebop:pad>
     *   [&lt;bebop:padFrame>]
     *    [&lt;bebop:border>]
     *      &lt;bebop:panelRow>
     *       &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *       &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *       ...
     *      &lt;/bebop:panelRow>
     *      &lt;bebop:panelRow>
     *       &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *       &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *       ...
     *      &lt;/bebop:panelRow>
     *    [&lt;/bebop:border>]
     *   [&lt;/bebop:padFrame>]
     * &lt;/bebop:boxPanel></pre></code>
     * @param state the current page state
     * @param parent the parent element for these child components
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        if ( isVisible(state) ) {

            Element panel = parent.newChildElement("bebop:columnPanel", BEBOP_XML_NS);
            exportAttributes(panel);
            // parent.addContent(panel);

            generateChildren(state, parent, generateTopNodes(panel));
        }
    }

    // Border attributes

    private void makeBorder() {
        if ( m_border == null ) {
            m_border = new Attributes();
            m_border.setAttribute("cellspacing", "0");
            m_border.setAttribute("cellpadding", "4");
            m_border.setAttribute("border", "0");
            m_border.setAttribute("width", "100%");
        }
    }

    /**
     * 
     *
     * @param c
     */
    public void setBorderColor(String c) {
        makeBorder();
        m_border.setAttribute("bgcolor", c);
    }

    /**
     * 
     *
     * @param w
     */
    public void setBorderWidth(String w) {
        makeBorder();
        m_border.setAttribute("cellpadding", w);
    }

    public void setColumnWidth(int col, String width) {
        m_columnWidth[col-1]=width;
    }

    /**
     * 
     *
     * @param b
     */
    public void setBorder(boolean b) {
        if (b) {
            makeBorder();
        } else {
            m_border = null;
        }
    }

    // Pad and Padframe attributes
    private void makePadFrame() {
        if (m_padFrame == null) {
            m_padFrame = new Attributes();
            m_padFrame.setAttribute("cellspacing", "0");
            m_padFrame.setAttribute("cellpadding", "6");
            m_padFrame.setAttribute("border", "0");
            m_padFrame.setAttribute("width", "100%");
        }
    }

    /**
     * 
     *
     */
    private void makePad() {
        if ( m_pad == null ) {
            m_pad = new Attributes();
            m_pad.setAttribute("cellspacing", "0");
            m_pad.setAttribute("cellpadding", "2");
            m_pad.setAttribute("border", "0");
            m_pad.setAttribute("width", "100%");
        }
    }

    /**
     * 
     *
     * @param c
     */
    public void setPadColor(String c) {
        makePadFrame();
        makePad();
        m_padFrame.setAttribute("bgcolor", c);
        m_pad.setAttribute("bgcolor", c);
    }

    /**
     * 
     *
     * @param w
     */
    public void setWidth(String w) {
        makePadFrame();
        m_padFrame.setAttribute("width", w);
    }

    /**
     * 
     *
     * @param w
     */
    public void setPadFrameWidth(String w) {
        makePadFrame();
        m_padFrame.setAttribute("cellpadding", w);
    }

    /**
     * 
     *
     * @param border
     */
    public void setPadBorder(boolean border) {
        makePad();
        if(border) {
            m_pad.setAttribute("border", "1");
        } else {
            m_pad.setAttribute("border", "0");
        }
    }

    /**
     * 
     *
     * @param padding
     */
    public void setPadCellPadding(String padding) {
        makePad();
        m_pad.setAttribute("cellpadding", padding);
    }

    /**
     * add top tags (will translate to opening/closing),
     * including display styles
     */
    private Element generateTopNodes(Element parent) {
        // FIXME: set background color, border effects, cell spacing etc.
        if (isInserted()) {
            return parent;
        }
        String l_class =  getClassAttr();
        if (m_border != null) {
            Element border = parent.newChildElement("bebop:border",BEBOP_XML_NS);
            if (l_class != null) {
                m_border.setAttribute("class", l_class);
            }
            m_border.exportAttributes(border);
            // parent.addContent(border);
            parent=border;          // nest the rest inside border
        }
        if ( m_padFrame != null ) {
            Element padFrame = parent.newChildElement("bebop:padFrame", BEBOP_XML_NS);
            if (l_class != null) {
                m_padFrame.setAttribute("class", l_class);
            }
            m_padFrame.exportAttributes(padFrame);
            // parent.addContent(padFrame);
            parent=padFrame;            // nest the rest in padFrame
        }
        Element pad = parent.newChildElement("bebop:pad", BEBOP_XML_NS);
        if (l_class != null) {
            m_pad.setAttribute("class", l_class);
        }
        m_pad.exportAttributes(pad);
        // parent.addContent(pad);
        return pad;
    }

    /**
     * Lay out the child components using constraints registered for them,
     * generating a DOM tree and extending another.
     *
     * @param state represents the state of the current request
     * @param hiddenParent the element to which hiddens are added
     * @param parent the element to which ordinary rows and cells are added
     */
    private void generateChildren(PageState state, Element hiddenParent,
                                  Element parent) {
        // Count the number of components printed in the current row
        int rowLen = m_nCols + 1; // Force generation of first row
        Element row  = null;
        Element cell = null;

        for (Iterator i = children(); i.hasNext(); ) {
            Component c = (Component) i.next();

            if ( c.isVisible(state) ) {

                if ( c instanceof Hidden ) {
                    c.generateXML(state, hiddenParent);
                } else {
                    if ( isInsert(c) ) {
                        c.generateXML(state, parent);
                        rowLen = m_nCols + 1; // Force generation of new row
                    } else {
                        if ( rowLen >= m_nCols || isFullWidth(c)) {
                            rowLen = 0;
                            row = parent.newChildElement("bebop:panelRow", BEBOP_XML_NS);
                            // parent.addContent(row);
                        }
                        cell = row.newChildElement("bebop:cell", BEBOP_XML_NS);
                        // row.addContent(cell);
                        if ( m_columnWidth[rowLen] != null ) {
                            cell.addAttribute("width", m_columnWidth[rowLen]);
                        }
                        getConstraint(c).exportAttributes(cell, m_nCols);
                        c.generateXML(state, cell);
                        rowLen++;
                        if ( isFullWidth(c) ) {
                            // Force a new row if c was full width
                            rowLen = m_nCols + 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the constraint for one child component.
     * @param c the child component
     * @param constraints the constraints to add
     */
    public void setConstraint(Component c, int constraints) {
        Assert.isUnlocked(this);
        m_constraints.put(c, new Constraint(constraints));
    }

    /**
     * Get the constraint object for a component. If no constraints have been
     * set explicitly, return a default constraint object.
     *
     * @post return != null
     */
    private Constraint getConstraint(Component c) {
        Constraint result = (Constraint) m_constraints.get(c);
        if ( result == null ) {
            return DEFAULT_CONSTRAINT;
        } else {
            return result;
        }
    }

    private boolean isInsert(Component c) {
        return getConstraint(c).isInsert();
    }

    private boolean isFullWidth(Component c) {
        return getConstraint(c).isFullWidth();
    }


    // Inner class(es)

    /**
     * Represent the constraints for one child component
     */
    private static class Constraint {
        private boolean m_fullWidth;
        private boolean m_insert;
        private String m_alignment;                        // for print
        private String m_halign;                        // for generateXML
        private String m_valign;                        // for generateXML

        public Constraint() {
            this(0);
        }

        public Constraint(int constraints) {
            StringBuilder s = new StringBuilder();

            if ( (constraints & (LEFT|CENTER|RIGHT)) != 0 ) {
                s.append(" align=\"");
                if ( (constraints & LEFT) != 0) {
                    s.append(m_halign = "left");
                } else if ( (constraints & CENTER) != 0) {
                    s.append(m_halign = "center");
                } else if ( (constraints & RIGHT) != 0) {
                    s.append(m_halign = "right");
                }
                s.append("\" ");
            } else {
                m_halign = null;
            }

            if ( (constraints & (TOP|MIDDLE|BOTTOM)) != 0 ) {
                s.append(" valign=\"");
                if ( (constraints & TOP) != 0) {
                    s.append(m_valign = "top");
                } else if ( (constraints & MIDDLE) != 0) {
                    s.append(m_valign = "middle");
                } else if ( (constraints & BOTTOM) != 0) {
                    s.append(m_valign = "bottom");
                }
                s.append("\" ");
            } else {
                m_valign = null;
            }

            m_alignment = s.toString();

            m_fullWidth = (constraints & FULL_WIDTH) != 0;
            m_insert = (constraints & INSERT) != 0;
        }

        public final boolean isFullWidth() {
            return m_fullWidth;
        }

        public final boolean isInsert() {
            return m_insert;
        }

        public final String getAlignment() {
            return m_alignment;
        }

        public final String getHAlign() {
            return m_halign;
        }

        public final String getVAlign() {
            return m_valign;
        }

        public void exportAttributes(Element cell, int nCols) {
            String halign = getHAlign();
            String valign = getVAlign();
            if (halign != null) {
                cell.addAttribute("align" , halign);
            }
            if (valign != null) {
                cell.addAttribute("valign", valign);
            }
            if ( isFullWidth() ) {
                cell.addAttribute("colspan", Integer.toString(nCols));
            }
        }
    }
}
