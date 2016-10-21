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

    public static final String CMS_BUNDLE = "org.librecms.CmsResources";
    public static final String CMS_FOLDER_BUNDLE
                               = "com.arsdigita.cms.ui.folder.CMSFolderResources";

    public static final String CONTENT_CENTER_APP_TYPE
                               = "com.arsdigita.cms.ContentCenter";
    public static final String CONTENT_CENTER_URL = "/content-center/";
    public static final String CONTENT_CENTER_DESC_BUNDLE
                               = "org.librecms.contentcenter.ContentCenterResources";

    public static final String CONTENT_SECTION_APP_TYPE
                                   = "org.librecms.contentsection.ContentSection";
    public static final String CONTENT_SECTION_SERVLET_PATH
                                   = "/templates/servlet/content-section/*";
    public static final String CONTENT_SECTION_DESC_BUNDLE
                                   = "org.librecms.contentsection.ContentSectionResources";

    public static final String CONTENT_SECTION_PAGE = "/admin";
    public static final String CONTENT_SECTION_ITEM_PAGE = "/item";

    public static final String CATEGORIZATION_TYPE_FOLDER = "folder";

    /**
     * Constant string used as key for creating service package as a legacy
     * application.
     */
    public static final String SERVICE_PACKAGE_KEY = "cms-service";
    public static final String SERVICE_URL = "/cms-service/";

    public static final String ASSET_ID = "asset_id";
    public static final String IMAGE_ID = "image_id";

    private CmsConstants() {
        //Nothing
    }

}
