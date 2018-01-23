/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.KernelConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * TODO Needs a description.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
final class CategoryEditForm extends BaseCategoryForm {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryEditForm.class);

    private static final String NO = "no";
    private static final String YES = "yes";
    
    private final CategoryRequestLocal selectedCategory;

    public CategoryEditForm(final CategoryRequestLocal parent,
                            final CategoryRequestLocal selectedCategory) {
        super("EditCategory", gz("cms.ui.category.edit"), parent);

        this.selectedCategory = selectedCategory;

        super.addInitListener(new InitListener());
        super.addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event)
            throws FormProcessException {
            
            final PageState state = event.getPageState();
            final Category category = selectedCategory.getCategory(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final GlobalizationHelper globalizationHelper = cdiUtil
                .findBean(GlobalizationHelper.class);

            getNameField().setValue(state, category.getName());
            final LocalizedString description = category.getDescription();
            getDescriptionArea()
                .setValue(state,
                          globalizationHelper
                              .getValueFromLocalizedString(description));
            // this seems anti-intuitive but the question is "can you place
            // items in this category.  If the user says "yes" then the
            // category is not abstract
            if (category.isAbstractCategory()) {
                getIsAbstractRadioGroup().setValue(state, NO);
            } else {
                getIsAbstractRadioGroup().setValue(state, YES);
            }

            if (category.isVisible()) {
                getIsVisibleRadioGroup().setValue(state, YES);
            } else {
                getIsVisibleRadioGroup().setValue(state, NO);
            }

            if (category.isEnabled()) {
                getIsEnabledRadioGroup().setValue(state, YES);
            } else {
                getIsEnabledRadioGroup().setValue(state, NO);
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            final ConfigurationManager manager = cdiUtil.findBean(
                ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(
                KernelConfig.class);
            final CategoryRepository categoryRepository = cdiUtil.findBean(
                CategoryRepository.class);

            final PageState state = event.getPageState();
            final Category category = selectedCategory.getCategory(state);

            if (permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CATEGORIES, category)) {
                category.setName((String) getNameField().getValue(state));
                final LocalizedString localizedDescription
                                          = new LocalizedString();
                localizedDescription
                    .addValue(config.getDefaultLocale(),
                              (String) getDescriptionArea().getValue(state));
                category.setDescription(localizedDescription);

                final String isAbstract = (String) getIsAbstractRadioGroup()
                    .getValue(state);
                // this seems anti-intuitive but the question is "can you place
                // items in this category.  If the user says "yes" then the
                // category is not abstract
                if (YES.equals(isAbstract)) {
                    category.setAbstractCategory(false);
                } else if (NO.equals(isAbstract)) {
                    category.setAbstractCategory(true);
                }

                final String isVisible = (String) getIsVisibleRadioGroup()
                    .getValue(state);
                if (YES.equals(isVisible)) {
                    category.setVisible(true);
                } else {
                    category.setVisible(false);
                }

                final String isEnabled = (String) getIsEnabledRadioGroup()
                    .getValue(state);
                if (YES.equals(isEnabled)) {
                    category.setEnabled(true);
                } else if (NO.equals(isEnabled)) {
                    category.setEnabled(false);
                }

                categoryRepository.save(category);
                
            } else {
                throw new AccessDeniedException();
            }
        }

    }

}
