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

import com.arsdigita.cms.ui.assets.AbstractAssetFormController;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactEntryKeyByLabelComparator;
import org.librecms.assets.ContactEntryKeyRepository;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.ContactableEntityManager;
import org.librecms.assets.ContactableEntityRepository;
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.AssetRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContactableEntityFormController<T extends ContactableEntity>
    extends AbstractAssetFormController<T> {

    protected static final String POSTAL_ADDRESS = "postalAddress";

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private ContactableEntityRepository contactableEntityRepository;

    @Inject
    private ContactableEntityManager contactableEntityManager;

    @Inject
    private ContactEntryKeyRepository keyRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    protected Map<String, Object> getAssetData(final T asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = new HashMap<>();

        final PostalAddress postalAddress = asset.getPostalAddress();
        if (postalAddress != null) {
            data.put(POSTAL_ADDRESS, postalAddress.getObjectId());
        }

        return data;
    }

    @Override
    public void updateAssetProperties(final T asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        if (data.containsKey(POSTAL_ADDRESS)) {

            final long addressId = (long) data.get(POSTAL_ADDRESS);
            final PostalAddress postalAddress = assetRepository
                .findById(addressId, PostalAddress.class)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No PostalAddress with ID %d found.", addressId)));

            contactableEntityManager
                .addPostalAddressToContactableEntity(postalAddress, asset);
        }
    }

    /**
     * Returns the contact entries of the provided contentable entity for the
     * provided language.
     *
     * @param contactableId  The ID of the contactable entity.
     * @param selectedLocale The selected locale.
     *
     * @return An list of the contact entires
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<String[]> getContactEntries(
        final Long contactableId, final Locale selectedLocale) {

        Objects.requireNonNull(contactableId,
                               "Can't get contact entries from null.");

        final ContactableEntity entity = contactableEntityRepository
            .findById(contactableId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContactEntity with ID %d found.", contactableId)));

        entity
            .getContactEntries()
            .stream()
            .map(contactEntry -> toContactEntryArray(contactEntry,
                                                     selectedLocale))
            .collect(Collectors.toList());

        final List<String[]> entries = new ArrayList<>();

        return entries;
    }

    private String[] toContactEntryArray(final ContactEntry entry,
                                         final Locale selectedLocale) {

        final String key = entry.getKey().getEntryKey();
        final String label = entry.getKey().getLabel().getValue(selectedLocale);
        final String value = entry.getValue();

        return new String[]{key, label, value};
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void addContactEntry(final String contactEntryKey,
                                final String contactEntryValue,
                                final Long toContactableEntityWithId) {

        final ContactableEntity contactable = contactableEntityRepository
            .findById(toContactableEntityWithId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContactableEntity with ID %d found",
            toContactableEntityWithId)));

        final ContactEntryKey key = keyRepository
            .findByEntryKey(contactEntryKey)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContactEntryKey with key \"%s\" found.", contactEntryKey)));
        final ContactEntry entry = new ContactEntry();
        entry.setKey(key);
        entry.setValue(contactEntryValue);
        entry.setOrder(contactable.getContactEntries().size());

        contactableEntityManager
            .addContactEntryToContactableEntity(entry, contactable);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void removeContactEntry(final int withIndex,
                                   final Long fromContactableEntityWithId) {

        final ContactableEntity contactable = contactableEntityRepository
            .findById(fromContactableEntityWithId)
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContactableEntity with ID %d found",
            fromContactableEntityWithId)));

        if (contactable.getContactEntries().size() > withIndex) {

            final ContactEntry contactEntry = contactable
                .getContactEntries()
                .get(withIndex);
            contactableEntityManager.removeContactEntryFromContactableEntity(
                contactEntry, contactable
            );
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
