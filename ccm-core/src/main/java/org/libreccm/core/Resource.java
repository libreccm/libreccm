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
package org.libreccm.core;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Jens Pelzetter jens@jp-digital.de
 */
@Entity
@Table(name = "resources")
public class Resource extends CcmObject implements Serializable {

    private static final long serialVersionUID = 7345482620613842781L;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "resource_titles",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}))
    private LocalizedString title;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "resource_descriptions",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}))
    private LocalizedString description;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @OneToMany(mappedBy = "parent")
    private List<Resource> childs;

    @ManyToOne
    private Resource parent;

//    @Column(name = "resource_type")
//    private String resourceType;

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public Date getCreated() {
        return new Date(created.getTime());
    }

    public void setCreated(final Date created) {
        this.created = new Date(created.getTime());
    }

    public List<Resource> getChilds() {
        return Collections.unmodifiableList(childs);
    }

    protected void setChilds(final List<Resource> childs) {
        this.childs = childs;
    }

    protected void addChild(final Resource child) {
        childs.add(child);
    }

    protected void removeChild(final Resource child) {
        childs.remove(child);
    }

    public Resource getParent() {
        return parent;
    }

    protected void setParent(Resource parent) {
        this.parent = parent;
    }

//    public String getResourceType() {
//        return resourceType;
//    }
//
//    public void setResourceType(final String resourceType) {
//        this.resourceType = resourceType;
//    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(title);
        hash = 29 * hash + Objects.hashCode(description);
        hash = 29 * hash + Objects.hashCode(created);
        hash = 29 * hash + Objects.hashCode(parent);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Resource other = (Resource) obj;

        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(created, other.getCreated())) {
            return false;
        }
        return Objects.equals(parent, other.getParent());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Resource;
    }

    @Override
    public String toString(final String data) {
        return super.toString(
            String.format(", title = %s, "
                              + "created = %tF %<tT%s",
                          title.toString(),
                          created,
                          data));
    }

}
