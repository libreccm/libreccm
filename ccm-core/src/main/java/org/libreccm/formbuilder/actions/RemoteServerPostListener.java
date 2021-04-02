/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.libreccm.formbuilder.actions;

import org.libreccm.formbuilder.ProcessListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_REMOTE_SERVER_POST_LISTENER", schema = DB_SCHEMA)
public class RemoteServerPostListener
        extends ProcessListener
        implements Serializable {

    private static final long serialVersionUID = 7095242410811956838L;

    @Column(name = "REMOTE_URL", length = 2048)
    private String remoteUrl;

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(final String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(this.remoteUrl);
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
        
        if (!(obj instanceof RemoteServerPostListener)) {
            return false;
        }
        final RemoteServerPostListener other = (RemoteServerPostListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(this.remoteUrl, other.getRemoteUrl());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof RemoteServerPostListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", remoteUrl = \"%s\"%s",
                                            remoteUrl,
                                            data));
    }

}
