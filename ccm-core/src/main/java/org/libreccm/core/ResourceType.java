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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.portation.Portable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * This class is a port of the old {@code ResourceType} entity. 
 * 
 * @deprecated The real purpose of this class is not clear. Also the 
 * informations provided by the entities of this class are all quite static or
 * can be interfered from the classes itself. In modern Java most if not all the
 * informations provided by the entities of this class would be expressed as 
 * annotations. At the moment it is not clear of we can remove this class 
 * completely therefore it is still here but will maybe removed very soon.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Deprecated
@Entity
@Table(name = "RESOURCE_TYPES", schema = DB_SCHEMA)
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
        @NamedQuery(name = "ResourceType.findByTitle",
                query = "SELECT r FROM ResourceType r WHERE r.title = :title")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = ResourceTypeIdResolver.class,
                  property = "title")
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.NPathComplexity",
                   "PMD.LongVariable"})
public class ResourceType implements Serializable, Portable {

    private static final long serialVersionUID = 4563584142251370627L;

    @Id
    @Column(name = "RESOURCE_TYPE_ID")
    private long resourceTypeId;

    @Column(name = "TITLE", length = 254, nullable = false)
    private String title;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "RESOURCE_TYPE_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "RESOURCE_TYPE_ID")}))
    private LocalizedString description;

    @Column(name = "WORKSPACE_APP")
    private boolean workspaceApplication;

    @Column(name = "FULL_PAGE_VIEW")
    private boolean viewableAsFullPage;

    @Column(name = "EMBEDDED_VIEW")
    private boolean viewableAsEmbedded;

    @Column(name = "SINGLETON")
    private boolean singleton;

    public ResourceType() {
        description = new LocalizedString();
    }

    public long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(final long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    public boolean isWorkspaceApplication() {
        return workspaceApplication;
    }

    public void setWorkspaceApplication(final boolean workspaceApplication) {
        this.workspaceApplication = workspaceApplication;
    }

    public boolean isViewableAsFullPage() {
        return viewableAsFullPage;
    }

    public void setViewableAsFullPage(final boolean viewableAsFullPage) {
        this.viewableAsFullPage = viewableAsFullPage;
    }

    public boolean isViewableAsEmbedded() {
        return viewableAsEmbedded;
    }

    public void setViewableAsEmbedded(final boolean viewableAsEmbedded) {
        this.viewableAsEmbedded = viewableAsEmbedded;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.resourceTypeId ^ (this.resourceTypeId
                                                         >>> 32));
        hash = 17 * hash + Objects.hashCode(this.title);
        hash = 17 * hash + Objects.hashCode(this.description);
        hash = 17 * hash + (this.workspaceApplication ? 1 : 0);
        hash = 17 * hash + (this.viewableAsFullPage ? 1 : 0);
        hash = 17 * hash + (this.viewableAsEmbedded ? 1 : 0);
        hash = 17 * hash + (this.singleton ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ResourceType)) {
            return false;
        }
        final ResourceType other = (ResourceType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (this.resourceTypeId != other.getResourceTypeId()) {
            return false;
        }
        if (!Objects.equals(this.title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(this.description, other.getDescription())) {
            return false;
        }
        if (this.workspaceApplication != other.isWorkspaceApplication()) {
            return false;
        }
        if (this.viewableAsFullPage != other.isViewableAsFullPage()) {
            return false;
        }
        if (this.viewableAsEmbedded != other.isViewableAsEmbedded()) {
            return false;
        }
        return this.singleton == other.isSingleton();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ResourceType;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "resourceTypeId = %d, "
                                 + "title = \"%s\", "
                                 + "description = { %s }, "
                                 + "workspaceApplication = %b, "
                                 + "viewableAsFullPage = %b, "
                                 + "viewableAsEmbedded = %b, "
                                 + "singleton = %b%s"
                                 + " }",
                             super.toString(),
                             resourceTypeId,
                             title,
                             Objects.toString(description),
                             workspaceApplication,
                             viewableAsFullPage,
                             viewableAsEmbedded,
                             singleton,
                             data);
    }

}
