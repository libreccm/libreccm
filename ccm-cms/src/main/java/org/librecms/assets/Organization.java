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

import com.arsdigita.cms.ui.assets.forms.OrganizationForm;

import org.hibernate.envers.Audited;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;
import static org.librecms.assets.AssetConstants.*;

/**
 * A reusable piece of information about an organization.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@AssetType(assetForm = OrganizationForm.class,
           labelBundle = ASSETS_BUNDLE,
           labelKey = "organization.label",
           descriptionBundle = ASSETS_BUNDLE,
           descriptionKey = "organization.description")
@Entity
@Audited
@Table(name = "ORGANIZATIONS", schema = DB_SCHEMA)
public class Organization extends ContactableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME", length = 1024)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(name);
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
        if (!(obj instanceof Organization)) {
            return false;
        }
        final Organization other = (Organization) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(name, other.getName());
    }

    @Override
    public boolean canEqual(final Object obj) {

        return obj instanceof Organization;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(
            "name = \"%s\"%s",
            name,
            data));
    }

}
