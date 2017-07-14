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
package com.arsdigita.cms.ui.authoring.multipartarticle;

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
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.MultiPartArticle;

import com.arsdigita.cms.ui.CMSDHTMLEditor;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contenttypes.MultiPartArticleSection;
import org.librecms.contenttypes.MultiPartArticleSectionRepository;

import java.util.Locale;

/**
 * Form to edit an ArticleSection for a MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SectionEditForm extends Form {


    public static final String TITLE = "title";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String PAGE_BREAK = "pageBreak";

    private static final String TEXT_PARAM = "textParam";
    private static final String IMAGE_PARAM = "imageParam";

    private final ItemSelectionModel selectedArticleModel;
    private final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel;
    private final StringParameter selectedLanguageParam;

    private MultiPartArticleSectionsStep sectionsStep;

    private SaveCancelSection saveCancelSection;

    public SectionEditForm(
        final ItemSelectionModel selectedArticleModel,
        final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel,
        final StringParameter selectedLanguageParam) {

        this(selectedArticleModel,
             selectedSectionModel,
             null,
             selectedLanguageParam);
    }

    public SectionEditForm(
        final ItemSelectionModel selectedArticleModel,
        final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel,
        final MultiPartArticleSectionsStep sectionsStep,
        final StringParameter selectedLanguageParam) {

        super("SectionEditForm", new ColumnPanel(2));
        this.selectedArticleModel = selectedArticleModel;
        this.selectedSectionModel = selectedSectionModel;
        this.sectionsStep = sectionsStep;
        this.selectedLanguageParam = selectedLanguageParam;

        super.setMethod(Form.POST);
        super.setEncType("multipart/form-data");

        final ColumnPanel panel = (ColumnPanel) super.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        addWidgets();
        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addInitListener(new SectionInitListener());
        addSubmissionListener(new SectionSubmissionListener());
        addProcessListener(new SectionProcessListener());
    }

    public SaveCancelSection getSaveCancelSection() {
        return saveCancelSection;
    }

    /**
     * Add form widgets for a Section.
     */
    private void addWidgets() {

        final TextField titleWidget = new TextField(
            new TrimmedStringParameter(TITLE));
        titleWidget.addValidationListener(new NotNullValidationListener());
        titleWidget.setLabel(
            new GlobalizedMessage("cms.contenttypes.ui.mparticle.section.title",
                                  CmsConstants.CMS_BUNDLE));
        add(titleWidget);

        final CMSDHTMLEditor textWidget = new CMSDHTMLEditor(
            new TrimmedStringParameter(TEXT));
        textWidget.setLabel(
            new GlobalizedMessage("cms.contenttypes.ui.mparticle.section.text",
                                  CmsConstants.CMS_BUNDLE));
        textWidget.setRows(40);
        textWidget.setCols(70);
        textWidget.setWrap(CMSDHTMLEditor.SOFT);
        add(textWidget, ColumnPanel.LEFT | ColumnPanel.FULL_WIDTH);

        final CheckboxGroup pageBreak = new CheckboxGroup(PAGE_BREAK);
        pageBreak.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "cms.contenttypes.ui.mparticle.section.create_break",
                           CmsConstants.CMS_BUNDLE))));
        add(pageBreak);
    }

    /**
     * Utility method to create a Section from the form data supplied.
     *
     * @param event
     * @param article
     *
     * @return
     */
    protected MultiPartArticleSection createSection(final FormSectionEvent event,
                                                    final MultiPartArticle article) {

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();

        final MultiPartArticleSection section = new MultiPartArticleSection();

        final Locale selectedLocale = SelectedLanguageUtil
            .selectedLocale(state, selectedLanguageParam);

        section.getTitle().addValue(selectedLocale, (String) data.get(TITLE));

        return section;
    }


    /**
     * Initialise the form. If there is a selected section, ie. this is an
     * 'edit' step rather than a 'create new' step, load the data into the form
     * fields.
     */
    private class SectionInitListener implements FormInitListener {

        @Override
        public void init(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final FormData data = event.getFormData();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final MultiPartArticleSectionRepository sectionRepo = cdiUtil
                .findBean(MultiPartArticleSectionRepository.class);

            if (selectedSectionModel.getSelectedKey(state) != null) {

                final Locale selectedLocale = SelectedLanguageUtil
                    .selectedLocale(state, selectedLanguageParam);

                final MultiPartArticleSection section = selectedSectionModel
                    .getSelectedSection(state);

                data.put(TITLE, section.getTitle().getValue(selectedLocale));
                data.put(TEXT, section.getText().getValue(selectedLocale));

                if (section.isPageBreak()) {
                    data.put(PAGE_BREAK, new Object[]{"true"});
                }
            }
        }

    }

    /**
     * Called on form submission. Check to see if the user clicked the cancel
     * button. If they did, don't continue with the form.
     */
    private class SectionSubmissionListener implements FormSubmissionListener {

        @Override
        public void submitted(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (saveCancelSection
                .getCancelButton()
                .isSelected(state)
                    && sectionsStep != null) {

                sectionsStep.onlyShowComponent(
                    state,
                    MultiPartArticleSectionsStep.SECTION_TABLE
                        + sectionsStep.getTypeIDStr());

                throw new FormProcessException(
                    "Submission cancelled",
                    new GlobalizedMessage(
                        "cms.contenttypes.ui.mparticle.submission_cancelled",
                        CmsConstants.CMS_BUNDLE)
                );
            }
        }

    }

    /**
     * Called after form has been validated. Create the new ArticleSection and
     * assign it to the current MultiPartArticle.
     */
    private class SectionProcessListener implements FormProcessListener {

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final FormData data = event.getFormData();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final MultiPartArticleSectionStepController controller = cdiUtil
                .findBean(MultiPartArticleSectionStepController.class);
            final MultiPartArticleSectionRepository sectionRepo = cdiUtil
                .findBean(MultiPartArticleSectionRepository.class);

            final Locale selectedLocale = SelectedLanguageUtil
                .selectedLocale(state, selectedLanguageParam);

            final MultiPartArticle article
                                       = (MultiPartArticle) selectedArticleModel
                    .getSelectedItem(state);

            // get the selected section to update or create a new one
            final MultiPartArticleSection section;
            if (selectedSectionModel.getSelectedKey(state) == null) {
                section = new MultiPartArticleSection();
                controller.addSection(article);
            } else {
                section = selectedSectionModel.getSelectedSection(state);
            }

            section.getTitle().addValue(selectedLocale,
                                        (String) data.get(TITLE));

            final Object[] pageBreakVal = (Object[]) data.get(PAGE_BREAK);
            final boolean pageBreak;
            if (pageBreakVal == null
                    || pageBreakVal.length == 0
                    || !"true".equals(pageBreakVal[0])) {
                pageBreak = false;
            } else {
                pageBreak = true;
            }
            section.setPageBreak(pageBreak);

            final String text;
            if (data.get(TEXT) == null) {
                text = "";
            } else {
                text = (String) data.get(TEXT);
            }
            section.getText().addValue(selectedLocale, text);

            sectionRepo.save(section);
        }

    }

}
