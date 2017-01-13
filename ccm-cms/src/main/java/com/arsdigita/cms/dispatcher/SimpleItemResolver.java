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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentItemVersion;

import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * This is the default implementation of
 * {@link com.arsdigita.cms.dispatcher.ItemResolver}.
 *
 * The {@link #getItem(java.lang.String, org.librecms.contentsection.Folder) }
 * method of the default implementation of  {@link ItemResolver}, 
 * {@link com.arsdigita.cms.dispatcher.SimpleItemResolver} runs a simple query
 * using the passed in information to retrieve the content item with a name that
 * matches the URL stub, in our example it looks for a content item with name
 * {@code cheese}. If no such item exists, or if there is such an item, but
 * without a live version, even though one has been requested, {@code getItem}
 * returns {@code null}.
 *
 * After the CMS Dispatcher received the content item from the
 * {@link ItemResolver}, it also asks it for the
 * {@link com.arsdigita.cms.dispatcher.MasterPage} for that item in the current
 * request. With the content item and the master page in hand, the dispatcher
 * calls {@code service} on the page.
 * 
 * <strong>
 * AS of version 7.0.0 this class not longer part of the public API. It is left
 * here to keep the changes to the UI classes as minimal as possible. For new
 * code other methods, for example from the {@link ContentItemManager} or 
 * the {@link ContentItemRepository} should be used. Because this class is no 
 * longer part of the public API the will might be removed or changed in future
 * releases without prior warning.
 * </strong>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SimpleItemResolver
    extends AbstractItemResolver
    implements ItemResolver {

    private static final Logger LOGGER = LogManager.getLogger(
        SimpleItemResolver.class.getName());

    private static final String ADMIN_PREFIX = "admin";
    private static final String WORKSPACE_PREFIX
                                    = CmsConstants.CONTENT_CENTER_URL;

    private static MasterPage masterPage = null;

    public SimpleItemResolver() {
    }

    /**
     * Return a content item based on page state (and content section).
     *
     * @param section The current content section
     * @param url     The section-relative URL
     * @param context The LIVE/DRAFT context (*not* the template use context)
     *
     * @return The content item mapped to the content section and URL, or null
     *         if no such item exists
     */
    @Override
    public ContentItem getItem(final ContentSection section,
                               final String url,
                               final String context) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("trying to get " + context + " item for url " + url);
        }

        final String itemUrl = stripTemplateFromURL(url);

        // getItem fails if called from a JSP template, because the request URL
        // gets replaced with the filesystem path to the JSP (when
        // DispatcherHelper.forwardRequestByPath is called).  To fix this, we check
        // if the item had already been put into the request by the CMSDispatcher
        // (which it usually has) and return it.
        ContentItem reqItem = (ContentItem) DispatcherHelper.getRequest().
            getAttribute("com.arsdigita.cms.dispatcher.item");
        if (reqItem != null) {
            LOGGER.info("found item in the request, returning it");
            return reqItem;
        }

        final Folder rootFolder = section.getRootDocumentsFolder();
        if (rootFolder == null) {
            LOGGER.info("no root folder found, returning null");
            return null;
        }

        return getItem(itemUrl, rootFolder);
    }

    /**
     * @param state the current page state
     *
     * @return the context of the current URL, such as "live" or "admin"
     */
    @Override
    public String getCurrentContext(final PageState state) {

        String url = state.getRequest().getRequestURI();

        final ContentSection section = CMS.getContext().getContentSection();

        // If this page is associated with a content section, transform
        // the URL so that it is relative to the content section site node.
        if (section != null) {
            final String sectionURL = section.getPrimaryUrl();
            if (url.startsWith(sectionURL)) {
                url = url.substring(sectionURL.length());
            }
        }

        // remove any template-specific URL components
        // (will only work if they're first in the URL at this point: verify
        url = stripTemplateFromURL(url);

        // Determine if we are under the admin UI.
        if (url.startsWith(ADMIN_PREFIX) || url.startsWith(WORKSPACE_PREFIX)) {
            return ContentItemVersion.DRAFT.toString();
        } else {
            return ContentItemVersion.LIVE.toString();
        }
    }

    /**
     * Return the content item at the specified path, or null if no such item
     * exists. The path is interpreted as a series of folders; for example,
     * "/foo/bar/baz" will look for an item named "baz" in a folder named "bar"
     * in a folder named "foo" under the specified root folder.
     *
     * @param url        the URL to the item
     * @param rootFolder The root folder where the item search will start
     *
     * @return the item on success, null if no such item exists
     */
    public ContentItem getItem(final String url, final Folder rootFolder) {

        final String[] tokens = url.split("/");

        Folder currentFolder = rootFolder;
        int i;
        for (i = 0; i < tokens.length; i++) {
            final String token = tokens[i];
            final Optional<Folder> folder = currentFolder.getSubFolders()
                .stream()
                .filter(subFolder -> subFolder.getDisplayName().equals(token))
                .findFirst();

            if (folder.isPresent()) {
                currentFolder = folder.get();
            } else {
                break;
            }
        }

        if (i >= tokens.length - 1) {
            // failure
            LOGGER.debug("no more tokens found, returning null");
            return null;
        } else {
            //Get the content item which is the last token
            final String name = tokens[tokens.length - 1];
            final Optional<CcmObject> item = currentFolder.getObjects()
                .stream()
                .map(categorization -> categorization.getCategorizedObject())
                .filter(object -> object.getDisplayName().equals(name))
                .findFirst();
            if (item.isPresent() && item.get() instanceof ContentItem) {
                return (ContentItem) item.get();
            } else {
                return null;
            }
        }
    }

    /**
     * Generate the URL for an item in the DRAFT context
     *
     * @param itemId
     * @param section
     *
     * @return
     */
    private String generateDraftURL(final Long itemId,
                                    final ContentSection section) {
        return ContentItemPage.getItemURL(
            String.format("%s%s/",
                          URL.getDispatcherPath(),
                          section.getPrimaryUrl()),
            itemId,
            ContentItemPage.AUTHORING_TAB);
    }

    /**
     * Generate the URL for an item in the LIVE context with a given template
     * context
     *
     * @param item
     * @param section
     * @param templateContext
     *
     * @return
     *
     */
    private String generateLiveURL(final ContentItem item,
                                   final ContentSection section,
                                   final String templateContext) {

        final String templateUrlFrag;
        if (templateContext == null || templateContext.isEmpty()) {
            templateUrlFrag = "";
        } else {
            templateUrlFrag = String.format(TEMPLATE_CONTEXT_PREFIX + "%s/",
                                            templateContext);
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemManager itemManager = cdiUtil.findBean(
            ContentItemManager.class);

        return String.format("%s/%s%s",
                             section.getPrimaryUrl(),
                             templateUrlFrag,
                             itemManager.getItemPath(item));
    }

    /**
     * Generate the preview URL for an item in the DRAFT context.
     *
     * @param item
     * @param section
     * @param templateContext
     *
     * @return
     */
    private String generatePreviewURL(final ContentItem item,
                                      final ContentSection section,
                                      final String templateContext) {
        final String templateUrlFrag;
        if (templateContext == null || templateContext.isEmpty()) {
            templateUrlFrag = "";
        } else {
            templateUrlFrag = String.format(TEMPLATE_CONTEXT_PREFIX + "%s/",
                                            templateContext);
        }
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemManager itemManager = cdiUtil.findBean(
            ContentItemManager.class);

        final StringBuilder url = new StringBuilder();
        url
            .append(section.getPrimaryUrl())
            .append("/")
            .append(CMSDispatcher.PREVIEW)
            .append("/")
            .append(templateUrlFrag)
            .append(itemManager.getItemPath(item));

        return url.toString();
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId  The item ID
     * @param name    The name of the content page
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "LIVE" or "DRAFT"
     *
     * @return The URL of the item
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final Long itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context) {
        return generateItemURL(state, itemId, name, section, context, null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId          The item ID
     * @param name            The name of the content page
     * @param state           The page state
     * @param section         the content section to which the item belongs
     * @param context         the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     *
     * @return The URL of the item
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final Long itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil.findBean(
            ContentItemRepository.class);

        if (ContentItemVersion.DRAFT.toString().equals(context)) {
            return generateDraftURL(itemId, section);
        } else if (ContentItemVersion.LIVE.toString().equals(context)) {
            final ContentItem item = itemRepo.findById(itemId);
            return generateLiveURL(item, section, templateContext);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            final ContentItem item = itemRepo.findById(itemId);
            return generatePreviewURL(item, section, templateContext);
        } else {
            throw new IllegalArgumentException(String.format(
                "Unknown context \"%s\".", context));
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item    The item
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "LIVE" or "DRAFT"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  final ContentSection section,
                                  final String context) {
        return generateItemURL(state, item, section, context, null);
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item            The item
     * @param state           The page state
     * @param section         the content section to which the item belongs
     * @param context         the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {

        if (ContentItemVersion.LIVE.toString().equals(context)) {
            return generateLiveURL(item, section, templateContext);
        } else if (ContentItemVersion.DRAFT.toString().equals(context)) {
            return generateDraftURL(item.getObjectId(), section);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            return generatePreviewURL(item, section, templateContext);
        } else {
            throw new IllegalArgumentException(String.format(
                "Unknown context \"%s\".", context));
        }
    }

    /**
     * Return a master page based on page state (and content section).
     *
     * @param item    The content item
     * @param request The HTTP request
     *
     * @throws javax.servlet.ServletException
     */
    @Override
    public CMSPage getMasterPage(final ContentItem item,
                                 final HttpServletRequest request)
        throws ServletException {

        if (masterPage == null) {
            masterPage = new MasterPage();
            masterPage.init();
        }

        return masterPage;
    }

}
