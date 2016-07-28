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

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.util.Assert;

import org.librecms.contentsection.ContentSection;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>A {@link com.arsdigita.cms.dispatcher.CMSPage} used for serving
 * content items.</p>
 *
 * <p>This page contains a <code>ContentPanel</code> component which fetches
 * the {@link com.arsdigita.cms.dispatcher.XMLGenerator} for the content
 * section.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id$
 */
public class MasterPage extends CMSPage {

    public MasterPage() {
        super("Master", new SimpleContainer());
        setIdAttr("master_page");

        add(new ContentPanel());
    }

    /**
     * Fetch the request-local content section.
     *
     * @param request The HTTP request
     * @return The current content section
     */
    public ContentSection getContentSection(HttpServletRequest request) {
        // Resets all content sections associations.
        ContentSection section = super.getContentSection(request);
        Assert.exists(section);
        return section;
    }


}
