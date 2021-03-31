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
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.RelatedLink;

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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsInternalLinkDetailsModel")
public class InternalLinkDetailsModel {

    @Inject
    private GlobalizationHelper globalizationHelper;

    private String listIdentifier;

    private String uuid;

    private String label;
    
    private Map<String, String> title;

    private List<String> unusedTitleLocales;

    private String targetItemUuid;

    private String targetItemName;

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
