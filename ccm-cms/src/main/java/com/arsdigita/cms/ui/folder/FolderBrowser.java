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
package com.arsdigita.cms.ui.folder;

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
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableHeader;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.dispatcher.ItemResolver;

import java.util.List;
import java.util.Locale;

/**
 * Browse folders and items. If the user clicks on a folder, the folder
 * selection model is updated. If the user clicks on any other item, an separate
 * item selection model is updated.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author <a href="mailto:quasi@quasiweb.de">Sören Bernstein</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderBrowser extends Table {

    private static final GlobalizedMessage[] HEADERS = {
        globalize("cms.ui.folder.name"),
        globalize("cms.ui.folder.languages"),
        globalize("cms.ui.folder.title"),
        //        globalize("cms.ui.folder.additionalInfo"),
        globalize("cms.ui.folder.type"),
        globalize("cms.ui.folder.creation_date"),
        globalize("cms.ui.folder.last_modified"),
        globalize("cms.ui.folder.action")};
    protected static final String SORT_ACTION_UP = "sortActionUp";
    protected static final String SORT_ACTION_DOWN = "sortActionDown";
    protected final static String SORT_KEY_NAME = "name";
    protected final static String SORT_KEY_TITLE = "title";
    protected final static String SORT_KEY_TYPE = "type";
    protected final static String SORT_KEY_LAST_MODIFIED_DATE = "lastModified";
    protected final static String SORT_KEY_CREATION_DATE = "creationDate";

    private final FolderSelectionModel folderSelectionModel;
    private final TableActionListener folderChanger;
    private final TableActionListener folderDeleter;
//    private TableActionListener m_indexChanger;
    private final TableColumn nameColumn;
    private final TableColumn deleteColumn;
//    private TableColumn m_indexColumn;
    private final StringParameter sortTypeParameter = new StringParameter(
        "sortType");
    private final StringParameter sortDirectionParameter = new StringParameter(
        "sortDirn");

    private StringParameter atozFilterParameter = null;
    private StringParameter filterParameter = null;
    private FolderManipulator folderManipulator;
    private Paginator paginator;
//    private FolderManipulator.FilterForm filterForm;
    private long folderSize;

    public FolderBrowser(final FolderSelectionModel folderSelectionModel) {
        super();
        sortTypeParameter.setDefaultValue(SORT_KEY_NAME);
        sortDirectionParameter.setDefaultValue(SORT_ACTION_UP);

        setModelBuilder(new FolderBrowserTableModelBuilder());
        setColumnModel(new DefaultTableColumnModel(HEADERS));
        setHeader(new TableHeader(getColumnModel()));

        this.folderSelectionModel = folderSelectionModel;

        setClassAttr("dataTable");

        getHeader().setDefaultRenderer(
            new com.arsdigita.cms.ui.util.DefaultTableCellRenderer());

        nameColumn = getColumn(0);
        nameColumn.setCellRenderer(new NameCellRenderer());
//        nameColumn.setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_NAME));
        getColumn(1).setCellRenderer(new LanguagesCellRenderer());
//        getColumn(2).setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_TITLE));
//        getColumn(5).setHeaderRenderer(new HeaderCellRenderer(
//            SORT_KEY_CREATION_DATE));
//        getColumn(6).setHeaderRenderer(new HeaderCellRenderer(
//            SORT_KEY_LAST_MODIFIED_DATE));
        deleteColumn = getColumn(6);
        deleteColumn.setCellRenderer(new ActionCellRenderer());
        deleteColumn.setAlign("center");
        folderChanger = new FolderChanger();
        addTableActionListener(folderChanger);

        folderDeleter = new ItemDeleter();
        addTableActionListener(folderDeleter);

        setEmptyView(new Label(globalize("cms.ui.folder.no_items")));

        Assert.exists(folderSelectionModel.getStateParameter());
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addComponentStateParam(this,
                                    folderSelectionModel.getStateParameter());
        page.addComponentStateParam(this,
                                    sortTypeParameter);
        page.addComponentStateParam(this,
                                    sortDirectionParameter);
        page.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (state.isVisibleOnPage(FolderBrowser.this)) {
                    showHideFolderActions(state);
                }
            }

        });
    }

    private void showHideFolderActions(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        final Category folder = (Category) folderSelectionModel
            .getSelectedObject(state);

        final boolean canDelete = permissionChecker.isPermitted(
            ItemPrivileges.DELETE, folder);
        deleteColumn.setVisible(state, canDelete);
    }

    @Override
    public void respond(final PageState state) throws ServletException {
        final String key = state.getControlEventName();
        final String value = state.getControlEventValue();
        if (SORT_ACTION_UP.equals(key)) {
            state.setValue(sortTypeParameter, value);
            state.setValue(sortDirectionParameter, SORT_ACTION_UP);
        } else if (SORT_ACTION_DOWN.equals(key)) {
            state.setValue(sortTypeParameter, value);
            state.setValue(sortDirectionParameter, SORT_ACTION_DOWN);
        } else {
            super.respond(state);
        }
    }

    public FolderSelectionModel getFolderSelectionModel() {
        return folderSelectionModel;
    }

    protected void setFolderManipulator(
        final FolderManipulator folderManipulator) {
        this.folderManipulator = folderManipulator;
    }

//    protected void setFilterForm(final FolderManipulator.FilterForm filterForm) {
//        this.filterForm = filterForm;
//    }
    protected void setAtoZfilterParameter(
        final StringParameter atozFilterParameter) {
        this.atozFilterParameter = atozFilterParameter;
    }

    protected void setFilterParameter(final StringParameter filterParameter) {
        this.filterParameter = filterParameter;
    }

    protected long getFolderSize() {
        return folderSize;
    }

    protected Paginator getPaginator() {
        return paginator;
    }

    protected void setPaginator(final Paginator paginator) {
        this.paginator = paginator;
    }

    protected String getFilter(final PageState state) {
        return (String) state.getValue(filterParameter);
    }

    protected String getAtoZfilter(final PageState state) {
        return (String) state.getValue(atozFilterParameter);
    }

    private class HeaderCellRenderer
        extends com.arsdigita.cms.ui.util.DefaultTableCellRenderer {

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
            final String currentSortDirection = (String) state.getValue(
                sortDirectionParameter);
            final String imageURLStub;

            if (SORT_ACTION_UP.equals(currentSortDirection)) {
                imageURLStub = "gray-triangle-up.gif";
            } else {
                imageURLStub = "gray-triangle-down.gif";
            }

            final ControlLink link = new ControlLink(new Label(headerName)) {

                @Override
                public void setControlEvent(PageState ps) {
                    String sortDirectionAction;
                    // by default, everything sorts "up" unless it
                    // is the current key and it is already pointing up
                    if (SORT_ACTION_UP.equals(currentSortDirection)
                            && isCurrentKey) {
                        sortDirectionAction = SORT_ACTION_DOWN;
                    } else {
                        sortDirectionAction = SORT_ACTION_UP;
                    }
                    ps.setControlEvent(table,
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
                Image image = new Image("/assets/" + imageURLStub);
                image.setBorder("0");
                container.add(image);
            }
            link.setChild(container);
            return link;
        }

    }
//

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
            final ContentSection section = CMS.getContext().getContentSection();
            final ContentSectionManager sectionManager = CdiUtil.createCdiUtil()
                .findBean(ContentSectionManager.class);
            final ItemResolver itemResolver = sectionManager.getItemResolver(
                section);

            final boolean isFolder = ((FolderBrowserTableModel) table
                                      .getTableModel(state)).isFolder();

            if (isFolder) {
                //return new ControlLink(new Text(name));
                return super.getComponent(table,
                                          state,
                                          value,
                                          isSelected,
                                          key,
                                          row,
                                          column);
            } else {
                return new Link(new Text(name),
                                itemResolver.generateItemURL(state,
                                                             (long) key,
                                                             name,
                                                             section,
                                                             "DRAFT"));
            }
        }

    }

    /**
     * Added by: Sören Bernstein <quasi@quasiweb.de>
     *
     * Produce links to view an item in a specific language and show all
     * existing language version and the live status in the folder browser.
     */
    private class LanguagesCellRenderer extends DefaultTableCellRenderer {

        public LanguagesCellRenderer() {
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

//            final ContentItem item = (ContentItem) value;
//            final String name = item.getDisplayName();
            final SimpleContainer container = new SimpleContainer();
            final ContentSection section = CMS.getContext().getContentSection();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//            final ContentItemManager itemManager = cdiUtil.findBean(
//                ContentItemManager.class);
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final ItemResolver itemResolver = sectionManager.getItemResolver(
                section);

//            item.getName().getAvailableLocales().stream()
//                .map((locale) -> locale.toString())
//                .map((lang) -> {
//                    final StringBuilder fontWeight = new StringBuilder(2);
//                    final StringBuilder styleClasses = new StringBuilder(20);
//                    if (itemManager.isLive(item)) {
//                        fontWeight.append(Label.BOLD);
//                        styleClasses.append("live ");
//                    }
//                    final Label langLabel = new Label(lang);
//                    langLabel.setFontWeight(fontWeight.toString().trim());
//                    langLabel.setClassAttr(styleClasses.toString().trim());
//                    return langLabel;
//                })
//                .forEach((langLabel) -> {
//                    container.add(new Link(
//                        langLabel,
//                        itemResolver.generateItemURL(state,
//                                                     item.getObjectId(),
//                                                     name,
//                                                     section,
//                                                     item.getVersion().name())));
//                });
            @SuppressWarnings("unchecked")
            final List<Locale> availableLocales = (List<Locale>) value;
            availableLocales.forEach(locale -> container.add(new Link(
                new Text(locale.toString()),
                itemResolver.generateItemURL(
                    state,
                    (long) key,
                    locale.toString(),
                    section,
                    "DRAFT"))));

            return container;
        }

    }

    /**
     * Produce delete links for items and non-empty folders.
     */
    private static class ActionCellRenderer implements TableCellRenderer {

        private static final Logger LOGGER = LogManager.getLogger(
            ActionCellRenderer.class);

        private static final Label noActionLabel;
        private static final ControlLink link;

        static {
            LOGGER.debug("Static initializer is starting...");
            noActionLabel = new Label("&nbsp;", false);
            noActionLabel.lock();
            link = new ControlLink(
                new Label(globalize("cms.ui.folder.delete")));
            link.setConfirmation(
                globalize("cms.ui.folder.delete_confirmation"));
            LOGGER.debug("Static initializer finished.");
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (((Boolean) value)) {
                return link;
            } else {
                return noActionLabel;
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
            final long itemId = Long.parseLong(event.getRowKey().toString());

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderBrowserController controller = cdiUtil.findBean(
                FolderBrowserController.class);
            controller.deleteObject(itemId);

            ((Table) event.getSource()).clearSelection(state);
        }

    }

//    /**
//     * Table model around ItemCollection
//     */
//    private static class FolderTableModel implements TableModel {
//
//        private static final int NAME = 0;
//        private static final int LANGUAGES = 1;
//        private static final int TITLE = 2;
//        private static final int ADDITIONAL_INFO = 3;
//        private static final int TYPE = 4;
//        private static final int CREATION_DATE = 5;
//        private static final int LAST_MODIFIED = 6;
//        private static final int DELETABLE = 7;
//        private static final int IS_INDEX = 8;
//        private PageState m_state;
//        private FolderBrowser m_table;
//        private List<ContentItem> m_itemColl;
//        private Category m_fol;
//        private Long m_folIndexID;
//        private final ContentItemRepository itemRepo;
//        private final ContentItemManager itemManager;
//        private final CategoryManager categoryManager;
//        private int index = -1;
//
//        //old constructor before using paginator
//        //public FolderTableModel(Folder folder) {
//        //m_itemColl = folder.getItems();
//        //}
//        public FolderTableModel(FolderBrowser table,
//                                PageState state,
//                                List<ContentItem> itemColl) {
//            m_state = state;
//            m_table = table;
//            m_itemColl = itemColl;
//
//            m_fol = (Category) table.getFolderSelectionModel()
//                .getSelectedObject(state);
//
//            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//            itemRepo = cdiUtil.findBean(ContentItemRepository.class);
//            itemManager = cdiUtil.findBean(ContentItemManager.class);
//            categoryManager = cdiUtil.findBean(CategoryManager.class);
//
//            if (!hideIndexColumn()) {
//                final Optional<CcmObject> indexItem = categoryManager
//                    .getIndexObject(m_fol);
//                if (indexItem.isPresent()) {
//                    m_folIndexID = indexItem.get().getObjectId();
//                } else {
//                    m_folIndexID = null;
//                }
//            }
//        }
//
//        @Override
//        public int getColumnCount() {
//            return 7;
//        }
//
//        @Override
//        public boolean nextRow() {
//            index++;
//            return index < m_itemColl.size();
//        }
//
//        @Override
//        public Object getElementAt(int columnIndex) {
//            switch (columnIndex) {
//                case NAME:
//                    return m_itemColl.get(index);
//                case LANGUAGES:
//                    return m_itemColl.get(index);
//                case TITLE:
//                    return m_itemColl.get(index).getDisplayName();
//                case ADDITIONAL_INFO:
//                    return "";
//                case TYPE:
//                    return m_itemColl.get(index).getContentType().getLabel()
//                        .getValue();
//                case CREATION_DATE: {
//                    final CcmRevision firstRevision = itemRepo
//                        .retrieveFirstRevision(
//                            m_itemColl.get(index), m_itemColl.get(index)
//                            .getObjectId());
//                    if (firstRevision == null) {
//                        return "--";
//                    } else {
//                        return FormatStandards.formatDate(new Date(firstRevision
//                            .getTimestamp()));
//                    }
//                }
//                case LAST_MODIFIED: {
//                    final CcmRevision currentRevision = itemRepo
//                        .retrieveCurrentRevision(
//                            m_itemColl.get(index),
//                            m_itemColl.get(index).getObjectId());
//                    if (currentRevision == null) {
//                        return "--";
//                    } else {
//                        return FormatStandards.formatDate(new Date(
//                            currentRevision.getTimestamp()));
//                    }
//                }
//                case DELETABLE:
//                    return isDeletable();
//                case IS_INDEX: {
//                    if (hideIndexColumn()) {
//                        throw new IndexOutOfBoundsException(
//                            "Column index " + columnIndex
//                                + " not in table model.");
//                    }
//                    if (m_folIndexID == null) {
//                        return false;
//                    }
//                    return m_folIndexID.compareTo(
//                        m_itemColl.get(index).getObjectId()) == 0;
//                }
//                default:
//                    throw new IndexOutOfBoundsException("Column index "
//                                                            + columnIndex
//                                                            + " not in table model.");
//            }
//        }
//
//        public boolean isDeletable() {
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug("Checking to see if " + this + " is deletable");
//            }
//
////            if (m_itemColl.isFolder()) {
////
////                if (!m_itemColl.hasChildren()) {
////                    if (s_log.isDebugEnabled()) {
////                        s_log.debug(
////                            "The item is an empty folder; it may be deleted");
////                    }
////                    return true;
////
////                } else {
////
////                    if (s_log.isDebugEnabled()) {
////                        s_log.debug(
////                            "The folder is not empty; it cannot be deleted");
////                    }
////                    return false;
////
////                }
////            } else 
//            if (itemManager.isLive(m_itemColl.get(index))) {
//
//                if (LOGGER.isDebugEnabled()) {
//                    LOGGER.debug(
//                        "This item has a live instance; it cannot be deleted");
//                }
//                return false;
//            }
//
//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug(
//                    "The item is not a folder and doesn't have a live instance; it may be deleted");
//            }
//            return true;
//        }
//
//        public Object getKeyAt(int columnIndex) {
//            // Note: Folders were marked by negative IDs
//            return m_itemColl.get(index).getObjectId();
//        }
//
//    }
    private class FolderChanger extends TableActionAdapter {

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();
            final int col = event.getColumn();

            if (nameColumn != getColumn(col)) {
                return;
            }
            final String key = (String) event.getRowKey();

            clearSelection(state);
            getFolderSelectionModel().setSelectedKey(state, Long.parseLong(key));

        }

    }

//    private class IndexChanger extends TableActionAdapter {
//
//        private FolderSelectionModel m_fol;
//
//        public IndexChanger(FolderSelectionModel fol) {
//            super();
//            m_fol = fol;
//        }
//
//        @Override
//        public void cellSelected(TableActionEvent e) {
//            PageState state = e.getPageState();
//
//            BigDecimal rowkey = new BigDecimal((String) e.getRowKey());
//            if (rowkey == null) {
//                return;
//            }
//
//            try {
//                ContentBundle contentItem = new ContentBundle(rowkey);
//
//                Folder folder = (Folder) m_fol.getSelectedObject(state);
//
//                ContentBundle currentIndexItem = (ContentBundle) folder.
//                    getIndexItem();
//                if (currentIndexItem == null || (currentIndexItem.getID().
//                                                 compareTo(contentItem.getID())
//                                                 != 0)) {
//                    folder.setIndexItem(contentItem);
//                } else {
//                    folder.removeIndexItem();
//                }
//                folder.save();
//            } catch (DataObjectNotFoundException donfe) {
//                // Do nothing
//            }
//        }
//
//    }
    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key @pre ( key != null )
     */
    private static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);

    }

}
