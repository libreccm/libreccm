/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.tree.TreeNode;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationTypeTreeNode implements TreeNode {

    private final ApplicationType applicationType;

    public ApplicationTypeTreeNode(final ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public Object getKey() {
        return applicationType.name();
    }

    @Override
    public Object getElement() {
        final org.libreccm.web.ApplicationManager appManager = CdiUtil
            .createCdiUtil().findBean(org.libreccm.web.ApplicationManager.class);

        return appManager.getApplicationTypeTitle(applicationType);
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

}
