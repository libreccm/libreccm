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
package org.libreccm.web;

import org.libreccm.core.Resource;
import org.libreccm.core.UserGroup;
import org.libreccm.jpautils.UriConverter;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "applications")
public class Application extends Resource implements Serializable {

    private static final long serialVersionUID = 9205226362368890784L;

    @Column(name = "primary_url", length = 1024, nullable = false)
    @Convert(converter = UriConverter.class)
    private URI primaryUrl;

    @OneToOne
    @JoinColumn(name = "container_group_id")
    private UserGroup containerGroup;

    public URI getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(final URI primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public UserGroup getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(final UserGroup containerGroup) {
        this.containerGroup = containerGroup;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(primaryUrl);
        hash = 97 * hash + Objects.hashCode(containerGroup);
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

        if (!(obj instanceof Application)) {
            return false;
        }

        final Application other = (Application) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(primaryUrl, other.getPrimaryUrl())) {
            return false;
        }
        return Objects.equals(containerGroup, other.getContainerGroup());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Application;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", primaryUrl = \"%s\", "
                                                + "containerGroup = %s%s",
                                            primaryUrl,
                                            Objects.toString(containerGroup),
                                            data));
    }

}
