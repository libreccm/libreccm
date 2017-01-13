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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.dispatcher.AccessDeniedException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * 
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @autor <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class TypeSecurityListener implements FormSubmissionListener {


    @Override
    public final void submitted(final FormSectionEvent event)
            throws FormProcessException {
        
        final ContentSection section = CMS.getContext().getContentSection();
        final PermissionChecker permissionChecker = CdiUtil.createCdiUtil().findBean(PermissionChecker.class);
        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section)) {
            throw new AccessDeniedException();
        }
    }
}
