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

import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.PageModel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * Basic {@link PageModel} component for displaying a content item. This class
 * is not indented for direct use. The subclasses should be used instead.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_ITEM_COMPONENTS", schema = DB_SCHEMA)
public class ContentItemComponent extends ComponentModel {

    private static final long serialVersionUID = 4904530823926147281L;

    @Column(name = "MODE")
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 13 * hash + Objects.hashCode(mode);
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
        if (!(obj instanceof ContentItemComponent)) {
            return false;
        }
        final ContentItemComponent other = (ContentItemComponent) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(mode, other.getMode());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentItemComponent;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String
            .format(", mode = \"%s\"",
                    Objects.toString(mode),
                    data));
    }

}
