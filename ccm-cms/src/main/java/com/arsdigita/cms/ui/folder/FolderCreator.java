/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;


public class FolderCreator extends FolderForm {

    private static Logger LOGGER = LogManager.getLogger(FolderCreator.class);

    public FolderCreator(final String name, final FolderSelectionModel parent) {
        super(name, parent);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FolderSelectionModel model = getFolderSelectionModel();

        // Create a new item_id and set it as the key
        model.setSelectedKey(state, null);
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final FolderSelectionModel model = getFolderSelectionModel();
        final long id = model.getSelectedKey(state);
        final Category parent = getCurrentFolder(state);

        final Category child = new Category();

        updateFolder(child,
                     parent,
                     data.getString(NAME),
                     data.getString(TITLE));
    }

}
