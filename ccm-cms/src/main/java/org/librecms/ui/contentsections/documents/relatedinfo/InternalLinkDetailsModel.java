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
package org.librecms.ui.contentsections.documents.relatedinfo;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.librecms.assets.RelatedLink;
import org.librecms.contentsection.AttachmentList;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Model proving the properties of an internal {@link RelatedLink} for the
 * internal link edit view of the {@link RelatedInfoStep}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsInternalLinkDetailsModel")
public class InternalLinkDetailsModel {

    /**
     * Used to retrieve values from {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * The identifier of the {@link AttachmentList} of the link.
     */
    private String listIdentifier;

    /**
     * The UUID of the link.
     */
    private String uuid;

    /**
     * The label of the link.
     */
    private String label;

    /**
     * The localized titles of the link.
     */
    private Map<String, String> title;

    /**
     * The locales for which no title has been specified.
     */
    private List<String> unusedTitleLocales;

    /**
     * The UUID of the target item of the link.
     */
    private String targetItemUuid;

    /**
     * The name of the target item of the link
     */
    private String targetItemName;

    /**
     * The title of the target item of the link. This value is determined from
     * the title of the target item using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String targetItemTitle;

    public String getListIdentifier() {
        return listIdentifier;
    }

    protected void setListIdentifier(final String listIdentifier) {
        this.listIdentifier = listIdentifier;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLabel() {
        return label;
    }

    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public String getTargetItemUuid() {
        return targetItemUuid;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public String getTargetItemTitle() {
        return targetItemTitle;
    }

    /**
     * Sets the properties of this model based on the properties on the provided
     * link.
     *
     * @param link The link to use.
     */
    protected void setInternalLink(final RelatedLink link) {
        Objects.requireNonNull(link);

        uuid = link.getUuid();
        label = globalizationHelper.getValueFromLocalizedString(
            link.getTitle()
        );
        title = link
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        final Set<Locale> titleLocales = link.getTitle().getAvailableLocales();
        unusedTitleLocales = globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .collect(Collectors.toList());
        targetItemUuid = link.getTargetItem().getItemUuid();
        targetItemName = link.getTargetItem().getDisplayName();
        targetItemTitle = globalizationHelper.getValueFromLocalizedString(
            link.getTargetItem().getTitle()
        );
    }

}
