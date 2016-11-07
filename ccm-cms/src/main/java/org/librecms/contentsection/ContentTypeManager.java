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
package org.librecms.contentsection;

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.LifecycleDefinition;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Methods for managing the default lifecycle and workflow of a content type.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentTypeManager {

    @Inject
    private ContentTypeRepository typeRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void setDefaultLifecycle(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final LifecycleDefinition definition) {

        type.setDefaultLifecycle(definition);

        typeRepo.save(type);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    public void setDefaultWorkflow(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type,
        final WorkflowTemplate template) {

        type.setDefaultWorkflow(template);
        
        typeRepo.save(type);
    }

}
