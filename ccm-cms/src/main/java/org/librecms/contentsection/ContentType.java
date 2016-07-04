/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.librecms.contentsection;

import static org.librecms.CmsConstants.*;

import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_TYPES", schema = DB_SCHEMA)
public class ContentType extends CcmObject implements Serializable {

    private static final long serialVersionUID = -2708659750560382851L;

    @Column(name = "CONTENT_ITEM_CLASS", length = 1024)
    private String contentItemClass;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_TYPE_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString label;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_TYPE_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString description;

    @Column(name = "ANCESTORS", length = 1024)
    private String ancestors;

    @Column(name = "DESCENDANTS", length = 1024)
    private String descendants;

    @Enumerated(EnumType.STRING)
    private ContentItemMode mode;

    //ToDo references for authoring kit etc

    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(final String ancestors) {
        this.ancestors = ancestors;
    }

    public String getDescendants() {
        return descendants;
    }

    public void setDescendants(final String descendants) {
        this.descendants = descendants;
    }

    public ContentItemMode getMode() {
        return mode;
    }

    public void setMode(final ContentItemMode mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(contentItemClass);
        hash = 79 * hash + Objects.hashCode(label);
        hash = 79 * hash + Objects.hashCode(description);
        hash = 79 * hash + Objects.hashCode(mode);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ContentType)) {
            return false;
        }

        final ContentType other = (ContentType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(contentItemClass, other.getContentItemClass())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return mode == other.getMode();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentType;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", contentItemClass = \"%s\", "
                                                + "label = {%s}, "
                                                + "mode = %s%s",
                                            contentItemClass,
                                            Objects.toString(label),
                                            mode,
                                            data));
    }

}
