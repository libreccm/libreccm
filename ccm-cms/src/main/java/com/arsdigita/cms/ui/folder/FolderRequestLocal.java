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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.ui.CcmObjectRequestLocal;

import org.apache.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;


public class FolderRequestLocal extends CcmObjectRequestLocal {

    private static final Logger s_log = Logger.getLogger(
        FolderRequestLocal.class);

    private final SingleSelectionModel m_model;

    public FolderRequestLocal(final SingleSelectionModel model) {
        m_model = model;
    }

    public FolderRequestLocal() {
        m_model = null;
    }

    @Override
    protected Object initialValue(final PageState state) {
        if (m_model == null) {
            return null;
        } else {
            final String id = m_model.getSelectedKey(state).toString();

            return CdiUtil.createCdiUtil().findBean(CategoryRepository.class)
                .findById(Long.parseLong(id));
        }
    }

    public final Category getFolder(final PageState state) {
        return (Category) get(state);
    }

}
