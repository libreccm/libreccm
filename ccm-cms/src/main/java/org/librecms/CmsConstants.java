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
package org.librecms;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CmsConstants {

    public static final String CMS_XML_NS = "http://cms.libreccm.org";

    public static final String DB_SCHEMA = "CCM_CMS";

    public static final String CONTENT_SECTION_APP_TYPE = "org.librecms.contentsection.ContentSection";
    public static final String CONTENT_SECTION_SERVLET_PATH = "/templates/servlet/content-section/*";

    public static final String PRIVILEGE_ADMINISTER_CATEGORIES = "administer_categories";
    public static final String PRIVILEGE_ADMINISTER_CONTENT_TYPES = "administer_content_types";
    public static final String PRIVILEGE_ADMINISTER_LIFECYLES = "administer_lifecyles";
    public static final String PRIVILEGE_ADMINISTER_ROLES = "administer_roles";
    public static final String PRIVILEGE_ADMINISTER_WORKFLOW = "administer_workflow";
    public static final String PRIVILEGE_ITEMS_APPROVE = "approve_items";
    public static final String PRIVILEGE_ITEMS_PUBLISH = "publish_items";
    public static final String PRIVILEGE_ITEMS_CATEGORIZE = "categorize_items";
    public static final String PRIVILEGE_ITEMS_CREATE_NEW = "create_new_items";
    public static final String PRIVILEGE_ITEMS_DELETE = "delete_items";
    public static final String PRIVILEGE_ITEMS_EDIT = "edit_items";
    public static final String PRIVILEGE_ITEMS_PREVIEW = "preview_items";
    public static final String PRIVILEGE_ITEMS_VIEW_PUBLISHED = "view_published_items";
    public static final String PRIVILEGE_APPLY_ALTERNATE_WORKFLOW = "apply_alternate_workflow";

    private CmsConstants() {
        //Nothing
    }

}
