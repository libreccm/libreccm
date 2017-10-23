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
package org.librecms.pagemodel;

import org.librecms.contentsection.ContentItem;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FIXED_CONTENT_ITEM_COMPONENTS", schema = DB_SCHEMA)
public class FixedContentItemComponent extends ContentItemComponent {

    private static final long serialVersionUID = -4518031021801472455L;

    @OneToOne
    @JoinColumn(name = "CONTENT_ITEM_ID")
    private ContentItem contentItem;

    public ContentItem getContentItem() {
        return contentItem;
    }

    public void setContentItem(final ContentItem contentItem) {
        this.contentItem = contentItem;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 41 * hash + Objects.hashCode(contentItem);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof FixedContentItemComponent)) {
            return false;
        }
        final FixedContentItemComponent other = (FixedContentItemComponent) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(contentItem, other.getContentItem());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof FixedContentItemComponent;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", contentItem = %s%s",
                                            Objects.toString(contentItem),
                                            data));
    }

}
