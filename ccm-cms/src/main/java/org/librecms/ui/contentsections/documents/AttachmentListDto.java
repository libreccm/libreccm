/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AttachmentListDto {
    
    private long listId;
    
    private String uuid;
    
    private String name;
    
    private long order;
    
    private String title;
    
    private String description;
    
    private List<ItemAttachmentDto> attachments;

    public long getListId() {
        return listId;
    }

    public void setListId(final long listId) {
        this.listId = listId;
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

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<ItemAttachmentDto> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public void setAttachments(final List<ItemAttachmentDto> attachments) {
        this.attachments = new ArrayList<>(attachments);
    }
    
    
    
}
