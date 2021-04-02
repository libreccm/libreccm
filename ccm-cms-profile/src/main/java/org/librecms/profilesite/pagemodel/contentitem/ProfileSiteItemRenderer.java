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
package org.librecms.profilesite.pagemodel.contentitem;

import org.librecms.contentsection.ContentItem;
import org.librecms.pagemodel.assets.AssetRenderers;
import org.librecms.pagemodel.contentitems.AbstractContentItemRenderer;
import org.librecms.pagemodel.contentitems.ContentItemRenderer;
import org.librecms.profilesite.ProfileSiteItem;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = ProfileSiteItem.class)
@RequestScoped
public class ProfileSiteItemRenderer extends AbstractContentItemRenderer {

    private static final long serialVersionUID = 1L;
    
    
    @Inject
    private AssetRenderers assetRenderers;

    @Override
    protected void renderItem(
        final ContentItem item, 
        final Locale language,
        final Map<String, Object> result
    ) {
        // Nothing
//        final ProfileSiteItem profileSiteItem;
//        if (item instanceof ProfileSiteItem) {
//            profileSiteItem = (ProfileSiteItem) item;
//        } else {
//            return;
//        }
    }

    @Override
    protected AssetRenderers getAssetRenderers() {
        return assetRenderers;
    }

}
