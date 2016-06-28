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
package org.librecms.contenttypes;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import static org.libreccm.core.CoreConstants.*;

/**
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "ARTICLES", schema = DB_SCHEMA)
public class Article extends GenericArticle implements Serializable {

    private static final long serialVersionUID = 3832010184748095822L;

    @Embedded
    @AssociationOverride(
            name = "VALUES",
            joinTable = @JoinTable(name = "ARTICLE_LEADS",
                                   schema = DB_SCHEMA,
                                   joinColumns = {
                                       @JoinColumn(name = "OBJECT_ID")}
            ))
    private LocalizedString lead;

    public LocalizedString getLead() {
        return lead;
    }

    public void setLead(final LocalizedString lead) {
        this.lead = lead;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(lead);
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

        if (!(obj instanceof Article)) {
            return false;
        }
        final Article other = (Article) obj;
        if (!(other.canEqual(this))) {
            return false;
        }

        return Objects.equals(lead, other.getLead());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Article;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", lead = %s%s",
                                            Objects.toString(lead),
                                            data));
    }

}
