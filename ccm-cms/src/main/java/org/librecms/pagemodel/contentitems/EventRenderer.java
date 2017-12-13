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
import org.librecms.contenttypes.Event;
import org.librecms.pagemodel.assets.AssetRenderers;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

/**
 * Renderer for {@link Event} items.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = Event.class)
public class EventRenderer extends AbstractContentItemRenderer {

    private static final long serialVersionUID = -3517404651544429745L;

    @Inject
    private AssetRenderers assetRenderers;
    
//    @Inject
//    public EventRenderer(final AssetRenderers assetRenderers) {
//        super(assetRenderers);
//    }

    /**
     * Render the provided {@link Event}. The following values are put into
     * {@code result}:
     *
     * <pre>
     *  {
     *      "text": {@link Event#getText()}
     *      "startDate": {@link Event#getStartDate()}
     *      "endDate": {@link Event#getEndDate()}
     *      "eventDate": {@link Event#getEventType()}
     *      "location": {@link Event#getLocation()}
     *      "mainContributor": {@link Event#getMainContributor()}
     *      "eventType": {@link Event#getEventType()}
     *      "mapLink": {@link Event#getMapLink()}
     *      "cost": {@link Event#getCost()}
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

        final Event event;
        if (item instanceof Event) {
            event = (Event) item;
        } else {
            return;
        }

        result.put("text", event.getText().getValue(language));
        result.put("startDate", event.getStartDate());
        result.put("endDate", event.getEndDate());
        result.put("eventDate", event.getEventDate().getValue(language));
        result.put("location", event.getLocation().getValue(language));
        result.put("mainContributor",
                   event.getMainContributor().getValue(language));
        result.put("eventType", event.getEventType().getValue(language));
        result.put("mapLink", event.getMapLink());
        result.put("cost", event.getCost().getValue(language));
    }

    @Override
    protected AssetRenderers getAssetRenderers() {
        return assetRenderers;
    }

    
    
}
