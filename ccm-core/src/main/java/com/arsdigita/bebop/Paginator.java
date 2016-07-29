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
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;

/**
 * A pagination component used to select different page views from a list of
 * items.
 *
 * <p>In most cases, this component will be used with either a {@link
 * List} or a {@link Table}. Here is an example on how to use this pagination
 * component with a Table:
 *
 * <blockquote><pre><code>
 * Table myTable = new Table(new myTableModelBuilder(),
 *                           new DefaultTableColumnModel());
 * Paginator pgntr = new Paginator((PaginationModelBuilder) myTable.getModelBuilder(), 10);
 *
 * Page p = new Page();
 * p.add(pgntr);
 * p.add(myTable);
 * </code></pre></blockquote>
 *
 * <p>The model builder that is used in
 * <code>myTable</code> was designed to also implement the {@link PaginationModelBuilder}
 * interface. With both interfaces being implemented by the same class, it is
 * much easier to cache the results of operations performed on the {@link com.arsdigita.persistence.DataQuery}
 * used to generate the results.
 *
 * <blockquote><pre><code>
 * public class myTableModelBuilder extends LockableImpl
 *     implements TableModelBuilder, PaginatedModelBuilder {
 *
 *     private RequestLocal m_query;
 *
 *     public myTableModelBuilder() {
 *         super();
 *         m_query = new RequestLocal();
 *     }
 *
 *     private getDataQuery(PageState state) {
 *         // returns the DataQuery used to generate rows for the table
 *     }
 *
 *     public int getTotalSize(Paginator pgntr, PageState state) {
 *         DataQuery query = getDataQuery(state);
 *         query.setRange(new Integer(pgntr.getFirst(state)),
 *                        new Integer(pgntr.getLast(state) + 1));
 *         m_query.set(state, query);
 *         return (int) query.size();
 *     }
 *
 *     public TableModel makeModel(Table table, PageState state) {
 *         return new myTableModel(table, state, (DataQuery) m_query.get(state));
 *     }
 * }
 * </code></pre></blockquote>
 *
 * <p>Subclasses that wish to render the page links in a different format will
 * override the {@link #buildPaginationDisplay()} method. The implementation of
 * this method must set the selection model used to retrieve the page number by
 * calling the {@link
 * #setPageNumSelectionModel(SingleSelectionModel)} method. Aside from changing
 * the display, this pagination component will hide itself if
 * {@link PaginationModelBuilder#getTotalSize(Paginator, PageState)} is less
 * than the page size (i.e., a single page can be used to display the entire
 * results). This default behavior can be changed by calling {@link #setHiddenIfSinglePage(boolean)}
 * with
 * <code>false</code>.
 *
 * @see PaginationModelBuilder
 *
 * @author Phong Nguyen
 * @version $Id$
 * @since 4.6.10
 *
 */
public class Paginator extends SimpleContainer implements Resettable {

    // $Change: 44247 $
    // $Revision$
    // $DateTime: 2004/08/16 18:10:38 $
    // $Author$
    // The builder which returns the total number of items to
    // paginate.
    private PaginationModelBuilder m_builder;
    // The selection model that returns the selected page number. The
    // contained ParameterModel should be a StringParameter, since
    // this is the default for List and Table.
    private SingleSelectionModel m_pageNumModel;
    // The selection model that returns the number of items to display
    // for one page.
    private SingleSelectionModel m_pageSizeModel;
    // This is used to determine if this component should be hidden
    // when there is only a single page to display. Defaults to true.
    private boolean m_hiddenIfSinglePage;
    // A label that contains a space, "&nbsp;". This is used to insert
    // spaces between the page links from within the list's
    // generateXML() method.
    private BoxPanel m_spacePanel;
    private Label m_space;
    // defined in List.java
    private static final String _SELECT_EVENT = "s";

    /**
     * Constructor.
     *
     * @param builder The builder used to retrieve the total number of results
     * to paginate.
     * @param defaultPageSize The default number of results to display on each
     * page.
     *
     */
    public Paginator(PaginationModelBuilder builder, int defaultPageSize) {
        super();

        m_builder = builder;
        m_hiddenIfSinglePage = true;

        // Create the selection model which returns the size of one
        // page and set its default value.
        IntegerParameter sizeParam = new IntegerParameter("ps");
        sizeParam.setDefaultValue(new Integer(defaultPageSize));
        sizeParam.setDefaultOverridesNull(true);
        m_pageSizeModel = new ParameterSingleSelectionModel(sizeParam);

        // Builds the display for rendering page links, this also sets
        // the page number selection model.
        buildPaginationDisplay();
    }

