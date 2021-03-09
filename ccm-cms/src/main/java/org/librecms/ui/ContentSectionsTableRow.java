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
package org.librecms.ui;

import org.libreccm.ui.admin.contentsections.ContentSectionTableRow;

import java.util.Comparator;
import java.util.Objects;

/**
 * Model for row in the table listing a available content sections.
 * 
 * @see CmsController#getContentSections()
 * @see ContentSectionsTableModel
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionsTableRow implements
    Comparable<ContentSectionsTableRow> {

    /**
     * The ID of the content section.
     */
    private long sectionId;

    /**
     * The label of the content section.
     */
    private String label;

    /**
     * Is the section empty and can be deleted?
     */
    private boolean deletable;

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(final long sectionId) {
        this.sectionId = sectionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Compares two {@link ContentSectionTableRow}s using the {@link #label} and
     * {@link #sectionId}.
     * 
     * @param other The other row
     * @return The result
     * 
     * @see Comparator#compare(java.lang.Object, java.lang.Object) 
     */
    @Override
    public int compareTo(final ContentSectionsTableRow other) {
        int result;
        result = Objects.compare(
            label, other.getLabel(), String::compareTo
        );

        if (result == 0) {
            result = Objects.compare(
                sectionId, other.getSectionId(), Long::compareTo
            );
        }

        return result;
    }

}
