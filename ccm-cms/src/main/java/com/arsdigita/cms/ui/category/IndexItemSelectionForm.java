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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.GlobalizedMessagesUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;

import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.dispatcher.ItemResolver;

import java.util.List;
import java.util.Optional;
import java.util.TooManyListenersException;

/**
 * Allows the user to select an index item to display when the front end user is
 * browsing by Category
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IndexItemSelectionForm extends CMSForm {

    private static final Logger LOGGER = LogManager.getLogger(
        IndexItemSelectionForm.class);

    private static final String NULL_OPTION_VALUE = "";
    private static final String NONE_OPTION_VALUE = "None";

    private final CategoryRequestLocal selectedCategory;
    private RadioGroup optionsGroup;
    private final SaveCancelSection saveCancelSection;

    public IndexItemSelectionForm(final CategoryRequestLocal selectedCategory) {

        super("EditCategory");
        super.setMethod(Form.POST);

        this.selectedCategory = selectedCategory;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);
        final GlobalizedMessagesUtil messagesUtil = globalizationHelper
            .getGlobalizedMessagesUtil(CmsConstants.CMS_BUNDLE);

        // Form header
        final Label header = new Label(messagesUtil
            .getGlobalizedMessage("cms.ui.category.select_index_item"));
        header.setFontWeight(Label.BOLD);
        super.add(header, ColumnPanel.FULL_WIDTH);

        // Form errors
        final FormErrorDisplay errorsDisplay = new FormErrorDisplay(this);
        super.add(errorsDisplay, ColumnPanel.FULL_WIDTH);

        // Option Group
        optionsGroup = new RadioGroup(new StringParameter("items"));
        try {
            optionsGroup.addPrintListener(this::printOptionsGroup);
        } catch (TooManyListenersException ex) {
            LOGGER.error("Error adding init listener to Radio Group", ex);
            throw new UnexpectedErrorException(ex);
        }
        optionsGroup.setLayout(RadioGroup.VERTICAL);
        super.add(optionsGroup);

        // Save and cancel buttons
        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        super.addSubmissionListener(new FormSecurityListener(
            AdminPrivileges.ADMINISTER_CATEGORIES));

        // Process listener
        super.addProcessListener(this::process);
    }

    private void printOptionsGroup(final PrintEvent event) {

        final RadioGroup group = (RadioGroup) event.getTarget();
        final PageState state = event.getPageState();
        final Category category = getCategory(event.getPageState());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryAdminController controller = cdiUtil
            .findBean(CategoryAdminController.class);
        final ContentItemManager itemManager = cdiUtil
            .findBean(ContentItemManager.class);
        final CategoryManager categoryManager = cdiUtil
            .findBean(CategoryManager.class);
        final ContentSectionManager sectionManager = cdiUtil
            .findBean(ContentSectionManager.class);
        final GlobalizationHelper globalizationHelper = cdiUtil
            .findBean(GlobalizationHelper.class);
        final GlobalizedMessagesUtil messagesUtil = globalizationHelper
            .getGlobalizedMessagesUtil(CmsConstants.CMS_BUNDLE);

        group.clearOptions();

        // option for NO index Object
        group.addOption(
            new Option(NONE_OPTION_VALUE,
                       new Label(messagesUtil
                           .getGlobalizedMessage("cms.ui.category.non_option"))));

        // option for inheriting from the parent category
        if (category.getParentCategory() != null) {
            group.addOption(
                new Option(NULL_OPTION_VALUE,
                           new Label(messagesUtil
                               .getGlobalizedMessage(
                                   "cms.ui.category.inherit_parent"))));
        }

        final ContentSection section = CMS.getContext()
            .getContentSection();
        final ItemResolver itemResolver = sectionManager
            .getItemResolver(section);

        final List<ContentItem> assignedItems = controller
            .retrieveAssignedContentItems(category);
        for (final ContentItem item : assignedItems) {

            final Link link = new Link(
                new Text(item.getDisplayName()),
                itemResolver.generateItemURL(
                    state,
                    item.getObjectId(),
                    item.getDisplayName(),
                    section,
                    item.getVersion().name()
                )
            );
            //add the option with the link
            group.addOption(new Option(Long.toString(item.getObjectId()), link));
        }

        // get currently selected item
        final Optional<CcmObject> optionalIndexObject = categoryManager
            .getIndexObject(category)
            .stream()
            .findFirst();
        if (optionalIndexObject.isPresent()) {
            final ContentItem indexItem
                                  = (ContentItem) optionalIndexObject
                    .get();
            final ContentItem liveItem = itemManager
                .getLiveVersion(indexItem, ContentItem.class)
                .get();
            group.setValue(
                state,
                Long.toString(liveItem.getObjectId()));
        } else {
            final String value;
            if (category.getParentCategory() == null) {
                value = NULL_OPTION_VALUE;
            } else {
                value = NONE_OPTION_VALUE;
            }
            group.setValue(state, value);
        }
    }

    private void process(final FormSectionEvent event)
        throws FormProcessException {

        final FormData data = event.getFormData();
        final ParameterData param = data
            .getParameter(optionsGroup.getParameterModel().getName());
        final String selectedValue = (String) param.getValue();

        final Category category = getCategory(event.getPageState());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryManager categoryManager = cdiUtil
            .findBean(CategoryManager.class);
        final CategoryRepository categoryRepository = cdiUtil
            .findBean(CategoryRepository.class);
        final ContentItemManager contentItemManager = cdiUtil
            .findBean(ContentItemManager.class);
        final ContentItemRepository contentItemRepository = cdiUtil
            .findBean(ContentItemRepository.class);

        if (selectedValue != null) {
            final Optional<ContentItem> optionalItem = contentItemRepository
                .findById(Long.parseLong(selectedValue));
            if (optionalItem.isPresent()) {
                final ContentItem item = contentItemManager
                    .getLiveVersion(optionalItem.get(),
                                    ContentItem.class)
                    .get();
                try {
                    categoryManager.setIndexObject(category, item);
                    categoryRepository.save(category);
                } catch (ObjectNotAssignedToCategoryException ex) {
                    throw new FormProcessException(ex);
                }
            }
        }
    }

    /**
     * Get the cancel button.
     *
     * @return The cancel button
     */
    protected Submit getCancelButton() {
        return saveCancelSection.getCancelButton();
    }

    /**
     * Fetch the selected category.
     *
     * @param state The page state
     *
     * @return The selected category
     *
     * @pre ( state != null )
     */
    protected Category getCategory(final PageState state) {
        return selectedCategory.getCategory(state);
    }

}
