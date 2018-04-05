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
@Table(name = "STYLES", schema = CoreConstants.DB_SCHEMA)
public class Styles implements Serializable {

    private static final long serialVersionUID = -6166372396205730453L;

    @Id
    @Column(name = "STYLE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long styleId;

    @Column(name = "STYLENAME")
    private String styleName;

    @OneToMany
    @JoinColumn(name = "STYLE_ID")
    @Cascade(CascadeType.ALL)
    private List<Rule> rules;

    @OneToMany
    @JoinColumn(name = "STYLE_ID")
    @Cascade(CascadeType.ALL)
    private List<MediaRule> mediaRules;

    public long getStyleId() {
        return styleId;
    }

    public void setStyleId(long styleId) {
        this.styleId = styleId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(final String styleName) {
        this.styleName = styleName;
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

    public List<MediaRule> getMediaRules() {
        return Collections.unmodifiableList(mediaRules);
    }

    public void setMediaRules(final List<MediaRule> mediaRules) {
        this.mediaRules = new ArrayList<>(mediaRules);
    }

    public void addMediaRule(final MediaRule mediaRule) {
        mediaRules.add(mediaRule);
    }

    public void removeMediaRule(final MediaRule mediaRule) {
        mediaRules.remove(mediaRule);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (styleId ^ (styleId >>> 32));
        hash = 97 * hash + Objects.hashCode(styleName);
        hash = 97 * hash + Objects.hashCode(rules);
        hash = 97 * hash + Objects.hashCode(mediaRules);
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
        if (!(obj instanceof Styles)) {
            return false;
        }
        final Styles other = (Styles) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (styleId != other.getStyleId()) {
            return false;
        }
        if (!Objects.equals(styleName, other.getStyleName())) {
            return false;
        }
        return Objects.equals(rules, other.getRules());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Styles;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "styleId = %d, "
                                 + "styleName = \"%s\", "
                                 + "rules = %s, "
                                 + "mediaRules = %s%s"
                                 + " }",
                             super.toString(),
                             styleId,
                             styleName,
                             Objects.toString(rules),
                             Objects.toString(mediaRules),
                             data);
    }

    public String toCss() {

        final String rulesCss = rules
            .stream()
            .map(Rule::toCss)
            .collect(Collectors.joining(";\n\n"));
        final String mediaRulesCss = mediaRules
            .stream()
            .map(MediaRule::toCss)
            .collect(Collectors.joining(";\n\n"));

        final StringBuilder builder = new StringBuilder();

        return builder
            .append(rulesCss)
            .append(mediaRulesCss)
            .toString();
    }

}
