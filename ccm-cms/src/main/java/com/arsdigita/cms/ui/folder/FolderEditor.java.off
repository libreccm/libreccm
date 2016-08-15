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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.Folder;


/**
 * Implements functionality for renaming a folder. Most code taken from FolderCreator. Need to refactor out base
 * functionality of FolderEditor & Creator.
 *
 * @author Jon Orris (jorris@arsdigita.com)
 *
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 * @version $Id: FolderEditor.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class FolderEditor extends FolderForm {

    public FolderEditor(String name, FolderSelectionModel folder) {
        super(name, folder);
    }


    /**
     * Initialize the form with name & label of folder being edited.
     */
    public void init(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        FormData data = e.getFormData();
        Folder folder = getCurrentFolder(s);
        data.put(NAME, folder.getName());
        data.put(TITLE, folder.getLabel());
    }


    public void process(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        FormData data = e.getFormData();
        Folder folder = getCurrentFolder(s);

        updateFolder(folder, (String)data.get(NAME), (String)data.get(TITLE));
    }
}
