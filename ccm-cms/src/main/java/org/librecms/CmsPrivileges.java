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
package org.librecms;

import org.libreccm.security.Privilege;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public enum CmsPrivileges implements Privilege {

    ADMINISTER_CATEGORIES,
    ADMINISTER_CONTENT_TYPES,
    ADMINISTER_LIFECYLES,
    ADMINISTER_ROLES,
    ADMINISTER_WORKFLOW,
    ITEMS_APPROVE,
    ITEMS_PUBLISH,
    ITEMS_CATEGORIZE,
    ITEMS_CREATE_NEW,
    ITEMS_DELETE,
    ITEMS_EDIT,
    ITEMS_PREVIEW,
    ITEMS_VIEW_PUBLISHED,
    APPLY_ALTERNATE_WORKFLOW;

    @Override
    public String getBundle() {
        return CmsConstants.CMS_BUNDLE;
    }
    
    @Override
    public String getPrefix() {
        return "privileges";
    }

}
