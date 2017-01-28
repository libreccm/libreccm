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

import org.libreccm.categorization.Category;
import org.libreccm.categorization.Domain;
import org.libreccm.security.Role;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.lifecycle.LifecycleDefinition;

/**
 * Constants for privileges allowing administrative actions on a content
 * section. The privileges defined in this can only be used for
 * {@link ContentSection}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class AdminPrivileges {

    /**
     * Allows the manipulation of the categories (see {@link Category} of the
     * {@link Domain}s assigned to the {@link ContentSection}.
     *
     * @see CcmApplication#domains
     */
    public static final String ADMINISTER_CATEGORIES = "administer_categories";
    /**
     * Allows editing, adding and removing the {@link ContentType} of a
     * {@link ContentSection}.
     *
     * @see ContentSection#contentTypes
     */
    public static final String ADMINISTER_CONTENT_TYPES
                                   = "administer_content_types";
    /**
     * Allows adding, editing and removing {@link LifecycleDefinition}s of a
     * {@link ContentSection}.
     *
     * @see ContentSection#lifecycleDefinitions
     */
    public static final String ADMINISTER_LIFECYLES = "administer_lifecyles";
    /**
     * Allows manipulation of the {@link Role}s assigned to a
     * {@link ContentSection}.
     *
     * @see ContentSection#roles
     */
    public static final String ADMINISTER_ROLES = "administer_roles";
    /**
     * Allows manipulation of the {@link WorkflowTemplate}s assigned to a
     * {@link ContentSection}.
     * 
     * @see ContentSection#workflowTemplates
     */
    public static final String ADMINISTER_WORKFLOW = "administer_workflow";

    private AdminPrivileges() {
        //Nothing
    }

}
