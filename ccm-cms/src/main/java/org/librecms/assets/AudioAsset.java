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

import com.arsdigita.cms.ui.assets.forms.AudioForm;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.ASSETS_BUNDLE;

/**
 * An asset for audio files, for example podcasts of music.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "AUDIO_ASSETS", schema = DB_SCHEMA)
@Audited
@AssetType(assetForm = AudioForm.class,
        labelKey = "audio_asset.label",
        labelBundle = ASSETS_BUNDLE,
        descriptionKey = "audio_asset.description",
        descriptionBundle = ASSETS_BUNDLE)
public class AudioAsset extends BinaryAsset implements Serializable {

    private static final long serialVersionUID = -2290028707028530325L;

    @OneToOne
    @JoinColumn(name = "LEGAL_METADATA_ID")
    private LegalMetadata legalMetadata;

    public LegalMetadata getLegalMetadata() {
        return legalMetadata;
    }

    public void setLegalMetadata(final LegalMetadata legalMetadata) {
        this.legalMetadata = legalMetadata;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(legalMetadata);
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
        
        if (!(obj instanceof AudioAsset)) {
            return false;
        }
        final AudioAsset other = (AudioAsset) obj;
        if (!other.canEqual(obj)) {
            return false;
        }
        
        return Objects.equals(legalMetadata, other.getLegalMetadata());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof AudioAsset;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", legalMetadata = %s%s",
                                            Objects.toString(legalMetadata),
                                            data));
    }

}
