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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "hosts",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"server_name", "server_port"})})
public class Host implements Serializable {

    private static final long serialVersionUID = 8727376444061847375L;

    @Id
    @Column(name = "host_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long hostId;

    @Column(name = "server_name", length = 512)
    private String serverName;

    @Column(name = "server_port")
    private long serverPort;

    public long getHostId() {
        return hostId;
    }

    public void setHostId(final long hostId) {
        this.hostId = hostId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    public long getServerPort() {
        return serverPort;
    }

    public void setServerPort(final long serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (int) (this.hostId ^ (this.hostId >>> 32));
        hash = 79 * hash + Objects.hashCode(this.serverName);
        hash = 79 * hash + (int) (this.serverPort ^ (this.serverPort >>> 32));
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Host)) {
            return false;
        }
        final Host other = (Host) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.hostId != other.getHostId()) {
            return false;
        }
        if (!Objects.equals(this.serverName, other.getServerName())) {
            return false;
        }
        return this.serverPort == other.getServerPort();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Host;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "hostId = %d, "
                                 + "serverName = \"%s\", "
                                 + "serverPort  =\"%s\""
                                 + " }",
                             super.toString(),
                             hostId,
                             serverName,
                             serverPort);
    }

}
