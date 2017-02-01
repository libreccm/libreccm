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
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableHeader;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.FormatStandards;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;
import org.libreccm.auditing.CcmRevision;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.dispatcher.ItemResolver;

import java.util.Date;

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

    private static final Logger s_log = Logger.getLogger(FolderBrowser.class);
    private static GlobalizedMessage[] s_headers = {
        globalize("cms.ui.folder.name"),
        globalize("cms.ui.folder.languages"),
        globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.additionalInfo"),
        globalize("cms.ui.folder.type"),
        globalize("cms.ui.folder.creation_date"),
        globalize("cms.ui.folder.last_modified"),
        globalize("cms.ui.folder.action"),
        globalize("cms.ui.folder.index")};
    private static GlobalizedMessage[] s_noIndexHeaders = {
        globalize("cms.ui.folder.name"),
        globalize("cms.ui.folder.languages"),
        globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.additionalInfo"),
        globalize("cms.ui.folder.type"),
        globalize("cms.ui.folder.creation_date"),
        globalize("cms.ui.folder.last_modified"),
        globalize("cms.ui.folder.action")};
    private static final String SORT_ACTION_UP = "sortActionUp";
    private static final String SORT_ACTION_DOWN = "sortActionDown";
    private FolderSelectionModel m_currentFolder;
    private TableActionListener m_folderChanger;
    private TableActionListener m_deleter;
    private TableActionListener m_indexChanger;
    private TableColumn m_nameColumn;
    private TableColumn m_deleteColumn;
    private TableColumn m_indexColumn;
    private final static String SORT_KEY_NAME = "name";
    private final static String SORT_KEY_TITLE = "title";
    private final static String SORT_KEY_LAST_MODIFIED_DATE = "lastModified";
    private final static String SORT_KEY_CREATION_DATE = "creationDate";
    private StringParameter m_sortType = new StringParameter("sortType");
    private StringParameter m_sortDirection = new StringParameter("sortDirn");
    private StringParameter m_aToZfilter = null;
    private StringParameter m_filter = null;
    private FolderManipulator.FilterForm m_filterForm;
    private long m_folderSize;

    public FolderBrowser(FolderSelectionModel currentFolder) {
        //super(new FolderTableModelBuilder(), s_headers);
        super();
        m_sortType.setDefaultValue(SORT_KEY_NAME);
        m_sortDirection.setDefaultValue(SORT_ACTION_UP);

        setModelBuilder(new FolderTableModelBuilder(currentFolder));
        setColumnModel(new DefaultTableColumnModel(hideIndexColumn()
                                                       ? s_noIndexHeaders
                                                       : s_headers));
        setHeader(new TableHeader(getColumnModel()));

        m_currentFolder = currentFolder;

        /*
         *
         * This code should be uncommented if the desired behaviour is for a
         * change of folder to cause reversion to default ordering of contained
         * items (by name ascending). Our feeling is that the user selected
         * ordering should be retained for the duration of the folder browsing
         * session. If anyone wants this alternative behaviour it should be
         * brought in under the control of a config parameter.
         *
         * m_currentFolder.addChangeListener(new ChangeListener() {
         *
         * public void stateChanged(ChangeEvent e) { PageState state =
         * e.getPageState(); state.setValue(m_sortType,
         * m_sortType.getDefaultValue()); state.setValue(m_sortDirection,
         * m_sortDirection.getDefaultValue());
         *
         * }});
         */
        setClassAttr("dataTable");

        getHeader().setDefaultRenderer(
            new com.arsdigita.cms.ui.util.DefaultTableCellRenderer());

        m_nameColumn = getColumn(0);
        m_nameColumn.setCellRenderer(new NameCellRenderer());
        m_nameColumn.setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_NAME));
        getColumn(1).setCellRenderer(new LanguagesCellRenderer());
        getColumn(2).setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_TITLE));
        getColumn(5).setHeaderRenderer(new HeaderCellRenderer(
            SORT_KEY_CREATION_DATE));
        getColumn(6).setHeaderRenderer(new HeaderCellRenderer(
            SORT_KEY_LAST_MODIFIED_DATE));
        m_deleteColumn = getColumn(7);
        m_deleteColumn.setCellRenderer(new ActionCellRenderer());
        m_deleteColumn.setAlign("center");
        m_folderChanger = new FolderChanger();
        addTableActionListener(m_folderChanger);

        m_deleter = new ItemDeleter();
        addTableActionListener(m_deleter);

        setEmptyView(new Label(globalize("cms.ui.folder.no_items")));

        Assert.exists(m_currentFolder.getStateParameter());
    }

    @Override
    public void register(Page p) {
        super.register(p);

        p.addComponentStateParam(this, m_currentFolder.getStateParameter());
        p.addComponentStateParam(this, m_sortType);
        p.addComponentStateParam(this, m_sortDirection);
        p.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final PageState state = e.getPageState();

                if (state.isVisibleOnPage(FolderBrowser.this)) {
                    showHideFolderActions(state);
                }
            }

        });
    }

    private void showHideFolderActions(PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        Category folder = (Category) m_currentFolder.getSelectedObject(state);
        Assert.exists(folder);

        final boolean canDelete = permissionChecker.isPermitted(
            ItemPrivileges.DELETE, folder);
        m_deleteColumn.setVisible(state, canDelete);
    }

    @Override
    public void respond(PageState state) throws ServletException {
        String key = state.getControlEventName();
        String value = state.getControlEventValue();
        if (SORT_ACTION_UP.equals(key)) {
            state.setValue(m_sortType, value);
            state.setValue(m_sortDirection, SORT_ACTION_UP);
        } else if (SORT_ACTION_DOWN.equals(key)) {
            state.setValue(m_sortType, value);
            state.setValue(m_sortDirection, SORT_ACTION_DOWN);
        } else {
            super.respond(state);
            //throw new ServletException("Unknown control event: " + key);
        }
    }

    public FolderSelectionModel getFolderSelectionModel() {
        return m_currentFolder;
    }

    protected void setFilterForm(FolderManipulator.FilterForm filterForm) {
        m_filterForm = filterForm;
    }

    protected void setAtoZfilterParameter(StringParameter aToZfilter) {
        m_aToZfilter = aToZfilter;
    }

    protected void setFilterParameter(StringParameter filter) {
        m_filter = filter;
    }

    protected long getFolderSize() {
        return m_folderSize;
    }

    private class FolderTableModelBuilder
        extends AbstractTableModelBuilder
        implements PaginationModelBuilder,
                   FolderManipulator.FilterFormModelBuilder {

        private final FolderSelectionModel folderModel;
        private final FolderBrowser folderBrowser;
        private final ContentItemRepository itemRepo;
        private final ConfigurationManager confManager;
        private final ContentSectionManager sectionManager;

        public FolderTableModelBuilder(final FolderSelectionModel folderModel) {
            this(folderModel, null);
        }

        public FolderTableModelBuilder(final FolderSelectionModel folderModel,
                                       final FolderBrowser folderBrowser) {
            this.folderModel = folderModel;
            this.folderBrowser = folderBrowser;
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            itemRepo = cdiUtil.findBean(ContentItemRepository.class);
            confManager = cdiUtil.findBean(ConfigurationManager.class);
            sectionManager = cdiUtil.findBean(ContentSectionManager.class);
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getTotalSize(final Paginator paginator,
                                final PageState state) {
            final Category folder = (Category) folderModel.getSelectedObject(
                state);


            /*
            SELECT c.categorizedObject FROM Categorization c "
            + "WHERE c.category = :folder "
            + "AND TYPE(c.categorizedObject) IN ContentItem"
            + "AND (LOWER(c.categorizationObject.displayName) LIKE CONCAT(LOWER(:name), '%') "
            44+ "OR LOWER(c.categorizedObject.name.value) LIKE CONCAT(:name), '%')
             */
            final CriteriaBuilder criteriaBuilder = itemRepo.
                getCriteriaBuilder();
            final CriteriaQuery<ContentItem> query = criteriaBuilder.
                createQuery(ContentItem.class);
            final Root<Categorization> root = query.from(Categorization.class);
            final Root<ContentItem> itemRoot = query.from(ContentItem.class);
            //final List<Predicate> predicates = new ArrayList<>();
            final Predicate categoryPredicate = criteriaBuilder.equal(
                root.get("Categorization.category"), folder);
            final Predicate typePredicate = criteriaBuilder.equal(
                itemRoot.type(), ContentItem.class);
            final List<Predicate> filters = new ArrayList<>();
            final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();
            if (state.getValue(m_aToZfilter) != null) {
                filters.add(criteriaBuilder.like(criteriaBuilder.lower(
                    root.get("Categorization.categorizedObject.displayName")),
                                                 String.format("%s%%",
                                                               ((String) state.
                                                                getValue(
                                                                m_aToZfilter)).
                                                                   toLowerCase(
                                                                       defaultLocale))));
                filters.add(criteriaBuilder.like(criteriaBuilder.lower(
                    root.get("Categorization.categoriziedObject.name.value")),
                                                 String.format("%s%%",
                                                               (String) state.
                                                                   getValue(
                                                                       m_aToZfilter))
                                                     .toLowerCase(defaultLocale)));
            }

            if (state.getValue(m_filter) != null) {
                filters.add(criteriaBuilder.like(criteriaBuilder.lower(
                    root.get("Categorization.categorizedObject.displayName")),
                                                 String.format("%s%%",
                                                               ((String) state.
                                                                    getValue(
                                                                    m_filter)))));
                filters.add(criteriaBuilder.like(criteriaBuilder.lower(
                    root.get("Categorization.categorizedObject.name.value")),
                                                 String.format("%s%%",
                                                               ((String) state.
                                                                    getValue(
                                                                    m_filter)))));
            }

            final Predicate filtersPredicate = criteriaBuilder.or(filters.
                toArray(new Predicate[filters.size()]));
            final Predicate predicates = criteriaBuilder.and(categoryPredicate,
                                                             typePredicate,
                                                             filtersPredicate);

            query.where(predicates).select(itemRoot);

            return itemRepo.executeCriteriaQuery(query.where(predicates).select(
                itemRoot)).size();
        }

        @Override
        public boolean isVisible(final PageState state) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public long getFolderSize(final PageState state) {
            return itemRepo.countItemsInFolder((Category) folderModel.
                getSelectedObject(state));
        }

    }

    private class HeaderCellRenderer
        extends com.arsdigita.cms.ui.util.DefaultTableCellRenderer {

        private String m_key;

        public HeaderCellRenderer(String key) {
            super(true);
            m_key = key;
        }

        @Override
        public Component getComponent(final Table table, final PageState state,
                                      Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            String headerName = (String) ((GlobalizedMessage) value).localize();
            String sortKey = (String) state.getValue(m_sortType);
            final boolean isCurrentKey = sortKey.equals(m_key);
            final String currentSortDirection = (String) state.getValue(
                m_sortDirection);
            String imageURLStub;

            if (SORT_ACTION_UP.equals(currentSortDirection)) {
                imageURLStub = "gray-triangle-up.gif";
            } else {
                imageURLStub = "gray-triangle-down.gif";
            }

            ControlLink cl = new ControlLink(headerName) {

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
                                       m_key);
                }

            };
            Label l = new Label();
            l.setLabel(headerName);
            l.setClassAttr("folderBrowserLink");
            l.setOutputEscaping(false);
            l.setFontWeight(Label.BOLD);

            SimpleContainer container = new SimpleContainer();
            container.add(l);
            if (isCurrentKey) {
                Image image = new Image("/assets/" + imageURLStub);
                image.setBorder("0");
                container.add(image);
            }
            cl.setChild(container);
            return cl;
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
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            final ContentItem item = (ContentItem) value;
            String name = item.getDisplayName();
//            final ContentSection section = item.getContentType().
//                    getContentSection();
            final ContentSection section = CMS.getContext().getContentSection();
            final ContentSectionManager sectionManager = CdiUtil.createCdiUtil()
                .findBean(ContentSectionManager.class);
            final ItemResolver itemResolver = sectionManager.getItemResolver(
                section);

            return new Link(name,
                            itemResolver.generateItemURL(
                                state,
                                item.getObjectId(),
                                name,
                                section,
                                item.getVersion().name()));

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
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            final ContentItem item = (ContentItem) value;
            String name = item.getDisplayName();

            final SimpleContainer container = new SimpleContainer();
            final ContentSection section = CMS.getContext().getContentSection();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemManager itemManager = cdiUtil.findBean(
                ContentItemManager.class);
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final ItemResolver itemResolver = sectionManager.getItemResolver(
                section);

            item.getName().getAvailableLocales().stream()
                .map((locale) -> locale.toString())
                .map((lang) -> {
                    final StringBuilder fontWeight = new StringBuilder(2);
                    final StringBuilder styleClasses = new StringBuilder(20);
                    if (itemManager.isLive(item)) {
                        fontWeight.append(Label.BOLD);
                        styleClasses.append("live ");
                    }
                    final Label langLabel = new Label(lang);
                    langLabel.setFontWeight(fontWeight.toString().trim());
                    langLabel.setClassAttr(styleClasses.toString().trim());
                    return langLabel;
                })
                .forEach((langLabel) -> {
                    container.add(new Link(
                        langLabel,
                        itemResolver.generateItemURL(state,
                                                     item.getObjectId(),
                                                     name,
                                                     section,
                                                     item.getVersion().name())));
                });

            return container;
        }

    }

    /**
     * Produce delete links for items and non-empty folders.
     */
    private static class ActionCellRenderer implements TableCellRenderer {

        private static final Label s_noAction;
        private static final ControlLink s_link;
        private static final Logger logger = Logger.getLogger(
            ActionCellRenderer.class);

        static {
            logger.debug("Static initializer is starting...");
            s_noAction = new Label("&nbsp;", false);
            s_noAction.lock();
            s_link = new ControlLink(
                new Label(globalize("cms.ui.folder.delete")));
            s_link.setConfirmation(
                globalize("cms.ui.folder.delete_confirmation"));
            logger.debug("Static initializer finished.");
        }

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {
            if (((Boolean) value)) {
                return s_link;
            } else {
                return s_noAction;
            }
        }

    }

    private final class IndexToggleRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {

            if (value == null) {
                return new Label(new GlobalizedMessage(
                    "cms.ui.folder.na",
                    CmsConstants.CMS_FOLDER_BUNDLE));
            }
            ControlLink link = new ControlLink("");

            if (((Boolean) value)) {
                link.setClassAttr("checkBoxChecked");
            } else {
                link.setClassAttr("checkBoxUnchecked");
            }

            return link;
        }

    }

