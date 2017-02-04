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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.dispatcher.AccessDeniedException;

import com.arsdigita.util.Assert;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class FormSecurityListener implements FormSubmissionListener {

    private final String m_action;
    private final ContentItemRequestLocal m_item;

    public FormSecurityListener(final String action,
                                final ContentItemRequestLocal item) {
        Assert.exists(action, String.class);

        m_action = action;
        m_item = item;
    }

    public FormSecurityListener(final String action) {
        this(action, null);
    }

    @Override
    public final void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        if (m_item == null) {
            if (permissionChecker.isPermitted(m_action)) {
                return;
            }
        } else {
            final ContentItem item = m_item.getContentItem(state);

            if (permissionChecker.isPermitted(m_action, item)) {
                return;
            }
        }

        throw new AccessDeniedException();
    }

}
