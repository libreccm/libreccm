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

import org.librecms.assets.AudioAsset;
import org.librecms.assets.ExternalAudioAsset;
import org.librecms.assets.ExternalVideoAsset;
import org.librecms.assets.Image;
import org.librecms.assets.VideoAsset;
import org.librecms.contentsection.AttachmentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsMediaStep")
public class MediaStepModel {

    private List<MediaListDto> mediaLists;

    private String mediaAssetPickerBaseUrl;

    private String sectionName;

    /**
     * Gets the {@link AttachmentList}s containing media assets of the current
     * content item and converts them to {@link MediaListDto}s to make data
     * about the lists available in the views.
     *
     * @return A list of the {@link AttachmentList} of the current content item.
     */
    public List<MediaListDto> getMediaLists() {
        return Collections.unmodifiableList(mediaLists);
    }

    public List<MediaListDto> getAttachmentsLists() {
        return Collections.unmodifiableList(mediaLists);
    }

    public void setAttachmentsLists(List<MediaListDto> mediaLists) {
        this.mediaLists = mediaLists;
    }

    protected void setMediaLists(
        final List<MediaListDto> attachmentLists
    ) {
        this.mediaLists = new ArrayList<>(attachmentLists);
    }

    public String getMediaAssetPickerBaseUrl() {
        return mediaAssetPickerBaseUrl;
    }

    public void setMediaAssetPickerBaseUrl(final String mediaAssetPickerBaseUrl) {
        this.mediaAssetPickerBaseUrl = mediaAssetPickerBaseUrl;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(final String sectionName) {
        this.sectionName = sectionName;
    }

    public String getAudioAssetType() {
        return AudioAsset.class.getName();
    }

    public String getExternalAudioAssetType() {
        return ExternalAudioAsset.class.getName();
    }

    public String getExternalVideoAssetType() {
        return ExternalVideoAsset.class.getName();
    }

    public String getImageType() {
        return Image.class.getName();
    }

    public String getVideoAssetType() {
        return VideoAsset.class.getName();
    }

}
