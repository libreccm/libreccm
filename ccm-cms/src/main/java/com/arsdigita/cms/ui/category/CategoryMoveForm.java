/*
 * Copyright (C) 2013 Jens Pelzetter All Rights Reserved.
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
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ui.BaseTree;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.util.Assert;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 */
public class CategoryMoveForm extends CMSForm {

    public static final String CONTEXT_SELECTED = "sel_context";
    //private static final String DEFAULT_USE_CONTEXT =
    //                            CategoryUseContextModelBuilder.DEFAULT_USE_CONTEXT;
    private final CategoryRequestLocal selectedCategory;
    private final SaveCancelSection saveCancelSection;
    private final ChangeListener changeListener;
    private final SingleSelectionModel selectionModel;
    private final Tree categoryTree;

    public CategoryMoveForm(final CategoryRequestLocal selectedCategory,
                            final SingleSelectionModel contextModel) {

        super("MoveCategory");
        setMethod(Form.POST);
        this.selectedCategory = selectedCategory;

        final Label header = new Label(GlobalizationUtil.globalize("cms.ui.category.move"));
        //final Label header = new Label();
        header.addPrintListener(new PrintListener() {
            @Override
            public void prepare(final PrintEvent event) {
                final String[] args = new String[1];
                args[0] = selectedCategory.getCategory(event.getPageState()).getName();

                final Label target = (Label) event.getTarget();
                target.setLabel(GlobalizationUtil.globalize("cms.ui.move.category", args));
            }

        });

        header.setFontWeight(Label.BOLD);
        add(header, ColumnPanel.FULL_WIDTH);

        changeListener = new TreeChangeListener();
        selectionModel = new ParameterSingleSelectionModel(new StringParameter("selectedCategory"));
        categoryTree = new BaseTree(new CategoryTreeModelBuilder(contextModel));
        categoryTree.addChangeListener(changeListener);

        add(categoryTree);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
        addSubmissionListener(new FormSecurityListener(AdminPrivileges.ADMINISTER_CATEGORIES));

    }

    protected Submit getCancelButton() {
        return saveCancelSection.getCancelButton();
    }

    protected Category getCategory(final PageState state) {
        final Category category = selectedCategory.getCategory(state);
        Assert.exists(category);
        return category;
    }

    private class TreeChangeListener implements ChangeListener {

        public TreeChangeListener() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            //Nothing for now
        }

    }

    private class InitListener implements FormInitListener {

        public InitListener() {
            //Nothing
        }

        @Override
        public void init(final FormSectionEvent event) throws FormProcessException {
            //Nothing
        }

    }

    private class ProcessListener implements FormProcessListener {

        public ProcessListener() {
            //Nothing
        }

        @Override
        public void process(final FormSectionEvent event) throws FormProcessException {
            final PageState state = event.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository categoryRepository = cdiUtil.findBean(CategoryRepository.class);
            final CategoryManager categoryManager = cdiUtil.findBean(CategoryManager.class);

            if (saveCancelSection.getSaveButton().isSelected(state)
                && !(categoryTree.getSelectedKey(state).equals(selectedCategory.getCategory(state).getUniqueId()))) {

                final Category categoryToMove = selectedCategory.getCategory(state);
                final String targetKey = (String) categoryTree.getSelectedKey(state);

                final Optional<Category> categoryOptional = categoryRepository.findById(Long.parseLong(targetKey));
                if (categoryOptional.isPresent()) {
                    final Category target = categoryOptional.get();

                    final Category parent = categoryToMove.getParentCategory();

                    categoryManager.removeSubCategoryFromCategory(categoryToMove, parent);
                    categoryRepository.save(parent);

                    categoryManager.addSubCategoryToCategory(categoryToMove, target);
                    categoryToMove.setParentCategory(target);

                    categoryRepository.save(target);
                    categoryRepository.save(categoryToMove);
                } else {
                    throw new FormProcessException(GlobalizationUtil.globalize("Category with id" + targetKey + " does not exist!"));
                }
            }

            categoryTree.clearSelection(state);
            categoryTree.clearExpansionState(state);
        }

    }
}
