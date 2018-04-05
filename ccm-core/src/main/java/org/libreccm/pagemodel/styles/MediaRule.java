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
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "STYLE_MEDIA_RULES", schema = CoreConstants.DB_SCHEMA)
public class MediaRule implements Serializable {

    private static final long serialVersionUID = -5776387865481417402L;

    @Id
    @Column(name = "MEDIA_RULE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long mediaRuleId;

    @OneToOne
    @JoinColumn(name = "MEDIA_QUERY_ID")
     @Cascade(CascadeType.ALL)
    private MediaQuery mediaQuery;

    @OneToMany
    @JoinColumn(name = "STYLE_ID")
    @Cascade(CascadeType.ALL)
    private List<Rule> rules;

    public long getMediaRuleId() {
        return mediaRuleId;
    }

    protected void setMediaRuleId(final long mediaRuleId) {
        this.mediaRuleId = mediaRuleId;
    }

    public MediaQuery getMediaQuery() {
        return mediaQuery;
    }

    public void setMediaQuery(final MediaQuery mediaQuery) {
        this.mediaQuery = mediaQuery;
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public void setRules(final List<Rule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public void addRule(final Rule rule) {
        rules.add(rule);
    }

    public void removeRule(final Rule rule) {
        rules.remove(rule);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int) (mediaRuleId ^ (mediaRuleId >>> 32));
        hash = 67 * hash + Objects.hashCode(mediaQuery);
        hash = 67 * hash + Objects.hashCode(rules);
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
        if (!(obj instanceof MediaRule)) {
            return false;
        }
        final MediaRule other = (MediaRule) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (mediaRuleId != other.getMediaRuleId()) {
            return false;
        }
        if (!Objects.equals(mediaQuery, other.getMediaQuery())) {
            return false;
        }
        return Objects.equals(rules, other.getRules());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof MediaRule;
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "mediaRuleId = %d, "
                                 + "mediaQuery = %s, "
                                 + "rules = %s%s"
                                 + " }",
                             super.toString(),
                             mediaRuleId,
                             Objects.toString(mediaQuery),
                             Objects.toString(rules),
                             data);
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toCss() {

        final String rulesCss = rules
            .stream()
            .map(Rule::toCss)
            .collect(Collectors.joining(";\n%t"));

        return String.format("%s {%n"
                                 + "%s%n"
                                 + "}%n",
                             mediaQuery.toCss(),
                             rulesCss);
    }

}
