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
package org.librecms.contenttypes;

import org.librecms.contentsection.ContentItem;

import java.util.Objects;

/**
 * A class encapsulating all informations about a available Content Type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentTypeInfo {

    /**
     * The bundle which provides the localisable label for the content type.
     */
    private String labelBundle;
    /**
     * The key of the label in the {@link #labelBundle}.
     */
    private String labelKey;
    /**
     * The bundle which provides the description of the content type.
     */
    private String descriptionBundle;
    /**
     * The key of the description of the content type in the
     * {@link #descriptionBundle}.
     */
    private String descriptionKey;

    private Class<? extends ContentItem> contentItemClass;
    private AuthoringKitInfo authoringKit;

    public String getLabelBundle() {
        return labelBundle;
    }

    public void setLabelBundle(final String labelBundle) {
        this.labelBundle = labelBundle;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(final String labelKey) {
        this.labelKey = labelKey;
    }

    public String getDescriptionBundle() {
        return descriptionBundle;
    }

    public void setDescriptionBundle(final String descriptionBundle) {
        this.descriptionBundle = descriptionBundle;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(final String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public Class<? extends ContentItem> getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(
        final Class<? extends ContentItem> contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

    public AuthoringKitInfo getAuthoringKit() {
        return authoringKit;
    }

    public void setAuthoringKit(final AuthoringKitInfo authoringKit) {
        this.authoringKit = authoringKit;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(labelBundle);
        hash = 97 * hash + Objects.hashCode(labelKey);
        hash = 97 * hash + Objects.hashCode(descriptionBundle);
        hash = 97 * hash + Objects.hashCode(descriptionKey);
        hash = 97 * hash + Objects.hashCode(contentItemClass);
        hash = 97 * hash + Objects.hashCode(authoringKit);
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
        if (!(obj instanceof ContentTypeInfo)) {
            return false;
        }
        final ContentTypeInfo other = (ContentTypeInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(labelBundle, other.getLabelBundle())) {
            return false;
        }
        if (!Objects.equals(labelKey, other.getLabelKey())) {
            return false;
        }
        if (!Objects.equals(descriptionBundle, other.getDescriptionBundle())) {
            return false;
        }
        if (!Objects.equals(descriptionKey, other.getDescriptionKey())) {
            return false;
        }

        if (!Objects.equals(contentItemClass, other.getContentItemClass())) {
            return false;
        }

        return Objects.equals(authoringKit, other.getAuthoringKit());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ContentTypeInfo;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "labelBundle = \"%s\", "
                                 + "labelKey = \"%s\", "
                                 + "descriptionBundle = \"%s\", "
                                 + "descriptionKey = \"%s\","
                                 + "contentItemClass = \"%s\", "
                                 + "authoringKit = { %s }%s"
                                 + " }",
                             super.toString(),
                             labelBundle,
                             labelKey,
                             descriptionBundle,
                             descriptionKey,
                             Objects.toString(contentItemClass),
                             Objects.toString(authoringKit),
                             data);
    }

}
