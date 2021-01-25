/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.librecms.contentsection.ContentSection;

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
    
}
