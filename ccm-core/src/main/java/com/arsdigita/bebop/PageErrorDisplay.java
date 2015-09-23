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
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;
import com.arsdigita.globalization.GlobalizedMessage;

import java.util.Iterator;

/**
 * Displays validation errors for the page. These might have occured due to validation listeners on
 * some state parameters within the page.
 *
 * @author Stanislav Freidin
 * @version $Id: PageErrorDisplay.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PageErrorDisplay extends List {

    private static final String COLOR = "color";

    /**
     * Constructs a new <code>PageErrorDisplay</code>.
     */
    public PageErrorDisplay() {
        this(new PageErrorModelBuilder());
    }

    /**
     * Constructs a new <code>PageErrorDisplay</code> from the errors supplied by a list model
     * builder.
     *
     * @param builder the {@link ListModelBuilder} that will supply the errors
     *
     */
    protected PageErrorDisplay(ListModelBuilder builder) {
        super(builder);
        setCellRenderer(new LabelCellRenderer());
        setTextColor("red");
        setClassAttr("pageErrorDisplay");
    }

    /**
     * Sets the HTML color of the error messages.
     *
     * @param c An HTML color, such as "#99CCFF" or "red"
     */
    public void setTextColor(String c) {
        setAttribute(COLOR, c);
    }

    /**
     * Gets the HTML color of the error messages.
     *
     * @return the HTML color of the error messages.
     */
    public String getTextColor() {
        return getAttribute(COLOR);
    }

    /**
     * Determines if there are errors to display.
     *
     * @param state the current page state
     *
     * @return <code>true</code> if there are any errors to display; <code>false</code> otherwise.
     */
    protected boolean hasErrors(PageState state) {
        return (state.getErrors().hasNext());
    }

    /**
     * Generates the XML for this component. If the state has no errors in it, does not generate any
     * XML.
     *
     * @param state  the current page state
     * @param parent the parent XML element
     */
    public void generateXML(PageState state, Element parent) {
        if (hasErrors(state)) {
            super.generateXML(state, parent);
        }
    }

    // A private class which builds a ListModel based on form errors
    private static class PageErrorModelBuilder extends LockableImpl
        implements ListModelBuilder {

        public PageErrorModelBuilder() {
            super();
        }

        public ListModel makeModel(List l, PageState state) {
            return new StringIteratorModel(state.getErrors());
        }

    }

    // A ListModel which generates items based on an Iterator
    protected static class StringIteratorModel implements ListModel {

        private Iterator m_iter;
        private GlobalizedMessage m_error;
        private int m_i;

        public StringIteratorModel(Iterator iter) {
            m_iter = iter;
            m_error = null;
            m_i = 0;
        }

        public boolean next() {
            if (!m_iter.hasNext()) {
                m_i = 0;
                return false;
            }

            m_error = (GlobalizedMessage) m_iter.next();
            ++m_i;

            return true;
        }

        private void checkState() {
            if (m_i == 0) {
                throw new IllegalStateException(
                    "next() has not been called succesfully"
                );
            }
        }

        public Object getElement() {
            checkState();
            return m_error;
        }

        public String getKey() {
            checkState();
            return Integer.toString(m_i);
        }

    }

    // A ListCellRenderer that renders labels
    private static class LabelCellRenderer implements ListCellRenderer {

        public Component getComponent(List list, PageState state, Object value,
                                      String key, int index, boolean isSelected) {
            return new Label((GlobalizedMessage) value);
        }

    }

}
