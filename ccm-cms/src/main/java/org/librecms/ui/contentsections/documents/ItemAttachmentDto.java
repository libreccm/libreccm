/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemAttachmentDto {

    private long attachmentId;

    private String uuid;

    private long sortKey;

    private String assetType;

    private String title;
    
    private boolean internalLink;

    public long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(final long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public boolean isInternalLink() {
        return internalLink;
    }

    public void setInternalLink(final boolean internalLink) {
        this.internalLink = internalLink;
    }
    
    

}
