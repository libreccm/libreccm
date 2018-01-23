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
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;


/**
 * TODO Needs a description.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class CategoryAddForm extends BaseCategoryForm {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryAddForm.class);

    private final SingleSelectionModel<String> categorySelectionModel;

    /**
     * Constructor.
     */
    public CategoryAddForm(final CategoryRequestLocal parent,
                           final SingleSelectionModel<String> model) {

        super("AddSubcategories", gz("cms.ui.category.add"), parent);

        categorySelectionModel = model;

        addProcessListener(new ProcessListener());
    }

    private final class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            LOGGER.debug("Adding a category");

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil
                .findBean(PermissionChecker.class);
            final CategoryController controller = cdiUtil
                .findBean(CategoryController.class);

            final PageState state = event.getPageState();

            final Category parent = getCategoryRequestLocal()
                .getCategory(state);
            final String name = (String) getNameField().getValue(state);
            final String description = (String) getDescriptionArea()
                .getValue(state);
            // this seems anti-intuitive but the question is "can you place
            // items in this category.  If the user says "yes" then the
            // category is not abstract
            final boolean isAbstract = !"yes"
                .equals(getIsAbstractRadioGroup().getValue(state));

            Assert.exists(parent, "Category parent");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Using parent category " + parent + " to "
                                 + "create new category");
            }

            if (permissionChecker
                .isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES, parent)) {

                final Category category = controller.createCategory(parent,
                                                                    name,
                                                                    description,
                                                                    isAbstract);

                categorySelectionModel.setSelectedKey(state,
                                                      category.getUniqueId());
            } else {
                // XXX user a better exception here.
                // PermissionException doesn't work for this case.
                throw new AccessDeniedException();
            }
        }

    }

}
