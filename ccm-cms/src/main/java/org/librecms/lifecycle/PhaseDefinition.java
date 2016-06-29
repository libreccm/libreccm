/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.lifecycle;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LIFECYCLE_PHASE_DEFINITIONS", schema = DB_SCHEMA)
public class PhaseDefinition implements Serializable {

    private static final long serialVersionUID = -7718926310758794075L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PHASE_DEFINITION_ID")
    private long definitionId;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "LIFECYCLE_PHASE_DEFINITION_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString label;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "LIFECYCLE_PHASE_DEFINITION_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString description;

    @Column(name = "DEFAULT_DELAY")
    private long defaultDelay;

    @Column(name = "DEFAULT_DURATION")
    private long defaultDuration;

    @Column(name = "DEFAULT_LISTENER", length = 1024)
    private String defaultListener;

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public long getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(final long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    public long getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(final long defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public String getDefaultListener() {
        return defaultListener;
    }

    public void setDefaultListener(final String defaultListener) {
        this.defaultListener = defaultListener;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (definitionId ^ (definitionId >>> 32));
        hash = 79 * hash + Objects.hashCode(label);
        hash = 79 * hash + Objects.hashCode(description);
        hash = 79 * hash + (int) (defaultDelay ^ (defaultDelay >>> 32));
        hash = 79 * hash + (int) (defaultDuration ^ (defaultDuration >>> 32));
        hash = 79 * hash + Objects.hashCode(defaultListener);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PhaseDefinition)) {
            return false;
        }
        final PhaseDefinition other = (PhaseDefinition) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (definitionId != other.getDefinitionId()) {
            return false;
        }
        if (defaultDelay != other.getDefaultDelay()) {
            return false;
        }
        if (defaultDuration != other.getDefaultDuration()) {
            return false;
        }
        if (!Objects.equals(defaultListener, other.getDefaultListener())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        return Objects.equals(description, other.getDescription());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof PhaseDefinition;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "definitionId = %d, "
                                 + "label = %s, "
                                 + "description = %s, "
                                 + "defaultDelay = %s, "
                                 + "defaultDuration = %s, "
                                 + "defaultListener = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             definitionId,
                             Objects.toString(label),
                             Objects.toString(description),
                             defaultDelay,
                             defaultDuration,
                             defaultListener,
                             data);
    }

}
