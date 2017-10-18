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
package org.librecms.pagemodel;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A component for showing a content item which is assigned to a category.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CATEGORIZED_ITEM_COMPONENT", schema = DB_SCHEMA)
public class CategorizedItemComponent extends ContentItemComponent {
    
    private static final long serialVersionUID = 6366311513244770272L;
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof CategorizedItemComponent;
    }
    
}
