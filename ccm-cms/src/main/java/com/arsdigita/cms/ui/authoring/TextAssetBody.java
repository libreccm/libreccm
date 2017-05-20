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
package com.arsdigita.cms.ui.authoring;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.Logger;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.logging.log4j.LogManager;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

import java.io.IOException;

import javax.activation.MimeType;

/**
 * Displays the mime-type and the body of a single {@code TextAsset}. Maintains
 * a form or uploading files into the text body of the asset, and a form for
 * editing the text of the asset.
 * <p>
 * Unlike most other authoring components, this component does not require the
 * asset to exist. If the asset does not exist (i.e., if
 * <code>!m_assetModel.isSelected(state)</code>), the upload and editing forms
 * will create a new asset and set it in the model by calling
 * <code>setSelectedObject</code> on the asset selection model. Child classes
 * should override the {@link #createTextAsset(PageState)} method in to create a
 * valid text asset.
 * <p>
 * This component is used primarily in {@link GenericArticleBody} and
 * {@link com.arsdigita.cms.ui.templates.TemplateBody}
 *
 * <b>Note: </b> In CCM NG (version 7 and newer) {@code TextAsset} does not
 * longer exist. Instead fields of type {@link LocalizedString} are used. This
 * class has been adapted to use {@link LocalizedString}. The name of the class
 * etc. has been kept to make the migration easier.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class TextAssetBody
    extends SecurityPropertyEditor
    implements Resettable, AuthoringStepComponent, RequestListener {

    public static final String FILE_UPLOAD = "file";
    public static final String TEXT_ENTRY = "text";

    private static final String STREAMLINED = "_streamlined";
    private static final String STREAMLINED_DONE = "1";
    private static final CMSConfig CMS_CONFIG = CMSConfig.getConfig();

    private final StringParameter streamlinedCreationParam;
    private ItemSelectionModel assetModel;

    /**
     * Construct a new GenericArticleBody component
     *
     * @param assetModel The {@link ItemSelectionModel} which will be
     *                   responsible for maintaining the current asset
     */
    public TextAssetBody(final ItemSelectionModel assetModel) {
        this(assetModel, null);
    }

    /**
     * Construct a new GenericArticleBody component
     *
     * @param assetModel         The {@link ItemSelectionModel} which will be
     *                           responsible for maintaining the current asset
     * @param authoringKitWizard The parent wizard which contains the form. The
     *                           form may use the wizard's methods, such as
     *                           stepForward and stepBack, in its process
     *                           listener.
     */
    public TextAssetBody(final ItemSelectionModel assetModel,
                         final AuthoringKitWizard authoringKitWizard) {

        super();
        this.assetModel = assetModel;

        if (authoringKitWizard == null) {
            streamlinedCreationParam = new StringParameter("item_body_done");
        } else {
            streamlinedCreationParam = new StringParameter(
                String.format("%s_body_done",
                              authoringKitWizard
                                  .getContentType()
                                  .getContentItemClass()
                                  .getName()));
        }

        if (!CMS_CONFIG.isHideTextAssetUploadFile()) {
            final PageFileForm pageFileForm = getPageFileForm();
            addFileWidgets(pageFileForm);
            add(FILE_UPLOAD,
                new GlobalizedMessage("cms.ui.upload", CmsConstants.CMS_BUNDLE),
                pageFileForm,
                pageFileForm.getSaveCancelSection().getCancelButton());
        }

        final PageTextForm pageTextForm = new PageTextForm();
        addTextWidgets(pageTextForm);
        add(TEXT_ENTRY,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            pageTextForm,
            pageTextForm.getSaveCancelSection().getCancelButton());

        // Specify full path to properties of the text asset
        final DomainObjectPropertySheet sheet = getBodyPropertySheet(assetModel);
        sheet.add(new GlobalizedMessage("cms.ui.authoring.body",
                                        CmsConstants.CMS_BUNDLE),
                  getTextAssetName());

        setDisplayComponent(sheet);

        getDisplayPane().setClassAttr("invertedPropertyDisplay");

    }

    /**
     * Determines the name of the property holding the text.
     *
     * @return The name of the property holding the text.
     */
    protected abstract String getTextAssetName();

    protected DomainObjectPropertySheet getBodyPropertySheet(
        final ItemSelectionModel assetModel) {

        return new TextAssetBodyPropertySheet(assetModel);
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     *
     */
    protected void setMimeTypeOptions(SingleSelect mimeSelect) {
        FileUploadSection.addMimeOptions(mimeSelect, "text");
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * To be overwritten by subclasses, should return the field.
     *
     * @param state
     */
    public abstract LocalizedString getTextAsset(final PageState state);

    /**
     * Reset this component to its original state
     *
     * @param state the current page state
     */
    @Override
    public void reset(final PageState state) {
        showDisplayPane(state);
    }

    // Create a text asset if it does not exist.
    // This should probably be a method in GenericArticle ?
    protected LocalizedString createOrGetTextAsset(
        final ItemSelectionModel assetModel,
        final PageState state) {
        // Get the text asset or create a new one
        LocalizedString text = getTextAsset(state);

        if (text == null) {
            text = createTextAsset(state);

        }

        return text;
    }

    /**
     * Create a brand new <code>TextAsset</code>. Child classes should override
     * this method to do the right thing. The default implementation creates a
     * parent-less <code>TextAsset</code> with a unique name.
     *
     * @param state the current page state
     *
     * @return a valid <code>TextAsset</code>
     */
    protected abstract LocalizedString createTextAsset(PageState state);

    /**
     * Set additional parameters of a brand new text asset, such as the parent
     * ID, after the asset has been successfully uploaded
     *
     * @param state the current page state
     * @param text  the new <code>TextAsset</code>
     */
    protected abstract void updateTextAsset(PageState state,
                                            LocalizedString text);

    /**
     * Return the <code>ItemSelectionModel</code> which will be used to maintain
     * the current text asset
     *
     * @return
     */
    public ItemSelectionModel getAssetSelectionModel() {
        return assetModel;
    }

    /**
     * Forward to the next step if the streamlined creation parameter is turned
     * on _and_ the streamlined_creation global state parameter is set to
     * 'active'
     *
     * @param state the PageState
     */
    protected void maybeForwardToNextStep(final PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)
                && !STREAMLINED_DONE.
                equals(state.getValue(streamlinedCreationParam))) {
            state.setValue(streamlinedCreationParam, STREAMLINED_DONE);
            fireCompletionEvent(state);
        }
    }

    /**
     * Cancel streamlined creation for this step if the streamlined creation
     * parameter is turned on _and_ the streamlined_creation global state param
     * is set to 'active'
     *
     * @param state the PageState
     */
    protected void cancelStreamlinedCreation(final PageState state) {
        if (ContentItemPage.isStreamlinedCreationActive(state)) {
            state.setValue(streamlinedCreationParam, STREAMLINED_DONE);
        }
    }

    /**
     * Open the edit component if the streamlined creation parameter is turned
     * on _and_ the streamlined_creation global state param is set to 'active'
     *
     * @param event
     */
    @Override
    public void pageRequested(RequestEvent event) {

        final PageState state = event.getPageState();

        if (ContentItemPage.isStreamlinedCreationActive(state)
                && !STREAMLINED_DONE.
                equals(state.getValue(streamlinedCreationParam))) {
            showComponent(state, TEXT_ENTRY);
        }
        //}

    }

    /**
     * This is the form that is used to upload files. This method can be used so
     * that a subclass can use their own subclass of PageFileForm.
     *
     * @return
     */
    protected PageFileForm getPageFileForm() {
        return new PageFileForm();
    }

    /**
     * A form for editing TextAsset items. Displays a "file upload" widget,
     * auto-guesses mime type
     */
    public class PageFileForm extends Form
        implements FormProcessListener, FormValidationListener {

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
         */
        public PageFileForm() {
            super("PageFileUpload", new BoxPanel(BoxPanel.VERTICAL));
            setMethod(Form.POST);
            setEncType("multipart/form-data");
        }

        protected String getFileUploadContent(PageState state) {
            return (String) fileUploadContent.get(state);
        }

        /**
         * Make sure that files of this type can be uploaded
         */
        private void validateFileType(final MimeType mime,
                                      final boolean textType)
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
        private byte[] readFileBytes(final File file)
            throws FormProcessException {

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
         * Convert bytes to String, possibly using INSO filter to convert to
         * HTML type
         */
        private String convertBytes(final byte[] fileBytes,
                                    final boolean textType,
                                    final boolean[] usedInso)
            throws FormProcessException {
            String fileContent;
            // If mime type is not text type, try to convert to html
            if (!textType) {
                fileContent = new String(fileBytes);
                if (fileContent != null) {
                    // Converted successfully, flag type should be html
                    usedInso[0] = true;
                } else {
                    throw new FormProcessException(
                        new GlobalizedMessage(
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
                        "cannot convert to encoding "
                            + enc, ex);
                }
                usedInso[0] = false;
            }
            return fileContent;
        }

        /**
         * Extract the contents of the HTML Body tag. (Done to prevent base and
         * other header tags from interfering with page display).
         */
        private String extractHTMLBody(final String htmlText)
            throws FormProcessException {

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
            fileUploadUsedINSO.set(state, new Boolean(usedInso[0]));
        }

        /**
         * Process file upload. Must be validated first.
         */
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final FormData data = event.getFormData();
            final PageState state = event.getPageState();
            // Get the text asset or create a new one
            final LocalizedString text = createOrGetTextAsset(assetModel, state);
            final File file = fileUploadSection.getFile(event);

            // Get info created during validation
            final String uploadContent = (String) fileUploadContent.get(state);
            boolean usedINSO = ((Boolean) fileUploadUsedINSO
                                .get(state));

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
            updateTextAsset(state, text);
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

    /**
     * A form for editing the body of the text.
     */
    public class PageTextForm
        extends Form
        implements FormInitListener,
                   FormProcessListener,
                   FormSubmissionListener {

//        private SingleSelect mimeWidget;
//        private Label mimeLabel;
        private TextArea textWidget;
        private SaveCancelSection saveCancelSection;
        /**
         * The text entry widget
         */
        public static final String TEXT_ENTRY = "text_entry";
        /**
         * The mime type widget
         */
        public static final String MIME_TYPE = "mime_type";

        /**
         * Construct a new PageTextForm
         *
         */
        public PageTextForm() {

            super("PageTextForm", new ColumnPanel(2));
            setMethod(Form.POST);
            setEncType("multipart/form-data");
        }

        // These are here so that TemplateBody can set them.
//        public final void setMimeWidget(final SingleSelect widget) {
//            mimeWidget = widget;
//        }
        public final void setTextWidget(final TextArea widget) {
            textWidget = widget;
        }

        public final void setSaveCancel(final SaveCancelSection widget) {
            saveCancelSection = widget;
        }

        // Init: load the item and preset the textarea
        public void init(final FormSectionEvent event) throws
            FormProcessException {
            // Ok, we need to stream out the text somehow, but for now
            // we just fake it
            final FormData data = event.getFormData();
            final PageState state = event.getPageState();

//            ToDo
// final LocalizedString text = assetModel.getSelectedObject(state);
//            if (text != null) {
//                data.put(TEXT_ENTRY, text.getText());
//                MimeType m = text.getMimeType();
//                if (m != null) {
//                    data.put(MIME_TYPE, m.getMimeType());
//                }
//            }
        }

        /**
         * Cancels streamlined editing.
         */
        @Override
        public void submitted(final FormSectionEvent event) {
            if (getSaveCancelSection().getCancelButton().isSelected(event.
                getPageState())) {
                TextAssetBody.this.cancelStreamlinedCreation(event
                    .getPageState());
            }
        }

        // process: update the mime type and content
        @Override
        public void process(final FormSectionEvent event) throws
            FormProcessException {

            final FormData data = event.getFormData();
            final PageState state = event.getPageState();
            final LocalizedString text = createOrGetTextAsset(assetModel, state);

            // Set the mime type
//            MimeType m = MimeType.loadMimeType((String) data.get(MIME_TYPE));
//            text.setMimeType(m);
            // Get the string and normalize it
            String textStr = (String) data.get(TEXT_ENTRY);

            if (textStr == null) {
                textStr = "";
            }

//            ToDo textStr.setText((String) data.get(TEXT_ENTRY));
            // Save everything
            updateTextAsset(state, text);

            TextAssetBody.this.maybeForwardToNextStep(event.getPageState());
        }

        /**
         * @return the save/cancel section for this form
         */
        public SaveCancelSection getSaveCancelSection() {
            return saveCancelSection;
        }

    }

    protected String getDefaultMimeType() {
        return "text/plain";
    }

    /* overridable method to put together the PageFileForm Component */
    protected void addFileWidgets(final PageFileForm pageFileForm) {

        pageFileForm.fileUploadSection = new FileUploadSection("Text Type:",
                                                               "text",
                                                               getDefaultMimeType());
        pageFileForm.fileUploadSection.getFileUploadWidget()
            .addValidationListener(
                new NotNullValidationListener());
        // Default to -guess- because want to use file extension to determine type.
        pageFileForm.fileUploadSection.getMimeTypeWidget().setDefaultValue(
            FileUploadSection.GUESS_MIME);
        pageFileForm.add(pageFileForm.fileUploadSection);

        pageFileForm.saveCancelSection = new SaveCancelSection();
        pageFileForm.add(pageFileForm.saveCancelSection);

        // add FormErrorDisplay component to display any error message
        FormErrorDisplay fe = new FormErrorDisplay(pageFileForm);
        pageFileForm.add(fe);

        pageFileForm.addValidationListener(pageFileForm);
        pageFileForm.addProcessListener(pageFileForm);  // process called from validationListener

        // Storage for file upload information
        pageFileForm.fileUploadContent = new RequestLocal();
        pageFileForm.fileUploadUsedINSO = new RequestLocal();
    }


    /* overridable method to put together the PageTextForm Component */
    protected void addTextWidgets(final PageTextForm pageTextForm) {

        ColumnPanel panel = (ColumnPanel) pageTextForm.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

//        c.add(new Label(
//            GlobalizationUtil.globalize("cms.ui.authoring.text_type")));
//        c.mimeWidget = new SingleSelect(PageTextForm.MIME_TYPE);
//        c.mimeWidget.setClassAttr("displayOneOptionAsLabel");
//        setMimeTypeOptions(c.mimeWidget);
//        c.add(c.mimeWidget, ColumnPanel.LEFT);
        pageTextForm.add(new Label(new GlobalizedMessage(
            "cms.ui.authoring.edit_body_text",
            CmsConstants.CMS_BUNDLE)),
              ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        pageTextForm.textWidget = new CMSDHTMLEditor(PageTextForm.TEXT_ENTRY);
        pageTextForm.textWidget.setRows(25);
        pageTextForm.textWidget.setCols(40);
        // cg - sets FCKEditor size - closer to actual published page 
        // width, and similar size as htmlarea.
        // could be configurable - unset means default 100% x 400px
        pageTextForm.textWidget.setMetaDataAttribute("width", "575");
        pageTextForm.textWidget.setMetaDataAttribute("height", "500");
        pageTextForm.textWidget.setWrap(CMSDHTMLEditor.SOFT);
        pageTextForm.add(pageTextForm.textWidget, ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        pageTextForm.saveCancelSection = new SaveCancelSection();
        pageTextForm.add(pageTextForm.saveCancelSection, ColumnPanel.FULL_WIDTH);

        // optionally, we clear the text of MSWord tags every time
        // the text is submitted/saved
        if (CMSConfig.getConfig().isSaveTextCleansWordTags()) {
            pageTextForm.saveCancelSection.getSaveButton().setOnClick("wordClean_"
                                                               + PageTextForm.TEXT_ENTRY
                                                           + "();");
        }

        pageTextForm.addInitListener(pageTextForm);
        pageTextForm.addProcessListener(pageTextForm);

    }

    /**
     * Registers global state parameter for cancelling streamlined creation
     */
    @Override
    public void register(final Page page) {
        super.register(page);
        page.addGlobalStateParam(streamlinedCreationParam);
        page.addRequestListener(this);
    }

}
