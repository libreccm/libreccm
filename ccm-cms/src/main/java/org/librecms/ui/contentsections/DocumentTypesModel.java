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
@Named("CmsDocumentTypesModel")
public class DocumentTypesModel {

    private List<DocumentTypesTableRowModel> assignedTypes;

    private Map<String, DocumentTypeInfoModel> availableTypes;

    private Map<String, String> availableLifecycles;

    private Map<String, String> availableWorkflows;

    public List<DocumentTypesTableRowModel> getAssignedTypes() {
        return Collections.unmodifiableList(assignedTypes);
    }

    protected void setAssignedTypes(
        final List<DocumentTypesTableRowModel> assignedTypes
    ) {
        this.assignedTypes = new ArrayList<>(assignedTypes);
    }

    public Map<String, DocumentTypeInfoModel> getAvailableTypes() {
        return Collections.unmodifiableMap(availableTypes);
    }

    public void setAvailableTypes(
        final Map<String, DocumentTypeInfoModel> availableTypes
    ) {
        this.availableTypes = new HashMap<>(availableTypes);
    }

    public Map<String, String> getAvailableLifecycles() {
        return Collections.unmodifiableMap(availableLifecycles);
    }

    public void setAvailableLifecycles(
        final Map<String, String> availableLifecycles
    ) {
        this.availableLifecycles = new HashMap<>(availableLifecycles);
    }

    public Map<String, String> getAvailableWorkflows() {
        return Collections.unmodifiableMap(availableWorkflows);
    }

    public void setAvailableWorkflows(
        final Map<String, String> availableWorkflows
    ) {
        this.availableWorkflows = new HashMap<>(availableWorkflows);
    }

}
