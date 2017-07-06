/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.globalization.GlobalizedMessage;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

/**
 * A form for editing the body of the text.
 */
public class PageTextForm
    extends Form
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener {

    private final TextBody textBody;

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
    public PageTextForm(final TextBody textBody) {

        super("PageTextForm", new ColumnPanel(2));

        this.textBody = textBody;

        setMethod(Form.POST);
        setEncType("multipart/form-data");

        addWidgets();
    }

    private void addWidgets() {

        final ColumnPanel panel = (ColumnPanel) getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        add(new Label(new GlobalizedMessage(
            "cms.ui.authoring.edit_body_text",
            CmsConstants.CMS_BUNDLE)),
            ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        textWidget = new CMSDHTMLEditor(PageTextForm.TEXT_ENTRY);
        textWidget.setRows(25);
        textWidget.setCols(40);
        // cg - sets editor size - closer to actual published page 
        // width, and similar size as htmlarea.
        // could be configurable - unset means default 100% x 400px
        textWidget.setMetaDataAttribute("width", "575");
        textWidget.setMetaDataAttribute("height", "500");
        textWidget.setWrap(CMSDHTMLEditor.SOFT);
        add(textWidget, ColumnPanel.LEFT
                            | ColumnPanel.FULL_WIDTH);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection, ColumnPanel.FULL_WIDTH);

        // optionally, we clear the text of MSWord tags every time
        // the text is submitted/saved
        if (CMSConfig.getConfig().isSaveTextCleansWordTags()) {
            saveCancelSection.getSaveButton().setOnClick(
                "wordClean_"
                    + PageTextForm.TEXT_ENTRY
                    + "();");
        }

        addInitListener(this);
        addProcessListener(this);

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

    /**
     * Initialise the text area with the current value.
     *
     * @param event
     *
     * @throws FormProcessException
     */
    public void init(final FormSectionEvent event) throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final String text = textBody.getText(state);

        if (text != null) {
            data.put(TEXT_ENTRY, text);
        }
    }

    /**
     * Cancels streamlined editing.
     *
     * @param event
     */
    @Override
    public void submitted(final FormSectionEvent event) {

        if (getSaveCancelSection()
            .getCancelButton()
            .isSelected(event.getPageState())) {

            textBody.cancelStreamlinedCreation(event.getPageState());
        }
    }

    // process: update the mime type and content
    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final String text = (String) data.get(TEXT_ENTRY);
        textBody.updateText(state, text);

        textBody.maybeForwardToNextStep(event.getPageState());
    }

    /**
     * @return the save/cancel section for this form
     */
    public SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

}
