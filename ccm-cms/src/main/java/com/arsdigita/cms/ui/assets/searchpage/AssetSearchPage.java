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
package com.arsdigita.cms.ui.assets.searchpage;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.LockableImpl;
import java.util.List;
import org.libreccm.cdi.utils.CdiUtil;

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

    private static final int RESULTS_TABLE_COL_TITLE = 0;
    private static final int RESULTS_TABLE_COL_PLACE = 1;
    private static final int RESULTS_TABLE_COL_TYPE = 2;

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

        mainPanel.setLeft(queryForm);

        final Table resultsTable = new Table();
        resultsTable.setEmptyView(
            new Label(
                new GlobalizedMessage(
                    "cms.ui.assets.search_page.none",
                    CmsConstants.CMS_BUNDLE)));
        resultsTable.setClassAttr("dataTable");

        final TableColumnModel columnModel = resultsTable.getColumnModel();
        columnModel.add(new TableColumn(
            RESULTS_TABLE_COL_TITLE,
            new GlobalizedMessage(
                "cms.ui.assets.search_page.results_table.title",
                CmsConstants.CMS_BUNDLE)));
        columnModel.add(new TableColumn(
            RESULTS_TABLE_COL_PLACE,
            new GlobalizedMessage(
                "cms.ui.assets.search_page.results_table.place",
                CmsConstants.CMS_BUNDLE)));
        columnModel.add(new TableColumn(
            RESULTS_TABLE_COL_TYPE,
            new GlobalizedMessage(
                "cms.ui.assets.search_page.results_table.type",
                CmsConstants.CMS_BUNDLE)));
        resultsTable.setModelBuilder(new ResultsTableModelBuilder());

        mainPanel.setBody(resultsTable);

    }

    private class ResultsTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetSearchPageController controller = cdiUtil
                .findBean(AssetSearchPageController.class);

            final List<ResultsTableRow> rows = controller
                .findAssets((String) query.getValue(state));
            return new ResultsTableModel(rows);
        }

    }
     
    private class TitleCellRenderer 
        extends LockableImpl 
        implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table, 
                                      final PageState state, 
                                      final Object value,
                                      final boolean isSelected, 
                                      final Object key, 
                                      final int row,
                                      final int column) {
            
            if (value == null) {
                return new Text("???");
            }
            
            final Link link = new Link(new Text(value.toString()), "");
            
            return link;
            
            
        }
        
        
    }

}
