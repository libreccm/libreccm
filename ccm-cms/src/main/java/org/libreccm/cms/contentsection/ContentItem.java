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
package org.libreccm.cms.contentsection;

import org.hibernate.envers.Audited;

import static org.libreccm.cms.CmsConstants.*;

import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Base type for all content item types. Specifies some common properties.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "content_items", schema = DB_SCHEMA)
public class ContentItem extends CcmObject implements Serializable {

    private static final long serialVersionUID = 5897287630227129653L;

    /**
     * The name of the content item which is used to generate the URL of the
     * content item. We are using a {@link LocalizedString} here to make it
     * possible to generate localised URLs. Therefore only characters allowed in
     * URLs should be used here.
     */
    @Embedded
    @AssociationOverride(
            name = "VALUES",
            joinTable = @JoinTable(name = "CONTENT_ITEM_NAMES",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "OBJECT_ID")}
            ))
    private LocalizedString name;

    /**
     * The content type associated with the content item.
     */
    @OneToOne
    private ContentType contentType;

    /**
     * The human readable title of the content item.
     */
    @Embedded
    @AssociationOverride(
            name = "VALUES",
            joinTable = @JoinTable(name = "CONTENT_ITEM_TITLES",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "OBJECT_ID")}
            ))
    private LocalizedString title;

    /**
     * A short text which describes the content of the content item.
     */
    @Embedded
    @AssociationOverride(
            name = "VALUES",
            joinTable = @JoinTable(name = "CONTENT_ITEM_DESCRIPTIONS",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "OBJECT_ID")}
            ))
    private LocalizedString description;

    /**
     * The version/publishing state of the content item.
     */
    @Enumerated(EnumType.STRING)
    private ContentItemVersion version;

    /**
     * The launch date of the content item (date when the item is made public)
     */
    @Temporal(TemporalType.DATE)
    private Date launchDate;

    /**
     * String with the IDs (separated by slashes) of the ancestors of the
     * content item (aka the path of the content item).
     */
    @Column(name = "ancestors", length = 1024)
    private String ancestors;

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public ContentItemVersion getVersion() {
        return version;
    }

    public void setVersion(final ContentItemVersion version) {
        this.version = version;
    }

    public Date getLaunchDate() {
        return new Date(launchDate.getTime());
    }

    public void setLaunchDate(final Date launchDate) {
        this.launchDate = new Date(launchDate.getTime());
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(final String ancestors) {
        this.ancestors = ancestors;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(name);
        hash = 59 * hash + Objects.hashCode(contentType);
        hash = 59 * hash + Objects.hashCode(title);
        hash = 59 * hash + Objects.hashCode(description);
        hash = 59 * hash + Objects.hashCode(version);
        hash = 59 * hash + Objects.hashCode(launchDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ContentItem)) {
            return false;
        }

        final ContentItem other = (ContentItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(contentType, other.getContentType())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (version != other.getVersion()) {
            return false;
        }
        if (!Objects.equals(launchDate, other.getLaunchDate())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentItem;
    }

    @Override
    public String toString(final String data) {
        return String.format(", name = {}, "
                                     + "contentType = {}, "
                                     + "title = {}, "
                                     + "version = %s,"
                                     + "launchDate = %s%s",
                             Objects.toString(name),
                             Objects.toString(contentType),
                             Objects.toString(description),
                             Objects.toString(version),
                             Objects.toString(launchDate));
    }

}
