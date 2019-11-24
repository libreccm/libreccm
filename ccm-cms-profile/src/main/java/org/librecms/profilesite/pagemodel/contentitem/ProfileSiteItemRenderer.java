/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
