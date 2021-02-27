/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsDocumentTypeModel")
public class DocumentTypeModel {
    
    private String displayName;

    private String contentItemClass;

    private Map<String, String> labels;

    private Map<String, String> descriptions;

    private List<DocumentTypeLifecycleModel> lifecycles;

    private List<DocumentTypeWorkflowModel> workflows;

    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }
    
    public Map<String, String> getLabels() {
        return Collections.unmodifiableMap(labels);
    }
    
    public void setLabels(final Map<String, String> labels) {
        this.labels = new HashMap<>(labels);
    }
    
    public Map<String, String> getDescriptions() {
        return Collections.unmodifiableMap(descriptions);
    }
    
    public void setDescriptions(final Map<String, String> descriptions) {
        this.descriptions = new HashMap<>(descriptions);
    }
    
    public List<DocumentTypeLifecycleModel> getLifecycles() {
        return Collections.unmodifiableList(lifecycles);
    }
    
    public void setLifecycles(
        final List<DocumentTypeLifecycleModel> lifecyles
    ) {
        this.lifecycles = new ArrayList<>(lifecyles);
    }
    
    public List<DocumentTypeWorkflowModel> getWorkflows() {
        return Collections.unmodifiableList(workflows);
    }
    
    public void setWorkflows(final List<DocumentTypeWorkflowModel> workflows) {
        this.workflows = new ArrayList<>(workflows);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    

}
