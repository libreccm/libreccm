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


import java.math.BigDecimal;
import java.util.HashMap;


/**
 * <p>This class contains methods for registering and resolving {@link
 * ResourceHandler CMS resources} in a specific content section.</p>
 *
 * <p>The <tt>PageResolver</tt> includes methods for caching resource
 * mappings.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision$ $Date$
 * @version $Id$ 
 */
public abstract class PageResolver {

    private BigDecimal m_sectionID;

    // Used for caching pages
    private HashMap m_pages;


    public PageResolver() {
        m_pages = new HashMap();
    }

    public void setContentSectionID(BigDecimal id) {
        m_sectionID = id;
    }

    protected BigDecimal getContentSectionID() {
        return m_sectionID;
    }


    /**
     * Fetch the page associated with the request URL.
     *
     * @param url The content section-relative URL stub
     * @return The resource
     */
    public ResourceHandler getPage(String url) {
        return (ResourceHandler) m_pages.get(url);
    }

    /**
     * Register a page to the content section.
     *
     * @param page The master page
     * @param url The desired URL of the page
     */
    public abstract void registerPage(ResourceHandler page, String url);


    /**
     * Register a page to the content section.
     *
     * @param page The master page
     * @param url The desired URL of the page
     */
    public abstract void unregisterPage(ResourceHandler page, String url);


    /**
     * Loads a page into the page resolver cache.
     *
     * @param url  The URL of the resource to load into the cache
     * @param page The resource
     */
    public void loadPage(String url, ResourceHandler page) {
        m_pages.put(url, page);
    }

    /**
     * Flushes a page from the page resolver cache.
     *
     * @param url The URL of the resource to remove from the cache
     */
    public void releasePage(String url) {
        m_pages.remove(url);
    }

}
