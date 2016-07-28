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

import com.arsdigita.dispatcher.Dispatcher;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


/**
 * An interface for resources that can be served.
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id$ 
 **/
public interface ResourceHandler extends Dispatcher {

    /**
     * This method is called by the {@link com.arsdigita.dispatcher.Dispatcher}
     * that initializes this page.
     */
    public void init() throws ServletException;

    /**
     * Fetches the content section context for this resource.
     *
     * @param request The HTTP request
     * @return A content section or null if there is none
     * @pre ( request != null )
     */
    public ContentSection getContentSection(HttpServletRequest request);

    /**
     * Fetches the content item context for this resource.
     *
     * @param request The HTTP request
     * @return A content item or null if there is none
     * @pre ( request != null )
     */
    public ContentItem getContentItem(HttpServletRequest request);

}
