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

import org.libreccm.pagemodel.ComponentModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A component which shows the category tree. Depending on the parameters set
 * either the complete category tree is shown or the sub tree of the current
 * category.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CATEGORY_TREE_COMPONENTS", schema = DB_SCHEMA)
public class CategoryTreeComponent extends ComponentModel {

    private static final long serialVersionUID = 9142791033478189003L;

    @Column(name = "SHOW_FULL_TREE")
    private boolean showFullTree;

    public boolean isShowFullTree() {
        return showFullTree;
    }

    public void setShowFullTree(final boolean showFullTree) {
        this.showFullTree = showFullTree;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 61 * hash + (showFullTree ? 1 : 0);
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

        if (!(obj instanceof CategoryTreeComponent)) {
            return false;
        }
        final CategoryTreeComponent other = (CategoryTreeComponent) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return showFullTree == other.isShowFullTree();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof CategoryTreeComponent;
    }

    @Override
    public String toString(final String data) {

        return super.toString(String.format(", showFullTree = %b%s",
                                            showFullTree,
                                            data));

    }

}
