/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.BigDecimalParameter;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ui.search.ItemQueryComponent;
import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.search.ui.ResultsPane;
import com.arsdigita.search.ui.QueryGenerator;
import com.arsdigita.toolbox.ui.LayoutPanel;

import org.librecms.CmsConstants;

/**
 * Contains a form for specifying search parameters, as well as a
 * {@link com.arsdigita.search.ui.ResultsPane} which will perform the search and
 * display the results
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Id: ItemSearchSection.java 1940 2009-05-29 07:15:05Z terry $
 */
public class ItemSearchSection extends FormSection implements Resettable {

    public static final String SINGLE_TYPE_PARAM = "single_type";

    private ItemQueryComponent itemQueryComponent;
    private Component resultsComponent;

    /**
     * Construct a new <code>ItemSearchSection</code> component
     *
     * @param context the context for the retrieved items. Should be
     * {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param limitToContentSection limit the search to the current content
     * section
     */
    public ItemSearchSection(final String context,
                             final boolean limitToContentSection) {
        this(null, context, limitToContentSection);
    }

    /**
     * Construct a new <code>ItemSearchSection</code> component
     *
     * @param context the context for the retrieved items. Should be
     * {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param name The name of the search parameter for the particular
     * FormSection
     * @param limitToContentSection limit the search to the current content
     * section
     */
    public ItemSearchSection(final String name,
                             final String context,
                             final boolean limitToContentSection) {
        this(name, context, limitToContentSection, null);
    }

    public ItemSearchSection(final String name,
                             final String context,
                             final boolean limitToContentSection,
                             final ContentType type) {
        super(new SimpleContainer());
        final String thisName;
        if (name == null) {
            thisName = "itemSearch";
        } else {
            thisName = name;
        }

        if (type == null) {
            itemQueryComponent = createQueryGenerator(context,
                                                      limitToContentSection);
        } else {
            itemQueryComponent = createQueryGenerator(context,
                                                      limitToContentSection,
                                                      type);
        }
        resultsComponent = createResultsPane(itemQueryComponent);

        LayoutPanel searchPanel = new LayoutPanel();
        searchPanel.setLeft(itemQueryComponent);
        searchPanel.setBody(resultsComponent);
        this.add(searchPanel);

        addQueryGenerator(this);
        addResultsPane(this);
        addFormListener();

        setClassAttr("itemSearch");
    }


    @Override
    public void reset(final PageState state) {
        resultsComponent.setVisible(state, false);
    }

    protected ItemQueryComponent createQueryGenerator(
            final String context, final boolean limitToContentSection) {
        return new ItemQueryComponent(context, limitToContentSection);
    }

    protected ItemQueryComponent createQueryGenerator(
            final String context,
            final boolean limitToContentSection,
            final ContentType type) {

        return new ItemQueryComponent(context, limitToContentSection, type);
    }

    protected Component createResultsPane(QueryGenerator generator) {
        ResultsPane pane = new ResultsPane(generator);
        pane.setRelativeURLs(true);
        pane.setSearchHelpMsg(new GlobalizedMessage("cms.ui.search.help",
                                                    CmsConstants.CMS_BUNDLE));
        pane.setNoResultsMsg(new GlobalizedMessage("cms.ui.search.no_results",
                                                   CmsConstants.CMS_BUNDLE));
        return pane;
    }

    protected void addResultsPane(final Container container) {
        container.add(resultsComponent);
    }

    protected void addQueryGenerator(final Container container) {
        container.add(itemQueryComponent);
    }

    protected void processQuery(final PageState state) {
        resultsComponent.setVisible(state, itemQueryComponent.hasQuery(state));
    }

    protected void addFormListener() {
        addProcessListener(new SearchFormProcessListener());
    }

    // Hide results by default
    @Override
    public void register(final Page page) {
        super.register(page);
        page.setVisibleDefault(resultsComponent, false);
        page.addGlobalStateParam(new BigDecimalParameter(SINGLE_TYPE_PARAM));
    }

    /**
     * Displays the "keywords" and "content types" widgets
     */
    private class SearchFormProcessListener implements FormProcessListener {

        @Override
        public void process(final FormSectionEvent event)
                throws FormProcessException {

            PageState s = event.getPageState();
            processQuery(s);
        }
    }
}
