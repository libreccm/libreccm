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
package org.librecms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.dispatcher.CMSDispatcher;
import com.arsdigita.cms.dispatcher.CMSPage;
import com.arsdigita.cms.dispatcher.MasterPage;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmObject;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

/**
 * Resolves items to URLs and URLs to items for multiple language variants.
 *
 * For version 7.0.0 this call has been moved from the
 * {@code com.arsdigita.cms.dispatcher} package to the
 * {@code org.librecms.dispatcher} package and refactored to an CDI bean. This
 * was necessary to avoid several problems when accessing the entity beans for
 * {@link Category} and {@link ContentItem}, primarily the infamous
 * {@code LazyInitializationException}. Also several checks for null parameters
 * were added to avoid {@code NullPointerExcpetions}.
 *
 * <strong>
 * AS of version 7.0.0 this class not longer part of the public API. It is left
 * here to keep the changes to the UI classes as minimal as possible. For new
 * code other methods, for example from the {@link ContentItemManager} or the
 * {@link ContentItemRepository} should be used. Because this class is no longer
 * part of the public API the will might be removed or changed in future
 * releases without prior warning.
 * </strong>
 *
 *
 *
 * @author <a href="mailto:mhanisch@redhat.com">Michael Hanisch</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class MultilingualItemResolver implements ItemResolver {

    private static final Logger LOGGER = LogManager.getLogger(
        MultilingualItemResolver.class);

    private static final String ADMIN_PREFIX = "admin";

    /**
     * The string identifying an item's ID in the query string of a URL.
     */
    protected static final String ITEM_ID = "item_id";

    /**
     * The separator used in URL query strings; should be either "&" or ";".
     */
    protected static final String SEPARATOR = "&";

    @Inject
    private FolderRepository folderRepo;

    @Inject
    private FolderManager folderManager;

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemManager itemManager;

    /**
     * Returns a content item based on section, url, and use context.
     *
     * @param section The current content section
     * @param itemUrl The section-relative URL
     * @param context The use context, e.g. <code>ContentItem.LIVE</code>,
     *                <code>CMSDispatcher.PREVIEW</code> or
     *                <code>ContentItem.DRAFT</code>. See {@link
     * #getCurrentContext}.
     *
     * @return The content item, or null if no such item exists
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public ContentItem getItem(final ContentSection section,
                               final String itemUrl,
                               final String context) {
        if (section == null) {
            throw new IllegalArgumentException(
                "Can't get item from section null.");
        }
        if (itemUrl == null) {
            throw new IllegalArgumentException("Can't get item for URL null.");
        }
        if (context == null) {
            throw new IllegalArgumentException(
                "Can't get item for context null.");
        }

        LOGGER.debug("Resolving the item in content section \"{}\" at URL "
                         + "\"{}\" for context \"{}\"...",
                     section.getLabel(),
                     itemUrl,
                     context);

        final Folder rootFolder = section.getRootDocumentsFolder();
        String url = stripTemplateFromURL(itemUrl);

        if (rootFolder == null) {
            // nothing to do, if root folder is null
            LOGGER.debug("The root folder is null; returning no item");
        } else {
            LOGGER.debug("Using root folder {}...", rootFolder.getName());

            if (ContentItemVersion.LIVE.toString().equals(context)) {
                LOGGER.debug("The use context is 'live'");

                LOGGER.debug("The root folder has a live version; recursing");

                final String prefix = String.join(
                    "",
                    section.getPrimaryUrl(),
                    folderManager.getFolderPath(rootFolder));

                if (url.startsWith(prefix)) {
                    LOGGER.
                        debug("The starts with prefix \"{}\"; removing it...",
                              prefix);

                    url = url.substring(prefix.length());
                }

                final ContentItem item = getItemFromLiveURL(url, rootFolder);

                LOGGER.debug("Resolved URL \"{}\" to item {}...",
                             url,
                             Objects.toString(item));

                return item;

            } else if (ContentItemVersion.DRAFT.toString().equals(context)) {
                LOGGER.debug("The use context is 'draft'");

                // For 'draft' items, 'generateUrl()' method returns
                // URL like this
                // '/acs/wcms/admin/item.jsp?item_id=10201&set_tab=1'
                // Check if URL contains any match of string
                // 'item_id', then try to instanciate item_id value
                // and return FIXME: Please hack this if there is
                // more graceful solution. [aavetyan]
                if (Assert.isEnabled()) {
                    Assert.isTrue(url.contains(ITEM_ID),
                                  String.format("url must contain parameter %s",
                                                ITEM_ID));
                }

                final ContentItem item = getItemFromDraftURL(url);

                LOGGER.debug("Resolved URL \"{}\" to item {}.",
                             url,
                             Objects.toString(item));

                return item;
            } else if (CMSDispatcher.PREVIEW.equals(context)) {
                LOGGER.debug("The use context is 'preview'");

                final String prefix = CMSDispatcher.PREVIEW + "/";

                if (url.startsWith(prefix)) {
                    LOGGER.debug(
                        "The URL starts with prefix \"{}\"; removing it",
                        prefix);

                    url = url.substring(prefix.length());
                }

                final ContentItem item = getItemFromLiveURL(url, rootFolder);

                LOGGER.debug("Resolved URL \"{}\" to item {}.",
                             url,
                             Objects.toString(item));

                return item;
            } else {
                throw new IllegalArgumentException(String.format(
                    "Invalid item resolver context \"%s\".", context));
            }
        }

        LOGGER.debug("No item resolved; returning null");

        return null;
    }

    /**
     * Fetches the current context based on the page state.
     *
     * @param state the current page state
     *
     * @return the context of the current URL, such as
     *         <code>ContentItem.LIVE</code> or <code>ContentItem.DRAFT</code>
     *
     * @see ContentItem#LIVE
     * @see ContentItem#DRAFT
     *
     * ToDo: Refactor to use the {@link ContentItemVersion} directly.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getCurrentContext(final PageState state) {
        LOGGER.debug("Getting the current context");

        // XXX need to use Web.getWebContext().getRequestURL() here.
        String url = state.getRequest().getRequestURI();

        final ContentSection section = CMS.getContext().getContentSection();

        // If this page is associated with a content section,
        // transform the URL so that it is relative to the content
        // section site node.
        if (section != null) {
            final String sectionURL = section.getPrimaryUrl();

            if (url.startsWith(sectionURL)) {
                url = url.substring(sectionURL.length());
            }
        }

        // Remove any template-specific URL components (will only work
        // if they're first in the URL at this point; verify). XXX but
        // we don't actually verify?
        url = stripTemplateFromURL(url);

        // Determine if we are under the admin UI.
        if (url.startsWith(ADMIN_PREFIX)
                || url.startsWith(CmsConstants.CONTENT_CENTER_URL)) {
            return ContentItemVersion.DRAFT.toString();
        } else {
            return ContentItemVersion.LIVE.toString();
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param itemId  The item ID
     * @param name    The name of the content page
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Transactional(Transactional.TxType.REQUIRED)
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
     *
     * @see #getCurrentContext
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String generateItemURL(final PageState state,
                                  final Long itemId,
                                  final String name,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {
        if (itemId == null) {
            throw new IllegalArgumentException(
                "Can't generate item URL for item id null.");
        }
        if (context == null) {
            throw new IllegalArgumentException(
                "Can't generate item URL for context null.");
        }
        if (section == null) {
            throw new IllegalArgumentException(
                "Can't generate item URL for section null.");
        }

        LOGGER.debug("Generating an item URL for item id {}, section \"{}\" "
                         + "and context \"{}\" with name \"{}\"...",
                     itemId,
                     section.getLabel(),
                     context,
                     name);

        if (ContentItemVersion.DRAFT.toString().equals(context)) {
            // No template context here.
            return generateDraftURL(section, itemId);
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            final ContentItem item = itemRepo.findById(itemId).get();
            return generatePreviewURL(section, item, templateContext);
        } else if (ContentItemVersion.LIVE.toString().equals(context)) {
            final ContentItem item = itemRepo.findById(itemId).get();

            return generateLiveURL(section, item, templateContext);
        } else {
            throw new IllegalArgumentException("Unknown context '" + context
                                                   + "'");
        }
    }

    /**
     * Generates a URL for a content item.
     *
     * @param item    The item
     * @param state   The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     *
     * @return The URL of the item
     *
     * @see #getCurrentContext
     */
    @Transactional(Transactional.TxType.REQUIRED)
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
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String generateItemURL(final PageState state,
                                  final ContentItem item,
                                  final ContentSection section,
                                  final String context,
                                  final String templateContext) {
        if (item == null) {
            throw new IllegalArgumentException(
                "Can't generate URL for item null.");
        }
        if (context == null) {
            throw new IllegalArgumentException(
                "Can't generate URL for context null.");
        }

        final ContentSection contentSection;
        if (section == null) {
            contentSection = item.getContentType().getContentSection();
        } else {
            contentSection = section;
        }

        LOGGER.debug("Generating an item URL for item \"{}\", section \"{}\" "
                         + "and context \"{}\".",
                     item.getDisplayName(),
                     contentSection.getLabel(),
                     context);

        if (ContentItemVersion.DRAFT.toString().equals(context)) {
            return generateDraftURL(section, item.getObjectId());
        } else if (CMSDispatcher.PREVIEW.equals(context)) {
            return generatePreviewURL(section, item, templateContext);
        } else if (ContentItemVersion.LIVE.toString().equals(context)) {
            return generateLiveURL(contentSection, item, templateContext);
        } else {
            throw new IllegalArgumentException(String.format(
                "Unknown context \"%s\".", context));
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public CMSPage getMasterPage(final ContentItem item,
                                 final HttpServletRequest request)
        throws ServletException {

        LOGGER.debug("Getting the master page for item {}",
                     item.getDisplayName());

        final MasterPage masterPage = new MasterPage();
        masterPage.init();

        return masterPage;
    }

    /**
     * Returns content item's draft version URL
     *
     * @param section The content section to which the item belongs
     * @param itemId  The content item's ID
     *
     * @return generated URL string
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected String generateDraftURL(final ContentSection section,
                                      final Long itemId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generating draft URL for item ID " + itemId
                             + " and section " + section);
        }

        if (Assert.isEnabled()) {
            Assert.isTrue(section != null && itemId != null,
                          "get draft url: neither secion nor item "
                              + "can be null");
        }

        final String url = ContentItemPage.getItemURL(
            String.format("%s/", section.getPrimaryUrl()),
            itemId,
            ContentItemPage.AUTHORING_TAB);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Generated draft URL " + url);
        }

        return url;
    }

    /**
     * Generate a <em>language-independent</em> URL to the <code>item</code> in
     * the given section.<p>
     * When a client retrieves this URL, the URL is resolved to point to a
     * specific language instance of the item referenced here, i.e. this URL
     * will be resolved to a <em>language-specific</em> URL internally.
     *
     * @param section         the <code>ContentSection</code> that contains this
     *                        item
     * @param item            <code>ContentItem</code> for which a URL should be
     *                        constructed.
     * @param templateContext template context; will be ignored if
     *                        <code>null</code>
     *
     * @return a <em>language-independent</em> URL to the <code>item</code> in
     *         the given <code>section</code>, which will be presented within
     *         the given <code>templateContext</code>
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected String generateLiveURL(final ContentSection section,
                                     final ContentItem item,
                                     final String templateContext) {
        LOGGER.debug("Generating live URL for item {} in section {}",
                     Objects.toString(item),
                     Objects.toString(section));

        /*
         * URL = URL of section + templateContext + path to the ContentBundle
         * which contains the item
         */
        final StringBuffer url = new StringBuffer(400);
        //url.append(section.getURL());
        url
            .append(section.getPrimaryUrl())
            .append("/");

        /*
         * add template context, if one is given
         */
        // This is breaking URL's...not sure why it's here. XXX
        // this should work with the appropriate logic. trying again.
        if (!(templateContext == null || templateContext.length() == 0)) {
            url
                .append(TEMPLATE_CONTEXT_PREFIX)
                .append(templateContext)
                .append("/");
        }

        url.append(itemManager.getItemPath(item));

        return url.toString();
    }

    /**
     * Generate a URL which can be used to preview the <code>item</code>, using
     * the given <code>templateContext</code>.<p>
     * Only a specific language instance can be previewed, meaning there
     * <em>no</em> language negotiation is involved when a request is made to a
     * URL that has been generated by this method.
     *
     * @param section         The <code>ContentSection</code> which contains the
     *                        <code>item</code>
     * @param item            The <code>ContentItem</code> for which a URL
     *                        should be generated.
     * @param templateContext the context that determines which template should
     *                        render the item when it is previewed; ignored if
     *                        the argument given here is <code>null</code>
     *
     * @return a URL which can be used to preview the given <code>item</code>
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected String generatePreviewURL(final ContentSection section,
                                        final ContentItem item,
                                        final String templateContext) {
        Assert.exists(section, "ContentSection section");
        Assert.exists(item, "ContentItem item");

        final StringBuffer url = new StringBuffer(100);
        url
            .append(section.getPrimaryUrl())
            .append("/")
            .append(CMSDispatcher.PREVIEW)
            .append("/");
        /*
         * add template context, if one is given
         */
        // This is breaking URL's...not sure why it's here. XXX
        // this should work with the appropriate logic. trying again.
        if (!(templateContext == null || templateContext.length() == 0)) {
            url
                .append(TEMPLATE_CONTEXT_PREFIX)
                .append(templateContext)
                .append("/");
        }

        url.append(itemManager.getItemPath(item));

        return url.toString();
    }

    /**
     * Retrieves <code>ITEM_ID</code> parameter from URL and instantiates item
     * according to this ID.
     *
     * @param url URL that indicates which item should be retrieved; must
     *            contain the <code>ITEM_ID</code> parameter
     *
     * @return the <code>ContentItem</code> the given <code>url</code> points
     *         to, or <code>null</code> if no ID has been found in the
     *         <code>url</code>
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected ContentItem getItemFromDraftURL(final String url) {
        LOGGER.debug("Looking up the item from draft URL ", url);

        int pos = url.indexOf(ITEM_ID);

        String item_id = url.substring(pos); // item_id == ITEM_ID=.... ?

        pos = item_id.indexOf("="); // should be exactly after the ITEM_ID string

        if (pos != ITEM_ID.length()) {
            // item_id seems to be something like ITEM_IDFOO=

            LOGGER.debug("No suitable item_id parameter found; returning null");

            return null;        // no ID found
        }

        pos++;                  // skip the "="

        // ID is the string between the equal (at pos) and the next separator
        int i = item_id.indexOf(SEPARATOR);
        item_id = item_id.substring(pos, Math.min(i, item_id.length() - 1));

        LOGGER.debug("Looking up item using item ID {}", item_id);

        final Optional<ContentItem> item = itemRepo.findById(Long.parseLong(
            item_id));

        if (item.isPresent()) {
            LOGGER.debug("Returning item {}", Objects.toString(item));
            return item.get();
        } else {
            return null;
        }
    }

    /**
     * Returns a content item based on URL relative to the root folder.
     *
     * @param url          The content item url
     * @param parentFolder The parent folder object, url must be relevant to it
     *
     * @return The Content Item instance, it can also be either Bundle or Folder
     *         objects, depending on URL and file language extension
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected ContentItem getItemFromLiveURL(final String url,
                                             final Folder parentFolder) {

        if (parentFolder == null || url == null || url.equals("")) {
            LOGGER.debug("The url is null or parent folder was null "
                             + "or something else is wrong, so just return "
                             + "null.");

            return null;
        }

        LOGGER.debug("Resolving the item for live URL {}"
                         + " and parent folder {}...",
                     url,
                     parentFolder.getName());

        int len = url.length();
        int index = url.indexOf('/');

        if (index >= 0) {
            LOGGER.debug("The URL starts with a slash; paring off the first "
                             + "URL element and recursing");

            final String liveUrl = index + 1 < len ? url.substring(index + 1)
                                       : "";

            return getItemFromLiveURL(liveUrl, parentFolder);
        } else {
            LOGGER.debug("Found a file element in the URL");

            final String[] nameAndLang = getNameAndLangFromURLFrag(url);
            final String name = nameAndLang[0];

            final Optional<ContentItem> item = itemRepo.findByNameInFolder(
                parentFolder, name);

            if (item.isPresent()) {
                return item.get();
            } else {
                return null;
            }
        }
    }

    /**
     * Returns an array containing the the item's name and lang based on the URL
     * fragment.
     *
     * @param url
     *
     * @return a two-element string array, the first element containing the
     *         bundle name, and the second element containing the lang string
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected String[] getNameAndLangFromURLFrag(final String url) {
        String name;
        String lang = null;

        /*
         * Try to find out if there's an extension with the language code
         * 1 Get a list of all "extensions", i.e. parts of the url
         *   which are separated by colons
         * 2 If one or more extensions have been found, compare them against
         *   the list of known language codes
         * 2a if a match is found, this language is used to retrieve an instance
         *    from a bundle
         * 2b if no match is found
         */
        final List<String> exts = new ArrayList<>(5);
        final StringTokenizer tok = new StringTokenizer(url, ".");

        while (tok.hasMoreTokens()) {
            exts.add(tok.nextToken());
        }

        if (exts.size() > 0) {
            LOGGER.debug("Found some file extensions to look at; they "
                             + "are {}",
                         exts);

            /*
             * We have found at least one extension, so we can
             * proceed.  Now try to find out if one of the
             * extensions looks like a language code (we only
             * support 2-letter language codes!).
             * If so, use this as the language to look for.
             */
 /*
             * First element is the NAME of the item, not an extension!
             */
            name = exts.get(0);
            String ext;
            final Collection<String> supportedLangs = KernelConfig.getConfig()
                .getSupportedLanguages();
            Iterator<String> supportedLangIt;

            for (int i = 1; i < exts.size(); i++) {
                ext = exts.get(i);

                LOGGER.debug("Examining extension {}", ext);

                /*
                 * Loop through all extensions, but discard the
                 * first one, which is the name of the item.
                 */
                if (ext != null && ext.length() == 2) {
                    /* Only check extensions consisting of 2
                     * characters.
                     *
                     * Compare current extension with known
                     * languages; if it matches, we've found the
                     * language we should use!
                     */
                    supportedLangIt = supportedLangs.iterator();
                    while (supportedLangIt.hasNext()) {
                        if (ext.equals(supportedLangIt.next())) {
                            lang = ext;
                            LOGGER.debug("Found a match; using "
                                             + "language {}", lang);
                            break;
                        }
                    }
                } else {
                    LOGGER.debug("Discarding extension {}; it is too short",
                                 ext);
                }
            }
        } else {
            LOGGER.debug("The file has no extensions; no language was encoded");
            name = url;     // no extension, so we just have a name here
            lang = null;    // no extension, so we cannot guess the language
        }

        LOGGER.debug("File name resolved to {}", name);
        LOGGER.debug("File language resolved to {}", lang);

        final String[] returnArray = new String[2];
        returnArray[0] = name;
        returnArray[1] = lang;
        return returnArray;
    }

    /**
     * Finds a language instance of a content item given the bundle, name, and
     * lang string. Note: Because there not ContentBundles anymore this method
     * simply returns the provided item.
     *
     * @param lang The lang string from the URL
     * @param item The content bundle
     *
     * @return The negotiated lang instance for the current request.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    protected ContentItem getItemFromLangAndBundle(final String lang,
                                                   final ContentItem item) {
        return item;
    }

}
