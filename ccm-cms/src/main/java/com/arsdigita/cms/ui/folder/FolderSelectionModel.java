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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.cms.CMS;

import com.arsdigita.ui.CcmObjectSelectionModel;

import org.libreccm.categorization.Category;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;


/**
 * Keeps track of the selection of an item in a folder. The objects that are
 * selected by this model are all subclasses of {@link
 * com.arsdigita.cms.Folder}.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FolderSelectionModel extends CcmObjectSelectionModel<Folder> {

    public FolderSelectionModel(final String name) {
        super(Category.class.getName(), name);
    }

    public FolderSelectionModel(final SingleSelectionModel<Long> model) {
        super(Category.class.getName(), model);
    }

    @Override
    public Long getSelectedKey(final PageState state) {
        // FIXME: this code will go away once parameter models support init listeners
        Long result = super.getSelectedKey(state);
        if (result == null) {
            result = getRootFolderID(state);
            setSelectedKey(state, result);
        }
        return result;
    }

    @Override
    public void setSelectedKey(final PageState state, final Long key) {
        super.setSelectedKey(state, key);
    }
    
    /**
     * Clear the selection by resetting it to the root folder id.
     *
     * @param state represents the cuerent request.
     */
    @Override
    public void clearSelection(final PageState state) {
        setSelectedKey(state, getRootFolderID(state));
    }

    /**
     * Return the ID of the root folder. By default, this is the root folder of
     * the content section in which the current request is made. If this model
     * is to be used outside a content section, this method has to be overriden
     * appropriately.
     *
     * @param state represents the current request
     * @return the ID of the root folder
     *
     * @pre s != null
     * @post return != null
     */
    protected Long getRootFolderID(final PageState state) {
        ContentSection sec = CMS.getContext().getContentSection();
        return sec.getRootDocumentsFolder().getObjectId();
    }

    /**
     * Return true, since this selection model will always have a folder
     * selected in it
     * @param state
     * @return 
     */
    @Override
    public boolean isSelected(final PageState state) {
        return true;
    }

}
