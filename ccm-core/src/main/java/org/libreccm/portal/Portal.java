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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "portals")
public class Portal extends Resource implements Serializable {

    private static final long serialVersionUID = -5492307663469206053L;

    @Column(name = "template")
    private boolean template;

    @OneToMany(mappedBy = "portal")
    private List<Portlet> portlets;

    public Portal() {
        super();

        portlets = new ArrayList<>();
    }

    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(final boolean template) {
        this.template = template;
    }

    public List<Portlet> getPortlets() {
        if (portlets == null) {
            return null;
        } else {
            return Collections.unmodifiableList(portlets);
        }
    }

    protected void setPortlets(final List<Portlet> portlets) {
        this.portlets = portlets;
    }

    protected void addPortlet(final Portlet portlet) {
        portlets.add(portlet);
    }

    protected void removePortlet(final Portlet portlet) {
        portlets.remove(portlet);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + (template ? 1 : 0);
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

        if (!(obj instanceof Portal)) {
            return false;
        }

        final Portal other = (Portal) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return (template == other.isTemplate());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Portal;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", template = %b%s",
                                            template,
                                            data));
    }

}
