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
package org.libreccm.messaging;

import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "threads")
public class Thread extends CcmObject implements Serializable {

    private static final long serialVersionUID = -395123286904985770L;

    @OneToOne
    @JoinColumn(name = "root_id")
    private Message root;

    public Message getRoot() {
        return root;
    }

    protected void setRoot(final Message root) {
        this.root = root;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * hash + Objects.hashCode(root);
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
        final Thread other = (Thread) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(this.root, other.getRoot());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Thread;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", root = %s%s",
                                            Objects.toString(root),
                                            data));
    }

}
