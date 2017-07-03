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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.table.RowData;
import com.arsdigita.cms.ui.ContentItemPage;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ItemLanguagesController {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private ContentItemL10NManager itemL10NManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    protected List<RowData<String>> retrieveLanguageVariants(
        final ContentItem item) {
        return retrieveLanguageVariants(item.getObjectId());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<RowData<String>> retrieveLanguageVariants(final long itemId) {

        final ContentItem item = itemRepo
            .findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentItem with ID %d in the database.", itemId)));

        final List<Locale> availableLangs = new ArrayList<>(itemL10NManager
            .availableLanguages(item));
        availableLangs.sort((locale1, locale2) -> {
            return locale1.toString().compareTo(locale2.toString());
        });

        return availableLangs.stream()
            .map(lang -> createRow(item, lang))
            .collect(Collectors.toList());
    }

    private RowData<String> createRow(final ContentItem item,
                                      final Locale lang) {
        final RowData<String> row = new RowData<>(2);
        row.setRowKey(lang.toString());
        row.setColData(0, lang.getDisplayName(globalizationHelper
                       .getNegotiatedLocale()));
        row.setColData(1, item.getTitle().getValue(lang));

        return row;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected int countLanguageVariants(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentItem with ID %d in the database.",
            item.getObjectId())));

        return itemL10NManager.availableLanguages(contentItem).size();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void addLanguage(final ContentItem item, final String language) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentItem with ID %d in the database.",
            item.getObjectId())));

        itemL10NManager.addLanguage(contentItem, new Locale(language));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void removeLanguage(final ContentItem item,
                                  final String language) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentItem with ID %d in the database.",
            item.getObjectId())));

        itemL10NManager.removeLanguage(contentItem, new Locale(language));
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected String getItemEditUrl(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentItem with ID %d in the database.",
            item.getObjectId())));

        return ContentItemPage.getItemURL(contentItem,
                                          ContentItemPage.AUTHORING_TAB);
    }

}
