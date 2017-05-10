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

import com.arsdigita.cms.ui.assets.forms.ExternalAudioAssetForm;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.*;

/**
 * An asset for an external audio file (an audio file which is
 * <strong>not</strong>
 * stored in the database of LibreCCM.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
@Entity
@Table(name = "EXTERNAL_AUDIO_ASSETS", schema = DB_SCHEMA)
@Audited
@AssetType(assetForm = ExternalAudioAssetForm.class,
           labelKey = "external_audio_asset.label",
           labelBundle = ASSETS_BUNDLE,
           descriptionKey = "external_audio_asset.description",
           descriptionBundle = ASSETS_BUNDLE)
public class ExternalAudioAsset extends Bookmark implements Serializable {

    private static final long serialVersionUID = 1190735204910197490L;

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
        hash = 23 * hash + Objects.hashCode(legalMetadata);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ExternalVideoAsset)) {
            return false;
        }
        final ExternalVideoAsset other = (ExternalVideoAsset) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(legalMetadata, other.getLegalMetadata());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ExternalVideoAsset;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", legalMetadata = %s%s",
                                            Objects.toString(legalMetadata),
                                            data));
    }

}
