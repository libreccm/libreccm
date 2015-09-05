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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.libreccm.core.CcmObject;

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.web.Application;


/**
 * Association class for the association between a {@link Domain} and a 
 * {@link CcmObject}. Instances of this class should not be created manually.
 * Instead the methods provided by the {@link DomainManager} manager class 
 * should be used.
 * 
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "domain_ownerships", schema = DB_SCHEMA)
public class DomainOwnership implements Serializable {

    private static final long serialVersionUID = 201504301305L;

    /**
     * The ID of this domain ownership.
     */
    @Id
    @Column(name = "ownership_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ownershipId;

    /**
     * The {@link CcmObject} owning the {@link Domain}.
     */
    @ManyToOne(optional = false)
    private Application owner;

    /**
     * The {@link Domain} owned by the {@link CcmObject}.
     */
    @ManyToOne(optional = false)
    private Domain domain;

    /**
     * The context for the domain mapping.
     */
    @Column(name = "context")
    private String context;

    /**
     * Defines the order in which the owning {@link CcmObject}s of a 
     * {@link Domain} are shown.
     */
    @Column(name = "owner_order")
    private long ownerOrder;

    /**
     * Defines the order in which the {@link Domain}s owned by a 
     * {@link CcmObject} are shown.
     */
    @Column(name = "domain_order")
    private long domainOrder;

    public long getOwnershipId() {
        return ownershipId;
    }

    public void setOwnershipId(final long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public Application getOwner() {
        return owner;
    }

    protected void setOwner(final Application owner) {
        this.owner = owner;
    }

    public Domain getDomain() {
        return domain;
    }

    protected void setDomain(final Domain domain) {
        this.domain = domain;
    }

    public String getContext() {
        return context;
    }

    public void setContext(final String context) {
        this.context = context;
    }

    public long getDomainOrder() {
        return domainOrder;
    }

    public void setDomainOrder(final long domainOrder) {
        this.domainOrder = domainOrder;
    }

    public long getOwnerOrder() {
        return ownerOrder;
    }

    public void setOwnerOrder(final long ownerOrder) {
        this.ownerOrder = ownerOrder;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (ownershipId ^ (ownershipId >>> 32));
        hash = 59 * hash + Objects.hashCode(owner);
        hash = 59 * hash + Objects.hashCode(domain);
        hash = 59 * hash + Objects.hashCode(context);
        hash = 59 * hash + (int) (domainOrder ^ (domainOrder >>> 32));
        hash = 59 * hash + (int) (ownerOrder ^ (ownerOrder >>> 32));
        return hash;
    }

    @Override
    @SuppressWarnings({"PMD.NPathComplexity",
                       "PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity"})
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DomainOwnership)) {
            return false;
        }
        final DomainOwnership other = (DomainOwnership) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (ownershipId != other.getOwnershipId()) {
            return false;
        }
        if (!Objects.equals(owner, other.getOwner())) {
            return false;
        }
        if (!Objects.equals(domain, other.getDomain())) {
            return false;
        }
        if (!Objects.equals(context, other.getContext())) {
            return false;
        }
        if (domainOrder != other.getDomainOrder()) {
            return false;
        }
        
        return ownerOrder == other.getOwnerOrder();
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof DomainOwnership;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                     + "ownershipId = %d, "
                                     + "owner = %s, "
                                     + "domain = %s, "
                                     + "context = \"%s\", "
                                     + "domainOrder = %d"
                                     + "ownerOrder = %d"
                                     + "%s }",
                             super.toString(),
                             ownershipId,
                             Objects.toString(owner),
                             Objects.toString(domain),
                             context,
                             domainOrder,
                             ownerOrder,
                             data);
    }
}
