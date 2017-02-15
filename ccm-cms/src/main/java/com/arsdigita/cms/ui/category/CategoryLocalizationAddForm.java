/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.KernelConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Collection;
import java.util.Locale;

/**
 * Generates a form for creating new localisations for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryLocalizationAddForm extends CategoryLocalizationForm {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryLocalizationAddForm.class);

    /** Creates a new instance of CategoryLocalizationAddForm */
    public CategoryLocalizationAddForm(final CategoryRequestLocal category) {

        super("AddCategoryLocalization",
                gz("cms.ui.category.localization_add"), category);

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());

    }

    // Deaktivate this widget, if category is root
//    public boolean isVisible(PageState state) {
//        return !m_category.getCategory(state).isRoot();
//    }
    private class InitListener implements FormInitListener {

        public final void init(final FormSectionEvent e)
                throws FormProcessException {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ConfigurationManager manager = cdiUtil.findBean(
                    ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(
                    KernelConfig.class);

            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            // Select one entry
            m_locale.addOption(new Option("",
                    new Label(GlobalizationUtil.globalize(
                              "cms.ui.select_one"))), state);
            final Collection<String> locales = config.getSupportedLanguages();
            if (locales != null) {
                for (String locale : locales) {
                    m_locale.addOption(new Option(locale,
                            new Text(new Locale(locale).getDisplayLanguage())), state);
                }
            }
        }
    }

    private final class ProcessListener implements FormProcessListener {

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            LOGGER.debug("Adding a categoryLocalization to category " + m_category);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
            final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);

            final PageState state = e.getPageState();

            final Category category = m_category.getCategory(state);
            final Locale locale = new Locale((String) m_locale.getValue(state));
            final String title = (String) m_title.getValue(state);
            final String description = (String) m_description.getValue(state);
            final String url = (String) m_url.getValue(state);
            final String isEnabled = (String) m_isEnabled.getValue(state);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding localization for locale " + locale
                        + " to category " + category);
            }

            if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, category)) {
                category.getTitle().addValue(locale, title);
                category.getDescription().addValue(locale, description);
                category.setEnabled(isEnabled.equals("yes"));
                categoryRepository.save(category);

            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }
    }
}
