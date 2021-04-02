/*
 * Copyright (C) 2021 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.activation.MimeType;

/**
 * A form for editing TextAsset items. Displays a "file upload" widget,
 * auto-guesses mime type
 */
class PageFileForm extends Form implements FormProcessListener,
                                           FormValidationListener {

    private final TextBody textBody;

    private SaveCancelSection saveCancelSection;
    private FileUploadSection fileUploadSection;
    // Variables saved by validate for processing
    private RequestLocal fileUploadContent;
    private RequestLocal fileUploadUsedINSO;
    /**
     * The text entry widget
     */
    public static final String TEXT_ENTRY = "text_entry";

    /**
     * Construct a new PageFileForm
     *
     * @param itemSelectionModel
     */
    public PageFileForm(final TextBody textBody) {

        super("PageFileUpload", new BoxPanel(BoxPanel.VERTICAL));

        this.textBody = textBody;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        addWidgets();
    }

    private void addWidgets() {

        fileUploadSection = new FileUploadSection(
            new GlobalizedMessage("cms.ui.authoring.text.mime_type"),
            "text",
            textBody.getDefaultMimeType());

        fileUploadSection
            .getFileUploadWidget()
            .addValidationListener(new NotNullValidationListener());
        fileUploadSection
            .getMimeTypeWidget()
            .setDefaultValue(FileUploadSection.GUESS_MIME);
        add(fileUploadSection);
        
        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);
        
        final FormErrorDisplay errorDisplay = new FormErrorDisplay(this);
        add(errorDisplay);
        
        addValidationListener(this);
        addProcessListener(this);
        
        fileUploadContent = new RequestLocal();
        fileUploadUsedINSO = new RequestLocal();

    }

    protected String getFileUploadContent(PageState state) {
        return (String) fileUploadContent.get(state);
    }

    /**
     * Make sure that files of this type can be uploaded
     */
    private void validateFileType(final MimeType mime, final boolean textType)
        throws FormProcessException {
        //            boolean validType = textType || ((mime instanceof TextMimeType)
        //                                             && ((TextMimeType) mime).
        //                                             allowINSOConvert().booleanValue());
        boolean validType = true; //ToDo
        if (!validType) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.authoring.invalid_file_type",
                CmsConstants.CMS_BUNDLE));
        }
        //            boolean insoWorks = MimeTypeStatus.getMimeTypeStatus().
        //                getInsoFilterWorks().intValue() == 1;
        boolean insoWorks = true; //ToDo
        if (!textType && !insoWorks) {
            // Can't convert.  inso filter is not working.  Give message.
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.authoring.couldnt_convert_missing_inso",
                CmsConstants.CMS_BUNDLE));
        }
    }

    /**
     * read in the content of the file (in bytes).
     */
    private byte[] readFileBytes(final File file) throws FormProcessException {
        byte[] fileBytes;
        try (final FileInputStream fs = new FileInputStream(file)) {
            fileBytes = new byte[fs.available()];
            fs.read(fileBytes);
        } catch (IOException ex) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.authoring.unable_to_load_file",
                CmsConstants.CMS_BUNDLE));
        }
        return fileBytes;
    }

    /**
     * Convert bytes to String, possibly using INSO filter to convert to HTML
     * type
     */
    private String convertBytes(final byte[] fileBytes, final boolean textType,
                                final boolean[] usedInso) throws
        FormProcessException {
        String fileContent;
        // If mime type is not text type, try to convert to html
        if (!textType) {
            fileContent = new String(fileBytes);
            if (fileContent != null) {
                // Converted successfully, flag type should be html
                usedInso[0] = true;
            } else {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.authoring.couldnt_convert_inso_failed",
                    CmsConstants.CMS_BUNDLE));
            }
        } else {
            // Text type, no need to convert
            final String enc = "UTF-8";
            try {
                fileContent = new String(fileBytes, enc);
            } catch (UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException(
                    "cannot convert to encoding " + enc, ex);
            }
            usedInso[0] = false;
        }
        return fileContent;
    }

    /**
     * Extract the contents of the HTML Body tag. (Done to prevent base and
     * other header tags from interfering with page display).
     */
    private String extractHTMLBody(final String htmlText) throws
        FormProcessException {
        final String lowerCase = htmlText.toLowerCase();
        int bodyStart = lowerCase.indexOf("<body");
        int bodyStart_v = lowerCase.indexOf(">", bodyStart);
        int bodyEnd = lowerCase.indexOf("</body>", bodyStart_v);
        if (bodyStart == -1 || bodyEnd == -1) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.authoring.html_file_missing_body_tags",
                CmsConstants.CMS_BUNDLE));
        }
        return htmlText.substring(bodyStart_v + 1, bodyEnd);
    }

    /**
     * Validate file upload
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event) throws
        FormProcessException {
        MimeType mime = fileUploadSection.getMimeType(event);
        //            boolean textType = mime.getPrefix().equals(TextMimeType.TEXT_PREFIX);
        final boolean textType = true; //ToDo
        validateFileType(mime, textType);
        // Convert the file to HTML, if possible
        File file = fileUploadSection.getFile(event);
        byte[] file_bytes = readFileBytes(file);
        boolean[] usedInso = new boolean[1];
        String file_content = convertBytes(file_bytes, textType, usedInso);
        // ToDo           if (TextMimeType.MIME_TEXT_HTML.equals(mime.getMimeType())) {
        file_content = extractHTMLBody(file_content);
        //            }
        final PageState state = event.getPageState();
        fileUploadContent.set(state, file_content);
        fileUploadUsedINSO.set(state, usedInso[0]);
    }

    /**
     * Process file upload. Must be validated first.
     */
    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        // Get the text asset or create a new one
        final String text = textBody.getText(state);
        final File file = fileUploadSection.getFile(event);
        // Get info created during validation
        final String uploadContent = (String) fileUploadContent.get(state);
        boolean usedINSO = (Boolean) fileUploadUsedINSO.get(state);
        // Set the mime type
        //            final MimeType mime = fileUploadSection.getMimeType(event);
        //            if (usedINSO) {
        //                mime = MimeType.loadMimeType("text/html");
        //            }
        //            if (mime != null) {
        //                text.setMimeType(mime);
        //            }
        // Save the uploaded content
        //ToDo            text.setText(uploadContent);
        //t.setName(fileName); // ???
        //            file = null;
        // Save everything
        textBody.updateText(state, text);
        //            if (text.isNew() || text.isModified()) {
        //                text.save();
        //            }
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    /**
     * @return the save/cancel section for this form
     */
    public FileUploadSection getFileUploadSection() {
        return fileUploadSection;
    }

}
