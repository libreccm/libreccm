/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.librecms.contentsection.ContentSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("ContentSectionModel")
public class ContentSectionModel {
    
    private ContentSection section;
    
    private List<FolderTreeNode> folders;
    
    protected void setSection(final ContentSection section) {
        this.section = Objects.requireNonNull(
            section, "Parameter section can't be null"
        );
    }
    
    public String getSectionName() {
        return Optional
            .ofNullable(section)
            .map(ContentSection::getLabel)
            .orElse("");
    }
    
    public List<FolderTreeNode> getFolders() {
        return Collections.unmodifiableList(folders);
    }
    
    protected void setFolders(final List<FolderTreeNode> folders) {
        this.folders = new ArrayList<>(folders);
    }
    
}
