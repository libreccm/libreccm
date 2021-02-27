/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentTypeInfoModel {

    private String label;

    private String description;

    private String contentItemClass;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

}
