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

import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.bebop.util.PanelConstraints;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>A container that prints its components in a table. Each child is
 * printed in its own table cell. The number of columns can be
 * specified in the constructor. The components are put into the table
 * in the order in which they were added to the <code>GridPanel</code>
 * by filling the table one row
 * at a time (filling each row from left to right), from the top of the table
 * to the bottom.</p>
 *
 * <p>The position of the component within the cell can be influenced
 * with  the following constraints.</p>
 *
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
 *      <td valign="top">Use <code>FULL_WIDTH</code> to instruct the panel to put
 *       the component in a row by itself, spanning the full width of the
 *       table.</td></tr>
 *   <tr>
 *      <td nowrap valign="top">Inserting children</td>
 *      <td valign="top">Use <code>INSERT</code> to instruct the panel to
 *    insert the corresponding component, assuming that it will also be
 *    laid out by a <code>ColumnPanel</code> with the same number of
 *    columns.</td></tr>
 * </TABLE>
 *
 * </dl>
 *
 * <p>Constraints can be combined by
 * ORing them together. For example, to print a component in a row of its
 * own, left-aligned, at the bottom of its cell, use the constraint
 * <code>FULL_WIDTH | LEFT | BOTTOM</code>.</p>
 *
 * <p>Using the <code>INSERT</code> constraint fuses the current
 * <code>GridPanel</code> with the panel of the child to which the
 * constraint is applied. For example, consider a {@link Form}, that
 * is to have a 2-column format with labels in the left column
 * and widgets in the right column. If a {@link FormSection} is added to
 * the form, it should be included seamlessly into the parent
 * form. To do this, set the <code>INSERT</code>
 * constraint when the {@link FormSection} is added to the {@link
 * Form}'s <code>GridPanel</code>. At the same time,  tell the
 * <code>GridPanel</code> used to lay out the {@link FormSection}
 * that it is is to be inserted into another panel.
 * The following
 * pseudo-code illustrates the example. (It assumes that Form and
 * FormSection are decorators of the GridPanel.)</p>
 *
 * <blockquote><pre>
 * Form form = new Form(new GridPanel(2));
 * FormSection sec = new FormSection(new GridPanel(2, true));
 * // "true" in the above constructor tells the GridPanel it is inserted.
 *
 * sec.add(new Label("Basic Item Metadata"), GridPanel.FULL_WIDTH);
 * sec.add(new Label("Title:"), GridPanel.RIGHT);
 * sec.add(new Text("title"));
 *
 * form.add(sec, GridPanel.INSERT);
 * </pre></blockquote>
 *
 * @see BoxPanel
 * @see SplitPanel
 * @author David Lutterkort 
 * @author Stanislav Freidin 
 * @author Justin Ross 
 * @version $Id: GridPanel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class GridPanel extends SimpleContainer 
                       implements BebopConstants, PanelConstraints {

    private static final ChildConstraint DEFAULT_CONSTRAINT
        = new ChildConstraint();

    private int m_numColumns;

    /*
     * Explicitly registered constraints for child components. Maps
     * <code>Components</code>s to <code>Constraints</code>
     */
    private Map m_childConstraintMap;

    /*
     * Is this panel inserted in another one? If so, do not produce
     * &lt;table&gt; tags.
     */
    private boolean m_isInserted;

    /**
     * Creates a table panel with the specified number of columns.
     *
     * @param numColumns the number of columns in the panel
     */
    public GridPanel(int numColumns) {
        this(numColumns, false);
    }

    /**
     * Creates a table panel with the specified number of columns and
     * indicates whether the panel is inserted.
     *
     * @param numColumns the number of columns in the panel
     * @param isInserted <code>true</code> if this panel is to be
     * printed as a direct child of a <code>GridPanel</code>
     * with the same number of columns
     * @see #setInserted
     */
    public GridPanel(int numColumns, boolean isInserted) {
        m_numColumns = numColumns;
        setInserted(isInserted);
        m_childConstraintMap = new HashMap();
    }

    /**
     * Adds a component, specifying constraints.
     * @param component the component to add
     * @param constraints the constraints for the component
     */
    public void add(Component component, int constraints) {
        super.add(component);

        m_childConstraintMap.put(component, new ChildConstraint(constraints));
    }

    /**
     * Sets whether this panel will be printed inside a
     * <code>GridPanel</code> with the same number of columns. If
     * <code>inserted</code> is <code>true</code>, no &lt;table&gt; tags will be
     * produced to enclose the child components.
     * @param <code>true</code> if this panel is to be printed
     * inside a GridPanel with the same number of columns
     *
     */
    public void setInserted(boolean isInserted) {
        Assert.isUnlocked(this);
        m_isInserted = isInserted;
    }

    /**
     * Determines whether this panel is to be inserted into another panel.
     * @return <code>true</code> if this panel is to be inserted into another panel;
     * <code>false</code> otherwise.
     * @see #setInserted
     */
    public final boolean isInserted() {
        return m_isInserted;
    }

    /**
     * Adds child components as a subtree under table-style nodes. If any of the
     * direct children are hidden form widgets, they are added directly to
     * <code>parent</code> rather than included in any of the
     * <code>cell</code> elements of the panel.
     *
     * <p>Generates a DOM fragment:
     * <p><code><pre>
     * &lt;bebop:gridPanel>
     *   &lt;bebop:panelRow>
     *     &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *     &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *     ...
     *   &lt;/bebop:panelRow>
     *   &lt;bebop:panelRow>
     *    &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *    &lt;bebop:cell> ... cell contents &lt;/bebop:cell>
     *    ...
     *   &lt;/bebop:panelRow>
     * &lt;/bebop:gridPanel></pre></code>
     * 
     * @param pageState
     * @param parent
     */
    @Override
    public void generateXML(PageState pageState, Element parent) {
        if (isVisible(pageState)) {
            if (isInserted()) {
                generateChildren(pageState, parent);
            } else {
                Element panel = parent.newChildElement(BEBOP_GRIDPANEL, BEBOP_XML_NS);
                exportAttributes(panel);
                generateChildren(pageState, panel);
            }
        }
    }

    /*
     * Lay out the child components using constraints registered for them,
     * generating a DOM tree and extending another.
     */
    private void generateChildren(PageState pageState, Element parent) {
        int positionInRow = 0;
        boolean newRowRequested = true; // First time through we want a new row.
        Element row = null;
        Element cell = null;
        ChildConstraint constraint = null;

        Iterator iter = children();
        while (iter.hasNext()) {
            Component child = (Component)iter.next();

            if (child.isVisible(pageState)) {
                if (child instanceof Hidden) {
                    child.generateXML(pageState, parent);
                } else {
                    constraint = getChildConstraint(child);

                    if (constraint.m_isInsert) {
                        child.generateXML(pageState, parent);

                        newRowRequested = true;
                    } else {
                        if (positionInRow >= m_numColumns
                            || constraint.m_isFullWidth
                            || newRowRequested) {
                            positionInRow = 0;

                            row = parent.newChildElement(BEBOP_PANELROW, BEBOP_XML_NS);

                            if (constraint.m_isFullWidth) {
                                // If the column was full width, we
                                // want a new row in the next iteration.
                                newRowRequested = true;
                            } else if (newRowRequested) {
                                // Reset to off.
                                newRowRequested = false;
                            }
                        }

                        cell = row.newChildElement(BEBOP_CELL, BEBOP_XML_NS);

                        child.generateXML(pageState, cell);

                        constraint.exportCellAttributes(cell, m_numColumns);

                        positionInRow++;
                    }
                }
            }
        }
    }

    /*
     * Helper stuff
     */

    private ChildConstraint getChildConstraint(Component component) {
        ChildConstraint constraint =
            (ChildConstraint)m_childConstraintMap.get(component);

        if (constraint == null) {
            constraint = DEFAULT_CONSTRAINT;
        }

        return constraint;
    }

    private static class ChildConstraint {
        public boolean m_isFullWidth;
        public boolean m_isInsert;
        public String m_horizontalAlignment;
        public String m_verticalAlignment;

        public ChildConstraint() {
            this(0);
        }

        public ChildConstraint(int constraints) {
            if ((constraints & LEFT) != 0) {
                m_horizontalAlignment = "left";
            } else if ((constraints & CENTER) != 0) {
                m_horizontalAlignment = "center";
            } else if ((constraints & RIGHT) != 0) {
                m_horizontalAlignment = "right";
            } else {
                m_horizontalAlignment = null;
            }

            if ((constraints & TOP) != 0) {
                m_verticalAlignment = "top";
            } else if ((constraints & MIDDLE) != 0) {
                m_verticalAlignment = "middle";
            } else if ((constraints & BOTTOM) != 0) {
                m_verticalAlignment = "bottom";
            } else {
                m_verticalAlignment = null;
            }

            m_isFullWidth = (constraints & FULL_WIDTH) != 0;

            m_isInsert = (constraints & INSERT) != 0;
        }

        public void exportCellAttributes(Element cell, int numColumns) {
            if (m_horizontalAlignment != null) {
                cell.addAttribute("align", m_horizontalAlignment);
            }

            if (m_verticalAlignment != null) {
                cell.addAttribute("valign", m_verticalAlignment);
            }

            if (m_isFullWidth) {
                cell.addAttribute("colspan", Integer.toString(numColumns));
            }
        }
    }
}
