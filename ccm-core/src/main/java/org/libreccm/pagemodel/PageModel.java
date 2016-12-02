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
package org.libreccm.pagemodel;

import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * A {@link PageModel} is used by a {@link PageBuilder} implementation to build
 * a page. The {@code PageModel} specifics which components are used on a page.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PAGE_MODELS", schema = CoreConstants.DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "PageModel.findByApplication",
        query = "SELECT p FROM PageModel p WHERE p.application = :application")
    ,
    @NamedQuery(
        name = "PageModel.countByApplication",
        query = "SELECT COUNT(p) FROM PageModel p "
                    + "WHERE p.application = :application")
    ,
    @NamedQuery(
        name = "PageModel.findByApplicationAndName",
        query = "SELECT p FROM PageModel p "
                    + "WHERE p.name = :name AND p.application = :application"
    ),
    @NamedQuery(
        name = "PageModel.countByApplicationAndName",
        query = "SELECT COUNT(p) FROM PageModel p "
                    + "WHERE p.name = :name AND p.application = :application"
    )
})
public class PageModel implements Serializable {

    private static final long serialVersionUID = 7252512839926020978L;

    @Id
    @Column(name = "PAGE_MODEL_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pageModelId;

    @Column(name = "UUID", length = 255, nullable = false)
    @NotNull
    private String uuid;

    @Column(name = "NAME", length = 255)
    private String name;

    @Column(name = "VERSION", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    private PageModelVersion version;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "PAGE_MODEL_TITLES",
                               schema = CoreConstants.DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "PAGE_MODEL_ID")
                               }))
    private LocalizedString title;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "PAGE_MODEL_DESCRIPTIONS",
                               schema = CoreConstants.DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "PAGE_MODEL_ID")
                               }))
    private LocalizedString description;

    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID")
    private CcmApplication application;

    @Column(name = "TYPE", length = 255, nullable = false)
    @NotNull
    private String type;

    @OneToMany(mappedBy = "pageModel")
    private List<ComponentModel> components;

    public PageModel() {
        title = new LocalizedString();
        description = new LocalizedString();
    }

    public long getPageModelId() {
        return pageModelId;
    }

    protected void setPageModelId(final long pageModelId) {
        this.pageModelId = pageModelId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public PageModelVersion getVersion() {
        return version;
    }

    protected void setPageModelVersion(final PageModelVersion version) {
        this.version = version;
    }

    public LocalizedString getTitle() {
        return title;
    }

    protected void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    protected void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public CcmApplication getApplication() {
        return application;
    }

    protected void setApplication(final CcmApplication application) {
        this.application = application;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<ComponentModel> getComponents() {
        return Collections.unmodifiableList(components);
    }

    protected void setComponents(final List<ComponentModel> components) {
        this.components = components;
    }

    public void addComponent(final ComponentModel component) {
        components.add(component);
    }

    public void removeComponent(final ComponentModel component) {
        components.remove(component);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (pageModelId ^ (pageModelId >>> 32));
        hash = 71 * hash + Objects.hashCode(uuid);
        hash = 71 * hash + Objects.hashCode(name);
        hash = 71 * hash + Objects.hashCode(title);
        hash = 71 * hash + Objects.hashCode(description);
        hash = 71 * hash + Objects.hashCode(type);
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
        if (!(obj instanceof PageModel)) {
            return false;
        }
        final PageModel other = (PageModel) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (pageModelId != other.getPageModelId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        return Objects.equals(description, other.getDescription());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof PageModel;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "pageModelId = %d, "
                                 + "uuid = %s, "
                                 + "name = \"%s\", "
                                 + "title = %s, "
                                 + "description = %s, "
                                 + "type = \"%s\""
                                 + " }",
                             super.toString(),
                             pageModelId,
                             uuid,
                             name,
                             Objects.toString(title),
                             Objects.toString(description),
                             type);
    }

}
