/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * A pseudo implemention of the {@link MvcAuthoringStep} interface used by the
 * {@link DocumentController} to show an error message when the authoring step
 * does not support the requested document.
 *
 * Most of methods in this implementation are throwing an
 * {@link UnsupportedOperationException}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UnsupportedDocumentType implements MvcAuthoringStep {

    @Override
    public Class<? extends ContentItem> supportedDocumentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getBundle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContentSection(ContentSection section) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContentSection getContentSection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContentItem(ContentItem document) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ContentItem getContentItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String showStep() {
        return "org/librecms/ui/contentsection/documents/unsupported-documenttype.xhtml";
    }

    @GET
    @Path("/documentPath:(.+)?")
    public String showForAllGets() {
        return showStep();
    }

    @POST
    @Path("/")
    public String showForPost() {
        return showStep();
    }

    @POST
    @Path("/documentPath:(.+)?")
    public String showForAllPosts() {
        return showStep();
    }

    @Override
    public String getContentSectionLabel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentSectionTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentItemPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
