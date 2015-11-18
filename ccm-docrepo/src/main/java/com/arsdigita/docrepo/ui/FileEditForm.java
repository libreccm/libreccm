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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.ResourceRepository;

import javax.servlet.http.HttpServletRequest;

//import com.arsdigita.docrepo.File;
//import com.arsdigita.docrepo.Folder;

/**
 * This component allows to change the file name and the description of a
 * file. It also serves to associate keywords to a file (knowledge object).
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class FileEditForm extends Form implements FormValidationListener,
        FormProcessListener, FormInitListener, Constants {

    private static final Logger log = Logger.getLogger(FileEditForm.class);

    // Todo: add strings to package properties
    private final static String FILE_EDIT = "file-edit";
    private final static String FILE_EDIT_FNAME = "file-edit-name";
    private final static String FILE_EDIT_DESCRIPTION = "file-edit-description";

    private StringParameter m_FileName;
    private StringParameter m_FileDesc;
    private FileInfoPropertiesPane m_parent;

    /**
     * Constructor. Initializes the file edit form in the info property pane.
     *
     * @param parent The file property pane
     */
    public FileEditForm(FileInfoPropertiesPane parent) {
        super(FILE_EDIT, new ColumnPanel(2));

        m_parent = parent;

        m_FileName = new StringParameter(FILE_EDIT_FNAME);
        m_FileDesc = new StringParameter(FILE_EDIT_DESCRIPTION);

        add(new Label(FILE_NAME_REQUIRED));
        TextField fnameEntry = new TextField(m_FileName);
        fnameEntry.addValidationListener(new NotEmptyValidationListener());
        add(fnameEntry);

        add(new Label(FILE_DESCRIPTION));
        TextArea descArea = new TextArea(m_FileDesc);
        descArea.setRows(10);
        descArea.setCols(40);
        add(descArea);

        Submit submit = new Submit("file-edit-save");
        submit.setButtonLabel(FILE_SAVE);
        add(new Label());

        SimpleContainer sc = new SimpleContainer();
        sc.add(submit);
        sc.add(new CancelButton(CANCEL));

        add(sc);

        addInitListener(this);
        addProcessListener(this);
        addValidationListener(this);
    }

    /**
     * Initializer to pre-fill name and description after an event has been
     * triggered.
     *
     * @param event The event
     */
    public void init(FormSectionEvent event) {
        PageState state = event.getPageState();

        // Todo: exchange usage of Kernel class
//        if (Kernel.getContext().getParty() == null) {
//            Util.redirectToLoginPage(state);
//        }

        FormData data = event.getFormData();

        Long fileId = (Long) state.getValue(FILE_ID_PARAM);
        final CdiUtil cdiUtil = new CdiUtil();
        final ResourceRepository resourceRepository;
        try {
            resourceRepository = cdiUtil.findBean(ResourceRepository.class);
            File file = (File) resourceRepository.findById(fileId);
            data.put(FILE_EDIT_FNAME, file.getName());
            data.put(FILE_EDIT_DESCRIPTION, file.getDescription());
        } catch (CdiLookupException ex) {
            log.error("Failed to find bean for ResourceRepository.", ex);
        }
    }

    /**
     * Read form and update when event has been triggered.+
     *
     * @param event The event
     */
    public void process(FormSectionEvent event) {
        PageState state = event.getPageState();
        HttpServletRequest req = state.getRequest();
        FormData data = event.getFormData();

        String fname = (String) data.get(FILE_EDIT_FNAME);
        String fdesc = (String) data.get(FILE_EDIT_DESCRIPTION);

        Long fileId = (Long) state.getValue(FILE_ID_PARAM);
        final CdiUtil cdiUtil = new CdiUtil();
        final ResourceRepository resourceRepository;
        try {
            resourceRepository = cdiUtil.findBean(ResourceRepository.class);
            File file = (File) resourceRepository.findById(fileId);
            if (file != null) {
                file.setName(fname);
                file.setDescription(fdesc);
                // Todo: How to change?
                //file.applyTag(FILE_EDIT_ACTION_DESCRIPTION.localize(req).toString());
                resourceRepository.save(file);
            } else {
                log. error(String.format("Couldn't find file %d in the " +
                        "database.", fileId));
            }
        } catch (CdiLookupException ex) {
            log.error("Failed to find bean for ResourceRepository.", ex);
        }
        m_parent.displayPropertiesAndActions(state);
    }

    /**
     * Tests if the new name already exists in the current folder when event
     * has been triggered.
     *
     * @param event The event
     */
    public void validate(FormSectionEvent event) {
        // Todo: redundant i think
    }
}