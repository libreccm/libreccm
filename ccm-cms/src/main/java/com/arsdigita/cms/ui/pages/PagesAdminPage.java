/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.ui.pages;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.TabbedPane;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.cms.ui.CMSApplicationPage;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.ui.ReactApp;
import com.arsdigita.ui.admin.categories.CategoriesTreeModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.librecms.CmsConstants;
import org.librecms.pages.PageManager;
import org.librecms.pages.PageRepository;
import org.librecms.pages.Pages;

import java.util.Map;
import java.util.Optional;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PagesAdminPage extends CMSApplicationPage {

    private static final String INDEX_PAGE_MODEL_SELECT = "indexPageModelSelect";
    private static final String ITEM_PAGE_MODEL_SELECT = "itemPageModelSelect";
    private static final String INHERIT_PAGEMODEL = "--inherit--";

    private final ParameterSingleSelectionModel<String> selectedCategory;

    private final Tree categoryTree;
    private final Label nothingSelectedLabel;
    private final Form pageModelForm;
    private final SingleSelect indexPageModelSelect;
    private final SingleSelect itemPageModelSelect;
    private final SaveCancelSection saveCancelSection;

    private Pages pagesInstance;
    private PagesContextBar pagesContextBar;

    public PagesAdminPage() {

        super("", new SimpleContainer());

        super.setAttribute("application", "admin");
        super.setClassAttr("cms-admin");
//        super.setTitle(new Label(new GlobalizedMessage("cms.ui.pages.title",
//                                                       CmsConstants.CMS_BUNDLE)));

        selectedCategory = new ParameterSingleSelectionModel<>(
            new StringParameter("selectedCategory"));
        super.addGlobalStateParam(selectedCategory.getStateParameter());

        categoryTree = new Tree(new CategoryTreeModelBuilder());
        categoryTree.addChangeListener(this::categoryTreeStateChanged);

        final LayoutPanel panel = new LayoutPanel();
        panel.setLeft(categoryTree);

        nothingSelectedLabel = new Label(new GlobalizedMessage(
            "cms.ui.pages.no_category_selected",
            CmsConstants.CMS_BUNDLE));
//        nothingSelectedLabel.addPrintListener(this::printNothingSelectedLabel);
        super.setVisibleDefault(nothingSelectedLabel, true);

        pageModelForm = new Form("pageModelForm");
        super.setVisibleDefault(pageModelForm, false);

        final Label heading = new Label();
        heading.addPrintListener(this::printPageModelFormHeading);
        heading.setClassAttr("heading");
        pageModelForm.add(heading);

        indexPageModelSelect = new SingleSelect(INDEX_PAGE_MODEL_SELECT);
        indexPageModelSelect
            .setLabel(new GlobalizedMessage("cms.ui.pages.index_page_model",
                                            CmsConstants.CMS_BUNDLE));
        try {
            indexPageModelSelect.addPrintListener(this::populatePageModelSelect);
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        pageModelForm.add(indexPageModelSelect);
//        super.setVisibleDefault(indexPageModelSelect, false);

        itemPageModelSelect = new SingleSelect(ITEM_PAGE_MODEL_SELECT);
        itemPageModelSelect
            .setLabel(new GlobalizedMessage("cms.ui.pages.item_page_model",
                                            CmsConstants.CMS_BUNDLE));
        try {
            itemPageModelSelect.addPrintListener(this::populatePageModelSelect);
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        pageModelForm.add(itemPageModelSelect);
//        super.setVisibleDefault(itemPageModelSelect, false);

        saveCancelSection = new SaveCancelSection();
        pageModelForm.add(saveCancelSection);
//        super.setVisibleDefault(saveCancelSection, false);

        pageModelForm.addInitListener(this::initPageModelForm);
        pageModelForm.addValidationListener(this::validatePageModelForm);
        pageModelForm.addProcessListener(this::processPageModelForm);

        final BoxPanel rightPanel = new BoxPanel(BoxPanel.VERTICAL);
        rightPanel.setClassAttr("right-panel");
        rightPanel.add(nothingSelectedLabel);
        rightPanel.add(pageModelForm);
        panel.setRight(rightPanel);

//        final SimpleContainer pageModelsManager = new SimpleContainer(
//            "admin:pageModelsManager", AdminUiConstants.ADMIN_XML_NS);
//        pageModelsManager.add(new Text("Placeholder page models editor"));
        final ReactApp pageModelsManager = new ReactApp(
            "page-models-editor", "scripts/dist/ccm-cms-pagemodelseditor.js");

        pagesContextBar = new PagesContextBar();
        super.add(pagesContextBar);

        final TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.addTab(new Label(new GlobalizedMessage(
            "cms.ui.pages.tab.pages", CmsConstants.CMS_BUNDLE)),
                          panel);
        tabbedPane
            .addTab(new Label(new GlobalizedMessage(
                "cms.ui.pages.tab.page_models", CmsConstants.CMS_BUNDLE)),
                    pageModelsManager);
        super.add(tabbedPane);

        super.addActionListener(this::initPage);

        super.lock();
    }

    public Pages getPagesInstance() {
        return pagesInstance;
    }

    public void setPagesInstance(final Pages pagesInstance) {
        this.pagesInstance = pagesInstance;
        pagesContextBar.setPagesInstance(pagesInstance);
    }

//    @Override
//    public void register(final Page page) {
//        
//        super.register(page);
//        
//        page.setVisibleDefault(nothingSelectedLabel, true);
//        page.setVisibleDefault(pageModelForm, false);
//        
//        page.addGlobalStateParam(selectedCategory.getStateParameter());
//        
//    }
    private void initPage(final ActionEvent event) {

        final PageState state = event.getPageState();

        if (selectedCategory.isSelected(state)) {
            nothingSelectedLabel.setVisible(state, false);
            pageModelForm.setVisible(state, true);
        } else {
            nothingSelectedLabel.setVisible(state, true);
            pageModelForm.setVisible(state, false);
        }

    }

//    private void printNothingSelectedLabel(final PrintEvent event) {
//
//        final PageState state = event.getPageState();
//        final Label target = (Label) event.getTarget();
//
//        target.setVisible(state, !selectedCategory.isSelected(state));
//    }
    private void printPageModelFormHeading(final PrintEvent event) {

        final PageState state = event.getPageState();
        final Label target = (Label) event.getTarget();

//        target.setVisible(state, !selectedCategory.isSelected(state));
        if (selectedCategory.isSelected(state)) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository categoryRepo = cdiUtil
                .findBean(CategoryRepository.class);

            final Category category = categoryRepo
                .findById(Long.parseLong(selectedCategory.getSelectedKey(state)))
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Category with ID %s in the database.",
                        selectedCategory.getSelectedKey(state))));

            target.setLabel(new GlobalizedMessage(
                "cms.ui.pages.page_config_for_category",
                CmsConstants.CMS_BUNDLE,
                new Object[]{category.getName()}));
        }
    }

    private void populatePageModelSelect(final PrintEvent event) {

//        final PageState state = event.getPageState();
        final SingleSelect target = (SingleSelect) event.getTarget();

//        if (!selectedCategory.isSelected(state)) {
//            target.setVisible(state, false);
//            return;
//        }
        target.clearOptions();

        target.addOption(new Option(INHERIT_PAGEMODEL,
                                    new Label(new GlobalizedMessage(
                                        "cms.ui.pages.assigned_page_model.inherit",
                                        CmsConstants.CMS_BUNDLE))));

//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final PageModelRepository pageModelRepo = cdiUtil.findBean(
//            PageModelRepository.class
//        );
//        final List<PageModel> pageModels = pageModelRepo
//            .findDraftByApplication(pagesInstance);
//        final GlobalizationHelper globalizationHelper = cdiUtil
//            .findBean(GlobalizationHelper.class);
//        for (final PageModel pageModel : pageModels) {
//            target.addOption(
//                new Option(
//                    Long.toString(pageModel.getPageModelId()),
//                    new Text(
//                        globalizationHelper.getValueFromLocalizedString(
//                            pageModel.getTitle()
//                        )
//                    )
//                )
//            );
//        }
        final PageModelAdminPageController controller = CdiUtil.createCdiUtil()
            .findBean(PageModelAdminPageController.class);
        final Map<String, Object> options = controller
            .findDraftPageModelsByApplication(pagesInstance);
        for (final Map.Entry<St)
    }

    private void categoryTreeStateChanged(final ChangeEvent event) {

        final PageState state = event.getPageState();

        final String selectedKey = (String) categoryTree.getSelectedKey(state);
        selectedCategory.setSelectedKey(state, selectedKey);
    }

    private void initPageModelForm(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

//        pageModelForm.setVisible(state, selectedCategory.isSelected(state));
        saveCancelSection.setVisible(state, selectedCategory.isSelected(state));

        if (!selectedCategory.isSelected(state)) {
            return;
        }

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryRepository categoryRepo = cdiUtil
            .findBean(CategoryRepository.class);
        final PageRepository pageRepo = cdiUtil
            .findBean(PageRepository.class);

        final Category category = categoryRepo
            .findById(Long.parseLong(selectedCategory.getSelectedKey(state)))
            .orElseThrow(() -> new UnexpectedErrorException(String
            .format("No Category with ID %s in the database.",
                    selectedCategory.getSelectedKey(state))));

        final Optional<org.librecms.pages.Page> page = pageRepo
            .findPageForCategory(category);

        if (page.isPresent()) {

            if (page.get().getIndexPageModel() == null) {
                indexPageModelSelect.setValue(state, INHERIT_PAGEMODEL);
            } else {
                indexPageModelSelect.setValue(state,
                                              Long.toString(page
                                                  .get()
                                                  .getIndexPageModel()
                                                  .getPageModelId()));
            }

            if (page.get().getItemPageModel() == null) {
                itemPageModelSelect.setValue(state, INHERIT_PAGEMODEL);
            } else {
                itemPageModelSelect.setValue(state,
                                             Long.toString(page
                                                 .get()
                                                 .getItemPageModel()
                                                 .getPageModelId()));
            }
        } else {
            indexPageModelSelect.setValue(state, INHERIT_PAGEMODEL);
            itemPageModelSelect.setValue(state, INHERIT_PAGEMODEL);
        }
    }

    private void validatePageModelForm(final FormSectionEvent event)
        throws FormProcessException {

        //Nothing for now
    }

    private void processPageModelForm(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final FormData data = event.getFormData();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository categoryRepo = cdiUtil
                .findBean(CategoryRepository.class);
            final PageManager pageManager = cdiUtil
                .findBean(PageManager.class);
            final PageRepository pageRepo = cdiUtil
                .findBean(PageRepository.class);
            final PageModelRepository pageModelRepo = cdiUtil
                .findBean(PageModelRepository.class);

            final Category category = categoryRepo
                .findById(Long.parseLong(selectedCategory.getSelectedKey(state)))
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Category with ID %s in the database.",
                        selectedCategory.getSelectedKey(state))));

            final org.librecms.pages.Page page = pageRepo
                .findPageForCategory(category)
                .orElse(pageManager.createPageForCategory(category));

            final String selectedIndexPageModelId = data
                .getString(INDEX_PAGE_MODEL_SELECT);
            final String selectedItemPageModelId = data
                .getString(ITEM_PAGE_MODEL_SELECT);

            if (!INHERIT_PAGEMODEL.equals(selectedIndexPageModelId)) {
                final PageModel model = pageModelRepo
                    .findById(Long.parseLong(selectedIndexPageModelId))
                    .orElseThrow(() -> new UnexpectedErrorException(String
                    .format("No PageModel with ID %s in the database.",
                            selectedIndexPageModelId)));
                page.setIndexPageModel(model);
            }

            if (!INHERIT_PAGEMODEL.equals(selectedItemPageModelId)) {
                final PageModel model = pageModelRepo
                    .findById(Long.parseLong(selectedItemPageModelId))
                    .orElseThrow(() -> new UnexpectedErrorException(String
                    .format("No PageModel with ID %s in the database.",
                            selectedItemPageModelId)));
                page.setItemPageModel(model);
            }

            pageRepo.save(page);
        }

        categoryTree.clearSelection(state);
        selectedCategory.clearSelection(state);
    }

    private class CategoryTreeModelBuilder
        extends LockableImpl
        implements TreeModelBuilder {

        @Override
        public TreeModel makeModel(final Tree tree,
                                   final PageState state) {

            return new CategoriesTreeModel(tree,
                                           pagesInstance.getCategoryDomain());
        }

    }

}
