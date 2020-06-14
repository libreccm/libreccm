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

import org.hibernate.search.annotations.Field;
import org.libreccm.core.Identifiable;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LIFECYLE_DEFINITIONS", schema = DB_SCHEMA)
public class LifecycleDefinition implements Identifiable, Serializable {

    private static final long serialVersionUID = 1291162870555527717L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LIFECYCLE_DEFINITION_ID")
    private long definitionId;

    @Column(name = "UUID", unique = true)
    @NotNull
    @Field
    @XmlElement(name = "uuid")
    private String uuid;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "LIFECYCLE_DEFINITION_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString label;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "LIFECYCLE_DEFINITION_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString description;

    @Column(name = "DEFAULT_LISTENER", length = 1024)
    private String defaultListener;

    @OneToMany
    @JoinColumn(name = "LIFECYCLE_DEFINITION_ID")
    private List<PhaseDefinition> phaseDefinitions;

    public LifecycleDefinition() {
        label = new LocalizedString();
        description = new LocalizedString();
        phaseDefinitions = new ArrayList<>();
    }

    public long getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(final long definitionId) {
        this.definitionId = definitionId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public void setLabel(final LocalizedString label) {
        Objects.requireNonNull(label);
        this.label = label;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    public String getDefaultListener() {
        return defaultListener;
    }

    public void setDefaultListener(final String defaultListener) {
        this.defaultListener = defaultListener;
    }

    public List<PhaseDefinition> getPhaseDefinitions() {
        if (phaseDefinitions == null) {
            return null;
        } else {
            return Collections.unmodifiableList(phaseDefinitions);
        }
    }

    protected void setPhaseDefinitions(List<PhaseDefinition> phaseDefinitions) {
        this.phaseDefinitions = phaseDefinitions;
    }

    public void addPhaseDefinition(final PhaseDefinition definition) {
        phaseDefinitions.add(definition);
    }

    public void removePhaseDefinition(final PhaseDefinition definition) {
        phaseDefinitions.remove(definition);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (int) (definitionId ^ (definitionId >>> 32));
        hash = 71 * hash + Objects.hashCode(label);
        hash = 71 * hash + Objects.hashCode(description);
        hash = 71 * hash + Objects.hashCode(defaultListener);
        hash = 71 * hash + Objects.hashCode(phaseDefinitions);
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
        if (!(obj instanceof LifecycleDefinition)) {
            return false;
        }
        final LifecycleDefinition other = (LifecycleDefinition) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (definitionId != other.getDefinitionId()) {
            return false;
        }
        if (!Objects.equals(defaultListener, other.getDefaultListener())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return Objects.equals(phaseDefinitions, other.getPhaseDefinitions());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof LifecycleDefinition;
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
                                 + " }",
                             super.toString(),
                             definitionId,
                             Objects.toString(label),
                             Objects.toString(description),
                             data);
    }

}
