/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.ContentSection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Models;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class DocumentUi {

    @Inject
    private Models models;

    public String showDocumentNotFound(
        final ContentSection section, final String documentPath
    ) {
        models.put("section", section.getLabel());
        models.put("documentPath", documentPath);
        return "org/librecms/ui/contentsection/documents/document-not-found.xhtml";
    }

   

}
