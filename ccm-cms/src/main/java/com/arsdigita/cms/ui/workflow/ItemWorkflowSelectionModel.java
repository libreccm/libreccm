/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.ItemSelectionModel;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

class ItemWorkflowSelectionModel extends ParameterSingleSelectionModel {

    private final ItemSelectionModel itemSelectionModel;

    public ItemWorkflowSelectionModel(final LongParameter itemIdParameter) {
        super(itemIdParameter);
        itemSelectionModel = new ItemSelectionModel(itemIdParameter);
    }

    public ItemWorkflowSelectionModel(
            final ItemSelectionModel itemSelectionModel) {
        super(itemSelectionModel.getStateParameter());
        this.itemSelectionModel = itemSelectionModel;
    }

    @Override
    public Object getSelectedKey(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil.findBean(ContentItemRepository.class);
        final ContentItem item = itemRepo.findById((Long) super.getSelectedKey(
                state));
        
        return item.getWorkflow().getWorkflowId();
    }

    public ItemSelectionModel getItemSelectionModel() {
        return itemSelectionModel;
    }
}
