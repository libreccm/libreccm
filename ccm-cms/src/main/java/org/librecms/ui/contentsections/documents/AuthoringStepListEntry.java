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
package org.librecms.ui.contentsections.documents;

/**
 * A DTO used in several views to display information about an authoring step.
 * 
 * @see SelectedDocumentModel
 * @see SelectedDocumentModel#getAuthoringStepsList() 
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AuthoringStepListEntry {

    /**
     * The label of the authoring step.
     */
    private String label;

    /**
     * The description of the authoring step.
     */
    private String description;

    /**
     * The path fragment of the authoring step.
     */
    private String pathFragment;

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

    public String getPathFragment() {
        return pathFragment;
    }

    public void setPathFragment(final String pathFragment) {
        this.pathFragment = pathFragment;
    }

}
