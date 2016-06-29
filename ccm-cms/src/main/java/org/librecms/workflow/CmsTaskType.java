/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.librecms.workflow;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASK_TYPES", schema = DB_SCHEMA)
public class CmsTaskType implements Serializable {

    private static final long serialVersionUID = -4326031746212785970L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TASK_TYPE_ID")
    private long taskTypeId;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ARTICLE_LEADS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString name;

    @Column(name = "DEFAULT_URL_GENERATOR_CLASS", length = 1024)
    private String defaultUrlGeneratorClass;

    @Column(name = "PRIVILEGE", length = 256)
    private String privilege;

    @OneToMany
    @JoinColumn(name = "TASK_TYPE_ID")
    private Set<TaskEventUrlGenerator> generators;

    public CmsTaskType() {
        generators = new HashSet<>();
    }

    public long getTaskTypeId() {
        return taskTypeId;
    }

    protected void setTaskTypeId(final long taskTypeId) {
        this.taskTypeId = taskTypeId;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public String getDefaultUrlGeneratorClass() {
        return defaultUrlGeneratorClass;
    }

    public void setDefaultUrlGeneratorClass(
        final String defaultUrlGeneratorClass) {
        this.defaultUrlGeneratorClass = defaultUrlGeneratorClass;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }

    public Set<TaskEventUrlGenerator> getGenerators() {
        if (generators == null) {
            return null;
        } else {
            return Collections.unmodifiableSet(generators);
        }
    }

    protected void setGenerators(final Set<TaskEventUrlGenerator> generators) {
        this.generators = generators;
    }

    public void addGenerator(final TaskEventUrlGenerator generator) {
        generators.add(generator);
    }

    public void removeGenerator(final TaskEventUrlGenerator generator) {
        generators.remove(generator);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (taskTypeId ^ (taskTypeId >>> 32));
        hash = 79 * hash + Objects.hashCode(name);
        hash = 79 * hash + Objects.hashCode(defaultUrlGeneratorClass);
        hash = 79 * hash + Objects.hashCode(privilege);
        hash = 79 * hash + Objects.hashCode(generators);
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
        if (obj instanceof CmsTaskType) {
            return false;
        }
        final CmsTaskType other = (CmsTaskType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (taskTypeId != other.getTaskTypeId()) {
            return false;
        }
        if (!Objects.equals(defaultUrlGeneratorClass,
                            other.getDefaultUrlGeneratorClass())) {
            return false;
        }
        if (!Objects.equals(privilege, other.getPrivilege())) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }

        return Objects.equals(generators, other.getGenerators());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof CmsTaskType;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "taskTypeId = %d, "
                                 + "name = %s, "
                                 + "defaultUrlGeneratorClass = \"%s\", "
                                 + "privilege = \"%s\","
                                 + "generators = { %s }%s"
                                 + " }",
                             super.toString(),
                             taskTypeId,
                             Objects.toString(name),
                             defaultUrlGeneratorClass,
                             privilege,
                             Objects.toString(generators),
                             data);
    }

}
