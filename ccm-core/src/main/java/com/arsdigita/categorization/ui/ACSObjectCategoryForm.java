/*
 * Copyright (C) 2004 Chris Gilbert
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
package com.arsdigita.categorization.ui;

import java.util.ArrayList;
import java.util.List;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract form for assigning categories to acs_objects. The assigned
 * categories are those specified by the category widget, which is retrieved by
 * the concrete subclass' implementation of getCategoryWidget.
 *
 * The category widget may be an implementation of CategoryWidget, which
 * generates a javascript tree of categories. Implementations need only specify
 * an XML prefix and namespace.
 *
 * The object that is to be assigned to the categories is specified by the
 * concrete subclass' implentation of getObject
 *
 * @author chris.gilbert@westsussex.gov.uk
 *
 *
 */
// this class has been abstracted out from the original cms specific 
// category form in ccm-cms
public abstract class ACSObjectCategoryForm extends Form {

    private final Widget categoryWidget;
    private final SaveCancelSection saveCancelSection;

    protected abstract CcmObject getObject(PageState state);

    public ACSObjectCategoryForm(final LongParameter root,
                                 final StringParameter mode,
                                 final Widget categoryWidget) {
        super("category", new BoxPanel(BoxPanel.VERTICAL));

        this.categoryWidget = categoryWidget;
        categoryWidget.addValidationListener(new NotNullValidationListener());
        saveCancelSection = new SaveCancelSection();

        super.add(categoryWidget);
        super.add(saveCancelSection);

        super.addInitListener(new FormInitListener() {

            @Override
            public void init(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final CcmObject object = getObject(state);

                final List<Long> selectedCats = new ArrayList<>();
                final Set<Long> ancestorCats = new HashSet<>();
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ACSObjectCategoryController controller = cdiUtil
                    .findBean(ACSObjectCategoryController.class);
                final List<Category> categories = controller
                    .getCategoriesForObject(object);
                for (final Category category : categories) {
                    selectedCats.add(category.getObjectId());
                    addAncestorCats(ancestorCats, category);
                }

                final Long[][] paramArray = new Long[2][];
                paramArray[0] = selectedCats
                    .toArray(new Long[selectedCats.size()]);
                paramArray[1] = ancestorCats
                    .toArray(new Long[ancestorCats.size()]);

                categoryWidget.setValue(state, paramArray);
            }

        });

        super.addProcessListener(new FormProcessListener() {

            @Override
            public void process(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                final CcmObject object = getObject(state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ACSObjectCategoryController controller = cdiUtil
                    .findBean(ACSObjectCategoryController.class);

                final Set<Long> curSelectedCat = controller
                    .getCategoriesForObject(object)
                    .stream()
                    .map(category -> category.getObjectId())
                    .collect(Collectors.toSet());

                final CategoryRepository categoryRepo = cdiUtil
                    .findBean(CategoryRepository.class);
                final CategoryManager categoryManager = cdiUtil
                    .findBean(CategoryManager.class);
                final List<Long> ids = new ArrayList<>();
                for (final BigDecimal value : (BigDecimal[]) categoryWidget
                    .getValue(state)) {

                    ids.add(value.longValue());

                }
                for (final Long id : ids) {
                    final Category cat = categoryRepo
                        .findById(id)
                        .orElseThrow(() -> new IllegalArgumentException(
                        String.format("No Category with ID %d in the database. "
                                          + "Where did that ID come from?",
                                      id)));
                    if (!curSelectedCat.contains(id)) {
                        controller.addObjectToCategory(object, cat);
                    } else {
                        try {
                            controller.removeObjectFromCategory(object, cat);
                        } catch (ObjectNotAssignedToCategoryException ex) {
                            throw new UnexpectedErrorException(ex);
                        }
                    }
                }

                fireCompletionEvent(state);
            }

        });
        super.addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                if (saveCancelSection.getCancelButton().isSelected(state)) {
                    fireCompletionEvent(state);
                    throw new FormProcessException("Submission cancelled",
                                                   GlobalizationUtil.globalize(
                                                       "categorization.cancel.msg"));
                }
            }

        });
    }

    private void addAncestorCats(final Set<Long> ancestorCats,
                                 final Category category) {

        if (category.getParentCategory() != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ACSObjectCategoryController controller = cdiUtil
                .findBean(ACSObjectCategoryController.class);
            ancestorCats
                .add(controller.getParentCategoryId(category));
        }

    }

}
