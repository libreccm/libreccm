/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.profilesite;

import org.hibernate.annotations.Type;
import org.librecms.assets.Person;
import org.librecms.contentsection.ContentItem;

import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.profilesite.ProfileSiteConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "PROFILE_SITES", schema = DB_SCHEMA)
public class ProfileSiteItem extends ContentItem {

    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name = "OWNER_ID")
    private Person owner;

    @Column(name = "POSITION")
    @Basic
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String position;

    @Column(name = "INTERESTS")
    @Basic
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String interests;

    @Column(name = "MISC")
    @Basic
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String misc;

    public Person getOwner() {
        return owner;
    }

    public void setOwner(final Person owner) {
        this.owner = owner;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(final String position) {
        this.position = position;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(final String interests) {
        this.interests = interests;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(final String misc) {
        this.misc = misc;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (owner != null) {
            hash = 37 * hash + Objects.hashCode(owner.getUuid());
        }
        hash = 37 * hash + Objects.hashCode(position);
        hash = 37 * hash + Objects.hashCode(interests);
        hash = 37 * hash + Objects.hashCode(misc);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProfileSiteItem)) {
            return false;
        }
        final ProfileSiteItem other = (ProfileSiteItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (owner != null
                && other.getOwner() != null
                && !Objects.equals(
                owner.getUuid(), other.getOwner().getUuid()
            )) {
            return false;
        }
        if (!Objects.equals(position, other.getPosition())) {
            return false;
        }
        if (!Objects.equals(interests, other.getInterests())) {
            return false;
        }
        return Objects.equals(misc, other.getMisc());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ProfileSiteItem;
    }

    @Override
    public String toString(final String data) {
        return super.toString(
            String.format(
                ", owner = %s, "
                    + "position = \"%s\", "
                    + "interests = \"%s\", "
                    + "misc = \"%s\"%s",
                Objects.toString(owner),
                position,
                interests,
                misc,
                data
            )
        );
    }

}
