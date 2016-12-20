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

import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.RequestLocal;
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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.toolbox.GlobalisationUtil;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Browse folders and items. If the user clicks on a folder, the folder selection model is updated.
 * If the user clicks on any other item, an separate item selection model is updated.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class ItemSearchFolderBrowser extends Table {

    private static final org.apache.log4j.Logger s_log = org.apache.log4j.Logger.getLogger(
        ItemSearchFolderBrowser.class);
    private static GlobalizedMessage[] s_headers = {
        globalize("cms.ui.folder.name"),
        globalize("cms.ui.folder.title"),
        globalize("cms.ui.folder.type")};
    private FolderSelectionModel m_currentFolder;
    private TableActionListener m_folderChanger;
    private TableActionListener m_deleter;
    private TableActionListener m_indexChanger;
    private TableColumn m_nameColumn;
    private Paginator m_paginator;

    public ItemSearchFolderBrowser(FolderSelectionModel currentFolder) {
        super((FolderTableModelBuilder) null, s_headers);

        FolderTableModelBuilder builder = new FolderTableModelBuilder();
        setModelBuilder(builder);

        m_paginator = new Paginator(builder, ContentSection.getConfig().
                                    getFolderBrowseListSize());

        m_currentFolder = currentFolder;

        setClassAttr("dataTable");

        getHeader().setDefaultRenderer(
            new com.arsdigita.cms.ui.util.DefaultTableCellRenderer());
        m_nameColumn = getColumn(0);
        m_nameColumn.setCellRenderer(new NameCellRenderer());

        m_folderChanger = new FolderChanger();
        addTableActionListener(m_folderChanger);

        setEmptyView(new Label(globalize("cms.ui.folder.no_items")));

        Assert.exists(m_currentFolder.getStateParameter());
    }

    public Paginator getPaginator() {
        return m_paginator;
    }

    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_currentFolder.getStateParameter());

        p.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                // MP: This action listener should only be called when the
                //      folder browser is visible.
                showHideFolderActions(event.getPageState());
            }

        });
    }

    private Folder getCurrentFolder(PageState state) {
        return (Folder) m_currentFolder.getSelectedObject(state);
    }

    private void showHideFolderActions(PageState state) {
        SecurityManager sm = Utilities.getSecurityManager(state);
        Folder folder = getCurrentFolder(state);
        Assert.exists(folder);
    }

    public FolderSelectionModel getFolderSelectionModel() {
        return m_currentFolder;
    }

    private class FolderTableModelBuilder
        extends AbstractTableModelBuilder implements PaginationModelBuilder {

        private RequestLocal m_size = new RequestLocal() {

            @Override
            protected Object initialValue(PageState state) {
                Folder.ItemCollection itemColl = getItemCollection(state);

                if (null == itemColl) {
                    return new Integer(0);
                }
                return new Integer((int) itemColl.size());
            }

        };
        private RequestLocal m_itemColl = new RequestLocal() {

            @Override
            protected Object initialValue(PageState state) {
                Folder.ItemCollection itemColl = getItemCollection(state);

                itemColl.addOrder("item.name");
                itemColl.setRange(new Integer(m_paginator.getFirst(state)),
                                  new Integer(m_paginator.getLast(state) + 1));

                return itemColl;
            }

        };

        public TableModel makeModel(Table t, PageState s) {
            FolderSelectionModel sel = ((ItemSearchFolderBrowser) t).
                getFolderSelectionModel();
            Folder f = getCurrentFolder(s);

            if (s_log.isDebugEnabled()) {
                if (null == f) {
                    s_log.debug("Selected folder is null");
                } else {
                    s_log.debug("Selected folder: " + f.getLabel() + " " + f.
                        getOID().toString());
                }
            }

            if (f == null) {
                return Table.EMPTY_MODEL;
            } else {
                t.getRowSelectionModel().clearSelection(s);
                return new FolderTableModel((Folder.ItemCollection) m_itemColl.
                    get(s));
            }
        }

        private Folder.ItemCollection getItemCollection(PageState state) {
            Folder f = getCurrentFolder(state);
            Folder.ItemCollection itemColl = f.getPrimaryInstances();

            if (null == itemColl) {
                return null;
            }

            BigDecimal singleTypeID = (BigDecimal) state.getValue(new BigDecimalParameter(
                ItemSearch.SINGLE_TYPE_PARAM));

            if (singleTypeID != null) {

                // The Filter Factory
                FilterFactory ff = itemColl.getFilterFactory();

                // Create an or-filter
                CompoundFilter or = ff.or();

                // The content type must be either of the requested type
                or.addFilter(ff.equals(ContentItem.CONTENT_TYPE + "."
                                           + ContentType.ID, singleTypeID));

                // Or must be a sibling of the requested type
                /*
                 * jensp 2011-11-14: The orginal code here was only traversing
                 * one level, but ContentType hierarchies may have several 
                 * levels. Therefore, this code was replaced by method which is 
                 * called recursivly until the type with no descendents is 
                 * reached.
                 */
                createSiblingFilter(or, ff, singleTypeID);
                /*try {
                 ContentType ct = new ContentType(singleTypeID);
                
                 StringTokenizer strTok = new StringTokenizer(ct.
                 getDescendants(), "/");
                 while (strTok.hasMoreElements()) {
                 or.addFilter(ff.equals(ContentItem.CONTENT_TYPE + "."
                 + ContentType.ID,
                 (String) strTok.nextElement()));
                 }
                 } catch (Exception ex) {
                 // WTF? The selected content type does not exist in the table???
                 s_log.error(String.format(
                 "Something is very wrong here, the ContentType '%s' "
                 + "seems not to exist. Ignoring for now, but please "
                 + "make your checks.",
                 singleTypeID.toString()),
                 ex);
                 }*/

                itemColl.addFilter(or);

            }

            itemColl.addOrder("isFolder desc");
            itemColl.addOrder("lower(item." + ContentItem.NAME + ") ");
            return itemColl;
        }

        private void createSiblingFilter(final CompoundFilter filter,
                                         final FilterFactory filterFactory,
                                         final BigDecimal typeId) {
            final ContentType type = new ContentType(typeId);
            if ((type.getDescendants() == null)
                    || type.getDescendants().trim().isEmpty()) {
                return;
            } else {
                final String[] descendantsIds = type.getDescendants().split("/");

                for (String descendantId : descendantsIds) {
                    filter.addFilter(filterFactory.equals(String.format(
                        ContentItem.CONTENT_TYPE + "." + ContentType.ID),
                                                          descendantId));
                    createSiblingFilter(filter, filterFactory, descendantId);
                }
            }
        }

        private void createSiblingFilter(final CompoundFilter filter,
                                         final FilterFactory filterFactory,
                                         final String typeId) {
            try {
                final BigDecimal _typeId = new BigDecimal(typeId);
                createSiblingFilter(filter, filterFactory, _typeId);
            } catch (NumberFormatException ex) {
                s_log.error(String.format("Failed to parse typeId '%s'.",
                                          typeId),
                            ex);
            }
        }

        public int getTotalSize(Paginator paginator, PageState state) {

            Integer size = (Integer) m_size.get(state);
            return size.intValue();
        }

        /**
         * Indicates whether the paginator should be visible, based on the visibility of the folder
         * browser itself and how many items are displayed
         *
         * @return true if folder browser is visible and there is more than 1 page of items, false
         *         otherwise
         */
        public boolean isVisible(PageState state) {
            int size = ((Integer) m_size.get(state)).intValue();

            return ItemSearchFolderBrowser.this.isVisible(state)
                       && (size
                           > ContentSection.getConfig().getFolderBrowseListSize());
        }

    }

    /**
     * Produce links to view an item or control links for folders to change into the folder.
     */
    private class NameCellRenderer extends DefaultTableCellRenderer {

        public NameCellRenderer() {
            super(true);
        }

        @Override
        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {
            Folder.ItemCollection coll = (Folder.ItemCollection) value;
            String name = coll.getName();
            if (coll.isFolder()) {
                return super.getComponent(table, state, name, isSelected, key,
                                          row, column);
            } else {
                ContentSection section = CMS.getContext().getContentSection();
                BigDecimal id = (BigDecimal) key;

                if (section == null) {
                    return new Label(name);
                } else {
                    //ItemResolver resolver = section.getItemResolver();

                    //String url =
                    //resolver.generateItemURL
                    //(state, id, name, section, coll.getVersion()));
                    SimpleContainer container = new SimpleContainer();

                    String widget = (String) state.getValue(new StringParameter(
                        ItemSearchPopup.WIDGET_PARAM));
                    String searchWidget = (String) state.getValue(
                        new StringParameter("searchWidget"));
                    boolean useURL = "true".equals(state.getValue(new StringParameter(
                        ItemSearchPopup.URL_PARAM)));

                    String fillString;
                    if (useURL) {
                        fillString = ItemSearchPopup.getItemURL(state.getRequest(), 
                                                                coll.getDomainObject().getOID());
                    } else {
                        fillString = id.toString();// + " (" + name + ")";
                    }
                    
                    String title = ((ContentPage) coll.getDomainObject()).getTitle();

                    Label js = new Label(
                        generateJSLabel(id, widget, searchWidget, fillString, title),
                        false);
                    container.add(js);

                    String url = "#";

                    Link link = new Link(name, url);
                    link.setClassAttr("title");
                    link.setOnClick("return fillItem" + id + "()");

                    container.add(link);

                    return container;
                }
            }
        }

        private String generateJSLabel(BigDecimal id, String widget, String searchWidget,
                                       String fill, String title) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(" <script language=javascript> ");
            buffer.append(" <!-- \n");
            buffer.append(" function fillItem").append(id).append("() { \n");
            buffer.append(" window.opener.document.").append(widget).append(".value=\"").
                append(fill).append("\";\n");
            if (searchWidget != null) {
                buffer.append(" window.opener.document.").append(searchWidget).append(".value=\"").
                    append(title.
                        replace("\"", "\\\"")).append("\";\n");
            }
            // set protocol to 'other' in FCKEditor, else relative url prepended by http://
            if (Bebop.getConfig().getDHTMLEditor().equals(
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
        private Folder.ItemCollection m_itemColl;

        public FolderTableModel(Folder.ItemCollection itemColl) {
            m_itemColl = itemColl;
        }

        public int getColumnCount() {
            return 3;
        }

        public boolean nextRow() {
            return m_itemColl != null ? m_itemColl.next() : false;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case NAME:
                    return m_itemColl;
                case TITLE:
                    return m_itemColl.getDisplayName();
                case TYPE:
                    return m_itemColl.getTypeLabel();
                default:
                    throw new IndexOutOfBoundsException("Column index "
                                                            + columnIndex
                                                            + " not in table model.");
            }
        }

        public Object getKeyAt(int columnIndex) {
            // Mark folders by using their negative ID (dirty, dirty)
            return (m_itemColl.isFolder()) ? m_itemColl.getID().negate()
                       : m_itemColl.getID();
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
            if (key.startsWith("-")) {
                clearSelection(s);
                getFolderSelectionModel().setSelectedKey(s, key.substring(1));
                m_paginator.reset(s);
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
    private static GlobalizedMessage globalize(String key) {
        //return FolderManipulator.globalize(key);
        final GlobalisationUtil util = new GlobalisationUtil(
            "com.arsdigita.cms."
                + "ui.folder.CMSFolderResources");
        return util.globalize(key);
    }

}
