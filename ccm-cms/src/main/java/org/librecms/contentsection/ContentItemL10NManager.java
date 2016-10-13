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
package org.librecms.contentsection;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.CmsConstants;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

/**
 * Manages the language versions of a content item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemL10NManager {

    /**
     * Checks if an content item has data for particular language.
     *
     * @param item   The item to check.
     * @param locale The locale to check for.
     *
     * @return {@link true} if the item has data for the language, {@code false}
     *         if not.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasLanguage(final ContentItem item, final Locale locale) {

        throw new UnsupportedOperationException();
    }

    /**
     * Adds a language to a content item. The method will retrieve all fields of
     * the type {@link LocalizedString} from the item and add a new entry for
     * the provided locale by coping the value for the default language
     * configured in {@link KernelConfig}. If a field does not have an entry for
     * the default language the first value found is used.
     *
     * @param item   The item to which the language variant is added.
     * @param locale The locale of the language variant to add.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addLanguage(
        @RequiresPrivilege(CmsConstants.PRIVILEGE_ITEMS_EDIT)
        final ContentItem item,
        final Locale locale) {

        throw new UnsupportedOperationException();
    }

    /**
     * Removes a language variant from a content item. This method will retrieve
     * all fields of the type {@link LocalizedString} from the item and remove
     * the entry for the provided locale if the field has an entry for that
     * locale.
     *
     * @param item   The item from which the language variant is removed.
     * @param locale The locale of the language variant to remove.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeLangauge(
        @RequiresPrivilege(CmsConstants.PRIVILEGE_ITEMS_EDIT)
        final ContentItem item, 
        final Locale locale) {
        
        throw new UnsupportedOperationException();
    }

    /**
     * This method normalises the values of the fields of type
     * {@link LocalizedString} of an item. The method will first retrieve all
     * fields of the type {@link LocalizedString} from the item and than build a
     * set with all locales provided by any of the fields. After that the method
     * will iterate over all {@link LocalizedString} fields and check if the
     * {@link LocalizedString} has an entry for every language in the set. If
     * not an entry for the language is added.
     *
     * @param item The item to normalise.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void normalizedLanguages(
        @RequiresPrivilege(CmsConstants.PRIVILEGE_ITEMS_EDIT)
        final ContentItem item) {
        
        throw new UnsupportedOperationException();
    }

}
