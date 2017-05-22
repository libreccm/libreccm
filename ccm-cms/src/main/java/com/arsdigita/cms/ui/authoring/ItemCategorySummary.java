/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.PageState;

import org.libreccm.categorization.Category;

import com.arsdigita.categorization.ui.ACSObjectCategorySummary;
import com.arsdigita.cms.CMS;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;
import java.util.stream.Collectors;

public class ItemCategorySummary extends ACSObjectCategorySummary {

    public ItemCategorySummary() {
        super();
    }

    @Override
    protected boolean canEdit(final PageState state) {

        final PermissionChecker permissionChecker = CdiUtil
            .createCdiUtil()
            .findBean(PermissionChecker.class);

        return permissionChecker.isPermitted(ItemPrivileges.CATEGORIZE,
                                             CMS.getContext().getContentItem());
    }


    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getObject()
     */
    @Override
    protected CcmObject getObject(final PageState state) {

        return CMS.getContext().getContentItem();
    }

    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getXMLPrefix()
     */
    @Override
    protected String getXMLPrefix() {
        return "cms";
    }

    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getXMLNameSpace()
     */
    @Override
    protected String getXMLNameSpace() {
        return CMS.CMS_XML_NS;
    }

    /* 
     * @see com.arsdigita.categorization.ui.ObjectCategorySummary#getRootCategories()
     */
    @Override
    protected List<Category> getRootCategories(final PageState state) {

        final ContentSection section = CMS.getContext().getContentSection();
        return section
            .getDomains()
            .stream()
            .map(domainOwnership -> domainOwnership.getDomain())
            .map(domain -> domain.getRoot())
            .collect(Collectors.toList());
    }

}
