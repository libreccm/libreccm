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
package org.librecms.contentsection;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import static org.librecms.CmsConstants.*;

import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.lifecycle.LifecycleDefinition;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The {@code ContentType} entity links a content item with its content section.
 * It also provides default values for the lifecycle and the workflow.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_TYPES", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "ContentType.findByContentSection",
        query = "SELECT c FROM ContentType c "
                    + "WHERE c.contentSection = :contentSection "
                    + "ORDER BY c.contentItemClass")
    ,
    @NamedQuery(
        name = "ContentType.findByContentSectionAndClass",
        query = "SELECT c FROM ContentType c "
                    + "WHERE c.contentSection = :contentSection "
                    + "AND c.contentItemClass  = :clazz")
    ,
        @NamedQuery(
        name = "ContentType.isInUse",
        query = "SELECT COUNT(i) FROM ContentItem i "
                    + "WHERE i.contentType = :type"
    )
})
public class ContentType extends CcmObject implements Serializable {

    private static final long serialVersionUID = -2708659750560382851L;

    @Column(name = "CONTENT_ITEM_CLASS", length = 1024)
    private String contentItemClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_SECTION_ID")
    private ContentSection contentSection;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_TYPE_LABELS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString label;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_TYPE_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString description;

    @Column(name = "ANCESTORS", length = 1024)
    private String ancestors;

    @Column(name = "DESCENDANTS", length = 1024)
    private String descendants;

    @Column(name = "TYPE_MODE")
    @Enumerated(EnumType.STRING)
    private ContentTypeMode mode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_LIFECYCLE_ID")
    private LifecycleDefinition defaultLifecycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_WORKFLOW")
    private WorkflowTemplate defaultWorkflow;

    public ContentType() {
        super();
        label = new LocalizedString();
        description = new LocalizedString();
    }
    
    public String getContentItemClass() {
        return contentItemClass;
    }

    public void setContentItemClass(final String contentItemClass) {
        this.contentItemClass = contentItemClass;
    }

    public ContentSection getContentSection() {
        return contentSection;
    }

    protected void setContentSection(final ContentSection contentSection) {
        this.contentSection = contentSection;
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

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(final String ancestors) {
        this.ancestors = ancestors;
    }

    public String getDescendants() {
        return descendants;
    }

    public void setDescendants(final String descendants) {
        this.descendants = descendants;
    }

    public ContentTypeMode getMode() {
        return mode;
    }

    public void setMode(final ContentTypeMode mode) {
        this.mode = mode;
    }

    public LifecycleDefinition getDefaultLifecycle() {
        return defaultLifecycle;
    }

    protected void setDefaultLifecycle(
        final LifecycleDefinition defaultLifecycle) {
        this.defaultLifecycle = defaultLifecycle;
    }

    public WorkflowTemplate getDefaultWorkflow() {
        return defaultWorkflow;
    }

    protected void setDefaultWorkflow(final WorkflowTemplate defaultWorkflow) {
        this.defaultWorkflow = defaultWorkflow;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(contentItemClass);
        hash = 79 * hash + Objects.hashCode(label);
        hash = 79 * hash + Objects.hashCode(description);
        hash = 79 * hash + Objects.hashCode(mode);
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

        if (!(obj instanceof ContentType)) {
            return false;
        }

        final ContentType other = (ContentType) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(contentItemClass, other.getContentItemClass())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return mode == other.getMode();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentType;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", contentItemClass = \"%s\", "
                                                + "label = {%s}, "
                                                + "mode = %s%s",
                                            contentItemClass,
                                            Objects.toString(label),
                                            mode,
                                            data));
    }

}
