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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;

import org.librecms.contentsection.ContentItem;
import org.librecms.lifecycle.Lifecycle;

import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Michael Pih
 * @author Jack Chung
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@goolemail.com">Jens Pelzetter</a>
 */
public class ItemLifecycleAdminPane extends BaseItemPane {

    private static final Logger LOGGER = LogManager.getLogger(
        ItemLifecycleAdminPane.class);
    private final ContentItemRequestLocal selectedItem;
    private final LifecycleRequestLocal selectedLifecycle;
    private final LayoutPanel introPane;
    private final LayoutPanel detailPane;
    private final LayoutPanel selectPane;
    private final LayoutPanel lockedPane;
    private final LayoutPanel errorPane;
    private final LayoutPanel cantPublishPane;

    public ItemLifecycleAdminPane(final ContentItemRequestLocal selectedItem) {
        this.selectedItem = selectedItem;
        selectedLifecycle = new ItemLifecycleRequestLocal();

        introPane = new LayoutPanel();
        add(introPane);

        final Label message = new Label(gz("cms.ui.item.lifecycle.intro"));
        introPane.setBody(message);

        detailPane = new LayoutPanel();
        add(detailPane);

        final ItemLifecycleItemPane itemPane = new ItemLifecycleItemPane(
            selectedItem, selectedLifecycle);
        detailPane.setBody(itemPane);

        selectPane = new LayoutPanel();
        add(selectPane);

        final ItemLifecycleSelectForm selectForm = new ItemLifecycleSelectForm(
            selectedItem);
        selectPane.setBody(selectForm);

        lockedPane = new LayoutPanel();
        add(lockedPane);

        final Label lockedMsg = new Label(gz(
            "cms.ui.item.lifecycle.publish_locked"));
        lockedPane.setBody(lockedMsg);
        final ControlLink lockedUpdateLink = new ControlLink(new Label(gz(
            "cms.ui.item.lifecycle.publish_locked.update")));
        lockedUpdateLink.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                throw new RedirectSignal(
                    URL.getDispatcherPath()
                        + ContentItemPage.getItemURL(
                        selectedItem.getContentItem(event.getPageState()),
                        ContentItemPage.PUBLISHING_TAB),
                    true);
            }

        });
        lockedPane.setBottom(lockedUpdateLink);

        errorPane = new LayoutPanel();
        add(errorPane);

        final Label errorMsg = new Label(gz("cms.ui.lifecycle.publish.error"));
        errorPane.setBody(errorMsg);

        cantPublishPane = new LayoutPanel();
        add(cantPublishPane);

        final Label cantPublish = new Label(gz(
            "cms.ui.lifecycle.publish.not_possible_abstract_category"));
        cantPublishPane.setBody(cantPublish);

        connect(selectForm, detailPane);
    }

    private class ItemLifecycleRequestLocal extends LifecycleRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final ContentItem item = selectedItem.getContentItem(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentItemManager itemManager = cdiUtil
                .findBean(ContentItemManager.class);
            final Optional<ContentItem> liveItem = itemManager
                .getLiveVersion(item, ContentItem.class);
            if (liveItem.isPresent()) {

                final Lifecycle lifecycle = liveItem.get().getLifecycle();
                LOGGER.debug("Returning lifecycle {}",
                             Objects.toString(lifecycle));
                return lifecycle;
            } else {
                return null;
            }
        }

    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        page.addActionListener(new VisibilityListener());
    }

    private class VisibilityListener implements ActionListener {

        @Override
        public final void actionPerformed(final ActionEvent event) {
            LOGGER.debug("Determining which pane to show");

            final PageState state = event.getPageState();

//            if (CMSConfig.getConfig().isThreadPublishing()
//                    && PublishLock.getInstance().isLocked(m_item.getContentItem(
//                    state))) {
//                if (PublishLock.getInstance().hasError(m_item.getContentItem(
//                    state))) {
//                    push(state, m_errorPane);
//                } else {
//                    push(state, m_lockedPane);
//                    state.getResponse().addIntHeader("Refresh", 5);
//                }
//            } else 
            if (isAssignedToAbstractCategory(selectedItem.getContentItem(state))) {
                push(state, cantPublishPane);
            } else {
                if (state.isVisibleOnPage(ItemLifecycleAdminPane.this)) {
                    if (selectedLifecycle.getLifecycle(state) == null) {
                        if (hasPermission(state)) {
                            push(state, selectPane);
                        } else {
                            push(state, introPane);
                        }
                    } else {
                        push(state, detailPane);
                    }
                }
            }
        }

    }

    private boolean hasPermission(final PageState state) {
        final ContentItem item = selectedItem.getContentItem(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        return permissionChecker.isPermitted(ItemPrivileges.PUBLISH, item);
    }

    /**
     * Checks if the item is assigned to an abstract category.
     *
     * A category is abstract if not items can assigned to it.
     *
     * @param item
     *
     * @return {@code true} if assigned to a abstract category, {@code false} if
     *         not.
     */
    private boolean isAssignedToAbstractCategory(final ContentItem item) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ItemLifecycleAdminController controller = cdiUtil
            .findBean(ItemLifecycleAdminController.class);

        return controller.isAssignedToAbstractCategory(item);

//        final long count = item.getCategories().stream()
//            .filter(categorization -> {
//                return categorization.getCategory().isAbstractCategory();
//            })
//            .count();
//        
//        return count > 0;
    }

}
