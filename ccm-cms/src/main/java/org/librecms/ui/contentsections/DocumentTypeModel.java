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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Model for the details view of a document type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsDocumentTypeModel")
public class DocumentTypeModel {

    /**
     * The display name of the type.
     */
    private String displayName;

    /**
     * The class implementing the type.
     */
    private String contentItemClass;

    /**
     * The localized labels of the type.
     */
    private Map<String, String> labels;

    /**
     * The localized descriptions of the type.
     */
    private Map<String, String> descriptions;

    /**
     * The lifecycles of the type.
     */
    private List<DocumentTypeLifecycleModel> lifecycles;

    /**
     * The workflows of the type.
     */
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
