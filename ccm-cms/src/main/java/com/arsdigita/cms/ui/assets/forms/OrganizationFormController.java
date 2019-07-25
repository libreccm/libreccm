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

import com.arsdigita.cms.ui.assets.IsControllerForAssetType;

import org.librecms.assets.Organization;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(Organization.class)
public class OrganizationFormController
    extends AbstractContactableEntityFormController<Organization> {

    protected static final String ORGANIZATION_NAME = "organizationName";

    @Override
    protected Map<String, Object> getAssetData(final Organization asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = super.getAssetData(asset,
                                                            selectedLocale);
        data.put(ORGANIZATION_NAME, asset.getName());

        return data;
    }

    @Override
    public void updateAssetProperties(final Organization asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        super.updateAssetProperties(asset, selectedLocale, data);

        if (data.containsKey(ORGANIZATION_NAME)) {
            final String organizationName = (String) data.get(ORGANIZATION_NAME);
            asset.setName(organizationName);
        }
    }

    @Override
    public Organization createAsset() {
        return new Organization();
    }

}
