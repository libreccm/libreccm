/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package org.librecms.assets;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
public class PersonName implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The surname/familyname of the person
     */
    @Column(name = "SURNAME")
    private String surname;

    /**
     * The given name of the person.
     */
    @Column(name = "GIVEN_NAME")
    private String givenName;

    /**
     * Any prefixes to the name of the person. Examples are Prof. or Dr.
     */
    @Column(name = "NAME_PREFIX")
    private String prefix;

    /**
     * Any suffixes to the name of the person. Examples for suffixes are PhD, or
     * especially for Great Britain the membership in various orders, for
     * example KBE or CBE.
     */
    @Column(name = "SUFFIX")
    private String suffix;

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(surname);
        hash = 79 * hash + Objects.hashCode(givenName);
        hash = 79 * hash + Objects.hashCode(prefix);
        hash = 79 * hash + Objects.hashCode(suffix);
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
        if (!(obj instanceof PersonName)) {
            return false;
        }
        final PersonName other = (PersonName) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(surname, other.getSurname())) {
            return false;
        }
        if (!Objects.equals(givenName, other.getGivenName())) {
            return false;
        }
        if (!Objects.equals(prefix, other.getPrefix())) {
            return false;
        }
        return Objects.equals(suffix, other.getSuffix());
    }

    public boolean canEqual(final Object obj) {

        return obj instanceof PersonName;
    }

    public String toString(final String data) {

        return String.format("%s{ "
                                 + "surname = \"%s\", "
                                 + "givenName = \"%s\", "
                                 + "prefix = \"%s\", "
                                 + "suffix = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             surname,
                             givenName,
                             prefix,
                             suffix);
    }

}
