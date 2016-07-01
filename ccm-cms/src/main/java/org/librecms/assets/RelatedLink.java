/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.assets;

import org.hibernate.envers.Audited;
import org.librecms.contentsection.ContentItem;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * An link to information related to a content item, either to an (external) URL
 * provided by an existing or new bookmark or to another content item. Can be
 * used as an attachment only.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "RELATED_LINKS", schema = DB_SCHEMA)
@Audited
public class RelatedLink extends Asset implements Serializable {

    private static final long serialVersionUID = 6933875117588667160L;

    @OneToOne
    @JoinColumn(name = "TARGET_ITEM")
    private ContentItem targetItem;

    @OneToOne
    @JoinColumn(name = "BOOKMARK_ID")
    private Bookmark bookmark;

    public ContentItem getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(final ContentItem targetItem) {
        this.targetItem = targetItem;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(final Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 67 * hash + Objects.hashCode(targetItem);
        hash = 67 * hash + Objects.hashCode(bookmark);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RelatedLink other = (RelatedLink) obj;
        if (!Objects.equals(targetItem, other.targetItem)) {
            return false;
        }
        if (!Objects.equals(bookmark, other.bookmark)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof RelatedLink;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", targetItem = %s,"
                                                + "bookmark = %s%s",
                                            Objects.toString(targetItem),
                                            Objects.toString(bookmark),
                                            data));
    }

}
