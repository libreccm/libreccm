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
package org.librecms.assets;

import com.arsdigita.cms.ui.assets.forms.LegalMetadataForm;

import org.librecms.contentsection.Asset;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.*;

/**
 * Container for storing legal metadata about a resource (a content item or an
 * other asset).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@AssetType(assetForm = LegalMetadataForm.class,
           labelKey = "legal_metadata.label",
           labelBundle = ASSETS_BUNDLE,
           descriptionKey = "legal_metadata.description",
           descriptionBundle = ASSETS_BUNDLE)
@Entity
@Table(name = "LEGAL_METADATA", schema = DB_SCHEMA)
@Audited
public class LegalMetadata extends Asset implements Serializable {

    private static final long serialVersionUID = -5766376031105842907L;

    /**
     * The person or organisation which holds the rights for the resource.
     */
    @Column(name = "RIGHTS_HOLDER", length = 512)
    private String rightsHolder;

    /**
     * Rights granted for the resource to us.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "LEGAL_METADATA_RIGHTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "ASSET_ID")
                               }
        )
    )
    private LocalizedString rights;

    @Column(name = "PUBLISHER")
    private String publisher;

    @Column(name = "CREATOR")
    private String creator;

    @ElementCollection
    @CollectionTable(name = "LEGAL_METADATA_CONTRIBUTORS",
                     schema = DB_SCHEMA,
                     joinColumns = {
                         @JoinColumn(name = "LEGAL_METADATA_ID")
                     })
    @Column(name = "CONTRIBUTORS")
    private List<String> contributors;

    public LegalMetadata() {
        super();
        contributors = new ArrayList<>();
    }

    public String getRightsHolder() {
        return rightsHolder;
    }

    public void setRightsHolder(final String rightsHolder) {
        this.rightsHolder = rightsHolder;
    }

    public LocalizedString getRights() {
        return rights;
    }

    public void setRights(final LocalizedString rights) {
        this.rights = rights;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public List<String> getContributors() {
        return Collections.unmodifiableList(contributors);
    }

    public void setContributors(final List<String> contributors) {
        this.contributors = new ArrayList<>(contributors);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 17 * hash + Objects.hashCode(rightsHolder);
        hash = 17 * hash + Objects.hashCode(rights);
        hash = 17 * hash + Objects.hashCode(publisher);
        hash = 17 * hash + Objects.hashCode(creator);
        hash = 17 * hash + Objects.hashCode(contributors);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (obj instanceof LegalMetadata) {
            return false;
        }
        final LegalMetadata other = (LegalMetadata) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(rightsHolder, other.getRightsHolder())) {
            return false;
        }
        if (!Objects.equals(publisher, other.getPublisher())) {
            return false;
        }
        if (!Objects.equals(creator, other.getCreator())) {
            return false;
        }
        if (!Objects.equals(rights, other.getRights())) {
            return false;
        }
        return Objects.equals(contributors, other.getContributors());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof LegalMetadata;
    }

    @Override
    public String toString(final String data) {
        final String contributorsStr;
        if (contributors == null) {
            contributorsStr = "";
        } else {
            contributorsStr = String.join(", ", contributors);
        }

        return super.toString(String.format(", rightsHolder = \"%s\", "
                                                + "rights = %s, "
                                                + "publisher = \"%s\", "
                                                + "creator = \"%s\", "
                                                + "contributors = {  }%s",
                                            rightsHolder,
                                            Objects.toString(rights),
                                            publisher,
                                            creator,
                                            contributorsStr,
                                            data));
    }

}
