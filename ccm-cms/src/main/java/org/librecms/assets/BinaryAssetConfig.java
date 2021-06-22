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
package org.librecms.assets;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 * Configuration parameters for binary assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class BinaryAssetConfig {

    /**
     * Sets the implementation of {@link BinaryAssetDataProvider} to use.
     */
    @Setting
    private String binaryAssetDataProvider = BinaryAssetBlobDataProvider.class
        .getName();

    public String getBinaryAssetDataProvider() {
        return binaryAssetDataProvider;
    }

    public void setBinaryAssetDataProvider(final String binaryAssetDataProvider) {
        this.binaryAssetDataProvider = binaryAssetDataProvider;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(binaryAssetDataProvider);
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
        if (!(obj instanceof BinaryAssetConfig)) {
            return false;
        }
        final BinaryAssetConfig other = (BinaryAssetConfig) obj;
        return Objects.equals(
            binaryAssetDataProvider,
            other.binaryAssetDataProvider
        );
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "binaryAssetDataProvider = %s"
                + " }",
            binaryAssetDataProvider
        );
    }

}
