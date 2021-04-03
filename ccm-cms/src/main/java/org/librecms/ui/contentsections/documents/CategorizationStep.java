/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Authoring step for categorizing a {@link ContentItem}. The class is an EE MVC
 * controller as well as a model for the view of the authoring step.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
@AuthoringStepPathFragment(CategorizationStep.PATH_FRAGMENT)
@Named("CmsCategorizationStep")
public class CategorizationStep implements MvcAuthoringStep {

    static final String PATH_FRAGMENT = "categorization";

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private DocumentUi documentUi;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    /**
     * The current content section.
     */
    private ContentSection section;

    /**
     * The current document.
     */
    private ContentItem document;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends ContentItem> supportedDocumentType() {
        return ContentItem.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.categorization.label");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.categorization.description");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBundle() {
        return DefaultAuthoringStepConstants.BUNDLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentSection getContentSection() {
        return section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentItem getContentItem() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentItem(final ContentItem document) {
        this.document = document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String showStep() {
        if (permissionChecker.isPermitted(ItemPrivileges.CATEGORIZE, document)) {
            return "org/librecms/ui/documents/categorization.xhtml";
        } else {
            return documentUi.showAccessDenied(
                section,
                document,
                getLabel()
            );
        }
    }

    /**
     * Provides a tree view of the category system assigned to the current
     * content section in an format which can be processed in MVC templates.
     *
     * The categories assigned to the current item as marked.
     *
     * @return Tree view of the category systems assigned to the current content
     *         section.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<CategorizationTree> getCategorizationTrees() {
        return section
            .getDomains()
            .stream()
            .map(DomainOwnership::getDomain)
            .map(this::buildCategorizationTree)
            .collect(Collectors.toList());
    }

    /**
     * Update the categorization of the current item.
     *
     * @param domainIdentifierParam   The identifier for category system to use.
     * @param assignedCategoriesParam The UUIDs of the categories assigned to
     *                                the current content item.
     *
     * @return A redirect to the categorization step.
     */
    @POST
    @Path("/{domainIdentifier}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateCategorization(
        @PathParam("domainIdentifierParam") final String domainIdentifierParam,
        @FormParam("assignedCategories")
        final Set<String> assignedCategoriesParam
    ) {
        final Identifier domainIdentifier = identifierParser.parseIdentifier(
            domainIdentifierParam
        );
        final Optional<Domain> domainResult;
        switch (domainIdentifier.getType()) {
            case ID:
                domainResult = section
                    .getDomains()
                    .stream()
                    .map(DomainOwnership::getDomain)
                    .filter(
                        domain -> domain.getObjectId() == Long
                        .parseLong(domainIdentifier.getIdentifier())
                    ).findAny();
                break;
            case UUID:
                domainResult = section
                    .getDomains()
                    .stream()
                    .map(DomainOwnership::getDomain)
                    .filter(
                        domain -> domain.getUuid().equals(
                            domainIdentifier.getIdentifier()
                        )
                    ).findAny();
                break;
            default:
                domainResult = section
                    .getDomains()
                    .stream()
                    .map(DomainOwnership::getDomain)
                    .filter(
                        domain -> domain.getDomainKey().equals(
                            domainIdentifier.getIdentifier()
                        )
                    ).findAny();
        }

        if (!domainResult.isPresent()) {
            models.put("section", section.getLabel());
            models.put("domainIdentifier", domainIdentifierParam);
            return "org/librecms/ui/documents/categorization-domain-not-found.xhtml";
        }

        final Domain domain = domainResult.get();
        updateAssignedCategories(domain.getRoot(), assignedCategoriesParam);

        return String.format(
            "redirect:/%s/@documents/%s/@authoringsteps/%s",
            section.getLabel(),
            getContentItemPath(),
            PATH_FRAGMENT
        );
    }

    /**
     * Helper method for updating the assigned categories of the current content
     * item. If the current item is not assigned to a category included in the
     * {@code assignedCategoriesParam} to category is assigned to the content
     * item. Likewise, if a category is assigned to the current content item,
     * but not included in the {@code assignedCategoriesParam} the catgory is
     * removed from the current content item.
     *
     * @param category                A category
     * @param assignedCategoriesParam The UUIDs of the categories which should
     *                                be assigned to the current content item.
     *
     */
    private void updateAssignedCategories(
        final Category category,
        final Set<String> assignedCategoriesParam
    ) {
        if (assignedCategoriesParam.contains(category.getUuid())
                && !categoryManager.isAssignedToCategory(category, document)) {
            categoryManager.addObjectToCategory(document, category);
        }

        try {
            if (!assignedCategoriesParam.contains(category.getUuid())
                    && categoryManager.isAssignedToCategory(category, document)) {
                categoryManager.removeObjectFromCategory(document, category);
            }
        } catch (ObjectNotAssignedToCategoryException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

    /**
     * Helper method for building the {@link CategorizationTree} for a category
     * system.
     *
     * @param domain The category system from which the
     *               {@link CategorizationTree} is created.
     *
     * @return The {@link CategorizationTree} for the provided category system.
     */
    private CategorizationTree buildCategorizationTree(final Domain domain) {
        final CategorizationTree tree = new CategorizationTree();
        tree.setDomainDescription(
            globalizationHelper.getValueFromLocalizedString(
                domain.getDescription()
            )
        );
        tree.setDomainKey(domain.getDomainKey());
        tree.setDomainTitle(
            globalizationHelper.getValueFromLocalizedString(
                domain.getTitle())
        );
        tree.setRoot(buildCategorizationTreeNode(domain.getRoot()));

        tree.setAssignedCategories(
            buildAssignedCategoriesList(tree.getRoot(), "")
        );

        return tree;
    }

    /**
     * Helper method for building a list of the categories assigned to the
     * current content item.
     *
     * @param node       A {@link CategorizationTreeNode}
     * @param parentPath The parent path of the category represented by the
     *                   {@code node}.
     *
     * @return A list of paths of the categories assigned to the current content
     *         item.
     */
    private List<String> buildAssignedCategoriesList(
        final CategorizationTreeNode node, final String parentPath
    ) {
        final List<String> assigned = new ArrayList<>();
        if (node.isAssigned()) {
            assigned.add(String.join("/", parentPath, node.getTitle()));
        }

        if (node.isSubCategoryAssigned()) {
            assigned.addAll(
                node
                    .getSubCategories()
                    .stream()
                    .map(
                        subCat -> buildAssignedCategoriesList(
                            subCat, String.join("/", node.getTitle()
                            )
                        )
                    )
                    .flatMap(result -> result.stream())
                    .collect(Collectors.toList())
            );
        }

        return assigned;
    }

    /**
     * Helper method for building the {@link CategorizationTreeNode} for a
     * category.
     *
     * @param category The category from which the node is created.
     *
     * @return A {@link CategorizationTreeNode} for the provided
     *         {@code category}.
     */
    private CategorizationTreeNode buildCategorizationTreeNode(
        final Category category
    ) {
        final CategorizationTreeNode node = new CategorizationTreeNode();
        node.setAssigned(categoryManager.isAssignedToCategory(
            category, document)
        );
        node.setCategoryId(category.getObjectId());
        node.setCategoryName(category.getName());
        node.setCategoryUuid(category.getUuid());
        node.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                category.getDescription()
            )
        );
        node.setSubCategories(
            category
                .getSubCategories()
                .stream()
                .map(this::buildCategorizationTreeNode)
                .collect(Collectors.toList())
        );
        node.setSubCategoryAssigned(
            category
                .getSubCategories()
                .stream()
                .allMatch(
                    subCat -> categoryManager.isAssignedToCategory(
                        subCat, document
                    )
                )
        );
        node.setTitle(
            globalizationHelper.getValueFromLocalizedString(
                category.getTitle()
            )
        );
        node.setUniqueId(category.getUniqueId());

        return node;
    }

}
