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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import com.arsdigita.cms.ui.assets.forms.ImageForm;

import org.hibernate.envers.Audited;
import org.librecms.ui.contentsections.assets.ImageCreateStep;
import org.librecms.ui.contentsections.assets.ImageEditStep;
import org.librecms.ui.contentsections.assets.MvcAssetEditKit;

import javax.persistence.OneToOne;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.*;

/**
 * An asset for images (in a format which can be displayed by a browser, like 
 * PNG, JPEG or GIF).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "IMAGES", schema = DB_SCHEMA)
@Audited
@AssetType(assetForm = ImageForm.class,
           labelKey = "image.label",
           labelBundle = ASSETS_BUNDLE,
           descriptionKey = "image.description",
           descriptionBundle = ASSETS_BUNDLE)
@MvcAssetEditKit(
    createStep = ImageCreateStep.class,
    editStep = ImageEditStep.class
)
public class Image extends BinaryAsset implements Serializable {

    private static final long serialVersionUID = -8095106228017573785L;

    @Column(name = "WIDTH")
    private long width;

    @Column(name = "HEIGHT")
    private long height;

    @OneToOne
    @JoinColumn(name = "LEGAL_METADATA_ID")
    private LegalMetadata legalMetadata;

    public Image() {
        super();
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(final long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(final long height) {
        this.height = height;
    }

    public LegalMetadata getLegalMetadata() {
        return legalMetadata;
    }

    public void setLegalMetadata(final LegalMetadata legalMetadata) {
        this.legalMetadata = legalMetadata;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 89 * hash + (int) (width ^ (width >>> 32));
        hash = 89 * hash + (int) (height ^ (height >>> 32));
        hash = 89 * hash + Objects.hashCode(legalMetadata);
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

        if (!(obj instanceof Image)) {
            return false;
        }
        final Image other = (Image) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (width != other.getWidth()) {
            return false;
        }
        if (height != other.getHeight()) {
            return false;
        }
        return Objects.equals(legalMetadata, other.getLegalMetadata());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Image;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", width = %d, "
                                                + "height = %d, "
                                                + "legalMetadata = %s%s",
                                            width,
                                            height,
                                            Objects.toString(legalMetadata),
                                            data));
    }

}
