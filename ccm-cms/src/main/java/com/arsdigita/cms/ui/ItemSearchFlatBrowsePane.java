/*
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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.LockableImpl;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class ItemSearchFlatBrowsePane extends SimpleContainer {

    private static final String QUERY_PARAM = "queryStr";
    public static final String WIDGET_PARAM = "widget";
    public static final String SEARCHWIDGET_PARAM = "searchWidget";
    public static final String FILTER_SUBMIT = "filterSubmit";
    private final Table resultsTable;
    private final Paginator paginator;
    private final StringParameter queryParam;
    private final QueryFieldsRequestLocal queryFields
                                              = new QueryFieldsRequestLocal();
    private final static CMSConfig CMS_CONFIG = CMSConfig.getConfig();

    public ItemSearchFlatBrowsePane() {
        //super(name);
        super();

        setIdAttr("itemSearchFlatBrowse");

        //final BoxPanel mainPanel = new BoxPanel(BoxPanel.VERTICAL);
        final LayoutPanel mainPanel = new LayoutPanel();

        queryParam = new StringParameter(QUERY_PARAM);

        mainPanel.setLeft(new FilterForm());

        resultsTable = new ResultsTable();
        paginator = new Paginator(
            (PaginationModelBuilder) resultsTable.getModelBuilder(),
            CMS_CONFIG.getItemSearchFlatBrowsePanePageSize());
        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);
        body.add(paginator);

        body.add(resultsTable);

        mainPanel.setBody(body);
        add(mainPanel);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, queryParam);
    }

    public void addQueryField(final String queryField) {
        queryFields.addQueryField(queryField);
    }

    void resetQueryFields() {
        queryFields.reset();
    }

    private class ResultsTable extends Table {

        private static final String TABLE_COL_TITLE = "title";
        private static final String TABLE_COL_PLACE = "place";
        private static final String TABLE_COL_TYPE = "type";

        public ResultsTable() {
            super();
            setEmptyView(new Label(new GlobalizedMessage(
                "cms.ui.item_search.flat.no_items",
                CmsConstants.CMS_BUNDLE)));
            setClassAttr("dataTable");

            final TableColumnModel columnModel = getColumnModel();
            columnModel.add(new TableColumn(
                0,
                new GlobalizedMessage("cms.ui.item_search.flat.title",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_TITLE));
            columnModel.add(new TableColumn(
                1,
                new GlobalizedMessage("cms.ui.item_search.flat.place",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_PLACE));
            columnModel.add(new TableColumn(
                2,
                new GlobalizedMessage("cms.ui.item_search.flat.type",
                                      CmsConstants.CMS_BUNDLE).localize(),
                TABLE_COL_TYPE));

            setModelBuilder(new ResultsTableModelBuilder());

            columnModel.get(0).setCellRenderer(new TitleCellRenderer());
        }

    }

    private class ResultsTableModelBuilder extends LockableImpl implements
        TableModelBuilder,
        PaginationModelBuilder {

        private final RequestLocal collection = new RequestLocal();

        @Override
        @SuppressWarnings("unchecked")
        public TableModel makeModel(final Table table, final PageState state) {

            if (collection.get(state) == null) {
                query(state);
            }

            return new ResultsTableModel(
                table,
                state,
                (List<ContentItem>) collection.get(state));
        }

        @Override
        public int getTotalSize(final Paginator paginator,
                                final PageState state) {
            if (collection.get(state) == null) {
                query(state);
            }
            return (int) ((List<ContentItem>) collection.get(state)).size();
        }

        @Override
        public boolean isVisible(final PageState state) {
            return true;
        }

        private void query(final PageState state) {
            //Nothing
        }

    }

    private class ResultsTableModel implements TableModel {

        private final Table table;
        private final List<ContentItem> collection;
        private ContentItem currentItem;
        private int index = -1;

        public ResultsTableModel(final Table table,
                                 final PageState state,
                                 final List<ContentItem> collection) {
            this.table = table;

            this.collection = collection;

        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            index++;

            if (collection != null && index < collection.size()) {
                currentItem = collection.get(index);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return currentItem.getDisplayName();
                case 1:
                    return getItemPath(currentItem);
                case 2:
                    if (currentItem.getContentType() == null) {
                        return "";
                    } else {
                        return currentItem.getContentType().getDisplayName();
                    }
                default:
                    return null;
            }
        }

        private String getItemPath(final ContentItem item) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemManager itemManager = cdiUtil.findBean(
                ContentItemManager.class);

            return itemManager.getItemPath(item);
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return currentItem.getObjectId();
        }

    }

    private class TitleCellRenderer extends LockableImpl
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
                return new Label("???");
            }

            final Link link = new Link(value.toString(), "");

            final String widget = (String) state.getValue(new StringParameter(
                WIDGET_PARAM));
            final String searchWidget = (String) state.getValue(
                new StringParameter(
                    SEARCHWIDGET_PARAM));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemRepository itemRepo = cdiUtil.findBean(
                ContentItemRepository.class);
            final Optional<ContentItem> page = itemRepo.findById(Long.parseLong(
                (String) key));

            final boolean useURL = "true".equals(state.getValue(
                new StringParameter(
                    ItemSearchPopup.URL_PARAM)));

            final String targetValue;
            if (useURL) {
                targetValue = ItemSearchPopup.getItemURL(
                    state.getRequest(), page.get().getObjectId());
            } else {
                targetValue = key.toString();
            }

            final StringBuffer buffer = new StringBuffer(30);
            buffer.append(String.format(
                "window.opener.document.%s.value=\"%s\"; ", widget,
                targetValue));
            if (searchWidget != null) {
                buffer.append(String.format(
                    "window.opener.document.%s.value=\"%s\"; ",
                    searchWidget,
                    page.get().getDisplayName().replace("\"", "\\\"")));
            }

            buffer.append("self.close(); return false;");

            link.setOnClick(buffer.toString());

            return link;
        }

    }

    private class FilterForm extends Form implements FormInitListener,
                                                     FormProcessListener {

        private final Submit submit;

        public FilterForm() {
            super("ItemSearchFlatBrowsePane");

            add(new Label(new GlobalizedMessage(
                "cms.ui.item_search.flat.filter",
                CmsConstants.CMS_BUNDLE)));
            final TextField filter = new TextField(new StringParameter(
                QUERY_PARAM));
            add(filter);

            submit = new Submit(FILTER_SUBMIT,
                                new GlobalizedMessage(
                                    "cms.ui.item_search.flat.filter.submit",
                                    CmsConstants.CMS_BUNDLE));
            add(submit);

            addInitListener(this);
            addProcessListener(this);
        }

        @Override
        public void init(final FormSectionEvent fse) throws FormProcessException {
            final PageState state = fse.getPageState();
            final FormData data = fse.getFormData();

            final String query = (String) data.get(QUERY_PARAM);
            if ((query == null) || query.isEmpty()) {
                data.setParameter(
                    QUERY_PARAM,
                    new ParameterData(
                        queryParam,
                        state.getValue(
                            new StringParameter(ItemSearchPopup.QUERY))));
                state.setValue(queryParam,
                               data.getParameter(QUERY_PARAM).getValue());
            }
        }

        @Override
        public void process(final FormSectionEvent fse) throws
            FormProcessException {
            final FormData data = fse.getFormData();
            final PageState state = fse.getPageState();

            state.setValue(queryParam, data.get(QUERY_PARAM));
            state.setValue(new StringParameter(ItemSearchPopup.QUERY),
                           data.get(QUERY_PARAM));
        }

    }

    private class QueryFieldsRequestLocal extends RequestLocal {

        private List<String> queryFields = new ArrayList<>();

        @Override
        protected Object initialValue(final PageState state) {
            return new ArrayList<>();
        }

        public List<String> getQueryFields() {
            return queryFields;
        }

        public void setQueryFields(final List<String> queryFields) {
            this.queryFields = queryFields;
        }

        public void addQueryField(final String queryField) {
            queryFields.add(queryField);
        }

        public void reset() {
            queryFields = new ArrayList<>();
        }

    }

}
