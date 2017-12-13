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
import org.librecms.contenttypes.News;
import org.librecms.pagemodel.assets.AssetRenderers;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Renderer for {@link News} items.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = News.class)
@RequestScoped
public class NewsRenderer extends AbstractContentItemRenderer {

    private static final long serialVersionUID = -493301428054148505L;

    @Inject
    private AssetRenderers assetRenderers;

//    @Inject
//    public NewsRenderer(final AssetRenderers assetRenderers) {
//        super(assetRenderers);
//    }
    /**
     * Renders the provided {@link News} item. The following values are put into
     * {@code result}:
     *
     * <pre>
     *  {
     *      "text": {@link News#getText()}
     *      "releaseDate": {@link News#getReleaseDate()}
     *  }
     * </pre>
     *
     * @param item     The item to render.
     * @param language The current language.
     * @param result   The map into which the result is placed.
     */
    @Override
    public void renderItem(final ContentItem item,
                           final Locale language,
                           final Map<String, Object> result) {

        final News news;
        if (item instanceof News) {
            news = (News) item;
        } else {
            return;
        }

        result.put("text", news.getText().getValue(language));
        result.put("releaseDate", news.getReleaseDate());
    }

    @Override
    protected AssetRenderers getAssetRenderers() {
        return assetRenderers;
    }

}
