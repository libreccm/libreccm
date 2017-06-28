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
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItemInitializer;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentType;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * A form for editing basic properties of documents (that is subclasses of class
 * ContentPage).
 *
 * Document specific classes inherit from this class which provides the basic
 * widgets for title, name launch date to use by those classes.
 *
 * This is just a convenience class. It uses parent class to construct the form
 * including basic widgets (i.e. title and name/URL as well as save/cancel
 * buttons) and adds optional launch date.
 *
 * Note: It is for editing existing content (specifically due to its validation
 * method).
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class BasicPageForm extends BasicItemForm {

    private static final String LAUNCH_DATE = "launch_date";

    private final StringParameter selectedLanguageParam;

    /**
     * Construct a new BasicPageForm
     *
     * @param formName              the name of this form
     * @param itemModel             The {@link ItemSelectionModel} which will be
     *                              responsible for loading the current item
     * @param selectedLanguageParam
     */
    public BasicPageForm(final String formName,
                         final ItemSelectionModel itemModel,
                         final StringParameter selectedLanguageParam) {

        super(formName, itemModel, selectedLanguageParam);
        Objects.requireNonNull(selectedLanguageParam);
        this.selectedLanguageParam = selectedLanguageParam;
    }

    /**
     * Construct a new BasicPageForm with nothing on it
     *
     * @param formName              the name of this form
     * @param columnPanel           the column panel of the form
     * @param itemModel             The {@link ItemSelectionModel} which will be
     *                              responsible for loading the current item
     * @param selectedLanguageParam
     */
    public BasicPageForm(final String formName,
                         final ColumnPanel columnPanel,
                         final ItemSelectionModel itemModel,
                         final StringParameter selectedLanguageParam) {

        super(formName, columnPanel, itemModel, selectedLanguageParam);
        this.selectedLanguageParam = selectedLanguageParam;
    }

    /**
     * Add various widgets to the form. Child classes should override this
     * method to perform all their widget-adding needs
     */
    @Override
    protected void addWidgets() {

        /* Add basic widgets title/name which are part of any content item    */
        super.addWidgets();

        final CMSConfig cmsConfig = CMSConfig.getConfig();

        /* Optionally add Lunchdate                                           */
        if (!cmsConfig.isHideLaunchDate()) {
            add(new Label(new GlobalizedMessage(
                "cms.ui.authoring.page_launch_date",
                CmsConstants.CMS_BUNDLE)));
            final ParameterModel launchDateParam
                                     = new DateParameter(LAUNCH_DATE);
            com.arsdigita.bebop.form.Date launchDate
                                              = new com.arsdigita.bebop.form.Date(
                    launchDateParam);
            if (CMSConfig.getConfig().isRequireLaunchDate()) {
                launchDate.addValidationListener(
                    new LaunchDateValidationListener());
                // if launch date is required, help user by suggesting today's date
                launchDateParam.setDefaultValue(new Date());
            }
            add(launchDate);
        }
    }

    /**
     * Utility method to initialise the name/title widgets. Child classes may
     * call this method from the init listener
     *
     * @param event the {@link FormSectionEvent} which was passed to the init
     *              listener
     *
     * @return the ContentPage instance which was extracted from the
     *         ItemSelectionModel
     */
    public ContentItem initBasicWidgets(final FormSectionEvent event) {
        Assert.exists(getItemSelectionModel());

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final ContentItem item = getItemSelectionModel()
            .getSelectedObject(state);

        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        final Locale selectedLocale;
        if (selectedLanguage == null) {
            selectedLocale = KernelConfig.getConfig().getDefaultLocale();
        } else {
            selectedLocale = new Locale(selectedLanguage);
        }

        if (item != null) {
            // Preset fields
            data.put(CONTENT_ITEM_ID, Long.toString(item.getObjectId()));
            data.put(NAME, item.getName().getValue(selectedLocale));
            data.put(TITLE, item.getTitle().getValue(selectedLocale));
            final CMSConfig cmsConfig = CMSConfig.getConfig();
            if (!cmsConfig.isHideLaunchDate()) {
                data.put(LAUNCH_DATE, item.getLaunchDate());
                // if launch date is required, help user by suggesting today's date
                if (cmsConfig.isRequireLaunchDate()
                        && item.getLaunchDate() == null) {
                    data.put(LAUNCH_DATE, new Date());
                }
            }
        }

        return item;
    }

    /**
     * Class specific implementation of FormValidationListener interface
     * (inherited from BasicItemForm).
     *
     * @param fse
     *
     * @throws FormProcessException
     */
    @Override
    public void validate(final FormSectionEvent fse) throws FormProcessException {

        super.validate(fse); //noop, BasicItemForm#validate does nothing

        final ContentItem item = getItemSelectionModel()
            .getSelectedItem(fse.getPageState());
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentItemManager itemManager = cdiUtil
            .findBean(ContentItemManager.class);

        final Optional<Folder> folder = itemManager.getItemFolder(item);
        if (folder.isPresent()) {
            final String name = fse.getFormData().getString(NAME);
            if (!item.getName()
                .getValue(KernelConfig.getConfig().getDefaultLocale())
                .equals(name)) {
                validateNameUniqueness(folder.get(), fse);
            }
        }
    }

    /**
     * Utility method to process the name/title widgets. Child classes may call
     * this method from the process listener.
     *
     * @param event the {@link FormSectionEvent} which was passed to the process
     *              listener
     *
     * @return
     */
    public ContentItem processBasicWidgets(final FormSectionEvent event) {
        Assert.exists(getItemSelectionModel());

        final FormData data = event.getFormData();
        final PageState state = event.getPageState();
        final ContentItem item = getItemSelectionModel()
            .getSelectedObject(state);

        if (item != null) {
            // Update attributes
            final String selectedLanguage = (String) state
                .getValue(selectedLanguageParam);
            final Locale selectedLocale;
            if (selectedLanguage == null) {
                selectedLocale = KernelConfig.getConfig().getDefaultLocale();
            } else {
                selectedLocale = new Locale(selectedLanguage);
            }

            item.getName().addValue(selectedLocale, (String) data.get(NAME));
            item.getTitle().addValue(selectedLocale, (String) data.get(TITLE));
            if (!CMSConfig.getConfig().isHideLaunchDate()) {
                item.setLaunchDate((Date) data.get(LAUNCH_DATE));
            }
        }

        return item;
    }

    /**
     * A utility method that will create a new item and tell the selection model
     * to select the new item.
     *
     * Creation components may call this method in the process listener of their
     * form. See {@link PageCreate} for an example.
     *
     * @param <T>
     * @param state
     * @param name
     * @param section
     * @param folder
     * @param initializer
     *
     * @return the new content item (or a proper subclass thereof)
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    public <T extends ContentItem> T createContentPage(
        final PageState state,
        final String name,
        final ContentSection section,
        final Folder folder,
        final ContentItemInitializer<T> initializer) throws FormProcessException {

//        final ItemSelectionModel selectionModel = getItemSelectionModel();
//        final ContentType contentType = selectionModel.getContentType();
//
//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final ContentItemManager itemManager = cdiUtil
//            .findBean(ContentItemManager.class);
//
//        // Create new item
//        final ContentItem item;
//        try {
//            @SuppressWarnings("unchecked")
//            final Class<? extends ContentItem> clazz
//                                                   = (Class<? extends ContentItem>) Class
//                    .forName(contentType.getContentItemClass());
//            item = itemManager.createContentItem(name,
//                                                 section,
//                                                 folder,
//                                                 clazz,
//                                                 initializer);
//        } catch (ClassNotFoundException ex) {
//            throw new FormProcessException(
//                "Couldn't create contentpage",
//                new GlobalizedMessage(
//                    "cms.ui.authoring.couldnt_create_contentpage",
//                    CmsConstants.CMS_BUNDLE),
//                ex);
//        }
//
//        // Create new item
//        // Make sure the item will be remembered across requests
//        selectionModel.setSelectedKey(state, item.getObjectId());
//
//        return item;
        return createContentItemPage(state,
                                     name,
                                     section,
                                     folder,
                                     null,
                                     initializer);
    }

    public <T extends ContentItem> T createContentItemPage(
        final PageState state,
        final String name,
        final ContentSection section,
        final Folder folder,
        final WorkflowTemplate workflowTemplate,
        final ContentItemInitializer<T> initializer) throws FormProcessException {

        final ItemSelectionModel selectionModel = getItemSelectionModel();
        final ContentType contentType = selectionModel.getContentType();

        // Create new item
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final ContentItemManager itemManager = cdiUtil
//            .findBean(ContentItemManager.class);
        final BasicPageFormController controller = cdiUtil
            .findBean(BasicPageFormController.class);

        final T item;
        try {
            @SuppressWarnings("unchecked")
            final Class<T> clazz = (Class<T>) Class
                .forName(contentType.getContentItemClass());

//            @SuppressWarnings("unchecked")
//            final Class<? extends ContentItem> clazz
//                                                   = (Class<? extends ContentItem>) Class
//                    .forName(contentType.getContentItemClass());
//            
            if (workflowTemplate == null) {
//                item = itemManager.createContentItem(name,
//                                                     section,
//                                                     folder,
//                                                     clazz,
//                                                     initializer);
                item = controller
                    .createContentItem(name, section, folder, clazz,
                                       initializer);
            } else {
//                item = itemManager.createContentItem(name,
//                                                     section,
//                                                     folder,
//                                                     workflowTemplate,
//                                                     clazz,
//                                                     initializer);
                item = controller.createContentItem(name,
                                                    section,
                                                    folder,
                                                    workflowTemplate,
                                                    clazz,
                                                    initializer);
            }
        } catch (ClassNotFoundException ex) {
            throw new FormProcessException(
                "Couldn't create contentpage",
                new GlobalizedMessage(
                    "cms.ui.authoring.couldnt_create_contentpage",
                    CmsConstants.CMS_BUNDLE),
                ex);
        }

        // Make sure the item will be remembered across requests
        selectionModel.setSelectedKey(state, item.getObjectId());

        return item;
    }

    /**
     * Constructs a new <code>LaunchDateValidationListener</code>.
     */
    private class LaunchDateValidationListener implements ParameterListener {

        @Override
        public void validate(final ParameterEvent event) {

            final ParameterData data = event.getParameterData();
            final Object value = data.getValue();

            if (value == null) {
                data.addError("launch date is required");
            }
        }

    }

}
