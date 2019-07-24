/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.librecms.assets;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.l10n.LocalizedString;

import java.util.Comparator;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContactEntryKeyByLabelComparator
    implements Comparator<ContactEntryKey> {

    private final Locale locale;

    private final Locale defaultLocale;

    public ContactEntryKeyByLabelComparator(final Locale locale) {

        this.locale = locale;
        defaultLocale = KernelConfig.getConfig().getDefaultLocale();
    }

    @Override
    public int compare(final ContactEntryKey key1, final ContactEntryKey key2) {

        final LocalizedString label1 = key1.getLabel();
        final LocalizedString label2 = key2.getLabel();

        final String localized1 = getLocalizedValue(label1);
        final String localized2 = getLocalizedValue(label2);

        if (localized1 == null) {
            return -1;
        } else {
            return localized1.compareTo(localized2);
        }
    }

    private String getLocalizedValue(final LocalizedString source) {

        if (source.hasValue(locale)) {
            return source.getValue(locale);
        } else if (source.hasValue(defaultLocale)) {
            return source.getValue(defaultLocale);
        } else {
            return source.getValue();
        }
    }

}
