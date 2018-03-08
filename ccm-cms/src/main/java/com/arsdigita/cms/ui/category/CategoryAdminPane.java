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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.BaseTree;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * A split pane for the Category Administration UI.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class CategoryAdminPane extends BaseAdminPane<String> {

    private static final Logger LOGGER = LogManager
        .getLogger(CategoryAdminPane.class);

    public static final String CONTEXT_SELECTED = "sel_context";
//    private static final String DEFAULT_USE_CONTEXT = "<default>";

    private final Label noCategorySystemsLabel;

    private final SingleSelectionModel<String> selectedCategorySystem;
    private final SingleSelectionModel<String> selectedCategory;

    private final Tree categoryTree;

    private final CategoryRequestLocal parentCategoryRequestLocal;
    private final CategoryRequestLocal categoryRequestLocal;

    public CategoryAdminPane() {
        super();

        selectedCategorySystem = new UseContextSelectionModel(
            new StringParameter(
                CONTEXT_SELECTED));

        // Left column
        // Use context section
        List list = new List(new CategoryUseContextModelBuilder());
        list.setSelectionModel(selectedCategorySystem);
        list.addChangeListener(new ContextSelectionListener());

        /* Category tree section */
        categoryTree = new BaseTree(new CategoryTreeModelBuilder(
            selectedCategorySystem));
        categoryTree.addChangeListener(new SelectionListener());
        selectedCategory = categoryTree.getSelectionModel();

        super.setSelectionModel(selectedCategory);
        setSelector(categoryTree);

        /* setup use context form */
        final Section contextSection = new Section();
        contextSection.setHeading(new Label(gz("cms.ui.category.use_contexts")));
        ActionGroup contextGroup = new ActionGroup();
        contextSection.setBody(contextGroup);
        contextGroup.setSubject(list);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        if (permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES)) {
            ActionLink addContextAction = new ActionLink(new Label(gz(
                "cms.ui.category.add_use_context")));
            Form addContextForm = new AddUseContextForm(selectedCategorySystem);
            getBody().add(addContextForm);
            getBody().connect(addContextAction, addContextForm);
            contextGroup
                .addAction(new VisibilityComponent(addContextAction,
                                                   AdminPrivileges.ADMINISTER_CATEGORIES));
        }

        final Section categorySection = new Section();
        categorySection.setHeading(new Label(gz("cms.ui.categories")));
        ActionGroup categoryGroup = new ActionGroup();
        categorySection.setBody(categoryGroup);
        categoryGroup.setSubject(categoryTree);

        final SimpleContainer leftContainer = new SimpleContainer();
        leftContainer.add(contextSection);
        leftContainer.add(categorySection);
        setLeft(leftContainer);

        parentCategoryRequestLocal = new ParentRequestLocal();
        categoryRequestLocal = new SelectionRequestLocal();

        setAdd(gz("cms.ui.category.add"),
               new CategoryAddForm(categoryRequestLocal, selectedCategory));

        setEdit(gz("cms.ui.category.edit"),
                new CategoryEditForm(parentCategoryRequestLocal,
                                     categoryRequestLocal));

        setDelete(new DeleteLink(new Label(gz("cms.ui.category.delete"))),
                  new DeleteForm(
                      new SimpleContainer()));

        setIntroPane(new Label(gz("cms.ui.category.intro")));
        setItemPane(new CategoryItemPane(selectedCategory,
                                         selectedCategorySystem,
                                         categoryRequestLocal,
                                         getAddLink(),
                                         getEditLink(),
                                         getDeleteLink()));

        noCategorySystemsLabel = new Label(new GlobalizedMessage(
            "cms.ui.category.no_category_systems_mapped",
            CmsConstants.CMS_BUNDLE));
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        //page.addActionListener(new RootListener());
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {

        if (isVisible(state)) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentSection section = CMS.getContext().getContentSection();
            final CategoryAdminController controller = cdiUtil
                .findBean(CategoryAdminController.class);
            final java.util.List<DomainOwnership> ownerships
                                                      = controller
                    .retrieveDomains(section);
            if (ownerships == null || ownerships.isEmpty()) {
                final Element panelElem = parent
                    .newChildElement("bebop:layoutPanel",
                                     BEBOP_XML_NS);
                final Element bodyElem = panelElem.newChildElement("bebop:body",
                                                                   BEBOP_XML_NS);
                noCategorySystemsLabel.generateXML(state, bodyElem);
            } else {
                noCategorySystemsLabel.setVisible(state, false);
                super.generateXML(state, parent);
            }
        }
    }

