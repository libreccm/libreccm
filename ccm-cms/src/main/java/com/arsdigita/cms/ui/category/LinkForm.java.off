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
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.CategoryForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.toolbox.ui.Cancellable;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * A form which edits secondary parents
 *
 * @author Michael Pih
 * @author Stanislav Freidin
 * @version $Revision: #18 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class LinkForm extends CategoryForm implements Cancellable {
    private final static Logger s_log = Logger.getLogger(LinkForm.class);

    private final CategoryRequestLocal m_category;
    private final Submit m_cancelButton;

    public LinkForm(final CategoryRequestLocal category) {
        super("LinkForm");

        m_category = category;

        m_cancelButton = new Submit("Finish");
        add(m_cancelButton, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        setAssignedCaption("Linked Categories");

        addSubmissionListener
            (new FormSecurityListener(SecurityManager.CATEGORY_ADMIN));
    }

    public final boolean isCancelled(final PageState state) {
        return m_cancelButton.isSelected(state);
    }

    /**
     * Load all categories which are assigned to the current item.
     */
    protected void initAssignedCategories(PageState state, CategoryMap m) {
        final Category category = m_category.getCategory(state);

        final BigDecimal parentID = category.getDefaultParentCategory().getID();
        CategoryCollection links = category.getParents();

        while ( links.next() ) {
            Category cat = links.getCategory();

            if ( !cat.getID().equals(parentID) ) {
                m.add(cat);
            }
        }
        links.close();
    }

    /**
     * Assign a secondary parent.
     */
    public void assignCategory(PageState state, Category category) {
        final Category child = m_category.getCategory(state);
        if (category.canEdit()) {
            category.addChild(child);
            category.save();
        }
    }

    /**
     * Unassign a secondary parent.
     */
    public void unassignCategory(PageState state, Category category) {
        final Category child = m_category.getCategory(state);

        if (category.canEdit()) {
            category.removeChild(child);
            category.save();
        }
    }

    /**
     * The category cannot be its own parent. Its children cannot
     * be parents either.
     */
    public Category getExcludedCategory(PageState state) {
        return m_category.getCategory(state);
    }

     /**
      *  This method returns the URL for the given item to make sure that
      *  there are not two objects in the same category with the same URL.
      */
     protected final String getItemURL(final PageState state) {
         return m_category.getCategory(state).getURL();
     }

     protected final ACSObject getObject(final PageState state) {
         return (Category) m_category.getCategory(state);
     }
}
