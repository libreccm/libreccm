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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * A layout component container which provides the information for laying
 * out other components using the Flex Layout properties in CSS.
 * 
 * The component contains a collection of {@code FlexBox} objects which contain
 * another component. A theme should use the Flex Layout properties in CSS to 
 * create the real layout.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FLEX_LAYOUT_COMPONENTS", schema = CoreConstants.DB_SCHEMA)
public class FlexLayout extends ComponentModel {

    private static final long serialVersionUID = 1977244351125227610L;

    /**
     * The direction in which the components are shown. This is really only a 
     * hint for the theme, usually for the layout on wide screens. On small 
     * screens is is most likely that the boxes are stacked vertically.
     */
    @Column(name = "DIRECTION")
    @Enumerated(EnumType.STRING)
    private FlexDirection direction;

    /**
     * The boxes containing the components.
     */
    @OneToMany(mappedBy = "layout")
    @OrderBy(value = "order")
    private List<FlexBox> boxes;

    public FlexLayout() {
        boxes = new ArrayList<>();
    }

    public FlexDirection getDirection() {
        return direction;
    }

    public void setDirection(final FlexDirection direction) {
        this.direction = direction;
    }

    public List<FlexBox> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    protected void setBoxes(final List<FlexBox> boxes) {
        this.boxes = new ArrayList<>(boxes);
    }

    protected void addBox(final FlexBox box) {
        boxes.add(box);
    }

    protected void removeBox(final FlexBox box) {
        boxes.remove(box);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(direction);
        hash = 67 * hash + Objects.hashCode(boxes);
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
        if (!(obj instanceof FlexLayout)) {
            return false;
        }
        final FlexLayout other = (FlexLayout) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (direction != other.getDirection()) {
            return false;
        }
        return Objects.equals(boxes, other.getBoxes());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof FlexLayout;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", "
                                                + "direction = %s, "
                                                + "layoutCells = %s",
                                            direction,
                                            Objects.toString(boxes)));
    }

}