//    @Override
//    public boolean isVisible(final PageState state) {
//
//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final ContentSection section = CMS.getContext().getContentSection();
//        final CategoryAdminController controller = cdiUtil
//            .findBean(CategoryAdminController.class);
//        final java.util.List<DomainOwnership> ownerships
//                                                  = controller
//                .retrieveDomains(section);
//
//        if (ownerships == null || ownerships.isEmpty()) {
//            return false;
//        } else {
//            return super.isVisible(state);
//        }
//    }
    private final class DeleteLink extends ActionLink {

        private final Label m_alternativeLabel;

        DeleteLink(Label label) {
            super(label);
            m_alternativeLabel = new Label(gz("cms.ui.category.undeletable"));
        }

        @Override
        public void generateXML(final PageState state, final Element parent) {

            if (isVisible(state)) {
                super.generateXML(state, parent);
            }

            //Category cat = m_category.getCategory(state);
            //String context = getUseContext(state);
            //boolean isDefaultContext =
            //        (context == null) || DEFAULT_USE_CONTEXT.equals(context);
            //if (cat.isRoot() || !cat.getChildren().isEmpty()) {
            //    m_alternativeLabel.generateXML(state, parent);
            //} else {
//            
            //}
        }

    }

    private final class DeleteForm extends BaseDeleteForm {

        DeleteForm(final SimpleContainer prompt) {
            super(prompt);
            prompt.add(new Label(gz("cms.ui.category.delete_prompt")));
            Label catLabel = new Label();
            catLabel.addPrintListener(pe -> {
                Label label = (Label) pe.getTarget();
                Category cat = categoryRequestLocal.getCategory(pe
                    .getPageState());
                java.util.List<Category> descendants = cat.getSubCategories();
                java.util.List<Categorization> catObjects = cat.getObjects();

                final StringBuilder builder = new StringBuilder(" ");
                if (descendants.size() > 0) {
                    builder.append("This category has ");
                    builder.append(descendants.size());
                    builder.append(" descendant category(ies). ");
                }
                if (catObjects.size() > 0) {
                    builder.append("It has ").append(catObjects.size());
                    builder.append(" descendant object(s). ");
                }
                if (descendants.size() > 0 || catObjects.size() > 0) {
                    builder.append(
                        "Descendants will be orphaned, if this category is removed.");
                }
                label.setLabel(gz(builder.toString()));
            });
            prompt.add(catLabel);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            final CategoryRepository repository = cdiUtil.findBean(
                CategoryRepository.class);
            final Category category = categoryRequestLocal.getCategory(state);
            if (category == null) {
                return;
            }

//            PermissionService.assertPermission(new PermissionDescriptor(PrivilegeDescriptor.DELETE,
//                                                                        category,
//                                                                        Kernel.getContext().
//                    getParty()));
            permissionChecker.checkPermission(
                AdminPrivileges.ADMINISTER_CATEGORIES, category);

//            if (category.isRoot()) {
//                Category root =
//                         Category.getRootForObject(CMS.getContext().getContentSection(),
//                                                   getUseContext(state));
//                if (category.equals(root)) {
//                    Category.clearRootForObject(CMS.getContext().getContentSection(),
//                                                getUseContext(state));
//                }
//                m_contextModel.setSelectedKey(state, DEFAULT_USE_CONTEXT);
//            } else {
            Category parent = category.getParentCategory();
            selectedCategory.setSelectedKey(state, parent.getUniqueId());
//            }

            //category.deleteCategoryAndOrphan();
            repository.delete(category);
        }

    }

    private final class SelectionRequestLocal extends CategoryRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {

            final String selectedCatetoryIdStr = selectedCategory
                .getSelectedKey(state);
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository repository = cdiUtil.findBean(
                CategoryRepository.class);
            final Category category;
            if (selectedCatetoryIdStr == null) {
                category = null;
            } else {
                category = repository
                    .findById(Long.parseLong(selectedCatetoryIdStr))
                    .orElseThrow(() -> new UnexpectedErrorException(String
                    .format("No Category with ID %s in the database.",
                            selectedCatetoryIdStr)));
            }
            return category;
        }

    }

    private final class ParentRequestLocal extends CategoryRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            return categoryRequestLocal.getCategory(state).getParentCategory();
        }

    }

