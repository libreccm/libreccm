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

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "theme", namespace = THEMES_XML_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class ThemeManifest {

    @XmlElement(name = "name", namespace = THEMES_XML_NS)
    private String name;
    
    @XmlElement(name = "type", namespace = THEMES_XML_NS)
    private String type;

    @XmlElement(name = "title", namespace = THEMES_XML_NS)
    private LocalizedString title;

    @XmlElement(name = "description", namespace = THEMES_XML_NS)
    private LocalizedString description;

    @XmlElementWrapper(name = "templates", namespace = THEMES_XML_NS)
    @XmlElement(name = "template", namespace = THEMES_XML_NS)
    private List<ThemeTemplate> templates;

    public ThemeManifest() {
        templates = new ArrayList<>();
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
        this.type  =type;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(name);
        hash = 83 * hash + Objects.hashCode(type);
        hash = 83 * hash + Objects.hashCode(title);
        hash = 83 * hash + Objects.hashCode(description);
        hash = 83 * hash + Objects.hashCode(templates);
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
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        return Objects.equals(templates, other.getTemplates());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeManifest;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "title = \"%s\", "
                                 + "description = \"%s\", "
                                 + "templates = %s%s"
                                 + " }",
                             super.toString(),
                             name,
                             Objects.toString(title),
                             Objects.toString(description),
                             Objects.toString(templates),
                             data);

    }

}
