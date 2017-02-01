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
import com.arsdigita.cms.dispatcher.CMSPage;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;

import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * The {@code ItemResolver} is responsible for mapping a URL in a particular
 * content section to a content item.
 *
 *
 * As an example, here is the item resolution process for a request to
 * {@code http://yourserver/cms/cheese}:
 *
 * The item resolver would be asked to map the URL stub {@code /cheese} in the
 * content section mounted at {@code /cms} to a content item. To this end, the
 * dispatcher calls the {@link #getItem} method, passing in the
 * {@link com.arsdigita.cms.ContentSection} and the URL stub for the item within
 * the section, {@code /cheese} in our example. As a final argument, the
 * dispatcher passes either {@link ContentItemVersion#DRAFT} or
 * {@link ContentItemVersion#LIVE} to the {@code ItemResolver}, depending on
 * whether the returned item should be the live version (for pages) or the draft
 * version (for previewing).
 *
 * Originally these interface was located in the
 * {@code org.arsdigita.cms.dispatcher} package but has been moved here when its
 * implementations had been refactored to CDI beans. Also the default
 * implementations of the {@link #getTemplateFromURL(java.lang.String)} and
 * {@link #stripTemplateFromURL(java.lang.String)} from the old
 * {@code AbstractItemResolver} class have been moved here which is now possible
 * thanks to the default methods in interfaces introduced in Java 8. The
 * class {@code AbstractItemResolver} has been removed completely.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id$
 */
public interface ItemResolver {

    public static final String TEMPLATE_CONTEXT_PREFIX = "tem_";

    /**
     * Return a content item based on section, url, and use context.
     *
     * @param section The current content section
     * @param url     The section-relative URL
     * @param context The use context
     *
     * @return The content item, or null if no such item exists
     */
    ContentItem getItem(ContentSection section,
                        String url,
                        String context);

    /**
     * Fetches the current context based on the page state.
     *
     * @param state the current page state
     *
     * @return the context of the current URL, such as "live" or "admin"
     */
    String getCurrentContext(PageState state);

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
    String generateItemURL(PageState state,
                           Long itemId,
                           String name,
                           ContentSection section,
                           String context);

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
    String generateItemURL(PageState state,
                           Long itemId,
                           String name,
                           ContentSection section,
                           String context,
                           String templateContext
    );

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
    String generateItemURL(PageState state,
                           ContentItem item,
                           ContentSection section,
                           String context);

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
    String generateItemURL(PageState state,
                           ContentItem item,
                           ContentSection section,
                           String context,
                           String templateContext);

    /**
     * Return a master page based on page state (and content section).
     *
     * @param item    The content item
     * @param request The HTTP request
     *
     * @return The master page
     *
     * @throws javax.servlet.ServletException
     */
    CMSPage getMasterPage(ContentItem item, HttpServletRequest request)
        throws ServletException;


    /*
     * Having to stick the following methods, getTemplateFromURL, and 
     * stripTemplateFromURL in the ItemResolver interface is somewhat ugly.
     * But, the relationship between ItemResolver and TemplateResolver needs
     * to be cleaned up to fix this.  As it is, ItemResolver parses URL's for
     * template contexts, and TemplateResolver sets the actual template contexts
     * in the request.
     */
    /**
     * Finds the template context from the URL and returns it, if it is there.
     * Otherwise, returns null.
     *
     * @param inUrl the URL from which to get the template context
     *
     * @return the template context, or null if there is no template context
     */
    default String getTemplateFromURL(final String inUrl) {
        final String tempUrl;
        if (inUrl.startsWith("/")) {
            tempUrl = inUrl.substring(1);
        } else {
            tempUrl = inUrl;
        }

        final StringTokenizer tokenizer = new StringTokenizer(tempUrl, "/");
        final String templateContext;
        if (tokenizer.hasMoreTokens()) {
            templateContext = tokenizer.nextToken();
        } else {
            templateContext = null;
        }

        if (templateContext != null && templateContext.startsWith(
            TEMPLATE_CONTEXT_PREFIX)) {
            return templateContext.substring(TEMPLATE_CONTEXT_PREFIX.length());
        } else {
            return null;
        }
    }

    /**
     * Removes the template context from the <code>inUrl</code>.
     *
     * @param inUrl URL, possibly including the template context.
     *
     * @return <code>inUrl</code> with the template context removed
     */
    default String stripTemplateFromURL(final String inUrl) {
        final String sTemplateContext = getTemplateFromURL(inUrl);

        if (sTemplateContext != null) {
            //there is a template context, so strip it
            final int iTemplateLength = TEMPLATE_CONTEXT_PREFIX.length()
                                            + sTemplateContext.length() + 1;
            return inUrl.substring(iTemplateLength, inUrl.length());
        } else {
            return inUrl;
        }
    }

}
