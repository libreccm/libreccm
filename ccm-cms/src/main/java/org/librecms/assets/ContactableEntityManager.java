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

import org.librecms.contentsection.AssetRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Manager class providing methods for adding and removing {@link ContactEntry}
 * and {@link PostalAddress} to and from a {@link ContactableEntity}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContactableEntityManager {

    @Inject
    private AssetRepository assetRepository;
    
    public void addContactEntryToContactableEntity(
        final ContactEntry contactEntry,
        final ContactableEntity contactableEntity) {

        if (contactEntry.getOrder() == 0) {
            contactEntry
                .setOrder(contactableEntity.getContactEntries().size());
        }
        contactableEntity.addContactEntry(contactEntry);
        assetRepository.save(contactableEntity);
    }
    
    public void removeContactEntryFromContactableEntity(
    final ContactEntry contactEntry,
    final ContactableEntity contactableEntity) {
        
        contactableEntity.removeContactEntry(contactEntry);
        assetRepository.save(contactableEntity);
    }
    
    public void addPostalAddressToContactableEntity(
    final PostalAddress postalAddress,
    final ContactableEntity contactableEntity) {
        
        contactableEntity.setPostalAddress(postalAddress);
        assetRepository.save(postalAddress);
    }
    
    public void removePostalAddressFromContactableEntity(
    final PostalAddress postalAddress,
    final ContactableEntity contactableEntity) {
        
        contactableEntity.setPostalAddress(null);
        assetRepository.save(postalAddress);
    }

}
