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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.categorization.Category;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelConfig;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Generates a form for creating new localisations for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id: CategoryLocalizationAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class CategoryLocalizationAddForm extends CategoryLocalizationForm {

    private static final Logger s_log = Logger.getLogger(CategoryAddForm.class);

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

            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            // Select one entry
            m_locale.addOption(new Option("",
                    new Label(GlobalizationUtil.globalize(
                              "cms.ui.select_one"))), state);

            // all supported languages (by registry entry)
            KernelConfig kernelConfig = Kernel.getConfig();
            StringTokenizer strTok = kernelConfig.getSupportedLanguagesTokenizer();

            while (strTok.hasMoreTokens()) {

                String code = strTok.nextToken();

                // If lanuage exists, remove it from the selection list
                if (!category.getCategoryLocalizationCollection().
                        localizationExists(code)) {
                    m_locale.addOption(new Option(code,
                            new Locale(code).getDisplayLanguage()), state);
                }
            }
        }
    }

    private final class ProcessListener implements FormProcessListener {

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            s_log.debug("Adding a categoryLocalization to category " + m_category);

            final PageState state = e.getPageState();

            final Category category = m_category.getCategory(state);
            final String locale = (String) m_locale.getValue(state);
            final String name = (String) m_name.getValue(state);
            final String description = (String) m_description.getValue(state);
            final String url = (String) m_url.getValue(state);
            final String isEnabled = (String) m_isEnabled.getValue(state);

            // Was soll das??
            //Assert.assertNotNull(parent, "Category parent");

            if (s_log.isDebugEnabled()) {
                s_log.debug("Adding localization for locale " + locale
                        + " to category " + category);
            }

            if (category.canEdit()) {
                category.addLanguage(locale, name, description, url);
                category.setEnabled("yes".equals(isEnabled), locale);
                category.save();

            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }
    }
}
