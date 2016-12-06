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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Base class for the components model for use in a {@link PageModel}. This
 * class is not designed for direct use. Instead the classes for concrete
 * components have be used. A component must be annotation with
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "PAGE_MODEL_COMPONENT_MODELS", schema = CoreConstants.DB_SCHEMA)
public class ComponentModel implements Serializable {

    private static final long serialVersionUID = 8585775139379396806L;

    @Id
    @Column(name = "COMPONENT_MODEL_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long componentModelId;

    @Column(name = "UUID", length = 255, nullable = false)
    @NotNull
    private String uuid;

    @Column(name = "MODEL_UUID", length = 255, nullable = false)
    @NotNull
    private String modelUuid;
    
    @ManyToOne
    @JoinColumn(name = "PAGE_MODEL_ID")
    private PageModel pageModel;

    @Column(name = "ID_ATTRIBUTE", length = 255)
    private String idAttribute;

    @Column(name = "CLASS_ATTRIBUTE", length = 512)
    private String classAttribute;

    @Column(name = "STYLE_ATTRIBUTE", length = 1024)
    private String styleAttribute;

    @Column(name = "COMPONENT_KEY", length = 255)
    private String key;

    public long getComponentModelId() {
        return componentModelId;
    }

    protected void setComponentModelId(final long componentModelId) {
        this.componentModelId = componentModelId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getModelUuid() {
        return modelUuid;
    }
    
    protected void setModelUuid(final String modelUuid) {
        this.modelUuid = modelUuid;
    }
    
    public PageModel getPageModel() {
        return pageModel;
    }

    protected void setPageModel(final PageModel pageModel) {
        this.pageModel = pageModel;
    }

    public String getIdAttribute() {
        return idAttribute;
    }

    public void setIdAttribute(final String idAttribute) {
        this.idAttribute = idAttribute;
    }

    public String getClassAttribute() {
        return classAttribute;
    }

    public void setClassAttribute(final String classAttribute) {
        this.classAttribute = classAttribute;
    }

    public String getStyleAttribute() {
        return styleAttribute;
    }

    public void setStyleAttribute(final String styleAttribute) {
        this.styleAttribute = styleAttribute;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash
            = 53 * hash + (int) (componentModelId ^ (componentModelId >>> 32));
        hash = 53 * hash + Objects.hashCode(uuid);
        hash = 53 * hash + Objects.hashCode(pageModel);
        hash = 53 * hash + Objects.hashCode(idAttribute);
        hash = 53 * hash + Objects.hashCode(classAttribute);
        hash = 53 * hash + Objects.hashCode(styleAttribute);
        hash = 53 * hash + Objects.hashCode(key);
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
        if (!(obj instanceof ComponentModel)) {
            return false;
        }
        final ComponentModel other = (ComponentModel) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (componentModelId != other.getComponentModelId()) {
            return false;
        }
        if (!Objects.equals(idAttribute, other.getIdAttribute())) {
            return false;
        }
        if (!Objects.equals(classAttribute, other.getClassAttribute())) {
            return false;
        }
        if (!Objects.equals(styleAttribute, other.getStyleAttribute())) {
            return false;
        }
        if (!Objects.equals(key, other.getKey())) {
            return false;
        }
        return Objects.equals(pageModel, other.getPageModel());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ComponentModel;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "componentModelId = %d, "
                                 + "pageModel = %s, "
                                 + "idAttribute = \"%s\", "
                                 + "classAttribute = \"%s\", "
                                 + "styleAttribute = \"%s\", "
                                 + "key = \"%s\""
                                 + " }",
                             super.hashCode(),
                             componentModelId,
                             pageModel,
                             idAttribute,
                             classAttribute,
                             styleAttribute,
                             key);
    }

}
