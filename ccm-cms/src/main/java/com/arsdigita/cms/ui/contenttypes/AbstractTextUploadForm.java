/*
 * Copyright (C) 2019 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.contenttypes;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.activation.MimeType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
/**
 * <p>
 * An abstract base form for uploading Texts. The code of this class has been
 * extracted from {@link TextAssetBody}, the form for editing an TextAsset (used
 * for example for Article). The TextAsset has the disadvantage of storing its
 * information in a separate table, so that information of a content item is
 * spread over several tables.
 * </p>
 * <p>
 * To use this form, define a property for your object which has the Java type
 * String and the database type CLOB, like this:
 * </p>
 * <pre>
 * String[0..1] text = ct_yourContenttype.text CLOB
 * </pre>
 * <p>
 * To use this form your have to overwrite three methods:
 * </p>
 * <ul>
 * <li>{@link #getLabelText()}</li>
 * <li>{@link #getMimeTypeLabel()}</li>
 * <li>{@link #setText()}</li>
 * </ul>
 * <li>
 * Of course, you have to add your form to a property step also, and write a
 * simple constructor, which takes an {@link ItemSelectionModel} as parameter
 * and passes it to the constructor of this class.
 * </li>
 *
 *
 * @author Jens Pelzetter
 */
public abstract class AbstractTextUploadForm
    extends Form
    implements FormInitListener,
               FormProcessListener,
               FormValidationListener {

    private static final Logger LOGGER = LogManager
        .getLogger(AbstractTextUploadForm.class);

    private ItemSelectionModel itemModel;
    private FileUploadSection fileUploadSection;
    private SaveCancelSection saveCancelSection;
    private RequestLocal fileUploadContent;
    private RequestLocal fileUploadContentUsedInso;

    public AbstractTextUploadForm(final ItemSelectionModel itemModel) {
        super("sciprojectUploadDescFrom", new BoxPanel(BoxPanel.VERTICAL));
        this.itemModel = itemModel;
        setMethod(Form.POST);
        setEncType("multipart/form-data");
        this.fileUploadContent = new RequestLocal();
        this.fileUploadContentUsedInso = new RequestLocal();
        addWidgets();
    }

    //Abstract methods to overwrite
    /**
     * The return value of this method is used as label for the upload form.
     *
     * @return The label for the upload form.
     */
    public abstract GlobalizedMessage getLabelText();

    /**
     * The return value of this method is used as label for the MimeType field.
     *
     * @return The label for the MimeType field.
     */
    public abstract GlobalizedMessage getMimeTypeLabel();

    /**
     * <p>
     * This method is called to pass the uploaded text to the edited object. In
     * the method, you have to retrieve the current selected object from the
     * <code>itemModel</code> parameter and call the appropriate
     * <code>set</code> of your class, and its save method. An simple example:
     * </p>
     * <pre>
     * @Override
     * public void setText(ItemSelectionModel itemModel,
     *                     PageState state,
     *                     String text) {
     *   YourContentType obj = (YourContentType) itemModel.getSelectedObject(state);
     *   obj.setText(text);
     *   obj.save();
     * }
     * </pre>
     *
     * @param itemModel The {@link ItemSelectionModel} used by the form.
     * @param state     The current {@link PageState}.
     * @param text      The uploaded text.
     */
    public abstract void setText(ItemSelectionModel itemModel,
                                 PageState state,
                                 String text);

    protected void addWidgets() {
        add(new Label(getLabelText()));
        fileUploadSection = new FileUploadSection(
            getMimeTypeLabel(),
            "mime",
            "text/plain");
        fileUploadSection.getFileUploadWidget().addValidationListener(
            new NotNullValidationListener());
        fileUploadSection.getMimeTypeWidget().setDefaultValue(
            FileUploadSection.GUESS_MIME);
        add(fileUploadSection);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        add(new FormErrorDisplay(this));

        addValidationListener(this);
        addProcessListener(this);
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();

        setVisible(state, true);
    }

    /**
     * Validate file upload
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event) throws
        FormProcessException {

        final MimeType mime = fileUploadSection.getMimeType(event);
        boolean textType = mime.getPrimaryType().equals("text");

        validateFileType(mime, textType);

        // Convert the file to HTML, if possible
        final File file = fileUploadSection.getFile(event);
        byte[] file_bytes = readFileBytes(file);
        boolean[] used_inso = new boolean[1];
        String file_content = convertBytes(file_bytes, textType, used_inso);

        if ("text/html".equals(mime.toString())) {
            file_content = extractHTMLBody(file_content);
        }

        final PageState state = event.getPageState();
        fileUploadContent.set(state, file_content);
        fileUploadContentUsedInso.set(state, used_inso[0]);
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {
        LOGGER.debug("Processing upload...");
        final PageState state = event.getPageState();
        //File file = fileUploadSection.getFile(fse);
        //SciProject project = (SciProject) itemModel.getSelectedObject(state);

        final String uploadContent = (String) fileUploadContent.get(state);
        //boolean usedInso = (Boolean) fileUploadContentUsedInso.get(state);

        LOGGER.debug(String.format("Setting project description to: %s",
                                   uploadContent));
        //project.setProjectDescription(uploadContent);
        LOGGER.debug("Saving project.");
        //project.save();
        setText(itemModel, state, uploadContent);
    }

    private void validateFileType(final MimeType mime, final boolean textType)
        throws FormProcessException {

        boolean validType = textType;

        if (!validType) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "cms.ui.authoring.invalid_file_type"));
        }
    }

    /**
     * read in the content of the file (in bytes).
     */
    private byte[] readFileBytes(final File file) throws FormProcessException {
        final byte[] fileBytes;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileBytes = new byte[fileInputStream.available()];
            fileInputStream.read(fileBytes);
        } catch (IOException ex) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "cms.ui.authoring.unable_to_load_file"));
        }
        return fileBytes;
    }

    /**
     * Convert bytes to String, possibly using INSO filter to convert to HTML
     * type
     */
    private String convertBytes(final byte[] fileBytes, 
                                final boolean textType,
                                final boolean[] usedInso)
        throws FormProcessException {
        
       return new String(fileBytes, StandardCharsets.UTF_8);
    }

    /**
     * Extract the contents of the html Body tag. (Done to prevent base and
     * other header tags from interfering with page display).
     */
    private String extractHTMLBody(final String htmlText)
        throws FormProcessException {
        
        final String lc = htmlText.toLowerCase();
        final int bodyStart = lc.indexOf("<body");
        final int bodyStart_v = lc.indexOf(">", bodyStart);
        final int bodyEnd = lc.indexOf("</body>", bodyStart_v);
        if (bodyStart == -1 || bodyEnd == -1) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "cms.ui.authoring.html_file_missing_body_tags"));
        }
        return htmlText.substring(bodyStart_v + 1, bodyEnd);
    }

}
