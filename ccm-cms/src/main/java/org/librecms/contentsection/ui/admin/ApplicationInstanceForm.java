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
package org.librecms.contentsection.ui.admin;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.ui.admin.applications.DefaultApplicationInstanceForm;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentSectionManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationInstanceForm extends DefaultApplicationInstanceForm {

    public ApplicationInstanceForm(
        final String name,
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {

        super(name, selectedAppType, selectedAppInstance);
    }

    @Override
    protected FormProcessListener createProcessListener() {
        return (FormSectionEvent e) -> {
            final PageState state = e.getPageState();
            if (getSaveCancelSection().getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();

                final String primaryUrlData = data.getString(
                    "new_instance_primary_url");

                final ContentSectionManager manager = CdiUtil.createCdiUtil()
                    .findBean(ContentSectionManager.class);

                manager.createContentSection(primaryUrlData);
            }
        };
    }

}
