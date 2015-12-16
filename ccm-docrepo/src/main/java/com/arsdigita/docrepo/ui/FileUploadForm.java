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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FileUpload;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.docrepo.InvalidNameException;
import com.arsdigita.docrepo.Util;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.BlobObject;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.Folder;
import org.libreccm.docrepo.Resource;
import org.libreccm.docrepo.ResourceRepository;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Form to upload and submit a file to the document repository.
 *
 * @author <mailto href="StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version $Id: FileUploadForm.java  pboy $
 */
public class FileUploadForm extends Form implements FormInitListener,
        FormValidationListener, FormProcessListener, Constants {

    private static final Logger log = Logger.getLogger(FileUploadForm.class);

    // Form constants
    private final static String FILE_UPLOAD = "file-upload";
    private final static String FILE_UPLOAD_FORM = "file-upload-form";
    private final static String FILE_UPLOAD_INPUT_DESCRIPTION = "file-description";

    private FileUpload m_fileUpload;

    private StringParameter m_FileDesc;
    private Tree m_tree;
    private BrowsePane m_parent;

    /**
     * Constructor.
     *
     * @param parent The browsePane
     * @param tree The tree
     */
    public FileUploadForm(BrowsePane parent, Tree tree) {
        this(parent, tree, true);
    }

    /**
     * Creates the form for the file upload.
     *
     * @param parent The browse pane
     * @param tree A tree
     * @param initListeners Weather there are initial listeners or not
     */
    public FileUploadForm(BrowsePane parent, Tree tree, boolean initListeners) {
        super(FILE_UPLOAD_FORM, new ColumnPanel(2));

        m_parent = parent;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        m_tree = tree;

        m_fileUpload = new FileUpload(FILE_UPLOAD);

        m_FileDesc = new StringParameter(FILE_UPLOAD_INPUT_DESCRIPTION);
        m_FileDesc.addParameterListener
                (new StringLengthValidationListener(4000));

        add(new Label(FILE_UPLOAD_ADD_FILE));
        add(m_fileUpload);

        add(new Label(FILE_DESCRIPTION));
        TextArea textArea = new TextArea(m_FileDesc);
        textArea.setRows(10);
        textArea.setCols(40);
        add(textArea);

        SimpleContainer sc = new SimpleContainer();
        Submit submit = new Submit("submit");
        submit.setButtonLabel(FILE_SUBMIT);
        sc.add(submit);
        CancelButton cancel = new CancelButton(CANCEL);
        sc.add(cancel);

        add(new Label()); // spacer
        add(sc, ColumnPanel.LEFT);

        if (initListeners) {
            addInitListener(this);
            addProcessListener(this);
            addValidationListener(this);
        }
    }

    /**
     * Post the file to a temporary file on the server and
     * insert it into the database
     *
     * @param e The form section event
     */
    protected Long insertFile(FormSectionEvent e)
            throws FormProcessException {
        log.debug("Inserting a file into the database");

        final PageState state = e.getPageState();
        final FormData data = e.getFormData();
        final HttpServletRequest req = state.getRequest();

        // stuff for the file
        final String fileName = getFileName(e);
        final String fileDescription = (String) data.get(FILE_UPLOAD_INPUT_DESCRIPTION);
        final String filePath = (String) data.get(FILE_UPLOAD);
        final MimeType mimeType = Util.guessContentType(fileName, req);


        if (log.isDebugEnabled()) {
            log.debug("getFileName() -> '" + fileName + "'");
            log.debug("description == '" + fileDescription + "'");
            log.debug("path == '" + filePath + "'");
        }

        java.io.File src = null;

        if (filePath != null && filePath.length() > 0) {
            HttpServletRequest mreq = e.getPageState().getRequest();

            //  Assert.assertTrue(mreq instanceof MultipartHttpServletRequest,
            Assert.isTrue(mreq instanceof MultipartHttpServletRequest,
                    "I got a " + mreq + " when I was " +
                            "expecting a MultipartHttpServletRequest");

            src = ((MultipartHttpServletRequest) mreq).getFile(FILE_UPLOAD);
            log.debug("file == '" + src + "'");
        }

        Folder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        final CdiUtil cdiUtil = new CdiUtil();
        final ResourceRepository resourceRepository = cdiUtil.findBean(
                ResourceRepository.class);

        if (selKey == null) {
            parent = Utils.getRootFolder(state);
        } else {
            Long folderID = new Long(selKey);

            final Resource resource = resourceRepository.findById(folderID);
            parent = resource != null && resource.isFolder()
                    ? (Folder) resource : null;
        }

        // insert the file in the database below parent
        final File file = new File;

        file.setParent(parent);
        file.setName(fileName);
        file.setDescription(fileDescription);
        file.setIsFolder(false);
        file.setPath(filePath);
        file.setMimeType(mimeType);
        file.setContent(new BlobObject().setContent(src));

        // annotate first file upload as initial version
        file.setDescription(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION.
                localize(req).toString());

        //file.applyTag(FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION.
        //localize(req).toString());

        file.save();

        new KernelExcursion() {
            protected void excurse() {
                Party currentParty = Kernel.getContext().getParty();
                setParty(Kernel.getSystemParty());
                PermissionService.grantPermission(new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                        file,
                        currentParty));
                Application app = Web.getWebContext().getApplication();
                Assert.exists(app, Application.class);
                PermissionService.setContext(file, app);
            }}.run();

        return file.getID();
    }

    public void init(FormSectionEvent e) {
        PageState state = e.getPageState();

        if ( Kernel.getContext().getParty() == null ) {
            Util.redirectToLoginPage(state);
        }

    }

    /**
     * Post the file to a temporary file on the server and
     * insert it into the database
     */
    public void process(FormSectionEvent e)
            throws FormProcessException {
        log.debug("Processing form submission");

        insertFile(e);

        if (m_parent != null) {
            m_parent.displayFolderContentPanel(e.getPageState());
        }

    }

    /**
     * Gets either the file name from the widget
     * or takes the filename from the upload
     * widget in this order.
     */
    protected String getFileName(FormSectionEvent e) {
        FormData data = e.getFormData();
        String filename = (String) data.get(FILE_UPLOAD);
        return Utils.extractFileName(filename, e.getPageState());
    }


    /**
     * Verify that the parent folder exists and does not contain any
     * other files or sub folders with the same name as the file being
     * uploaded.
     */

    public void validate(FormSectionEvent e) throws FormProcessException {
        PageState state = e.getPageState();
        FormData data = e.getFormData();
        HttpServletRequest req = state.getRequest();

        String fname = Utils.extractFileName(getFileName(e), state);

        // XXX Not localized as the other errors are.
        if (fname.length() > 200) {
            data.addError
                    (FILE_UPLOAD,
                            "This filename is too long.  It must be fewer than 200 " +
                                    "characters.");
        }

        Folder parent = null;
        String selKey = (String) m_tree.getSelectedKey(state);

        if (selKey == null) {
            parent = Utils.getRootFolder(state);
        } else {
            BigDecimal folderID = new BigDecimal(selKey);
            try {
                parent = new Folder(folderID);
            } catch(DataObjectNotFoundException nf) {
                throw new ObjectNotFoundException(FOLDER_PARENTNOTFOUND_ERROR
                        .localize(req).toString());
            }
        }

        try {
            parent.getResourceID(fname);
            data.addError(FILE_UPLOAD,
                    RESOURCE_EXISTS_ERROR
                            .localize(req).toString());
        } catch(DataObjectNotFoundException nf) {
            // ok here
        } catch(InvalidNameException ex) {
            data.addError(FILE_UPLOAD,
                    ex.getMessage());
        }
    }
}
