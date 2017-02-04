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
package org.libreccm.security;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.core.CcmObject;

/**
 * If N:M or relation with attributes is annotated with
 * {@link RecursivePermissions} and the relation object is not a
 * {@link CcmObject} the relation object must implement this interface.
 *
 * An example are {@link Category#objects} and {@link Categorization}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface Relation {

    /**
     * Get the owning object of the relation.
     *
     * @return
     */
    CcmObject getOwner();

    /**
     * Get the related object of the relation.
     *
     * @return
     */
    CcmObject getRelatedObject();

}
