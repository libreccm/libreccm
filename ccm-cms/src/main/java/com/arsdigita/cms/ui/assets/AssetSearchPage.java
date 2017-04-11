/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import org.librecms.CmsConstants;

/**
 * Page contains the widgets for selecting an asset.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetSearchPage extends CMSPage {

    private static final String QUERY_PARAM = "query";
    private static final String WIDGET_PARAM = "widget";
    private static final String ASSET_TYPE_PARAM = "assettype";

    private final LongParameter contentSectionId;

    private TextField query;

    public AssetSearchPage() {
        super(new Label(new GlobalizedMessage("cms.ui.assets.search_page.title",
                                              CmsConstants.CMS_BUNDLE)),
              new SimpleContainer());

        addGlobalStateParam(new StringParameter(ASSET_TYPE_PARAM));
        addGlobalStateParam(new StringParameter(WIDGET_PARAM));
        addGlobalStateParam(new StringParameter(QUERY_PARAM));

        contentSectionId = new LongParameter("content-section-id");

        final LayoutPanel mainPanel = new LayoutPanel();

        final Form queryForm = new Form("asset-search-page-query-form");
        queryForm.add(new Label(new GlobalizedMessage(
                "cms.ui.assets.search_page.query",
                CmsConstants.CMS_BUNDLE)));
        query = new TextField("asset-search-page-query-form");
        queryForm.add(query);
        final Submit querySubmit = new Submit(new GlobalizedMessage(
                "cms.ui.assets.search_page.query.submit"));
        queryForm.add(querySubmit);
        
        queryForm.addInitListener(new FormInitListener() {
            @Override
            public void init(final FormSectionEvent event) 
                    throws FormProcessException {
                
                final PageState state = event.getPageState();
                final FormData data = event.getFormData();
                
                final String query = (String) data.get(QUERY_PARAM);
                
            }
        });

    }

}
