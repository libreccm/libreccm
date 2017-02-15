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

import com.arsdigita.bebop.*;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.*;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.librecms.contentsection.*;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.dispatcher.ItemResolver;

import java.util.Optional;

/**
 * Allows the user to select an index item to display when the front end user is
 * browsing by Category
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
+ */
public class IndexItemSelectionForm extends CMSForm {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryEditForm.class);

    private final CategoryRequestLocal m_category;
    private RadioGroup m_options;
    private static final String NULL_OPTION_VALUE = "";
    private static final String NONE_OPTION_VALUE = "None";

    private SaveCancelSection m_saveCancelSection;

    public IndexItemSelectionForm(CategoryRequestLocal m) {
        super("EditCategory");

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentSectionManager sectionManager =
                cdiUtil.findBean(ContentSectionManager.class);
        final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);
        final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);
        final ContentItemManager contentItemManager = cdiUtil.findBean(ContentItemManager.class);
        final ContentItemRepository contentItemRepository = cdiUtil.findBean(ContentItemRepository.class);

        setMethod(Form.POST);

        m_category = m;

        // Form header
        Label header = new Label(GlobalizationUtil.globalize("cms.ui.category.select_index_item"));
        header.setFontWeight(Label.BOLD);
        add(header, ColumnPanel.FULL_WIDTH);

        // Form errors
        FormErrorDisplay m_errors = new FormErrorDisplay(this);
        add(m_errors, ColumnPanel.FULL_WIDTH);

        // Option Group
        m_options = new RadioGroup(new StringParameter("items"));
        try {
            m_options.addPrintListener(event -> {
                RadioGroup group = (RadioGroup) event.getTarget();
                PageState state = event.getPageState();
                Category category = getCategory(event.getPageState());
                java.util.List<Categorization> children = category.getObjects();

                group.clearOptions();

                // option for NO index Object
                group.addOption(new Option(NONE_OPTION_VALUE,
                        new Label(GlobalizationUtil.globalize("cms.ui.category.non_option"))));

                // option for inheriting from the parent category
                if (category.getParentCategory() != null) {
                    group.addOption(new Option(NULL_OPTION_VALUE,
                            new Label(GlobalizationUtil.globalize("cms.ui.category.inherit_parent"))));
                }

                final ContentSection section = CMS.getContext().getContentSection();
                final ItemResolver itemResolver = sectionManager.getItemResolver(
                        section);
                for (Categorization child : children) {
                    ContentItem item = (ContentItem) child.getCategorizedObject();
                    Link link = new Link(
                            new Text(item.getDisplayName()),
                            itemResolver.generateItemURL(
                                    state,
                                    item.getObjectId(),
                                    item.getDisplayName(),
                                    section,
                                    item.getVersion().name()
                            )
                    );
                    Component linkComponent = link;
                    //add the option with the link
                    group.addOption(new Option(item.getItemUuid(),
                            linkComponent));
                }

                // get currently selected item
                Optional<CcmObject> optionalIndexObject = categoryManager.getIndexObject(category);
                if (optionalIndexObject.isPresent()) {
                    ContentItem indexItem = (ContentItem) optionalIndexObject.get();
                    group.setValue(
                            state,
                            contentItemManager.getDraftVersion(indexItem, ContentItem.class).getItemUuid()
                    );
                } else {
                    String value = category.getParentCategory() != null
                            ? NULL_OPTION_VALUE
                            : NONE_OPTION_VALUE;
                    group.setValue(state, value);
                }
            });
        } catch (java.util.TooManyListenersException e) {
            LOGGER.error("Error adding init listener to Radio Group", e);
            throw new UncheckedWrapperException(e);
        }
        m_options.setLayout(RadioGroup.VERTICAL);
        add(m_options);

        // Save and cancel buttons
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addSubmissionListener(new FormSecurityListener(AdminPrivileges.ADMINISTER_CATEGORIES));

        // Process listener
        addProcessListener(new FormProcessListener() {
            public void process(FormSectionEvent event)
                    throws FormProcessException {
                PageState state = event.getPageState();
                FormData data = event.getFormData();
                ParameterData param = data.getParameter(m_options.getParameterModel().getName());
                String selectedValue = (String) param.getValue();

                Category category
                        = getCategory(event.getPageState());

                if (selectedValue != null) {
                    Optional<ContentItem> optionalItem = contentItemRepository.findById(Long.parseLong(selectedValue));
                    if (optionalItem.isPresent()) {
                        ContentItem item = contentItemManager.getDraftVersion(optionalItem.get(), ContentItem.class);
                        try {
                            categoryManager.setIndexObject(category, item);
                            categoryRepository.save(category);
                        } catch (ObjectNotAssignedToCategoryException e) {
                            throw new FormProcessException(e);
                        }
                    }
                }

            }
        });
    }

    /**
     * Get the cancel button.
     *
     * @return The cancel button
     */
    protected Submit getCancelButton() {
        return m_saveCancelSection.getCancelButton();
    }

    /**
     * Fetch the selected category.
     *
     * @param state The page state
     * @return The selected category
     * @pre ( state != null )
     */
    protected Category getCategory(PageState state) {
        Category category = m_category.getCategory(state);
        Assert.exists(category);
        return category;
    }

}
