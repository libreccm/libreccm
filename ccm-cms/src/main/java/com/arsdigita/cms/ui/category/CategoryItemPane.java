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
import com.arsdigita.bebop.BaseLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.cms.ui.templates.CategoryTemplates;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import com.arsdigita.xml.Element;

import java.net.URLEncoder;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;

/**
 * Edits a single category.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
class CategoryItemPane extends BaseItemPane {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryItemPane.class);
    private final SingleSelectionModel m_model;
    private final CategoryRequestLocal m_category;
    private final SimpleContainer m_detailPane;

    public CategoryItemPane(final SingleSelectionModel model,
                            final SingleSelectionModel contextModel,
                            final CategoryRequestLocal category,
                            final ActionLink addLink,
                            final ActionLink editLink,
                            final ActionLink deleteLink) {
        m_model = model;
        m_category = category;

        // Details
        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        final ActionLink orderItemsLink = new ActionLink(new Label(
            gz("cms.ui.category.categorized_objects"))) {

            @Override
            public boolean isVisible(PageState state) {
                // update for live items only
                if (!super.isVisible(state)) {
                    return false;
                }
                return !m_category.getCategory(state).getObjects().isEmpty();
            }

        };

        final Form orderItemsForm = new OrderItemsForm(m_category);
        final Form orderItemsForm2 = new OrderItemsForm(m_category);
        add(orderItemsForm);
        add(orderItemsForm2);

        // Change index item
        final ActionLink indexLink = new ActionLink(new Label(gz(
            "cms.ui.category.change_index_item")));
        final Form indexForm = new IndexItemSelectionForm(m_category);
        add(indexForm);

        //Move link
        final ActionLink moveLink = new MoveLink(new Label(gz(
            "cms.ui.category.move")));
        final Form moveForm = new CategoryMoveForm(m_category, contextModel);
        add(moveForm);

        ViewItemLink viewIndexLink = new ViewItemLink(new Label(gz(
            "cms.ui.category.view_index_item")), "");
        EditItemLink editIndexLink = new EditItemLink(new Label(gz(
            "cms.ui.category.edit_index_item")), "");

        // Summary
        m_detailPane.add(new SummarySection(editLink,
                                            deleteLink,
                                            indexLink,
                                            moveLink,
                                            viewIndexLink,
                                            editIndexLink,
                                            orderItemsLink));

        // Quasimodo: BEGIN
        // Localizations
        ActionLink addCategoryLocalizationLink = new ActionLink(new Label(gz(
            "cms.ui.category.localization_add"))) {

            @Override
            public boolean isVisible(PageState state) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ConfigurationManager manager = cdiUtil.findBean(
                    ConfigurationManager.class);
                final KernelConfig config = manager.findConfiguration(
                    KernelConfig.class);
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);
                // Only show addLanguage button, if there are langauges to add
                int countSupportedLanguages = config.getSupportedLanguages()
                    .size();
                final Category category = m_category.getCategory(state);
                if (permissionChecker.isPermitted(
                    AdminPrivileges.ADMINISTER_CATEGORIES,
                    category)) {
                    if (category.getTitle().getAvailableLocales().size()
                            < countSupportedLanguages
                            || category.getDescription().getAvailableLocales()
                            .size() < countSupportedLanguages) {
                        return true;
                    }
                }
                return false;
            }

        };

        CategoryLocalizationAddForm addCategoryLocalizationForm
                                        = new CategoryLocalizationAddForm(
                m_category);
        m_detailPane.add(new CategoryLocalizationSection(
            addCategoryLocalizationLink));
        add(addCategoryLocalizationForm);
        connect(addCategoryLocalizationLink, addCategoryLocalizationForm);
        connect(addCategoryLocalizationForm);
        // Quasimodo: END

        // Subcategories
        m_detailPane.add(new SubcategorySection(addLink));

        // Linked categories
        final ActionLink linkAddLink = new ActionLink(new Label(gz(
            "cms.ui.category.linked_add")));

        final Form linkForm = new LinkForm(m_category);
        add(linkForm);

        linkAddLink.addActionListener(new NavigationListener(linkForm));
        linkForm.addSubmissionListener(new CancelListener(linkForm));

        m_detailPane.add(new LinkedCategorySection(linkAddLink));

        // Templates
//        m_detailPane.add(new AdminVisible(new CategoryTemplateSection()));
        // Permissions
        //m_detailPane.add(new PermissionsSection());
        //m_detailPane.add(new Text("PermissionSection Placeholder"));
        connect(indexLink, indexForm);
        connect(indexForm);

        connect(moveLink, moveForm);
        connect(moveForm);

        connect(orderItemsLink, orderItemsForm);
        connect(orderItemsForm);

    }

    private class EditVisible extends VisibilityComponent {

        EditVisible(final Component child) {
            super(child, null);
        }

        @Override
        public boolean hasPermission(PageState ps) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            return permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CATEGORIES);
        }

    }

    private class AdminVisible extends VisibilityComponent {

        AdminVisible(final Component child) {
            super(child, null);
        }

        @Override
        public boolean hasPermission(PageState ps) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            return permissionChecker.isPermitted(
                AdminPrivileges.ADMINISTER_CATEGORIES);
        }

    }

    private class SummarySection extends Section {

        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink,
                       final ActionLink indexLink,
                       final ActionLink moveLink,
                       final ActionLink orderItemsLink) {
            setHeading(new Label(gz("cms.ui.category.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());

            group.addAction(new EditVisible(editLink), ActionGroup.EDIT);
            group.addAction(new EditVisible(orderItemsLink));
            group.addAction(new EditVisible(moveLink));
            group.addAction(new EditVisible(indexLink));
            group.addAction(new AdminVisible(deleteLink), ActionGroup.DELETE);
        }

        /*
         * This alternative constructor sets two additional links, allowing
         * the user to view and edit the content index item.
         */
        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink,
                       final ActionLink indexLink,
                       final ActionLink moveLink,
                       final BaseLink viewIndexItem,
                       final BaseLink editIndexItem,
                       final ActionLink orderItemsLink) {
            setHeading(new Label(gz("cms.ui.category.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());

            group.addAction(new EditVisible(editLink), ActionGroup.EDIT);
            group.addAction(new EditVisible(orderItemsLink));
            group.addAction(new EditVisible(indexLink));
            group.addAction(new EditVisible(moveLink));
            group.addAction(new EditVisible(viewIndexItem));
            group.addAction(new EditVisible(editIndexItem));
            group.addAction(new AdminVisible(deleteLink), ActionGroup.DELETE);
        }

        private class Properties extends PropertyList {

            @Override
            protected final java.util.List properties(final PageState state) {
                final java.util.List props = super.properties(state);
                final Category category = m_category.getCategory(state);

                String itemTitle = "None";

                if (category != null) {
                    itemTitle = category.getDisplayName();
                }

                props.add(new Property(gz("cms.ui.name"),
                                       category.getName()));
                props.add(new Property(gz("cms.ui.description"),
                                       category.getDescription().getValue()));
                props.add(new Property(gz("cms.ui.category.is_not_abstract"),
                                       category.isAbstractCategory()
                                           ? gz("cms.ui.no")
                                           : gz("cms.ui.yes")));
                props.add(new Property(gz("cms.ui.cateogry.is_visible"),
                                       category.isVisible()
                                           ? gz("cms.ui.yes")
                                           : gz("cms.ui.no")));
                props.add(new Property(gz("cms.ui.category.is_enabled"),
                                       category.isEnabled()
                                           ? gz("cms.ui.yes")
                                           : gz("cms.ui.no")));
                props.add(new Property(gz("cms.ui.category.index_item"),
                                       itemTitle));

                return props;
            }

        }

    }

    // Quasimodo: BEGIN
    // CategoryLocalizationSection
    private class CategoryLocalizationSection extends Section {

        private CategoryLocalizationTable m_catLocalizationTable;
        private CategoryLocalizationEditForm m_editCategoryLocalizationForm;
        private StringParameter m_catLocaleParam;
        private ParameterSingleSelectionModel m_catLocale;

        CategoryLocalizationSection(ActionLink addLink) {
            setHeading(new Label(gz("cms.ui.category.localizations")));
            m_catLocaleParam = new StringParameter("catLocale");
            m_catLocale = new ParameterSingleSelectionModel(m_catLocaleParam);

            final ActionGroup group = new ActionGroup();
            setBody(group);
            m_catLocalizationTable = new CategoryLocalizationTable(m_category,
                                                                   m_model,
                                                                   m_catLocale);
            group.setSubject(m_catLocalizationTable);
            group.addAction(new AdminVisible(addLink), ActionGroup.ADD);

            m_editCategoryLocalizationForm = new CategoryLocalizationEditForm(
                m_category,
                m_catLocale);
            add(m_editCategoryLocalizationForm);
            connect(m_editCategoryLocalizationForm);
            connect(m_catLocalizationTable, 0, m_editCategoryLocalizationForm);
        }

        @Override
        public void register(Page page) {
            super.register(page);
            page.addComponentStateParam(m_editCategoryLocalizationForm,
                                        m_catLocaleParam);
        }

    }

    private class SubcategorySection extends Section {

        SubcategorySection(final ActionLink addLink) {
            setHeading(new Label(gz("cms.ui.category.subcategories")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new SubcategoryList(m_category, m_model));
            group.addAction(new AdminVisible(addLink), ActionGroup.ADD);
        }

    }

    private class LinkedCategorySection extends Section {

        LinkedCategorySection(final ActionLink linkAddLink) {
            setHeading(new Label(gz("cms.ui.category.linked")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new CategoryLinks(m_category, m_model));
            group.addAction(new EditVisible(linkAddLink), ActionGroup.EDIT);
        }

        @Override
        public final boolean isVisible(final PageState state) {
            return m_category.getCategory(state).getParentCategory().isVisible();
        }

    }

    private class CategoryTemplateSection extends Section {

        CategoryTemplateSection() {
            setHeading(new Label(gz("cms.ui.category.templates")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new CategoryTemplates(m_category));
            // XXX secvis
            //group.addAction(link);
        }

    }

//    private class PermissionsSection extends Section {
//
//        @Override
//        public boolean isVisible(PageState ps) {
//            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//            final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);
//            Category cat = m_category.getCategory(ps);
//            return cat.getParentCategory() != null && permissionChecker.isPermitted(AdminPrivileges.ADMINISTER_CATEGORIES);
//        }
//
//        PermissionsSection() {
//            setHeading(new Label(gz("cms.ui.permissions")));
//
//            final ActionGroup group = new ActionGroup();
//            setBody(group);
//
////            PrivilegeDescriptor[] privs = new PrivilegeDescriptor[]{
////                PrivilegeDescriptor.EDIT,
////                Category.MAP_DESCRIPTOR,
////                PrivilegeDescriptor.DELETE,
////                PrivilegeDescriptor.ADMIN
////            };
////
////            HashMap privMap = new HashMap();
////            privMap.put("edit", "Edit");
////            privMap.put("delete", "Delete");
////            privMap.put(Category.MAP_DESCRIPTOR.getName(), "Categorize Items");
////            privMap.put("admin", "Admin");
////
////            final CMSPermissionsPane permPane = new CMSPermissionsPane(privs, privMap,
////                                                                       new ACSObjectSelectionModel(
////                    m_model)) {
////                @Override
////                public void showAdmin(PageState ps) {
////                    Assert.exists(m_model.getSelectedKey(ps));
////
////                    super.showAdmin(ps);
////                    getAdminListingPanel().setVisible(ps, false);
////                }
////
////            };
////
////            final ActionLink restoreDefault = new ActionLink(new Label(gz(
////                    "cms.ui.restore_default_permissions"))) {
////                @Override
////                public boolean isVisible(PageState ps) {
////                    Category cat = m_category.getCategory(ps);
////                    return PermissionService.getContext(cat) == null;
////                }
////
////            };
////
////            final ActionLink useCustom = new ActionLink(new Label(gz(
////                    "cms.ui.use_custom_permissions"))) {
////                @Override
////                public boolean isVisible(PageState ps) {
////                    Category cat = m_category.getCategory(ps);
////                    return PermissionService.getContext(cat) != null;
////                }
////
////            };
////
////            ActionListener al = new ActionListener() {
////                public void actionPerformed(ActionEvent event) {
////                    PageState state = event.getPageState();
////                    // if this is the root then we cannot revert to anything
////                    // since there is not a parent
////                    Category cat = m_category.getCategory(state);
////                    if (!cat.canAdmin()) {
////                        throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
////                    }
////                    DataObject context = PermissionService.getContext(cat);
////                    if (context != null) {
////                        PermissionService.clonePermissions(cat);
////                    } else {
////                        ACSObject parent;
////                        try {
////                            parent = cat.getDefaultParentCategory();
////                        } catch (CategoryNotFoundException ce) {
////                            throw new IllegalStateException(
////                                    "link shouldn't exist for root categories");
////                        }
////                        PermissionService.setContext(cat, parent);
////
////                        // revoke all direct permissions so category will only
////                        // have inherited permissions
////                        ObjectPermissionCollection perms =
////                                                   PermissionService.getDirectGrantedPermissions(
////                                cat.getOID());
////                        while (perms.next()) {
////                            PermissionService.revokePermission(
////                                    new PermissionDescriptor(
////                                    perms.getPrivilege(), cat.getOID(),
////                                    perms.getGranteeOID()));
////                        }
////                    }
////                    permPane.reset(state);
////                }
////
////            }
////
////            restoreDefault.addActionListener(al);
////            useCustom.addActionListener(al);
////
////            SimpleContainer links = new SimpleContainer();
////            links.add(restoreDefault);
////            links.add(useCustom);
////
////            group.setSubject(permPane);
////            group.addAction(links);
////
////            m_model.addChangeListener(new ChangeListener() {
////                public void stateChanged(ChangeEvent e) {
////                    PageState ps = e.getPageState();
////                }
////
////            });
////
//        }
//
//    }
    private static class OrderItemsForm extends CMSForm {

        public OrderItemsForm(CategoryRequestLocal category) {
            super("orderItems", new SimpleContainer());
            Label header = new Label(gz("cms.ui.category.categorized_objects"));
            header.setFontWeight(Label.BOLD);
            add(header);
            add(new CategorizedObjectsList(category));

            add(new Submit("Done"));

        }

    }

    /*
     * This private class creates a link to the index item for a category.
     */
    private class ViewItemLink extends Link {

        ViewItemLink(Component c, String s) {
            super(c, s);
        }

        // Build the preview link.  This uses a standard redirect link to find
        // the content. The prepareURL method is called by the printwriter
        @Override
        protected String prepareURL(final PageState state, String location) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryManager manager = cdiUtil.findBean(
                CategoryManager.class);

            ContentItem indexItem = (ContentItem) manager
                .getIndexObject(m_category.getCategory(state))
                .stream()
                .findFirst()
                .get();
            return "/redirect/?oid=" + URLEncoder.encode(Long.toString(indexItem
                .getObjectId()));
        }

        // We only show this link when an index item exists for this category
        @Override
        public boolean isVisible(PageState state) {
            if (!super.isVisible(state)) {
                return false;
            }
            Category /*ACSObject*/ indexItem = m_category
                    .getCategory(state);//.getDirectIndexObject();
            if (indexItem == null) {
                return false;
            } else {
                return true;
            }
        }

    };

    private class EditItemLink extends Link {

        EditItemLink(Component c, String s) {
            super(c, s);
        }

        /**
         * Build the preview link. This is based on code in the
         * ContentSoonExpiredPane class. The prepareURL method of the parent is
         * overwritten. This method is called by the printwriter
         */
        @Override
        protected String prepareURL(final PageState state, String location) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryManager manager = cdiUtil.findBean(
                CategoryManager.class);
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            final ContentItemManager contentItemManager = cdiUtil.findBean(
                ContentItemManager.class);

            Optional<CcmObject> indexItem = manager
                .getIndexObject(m_category.getCategory(state))
                .stream()
                .findFirst();
            if (!indexItem.isPresent()) {
                return "";
            }
            if (!permissionChecker.isPermitted("", indexItem.get())) {
                return "";
            } else {
                Long draftID = contentItemManager
                    .getDraftVersion((ContentItem) indexItem.get(),
                                     ContentItem.class)
                    .getObjectId();
                return "item.jsp?item_id=" + draftID + "&set_tab="
                           + ContentItemPage.AUTHORING_TAB;
            }
        }

        /**
         * We only show this link when an index item exists for this category
         * and the user is allowed to edit this item.
         *
         * @param state
         *
         * @return
         */
        @Override
        public boolean isVisible(PageState state) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final CategoryManager manager = cdiUtil.findBean(
                CategoryManager.class);
            final ContentItemManager contentItemManager = cdiUtil.findBean(
                ContentItemManager.class);
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            if (!super.isVisible(state)) {
                return false;
            }
            Optional<CcmObject> indexItem = manager.getIndexObject(m_category
                .getCategory(state))
                .stream()
                .findFirst();
            if (!indexItem.isPresent()) {
                return false;
            } else {
                return permissionChecker.isPermitted(ItemPrivileges.EDIT,
                                                     indexItem.get());
            }
        }

    };

    private class MoveLink extends ActionLink {

        private final Label alternativeLabel;

        public MoveLink(final Label label) {
            super(label);
            alternativeLabel = new Label(GlobalizationUtil.globalize(
                "cms.ui.category.cantmoved"));
        }

        @Override
        public void generateXML(final PageState state, final Element parent) {
            if (!isVisible(state)) {
                return;
            }

            final Category category = m_category.getCategory(state);
            if (category.getParentCategory() == null) {
                alternativeLabel.generateXML(state, parent);
            } else {
                super.generateXML(state, parent);
            }
        }

    }

}
