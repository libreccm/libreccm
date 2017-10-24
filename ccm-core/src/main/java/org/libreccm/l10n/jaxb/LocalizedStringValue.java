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

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalizedStringValue implements Serializable {

    private static final long serialVersionUID = 8435485565736441379L;

    @XmlAttribute(name = "lang", namespace = L10N_XML_NS)
    private String locale;

    @XmlValue
    private String value;

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(locale);
        hash = 97 * hash + Objects.hashCode(value);
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
        if (!(obj instanceof LocalizedStringValue)) {
            return false;
        }
        final LocalizedStringValue other = (LocalizedStringValue) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(locale, other.getLocale())) {
            return false;
        }
        return Objects.equals(value, other.getValue());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof LocalizedStringValue;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "locale = %s, "
                                 + "value = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             Objects.toString(locale),
                             value,
                             data);
    }

}
