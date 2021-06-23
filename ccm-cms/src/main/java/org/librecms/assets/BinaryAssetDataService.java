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

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.UnexpectedErrorException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class BinaryAssetDataService {

    @Inject
    private ConfigurationManager confManager;

    @Inject
    @Any
    private Instance<BinaryAssetDataProvider> dataProvider;

    public void copyDataToOutputStream(
        final BinaryAsset asset, final OutputStream outputStream
    ) {
        Objects.requireNonNull(asset, "Can't retrieve data from null.");
        Objects.requireNonNull(outputStream, "Can't copy data to null.");
        getDataProvider().copyDataToOutputStream(asset, outputStream);
    }

    public void saveData(final BinaryAsset asset, final InputStream stream) {
        Objects.requireNonNull(asset, "Can't save data to null.");

        final BinaryAssetDataProvider dataProvider = getDataProvider();
        dataProvider.saveData(asset, stream);
    }

    @SuppressWarnings("unchecked")
    private BinaryAssetDataProvider getDataProvider() {
        final BinaryAssetConfig config = confManager.findConfiguration(
            BinaryAssetConfig.class
        );

        final Class<? extends BinaryAssetDataProvider> clazz;
        try {
            clazz = (Class<? extends BinaryAssetDataProvider>) Class.forName(
                config.getBinaryAssetDataProvider()
            );
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }

        final Instance<? extends BinaryAssetDataProvider> selectedInstance
            = dataProvider.select(clazz);

        if (selectedInstance.isResolvable()) {
            return selectedInstance.get();
        } else {
            throw new UnexpectedErrorException(
                String.format(
                    "The configured implementation of %s could not be resolved.",
                    BinaryAssetDataProvider.class.getName()
                )
            );
        }
    }

}
