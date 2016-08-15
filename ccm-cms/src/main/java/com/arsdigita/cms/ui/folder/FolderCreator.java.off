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
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class FolderCreator extends FolderForm {

    private static Logger s_log =
        Logger.getLogger(FolderCreator.class.getName());


    public FolderCreator(String name, FolderSelectionModel parent) {
        super(name, parent);
    }

    public void init(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        ItemSelectionModel m = getItemSelectionModel();

        // Create a new item_id and set it as the key
        try {
            m.setSelectedKey(state, Sequences.getNextValue());
        } catch (java.sql.SQLException ex) {
            s_log.error("Error retrieving sequence.nextval", ex);
            throw new FormProcessException(ex);
        }
    }


    public void process(FormSectionEvent e) throws FormProcessException {
        PageState s = e.getPageState();
        FormData data = e.getFormData();
        ItemSelectionModel m = getItemSelectionModel();
        BigDecimal id =  (BigDecimal) m.getSelectedKey(s);
        Folder parent = getCurrentFolder(s);

        Folder child = null;
        try {
            child = new Folder(id);
        } catch (DataObjectNotFoundException ex) {
            child = new Folder(SessionManager.getSession().create
                               (new OID(Folder.BASE_DATA_OBJECT_TYPE, id)));
        }

        updateFolder(child, parent, (String)data.get(NAME), (String)data.get(TITLE));
    }
}
