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
import com.arsdigita.cms.ui.assets.IsControllerForAssetType;

import org.librecms.assets.SideNote;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(SideNote.class)
public class SideNoteFormController
    extends AbstractAssetFormController<SideNote> {

    protected static final String TEXT = "text";

    @Override
    protected Map<String, Object> getAssetData(final SideNote asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = new HashMap<>();

        data.put(TEXT, asset.getText().getValue(selectedLocale));

        return data;
    }

    @Override
    public void updateAssetProperties(final SideNote asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        if (data.containsKey(TEXT)) {

            final String value = (String) data.get(TEXT);
            asset.getText().addValue(selectedLocale, value);
        }

    }

}
