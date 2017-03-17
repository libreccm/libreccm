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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;

import com.arsdigita.cms.ui.BaseForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class BaseLifecycleForm extends BaseForm {

    private final TextField lifecycleName;
    private final TextArea lifecycleDescription;

    BaseLifecycleForm(final String key, final GlobalizedMessage message) {
        super(key, message);

        lifecycleName = new TextField(new TrimmedStringParameter("label"));
        addField(gz("cms.ui.lifecycle.name"), lifecycleName);

        lifecycleName.addValidationListener(new NotEmptyValidationListener());
        lifecycleName.setSize(40);
        lifecycleName.setMaxLength(1000);

        lifecycleDescription = new TextArea(
            new TrimmedStringParameter("description"));
        addField(gz("cms.ui.lifecycle.description"), lifecycleDescription);

        lifecycleDescription.addValidationListener(
            new StringLengthValidationListener(4000));
        lifecycleDescription.setCols(40);
        lifecycleDescription.setRows(5);
        lifecycleDescription.setWrap(TextArea.SOFT);

        addAction(new Finish());
        addAction(new Cancel());

        addSubmissionListener(new FormSecurityListener(
            AdminPrivileges.ADMINISTER_LIFECYLES));
    }

    protected TextField getLifecycleName() {
        return lifecycleName;
    }

    protected TextArea getLifecycleDescription() {
        return lifecycleDescription;
    }

}
