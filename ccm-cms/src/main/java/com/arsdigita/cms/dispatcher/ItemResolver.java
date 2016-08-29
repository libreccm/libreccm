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

import com.arsdigita.bebop.PageState;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

/**
 * <p>
 * The <tt>ItemResolver</tt> is responsible for mapping a URL in a particular
 * content section to a content item.</p>
 *
 * <p>
 * As an example, here is the item resolution process for a request to
 * <tt>http://yourserver/cms/cheese</tt>:</p>
 *
 * <p>
 * The item resolver would be asked to map the URL stub <tt>/cheese</tt>
 * in the content section mounted at <tt>/cms</tt> to a content item. To this
 * end, the dispatcher calls the <tt>getItem</tt> method, passing in the
 * {@link com.arsdigita.cms.ContentSection} and the URL stub for the item within
 * the section, <tt>/cheese</tt> in our example. As a final argument, the
 * dispatcher passes either <tt>ContentItem.DRAFT</tt> or
 * <tt>ContentItem.LIVE</tt> to the <tt>ItemResolver</tt>, depending on whether
 * the returned item should be the live version (for public pages) or the draft
 * version (for previewing).</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id$
 */
public interface ItemResolver {

    /**
     * Return a content item based on section, url, and use context.
     *
     * @param section The current content section
     * @param url The section-relative URL
     * @param context The use context
     * @return The content item, or null if no such item exists
     */
    public ContentItem getItem(ContentSection section,
                               String url,
                               String context);

    /**
     * Fetches the current context based on the page state.
     *
     * @param state the current page state
     * @return the context of the current URL, such as "live" or "admin"
     */
    public String getCurrentContext(PageState state);

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL(PageState state,
                                  Long itemId,
                                  String name,
                                  ContentSection section,
                                  String context);

    /**
     * Generates a URL for a content item.
     *
     * @param itemId The item ID
     * @param name The name of the content page
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL(PageState state,
                                  Long itemId,
                                  String name,
                                  ContentSection section,
                                  String context,
                                  String templateContext
    );

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL(PageState state,
                                  ContentItem item,
                                  ContentSection section,
                                  String context);

    /**
     * Generates a URL for a content item.
     *
     * @param item The item
     * @param state The page state
     * @param section the content section to which the item belongs
     * @param context the context of the URL, such as "live" or "admin"
     * @param templateContext the context for the URL, such as "public"
     * @return The URL of the item
     * @see #getCurrentContext
     */
    public String generateItemURL(PageState state,
                                  ContentItem item,
                                  ContentSection section,
                                  String context,
                                  String templateContext);

    /**
     * Return a master page based on page state (and content section).
     *
     * @param item The content item
     * @param request The HTTP request
     * @return The master page
     */
    public CMSPage getMasterPage(ContentItem item, HttpServletRequest request)
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
     * @return the template context, or null if there is no template context
     */
    public String getTemplateFromURL(String inUrl);

    /**
     * Removes the template context from the <code>inUrl</code>.
     *
     * @param inUrl URL, possibly including the template context.
     * @return <code>inUrl</code> with the template context removed
     */
    public String stripTemplateFromURL(String inUrl);

}
