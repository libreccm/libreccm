/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.util;



/**
 * <p>Security class used for checking and granting privileges in
 * CMS.</p>
 *
 * @author Michael Pih
 * @version $Revision: #7 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: SecurityConstants.java 2090 2010-04-17 08:04:14Z pboy $
 */
public interface SecurityConstants {

    // CMS Actions
    public final static String STAFF_ADMIN = "staff_admin";
    public final static String WORKFLOW_ADMIN = "workflow_admin";
    public final static String CATEGORY_ADMIN = "category_admin";
    public final static String LIFECYCLE_ADMIN = "lifecycle_admin";
    public final static String CONTENT_TYPE_ADMIN = "content_type_admin";
    public final static String PUBLISH = "publish";
    public final static String NEW_ITEM = "new_item";
    public final static String PUBLIC_PAGES = "public_pages";
    public final static String PREVIEW_PAGES = "preview_pages";
    public final static String ADMIN_PAGES = "admin_pages";
    public final static String EDIT_ITEM = "edit_item";
    public final static String SCHEDULE_PUBLICATION = "schedule_publication";
    public final static String DELETE_ITEM = "delete_item";
    public final static String APPLY_WORKFLOW = "apply_workflow";
    public final static String CATEGORIZE_ITEMS = "categorize_items";
    public final static String DELETE_IMAGES = "delete_images";
    public final static String APPLY_ALTERNATE_WORKFLOWS = "apply_alternate_workflows";

    // CMS Privileges
    public final static String CMS_APPLY_ALTERNATE_WORKFLOWS = "cms_apply_alternate_workflows";
    public final static String CMS_CATEGORIZE_ITEMS = "cms_categorize_items";
    public final static String CMS_CATEGORY_ADMIN = "cms_category_admin";
    public final static String CMS_CONTENT_TYPE_ADMIN = "cms_content_type_admin";
    public final static String CMS_DELETE_ITEM = "cms_delete_item";
    public final static String CMS_EDIT_ITEM = "cms_edit_item";
    public final static String CMS_ITEM_ADMIN = "cms_item_admin";
    public final static String CMS_LIFECYCLE_ADMIN = "cms_lifecycle_admin";
    public final static String CMS_NEW_ITEM = "cms_new_item";
    public final static String CMS_PREVIEW_ITEM = "cms_preview_item";
    public final static String CMS_PUBLISH = "cms_publish";
    public final static String CMS_APPROVE_ITEM = "cms_approve_item";
    public final static String CMS_READ_ITEM = "cms_read_item";
    public final static String CMS_STAFF_ADMIN = "cms_staff_admin";
    public final static String CMS_WORKFLOW_ADMIN = "cms_workflow_admin";
}
