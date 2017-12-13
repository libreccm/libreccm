/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.pagemodel.contentitems;

import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.Article;
import org.librecms.pagemodel.assets.AssetRenderers;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Renderer for {@link Article} items.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = Article.class)
@RequestScoped
public class ArticleRenderer extends AbstractContentItemRenderer {

    private static final long serialVersionUID = 8355183377902033759L;

    @Inject
    private AssetRenderers assetRenderers;
    
//    @Inject
//    public ArticleRenderer(final AssetRenderers assetRenderers) {
//        super(assetRenderers);
//    }

    /**
     * Render the provided {@link Article}. The following values are put into
     * the map:
     *
     * <pre>
     *  {
     *      "text": {@link Article#getText()}
     *  }
     * </pre>
     *
     * @param item     The item to render.
     * @param language The current language.
     * @param result   The map into which the result is placed.
     */
    @Override
    protected void renderItem(final ContentItem item,
                           final Locale language,
                           final Map<String, Object> result) {

        final Article article;
        if (item instanceof Article) {
            article = (Article) item;
        } else {
            return;
        }

        result.put("text", article.getText().getValue(language));
    }
    
    @Override
    public AssetRenderers getAssetRenderers() {
        return assetRenderers;
    }

}
