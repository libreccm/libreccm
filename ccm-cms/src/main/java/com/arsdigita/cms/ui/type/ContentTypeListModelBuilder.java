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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.NoSuchElementException;

/**
 * Builds a dynamic list of content types for a content section.
 */
class ContentTypeListModelBuilder 
    extends LockableImpl
    implements ListModelBuilder {

    /**
     *
     * @param list
     * @param state
     *
     * @return
     */
    @Override
    public ListModel makeModel(final List list, final PageState state) {
        return new Model();
    }

    /**
     *
     */
    private class Model implements ListModel {

        private final java.util.List<String[]> types;
        private int index = -1;

        Model() {
            final ContentSection section = CMS.getContext().getContentSection();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentTypeAdminPaneController controller = cdiUtil.findBean(
                ContentTypeAdminPaneController.class);
            
            types = controller.getTypeList(section);
        }

        @Override
        public boolean next() throws NoSuchElementException {
            index++;
            return index < types.size();
        }

        @Override
        public Object getElement() {
            return types.get(index)[1];
        }

        @Override
        public String getKey() {
            return types.get(index)[0];
        }

    }

}
