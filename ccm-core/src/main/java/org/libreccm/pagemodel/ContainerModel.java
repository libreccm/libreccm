/*
 * Copyright (C) 2018 LibreCCM Foundation.
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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.libreccm.core.CoreConstants;
import org.libreccm.pagemodel.styles.Styles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A {@code ContainerModel} for grouping {@link ComponentModel}s. Each
 * {@link PageModel} contains a least one container. A container also contains
 * styles which allow it to provide the theme engine with CSS for aligning the
 * components in the container. Please note that theme developers can ignore the
 * information.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PAGE_MODEL_CONTAINER_MODELS", schema = CoreConstants.DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "ContainerModel.findByKeyAndPage",
                query = "SELECT c FROM ContainerModel c "
                            + "WHERE c.key = :key "
                            + "AND c.pageModel = :pageModel")
})
@XmlRootElement(name = "container-model")
public class ContainerModel implements Serializable {

    private static final long serialVersionUID = -7472858443655353588L;

    /**
     * ID of the ContainerModel in the database.
     */
    @Id
    @Column(name = "CONTAINER_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "container-model-id")
    private long containerId;

    /**
     * The UUID of this version of the container.
     */
    @Column(name = "UUID", length = 255, nullable = false)
    @NotNull
    private String uuid;

    /**
     * The UUID of the container which is the same in all versions.
     */
    @Column(name = "CONTAINER_UUID", length = 255, nullable = false)
    @NotNull
    private String containerUuid;

    /**
     * A key for identifying the container inside a {@link PageModel}. May be
     * used for the value of the {@code id} or {@code class} attribute in HTML.
     * It is recommended the use semantic names.
     */
    @Column(name = "CONTAINER_KEY", length = 255)
    private String key;

    /**
     * Styles for this container. This should be limited to CSS which describes
     * the layout of the components in the container. Colours etc. are the
     * responsibility of the theme. A theme might also alter the styles stored
     * here.
     */
    @OneToOne
    @JoinColumn(name = "STYLE_ID")
    @Cascade(CascadeType.ALL)
    private Styles styles;

    @ManyToOne
    @JoinColumn(name = "PAGE_MODEL_ID")
    @XmlTransient
    private PageModel pageModel;

    /**
     * The components in this this container.
     */
    @OneToMany(mappedBy = "container")
    @OrderBy("key ASC")
    private List<ComponentModel> components;

    public ContainerModel() {
        this.styles = new Styles();
        this.components = new ArrayList<>();
    }

    public long getContainerId() {
        return containerId;
    }

    protected void setContainerId(final long containerId) {
        this.containerId = containerId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getContainerUuid() {
        return containerUuid;
    }

    protected void setContainerUuid(final String containerUuid) {
        this.containerUuid = containerUuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public Styles getStyles() {
        return styles;
    }

    protected void setStyles(final Styles styles) {
        this.styles = styles;
    }

    public PageModel getPageModel() {
        return pageModel;
    }

    protected void setPageModel(final PageModel pageModel) {
        this.pageModel = pageModel;
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
        int hash = 5;
        hash = 97 * hash + (int) (containerId ^ (containerId >>> 32));
        hash = 97 * hash + Objects.hashCode(uuid);
        hash = 97 * hash + Objects.hashCode(containerUuid);
        hash = 97 * hash + Objects.hashCode(key);
        hash = 97 * hash + Objects.hashCode(styles);
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
        if (!(obj instanceof ContainerModel)) {
            return false;
        }
        final ContainerModel other = (ContainerModel) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (containerId != other.getContainerId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (!Objects.equals(containerUuid, other.getContainerUuid())) {
            return false;
        }

        if (!Objects.equals(styles, other.getStyles())) {
            return false;
        }

        return Objects.equals(key, other.getKey());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ContainerModel;
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "containerId = %d, "
                                 + "uuid = %s, "
                                 + "containerUuid = %s, "
                                 + "key = \"%s\", "
                                 + "styles = %s%s"
                                 + " }",
                             super.toString(),
                             containerId,
                             uuid,
                             containerUuid,
                             key,
                             Objects.toString(styles),
                             data);
    }

    @Override
    public final String toString() {

        return toString("");
    }

}
