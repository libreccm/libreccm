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
package org.librecms.contenttypes.glossaryitem;

import static org.librecms.contenttypes.glossaryitem.GlossaryitemConstants.*;

import org.hibernate.envers.Audited;
import org.librecms.contentsection.ContentItem;

import java.io.Serializable;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;

/**
 * This content type represents a GlossaryItem.
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 25/11/2015
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Glossaryitem extends ContentItem implements Serializable {

    @Column(name = "DEFINITION")
    @NotEmpty
    private LocalizedString definition;

    //Getter and setter:
    public LocalizedString getDefinition() {
        return definition;
    }

    public void setDefinition(LocalizedString definition) {
        this.definition = definition;
    }

}
