/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.libreccm.theming.manifest;

import org.libreccm.l10n.LocalizedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import static org.libreccm.theming.ThemeConstants.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Each theme contains a Manifest (either in XML or JSON format) which provides
 * informations about the theme.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "theme", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class ThemeManifest implements Serializable {

    private static final long serialVersionUID = 699497658459398231L;

    /**
     * The name of the theme. Usually the same as the name of directory which
     * contains the theme.
     */
    @XmlElement(name = "name", namespace = THEMES_XML_NS)
    private String name;

    /**
     * The type of the theme, for example XSLT.
     */
    @XmlElement(name = "type", namespace = THEMES_XML_NS)
    private String type;

    @XmlElement(name = "master-theme", namespace = THEMES_XML_NS)
    private String masterTheme;

    /**
     * The (localised) title of the theme.
     */
    @XmlElement(name = "title", namespace = THEMES_XML_NS)
    private LocalizedString title;

    /**
     * A (localised) description of the theme.
     */
    @XmlElement(name = "description", namespace = THEMES_XML_NS)
    private LocalizedString description;

    /**
     * The templates provided by the theme.
     */
    @XmlElementWrapper(name = "templates", namespace = THEMES_XML_NS)
    @XmlElement(name = "template", namespace = THEMES_XML_NS)
    private List<ThemeTemplate> templates;

    /**
     * Path of the default template.
     */
    @XmlElement(name = "default-template", namespace = THEMES_XML_NS)
    private String defaultTemplate;

    @XmlElementWrapper(name = "mvcTemplates", namespace = THEMES_XML_NS)
    @XmlElement(name = "template", namespace = THEME_MANIFEST_XML)
    private Map<String, Map<String, ThemeTemplate>> mvcTemplates;

    public ThemeManifest() {
        templates = new ArrayList<>();
        mvcTemplates = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getMasterTheme() {
        return masterTheme;
    }

    public void setMasterTheme(final String masterTheme) {
        this.masterTheme = masterTheme;
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

    public List<ThemeTemplate> getTemplates() {
        return Collections.unmodifiableList(templates);
    }

    public void setTemplates(final List<ThemeTemplate> templates) {
        this.templates = new ArrayList<>(templates);
    }

    public void addThemeTemplate(final ThemeTemplate template) {
        templates.add(template);
    }

    public void removeThemeTemplate(final ThemeTemplate template) {
        templates.remove(template);
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(final String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public Map<String, Map<String, ThemeTemplate>> getMvcTemplates() {
        return Collections.unmodifiableMap(mvcTemplates);
    }

    public Optional<Map<String, ThemeTemplate>> getMvcTemplatesOfCategory(
        final String category
    ) {
        return Optional.ofNullable(mvcTemplates.get(category));
    }

    public void addMvcTemplatesCategory(final String category) {
        mvcTemplates.put(category, new HashMap<>());
    }

    public void addMvcTemplatesCategory(
        final String category, final Map<String, ThemeTemplate> templates
    ) {
        mvcTemplates.put(category, templates);
    }

    public Optional<ThemeTemplate> getMvcTemplate(
        final String category, final String objectType
    ) {
        final Optional<Map<String, ThemeTemplate>> templatesInCat
            = getMvcTemplatesOfCategory(category);

        if (templatesInCat.isPresent()) {
            return Optional.ofNullable(templatesInCat.get().get(objectType));
        } else {
            return Optional.empty();
        }
    }

    public void addMvcTemplate(
        final String category,
        final String objectType,
        final ThemeTemplate template
    ) {
        if (!mvcTemplates.containsKey(category)) {
            addMvcTemplatesCategory(category);
        }

        mvcTemplates
            .get(category)
            .put(
                objectType,
                Objects.requireNonNull(
                    template,
                    "Template can't be null."
                )
            );
    }

    protected void setMvcTemplates(
        final Map<String, Map<String, ThemeTemplate>> mvcTemplates
    ) {
        this.mvcTemplates = mvcTemplates;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(name);
        hash = 83 * hash + Objects.hashCode(type);
        hash = 83 * hash + Objects.hashCode(masterTheme);
        hash = 83 * hash + Objects.hashCode(title);
        hash = 83 * hash + Objects.hashCode(description);
        hash = 83 * hash + Objects.hashCode(templates);
        hash = 83 * hash + Objects.hashCode(defaultTemplate);
        hash = 83 * hash + Objects.hashCode(mvcTemplates);
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
        if (!(obj instanceof ThemeManifest)) {
            return false;
        }
        final ThemeManifest other = (ThemeManifest) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        if (!Objects.equals(masterTheme, other.getMasterTheme())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(templates, other.getTemplates())) {
            return false;
        }
        if (!Objects.equals(defaultTemplate, other.getDefaultTemplate())) {
            return false;
        }
        return mvcTemplates.equals(other.getMvcTemplates());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeManifest;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format(
            "%s{ "
                + "name = \"%s\", "
                + "type = \"%s\", "
                + "masterTheme = \"%s\", "
                + "title = \"%s\", "
                + "description = \"%s\", "
                + "templates = %s, "
                + "defaultTemplate, "
                + "mvcTemplates = %s%s"
                + " }",
            super.toString(),
            name,
            type,
            masterTheme,
            Objects.toString(title),
            Objects.toString(description),
            Objects.toString(templates),
            defaultTemplate,
            Objects.toString(mvcTemplates),
            data
        );

    }

}
