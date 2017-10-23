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
package org.librecms.pagemodel.assets;

import org.librecms.assets.SideNote;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * Renderer for {@link SideNote} assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@AssetRenderer(renders = SideNote.class)
public class SideNoteRenderer extends AbstractAssetRenderer {

    /**
     * Renderer to provided {@link SideNote}. The only property put into
     * {@code result} by this renderer is {@code text}:
     *
     * <pre>
     *  {
     *      "text": {@link SideNote#getText()}
     *  }
     * </pre>
     *
     * @param asset    The {@link SideNote} to render.
     * @param language The current language.
     * @param result   The into which the result is placed.
     */
    @Override
    protected void renderAsset(final Asset asset,
                               final Locale language,
                               final Map<String, Object> result) {

        final SideNote siteNote;
        if (asset instanceof SideNote) {
            siteNote = (SideNote) asset;
        } else {
            return;
        }

        result.put("text", siteNote.getText().getValue(language));
    }

}
