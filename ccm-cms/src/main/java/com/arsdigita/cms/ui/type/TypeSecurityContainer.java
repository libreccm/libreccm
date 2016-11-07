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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.dispatcher.Utilities;

import org.libreccm.security.Party;

import com.arsdigita.toolbox.ui.SecurityContainer;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
public final class TypeSecurityContainer extends SecurityContainer {

    public TypeSecurityContainer(final Component component) {
        super(component);
    }

    @Override
    protected final boolean canAccess(final Party party,
                                      final PageState state) {
        final PermissionChecker permissionChecker = CdiUtil.createCdiUtil().findBean(PermissionChecker.class);
        
        final ContentSection section = CMS.getContext().getContentSection();
        
        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section);
    }
}
