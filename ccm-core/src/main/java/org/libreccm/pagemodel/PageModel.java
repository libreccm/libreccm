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

import org.libreccm.core.CcmObject;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.web.CcmApplication;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A {@link PageModel} is used by a {@link PageBuilder} implementation to build
 * a page. The {@code PageModel} specifics which components are used on a page.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see PageModelRepository
 * @see PageModelManager
 * @see PageBuilder
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "PAGE_MODELS", schema = CoreConstants.DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "PageModel.findDraftVersion",
        query = "SELECT p FROM PageModel p "
                    + "WHERE p.modelUuid = :uuid "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.DRAFT")
    ,
    @NamedQuery(
        name = "PageModel.hasLiveVersion",
        query = "SELECT (CASE WHEN COUNT(p) > 0 THEN true ELSE False END) "
                    + "FROM PageModel p "
                    + "WHERE p.modelUuid = :uuid "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE"
    )
    ,
    @NamedQuery(
        name = "PageModel.findLiveVersion",
        query = "SELECT p FROM PageModel p "
                    + "WHERE p.modelUuid = :uuid "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE")
    ,
    @NamedQuery(
        name = "PageModel.findByApplication",
        query = "SELECT p FROM PageModel p "
                    + "WHERE p.application = :application "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE")
    ,
    @NamedQuery(
        name = "PageModel.countByApplication",
        query = "SELECT COUNT(p) FROM PageModel p "
                    + "WHERE p.application = :application "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE")
    ,
    @NamedQuery(
        name = "PageModel.findByApplicationAndName",
        query = "SELECT p FROM PageModel p "
                    + "WHERE p.name = :name "
                    + "AND p.application = :application "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE"
    )
    ,
    @NamedQuery(
        name = "PageModel.countByApplicationAndName",
        query = "SELECT COUNT(p) FROM PageModel p "
                    + "WHERE p.name = :name "
                    + "AND p.application = :application "
                    + "AND p.version = org.libreccm.pagemodel.PageModelVersion.LIVE"
    )
})
public class PageModel extends CcmObject implements Serializable {

    private static final long serialVersionUID = 7252512839926020978L;

    /**
     * The UUID of the model. Same for draft and live version.
     */
    @Column(name = "MODEL_UUID", length = 255, nullable = false)
    @NotNull
    private String modelUuid;

    /**
     * The name of this {@code PageModel}. Not localised, for use in URLs.
     */
    @Column(name = "NAME", length = 255)
    private String name;

    /**
     * The version of this {@code PageModel}.
     */
    @Column(name = "VERSION", length = 255, nullable = false)
    @Enumerated(EnumType.STRING)
    private PageModelVersion version;

    /**
     * The localised title of this {@code PageModel} (shown in the
     * administration UI),
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "PAGE_MODEL_TITLES",
                               schema = CoreConstants.DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "PAGE_MODEL_ID")
                               }))
    private LocalizedString title;

    /**
     * A description of this {@code PageModel} describing its purpose.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "PAGE_MODEL_DESCRIPTIONS",
                               schema = CoreConstants.DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "PAGE_MODEL_ID")
                               }))
    private LocalizedString description;

    /**
     * The application with which this {@code PageModel} is associated.
     */
    @ManyToOne
    @JoinColumn(name = "APPLICATION_ID")
    private CcmApplication application;

    /**
     * The type of this {@code PageModel}.
     */
    @Column(name = "TYPE", length = 255, nullable = false)
    @NotNull
    private String type;

    /**
     * The components of the page described by this {@code PageModel}.
     */
    @OneToMany(mappedBy = "pageModel")
    private List<ComponentModel> components;

    public PageModel() {
        title = new LocalizedString();
        description = new LocalizedString();
    }

    public String getModelUuid() {
        return modelUuid;
    }

    protected void setModelUuid(final String modelUuid) {
        this.modelUuid = modelUuid;
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

    protected void setVersion(final PageModelVersion version) {
        this.version = version;
    }

    public LocalizedString getTitle() {
        return title;
    }

    protected void setTitle(final LocalizedString title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    protected void setDescription(final LocalizedString description) {
        Objects.requireNonNull(description);
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
        this.components = new ArrayList<>(components);
    }

    protected void addComponent(final ComponentModel component) {
        components.add(component);
    }

    protected void removeComponent(final ComponentModel component) {
        components.remove(component);
    }

    protected void clearComponents() {
        components.clear();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
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

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof PageModel)) {
            return false;
        }
        final PageModel other = (PageModel) obj;
        if (!other.canEqual(this)) {
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

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof PageModel;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = \"%s\", "
                                                + "title = %s, "
                                                + "description = %s%s",
                                            super.toString(),
                                            name,
                                            Objects.toString(title),
                                            Objects.toString(description),
                                            data));
    }

}
