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

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.lifecycle.LifecycleDefinition;

/**
 * Model for the lifecyles of a {@link ContentType} (document type).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DocumentTypeLifecycleModel {

    /**
     * The ID of the lifecycle definition.
     */
    private long definitionId;

    /**
     * The UUID of the lifecycle definition.
     */
    private String uuid;

    /**
     * The label of the lifecycle definition.This value is determined from
     * {@link LifecycleDefinition#label} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String label;

    /**
     * The description of the lifecycle definition.This value is determined from
     * {@link LifecycleDefinition#description} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String description;

    /**
     * Is the lifecycle the default lifecycle for the type?
     */
    private boolean defaultLifecycle;

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

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

    public boolean isDefaultLifecycle() {
        return defaultLifecycle;
    }

    public void setDefaultLifecycle(final boolean defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

}
