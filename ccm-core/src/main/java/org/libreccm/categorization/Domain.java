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
package org.libreccm.categorization;

import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "domains")
public class Domain extends CcmObject implements Serializable {

    private static final long serialVersionUID = 4012590760598188732L;

    @Column(name = "domain_key", nullable = false, unique = true)
    private String domainKey;

    @Column(name = "uri", nullable = false, unique = true)
    private URI uri;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "domain_titles",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}))
    private LocalizedString title;
    
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "domain_descriptions",
                               joinColumns = {
                                   @JoinColumn(name = "object_id")}))
    private LocalizedString description;
    
    @Column(name = "version", nullable = false)
    private String version;
    
    @Column(name = "released")
    @Temporal(TemporalType.TIMESTAMP)
    private Date released;
    
    @ManyToOne
    private Category root;

    public String getDomainKey() {
        return domainKey;
    }

    public void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getReleased() {
        return new Date(released.getTime());
    }

    public void setReleased(final Date released) {
        this.released = new Date(released.getTime());
    }

    public Category getRoot() {
        return root;
    }

    protected void setRoot(final Category root) {
        this.root = root;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(domainKey);
        hash = 41 * hash + Objects.hashCode(uri);
        hash = 41 * hash + Objects.hashCode(title);
        hash = 41 * hash + Objects.hashCode(description);
        hash = 41 * hash + Objects.hashCode(version);
        hash = 41 * hash + Objects.hashCode(released);
        hash = 41 * hash + Objects.hashCode(root);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Domain other = (Domain) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        
        if (!Objects.equals(domainKey, other.getDomainKey())) {
            return false;
        }
        if (!Objects.equals(uri, other.getUri())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(version, other.getVersion())) {
            return false;
        }
        if (!Objects.equals(released, other.getReleased())) {
            return false;
        }
        return Objects.equals(root, other.getRoot());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Domain;
    }
    
    @Override
    public String toString(final String data) {
        return String.format(
            ", domainKey = \"%s\", "
                + "uri = \"%s\", "
                + "title = \"%s\", "
                + "version = \"%s\", "
                + "released = %tF %<tT, "
                + "root = \"%s\"%s",
            domainKey,
            uri.toString(),
            title.toString(),
            version,
            released,
            root.toString(),
            data
        );
    }

}
