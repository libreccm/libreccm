/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.pagemodel.styles;

import org.libreccm.core.CoreConstants;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "STYLE_MEDIA_QUERIES", schema = CoreConstants.DB_SCHEMA)
public class MediaQuery implements Serializable {

    private static final long serialVersionUID = 8047120379515301590L;

    @Id
    @Column(name = "MEDIA_QUERY_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long mediaQueryId;

    @Column(name = "MEDIA_TYPE")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value",
                           column = @Column(name = "MIN_WIDTH_VALUE"))
        ,
        @AttributeOverride(name = "unit",
                           column = @Column(name = "MIN_WIDTH_UNIT"))
    })
    private Dimension minWidth;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "value",
                           column = @Column(name = "MAX_WIDTH_VALUE"))
        ,
        @AttributeOverride(name = "unit",
                           column = @Column(name = "MAX_WIDTH_UNIT"))
    })
    private Dimension maxWidth;

    public long getMediaQueryId() {
        return mediaQueryId;
    }

    protected void setMediaQueryId(long mediaQueryId) {
        this.mediaQueryId = mediaQueryId;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Dimension getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(final Dimension minWidth) {
        this.minWidth = minWidth;
    }

    public Dimension getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(final Dimension maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) (mediaQueryId ^ (mediaQueryId >>> 32));
        hash = 31 * hash + Objects.hashCode(mediaType);
        hash = 31 * hash + Objects.hashCode(minWidth);
        hash = 31 * hash + Objects.hashCode(maxWidth);
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
        if (!(obj instanceof MediaQuery)) {
            return false;
        }
        final MediaQuery other = (MediaQuery) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (mediaQueryId != other.getMediaQueryId()) {
            return false;
        }
        if (mediaType != other.getMediaType()) {
            return false;
        }
        if (!Objects.equals(minWidth, other.getMinWidth())) {
            return false;
        }
        return Objects.equals(maxWidth, other.getMaxWidth());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof MediaQuery;
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "mediaQueryId = %d, "
                                 + "mediaType = \"%s\", "
                                 + "minWidth = %s, "
                                 + "maxWidth = %s%s"
                                 + " }",
                             super.toString(),
                             mediaQueryId,
                             Objects.toString(mediaType),
                             Objects.toString(minWidth),
                             Objects.toString(maxWidth),
                             data);
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toCss() {
        
        final StringBuilder builder = new StringBuilder("@media");
        
        if (mediaType != null) {
            builder.append(" ").append(mediaType.toString()).append(" ");
        }
        
        if (minWidth != null) {
            if (builder.length() > "@media".length()) {
                builder.append(" and ");
            }
            
            builder.append(String.format("(min-width: %s", minWidth.toCss()));
        }
        
        if (maxWidth != null) {
            if (builder.length() > "@media".length()) {
                builder.append(" and ");
            }
            
            builder.append(String.format("(max-width: %s", maxWidth.toCss()));
        }
        
        
        return builder.toString();
    }
}
