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

import static org.libreccm.categorization.CategorizationConstants.*;
import static org.libreccm.core.CoreConstants.*;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.libreccm.core.CcmObject;
import org.libreccm.core.DefaultEntityGraph;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A domain is collection of categories designed a specific purpose. This entity
 * replaces the {@code Domain} entity from the old {@code ccm-ldn-terms} module
 * as well as the {@code CategoryPurpose} entity from the old
 * {@code ccm-core module}.
 *
 * A {@code Domain} can be mapped to multiple {@link CcmApplication}s. Normally
 * This is used to make a {@code Domain} available in the application. The
 * {@link CcmApplication}s to which a {@code Domain} is mapped are called
 * <em>owners</em> of the domain.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CATEGORY_DOMAINS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(name = "Domain.findByKey",
                query = "SELECT d FROM Domain d WHERE d.domainKey = :key"),
    @NamedQuery(name = "Domain.findByUri",
                query = "SELECT d FROM Domain d WHERE d.uri = :uri"),
    @NamedQuery(name = "Domain.findAll",
                query = "SELECT d FROM Domain d ORDER BY d.domainKey"),
    @NamedQuery(
        name = "Domain.search",
        query
            = "SELECT d FROM Domain d "
                  + "WHERE d.domainKey LIKE CONCAT (LOWER(:term), '%') "
                  + "OR d.uri LIKE CONCAT (LOWER(:term), '%') "
                  + "ORDER BY d.domainKey")
})
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "Domain.allCategories",
        attributeNodes = {
            @NamedAttributeNode(value = "root",
                                subgraph = "subCategories")},
        subgraphs = {
            @NamedSubgraph(
                name = "subCategories",
                attributeNodes = {
                    @NamedAttributeNode("subCategories")
                }
            )
        }
    ),
    @NamedEntityGraph(
        name = "Domain.withOwners",
        attributeNodes = {
            @NamedAttributeNode(value = "owners")
        }
    )
})
@DefaultEntityGraph("Domain.allCategories")
@XmlRootElement(name = "domain", namespace = CAT_XML_NS)
public class Domain extends CcmObject implements Serializable {

    private static final long serialVersionUID = 4012590760598188732L;

    /**
     * A unique identifier for the {@code Domain}. This should be short string
     * without special characters or spaces, for example {@code APLAWS-NAV} or
     * {@code MYNAV}.
     */
    @Column(name = "DOMAIN_KEY", nullable = false, unique = true, length = 255)
    @NotBlank
    @Pattern(regexp = "[\\w-.]*")
    @XmlElement(name = "domain-key", namespace = CAT_XML_NS)
    private String domainKey;

    /**
     * An unique URI identifying the domain. It is not required that this domain
     * points to a real resource, it primary purpose is provide a unique
     * identifier. for the domain. If you create your own category system you
     * should use the top level domain of your organisation. Also the URI should
     * include the domain key in lower case letters. For example if the domain
     * key is {@code EXAMPLE-NAV}, than the URI can be
     *
     * <pre>
     * http://example.org/domains/example-nav
     * </pre>
     */
    @Column(name = "URI", nullable = true, unique = true, length = 1024)
    @URL
    @XmlElement(name = "uri", namespace = CAT_XML_NS)
    private String uri;

    /**
     * A human readable title for the {@code Domain}. The title can be
     * localised.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "DOMAIN_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}))
    @XmlElement(name = "title", namespace = CAT_XML_NS)
    private LocalizedString title;

    /**
     * A description of the domain. The description can be localised.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "DOMAIN_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}))
    @XmlElement(name = "description", namespace = CAT_XML_NS)
    private LocalizedString description;

    /**
     * A version string for the {@code Domain}.
     */
    @Column(name = "VERSION", nullable = true)
    @NotBlank
    @XmlElement(name = "version", namespace = CAT_XML_NS)
    private String version;

    /**
     * A timestamp for the release date of the {@code Domain}.
     */
    @Column(name = "RELEASED")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlElement(name = "released", namespace = CAT_XML_NS)
    private Date released;

    /**
     * The root category of the domain.
     */
    @ManyToOne
    @JoinColumn(name = "ROOT_CATEGORY_ID")
    @XmlElement(name = "root", namespace = CAT_XML_NS)
    private Category root;

    /**
     * The owners of the domain.
     */
    @OneToMany(mappedBy = "domain")
    @XmlElementWrapper(name = "owners", namespace = CAT_XML_NS)
    private List<DomainOwnership> owners;

    public Domain() {
        super();
        title = new LocalizedString();
        description = new LocalizedString();
        owners = new ArrayList<>();
    }

    public String getDomainKey() {
        return domainKey;
    }

    public void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
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

    public void setVersion(final String version) {
        this.version = version;
    }

    public Date getReleased() {
        if (released == null) {
            return null;
        } else {
            return new Date(released.getTime());
        }
    }

    public void setReleased(final Date released) {
        if (released == null) {
            this.released = null;
        } else {
            this.released = new Date(released.getTime());
        }
    }

    public Category getRoot() {
        return root;
    }

    protected void setRoot(final Category root) {
        this.root = root;
    }

    /**
     * Returns an <strong>unmodifiable</strong> list of the owners of this
     * {@code Domain}. To add or remove owners use the methods provided by the
     * {@link DomainManager}.
     *
     * @return An <strong>unmodifiable</strong> list of the owners of this {
     *
     * @Domain}
     *
     * @see #owners
     */
    public List<DomainOwnership> getOwners() {
        return Collections.unmodifiableList(owners);
    }

    /**
     * <strong>Internal</strong> method for setting the list of owners.
     *
     * @param owners A list of owners.
     */
    protected void setOwners(final List<DomainOwnership> owners) {
        this.owners = owners;
    }

    /**
     * <strong>Internal</strong> method for adding a {@link DomainOwnership}. To
     * add or remove owners use the methods provided by the
     * {@link DomainManager}.
     *
     * @param owner The domain ownership to add.
     */
    protected void addOwner(final DomainOwnership owner) {
        owners.add(owner);
    }

    /**
     * <strong>Internal</strong> method for removing a {@link DomainOwnership}.
     * To add or remove owners use the methods provided by the
     * {@link DomainManager}.
     *
     * @param owner The domain ownership to add.
     */
    protected void removeOwner(final DomainOwnership owner) {
        owners.remove(owner);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
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
    @SuppressWarnings({"PMD.NPathComplexity",
                       "PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity"})
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Domain)) {
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
            Objects.toString(uri),
            Objects.toString(title),
            version,
            released,
            Objects.toString(root),
            data
        );
    }

}
