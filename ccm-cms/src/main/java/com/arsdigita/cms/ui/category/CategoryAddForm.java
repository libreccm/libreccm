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
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * TODO Needs a description.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: CategoryAddForm.java 2090 2010-04-17 08:04:14Z pboy $
 */
final class CategoryAddForm extends BaseCategoryForm {

    private static final Logger s_log = Logger.getLogger
        (CategoryAddForm.class);

    private final SingleSelectionModel m_model;

    /**
     * Constructor.
     */
    public CategoryAddForm(final CategoryRequestLocal parent,
                           final SingleSelectionModel model) {
        super("AddSubcategories", gz("cms.ui.category.add"), parent);

        m_model = model;

        addProcessListener(new ProcessListener());
    }

    private final class ProcessListener implements FormProcessListener {

        public final void process(final FormSectionEvent e)
                     throws FormProcessException {
            s_log.debug("Adding a category");

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);
            final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);
            final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
            final ConfigurationManager manager = cdiUtil.findBean(ConfigurationManager.class);
            final KernelConfig config = manager.findConfiguration(KernelConfig.class);

            final PageState state = e.getPageState();

            final Category parent = m_parent.getCategory(state);
            final String name = (String) m_name.getValue(state);
            final String description = (String) m_description.getValue(state);
            final String isAbstract = (String) m_isAbstract.getValue(state);

            Assert.exists(parent, "Category parent");

            if (s_log.isDebugEnabled()) {
                s_log.debug("Using parent category " + parent + " to " +
                            "create new category");
            }

            if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, parent)) {
                final Category category = new Category();
                category.setName(name);
                final LocalizedString localizedDescription = new LocalizedString();
                localizedDescription.addValue(config.getDefaultLocale(), description);
                category.setDescription(localizedDescription);
                // this seems anti-intuitive but the question is "can you place
                // items in this category.  If the user says "yes" then the
                // category is not abstract
                if ("yes".equals(isAbstract)) {
                    category.setAbstractCategory(false);
                } else if ("no".equals(isAbstract)) {
                    category.setAbstractCategory(true);
                }

                categoryRepository.save(category);

                categoryManager.addSubCategoryToCategory(category, parent);

                m_model.setSelectedKey(state, category.getUniqueId());
            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }
    }
}
