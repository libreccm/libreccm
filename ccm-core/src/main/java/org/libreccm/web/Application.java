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

import static org.libreccm.web.WebConstants.*;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.core.Resource;
import org.libreccm.core.Group;
import org.libreccm.jpa.utils.UriConverter;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "applications", schema = "ccm_core")
@XmlRootElement(name = "application", namespace = WEB_XML_NS)
public class Application extends Resource implements Serializable {

    private static final long serialVersionUID = 9205226362368890784L;

    @Column(name = "primary_url", length = 1024, nullable = false)
    @Convert(converter = UriConverter.class)
    @XmlElement(name = "primary-url", namespace = WEB_XML_NS)
    private URI primaryUrl;

    @OneToOne
    @JoinColumn(name = "container_group_id")
    @XmlElement(name = "container-group", namespace = WEB_XML_NS)
    private Group containerGroup;

    /**
     * Category Domains owned by this {@code CcmObject}.
     */
    @OneToMany(mappedBy = "owner")
    @XmlElementWrapper(name = "domains", namespace = WEB_XML_NS)
    @XmlElement(name = "domain", namespace = WEB_XML_NS)
    private List<DomainOwnership> domains;

    public Application() {
        super();
        domains = new ArrayList<>();
    }
    
    public URI getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(final URI primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public Group getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(final Group containerGroup) {
        this.containerGroup = containerGroup;
    }

    /**
     * Gets an <strong>unmodifiable</strong> list of the domains which are owned
     * by the {@code Application}.
     *
     * @return An unmodifiable list of the domain ownerships of this
     *         {@code Application}. Might be {@code null} or empty.
     */
    public List<DomainOwnership> getDomains() {
        return Collections.unmodifiableList(domains);
    }

    /**
     * Setter for the list of domain ownerships, only for use by JPA.
     *
     * @param domains A list of domain ownerships.
     */
    protected void setDomains(final List<DomainOwnership> domains) {
        this.domains = domains;
    }

    /**
     * <strong>Internal</strong> method for adding a domain ownership. Users
     * should use the appropriate methods of the {@link DomainManager} class to
     * manage the {@link Domain}s assigned to {@code CcmObject}.
     *
     * @param domain The domain ownership to add.
     */
    protected void addDomain(final DomainOwnership domain) {
        domains.add(domain);
    }

    /**
     * <strong>Internal</strong> method for removing a domain ownership. Users
     * should use the appropriate methods of the {@link DomainManager} to manage
     * the {@link Domain}s assigned to {@code CcmObject}.
     *
     * @param domain The domain to remove.
     */
    protected void removeDomain(final DomainOwnership domain) {
        domains.remove(domain);
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
