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
package org.librecms.contentsection.privileges;

import org.librecms.contentsection.ContentItem;

/**
 * Constants for privileges allowing actions on the items of a content section.
 * All privileges defined in this class can either be assigned for the complete 
 * {@link ContentSection} or for a specific documents/items {@link Folder}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ItemPrivileges {

    /**
     * Allows the user to edit the permissions for items.
     */
    public static final String ADMINISTER = "administer_items";
    
    /**
     * Allows the user to approve {@link ContentItem}s.
     */
    public static final String APPROVE = "approve_items";
    /**
     * Allows the user to publish, republish and unpublish {@link ContentItem}.
     */
    public static final String PUBLISH = "publish_items";
    /**
     * Allows the user to categorise {@link ContentItem}s.
     */
    public static final String CATEGORIZE = "categorize_items";
    /**
     * Allows the user to create new {@link ContentItem}s.
     */
    public static final String CREATE_NEW = "create_new_items";
    /**
     * Allows the user to delete {@link ContentItem}s.
     */
    public static final String DELETE = "delete_items";
    /**
     * Allows the user to edit existing {@link ContentItem}s.
     */
    public static final String EDIT = "edit_items";
    /**
     * Allows to user to view the draft version of {@link ContentItem}.
     */
    public static final String PREVIEW = "preview_items";
    /**
     * Allows the user to view the live version of {@link ContentItems}.
     */
    public static final String VIEW_PUBLISHED = "view_published_items";
    /**
     * Allows the user to apply another {@link Workflow} than the default one to
     * an {@link ContentItem}.
     */
    public static final String APPLY_ALTERNATE_WORKFLOW
                                   = "apply_alternate_workflow";

    private ItemPrivileges() {
        //Nothing
    }

}
