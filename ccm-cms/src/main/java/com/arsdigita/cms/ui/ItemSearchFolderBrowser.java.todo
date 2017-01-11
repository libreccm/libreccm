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

import com.arsdigita.bebop.BebopConfig;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionConfig;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Browse folders and items. If the user clicks on a folder, the folder
 * selection model is updated. If the user clicks on any other item, an separate
 * item selection model is updated.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemSearchFolderBrowser extends Table {

    private static final Logger LOGGER = LogManager.getLogger(
        ItemSearchFolderBrowser.class);
    private static final GlobalizedMessage[] HEADERS = {
        globalize("cms.ui.folder.name"),
        globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.type")};
    private FolderSelectionModel currentFolder;
    private TableActionListener folderChanger;
    private TableActionListener deleter;
    private TableActionListener indexChanger;
    private TableColumn nameColumn;
    private Paginator paginator;

    public ItemSearchFolderBrowser(final FolderSelectionModel currentFolder) {

        super((FolderTableModelBuilder) null, HEADERS);

        final FolderTableModelBuilder builder = new FolderTableModelBuilder();
        setModelBuilder(builder);

        paginator = new Paginator(
            builder, CMSConfig.getConfig().getFolderBrowseListSize());

        this.currentFolder = currentFolder;

        setClassAttr("dataTable");

        getHeader().setDefaultRenderer(
            new com.arsdigita.cms.ui.util.DefaultTableCellRenderer());
        nameColumn = getColumn(0);
        nameColumn.setCellRenderer(new NameCellRenderer());

        folderChanger = new FolderChanger();
        addTableActionListener(folderChanger);

        setEmptyView(new Label(globalize("cms.ui.folder.no_items")));

        Assert.exists(currentFolder.getStateParameter());
    }

    public Paginator getPaginator() {
        return paginator;
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, currentFolder.getStateParameter());

        page.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent event) {
                // MP: This action listener should only be called when the
                //      folder browser is visible.
                showHideFolderActions(event.getPageState());
            }

        });
    }

    private Folder getCurrentFolder(final PageState state) {
        return (Folder) currentFolder.getSelectedObject(state);
    }

    private void showHideFolderActions(final PageState state) {
        // Empty
    }

    public FolderSelectionModel getFolderSelectionModel() {
        return currentFolder;
    }

    private class FolderTableModelBuilder
        extends AbstractTableModelBuilder implements PaginationModelBuilder {

        private RequestLocal size = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                List<ContentItem> items = getItemCollection(state);

                if (null == items) {
                    return 0;
                }
                return items.size();
            }

        };

        private RequestLocal items = new RequestLocal() {

            @Override
            protected Object initialValue(final PageState state) {
                List<ContentItem> items = getItemCollection(state);

                return items;
            }

        };

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            final FolderSelectionModel selectionModel
                                           = ((ItemSearchFolderBrowser) table)
                    .getFolderSelectionModel();
            final Folder folder = getCurrentFolder(state);

            if (LOGGER.isDebugEnabled()) {
                if (null == folder) {
                    LOGGER.debug("Selected folder is null");
                } else {
                    LOGGER.debug("Selected folder: {} {}",
                                 folder.getDisplayName(),
                                 folder.getObjectId());
                }
            }

            if (folder == null) {
                return Table.EMPTY_MODEL;
            } else {
                table.getRowSelectionModel().clearSelection(state);
                return new FolderTableModel((List<ContentItem>) items.
                    get(state));
            }
        }

        private List<ContentItem> getItemCollection(final PageState state) {
            final Folder folder = getCurrentFolder(state);

            final List<ContentItem> items = folder.getObjects()
                .stream()
                .map(categorization -> categorization.getCategorizedObject())
                .filter(object -> object instanceof ContentItem)
                .map(object -> (ContentItem) object)
                .collect(Collectors.toList());

            return items;
        }

        @Override
        public int getTotalSize(final Paginator paginator,
                                final PageState state) {

            return (int) this.size.get(state);
        }

        /**
         * Indicates whether the paginator should be visible, based on the
         * visibility of the folder browser itself and how many items are
         * displayed
         *
         * @return true if folder browser is visible and there is more than 1
         *         page of items, false otherwise
         */
        @Override
        public boolean isVisible(final PageState state) {
            final int size = (int) this.size.get(state);

            return ItemSearchFolderBrowser.this.isVisible(state)
                       && (size > CMSConfig.getConfig()
                               .getFolderBrowseListSize());
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
            if (value instanceof Folder) {
                return super.getComponent(table,
                                          state,
                                          ((CcmObject) value).getDisplayName(),
                                          isSelected,
                                          key,
                                          row,
                                          column);
            } else if (value instanceof ContentItem) {
                final ContentItem item = (ContentItem) value;

                final ContentSection section = CMS.getContext()
                    .getContentSection();
                final long objectId = (long) key;

                final String name = item.getDisplayName();

                if (section == null) {
                    return new Text(name);
                } else {
                    final SimpleContainer container = new SimpleContainer();

                    final String widget = (String) state.getValue(
                        new StringParameter(
                            ItemSearchPopup.WIDGET_PARAM));
                    String searchWidget = (String) state.getValue(
                        new StringParameter("searchWidget"));
                    boolean useURL = "true".equals(state.getValue(
                        new StringParameter(
                            ItemSearchPopup.URL_PARAM)));

                    String fillString;
                    if (useURL) {
                        fillString = ItemSearchPopup.getItemURL(
                            state.getRequest(), item.getObjectId());
                    } else {
                        fillString = Long.toString(objectId);
                    }

                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final GlobalizationHelper globalizationHelper = cdiUtil
                        .findBean(GlobalizationHelper.class);
                    final String title = item.getTitle().getValue(
                        globalizationHelper.getNegotiatedLocale());

                    final Label jsLabel = new Label(
                        generateJSLabel(objectId,
                                        widget,
                                        searchWidget,
                                        fillString,
                                        title),
                        false);
                    container.add(jsLabel);

                    final String url = "#";

                    final Link link = new Link(name, url);
                    link.setClassAttr("title");
                    link.setOnClick("return fillItem" + objectId + "()");

                    container.add(link);

                    return container;
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "Expected value to be either a '{}' or a '{]', but was '{}'.",
                    Folder.class.getName(),
                    ContentItem.class.getName(),
                    value.getClass().getName()));
            }
        }

        private String generateJSLabel(final long id,
                                       final String widget,
                                       final String searchWidget,
                                       final String fill,
                                       final String title) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append(" <script language=javascript> ");
            buffer.append(" <!-- \n");
            buffer.append(" function fillItem").append(id).append("() { \n");
            buffer.append(" window.opener.document.").append(widget).append(
                ".value=\"").
                append(fill).append("\";\n");
            if (searchWidget != null) {
                buffer.append(" window.opener.document.").append(searchWidget)
                    .append(".value=\"").
                    append(title.
                        replace("\"", "\\\"")).append("\";\n");
            }
            // set protocol to 'other' in FCKEditor, else relative url prepended by http://
            if (BebopConfig.getConfig().getDefaultDhtmlEditor().equals(
                BebopConstants.BEBOP_FCKEDITOR)) {
                buffer.append(
                    " if(window.opener.document.getElementById('cmbLinkProtocol')) {\n");
                buffer.append(
                    "  window.opener.document.getElementById('cmbLinkProtocol').value=\"\";\n");
                buffer.append(" }\n");
            }

            buffer.append(" self.close(); \n"
                              + " return false; \n"
                              + " } \n"
                              + " --> \n"
                              + " </script> ");

            return buffer.toString();
        }

    }

    /**
     * Table model around ItemCollection
     */
    private static class FolderTableModel implements TableModel {

        private static final int NAME = 0;
        private static final int TITLE = 1;
        private static final int TYPE = 2;
        private final List<ContentItem> items;
        private int index = -1;

        public FolderTableModel(final List<ContentItem> items) {
            this.items = items;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean nextRow() {
            index++;
            if (index < items.size()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case NAME:
                    return items.get(index);
                case TITLE:
                    return items.get(index).getDisplayName();
                case TYPE:
                    return items.get(index).getContentType().getDisplayName();
                default:
                    throw new IllegalArgumentException(String.format(
                        "Column index {} not in table model.", columnIndex));
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return items.get(index).getObjectId();
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
            final String key = (String) event.getRowKey();
            if (key.startsWith("-")) {
                clearSelection(state);
                getFolderSelectionModel().setSelectedKey(
                    state, Long.parseLong(key.substring(1)));
                paginator.reset(state);
            }
        }

    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     *
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);
    }

}
