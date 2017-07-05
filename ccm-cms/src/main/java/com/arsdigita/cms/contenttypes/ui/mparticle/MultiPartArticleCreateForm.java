/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.ApplyWorkflowFormSection;
import com.arsdigita.cms.ui.authoring.CreationComponent;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.LanguageWidget;
import com.arsdigita.globalization.GlobalizedMessage;

import java.util.Date;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contenttypes.MultiPartArticle;

import java.util.Locale;

/**
 * A form which will create a MultiPartArticle or one of its subclasses.
 *
 * @author <a href="mailto:dturner@arsidigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MultiPartArticleCreateForm
    extends MultiPartArticleForm
    implements FormInitListener,
               FormProcessListener,
               FormSubmissionListener,
               FormValidationListener,
               CreationComponent {

    private final CreationSelector creationSelector;
    private ApplyWorkflowFormSection workflowSection;

    public MultiPartArticleCreateForm(
        final ItemSelectionModel itemSelectionModel,
        final CreationSelector creationSelector,
        final StringParameter selectedLanguageParam) {

        super("MultiPartArticleCreate",
              itemSelectionModel,
              selectedLanguageParam);

        this.creationSelector = creationSelector;
        workflowSection.setCreationSelector(creationSelector);
        workflowSection.setContentType(itemSelectionModel.getContentType());
        addSubmissionListener(this);
        getSaveCancelSection()
            .getSaveButton()
            .setButtonLabel(new GlobalizedMessage("cms.ui.create",
                                                  CmsConstants.CMS_BUNDLE));
    }

    @Override
    protected void addWidgets() {
        workflowSection = new ApplyWorkflowFormSection();
        add(workflowSection, ColumnPanel.INSERT);
        add(new Label(new GlobalizedMessage("cms.ui.language.field",
                                            CmsConstants.CMS_BUNDLE)));
        add(new LanguageWidget(LANGUAGE));
        super.addWidgets();
    }

    /**
     * Return the ApplyWorkflowFormSection associated with this
     * CreationComponent.
     *
     * @return the ApplyWorkflowFormSection associated with this
     *         CreationComponent.
     */
    @Override
    public ApplyWorkflowFormSection getWorkflowSection() {
        return workflowSection;
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        // this is currently a no-op
    }

    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            creationSelector.redirectBack(state);
            throw new FormProcessException(
                "Submission cancelled",
                new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle.submission_cancelled",
                    CmsConstants.CMS_BUNDLE)
            );
        }
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        final Folder folder = creationSelector.getFolder(event.getPageState());
        if (!validateNameUniqueness(folder, event)) {
            throw new FormProcessException(
                "An item with this name already exists",
                new GlobalizedMessage(
                    "cms.contenttypes.ui.mparticle."
                        + "an_item_with_this_name_already_exists",
                    CmsConstants.CMS_BUNDLE)
            );
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final ContentSection section = creationSelector.getContentSection(state);
        final Folder folder = creationSelector.getFolder(state);

        final Locale locale = new Locale((String) data.get(LANGUAGE));

        final MultiPartArticle article = createArticle(state,
                                                       (String) data.get(NAME),
                                                       section,
                                                       folder,
                                                       locale);
        article.getTitle().addValue(locale, (String) data.get(TITLE));
        if (!CMSConfig.getConfig().isHideLaunchDate()) {
            article.setLaunchDate((Date) data.get(LAUNCH_DATE));
        }
        article
            .getSummary()
            .addValue(locale, (String) data.get(SUMMARY));

        workflowSection.applyWorkflow(state, article);

        final CdiUtil cdiUtil =CdiUtil.createCdiUtil();
        final ContentItemRepository itemRepo = cdiUtil
            .findBean(ContentItemRepository.class);
        itemRepo.save(article);
        
        creationSelector.editItem(state, article);
    }

}
