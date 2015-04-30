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
package org.libreccm.categorization;

import org.libreccm.core.CcmObject;

import java.io.Serializable;
import java.util.List;
import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import org.libreccm.l10n.LocalizedString;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "categories")
public class Category extends CcmObject implements Serializable {

    private static final long serialVersionUID = -7250208963391878547L;

    @Column(name = "unique_id", nullable = false)
    private String uniqueId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "category_titles",
                                   joinColumns = {
                                       @JoinColumn(name = "object_id")}
            ))
    private LocalizedString title;
    
    @Embedded
    @AssociationOverride(
            name = "values",
            joinTable = @JoinTable(name = "category_descriptions",
                                   joinColumns = {
                                       @JoinColumn(name = "object_id")}
            ))
    private LocalizedString description;
    
    @Column(name = "enabled")
    private boolean enabled;
    
    @Column(name  = "visible")
    private boolean visible;
    
    @Column(name = "abstract_category")
    private boolean abstractCategory;

    
    private List<CcmObject> owners;
    private List<CcmObject> objects;
    private List<Category> subCategories;
    private Category parentCategory;

}
