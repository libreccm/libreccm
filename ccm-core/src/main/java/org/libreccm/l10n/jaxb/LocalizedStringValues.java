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
package org.libreccm.l10n.jaxb;

import static org.libreccm.l10n.L10NConstants.*;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalizedStringValues implements Serializable {

    private static final long serialVersionUID = 1L;

    @JacksonXmlElementWrapper(useWrapping = false)
    @XmlElement(name = "value", namespace = L10N_XML_NS)
    private List<LocalizedStringValue> values;

    public List<LocalizedStringValue> getValues() {
        return new ArrayList<>(values);
    }

    public void setValues(final List<LocalizedStringValue> values) {
        this.values = new ArrayList<>(values);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(values);
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
        if (!(obj instanceof LocalizedStringValues)) {
            return false;
        }
        final LocalizedStringValues other = (LocalizedStringValues) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(values, other.getValues());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof LocalizedStringValues;
    }

    @Override
    public final String toString() {
        return toString("");
    }
    
    public String toString(final String data) {
        return String.format("%s{ "
                                 + "values = %s%s"
                                 + " }",
                             super.toString(),
                             Objects.toString(values),
                             data);
    }

}
