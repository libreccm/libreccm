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
package org.librecms.ui.contentsections;

import org.librecms.contentsection.ContentSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model for the list view of document types/{@link ContentTypes} of a
 * {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsDocumentTypesModel")
public class DocumentTypesModel {

    /**
     * The content types assigned to the content section.
     */
    private List<DocumentTypesTableRowModel> assignedTypes;

    /**
     * The available types thare are not assigned to the content section.
     */
    private Map<String, DocumentTypeInfoModel> availableTypes;

    /**
     * The available lifecycles definitions.
     */
    private Map<String, String> availableLifecycles;

    /**
     * The available workflow templates.
     */
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
