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
package org.libreccm.web;

import static org.libreccm.core.CoreConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.libreccm.core.Group;
import org.libreccm.core.Privilege;
import org.libreccm.core.ResourceType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "application_types", schema = DB_SCHEMA)
@SuppressWarnings("PMD.LongVariable")
public class ApplicationType extends ResourceType implements Serializable {

    private static final long serialVersionUID = -1175728067001112457L;
    private static final String PMD_LONG_VARIABLE = "PMD.LongVariable";

    @OneToMany
    @JoinColumn(name = "relevant_privilege_id")
    @SuppressWarnings(PMD_LONG_VARIABLE)
    private List<Privilege> relevantPrivileges;

    @ManyToOne
    @JoinColumn(name = "container_group_id")
    private Group containerGroup;

    @ManyToOne
    @JoinColumn(name = "provider_app_type_id")
    @SuppressWarnings(PMD_LONG_VARIABLE)
    private ApplicationType providerApplicationType;

    @OneToMany(mappedBy = "providerApplicationType")
    @SuppressWarnings(PMD_LONG_VARIABLE)
    private List<ApplicationType> dependentApplicationTypes;

    public ApplicationType() {
        super();

        relevantPrivileges = new ArrayList<>();
        dependentApplicationTypes = new ArrayList<>();
    }

    public List<Privilege> getRelevantPrivileges() {
        return Collections.unmodifiableList(relevantPrivileges);
    }

    @SuppressWarnings(PMD_LONG_VARIABLE)
    protected void setRelevantPrivileges(
        final List<Privilege> relevantPrivileges) {
        this.relevantPrivileges = relevantPrivileges;
    }

    protected void addRelevantPrivilege(final Privilege privilege) {
        relevantPrivileges.add(privilege);
    }

    protected void removeRelevantPrivlege(final Privilege privilege) {
        relevantPrivileges.remove(privilege);
    }

    public Group getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(final Group containerGroup) {
        this.containerGroup = containerGroup;
    }

    public ApplicationType getProviderApplicationType() {
        return providerApplicationType;
    }

    @SuppressWarnings(PMD_LONG_VARIABLE)
    protected void setProviderApplicationType(
        final ApplicationType providerApplicationType) {
        this.providerApplicationType = providerApplicationType;
    }

    @SuppressWarnings(PMD_LONG_VARIABLE)
    public List<ApplicationType> getDependentApplicationTypes() {
        return Collections.unmodifiableList(dependentApplicationTypes);
    }

    @SuppressWarnings(PMD_LONG_VARIABLE)
    protected void setDependentApplicationTypes(
        final List<ApplicationType> dependentApplicationTypes) {
        this.dependentApplicationTypes = dependentApplicationTypes;
    }
    
    protected void addDependantApplicationType(
        final ApplicationType applicationType) {
        dependentApplicationTypes.add(applicationType);
    }

    protected void removeDependentApplicationType(
        final ApplicationType applicationType) {
        dependentApplicationTypes.remove(applicationType);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.containerGroup);
        hash = 97 * hash + Objects.hashCode(this.providerApplicationType);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ApplicationType)) {
            return false;
        }
        final ApplicationType other = (ApplicationType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(this.containerGroup, other.containerGroup)) {
            return false;
        }
        return Objects.equals(this.providerApplicationType,
                              other.providerApplicationType);
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ApplicationType;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(
            ", containerGroup = { %s },"
                + "providerApplicationType = { %s }%s",
            Objects.toString(containerGroup),
            Objects.toString(
                providerApplicationType),
            data));
    }

}
