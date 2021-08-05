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
package org.librecms.ui.contentsections.documents.media;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MediaStepMediaOrder {

    private List<String> mediaListsOrder;

    private Map<String, List<String>> mediaOrder;

    private List<MovedMedia> movedMedia;

    public List<String> getMediaListsOrder() {
        return Collections.unmodifiableList(mediaListsOrder);
    }

    public void setMediaListsOrder(final List<String> mediaListsOrder) {
        this.mediaListsOrder = new ArrayList<>(mediaListsOrder);
    }

    public Map<String, List<String>> getMediaOrder() {
        return Collections.unmodifiableMap(mediaOrder);
    }

    public void setMediaOrder(
        final Map<String, List<String>> mediaOrder
    ) {
        this.mediaOrder = mediaOrder
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey(),
                    entry -> new ArrayList<>(entry.getValue())
                )
            );
    }

    public List<MovedMedia> getMovedMedia() {
        return Collections.unmodifiableList(movedMedia);
    }

    public void setMovedMedia(final List<MovedMedia> movedMedia) {
        this.movedMedia = new ArrayList<>(movedMedia);
    }

    @Override
    public String toString() {
        return String.format("mediaListsOrder = %s, "
                + "mediaOrder = %s, "
                + "movedMedia = %s",
            Objects.toString(mediaListsOrder),
            Objects.toString(mediaOrder),
            Objects.toString(movedMedia)
        );
    }

}
