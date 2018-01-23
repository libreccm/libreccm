/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.category;

import org.libreccm.categorization.Category;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoryLocalizationTableController {

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<CategoryLocalizationTableRow> getCategoryLocalizations(
        final Category forCategory) {

        final Map<Locale, CategoryLocalizationTableRow> localizations
                                                            = new HashMap<>();

        final Set<Locale> locales = new HashSet<>();
        locales.addAll(forCategory.getTitle().getAvailableLocales());
        locales.addAll(forCategory.getDescription().getAvailableLocales());

        return locales
            .stream()
            .map(locale -> generateRow(locale, forCategory))
            .sorted()
            .collect(Collectors.toList());
    }

    private CategoryLocalizationTableRow generateRow(final Locale locale,
                                                     final Category category) {

        final CategoryLocalizationTableRow row
                                           = new CategoryLocalizationTableRow(
                locale);
        
        if (category.getTitle().hasValue(locale)) {
            row.setTitle(category.getTitle().getValue(locale));
        }
        
        if (category.getDescription().hasValue(locale)) {
            row.setDescription(category.getDescription().getValue(locale));
        }
        
        return row;
    }

}
