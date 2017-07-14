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
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.contenttypes.MultiPartArticle;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contenttypes.MultiPartArticleSection;

import java.util.Locale;

/**
 * A form to confirm deletion of a single section of a MultiPartArticle.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SectionDeleteForm extends Form
    implements FormInitListener, FormSubmissionListener, FormProcessListener {

    private final static Logger LOGGER = LogManager
        .getLogger(SectionDeleteForm.class.getName());

    private final ItemSelectionModel selectedArticleModel;
    private final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel;
    private final SaveCancelSection saveCancelSection;
    private final Label sectionNameLabel;

    private final StringParameter selectedLanguageParam;

    public SectionDeleteForm(
        final ItemSelectionModel selectedArticleModel,
        final SectionSelectionModel<? extends MultiPartArticleSection> selectedSectionModel,
        final StringParameter selectedLanguageParam) {

        super("SectionDeleteForm", new ColumnPanel(2));
        this.selectedArticleModel = selectedArticleModel;
        this.selectedSectionModel = selectedSectionModel;
        this.selectedLanguageParam = selectedLanguageParam;

        final ColumnPanel panel = (ColumnPanel) super.getPanel();
        panel.setBorder(false);
        panel.setPadColor("#FFFFFF");
        panel.setColumnWidth(1, "20%");
        panel.setColumnWidth(2, "80%");
        panel.setWidth("100%");

        sectionNameLabel = new Label(new GlobalizedMessage(
            "cms.contenttypes.ui.mparticle.section_name"));
        super.add(sectionNameLabel, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        saveCancelSection = new SaveCancelSection();
        saveCancelSection
            .getSaveButton()
            .setButtonLabel(new GlobalizedMessage("cms.ui.delete",
                                                  CmsConstants.CMS_BUNDLE));
        super.add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        super.addInitListener(this);
        super.addSubmissionListener(this);
        super.addProcessListener(this);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();

        final MultiPartArticleSection section = selectedSectionModel
            .getSelectedSection(state);

        if (section == null) {
            LOGGER.error("No section selected");
        } else {
            final Locale selectedLocale = SelectedLanguageUtil
                .selectedLocale(state, selectedLanguageParam);

            sectionNameLabel.setLabel(
                section.getTitle().getValue(selectedLocale),
                state);
        }
    }

    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection
            .getCancelButton()
            .isSelected(state)) {

            throw new FormProcessException(
                "Submission cancelled",
                new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.submission_cancelled",
                    CmsConstants.CMS_BUNDLE));
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final MultiPartArticle article = (MultiPartArticle) selectedArticleModel
            .getSelectedObject(state);
        final MultiPartArticleSection section = selectedSectionModel
            .getSelectedSection(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final MultiPartArticleSectionStepController controller = cdiUtil
            .findBean(MultiPartArticleSectionStepController.class);

        controller.removeSection(article, section);

        LOGGER.info("section {} deleted",
                    selectedSectionModel.getSelectedKey(state));
    }

}