    /**
     * Builds the display for rendering the page links. Subclasses can override
     * this method to provide a different rendering of the page links. If this
     * is the case, make sure that the {@link
     * #setPageNumSelectionModel(SingleSelectionModel)} method is called to set
     * the selection model for retrieving the selected page number.
     *
     */
    protected void buildPaginationDisplay() {
        PaginatorList list = new PaginatorList(new PageListModelBuilder(this, getPaginationModelBuilder()));
        setPageNumSelectionModel(list.getSelectionModel());

        // This is used within the list's generateXML() method to
        // insert spaces between the page links.
        m_space = new Label("&nbsp;", false);
        m_spacePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        m_spacePanel.add(m_space);

        BoxPanel display = new BoxPanel(BoxPanel.HORIZONTAL);
        display.add(new Label(GlobalizationUtil.globalize("bebop.page")));
        display.add(list);
        display.add(m_space);
        add(display);
    }

    /**
     * Sets the selection model that is used for returning the selected page
     * number. Subclasses that override the {@link
     * #buildPaginationDisplay()} method will need to call this method.
     *
     * @param pageNumModel The selection model used for returning the selected
     * page number.
     *
     */
    protected void setPageNumSelectionModel(SingleSelectionModel pageNumModel) {
        m_pageNumModel = pageNumModel;
    }

    /**
     * Returns the selected page number.
     *
     * @param state Represents the current state of the request.
     * @return The selected page number.
     *
     */
    public int getSelectedPageNum(PageState state) {
        String pageNum = (String) m_pageNumModel.getSelectedKey(state);
        if (pageNum == null) {
            m_pageNumModel.setSelectedKey(state, "1");
            return 1;
        }
        return Integer.parseInt(pageNum);
    }

    /**
     * Sets the selected page number.
     *
     * @param state Represents the current state of the request.
     * @param pageNum The number of the page to set as selected.
     *
     */
    public void setSelectedPageNum(PageState state, int pageNum) {
        m_pageNumModel.setSelectedKey(state, String.valueOf(pageNum));
    }

    /**
     * Returns the number of items to display per page.
     *
     * @param state Represents the current state of the request.
     * @return The number of items to display per page.
     *
     */
    public int getPageSize(PageState state) {
        return ((Integer) m_pageSizeModel.getSelectedKey(state)).intValue();
    }

    /**
     * Sets the number of items to display per page.
     *
     * @param state Represents the current state of the request.
     * @param pageSize The number of items to display per page.
     *
     */
    public void setPageSize(PageState state, int pageSize) {
        m_pageSizeModel.setSelectedKey(state, new Integer(pageSize));
    }

    /**
     * This returns the total number of pages that will be displayed by this
     * paginator.
     *
     * @param state The page state
     */
    public int getTotalPages(PageState state) {
        int totalSize = m_builder.getTotalSize(this, state);
        int pageSize = getPageSize(state);
        int minSize = totalSize / pageSize;
        if (minSize * pageSize == totalSize) {
            return minSize;
        } else {
            return minSize + 1;
        }
    }

    /**
     * Returns the number of the first item to display.
     *
     * @param state Represents the current state of the request.
     * @return The number of the first item to display.
     *
     */
    public int getFirst(PageState state) {
        return ((getSelectedPageNum(state) - 1) * getPageSize(state)) + 1;
    }

    /**
     * Returns the number of the last item to display.
     *
     * @param state Represents the current state of the request.
     * @return The number of teh last item to display.
     *
     */
    public int getLast(PageState state) {
        // NOTE: If the returned integer is used for
        // DataQuery.setRange(int, int), then it needs to be
        // incremented by 1.
        return (getSelectedPageNum(state) * getPageSize(state));
    }

    /**
     * Returns the builder that is used to retrieve the total number of items to
     * paginate.
     *
     * @return The builder used to compute the total number of items to
     * paginate.
     *
     */
    public PaginationModelBuilder getPaginationModelBuilder() {
        return m_builder;
    }

    /**
     * Specifies whether this component is hidden if there is only a single page
     * of items to display.
     *
     * @return Returns
     * <code>true</code> if this component is hidden when there is only a single
     * page to view.
     *
     */
    public boolean isHiddenIfSinglePage() {
        return m_hiddenIfSinglePage;
    }

    /**
     * Sets whether or not this component should be hidden if there is only a
     * single page of items to display.
     *
     * @param isHidden By default, this component will be hidden when there is
     * only a single page to display. Set this to
     * <code>false</code> if this is not the desired effect.
     *
     */
    public void setHiddenIfSinglePage(boolean isHidden) {
        m_hiddenIfSinglePage = isHidden;
    }

    /**
     * Returns
     * <code>true</code> if this component is visible. This component will not
     * be visible if the paginator model builder's isVisible method reports
     * false. If this component is set to be hidden when there is only a single
     * page to display, then the total page size returned from the {@link PaginationModelBuilder}
     * object must be greater than the number of items per page.
     *
     * @param state Represents the current state of the request.
     * @return Returns
     * <code>true</code> if this component is visible.
     *
     * @see #getPageSize(PageState)
     * @see PaginationModelBuilder#getTotalSize(Paginator, PageState)
     *
     */
    @Override
    public boolean isVisible(PageState state) {
        return (super.isVisible(state)
                && m_builder.isVisible(state)
                && ((!m_hiddenIfSinglePage)
                || m_builder.getTotalSize(this, state) > getPageSize(state)));
    }

