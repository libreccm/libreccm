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

import org.librecms.assets.PostalAddress;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(PostalAddress.class)
public class PostalAddressFormController
    extends AbstractAssetFormController<PostalAddress> {

    protected static final String STATE = "state";
    protected static final String CITY = "city";
    protected static final String POSTAL_CODE = "postalCode";
    protected static final String ADDRESS = "address";

    @Override
    protected Map<String, Object> getAssetData(final PostalAddress asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = new HashMap<>();

        data.put(ADDRESS, asset.getAddress());
        data.put(POSTAL_CODE, asset.getPostalCode());
        data.put(CITY, asset.getCity());
        data.put(STATE, asset.getState());

        return data;
    }

    @Override
    public void updateAssetProperties(final PostalAddress asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        if (data.containsKey(ADDRESS)) {
            asset.setAddress((String) data.get(ADDRESS));
        }

        if (data.containsKey(POSTAL_CODE)) {
            asset.setPostalCode((String) data.get(POSTAL_CODE));
        }

        if (data.containsKey(CITY)) {
            asset.setCity((String) data.get(CITY));
        }

        if (data.containsKey(STATE)) {
            asset.setState((String) data.get(STATE));
        }
    }

}
