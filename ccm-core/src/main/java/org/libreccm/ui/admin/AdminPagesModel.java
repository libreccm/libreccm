/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin;

import org.libreccm.l10n.GlobalizationHelper;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AdminPagesModel")
public class AdminPagesModel {

    @Inject
    private Instance<AdminPage> adminPages;

    @Inject
    private GlobalizationHelper globalizationHelper;

    public List<AdminPageModel> getAdminPages() {
        return adminPages
            .stream()
            .sorted(
                (page1, page2) -> Integer.compare(
                    page1.getPosition(), page2.getPosition()
                )
            )
            .map(this::buildAdminPageModel)
            .collect(Collectors.toList());
    }

    private AdminPageModel buildAdminPageModel(final AdminPage fromAdminPage) {
        final ResourceBundle labelBundle = ResourceBundle.getBundle(
            fromAdminPage.getLabelBundle(),
            globalizationHelper.getNegotiatedLocale()
        );
        final ResourceBundle descriptionBundle = ResourceBundle.getBundle(
            fromAdminPage.getDescriptionBundle(),
            globalizationHelper.getNegotiatedLocale()
        );

        final AdminPageModel model = new AdminPageModel();
        model.setPath(fromAdminPage.getPath());
        model.setLabel(labelBundle.getString(fromAdminPage.getLabelKey()));
        model.setDescription(
            descriptionBundle.getString(
                fromAdminPage.getDescriptionKey()
            )
        );
        model.setIcon(fromAdminPage.getIcon());
        return model;
    }

}
