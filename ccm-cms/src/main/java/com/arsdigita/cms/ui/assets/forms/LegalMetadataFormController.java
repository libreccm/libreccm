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

import org.librecms.assets.LegalMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@IsControllerForAssetType(LegalMetadata.class)
public class LegalMetadataFormController
    extends AbstractAssetFormController<LegalMetadata> {

    protected static final String CONTRIBUTORS = "contributors";
    protected static final String CREATOR = "creator";
    protected static final String PUBLISHER = "publisher";
    protected static final String RIGHTS = "rights";
    protected static final String RIGHTS_HOLDER = "rightsHolder";

    @Override
    protected Map<String, Object> getAssetData(final LegalMetadata asset,
                                               final Locale selectedLocale) {

        final Map<String, Object> data = new HashMap<>();

        data.put(RIGHTS_HOLDER, asset.getRightsHolder());
        data.put(RIGHTS, asset.getRights().getValue(selectedLocale));
        data.put(PUBLISHER, asset.getPublisher());
        data.put(CREATOR, asset.getCreator());
        data.put(CONTRIBUTORS, asset.getContributors());

        return data;
    }

    @Override
    public void updateAssetProperties(final LegalMetadata asset,
                                      final Locale selectedLocale,
                                      final Map<String, Object> data) {

        if (data.containsKey(RIGHTS_HOLDER)) {
            asset.setRightsHolder((String) data.get(RIGHTS_HOLDER));
        }

        if (data.containsKey(RIGHTS)) {
            asset.getRights().addValue(selectedLocale,
                                       (String) data.get(RIGHTS));
        }
        
        if (data.containsKey(PUBLISHER)) {
            asset.setPublisher((String) data.get(PUBLISHER));
        }
        
        if (data.containsKey(CREATOR)) {
            asset.setCreator((String) data.get(CREATOR));
        }

        if (data.containsKey(CONTRIBUTORS)) {
            @SuppressWarnings("unchecked")
            final List<String> contributors = (List<String>) data
                .get(CONTRIBUTORS);
            asset.setContributors(contributors);
        }
    }

    @Override
    public LegalMetadata createAsset() {
        return new LegalMetadata();
    }

}
 