// Deletes an item
    private class ItemDeleter extends TableActionAdapter {

        @Override
        public void cellSelected(TableActionEvent e) {
            int col = e.getColumn();

            if (m_deleteColumn != getColumn(col)) {
                return;
            }

            PageState s = e.getPageState();
            long itemId = Long.parseLong(e.getRowKey().toString());

            final ContentItemRepository itemRepo = CdiUtil.createCdiUtil().
                findBean(ContentItemRepository.class);
            final Optional<ContentItem> item = itemRepo.findById(itemId);
            if (item.isPresent()) {
                itemRepo.delete(item.get());
            }

            ((Table) e.getSource()).clearSelection(s);
        }

    }

    /**
     * Table model around ItemCollection
     */
    private static class FolderTableModel implements TableModel {

        private static final int NAME = 0;
        private static final int LANGUAGES = 1;
        private static final int TITLE = 2;
        private static final int ADDITIONAL_INFO = 3;
        private static final int TYPE = 4;
        private static final int CREATION_DATE = 5;
        private static final int LAST_MODIFIED = 6;
        private static final int DELETABLE = 7;
        private static final int IS_INDEX = 8;
        private PageState m_state;
        private FolderBrowser m_table;
        private List<ContentItem> m_itemColl;
        private Category m_fol;
        private Long m_folIndexID;
        private final ContentItemRepository itemRepo;
        private final ContentItemManager itemManager;
        private final CategoryManager categoryManager;
        private int index = -1;

        //old constructor before using paginator
        //public FolderTableModel(Folder folder) {
        //m_itemColl = folder.getItems();
        //}
        public FolderTableModel(FolderBrowser table,
                                PageState state,
                                List<ContentItem> itemColl) {
            m_state = state;
            m_table = table;
            m_itemColl = itemColl;

            m_fol = (Category) table.getFolderSelectionModel()
                .getSelectedObject(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            itemRepo = cdiUtil.findBean(ContentItemRepository.class);
            itemManager = cdiUtil.findBean(ContentItemManager.class);
            categoryManager = cdiUtil.findBean(CategoryManager.class);

            if (!hideIndexColumn()) {
                final Optional<CcmObject> indexItem = categoryManager
                    .getIndexObject(m_fol);
                if (indexItem.isPresent()) {
                    m_folIndexID = indexItem.get().getObjectId();
                } else {
                    m_folIndexID = null;
                }
            }
        }

        @Override
        public int getColumnCount() {
            return 7;
        }

        @Override
        public boolean nextRow() {
            index++;
            return index < m_itemColl.size();
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case NAME:
                    return m_itemColl.get(index);
                case LANGUAGES:
                    return m_itemColl.get(index);
                case TITLE:
                    return m_itemColl.get(index).getDisplayName();
                case ADDITIONAL_INFO:
                    return "";
                case TYPE:
                    return m_itemColl.get(index).getContentType().getLabel()
                        .getValue();
                case CREATION_DATE: {
                    final CcmRevision firstRevision = itemRepo
                        .retrieveFirstRevision(
                            m_itemColl.get(index), m_itemColl.get(index)
                            .getObjectId());
                    if (firstRevision == null) {
                        return "--";
                    } else {
                        return FormatStandards.formatDate(new Date(firstRevision
                            .getTimestamp()));
                    }
                }
                case LAST_MODIFIED: {
                    final CcmRevision currentRevision = itemRepo
                        .retrieveCurrentRevision(
                            m_itemColl.get(index),
                            m_itemColl.get(index).getObjectId());
                    if (currentRevision == null) {
                        return "--";
                    } else {
                        return FormatStandards.formatDate(new Date(
                            currentRevision.getTimestamp()));
                    }
                }
                case DELETABLE:
                    return isDeletable();
                case IS_INDEX: {
                    if (hideIndexColumn()) {
                        throw new IndexOutOfBoundsException(
                            "Column index " + columnIndex
                                + " not in table model.");
                    }
                    if (m_folIndexID == null) {
                        return false;
                    }
                    return m_folIndexID.compareTo(
                        m_itemColl.get(index).getObjectId()) == 0;
                }
                default:
                    throw new IndexOutOfBoundsException("Column index "
                                                            + columnIndex
                                                            + " not in table model.");
            }
        }

        public boolean isDeletable() {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Checking to see if " + this + " is deletable");
            }

//            if (m_itemColl.isFolder()) {
//
//                if (!m_itemColl.hasChildren()) {
//                    if (s_log.isDebugEnabled()) {
//                        s_log.debug(
//                            "The item is an empty folder; it may be deleted");
//                    }
//                    return true;
//
//                } else {
//
//                    if (s_log.isDebugEnabled()) {
//                        s_log.debug(
//                            "The folder is not empty; it cannot be deleted");
//                    }
//                    return false;
//
//                }
//            } else 
            if (itemManager.isLive(m_itemColl.get(index))) {

                if (s_log.isDebugEnabled()) {
                    s_log.debug(
                        "This item has a live instance; it cannot be deleted");
                }
                return false;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug(
                    "The item is not a folder and doesn't have a live instance; it may be deleted");
            }
            return true;
        }

        public Object getKeyAt(int columnIndex) {
            // Note: Folders were marked by negative IDs
            return m_itemColl.get(index).getObjectId();
        }

    }

    private class FolderChanger extends TableActionAdapter {

        @Override
        public void cellSelected(TableActionEvent e) {
            PageState s = e.getPageState();
            int col = e.getColumn().intValue();

            if (m_nameColumn != getColumn(col)) {
                return;
            }
            String key = (String) e.getRowKey();
            if (key.startsWith("-")) { // XXX dirty dirty
                clearSelection(s);
                getFolderSelectionModel().setSelectedKey(
                    s, Long.parseLong(key.substring(1)));
            }
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

    private static boolean hideIndexColumn() {
        return true;
    }

}
