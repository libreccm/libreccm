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
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.AdminPrivileges;

/**
 * A split pane for the Category Administration UI.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
public final class CategoryAdminPane extends BaseAdminPane {

    public static final String CONTEXT_SELECTED = "sel_context";
    private static final String DEFAULT_USE_CONTEXT = "<default>";
    private static final Logger LOGGER = LogManager.getLogger(
        CategoryAdminPane.class);
    private final SingleSelectionModel m_contextModel;
    private final Tree m_categoryTree;
    private final SingleSelectionModel m_model;
    private final CategoryRequestLocal m_parent;
    private final CategoryRequestLocal m_category;

    public CategoryAdminPane() {
        super();

        m_contextModel = new UseContextSelectionModel(new StringParameter(
            CONTEXT_SELECTED));

        // Left column
        // Use context section
        List list = new List(new CategoryUseContextModelBuilder());
        list.setSelectionModel(m_contextModel);
        list.addChangeListener(new ContextSelectionListener());

        /* Category tree section */
        m_categoryTree = new BaseTree(new CategoryTreeModelBuilder(
            m_contextModel));
        m_categoryTree.addChangeListener(new SelectionListener());
        m_model = m_categoryTree.getSelectionModel();

        setSelectionModel(m_model);
        setSelector(m_categoryTree);

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
            Form addContextForm = new AddUseContextForm(m_contextModel);
            getBody().add(addContextForm);
            getBody().connect(addContextAction, addContextForm);
            contextGroup.addAction(new VisibilityComponent(addContextAction,
                                                           AdminPrivileges.ADMINISTER_CATEGORIES));
        }

        final Section categorySection = new Section();
        categorySection.setHeading(new Label(gz("cms.ui.categories")));
        ActionGroup categoryGroup = new ActionGroup();
        categorySection.setBody(categoryGroup);
        categoryGroup.setSubject(m_categoryTree);

        final SimpleContainer leftContainer = new SimpleContainer();
        leftContainer.add(contextSection);
        leftContainer.add(categorySection);
        setLeft(leftContainer);

        m_parent = new ParentRequestLocal();
        m_category = new SelectionRequestLocal();

        setAdd(gz("cms.ui.category.add"),
               new CategoryAddForm(m_category, m_model));

        setEdit(gz("cms.ui.category.edit"),
                new CategoryEditForm(m_parent, m_category));

        setDelete(new DeleteLink(new Label(gz("cms.ui.category.delete"))),
                  new DeleteForm(
                      new SimpleContainer()));

        setIntroPane(new Label(gz("cms.ui.category.intro")));
        setItemPane(new CategoryItemPane(m_model,
                                         m_contextModel,
                                         m_category,
                                         getAddLink(),
                                         getEditLink(),
                                         getDeleteLink()));
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        //page.addActionListener(new RootListener());
    }

    private final class DeleteLink extends ActionLink {

        private final Label m_alternativeLabel;

        DeleteLink(Label label) {
            super(label);
            m_alternativeLabel = new Label(gz("cms.ui.category.undeletable"));
        }

        @Override
        public void generateXML(PageState state, Element parent) {
            if (!isVisible(state)) {
                return;
            }

            //Category cat = m_category.getCategory(state);
            //String context = getUseContext(state);
            //boolean isDefaultContext =
            //        (context == null) || DEFAULT_USE_CONTEXT.equals(context);
            //if (cat.isRoot() || !cat.getChildren().isEmpty()) {
            //    m_alternativeLabel.generateXML(state, parent);
            //} else {
            super.generateXML(state, parent);
            //}
        }

    }

    private final class DeleteForm extends BaseDeleteForm {

        DeleteForm(SimpleContainer prompt) {
            super(prompt);
            prompt.add(new Label(gz("cms.ui.category.delete_prompt")));
            Label catLabel = new Label();
            catLabel.addPrintListener(pe -> {
                Label label = (Label) pe.getTarget();
                Category cat = m_category.getCategory(pe.getPageState());
                java.util.List<Category> descendants = cat.getSubCategories();
                java.util.List<Categorization> catObjects = cat.getObjects();

                StringBuffer sb = new StringBuffer(" ");
                if (descendants.size() > 0) {
                    sb.append("This category has ");
                    sb.append(descendants.size());
                    sb.append(" descendant category(ies). ");
                }
                if (catObjects.size() > 0) {
                    sb.append("It has ").append(catObjects.size());
                    sb.append(" descendant object(s). ");
                }
                if (descendants.size() > 0 || catObjects.size() > 0) {
                    sb.append(
                        "Descendants will be orphaned, if this category is removed.");
                }
                label.setLabel(gz(sb.toString()));
            });
            prompt.add(catLabel);
        }

        @Override
        public final void process(final FormSectionEvent e)
            throws FormProcessException {
            final PageState state = e.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            final CategoryRepository repository = cdiUtil.findBean(
                CategoryRepository.class);
            final Category category = m_category.getCategory(state);
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
            m_model.setSelectedKey(state, parent.getUniqueId());
//            }

            //category.deleteCategoryAndOrphan();
            repository.delete(category);
        }

    }

    private final class SelectionRequestLocal extends CategoryRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String id = m_model.getSelectedKey(state).toString();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryRepository repository = cdiUtil.findBean(
                CategoryRepository.class);
            if (id == null) {
                return null;
            } else {
                return repository.findById(Long.parseLong(id));
            }
        }

    }

    private final class ParentRequestLocal extends CategoryRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            return m_category.getCategory(state).getParentCategory();
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
    private class UseContextSelectionModel extends ParameterSingleSelectionModel {

        public UseContextSelectionModel(ParameterModel m) {
            super(m);
        }

        @Override
        public Object getSelectedKey(final PageState state) {

            Object val = super.getSelectedKey(state);
            if (val == null
                    || ((CharSequence) val).length() == 0) {

                val = DEFAULT_USE_CONTEXT;
                state.setValue(getStateParameter(), val);
                fireStateChanged(state);
            }
            return val;
        }

    }

    public String getUseContext(final PageState state) {

        String selected = (String) m_contextModel.getSelectedKey(state);
        if (DEFAULT_USE_CONTEXT.equals(selected)) {
            return null;
        } else {
            return selected;
        }
//        return (DEFAULT_USE_CONTEXT).equals(selected) ? (String) null : selected;
    }

    private class ContextSelectionListener implements ChangeListener {

        @Override
        public final void stateChanged(final ChangeEvent event) {

            LOGGER.debug("Selection state changed; I may change "
                             + "the body's visible pane");

            final PageState state = event.getPageState();

            getBody().reset(state);

            if (m_contextModel.isSelected(state)) {

                final String rootCategoryId = (String) m_contextModel
                    .getSelectedKey(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final CategoryRepository categoryRepo = cdiUtil
                    .findBean(CategoryRepository.class);
                final Category root;
                if (DEFAULT_USE_CONTEXT.equals(rootCategoryId)) {
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
                } else {
                    root = categoryRepo
                        .findById(Long.parseLong(rootCategoryId))
                        .orElseThrow(() -> new UnexpectedErrorException(String
                        .format("No Category with ID %s in the database.",
                                rootCategoryId)));
                }
                if (root != null) {
                    m_model.setSelectedKey(state, root.getUniqueId());
                    m_categoryTree.reset(state);
                }

            }
            if (m_model.isSelected(state)) {
                LOGGER.debug("The selection model is selected; displaying "
                                 + "the item pane");

                getBody().push(state, getItemPane());
            }
        }

    }

}