    /**
     * Register the page size selection model.
     *
     * @param p The page to register with.
     *
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.setVisibleDefault(m_spacePanel, false);
        p.addComponentStateParam(this, m_pageSizeModel.getStateParameter());
    }

    /**
     * Resets this component by clearing the selected page.
     *
     * @param state Represents the current state of the request.
     *
     */
    public void reset(PageState state) {
        m_pageNumModel.clearSelection(state);
    }

    /**
     * This class was added to provide greater flexibility in displaying the
     * page links. Links can be displayed in a space separated list to support
     * wrapping or in a table with a fixed number of page links per row. By
     * default the page links will be rendered inside a one-cell table. If the
     * number of page links to display per line is specified, then each page
     * link will be rendered in it's own cell.
     *
     */
    private class PaginatorList extends List {

        public PaginatorList(ListModelBuilder builder) {
            super(builder);
        }

        /**
         * If the number of page links to display per line is specified then the
         * generated xml will be similar to that of a Table. Else, it will be
         * similar to a simple container.
         *
         * @param state Represents the current state of the request.
         * @param parent The element to which to attach the XML.
         *
         */
        @Override
        public void generateXML(PageState state, Element parent) {
            if (!this.isVisible(state)) {
                return;
            }

            // maybe display the Previous arrow
            if (getSelectedPageNum(state) > 1) {
                state.setControlEvent(this, _SELECT_EVENT,
                        Integer.toString(getSelectedPageNum(state) - 1));
                (new ControlLink(new Label("<"))).generateXML(state, parent);
                (new Label(" ")).generateXML(state, parent);
                state.setControlEvent(this, _SELECT_EVENT,
                        Integer.toString(getSelectedPageNum(state) - 1));
                (new ControlLink(new Label(GlobalizationUtil.globalize("bebop.previous")))).generateXML(state, parent);
                (new Label("  ")).generateXML(state, parent);
            }

            ListModel m = getModel(state);
            Object selKey = getSelectedKey(state);
            Component c;
            int i = 0;
            while (m.next()) {
                String key = m.getKey();
                // Converting both keys to String for comparison
                // since ListModel.getKey returns a String
                boolean selected = (selKey != null) && (key != null)
                        && selKey.toString().equals(key.toString());

                state.setControlEvent(this, _SELECT_EVENT, m.getKey());
                c = getCellRenderer().getComponent(this, state, m.getElement(),
                        m.getKey(), i, selected);
                c.generateXML(state, parent);
                m_space.generateXML(state, parent);
                i += 1;
            }

            // maybe display the next arrow
            if (getSelectedPageNum(state) < getTotalPages(state)) {
                state.setControlEvent(this, _SELECT_EVENT,
                        Integer.toString(getSelectedPageNum(state) + 1));
                (new Label("  ")).generateXML(state, parent);
                (new ControlLink(new Label(GlobalizationUtil.globalize("bebop.next")))).generateXML(state, parent);
                (new Label(" ")).generateXML(state, parent);
                state.setControlEvent(this, _SELECT_EVENT,
                        Integer.toString(getSelectedPageNum(state) + 1));
                (new ControlLink(new Label(">"))).generateXML(state, parent);
            }

            state.clearControlEvent();
        }
    }

    /**
     * A list model builder for the pagination list.
     *
     */
    private class PageListModelBuilder extends LockableImpl
            implements ListModelBuilder {

        Paginator m_paginator;
        PaginationModelBuilder m_builder;

        public PageListModelBuilder(Paginator paginator,
                PaginationModelBuilder builder) {
            super();
            m_paginator = paginator;
            m_builder = builder;
        }

        public ListModel makeModel(List list, PageState state) {
            return new PageListModel(m_builder.getTotalSize(m_paginator, state),
                    getPageSize(state),
                    getSelectedPageNum(state));
        }
    }

    /**
     * A list model for the pagination list which is used to generate the text
     * for the page links.
     *
     */
    private class PageListModel extends LockableImpl
            implements ListModel {

        int m_totalSize;
        int m_pageSize;
        int m_pageCount;
        int m_current;

        public PageListModel(int totalSize, int pageSize, int current) {
            super();
            m_totalSize = totalSize;
            m_pageSize = pageSize;
            m_pageCount = 0;
            m_current = current;
        }

        public boolean next() {
            if (m_pageCount * m_pageSize < m_totalSize) {
                m_pageCount += 1;
                return true;
            }
            return false;
        }

        public Object getElement() {
            /*
             * TODO: Remove or relocate this int begin = ((m_pageCount-1) *
             * m_pageSize) + 1; int end = (m_pageCount) * m_pageSize; if (end >
             * m_totalSize) { end = m_totalSize; } return "" + begin + "-" +
             * end;
             */
            if (Math.abs(m_current - m_pageCount) <= 5
                    || m_pageCount == 1
                    || m_pageCount * m_pageSize >= m_totalSize) {
                return Integer.toString(m_pageCount);
            } else {
                return ".";
            }
        }

        public String getKey() {
            return Integer.toString(m_pageCount);
        }
    }
}
