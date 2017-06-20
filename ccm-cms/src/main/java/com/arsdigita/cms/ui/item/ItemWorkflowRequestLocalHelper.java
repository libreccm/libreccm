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
package com.arsdigita.cms.ui.item;

import org.hibernate.LazyInitializationException;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowRepository;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Helper class for {@link ItemWorkflowRequestLocal} to avoid a
 * {@link LazyInitializationException} when accessing the workflow of current
 * content item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ItemWorkflowRequestLocalHelper {

    @Inject
    private ContentItemRepository itemRepo;

    @Inject
    private WorkflowRepository workflowRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected Workflow findWorkflowForContentItem(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        final Workflow workflow;
        if (contentItem.getWorkflow() == null) {
            workflow = null;
        } else {
            workflow = workflowRepo
                .findById(contentItem.getWorkflow().getWorkflowId())
                .orElseThrow(() -> new IllegalArgumentException(String
                .format("No Workflow with ID %d in the database.",
                        contentItem.getWorkflow().getWorkflowId())));
        }

        return workflow;
    }

}
