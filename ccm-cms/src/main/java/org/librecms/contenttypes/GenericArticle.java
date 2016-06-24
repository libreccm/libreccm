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

import static org.librecms.CmsConstants.*;

import org.librecms.contentsection.ContentItem;
import org.libreccm.l10n.LocalizedString;

/**
 * Base class for article like content items. These items usually contain a 
 * short introduction text (the description) and a longer text containing 
 * the more detailed content. 
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "ARTICLES", schema = DB_SCHEMA)
public class GenericArticle extends ContentItem implements Serializable {

    private static final long serialVersionUID = -6737443527969703121L;
    
    @Embedded
    @AssociationOverride(
        name = "VALUES",
        joinTable = @JoinTable(name = "ARTICLE_TEXTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString text;

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 17 * hash + Objects.hashCode(text);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (!super.equals(obj)) {
            return false;
        }
        
        if (!(obj instanceof GenericArticle)) {
            return false;
        }
        final GenericArticle other = (GenericArticle) obj;
        if(!other.canEqual(this)) {
            return false;
        }
        
        return Objects.equals(text, other.getText());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof GenericArticle;
    }

}
