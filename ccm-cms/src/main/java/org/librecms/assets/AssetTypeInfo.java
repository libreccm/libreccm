/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import com.arsdigita.cms.ui.assets.AbstractAssetForm;

import org.librecms.contentsection.Asset;

import java.util.Objects;

/**
 * A easy to use encapsulation for the informations about an asset type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetTypeInfo {

    /**
     * The bundle which provides the localised label of the asset type.
     */
    private String labelBundle;

    /**
     * The key of the label in the {@link #labelBundle}.
     */
    private String labelKey;

    /**
     * The bundle which provides the localised description of the asset type.
     */
    private String descriptionBundle;

    /**
     * The key of the description in the {@link #descriptionBundle}.
     */
    private String descriptionKey;

    /**
     * The class of the asset type.
     */
    private Class<? extends Asset> assetClass;

    /**
     * The form for editing and creating asset of the type described.
     */
    private Class<? extends AbstractAssetForm> assetForm;

    public String getLabelBundle() {
        return labelBundle;
    }

    public void setLabelBundle(final String labelBundle) {
        this.labelBundle = labelBundle;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(final String labelKey) {
        this.labelKey = labelKey;
    }

    public String getDescriptionBundle() {
        return descriptionBundle;
    }

    public void setDescriptionBundle(final String descriptionBundle) {
        this.descriptionBundle = descriptionBundle;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(final String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public Class<? extends Asset> getAssetClass() {
        return assetClass;
    }

    public void setAssetClass(final Class<? extends Asset> assetClass) {
        this.assetClass = assetClass;
    }

    public Class<? extends AbstractAssetForm> getAssetForm() {
        return assetForm;
    }

    public void setAssetForm(final Class<? extends AbstractAssetForm> assetForm) {
        this.assetForm = assetForm;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(labelBundle);
        hash = 59 * hash + Objects.hashCode(labelKey);
        hash = 59 * hash + Objects.hashCode(descriptionBundle);
        hash = 59 * hash + Objects.hashCode(descriptionKey);
        hash = 59 * hash + Objects.hashCode(assetClass);
        hash = 59 * hash + Objects.hashCode(assetForm);
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
        if (!(obj instanceof AssetTypeInfo)) {
            return false;
        }
        final AssetTypeInfo other = (AssetTypeInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(labelBundle, other.getLabelBundle())) {
            return false;
        }
        if (!Objects.equals(labelKey, other.getLabelKey())) {
            return false;
        }
        if (!Objects.equals(descriptionBundle, other.getDescriptionBundle())) {
            return false;
        }
        if (!Objects.equals(descriptionKey, other.getDescriptionKey())) {
            return false;
        }
        if (!Objects.equals(assetClass, other.getAssetClass())) {
            return false;
        }
        return Objects.equals(assetForm, other.getAssetForm());
    }
    
    private boolean canEqual(final Object obj) {
        return obj instanceof AssetTypeInfo;
    }
    
    @Override
    public final String toString() {
        return toString("");
    }
    
    public String toString(final String data) {
        return String.format("%s{ "
            + "labelBundle = \"%s\", "
            + "labelKey = \"%s\", "
            + "descriptionBundle = \"%s\", "
            + "descriptionKey = \"%s\", "
            + "assetClass = \"%s\", "
            + "assetForm = \"%s\"%s "
            + " }",
                             super.toString(),
                             labelBundle,
                             labelKey,
                             descriptionBundle,
                             descriptionKey,
                             Objects.toString(assetClass),
                             Objects.toString(assetForm),
                             data);
    }

}
