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

import org.librecms.contenttypes.Event;

import java.util.Locale;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemRenderer(renders = Event.class)
public class EventRenderer extends AbstractContentItemRenderer<Event> {

    @Override
    public void renderItem(final Event event,
                           final Locale language,
                           final Map<String, Object> result) {

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

}
