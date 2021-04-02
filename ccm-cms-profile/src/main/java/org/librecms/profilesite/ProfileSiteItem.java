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
package org.librecms.profilesite;

import com.arsdigita.cms.contenttypes.ui.ProfileSiteItemCreate;
import com.arsdigita.cms.contenttypes.ui.ProfileSiteItemInterestsStep;
import com.arsdigita.cms.contenttypes.ui.ProfileSiteItemMiscStep;
import com.arsdigita.cms.contenttypes.ui.ProfileSiteItemPositionStep;
import com.arsdigita.cms.contenttypes.ui.ProfileSiteItemPropertiesStep;

import org.libreccm.l10n.LocalizedString;
import org.librecms.assets.Person;
import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.AuthoringKit;
import org.librecms.contenttypes.AuthoringStep;
import org.librecms.contenttypes.ContentTypeDescription;

import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.profilesite.ProfileSiteConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PROFILE_SITES", schema = DB_SCHEMA)
@ContentTypeDescription(
    labelBundle = "org.librecms.profilesite.ProfileSiteItem",
    descriptionBundle = "org.librecms.profilesite.ProfileSiteItem"
)
@AuthoringKit(
    createComponent = ProfileSiteItemCreate.class,
    steps = {
        @AuthoringStep(
            component = ProfileSiteItemPropertiesStep.class,
            labelBundle = ProfileSiteConstants.BUNDLE,
            labelKey = "profile_site_item.basic_properties.label",
            descriptionBundle = ProfileSiteConstants.BUNDLE,
            descriptionKey = "profile_site_item.basic_properties.description",
            order = 1
        ),
        @AuthoringStep(
            component = ProfileSiteItemPositionStep.class,
            labelBundle = ProfileSiteConstants.BUNDLE,
            labelKey = "profile_site_item.position.label",
            descriptionBundle = ProfileSiteConstants.BUNDLE,
            descriptionKey = "profile_site_item.position.description",
            order = 2
        ),
        @AuthoringStep(
            component = ProfileSiteItemInterestsStep.class,
            labelBundle = ProfileSiteConstants.BUNDLE,
            labelKey = "profile_site_item.interests.label",
            descriptionBundle = ProfileSiteConstants.BUNDLE,
            descriptionKey = "profile_site_item.interests.description",
            order = 3
        ),
        @AuthoringStep(
            component = ProfileSiteItemMiscStep.class,
            labelBundle = ProfileSiteConstants.BUNDLE,
            labelKey = "profile_site_item.misc.label",
            descriptionBundle = ProfileSiteConstants.BUNDLE,
            descriptionKey = "profile_site_item.misc.description",
            order = 4
        )
    }
)
public class ProfileSiteItem extends ContentItem {

    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name = "OWNER_ID")
    private Person owner;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "PROFILE_SITE_ITEMS_POSITION",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "PROFILE_SITE_ITEM_ID")
            }
        )
    )
    private LocalizedString position;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "PROFILE_SITE_ITEMS_INTERESTS",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "PROFILE_SITE_ITEM_ID")
            }
        )
    )
    private LocalizedString interests;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "PROFILE_SITE_ITEMS_MISC",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "PROFILE_SITE_ITEM_ID")
            }
        )
    )
    private LocalizedString misc;

    public ProfileSiteItem() {
        position = new LocalizedString();
        interests = new LocalizedString();
        misc = new LocalizedString();
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(final Person owner) {
        this.owner = owner;
    }

    public LocalizedString getPosition() {
        return position;
    }

    protected void setPosition(final LocalizedString position) {
        this.position = position;
    }

    public LocalizedString getInterests() {
        return interests;
    }

    protected void setInterests(final LocalizedString interests) {
        this.interests = interests;
    }

    public LocalizedString getMisc() {
        return misc;
    }

    protected void setMisc(final LocalizedString misc) {
        this.misc = misc;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (owner != null) {
            hash = 37 * hash + Objects.hashCode(owner.getUuid());
        }
        hash = 37 * hash + Objects.hashCode(position);
        hash = 37 * hash + Objects.hashCode(interests);
        hash = 37 * hash + Objects.hashCode(misc);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProfileSiteItem)) {
            return false;
        }
        final ProfileSiteItem other = (ProfileSiteItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (owner != null
                && other.getOwner() != null
                && !Objects.equals(
                owner.getUuid(), other.getOwner().getUuid()
            )) {
            return false;
        }
        if (!Objects.equals(position, other.getPosition())) {
            return false;
        }
        if (!Objects.equals(interests, other.getInterests())) {
            return false;
        }
        return Objects.equals(misc, other.getMisc());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ProfileSiteItem;
    }

    @Override
    public String toString(final String data) {
        return super.toString(
            String.format(
                ", owner = %s, "
                    + "position = \"%s\", "
                    + "interests = \"%s\", "
                    + "misc = \"%s\"%s",
                Objects.toString(owner),
                Objects.toString(position),
                Objects.toString(interests),
                Objects.toString(misc),
                data
            )
        );
    }

}
