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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.dispatcher.AccessDeniedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Locale;

/**
 * Generates a form for editing an existing localisation for the given category.
 *
 * This class is part of the admin GUI of CCM and extends the standard form
 * in order to present forms for managing the multi-language categories.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public class CategoryLocalizationEditForm extends CategoryLocalizationForm {

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryLocalizationAddForm.class);

    private final SingleSelectionModel m_catLocale;
    /**
     * Creates a new instance of CategoryLocalizationEditForm
     */
    public CategoryLocalizationEditForm(final CategoryRequestLocal category, SingleSelectionModel catLocale) {

        super("EditCategoryLocalization", gz(
                   "cms.ui.category.localization_edit"), category);

        m_catLocale = catLocale;

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());

    }

    /**
     * please add: purpose of this class
     */
    private class InitListener implements FormInitListener {
        public final void init(final FormSectionEvent e)
        throws FormProcessException {

            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            final String categoryLocalizationLocale = (String) m_catLocale.getSelectedKey(state);
            final Locale locale = new Locale(categoryLocalizationLocale);

            // Hide Locale-Widget and lock it (read-only)
            m_locale.addOption(new Option(categoryLocalizationLocale,
                new Text(locale.getDisplayLanguage())), state);
            m_locale.setValue(state, categoryLocalizationLocale);
//            m_locale.setVisible(state, false);
            m_locale.lock();

            m_title.setValue(state, category.getTitle().getValue(locale));
            m_description.setValue(state, category.getDescription().getValue(locale));
//            m_url.setValue(state, category.getName());

//            if (category.isEnabled()) {
//                m_isEnabled.setValue(state, "yes");
//            } else {
//                m_isEnabled.setValue(state, "no");
//            }
        }
    }

    /**
     * ##todo: document purpose of this
     */
    private class ProcessListener implements FormProcessListener {
        public final void process(final FormSectionEvent e)
        throws FormProcessException {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
            final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);

            final PageState state = e.getPageState();
            final Category category = m_category.getCategory(state);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Editing localization for locale " + m_locale +
                            " for category " + category);
            }

            if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, category)) {
                final Locale locale = new Locale((String) m_locale.getValue(state));
                category.getTitle().addValue(locale, (String) m_title.getValue(state));
                category.getDescription().addValue(locale, (String) m_description.getValue(state));
//                category.setName((String) m_url.getValue(state));
//                category.setEnabled("yes".equals(m_isEnabled.getValue(state)));
                categoryRepository.save(category);
            } else {
                throw new AccessDeniedException();
            }
        }
    }
}
