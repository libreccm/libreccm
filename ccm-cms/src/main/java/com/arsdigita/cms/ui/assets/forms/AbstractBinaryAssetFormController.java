/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.cms.ui.assets.AbstractAssetFormController;

import org.librecms.assets.BinaryAsset;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.activation.MimeType;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractBinaryAssetFormController<T extends BinaryAsset>
    extends AbstractAssetFormController<T> {

    protected static final String DESCRIPTION = "description";

    protected static final String FILE_NAME = "fileName";

    protected static final String MIME_TYPE = "mimeType";

    protected static final String DATA = "data";

    protected static final String SIZE = "size";

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected Map<String, Object> getAssetData(final T asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = new HashMap<>();

        final String description = asset
            .getDescription()
            .getValue(selectedLocale);

        data.put(DESCRIPTION, description);
        data.put(FILE_NAME, asset.getFileName());
        data.put(MIME_TYPE, asset.getMimeType());
        data.put(DATA, asset.getData());
        data.put(SIZE, asset.getSize());

        return data;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateAssetProperties(final T asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        if (data.containsKey(DESCRIPTION)) {
            asset.getDescription().addValue(selectedLocale,
                                            (String) data.get(DESCRIPTION));
        }

        if (data.containsKey(FILE_NAME)) {
            asset.setFileName((String) data.get(FILE_NAME));
        }

        if (data.containsKey(MIME_TYPE)) {
            asset.setMimeType((MimeType) data.get(MIME_TYPE));
        }

        if (data.containsKey(DATA)) {
            //asset.setData((byte[]) data.get(DATA));
        }

        if (data.containsKey(SIZE)) {
            asset.setSize((long) data.get(SIZE));
        }
    }

}
