/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import org.librecms.contentsection.Asset;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * Assets for side notes (additional informations) for a content item.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "SIDE_NOTES", schema = DB_SCHEMA)
@Audited
public class SideNote extends Asset implements Serializable {

    private static final long serialVersionUID = -4566222634780521726L;

    @Embedded
    @AssociationOverride(
    name = "values",
    joinTable = @JoinTable(name = "SIDE_NOTE_TITLES",
                           schema = DB_SCHEMA,
                           joinColumns = {
                               @JoinColumn(name = "SIDE_NOTE_ID")
                           }))
    private LocalizedString title;
    
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "SIDE_NOTE_TEXTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "SIDE_NOTE_ID")
                               }
        )
    )
    private LocalizedString text;

    public SideNote() {
        super();
        text = new LocalizedString();
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 11 * hash + Objects.hashCode(text);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof SideNote)) {
            return false;
        }
        final SideNote other = (SideNote) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(text, other.getText());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof SideNote;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", text = %s%s",
                                            Objects.toString(text),
                                            data));
    }

}
