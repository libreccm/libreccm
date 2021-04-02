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
package com.arsdigita.cms.contenttypes.ui;

import org.librecms.assets.Person;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.profilesite.ProfileSiteItem;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ProfileSiteItemController {

    public static final String OWNER = "owner";

    public static final String POSITION = "position";
    
    public static final String INTERSETS = "interests";
    
    public static final String MISC = "misc";
    
    @Inject
    private AssetRepository assetRepository;

    @Inject
    private ContentItemRepository itemRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    public void setOwner(final long profileSiteItemId, final long ownerId) {
        final ProfileSiteItem profileSiteItem = itemRepository
            .findById(profileSiteItemId, ProfileSiteItem.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No ProfileSiteItem with ID %d found.",
                        profileSiteItemId
                    )
                )
            );

        final Person owner = assetRepository
            .findById(ownerId, Person.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No Person with ID %d found.", ownerId
                    )
                )
            );

        profileSiteItem.setOwner(owner);
        itemRepository.save(profileSiteItem);
    }

    public void setPosition(
        final long profileSiteItemId, final String position, final Locale locale
    ) {
        final ProfileSiteItem profileSiteItem = itemRepository
            .findById(profileSiteItemId, ProfileSiteItem.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No ProfileSiteItem with ID %d found.",
                        profileSiteItemId
                    )
                )
            );
        profileSiteItem.getPosition().addValue(locale, position);
    }

    public void setInterests(
        final long profileSiteItemId, 
        final String interests, 
        final Locale locale
    ) {
        final ProfileSiteItem profileSiteItem = itemRepository
            .findById(profileSiteItemId, ProfileSiteItem.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No ProfileSiteItem with ID %d found.",
                        profileSiteItemId
                    )
                )
            );
        profileSiteItem.getInterests().addValue(locale, interests);
    }

    public void setMisc(
        final long profileSiteItemId, final String misc, final Locale locale
    ) {
        final ProfileSiteItem profileSiteItem = itemRepository
            .findById(profileSiteItemId, ProfileSiteItem.class)
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "No ProfileSiteItem with ID %d found.",
                        profileSiteItemId
                    )
                )
            );
        profileSiteItem.getMisc().addValue(locale, misc);
    }

}
