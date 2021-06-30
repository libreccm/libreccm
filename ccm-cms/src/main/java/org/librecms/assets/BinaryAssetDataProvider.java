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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for providers of the data of a {@link BinaryAsset}. The
 * implementations are used by the {@link BinaryAssetDataService}.
 *
 * Implementations MUST NOT copy all binary data to the working memory. Instead
 * a streaming approch MUST be used to avoid out-of-memory problems with large
 * files.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface BinaryAssetDataProvider {

    /**
     * Copy the data of the {@link BinaryAsset} to the provided
     * {@link OutputStream}.
     *
     * @param asset        The asset providing the data.
     * @param outputStream The output stream to use.
     */
    void copyDataToOutputStream(
        BinaryAsset asset, OutputStream outputStream
    );

    /**
     * Saves binary data for the provided {@link BinaryAsset}. Implementations
     * must store the provided binary data and set the following fields:
     * <ul>
     * <li>{@link BinaryAsset#fileName }</li>
     * <li>{@link BinaryAsset#mimeType}</li>
     * <li>{@link BinaryAsset#size}</li>
     * </ul>
     *
     * The {@code size} has the be determined by the implementation of this
     * method.
     *
     * @param asset       The asset that will hold the data.
     * @param stream      The {@link InputStream} providing the data.
     * @param fileName    The file name of the data.
     * @param contentType The content type of the data.
     */
    void saveData(
        BinaryAsset asset,
        InputStream stream,
        String fileName,
        String contentType
    );

}
