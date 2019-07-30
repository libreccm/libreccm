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
package org.librecms.pagemodel.assets;

import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.Organization;
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContactableEntityRenderer extends AbstractAssetRenderer {

    @Inject
    private AssetRenderers assetRenderers;

    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        final ContactableEntity contactable;
        if (asset instanceof ContactableEntity) {
            contactable = (ContactableEntity) asset;
        } else {
            return;
        }

        final Map<String, String> contactEntries = buildContactEntries(
            contactable);
        result.put("contactEntries", contactEntries);

        if (contactable.getPostalAddress() != null) {
            
            final AbstractAssetRenderer postalAddressRenderer = assetRenderers
            .findRenderer(PostalAddress.class);
            final PostalAddress postalAddress = contactable.getPostalAddress();

            result.put("postalAddress", 
                       postalAddressRenderer.render(postalAddress, language));
        }
    }

    private Map<String, String> buildContactEntries(
        final ContactableEntity contactable) {

        return contactable
            .getContactEntries()
            .stream()
            .map(this::buildContactEntry)
            .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
    }

    private String[] buildContactEntry(final ContactEntry entry) {

        final String key = entry.getKey().getEntryKey();
        final String value = entry.getValue();

        return new String[]{key, value};
    }

}
