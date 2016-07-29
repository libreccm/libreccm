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
package org.librecms.contentsection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public enum ContentItemVersion {

    /**
     * A draft version is only visible to authenticated users with the
     * appropriate permissions. The draft version is also the version which is
     * edited by the authors.
     */
    DRAFT,
    /**
     * This version is assigned to the live copy by the
     * {@link ContentItemManager} while the item is published.
     */
    PUBLISHING,
    /**
     * A published version which is not yet visible because its lifecycle
     * defines a later date.
     */
    PENDING,
    /**
     * The live version of a content item is the one which is visible to
     * most/all users. The live version is basically a copy of the state of the
     * draft version of a content item on a specific time.
     */
    LIVE

}
