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
package org.libreccm.ui.admin.themes;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.ThemeProvider;
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.Themes;
import org.libreccm.ui.admin.AdminConstants;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("Themes")
public class ThemesModel {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Themes themes;

    public List<ThemesTableRow> getThemes() {
        return themes
            .getAvailableThemes()
            .stream()
            .map(this::mapThemeInfo)
            .collect(Collectors.toList());
    }

    public Map<String, String> getProviderOptions() {
        return themes
            .getThemeProviders()
            .stream()
            .filter(ThemeProvider::supportsChanges)
            .filter(ThemeProvider::supportsDraftThemes)
            .collect(
                Collectors.toMap(
                    provider -> provider.getClass().getName(),
                    provider -> provider.getName()
                )
            );
    }

    private ThemesTableRow mapThemeInfo(final ThemeInfo themeInfo) {

        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(AdminConstants.ADMIN_BUNDLE);

        final ThemesTableRow row = new ThemesTableRow();
        row.setDescription(
            Optional
                .ofNullable(themeInfo.getManifest().getDescription())
                .map(ls -> globalizationHelper.getValueFromLocalizedString(ls))
                .orElse("")
        );
        row.setName(themeInfo.getName());
        row.setProvider(themeInfo.getProvider().getName());
        row.setTitle(
            Optional
                .ofNullable(themeInfo.getManifest().getTitle())
                .map(ls -> globalizationHelper.getValueFromLocalizedString(ls))
                .orElse("")
        );
        row.setType(themeInfo.getType());
        row.setVersion(
            textsUtil.getText(
                String.format(
                    "themes.versions.%s",
                    Objects.toString(
                        themeInfo.getVersion()).toLowerCase(Locale.ROOT)
                )
            )
        );

        row.setPublished(themeInfo.getVersion() == ThemeVersion.LIVE);

        final Optional<ThemeProvider> themeProviderResult = themes
            .findThemeProviderInstance(themeInfo.getProvider());
        if (themeProviderResult.isPresent()) {
            final ThemeProvider themeProvider = themeProviderResult.get();

            row.setEditable(themeProvider.supportsChanges());
            row.setPublishable(themeProvider.supportsDraftThemes());
        } else {
            row.setEditable(false);
            row.setPublishable(false);
        }

        return row;
    }

}
