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
package org.libreccm.pagemodel.layout;

import org.libreccm.core.CoreConstants;
import org.libreccm.pagemodel.ComponentModel;

import java.io.Serializable;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A box in a {@link FlexLayout}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FLEX_LAYOUT_BOXES", schema = CoreConstants.DB_SCHEMA)
@NamedQueries(
@NamedQuery(name = "FlexBox.findBoxesForLayout",
            query = "SELECT b FROM FlexBox b WHERE b.layout = :layout"))
public class FlexBox implements Serializable {

    private static final long serialVersionUID = -6085798536072937899L;

    @Id
    @Column(name = "BOX_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long boxId;

    @ManyToOne
    private FlexLayout layout;
    
    @Column(name = "BOX_ORDER")
    private int order;

    @Column(name = "BOX_SIZE")
    private int size;

    @OneToOne
    @JoinColumn(name = "COMPONENT_ID")
    private ComponentModel component;

    public long getBoxId() {
        return boxId;
    }

    public void setBoxId(final long boxId) {
        this.boxId = boxId;
    }

    public FlexLayout getLayout() {
        return layout;
    }
    
    protected void setLayout(final FlexLayout layout) {
        this.layout = layout;
    }
    
    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public ComponentModel getComponent() {
        return component;
    }

    public void setComponent(final ComponentModel component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (boxId ^ (boxId >>> 32));
        hash = 89 * hash + order;
        hash = 89 * hash + size;
        hash = 89 * hash + Objects.hashCode(component);
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
        if (!(obj instanceof FlexBox)) {
            return false;
        }
        final FlexBox other = (FlexBox) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (boxId != other.getBoxId()) {
            return false;
        }
        if (order != other.getOrder()) {
            return false;
        }
        if (size != other.getSize()) {
            return false;
        }
        return Objects.equals(component, other.getComponent());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof FlexBox;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "columnId = %d, "
                                 + "order = %d"
                                 + "size = %d, "
                                 + "component = %s%s"
                                 + " }",
                             super.toString(),
                             boxId,
                             order,
                             size,
                             Objects.toString(component),
                             data);
    }

}
