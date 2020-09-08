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
package org.libreccm.portal;

import org.libreccm.core.Resource;

import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PORTLETS", schema = DB_SCHEMA)
public class Portlet extends Resource implements Serializable {

    private static final long serialVersionUID = -5718126018588744104L;

    @ManyToOne
    @JoinColumn(name = "PORTAL_ID")
    private Portal portal;

    @Column(name = "CELL_NUMBER")
    private long cellNumber;

    @Column(name = "SORT_KEY")
    private long sortKey;

    public Portal getPortal() {
        return portal;
    }

    protected void setPortal(final Portal portal) {
        this.portal = portal;
    }

    public long getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(final long cellNumber) {
        this.cellNumber = cellNumber;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(final long sortKey) {
        this.sortKey = sortKey;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(portal);
        hash = 97 * hash + (int) (cellNumber ^ (cellNumber >>> 32));
        hash = 97 * hash + (int) (sortKey ^ (sortKey >>> 32));
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
        
        if (!(obj instanceof Portlet)) {
            return false;
        }

        final Portlet other = (Portlet) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(portal, other.getPortal())) {
            return false;
        }
        if (cellNumber != other.getCellNumber()) {
            return false;
        }
        return sortKey == other.getSortKey();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Portlet;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", portal = %s, "
                                                + "cellNumber = %d, "
                                                + "sortKey = %d%s",
                                            Objects.toString(portal),
                                            cellNumber,
                                            sortKey,
                                            data));
    }

}
