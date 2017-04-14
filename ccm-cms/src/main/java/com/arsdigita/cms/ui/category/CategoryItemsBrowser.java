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
package com.arsdigita.cms.ui.category;


import com.arsdigita.bebop.*;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.CMS;

import com.arsdigita.cms.ui.CMSContainer;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.dispatcher.ItemResolver;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Displays a list of items for the given category
 *
 * WARNING: The code to actually list the items is currently a travesty.
 * It needs to be re-written from scratch, by using custom data queries.
 * @version $Id: CategoryItemsBrowser.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class CategoryItemsBrowser extends Grid {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryItemsBrowser.class);

    private RequestLocal m_resolver;

    private String m_context;

    /**
     * Construct a new CategoryItemsBrowser
     * <p>
     * The {@link SingleSelectionModel} which will provide the
     * current category
     *
     * @param sel the {@link ACSObjectSelectionModel} which will maintain
     *   the current category
     *
     * @param numCols the number of columns in the browser
     *
     * @param context the context for the retrieved items. Should be
     *   "draft" or "live"
     */
    public CategoryItemsBrowser(ACSObjectSelectionModel sel, int numCols,
                                String context) {
        super(null, numCols);
        super.setModelBuilder(new CategoryItemModelBuilder(sel));
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentSectionManager sectionManager = cdiUtil.findBean(ContentSectionManager.class);
        m_context = context;

        setRowSelectionModel(sel);
        setEmptyView(new Label(GlobalizationUtil.globalize
                               ("cms.ui.category.item.none")));

        // Cache the item resolver
        m_resolver = new RequestLocal() {
                public Object initialValue(PageState s) {
                    ContentSection section =
                        CMS.getContext().getContentSection();
                    final ItemResolver itemResolver = sectionManager.getItemResolver(section);
                    LOGGER.warn("Item resolver is" + itemResolver.getClass());
                    return itemResolver;
                }
            };

        setDefaultCellRenderer(new ItemSummaryCellRenderer());
    }

    /**
     * @return the current context
     */
    public String getContext() {
        return m_context;
    }

    public void setContext(String context) {
        Assert.isUnlocked(this);
        m_context = context;
    }

    /**
     * Iterates through all the children of the given Category
     */
    private class CategoryItemModelBuilder extends LockableImpl
            implements ListModelBuilder {

        private ACSObjectSelectionModel m_sel;

        public CategoryItemModelBuilder(ACSObjectSelectionModel sel) {
            m_sel = sel;
        }

//        public DataQuery getDataQuery(PageState s) {
//            Category cat = (Category)m_sel.getSelectedObject(s);
//
//            ContentSection section = CMS.getContext().getContentSection();
//            User user = (User)Kernel.getContext().getParty();
//            OID oid = null;
//            if (user != null) {
//                oid = user.getOID();
//            }
//            // If the category is the root, list all items
//            if(cat == null || (cat.equals(section.getRootCategory()))) {
//                return ContentPage.getPagesInSectionQuery
//                    (section, getContext(), oid);
//            } else {
//                return ContentPage.getPagesInSectionQuery
//                    (section, getContext(), cat, oid);
//            }
//        }

        @Override
        public ListModel makeModel(List l, PageState state) {
            Category category = (Category) m_sel.getSelectedObject(state);
            java.util.List<ContentItem> objects = category
                    .getObjects()
                    .stream().map(Categorization::getCategorizedObject)
                    .filter(x -> x instanceof ContentItem)
                    .map(x -> (ContentItem) x)
                    .collect(Collectors.toList());
            return new ContentItemListModel(objects);
        }

        private class ContentItemListModel implements ListModel {

            private final Iterator<ContentItem> iterator;

            private ContentItem current;

            public ContentItemListModel(java.util.List<ContentItem> list) {
                this.iterator = list.iterator();
            }

            @Override
            public boolean next() {
                if (iterator.hasNext()) {
                    current = iterator.next();
                    return true;
                }
                return false;
            }

            @Override
            public Object getElement() {
                return current;
            }

            @Override
            public String getKey() {
                return current.getItemUuid();
            }
        }
    }

    /**
     * Renders a ContentItem in preview mode
     */
    private class ItemSummaryCellRenderer
        implements TableCellRenderer {

        @Override
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

//            if(value == null)
                return new Label(GlobalizationUtil.globalize("&nbsp;"), false);

//            DomainObject d = DomainObjectFactory.newInstance((DataObject)value);
//
//            Assert.isTrue(d instanceof ContentPage);
//            ContentPage p = (ContentPage)d;
//
//            CMSContainer box = new CMSContainer();
//            Component c;
//
//            ContentSection section =
//                CMS.getContext().getContentSection();
//
//            ItemResolver resolver = (ItemResolver)m_resolver.get(state);
//
//            final String url = resolver.generateItemURL
//                                     (state, p.getID(), p.getName(), section,
//                                      resolver.getCurrentContext(state));
//            c = new Link(p.getTitle(), url);
//
//            c.setClassAttr("title");
//            box.add(c);
//
//            String summary = p.getSearchSummary();
//            if(summary != null && summary.length() > 0) {
//                c = new Label(summary);
//                c.setClassAttr("summary");
//                box.add(c);
//            }
//
//            ContentType t = p.getContentType();
//            if(t != null) {
//                c = new Label(t.getName());
//            } else {
//                c = new Label(GlobalizationUtil.globalize("cms.ui.category.item"));
//            }
//            c.setClassAttr("type");
//            box.add(c);
//
//            box.setClassAttr("itemSummary");

//            return box;
        }
    }
}
