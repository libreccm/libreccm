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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.workflow.WorkflowTemplate;
import org.libreccm.workflow.WorkflowTemplateRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemInitializer;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A form which will create a new document (that is subclasses of class
 * ContentPage).
 *
 * Used to create a new document / content item. Creates widgets to select the
 * workflow, and language. It displays the type of document as well. Super class
 * adds additional widgets (title and name/URL) to complete the form.
 *
 * It's a pane which is part of a more complex page, additionally containing
 * folder structure, content items in the folder, permissions, etc.
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PageCreateForm
    extends BasicPageForm
    implements FormSubmissionListener, CreationComponent {

    private final CreationSelector creationSelector;
    private ApplyWorkflowFormSection workflowSection;

    /**
     * The state parameter which specifies the content section
     */
    public static final String SECTION_ID = "sid";

    /**
     * Construct a new PageCreationForm
     *
     * @param itemModel        The {@link ItemSelectionModel} which will be
     *                         responsible for loading the current item
     * @param creationSelector The {@link CreationSelector} parent. This class
     *                         should call either the {@link
     *                  CreationSelector#redirectBack(PageState)} or {@link
     *                  CreationSelector#editItem(PageState, ContentItem)} methods on the parent
     *                         eventually
     */
    public PageCreateForm(final ItemSelectionModel itemModel,
                          final CreationSelector creationSelector) {

        super("PageCreate", itemModel);

        this.creationSelector = creationSelector;

        workflowSection.setCreationSelector(creationSelector);
        addSubmissionListener(this);

        getSaveCancelSection().getSaveButton()
            .setButtonLabel(new GlobalizedMessage("cms.ui.create",
                                                  CmsConstants.CMS_BUNDLE));
    }

    /**
     * Add various widgets to the form. Child classes should override this
     * method to perform all their widget-adding needs.
     */
    @Override
    protected void addWidgets() {

        /* Retrieve Content Type  */
        final ContentType type = getItemSelectionModel().getContentType();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypesManager typesManager = cdiUtil
            .findBean(ContentTypesManager.class);
        final ContentTypeInfo typeInfo = typesManager.getContentTypeInfo(type);

        /* Add workflow selection based on configured Content Type            */
        workflowSection = new ApplyWorkflowFormSection(type);
        add(workflowSection, ColumnPanel.INSERT);
        /* content type */
        add(new Label(new GlobalizedMessage("cms.ui.authoring.content_type",
                                            CmsConstants.CMS_BUNDLE)));
        final Label typeOutput = new Label(
            new ContentTypePrintListener(typeInfo));
        add(typeOutput);
        /* language selection   */
        add(new Label(new GlobalizedMessage("cms.ui.language.field",
                                            CmsConstants.CMS_BUNDLE)));
        add(new LanguageWidget(LANGUAGE));

        /* Additional widgets from super type: title and name (url)   */
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

    /**
     * Create a new item id.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        // this is currently a no-op
    }

    /**
     * If the Cancel button was pressed, hide self and show the display
     * component.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (getSaveCancelSection().getCancelButton().isSelected(state)) {
            creationSelector.redirectBack(state);

            throw new FormProcessException(
                new GlobalizedMessage("cms.ui.authoring.submission_cancelled",
                                      CmsConstants.CMS_BUNDLE));
        }
    }

    /**
     * Validate inputs to ensure name uniqueness. Note: We can't call
     * {@code super.validate(FormSectionEvent)} here because the super method
     * {@link BasicPageForm#validate(com.arsdigita.bebop.event.FormSectionEvent)}
     * tries to access things which on existing yet.
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {

        final Folder folder = creationSelector.getFolder(event.getPageState());
        validateNameUniqueness(folder, event);
    }

    /**
     * Class specific implementation if FormProcessListener (as inherited from
     * BasicItemForm), saves fields to the database.
     *
     * @param event
     *
     * @throws FormProcessException
     */
    @Override
    public final void process(final FormSectionEvent event)
        throws FormProcessException {

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final ContentSection section = creationSelector.getContentSection(state);
        final Folder folder = creationSelector.getFolder(state);

        Assert.exists(section, ContentSection.class);

        final Long selectedWorkflowTemplateId = workflowSection
            .getSelectedWorkflowTemplateId(state);

        final ContentItem item;
        if (selectedWorkflowTemplateId == null) {
            item = createContentPage(state,
                                     (String) data.get(NAME),
                                     section,
                                     folder,
                                     getItemInitializer(data, state));
        } else {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final WorkflowTemplateRepository workflowTemplateRepo = cdiUtil
                .findBean(WorkflowTemplateRepository.class);
            final WorkflowTemplate workflowTemplate = workflowTemplateRepo
                .findById(selectedWorkflowTemplateId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                "No WorkflowTemplate with ID %d in the database.",
                selectedWorkflowTemplateId)));

            item = createContentItemPage(state,
                                         (String) data.get(NAME),
                                         section,
                                         folder,
                                         workflowTemplate,
                                         getItemInitializer(data, state));
        }

        final Locale locale = new Locale((String) data.get(LANGUAGE));
        item.getName().addValue(locale, (String) data.get(NAME));
        item.getTitle().addValue(locale, (String) data.get(TITLE));

//        workflowSection.applyWorkflow(state, item);
        creationSelector.editItem(state, item);
    }

    protected ContentItemInitializer<?> getItemInitializer(
        final FormData data, final PageState state) {

        return item -> {
        };
    }

    private class ContentTypePrintListener implements PrintListener {

        private final ContentTypeInfo typeInfo;

        public ContentTypePrintListener(final ContentTypeInfo typeInfo) {
            this.typeInfo = typeInfo;
        }

        @Override
        public void prepare(final PrintEvent event) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);
            final ResourceBundle bundle = ResourceBundle
                .getBundle(typeInfo.getLabelBundle(),
                           globalizationHelper.getNegotiatedLocale());

            final String typeLabel = bundle.getString(typeInfo.getLabelKey());

            final Label target = (Label) event.getTarget();
            target.setLabel(typeLabel);
        }

    }

}
