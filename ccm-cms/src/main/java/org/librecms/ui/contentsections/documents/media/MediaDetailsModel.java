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

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsMediaDetailsModel")
public class MediaDetailsModel {

    private String baseUrl;

    /**
     * The identifier of the {@link AttachmentList} of the link.
     */
    private String listIdentifier;

    /**
     * The UUID of the link.
     */
    private String uuid;

    private String name;

    private String sectionName;

    private String mediaType;
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getListIdentifier() {
        return listIdentifier;
    }

    public void setListIdentifier(final String listIdentifier) {
        this.listIdentifier = listIdentifier;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(final String sectionName) {
        this.sectionName = sectionName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }
    
    

}
