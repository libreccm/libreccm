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

import java.util.Date;
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
//    private Paginator paginator;
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
        nameColumn.setHeaderRenderer(new HeaderCellRenderer(SORT_KEY_NAME));
        getColumn(1).setCellRenderer(new LanguagesCellRenderer());
        getColumn(4).setHeaderRenderer(new HeaderCellRenderer(
            SORT_KEY_CREATION_DATE));

        getColumn(4).setCellRenderer(new DateCellRenderer());
        getColumn(5).setCellRenderer(new DateCellRenderer());
        getColumn(5).setHeaderRenderer(new HeaderCellRenderer(
            SORT_KEY_LAST_MODIFIED_DATE));

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

//    protected Paginator getPaginator() {
//        return paginator;
//    }
//
//    protected void setPaginator(final Paginator paginator) {
//        this.paginator = paginator;
//    }

    protected String getFilter(final PageState state) {
        return (String) state.getValue(filterParameter);
    }

    protected String getAtoZfilter(final PageState state) {
        return (String) state.getValue(atozFilterParameter);
    }

    protected String getSortType(final PageState state) {
        return (String) state.getValue(sortTypeParameter);
    }

    protected String getSortDirection(final PageState state) {
        return (String) state.getValue(sortDirectionParameter);
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
                return new Link(new Text(name),
                                itemResolver.generateItemURL(state,
                                                             objectId,
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

            final SimpleContainer container = new SimpleContainer();
            final ContentSection section = CMS.getContext().getContentSection();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final ItemResolver itemResolver = sectionManager.getItemResolver(
                section);

            @SuppressWarnings("unchecked")
            final List<Locale> availableLocales = (List<Locale>) value;
            availableLocales.forEach(locale -> container.add(new Link(
                new Text(locale.toString()),
                itemResolver.generateItemURL(
                    state,
                    getObjectId(key),
                    locale.toString(),
                    section,
                    "DRAFT"))));

            return container;
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

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderBrowserController controller = cdiUtil.findBean(
                FolderBrowserController.class);
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
    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     */
    private static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);

    }

    private long getObjectId(final Object key) {

        final String keyStr = (String) key;

        if (keyStr.startsWith("folder-")) {
            return Long.parseLong(keyStr.substring("folder-".length()));
        } else if (keyStr.startsWith("item-")) {
            return Long.parseLong(keyStr.substring("item-".length()));
        } else {
            return Long.parseLong(keyStr);
        }

    }

}
