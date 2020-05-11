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
package org.libreccm.security;

import static org.libreccm.core.CoreConstants.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.core.api.JsonArrayCollector;

import javax.persistence.FetchType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Party is a base class for {@link User} and {@link Group} defining some common
 * characteristics and associations, especially the association to
 * {@link Role}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PARTIES", schema = DB_SCHEMA)
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQueries({
    @NamedQuery(
        name = "Party.findByUuid",
        query = "SELECT p FROM Party p WHERE p.uuid = :uuid"
    ),
    @NamedQuery(
        name = "Party.findByName",
        query = "SELECT p FROM Party p WHERE p.name = :name"),
    @NamedQuery(
        name = "Party.searchByName",
        query = "SELECT p FROM Party p "
                    + "WHERE LOWER(p.name) LIKE CONCAT(LOWER(:term), '%') "
                    + "ORDER BY p.name"),
    @NamedQuery(
        name = "Party.findByRole",
        query = "SELECT p FROM Party p "
                    + "JOIN p.roleMemberships m "
                    + "WHERE m.role = :role")

})
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Party.withRoleMemberships",
                      attributeNodes = @NamedAttributeNode(
                          value = "roleMemberships"))
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = PartyIdResolver.class,
                  property = "uuid")
public class Party implements Serializable {

    private static final long serialVersionUID = 3319997992281332204L;

    @Id
    @Column(name = "PARTY_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlElement(name = "party-id", namespace = CORE_XML_NS)
    private long partyId;

    @Column(name = "UUID", unique = true, nullable = false)
    @XmlElement(name = "uuid", namespace = CORE_XML_NS)
    private String uuid;

    /**
     * The name of the party. Must only contain the letters a to z and A to Z,
     * the numbers 0 to 9 the {@code -} (dash) and the {@code _} (underscore).
     */
    @Column(name = "NAME", length = 256, nullable = false)
    @NotNull
    @XmlElement(name = "name", namespace = CORE_XML_NS)
//    @Pattern(regexp = "[a-zA-Z0-9\\-_\\.]*")
    private String name;

    /**
     * The role memberships the party.
     */
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @XmlElementWrapper(name = "role-memberships", namespace = CORE_XML_NS)
    @XmlElement(name = "role-membership", namespace = CORE_XML_NS)
    @JsonIgnore
    private Set<RoleMembership> roleMemberships = new HashSet<>();

    protected Party() {
        super();
    }

    public long getPartyId() {
        return partyId;
    }

    protected void setPartyId(final long partyId) {
        this.partyId = partyId;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<RoleMembership> getRoleMemberships() {
        if (roleMemberships == null) {
            return null;
        } else {
            return Collections.unmodifiableSet(roleMemberships);
        }
    }

    protected void setRoleMemberships(final Set<RoleMembership> roleMemberships) {
        this.roleMemberships = roleMemberships;
    }

    protected void addRoleMembership(final RoleMembership roleMembership) {
        roleMemberships.add(roleMembership);
    }

    protected void removeRoleMembership(final RoleMembership roleMembership) {
        roleMemberships.remove(roleMembership);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (int) (partyId ^ (partyId >>> 32));
        hash = 37 * hash + Objects.hashCode(name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Party)) {
            return false;
        }
        final Party other = (Party) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (partyId != other.getPartyId()) {
            return false;
        }

        return Objects.equals(name, other.getName());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Party;
    }

    public JsonObjectBuilder buildJson() {
        final JsonArray array = roleMemberships
            .stream()
            .map(RoleMembership::buildJson)
            .map(JsonObjectBuilder::build)
            .collect(new JsonArrayCollector());

        return Json
            .createObjectBuilder()
            .add("partyId", partyId)
            .add("uuid", uuid)
            .add("name", name)
            .add(
                "roleMemberships",
                roleMemberships
                    .stream()
                    .map(RoleMembership::buildJson)
                    .map(JsonObjectBuilder::build)
                    .collect(new JsonArrayCollector())
            );

    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "partyId = %d, "
                                 + "name = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             partyId,
                             name,
                             data);
    }

}
