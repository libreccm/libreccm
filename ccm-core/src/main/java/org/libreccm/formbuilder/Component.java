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
package org.libreccm.formbuilder;

import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_components", schema = "ccm_core")
//Can't reduce complexity yet
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity"})
public class Component extends CcmObject implements Serializable {

    private static final long serialVersionUID = 1787173100367982069L;

    @Column(name = "admin_name")
    private String adminName;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "formbuilder_component_descriptions",
                               joinColumns = {
                                   @JoinColumn(name = "component_id")}))
    private LocalizedString description;

    @Column(name = "attribute_string")
    private String attributeString;

    @Column(name = "active")
    private boolean active;

    @ManyToOne
    private Component parentComponent;

    @OneToMany(mappedBy = "parentComponent")
    private List<Component> childComponents;

    @Column(name = "component_order")
    private long componentOrder;

    @Column(name = "selected")
    private boolean selected;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(final String adminName) {
        this.adminName = adminName;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getAttributeString() {
        return attributeString;
    }

    public void setAttributeString(final String attributeString) {
        this.attributeString = attributeString;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Component getParentComponent() {
        return parentComponent;
    }

    protected void setParentComponent(final Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public List<Component> getChildComponents() {
        if (childComponents == null) {
            return null;
        } else {
            return Collections.unmodifiableList(childComponents);
        }
    }

    protected void setChildComponents(final List<Component> childComponents) {
        this.childComponents = childComponents;
    }

    public long getComponentOrder() {
        return componentOrder;
    }

    public void setComponentOrder(final long componentOrder) {
        this.componentOrder = componentOrder;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 53 * hash + Objects.hashCode(adminName);
        hash = 53 * hash + Objects.hashCode(description);
        hash = 53 * hash + Objects.hashCode(attributeString);
        hash = 53 * hash + (active ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(parentComponent);
        hash = 53 * hash + (int) (componentOrder ^ (componentOrder >>> 32));
        hash = 53 * hash + (selected ? 1 : 0);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity",
                       "PMD.NPathComplexity"})
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Component)) {
            return false;
        }
        final Component other = (Component) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(adminName, other.getAdminName())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(attributeString, other.getAttributeString())) {
            return false;
        }
        if (active != other.isActive()) {
            return false;
        }
        if (!Objects.equals(parentComponent, other.getParentComponent())) {
            return false;
        }
        if (componentOrder != other.getComponentOrder()) {
            return false;
        }
        return selected == other.isSelected();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Component;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", adminName = \"%s\", "
                                                + "description = %s, "
                                                + "attributeString  = \"%s\", "
                                                + "active = %b, "
                                                + "parentComponent = %s, "
                                                + "componentOrder = %d, "
                                                + "selected = %b%s",
                                            adminName,
                                            Objects.toString(description),
                                            attributeString,
                                            active,
                                            Objects.toString(parentComponent),
                                            componentOrder,
                                            selected,
                                            data));
    }

}
