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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Model for the available admin pages.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AdminPagesModel")
public class AdminPagesModel {

    /**
     * Injection point for the admin pages.
     */
    @Inject
    private Instance<AdminPage> adminPages;

    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Cache for bundles
     */
    private final Map<String, ResourceBundle> bundles = new HashMap<>();

    /**
     * Retrieves the available admin pages and converts them into
     * {@link AdminPageModel}s for usage in the views.
     *
     * @return A list of the available admin pages.
     */
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
        final ResourceBundle labelBundle = getBundle(
            fromAdminPage.getLabelBundle()
        );
        final ResourceBundle descriptionBundle = getBundle(
            fromAdminPage.getDescriptionBundle()
        );

        final AdminPageModel model = new AdminPageModel();
        model.setPageUri(fromAdminPage.getPageUri());
        model.setLabel(labelBundle.getString(fromAdminPage.getLabelKey()));
        model.setDescription(
            descriptionBundle.getString(
                fromAdminPage.getDescriptionKey()
            )
        );
        model.setIcon(fromAdminPage.getIcon());
        model.setPosition(fromAdminPage.getPosition());
        return model;
    }

    private ResourceBundle getBundle(final String bundleName) {
        if (bundles.containsKey(bundleName)) {
            return bundles.get(bundleName);
        } else {
            final ResourceBundle bundle = ResourceBundle.getBundle(
                bundleName,
                globalizationHelper.getNegotiatedLocale()
            );
            bundles.put(bundleName, bundle);
            return bundle;
        }
    }

}
