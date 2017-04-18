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
 */package org.librecms.assets;

import com.arsdigita.cms.ui.assets.forms.FileAssetForm;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.Audited;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.*;

/**
 * An asset for making files available for download.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FILES", schema = DB_SCHEMA)
@Audited
@AssetType(assetForm = FileAssetForm.class,
           labelKey = "fileasset.label",
           labelBundle = ASSETS_BUNDLE,
           descriptionKey = "fileasset.description",
           descriptionBundle = ASSETS_BUNDLE)
public class FileAsset extends BinaryAsset implements Serializable {

    private static final long serialVersionUID = -8195062456502964401L;

    @Override
    public int hashCode() {
        return super.hashCode();
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

        if (!(obj instanceof FileAsset)) {
            return false;
        }
        final FileAsset other = (FileAsset) obj;
        return other.canEqual(this);
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof FileAsset;
    }
    
    
}
