/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.pagemodel.styles;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.libreccm.core.CoreConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "STYLE_RULES", schema = CoreConstants.DB_SCHEMA)
public class Rule implements Serializable {

    private static final long serialVersionUID = -4027217790520373364L;

    @Id
    @Column(name = "RULE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ruleId;

    @Column(name = "SELECTOR", length = 2048)
    private String selector;

    @OneToMany
    @JoinColumn(name = "RULE_ID")
    @Cascade({CascadeType.ALL})
    private List<CssProperty> properties;

    public Rule() {
        properties = new ArrayList<>();
    }

    public long getRuleId() {
        return ruleId;
    }

    protected void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(final String selector) {
        this.selector = selector;
    }

    public List<CssProperty> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    public void setProperties(final List<CssProperty> properties) {
        this.properties = new ArrayList<>(properties);
    }

    public void addProperty(final CssProperty property) {
        properties.add(property);
    }
    
    public void removeProperties(final CssProperty property) {
        properties.remove(property);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (ruleId ^ (ruleId >>> 32));
        hash = 29 * hash + Objects.hashCode(selector);
        hash = 29 * hash + Objects.hashCode(properties);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Rule)) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (ruleId != other.getRuleId()) {
            return false;
        }
        if (!Objects.equals(selector, other.getSelector())) {
            return false;
        }
        return Objects.equals(properties, other.getProperties());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Rule;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "ruleId = %d, "
                                 + "selector = \"%s\", "
                                 + "properties = %s%s"
                                 + " }",
                             super.toString(),
                             ruleId,
                             selector,
                             Objects.toString(properties),
                             data);
    }

    public String toCss() {

        final String propertiesCss = properties
            .stream()
            .map(CssProperty::toCss)
            .collect(Collectors.joining(";\n\t"));

        return String.format("%s {%n"
                                 + "%s%n"
                                 + "}%n",
                             selector,
                             propertiesCss);
    }

}
