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
package org.libreccm.ui.admin.contentsections;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionTableRow implements
    Comparable<ContentSectionTableRow> {

    private long sectionId;

    private String uuid;

    private String label;

    public long getSectionId() {
        return sectionId;
    }

    protected void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getLabel() {
        return label;
    }

    protected void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public int compareTo(final ContentSectionTableRow other) {
        return Comparator
            .nullsFirst(
                Comparator
                    .comparing(ContentSectionTableRow::getLabel)
                    .thenComparing(ContentSectionTableRow::getSectionId)
            ).compare(this, other);
    }

}
