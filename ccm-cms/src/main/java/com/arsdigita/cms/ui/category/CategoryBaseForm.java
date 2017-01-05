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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.log4j.Logger;
import org.libreccm.categorization.Category;

/**
 * A form which creates a new category. Extends the edit form for
 * convenience.
 *
 * @author Michael Pih
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @version $Id: CategoryBaseForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class CategoryBaseForm extends CMSForm {
    private static final Logger s_log =
        Logger.getLogger(CategoryBaseForm.class);

    final CategoryRequestLocal m_parent;

    final FormErrorDisplay m_errors;
    final TextField m_title;
    final TextArea m_description;
    final SaveCancelSection m_saveCancelSection;

    /**
     * Constructor.
     */
    CategoryBaseForm(final String string, final CategoryRequestLocal parent) {
        super("AddSubcategories");

        m_parent = parent;

        // Form header
        Label header = new Label(gz("cms.ui.category.add"));
        add(header, GridPanel.FULL_WIDTH);

        // Form errors
        m_errors = new FormErrorDisplay(this);
        add(m_errors, GridPanel.FULL_WIDTH);

        // Name
        m_title = new TextField(new TrimmedStringParameter("name"));
        m_title.setSize(30);
        m_title.setMaxLength(30);
        m_title.addValidationListener(new NotNullValidationListener());
        m_title.addValidationListener(new TitleUniqueListener());
        add(new Label(gz("cms.ui.name")));
        add(m_title);

        // Description
        m_description = new TextArea(new TrimmedStringParameter("description"));
        m_description.setWrap(TextArea.SOFT);
        m_description.setRows(5);
        m_description.setCols(40);
        m_description.addValidationListener(new NotNullValidationListener());
        add(new Label(gz("cms.ui.description")));
        add(m_description);

        // Save and cancel buttons
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, GridPanel.FULL_WIDTH | GridPanel.LEFT);
    }

    public final boolean isCancelled(final PageState state) {
        return m_saveCancelSection.getCancelButton().isSelected(state);
    }

    static GlobalizedMessage gz(final String key) {
        return GlobalizationUtil.globalize(key);
    }

    static String lz(final String key) {
        return (String) gz(key).localize();
    }

    class TitleUniqueListener implements ParameterListener {
        public final void validate(final ParameterEvent e)
            throws FormProcessException {

            final PageState state = e.getPageState();
            final String title = (String) m_title.getValue(state);

            final Category parent = m_parent.getCategory(state);
            final java.util.List<Category> children = parent.getSubCategories();

            for (Category child : children) {
                if (child.getName().equalsIgnoreCase(title)) {
                    throw new FormProcessException
                        (GlobalizationUtil.globalize("cms.ui.category.name_not_unique"));
                }
            }
        }
    }
}
