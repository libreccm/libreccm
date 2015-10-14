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
package com.arsdigita.ui.admin;


import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.EmailParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.ColumnPanel;

import static com.arsdigita.ui.admin.AdminConstants.*;

/**
 * Base form for add/edit group.
 *
 * @author David Dao
 * @version $Id$
 */
class GroupForm extends Form {

    protected TextField m_name;
//    protected TextField m_email;

    public GroupForm(String formName) {
        super(formName);

        m_name = new TextField(new StringParameter(GROUP_FORM_INPUT_NAME));
        m_name.setMaxLength(200);
        m_name.addValidationListener(new NotEmptyValidationListener());
        m_name.addValidationListener(new StringLengthValidationListener (200));


        add(GROUP_FORM_LABEL_NAME);
        add(m_name);

//        m_email = new TextField(new EmailParameter(GROUP_FORM_INPUT_PRIMARY_EMAIL));
//        m_email.setMaxLength(100);
//        add(GROUP_FORM_LABEL_PRIMARY_EMAIL);
//        add(m_email);

        // Submit button
        add(new Submit(GROUP_FORM_SUBMIT), ColumnPanel.CENTER |
            ColumnPanel.FULL_WIDTH);
    }
}
