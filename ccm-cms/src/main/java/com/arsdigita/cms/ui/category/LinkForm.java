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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ui.CategoryForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.toolbox.ui.Cancellable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * A form which edits secondary parents
 *
 * @author Michael Pih
 * @author Stanislav Freidin
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class LinkForm extends CategoryForm implements Cancellable {

    private static final Logger LOGGER = LogManager.getLogger(
            LinkForm.class);

    private final CategoryRequestLocal m_category;
    private final Submit m_cancelButton;

    public LinkForm(final CategoryRequestLocal category) {
        super("LinkForm");

        m_category = category;

        m_cancelButton = new Submit("Finish");
        add(m_cancelButton, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        setAssignedCaption("Linked Categories");

        addSubmissionListener
            (new FormSecurityListener(AdminPrivileges.ADMINISTER_CATEGORIES));
    }

    public final boolean isCancelled(final PageState state) {
        return m_cancelButton.isSelected(state);
    }

    /**
     * Load all categories which are assigned to the current item.
     */
    protected void initAssignedCategories(PageState state, CategoryMap m) {
        final Category category = m_category.getCategory(state);
        m.add(category.getParentCategory());
        /*final BigDecimal parentID = category.getDefaultParentCategory().getID();
        CategoryCollection links = category.getParents();

        while ( links.next() ) {
            Category cat = links.getCategory();

            if ( !cat.getID().equals(parentID) ) {
                m.add(cat);
            }
        }
        links.close();*/
    }

    /**
     * Assign a secondary parent.
     */
    public void assignCategory(PageState state, Category category) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
        final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);
        final Category child = m_category.getCategory(state);
        if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, category)) {
            categoryManager.addSubCategoryToCategory(child, category);
        }
    }

    /**
     * Unassign a secondary parent.
     */
    public void unassignCategory(PageState state, Category category) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
        final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);
        final Category child = m_category.getCategory(state);
        if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, category)) {
            categoryManager.removeSubCategoryFromCategory(child, category);
        }
    }

    /**
     * The category cannot be its own parent. Its children cannot
     * be parents either.
     */
    @Override
    public Category getExcludedCategory(PageState state) {
        return m_category.getCategory(state);
    }

     /**
      *  This method returns the URL for the given item to make sure that
      *  there are not two objects in the same category with the same URL.
      */
     protected final String getItemURL(final PageState state) {
         return m_category.getCategory(state).getName();
         //return m_category.getCategory(state).getURL();
     }

     protected final CcmObject getObject(final PageState state) {
         return (Category) m_category.getCategory(state);
     }
}
