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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.SecurityPropertyEditor;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import org.librecms.CMSConfig;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

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
public abstract class TextBody
    extends SecurityPropertyEditor
    implements Resettable, AuthoringStepComponent, RequestListener {

    public static final String FILE_UPLOAD = "file";
    public static final String TEXT_ENTRY = "text";

    private static final String STREAMLINED_DONE = "1";
    private static final CMSConfig CMS_CONFIG = CMSConfig.getConfig();

    private final StringParameter streamlinedCreationParam;
    private final StringParameter selectedLanguageParam;
    private final ItemSelectionModel itemSelectionModel;

    /**
     * Construct a new GenericArticleBody component
     *
     * @param assetModel            The {@link ItemSelectionModel} which will be
     *                              responsible for maintaining the current
     *                              asset
     * @param selectedLanguageParam Parameter for the currently selected locale.
     */
    public TextBody(final ItemSelectionModel assetModel,
                    final StringParameter selectedLanguageParam) {

        this(assetModel, null, selectedLanguageParam);
    }

    /**
     * Construct a new GenericArticleBody component
     *
     * @param itemSelectionModel   The {@link ItemSelectionModel} which will be
     *                             responsible for maintaining the current asset
     * @param authoringKitWizard   The parent wizard which contains the form.
     *                             The form may use the wizard's methods, such
     *                             as stepForward and stepBack, in its process
     *                             listener.
     * @param selectedLangugeParam Parameter for the currently selected locale.
     */
    public TextBody(final ItemSelectionModel itemSelectionModel,
                    final AuthoringKitWizard authoringKitWizard,
                    final StringParameter selectedLangugeParam) {

        super();
        this.itemSelectionModel = itemSelectionModel;

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

        this.selectedLanguageParam = selectedLangugeParam;

        if (!CMS_CONFIG.isHideTextAssetUploadFile()) {
            final PageFileForm pageFileForm = getPageFileForm();
            add(FILE_UPLOAD,
                new GlobalizedMessage("cms.ui.upload", CmsConstants.CMS_BUNDLE),
                pageFileForm,
                pageFileForm.getSaveCancelSection().getCancelButton());
        }

        final PageTextForm pageTextForm = new PageTextForm(this);
        add(TEXT_ENTRY,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            pageTextForm,
            pageTextForm.getSaveCancelSection().getCancelButton());

        // Specify full path to properties of the text asset
        final DomainObjectPropertySheet sheet = getTextBodyPropertySheet(
            itemSelectionModel);
        sheet.add(new GlobalizedMessage("cms.ui.article.text",
                                        CmsConstants.CMS_BUNDLE),
                  getTextPropertyName());

        setDisplayComponent(sheet);

        getDisplayPane().setClassAttr("invertedPropertyDisplay");

    }

    /**
     * Determines the name of the property holding the text.
     *
     * @return The name of the property holding the text.
     */
    protected abstract String getTextPropertyName();

    protected DomainObjectPropertySheet getTextBodyPropertySheet(
        final ItemSelectionModel itemSelectionModel) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemSelectionModel);
        
        return sheet;
        
//        return new TextBodyPropertySheet(itemSelectionModel,
//                                         selectedLanguageParam);
    }

    /**
     * Adds the options for the mime type select widget of
     * <code>GenericArticleForm</code> and sets the default mime type.
     *
     * @param mimeSelect
     */
    protected void setMimeTypeOptions(final SingleSelect mimeSelect) {
        FileUploadSection.addMimeOptions(mimeSelect, "text");
        mimeSelect.setOptionSelected("text/html");
    }

    /**
     * To be overwritten by subclasses, should return the text for the currently
     * selected locale.
     *
     * @param state The current page state
     *
     * @return The text of the currently selected locale.
     */
    public abstract String getText(final PageState state);

    /**
     * Reset this component to its original state
     *
     * @param state the current page state
     */
    @Override
    public void reset(final PageState state) {
        showDisplayPane(state);
    }

    /**
     * Update the text for the currently selected locale.
     *
     * @param state the current page state
     * @param text  the new next text for the currently selected locale.
     */
    protected abstract void updateText(PageState state, String text);

    /**
     * Return the <code>ItemSelectionModel</code> which will be used to maintain
     * the current text asset
     *
     * @return
     */
    public ItemSelectionModel getItemSelectionModel() {
        return itemSelectionModel;
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
    public void pageRequested(final RequestEvent event) {

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
     * @return The form for uploading a text.
     */
    protected PageFileForm getPageFileForm() {
        return new PageFileForm(this);
    }

    protected String getDefaultMimeType() {
        return "text/plain";
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
