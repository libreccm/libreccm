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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.cms.CMS;
import org.librecms.contentsection.ContentSection;
import org.apache.log4j.Logger;

/** 
 * 
 */
public final class ContentSectionRequestLocal extends RequestLocal {

    private static final Logger s_log = Logger.getLogger
        (ContentSectionRequestLocal.class);

    @Override
    protected Object initialValue(final PageState state) {
        return CMS.getContext().getContentSection();
    }

    public final ContentSection getContentSection(final PageState state) {
        return (ContentSection) get(state);
    }
}
