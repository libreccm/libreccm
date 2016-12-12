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
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategorizedCollection;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;

/**
 * Allows the user to select an index item to display when the front end user is
 * browsing by Category
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Revision: #18 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class IndexItemSelectionForm extends CMSForm {

    private static org.apache.log4j.Logger s_log
            = org.apache.log4j.Logger.getLogger(
                    IndexItemSelectionForm.class);
    private final CategoryRequestLocal m_category;
    private RadioGroup m_options;
    private static final String NULL_OPTION_VALUE = "";
    private static final String NONE_OPTION_VALUE = "None";
    private FormErrorDisplay m_errors;
    private SaveCancelSection m_saveCancelSection;

    public IndexItemSelectionForm(CategoryRequestLocal m) {
        super("EditCategory");
        setMethod(Form.POST);

        m_category = m;

        // Form header
        Label header = new Label(GlobalizationUtil.globalize("cms.ui.category.select_index_item"));
        header.setFontWeight(Label.BOLD);
        add(header, ColumnPanel.FULL_WIDTH);

        // Form errors
        m_errors = new FormErrorDisplay(this);
        add(m_errors, ColumnPanel.FULL_WIDTH);

        // Option Group
        m_options = new RadioGroup(new StringParameter("items"));
        try {
            m_options.addPrintListener(new PrintListener() {
                public void prepare(PrintEvent event) {
                    RadioGroup group = (RadioGroup) event.getTarget();
                    PageState state = event.getPageState();
                    Category category = getCategory(event.getPageState());
                    CategorizedCollection children = category.getObjects(
                            ContentItem.BASE_DATA_OBJECT_TYPE);

                    group.clearOptions();

                    // option for NO index Object
                    group.addOption(new Option(NONE_OPTION_VALUE,
                            new Label(NONE_OPTION_VALUE)));

                    // option for inheriting from the parent category
                    if (category.getParentCategoryCount() > 0) {
                        group.addOption(new Option(NULL_OPTION_VALUE,
                                new Label("Inherit Index from Parent Category")));
                    }

                    while (children.next()) {
                        ACSObject item
                                = (ACSObject) children.getDomainObject();

                        if ((item instanceof ContentItem) && ((ContentItem) item).getVersion().
                                equals(ContentItem.DRAFT)) {
                            
                            //create a link to the item:
                            ContentBundle bundleItem = (ContentBundle) item;
                            ContentSection section = bundleItem.getContentSection();
                            ItemResolver resolver = section.getItemResolver();

                            Link link = new Link(
                                    bundleItem.getDisplayName(),
                                    resolver.generateItemURL(state,
                                            ((ContentBundle) bundleItem.getDraftVersion()).getPrimaryInstance(),
                                            section,
                                            ((ContentBundle) bundleItem.getDraftVersion()).getPrimaryInstance().getVersion()));
                            Component linkComponent = link;
                            //add the option with the link
                            group.addOption(new Option(item.getID().toString(),
                                    linkComponent));
                        }
                    }
                    // get currently selected item
                    ACSObject indexItem = category.getDirectIndexObject();
                    if (indexItem != null && indexItem instanceof ContentItem) {
                        group.setValue(state, ((ContentItem) indexItem)
                                .getWorkingVersion()
                                .getID().toString());
                    } else {
                        String value = NONE_OPTION_VALUE;
                        if (!category.ignoreParentIndexItem()
                                && category.getParentCategoryCount() > 0) {
                            value = NULL_OPTION_VALUE;
                        }
                        group.setValue(state, value);
                    }
                }

            });
        } catch (java.util.TooManyListenersException e) {
            s_log.error("Error adding init listener to Radio Group", e);
            throw new UncheckedWrapperException(e);
        }
        m_options.setLayout(RadioGroup.VERTICAL);
        add(m_options);

        // Save and cancel buttons
        m_saveCancelSection = new SaveCancelSection();
        add(m_saveCancelSection, ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);

        addSubmissionListener(new FormSecurityListener(SecurityManager.CATEGORY_ADMIN));

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

                ContentItem item = null;
                if (selectedValue != null) {
                    if (NULL_OPTION_VALUE.equals(selectedValue)) {
                        category.setIgnoreParentIndexItem(false);
                        selectedValue = null;
                    } else if (NONE_OPTION_VALUE.equals(selectedValue)) {
                        category.setIgnoreParentIndexItem(true);
                        selectedValue = null;

                    } else {
                        item = new ContentItem(new BigDecimal(selectedValue));
                        item = item.getWorkingVersion();
                    }
                }
                category.setIndexObject(item);
                category.save();
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
