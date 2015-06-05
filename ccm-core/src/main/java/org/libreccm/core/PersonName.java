/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.core;

import static org.libreccm.core.CoreConstants.*;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An embeddable entity representing a person's name.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Embeddable
@XmlRootElement(name = "person-name", namespace = XML_NS)
public class PersonName implements Serializable {

    private static final long serialVersionUID = -5805626320605809172L;

    @Column(name = "title_pre", length = 512)
    @XmlElement(name = "title-pre", namespace = XML_NS)
    private String titlePre;

    @Column(name = "given_name", length = 512)
    @NotBlank
    @XmlElement(name = "given-name", namespace = XML_NS)
    private String givenName;

    @Column(name = "middle_name", length = 512)
    @XmlElement(name = "middle-name", namespace = XML_NS)
    private String middleName;

    @Column(name = "family_name", length = 512)
    @NotBlank
    @XmlElement(name = "family-name", namespace = XML_NS)
    private String familyName;

    @Column(name = "title_post", length = 512)
    @XmlElement(name = "title-post", namespace = XML_NS)
    private String titlePost;

    public String getTitlePre() {
        return titlePre;
    }

    public void setTitlePre(final String titlePre) {
        this.titlePre = titlePre;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(final String familyName) {
        this.familyName = familyName;
    }

    public String getTitlePost() {
        return titlePost;
    }

    public void setTitlePost(final String titlePost) {
        this.titlePost = titlePost;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.titlePre);
        hash = 37 * hash + Objects.hashCode(this.givenName);
        hash = 37 * hash + Objects.hashCode(this.middleName);
        hash = 37 * hash + Objects.hashCode(this.familyName);
        hash = 37 * hash + Objects.hashCode(this.titlePost);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersonName)) {
            return false;
        }
        final PersonName other = (PersonName) obj;
        if (!Objects.equals(this.titlePre, other.getTitlePre())) {
            return false;
        }
        if (!Objects.equals(this.givenName, other.getGivenName())) {
            return false;
        }
        if (!Objects.equals(this.middleName, other.getMiddleName())) {
            return false;
        }
        if (!Objects.equals(this.familyName, other.getFamilyName())) {
            return false;
        }
        return Objects.equals(this.titlePost, other.getTitlePost());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "titlePre = \"%s\", "
                                 + "givenName = \"%s\", "
                                 + "middleName = \"%s\", "
                                 + "familyName = \"%s\", "
                                 + "titlePost = \"%s\""
                                 + " }",
                             super.toString(),
                             titlePre,
                             givenName,
                             middleName,
                             familyName,
                             titlePost);
    }

}