//    private final class RootListener implements ActionListener {
//
//        public final void actionPerformed(final ActionEvent e) {
//            final PageState state = e.getPageState();
//
//            if (!m_model.isSelected(state)) {
//                final Category root =
//                               Category.getRootForObject(CMS.getContext().getContentSection(),
//                                                         getUseContext(state));
//                if (root != null) {
//                    m_model.setSelectedKey(state, root.getID());
//                }
//            }
//        }
//
//    }
    private class UseContextSelectionModel
        extends ParameterSingleSelectionModel<String> {

        public UseContextSelectionModel(final ParameterModel parameterModel) {
            super(parameterModel);
        }

        @Override
        public String getSelectedKey(final PageState state) {

            String val = super.getSelectedKey(state);
            if (val == null
                    || val.isEmpty()
                    || val.matches("\\s*")) {

                final ContentSection section = CMS
                    .getContext()
                    .getContentSection();
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryAdminController controller = cdiUtil
                    .findBean(CategoryAdminController.class);
                final java.util.List<DomainOwnership> domainOwnerships
                                                          = controller
                        .retrieveDomains(section);
                if (domainOwnerships == null || domainOwnerships.isEmpty()) {
                    val = null;
                } else {
                    final Domain categorySystem = controller
                        .retrieveDomains(section).get(0).getDomain();
                    val = Long.toString(categorySystem.getObjectId());

                    state.setValue(getStateParameter(), val);
                    fireStateChanged(state);
                }
            }
            return val;
        }

    }

    public String getUseContext(final PageState state) {

        final String selected = selectedCategorySystem.getSelectedKey(state);
        return selected;
//        if (DEFAULT_USE_CONTEXT.equals(selected)) {
//            
//            
//            
//            return null;
//        } else {
//            return selected;
//        }
    }

    private class ContextSelectionListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {

            LOGGER.debug("Selection state changed; I may change "
                             + "the body's visible pane");

            final PageState state = event.getPageState();

            getBody().reset(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final Category root;
            if (selectedCategorySystem.isSelected(state)) {
                final String categorySystemIdStr = selectedCategorySystem
                    .getSelectedKey(state);

                final DomainRepository domainRepo = cdiUtil
                    .findBean(DomainRepository.class);
                final Domain categorySystem = domainRepo
                    .findById(Long.parseLong(categorySystemIdStr))
                    .orElseThrow(() -> new UnexpectedErrorException(String
                    .format("No Domain with ID %s in the database.",
                            categorySystemIdStr)));
                root = categorySystem.getRoot();

//                final String rootCategoryId = selectedCategorySystem
//                    .getSelectedKey(state);
//
//                
//                final Category root;
//                if (DEFAULT_USE_CONTEXT.equals(rootCategoryId)) {
//                    final ContentSection section = CMS
//                        .getContext()
//                        .getContentSection();
//
//                    final CategoryAdminController controller = CdiUtil
//                        .createCdiUtil()
//                        .findBean(CategoryAdminController.class);
//                    final java.util.List<DomainOwnership> ownerships
//                                                              = controller
//                            .retrieveDomains(section);
//                    root = ownerships.get(0).getDomain().getRoot();
//                } else {
//                    root = categoryRepo
//                        .findById(Long.parseLong(rootCategoryId))
//                        .orElseThrow(() -> new UnexpectedErrorException(String
//                        .format("No Category with ID %s in the database.",
//                                rootCategoryId)));
//                }
            } else {
                final ContentSection section = CMS
                    .getContext()
                    .getContentSection();

                final CategoryAdminController controller = CdiUtil
                    .createCdiUtil()
                    .findBean(CategoryAdminController.class);
                final java.util.List<DomainOwnership> ownerships
                                                          = controller
                        .retrieveDomains(section);
                root = ownerships.get(0).getDomain().getRoot();
            }

            if (root != null) {
                selectedCategory.setSelectedKey(state, root.getUniqueId());
                categoryTree.reset(state);
            }
            if (selectedCategory.isSelected(state)) {
                LOGGER.debug("The selection model is selected; displaying "
                                 + "the item pane");

                getBody().push(state, getItemPane());
            }
        }

    }

}
