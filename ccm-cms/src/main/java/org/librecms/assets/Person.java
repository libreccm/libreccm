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

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * An  asset representing a person.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PERSONS", schema = DB_SCHEMA)
public class Person extends ContactableEntity {

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
     * Any suffixes to the name of the person. Examples for suffixes are 
     * PhD, or especially for Great Britain the membership in various orders, 
     * for example KBE or CBE.
     */
    @Column(name = "SUFFIX")
    private String suffix;

    /**
     * The birthdate of the person.
     */
    @Column(name = "BIRTHDATA")
    private LocalDate birthdate;

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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(final LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(surname);
        hash = 37 * hash + Objects.hashCode(givenName);
        hash = 37 * hash + Objects.hashCode(prefix);
        hash = 37 * hash + Objects.hashCode(suffix);
        hash = 37 * hash + Objects.hashCode(birthdate);
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
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Person)) {
            return false;
        }
        final Person other = (Person) obj;
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
        if (!Objects.equals(suffix, other.getSuffix())) {
            return false;
        }
        return Objects.equals(birthdate, other.getBirthdate());
    }

    @Override
    public boolean canEqual(final Object obj) {

        return obj instanceof Person;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            "surname = \"%s\", "
                + "givenName = \"%s\", "
                + "prefix = \"%s\", "
                + "suffix = \"%s\", "
                + "birthdate = %s%s",
            surname,
            givenName,
            prefix,
            suffix,
            Objects.toString(birthdate),
            data));
    }

}
