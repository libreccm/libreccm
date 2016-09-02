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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;

/**
 * Form to search for users to be added to a staff group.
 *
 *
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 */
public class UserSearchForm extends CMSForm {

    private final static String SEARCH_LABEL = "Search";

    private TextField m_search;

    public UserSearchForm(String name) {
        this(name,
             new Label(new GlobalizedMessage(
                 "cms.ui.search_to_add_new_members",
                 CmsConstants.CMS_BUNDLE)));

    }

    public UserSearchForm(String name, Label heading) {
        super(name, new ColumnPanel(3));
        heading.setFontWeight(Label.BOLD);
        add(heading, ColumnPanel.FULL_WIDTH);

        add(new Label(new GlobalizedMessage(
            "cms.ui.enter_first_name_last_name_andor_email_address",
            CmsConstants.CMS_BUNDLE)));

        m_search = new TextField(new StringParameter("query"));
        m_search.setSize(20);
        add(m_search, ColumnPanel.RIGHT);

        Submit submit = new Submit("submit");
        submit.setButtonLabel(SEARCH_LABEL);
        add(submit, ColumnPanel.LEFT);
    }

    public TextField getSearchWidget() {
        return m_search;
    }

}
