/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.Bookmark;
import org.librecms.contentsection.AssetRepository;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsBookmarkCreateStep")
public class BookmarkCreateStep extends AbstractMvcAssetCreateStep<Bookmark> {

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String bookmarkDescription;

    private String url;

    @Override
    public String showCreateStep() {
        return "org/librecms/ui/contentsection/assets/bookmark/create-bookmark.xhtml";
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("bookmark.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("bookmark.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

    public String getUrl() {
        return url;
    }

    public String getBookmarkDescription() {
        return bookmarkDescription;
    }

    @Override
    protected Class<Bookmark> getAssetClass() {
        return Bookmark.class;
    }

    @Override
    protected String setAssetProperties(
        final Bookmark bookmark, final Map<String, String[]> formParams
    ) {
        url = Optional
            .ofNullable(formParams.get("url"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse("");
        bookmarkDescription = Optional
            .ofNullable(formParams.get("description"))
            .filter(value -> value.length > 0)
            .map(value -> value[0])
            .orElse("");

        if (url.isEmpty() || url.matches("\\s*")) {
            addMessage(
                "warning",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("bookmark.create.url.missing")
            );
            return showCreateStep();
        }

        bookmark.setUrl(url);
        bookmark
            .getDescription()
            .addValue(new Locale(getInitialLocale()), bookmarkDescription);
        assetRepository.save(bookmark);

        return String.format(
            "redirect:/%s/assets/%s/%s/@bookmark-edit",
            getContentSectionLabel(),
            getFolderPath(),
            getName()
        );
    }

}
