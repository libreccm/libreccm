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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.applications.AbstractAppSettingsPane;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.librecms.CmsConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SettingsPane extends AbstractAppSettingsPane {

    public SettingsPane(
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {
        super(selectedAppType, selectedAppInstance);
    }

    @Override
    protected void createWidgets() {
        final Label label = new Label(new GlobalizedMessage(
            "contentsection.ui.admin.app_note", CmsConstants.CMS_BUNDLE));
        add(label);

        final ActionLink link = new ActionLink(new GlobalizedMessage(
            "contentsection.ui.admin.link_app", CmsConstants.CMS_BUNDLE));
        link.addActionListener(e -> {
            final PageState state = e.getPageState();
            final String primaryUrl;
            if (getSelectedAppInstance(state).get().getPrimaryUrl().startsWith(
                "/")) {
                primaryUrl = getSelectedAppInstance(state).get().getPrimaryUrl();
            } else {
                primaryUrl = String.format(
                    "/%s",
                    getSelectedAppInstance(state).get().getPrimaryUrl());
            }
            throw new RedirectSignal(
                URL.there(state.getRequest(), primaryUrl, null),
                false);
        });
        add(link);

    }

}
