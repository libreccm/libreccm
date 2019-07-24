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
package com.arsdigita.cms.ui.assets.forms;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactEntryKeyByLabelComparator;
import org.librecms.assets.ContactEntryKeyRepository;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.ContactableEntityManager;
import org.librecms.assets.ContactableEntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AbstractContactableEntityFormController {

    @Inject
    private ContactableEntityRepository contactableEntityRepository;

    @Inject
    private ContactableEntityManager contactableEntityManager;

    @Inject
    private ContactEntryKeyRepository keyRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<ContactEntry> getContactEntries(
        final ContactableEntity contactable) {

        Objects.requireNonNull(contactable,
                               "Can't get contact entries from null.");

        final ContactableEntity entity = contactableEntityRepository
            .findById(contactable.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContactEntity with ID %d found.",
            contactable.getObjectId())));

        final List<ContactEntry> entries = new ArrayList<>();
        for (final ContactEntry entry : entity.getContactEntries()) {

            entries.add(entry);
        }

        return entries;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void addContactEntry(final ContactEntry contactEntry,
                                final ContactableEntity toContactableEntity) {

        contactableEntityManager
            .addContactEntryToContactableEntity(contactEntry,
                                                toContactableEntity);

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeContactEntry(final ContactableEntity contactableEntity,
                                   final int index) {

        if (contactableEntity.getContactEntries().size() > index) {

            final ContactEntry contactEntry = contactableEntity
                .getContactEntries()
                .get(index);
            contactableEntityManager.removeContactEntryFromContactableEntity(
                contactEntry, contactableEntity);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<ContactEntryKey> findAvailableContactEntryKeys() {

        final Locale locale = globalizationHelper.getNegotiatedLocale();
        
        return keyRepository
            .findAll()
            .stream()
            .sorted(new ContactEntryKeyByLabelComparator(locale))
            .collect(Collectors.toList());
    }

}
