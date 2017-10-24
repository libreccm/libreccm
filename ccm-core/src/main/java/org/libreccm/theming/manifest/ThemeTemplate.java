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

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlRootElement(name = "template", namespace = "http://themes.libreccm.org")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThemeTemplate implements Serializable {

    private static final long serialVersionUID = -9034588759798295569L;

    @XmlElement(name = "name", namespace = "http://themes.libreccm.org")
    private String name;

    @XmlElement(name = "title", namespace = "http://themes.libreccm.org")
    private LocalizedString title;

    @XmlElement(name = "description", namespace = "http://themes.libreccm.org")
    private LocalizedString description;

    @XmlElement(name = "path", namespace = "http://themes.libreccm.org")
    private String path;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(name);
        hash = 67 * hash + Objects.hashCode(title);
        hash = 67 * hash + Objects.hashCode(description);
        hash = 67 * hash + Objects.hashCode(path);
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
        if (!(obj instanceof ThemeTemplate)) {
            return false;
        }
        final ThemeTemplate other = (ThemeTemplate) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(path, other.getPath())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        return Objects.equals(description, other.getDescription());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ThemeTemplate;
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "name = \"%s\", "
                                 + "title = %s, "
                                 + "description = %s, "
                                 + "path = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             name,
                             Objects.toString(title),
                             Objects.toString(description),
                             path,
                             data);
    }

}
