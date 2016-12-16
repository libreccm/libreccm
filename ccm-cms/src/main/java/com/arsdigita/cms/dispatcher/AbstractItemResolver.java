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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.util.StringTokenizer;

/**
 * @author bche
 */
public abstract class AbstractItemResolver implements ItemResolver {

    protected static final String TEMPLATE_CONTEXT_PREFIX = "tem_";

    @Override
    public abstract ContentItem getItem(
        ContentSection section,
        String url,
        String context);

    @Override
    public abstract String getCurrentContext(PageState state);

    @Override
    public abstract String generateItemURL(
        PageState state,
        Long itemId,
        String name,
        ContentSection section,
        String context);

    @Override
    public abstract String generateItemURL(PageState state,
                                           Long itemId,
                                           String name,
                                           ContentSection section,
                                           String context,
                                           String templateContext);

    @Override
    public abstract String generateItemURL(PageState state,
                                           ContentItem item,
                                           ContentSection section,
                                           String context);

    @Override
    public abstract String generateItemURL(PageState state,
                                           ContentItem item,
                                           ContentSection section,
                                           String context,
                                           String templateContext);

    @Override
    public abstract CMSPage getMasterPage(ContentItem item,
                                          HttpServletRequest request)
        throws ServletException;

    /**
     * Finds the template context from the URL and returns it, if it is there.
     * Otherwise, returns null.
     *
     * @param inUrl the URL from which to get the template context
     *
     * @return the template context, or null if there is no template context
     */
    @Override
    public String getTemplateFromURL(final String inUrl) {
        String tempUrl;
        String url;
        if (inUrl.startsWith("/")) {
            tempUrl = inUrl.substring(1);
        } else {
            tempUrl = inUrl;
        }

        String templateContext = null;
        StringTokenizer tokenizer = new StringTokenizer(tempUrl, "/");

        if (tokenizer.hasMoreTokens()) {
            templateContext = tokenizer.nextToken();
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
    @Override
    public String stripTemplateFromURL(final String inUrl) {
        
        String sTemplateContext = getTemplateFromURL(inUrl);

        if (sTemplateContext != null) {
            //there is a template context, so strip it
            int iTemplateLength = TEMPLATE_CONTEXT_PREFIX.length()
                                      + sTemplateContext.length() + 1;
            String sStripped = inUrl.substring(iTemplateLength, inUrl.length());
            return sStripped;
        } else {
            return inUrl;
        }
    }

}
