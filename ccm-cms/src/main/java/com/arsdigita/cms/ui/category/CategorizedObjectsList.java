/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.CMS;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.xml.Element;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.*;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.dispatcher.ItemResolver;

import java.util.Collection;
import java.util.Iterator;
import javax.servlet.ServletException;

/**
 * A List of all objects currently categorized under this category
 *
 * @author Randy Graebner (randyg@redhat.com)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategorizedObjectsList extends SortableCategoryList {

    //public final static String CATEGORIZED_OBJECTS = "co";

    public CategorizedObjectsList(final CategoryRequestLocal category) {
        super(category);

        setModelBuilder(new CategorizedObjectsModelBuilder());
        Label label = new Label(GlobalizationUtil.globalize("cms.ui.category.item.none"));
        label.setFontWeight(Label.ITALIC);
        setEmptyView(label);
    }

    /**
     * This actually performs the sorting
     */
    public void respond(PageState ps) throws ServletException {
        /* TODO Reimplement sorting
        final String event = ps.getControlEventName();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository contentItemRepository = cdiUtil.findBean(ContentItemRepository.class);
        final ContentItemManager contentItemManager = cdiUtil.findBean(ContentItemManager.class);
        final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);


        if (NEXT_EVENT.equals(event) || PREV_EVENT.equals(event)) {
            final long selectedID = Long.parseLong(ps.getControlEventValue());
            final Category parent = getCategory(ps);

            final ContentItem selectedItem = contentItemRepository.findById(selectedID).get();
            final Long selectedDraftId = contentItemManager.getDraftVersion(selectedItem, ContentItem.class).getObjectId();

            if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES)) {
                final Long swapId = getSwapID(parent, selectedID, event);
                parent.swapSortKeys(selectedID, swapId);
                final ContentItem swapItem = new ContentItem(swapId);
                final BigDecimal swapDraftId = swapItem.getDraftVersion().getID();

                final BigDecimal sortKey1 = parent.getSortKey(selectedItem);
                final BigDecimal sortKey2 = parent.getSortKey(swapItem);

                parent.setSortKey(new ContentItem(selectedDraftId), sortKey1);
                parent.setSortKey(new ContentItem(swapDraftId), sortKey2);

            }
        } else {
            super.respond(ps);
        }*/
    }


    protected long getSwapID(Category category, long selectedID, String event) {
        long priorID = -1;
        long swapID = -1;
        boolean foundSelectedID = false;

        if (category != null && !category.getObjects().isEmpty()) {
            //items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE); TODO
            //items.sort(true);
            for (Categorization categorization : category.getObjects()) {
                long thisID = categorization.getCategorizationId();
                if (foundSelectedID && NEXT_EVENT.equals(event)) {
                    swapID = thisID;
                    break;
                }

                if (thisID == selectedID) {
                    foundSelectedID = true;
                    if (PREV_EVENT.equals(event)) {
                        swapID = priorID;
                        break;
                    }
                }

                priorID = thisID;
            }
        }
        return swapID;
    }

    @Override
    protected void generateLabelXML(PageState state, Element parent, Label label, String key, Object element) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
        final ContentSectionManager contentSectionManager = cdiUtil.findBean(ContentSectionManager.class);

        ContentItem item = (ContentItem) element;

        boolean canEdit = permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES);

        if (canEdit) {
            ContentSection section = CMS.getContext().getContentSection();
            ItemResolver resolver = contentSectionManager.getItemResolver(section);
            Link link = new Link(
                    new Text(item.getDisplayName()),
                    resolver.generateItemURL(
                            state,
                            item.getObjectId(),
                            item.getDisplayName(),
                            section,
                            item.getVersion().name()
                    )
            );
            Component c = link;
            c.generateXML(state, parent);
        }
    }

    private class CategorizedObjectsModelBuilder extends LockableImpl
            implements ListModelBuilder {

        public final ListModel makeModel(final List list,
                final PageState state) {
            final Category category = getCategory(state);

            if (category != null && !category.getObjects().isEmpty()) {
                Collection<Categorization> items = category.getObjects();
                //items.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
                //items.sort(true);
                return new CategorizedCollectionListModel(items);
            } else {
                return List.EMPTY_MODEL;
            }
        }
    }

    /**
     * A {@link ListModel} that iterates over categorized objects via an
     * iterator
     */
    private static class CategorizedCollectionListModel implements ListModel {

        private Iterator<Categorization> m_objs;
        private Categorization m_object;

        CategorizedCollectionListModel(Collection<Categorization> coll) {
            m_objs = coll.iterator();
            m_object = null;

        }

        @Override
        public boolean next() {
            if (m_objs.hasNext()) {
                m_object = m_objs.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElement() {
            return m_object;
        }

        @Override
        public String getKey() {
            return Long.toString(m_object.getCategorizationId());
        }
    }
}
