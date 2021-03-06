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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ui.workflow.WorkflowRequestLocal;

import org.libreccm.cdi.utils.CdiUtil;

public class ItemWorkflowRequestLocal extends WorkflowRequestLocal {

    @Override
    protected final Object initialValue(final PageState state) {

        final ContentItem item = CMS.getContext().getContentItem();

//        return item.getWorkflow();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ItemWorkflowRequestLocalHelper helper = cdiUtil
            .findBean(ItemWorkflowRequestLocalHelper.class);

        return helper.findWorkflowForContentItem(item);
    }

}
