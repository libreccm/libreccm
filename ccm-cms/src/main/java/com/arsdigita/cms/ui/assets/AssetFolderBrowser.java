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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.folder.FolderBrowser;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;

import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.dispatcher.ItemResolver;

/**
 * Browse folder and assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetFolderBrowser extends Table {

    protected static final String SORT_ACTION_UP = "sortActionUp";
    protected static final String SORT_ACTION_DOWN = "sortActionDown";
    protected final static String SORT_KEY_NAME = "name";
    protected final static String SORT_KEY_TITLE = "title";
    protected final static String SORT_KEY_TYPE = "type";
    protected final static String SORT_KEY_LAST_MODIFIED_DATE = "lastModified";
    protected final static String SORT_KEY_CREATION_DATE = "creationDate";

    private final FolderSelectionModel folderSelectionModel;
    private TableActionListener folderChanger;
    private TableActionListener folderDeleter;
    private TableColumn nameColumn;
    private TableColumn deleteColumn;
    private final StringParameter sortTypeParameter = new StringParameter(
            "sortType");
    private final StringParameter sortDirectionParameter = new StringParameter(
            "sortDir");

    private Paginator paginator;
    private long folderSize;

    public AssetFolderBrowser(final FolderSelectionModel folderSelectionModel) {
        super();
        sortTypeParameter.setDefaultValue(SORT_KEY_NAME);
        sortDirectionParameter.setDefaultValue(SORT_ACTION_UP);

        this.folderSelectionModel = folderSelectionModel;

        initComponents();
    }

    private void initComponents() {
        setModelBuilder(new AssetFolderBrowserTableModelBuilder());

        final GlobalizedMessage[] headers = {
            globalize("cms.ui.folder.name"),
            globalize("cms.ui.folder.title"),
            globalize("cms.ui.folder.type"),
            globalize("cms.ui.folder.creation_date"),
            globalize("cms.ui.folder.last_modified"),
            globalize("cms.ui.folder.action")};

        setModelBuilder(new AssetFolderBrowserTableModelBuilder());
        setColumnModel(new DefaultTableColumnModel(headers));
        setClassAttr("dataTable");

        getHeader().setDefaultRenderer(new DefaultTableCellRenderer());

        nameColumn = getColumn(AssetFolderBrowserTableModel.COL_NAME);
        nameColumn.setCellRenderer(new NameCellRenderer());
        nameColumn.setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_NAME));

        getColumn(AssetFolderBrowserTableModel.COL_CREATION_DATE)
                .setHeaderRenderer(
                        new HeaderCellRenderer(SORT_KEY_CREATION_DATE));
        getColumn(AssetFolderBrowserTableModel.COL_CREATION_DATE)
                .setCellRenderer(new DateCellRenderer());

        getColumn(AssetFolderBrowserTableModel.COL_LAST_MODIFIED)
                .setHeaderRenderer(new HeaderCellRenderer(
                        SORT_KEY_LAST_MODIFIED_DATE));
        getColumn(AssetFolderBrowserTableModel.COL_LAST_MODIFIED)
                .setCellRenderer(new DateCellRenderer());

        deleteColumn = getColumn(AssetFolderBrowserTableModel.COL_DELETEABLE);
        deleteColumn.setCellRenderer(new ActionCellRenderer());
        deleteColumn.setAlign("center");

        folderChanger = new FolderChanger();
        addTableActionListener(folderChanger);

        folderDeleter = new ItemDeleter();
        addTableActionListener(folderDeleter);

        setEmptyView(new Label(globalize("cms.ui.folder.no_assets")));
    }

    @Override
    public void register(final Page page) {

        super.register(page);
        
        page.addComponentStateParam(this, folderSelectionModel.
                                    getStateParameter());
        page.addComponentStateParam(this, sortTypeParameter);
        page.addComponentStateParam(this, sortDirectionParameter);
    }

    protected FolderSelectionModel getFolderSelectionModel() {
        return folderSelectionModel;
    }

    protected Paginator getPaginator() {
        return paginator;
    }

    protected void setPaginator(final Paginator paginator) {
        this.paginator = paginator;
    }
    
    protected String getSortType(final PageState state) {
        return (String) state.getValue(sortTypeParameter);
    }

    protected String getSortDirection(final PageState state) {
        return (String) state.getValue(sortDirectionParameter);
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     */
    private GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);

    }

    private class HeaderCellRenderer extends DefaultTableCellRenderer {

        private final String headerKey;

        public HeaderCellRenderer(final String headerKey) {
            super(true);
            this.headerKey = headerKey;
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final GlobalizedMessage headerName = (GlobalizedMessage) value;
            final String sortKey = (String) state.getValue(sortTypeParameter);
            final boolean isCurrentKey = sortKey.equals(key);
            final String currentSortDirection = (String) state
                    .getValue(sortDirectionParameter);
            final String imageUrlStub;

            if (SORT_ACTION_UP.equals(currentSortDirection)) {
                imageUrlStub = "gray-triangle-up.gif";
            } else {
                imageUrlStub = "gray-triangle-down.gif";
            }

            final ControlLink link = new ControlLink(new Label(headerName)) {

                @Override
                public void setControlEvent(final PageState state) {
                    String sortDirectionAction;
                    // by default, everything sorts "up" unless it
                    // is the current key and it is already pointing up
                    if (SORT_ACTION_UP.equals(currentSortDirection)
                                && isCurrentKey) {
                        sortDirectionAction = SORT_ACTION_DOWN;
                    } else {
                        sortDirectionAction = SORT_ACTION_UP;
                    }
                    state.setControlEvent(table,
                                          sortDirectionAction,
                                          headerKey);
                }

            };
            final Label label = new Label();
            label.setLabel(headerName);
            label.setClassAttr("folderBrowserLink");
            label.setOutputEscaping(false);
            label.setFontWeight(Label.BOLD);

            final SimpleContainer container = new SimpleContainer();
            container.add(label);
            if (isCurrentKey) {
                Image image = new Image("/assets/" + imageUrlStub);
                image.setBorder("0");
                container.add(image);
            }
            link.setChild(container);
            return link;
        }

    }

    /**
     * Produce links to view an item or control links for folders to change into
     * the folder.
     */
    private class NameCellRenderer extends DefaultTableCellRenderer {

        public NameCellRenderer() {
            super(true);
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final String name = (String) value;
            final ContentSection section = CMS.getContext().
                    getContentSection();
            final ContentSectionManager sectionManager = CdiUtil.
                    createCdiUtil()
                    .findBean(ContentSectionManager.class);

            final boolean isFolder = ((AssetFolderBrowserTableModel) table
                                      .getTableModel(state))
                    .isFolder();
            final long objectId = getObjectId(key);

            if (isFolder) {
                //return new ControlLink(new Text(name));
                return super.getComponent(table,
                                          state,
                                          value,
                                          isSelected,
                                          objectId,
                                          row,
                                          column);
            } else {
//                    return new Link(new Text(name),
//                                    itemResolver.generateItemURL(state,
//                                                                 objectId,
//                                                                 name,
//                                                                 section,
//                                                                 "DRAFT"));
                return new Text(name);
            }
        }

    }

    private class DateCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (value instanceof Date) {
                final Date date = (Date) value;
                return new Text(String.format("%1$TF %1$TT", date));
            } else if (value == null) {
                return new Text("");
            } else {
                return new Text(value.toString());
            }
        }

    }

    /**
     * Produce delete links for items and non-empty folders.
     */
    private class ActionCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if ((!(Boolean) value)) {
                return new Label("&nbsp;", false);
            } else {
                final ControlLink link = new ControlLink(
                        new Label(new GlobalizedMessage("cms.ui.folder.delete",
                                                        CmsConstants.CMS_BUNDLE)));
                link.setConfirmation(
                        new GlobalizedMessage(
                                "cms.ui.folder.delete_confirmation",
                                CmsConstants.CMS_BUNDLE));
                return link;
            }
        }

    }

    // Deletes an item
    private class ItemDeleter extends TableActionAdapter {

        @Override
        public void cellSelected(final TableActionEvent event) {
            int col = event.getColumn();

            if (deleteColumn != getColumn(col)) {
                return;
            }

            final PageState state = event.getPageState();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetFolderBrowserController controller = cdiUtil.findBean(
                    AssetFolderBrowserController.class);
            controller.deleteObject((String) event.getRowKey());

            ((Table) event.getSource()).clearSelection(state);
        }

    }

    private class FolderChanger extends TableActionAdapter {

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();
            final int col = event.getColumn();

            if (nameColumn != getColumn(col)) {
                return;
            }

            clearSelection(state);
            getFolderSelectionModel().setSelectedKey(
                    state,
                    getObjectId(event.getRowKey()));
        }

    }

    private long getObjectId(final Object key) {

        final String keyStr = (String) key;

        if (keyStr.startsWith("folder-")) {
            return Long.parseLong(keyStr.substring("folder-".length()));
        } else if (keyStr.startsWith("asset-")) {
            return Long.parseLong(keyStr.substring("asset-".length()));
        } else {
            return Long.parseLong(keyStr);
        }

    }

}
