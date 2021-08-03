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

import org.librecms.ui.contentsections.ContentSectionNotFoundException;
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
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
@Path(MvcAuthoringSteps.PATH_PREFIX + "categorization")
@Controller
@MvcAuthoringStepDef(
    bundle = DefaultAuthoringStepConstants.BUNDLE,
    descriptionKey = "authoringsteps.categorization.description",
    labelKey = "authoringsteps.categorization.label",
    supportedDocumentType = ContentItem.class
)
public class CategorizationStep extends AbstractMvcAuthoringStep {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategorizationStepModel categorizationStepModel;

    @Inject
    private DocumentUi documentUi;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init() throws ContentSectionNotFoundException,
                                 DocumentNotFoundException {
        super.init();

        categorizationStepModel.setCategorizationTrees(
            getContentSection()
                .getDomains()
                .stream()
                .map(DomainOwnership::getDomain)
                .map(this::buildCategorizationTree)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Class<CategorizationStep> getStepClass() {
        return CategorizationStep.class;
    }

    @GET
    @Path("/")
    @Transactional(Transactional.TxType.REQUIRED)
    public String showStep(
        @PathParam(MvcAuthoringSteps.SECTION_IDENTIFIER_PATH_PARAM)
        final String sectionIdentifier,
        @PathParam(MvcAuthoringSteps.DOCUMENT_PATH_PATH_PARAM_NAME)
        final String documentPath
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        if (permissionChecker.isPermitted(
            ItemPrivileges.CATEGORIZE, getDocument()
        )) {
            return "org/librecms/ui/contentsection/documents/categorization.xhtml";
        } else {
            return documentUi.showAccessDenied(
                getContentSection(),
                getDocument(),
                getLabel()
            );
        }
    }

    /**
     * Update the categorization of the current item.
     *
     *
     *
     * @param domainParam
     * @param assignedCategoriesParam
     *
     * @return A redirect to the categorization step.
     */
    @POST
    @Path("/domains/{domain}")
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateCategorization(
        @PathParam("domain")
        final String domainParam,
        @FormParam("assigned-categories")
        final Set<String> assignedCategoriesParam
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (DocumentNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final Identifier domainIdentifier = identifierParser.parseIdentifier(
            domainParam
        );
        final Optional<Domain> domainResult;
        switch (domainIdentifier.getType()) {
            case ID:
                domainResult = getContentSection()
                    .getDomains()
                    .stream()
                    .map(DomainOwnership::getDomain)
                    .filter(
                        domain -> domain.getObjectId() == Long
                        .parseLong(domainIdentifier.getIdentifier())
                    ).findAny();
                break;
            case UUID:
                domainResult = getContentSection()
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
                domainResult = getContentSection()
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
            models.put("section", getContentSection().getLabel());
            models.put("domainIdentifier", domainIdentifier);
            return "org/librecms/ui/documents/categorization-domain-not-found.xhtml";
        }

        updateAssignedCategories(
            domainResult.get().getRoot(), assignedCategoriesParam
        );

        return buildRedirectPathForStep();
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
        final ContentItem document = getDocument();
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
        
        if (!category.getSubCategories().isEmpty()) {
            for(final Category subCategory : category.getSubCategories()) {
                updateAssignedCategories(subCategory, assignedCategoriesParam);
            }
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
            assigned.add(String.join("/", parentPath, getCategoryLabel(node)));
        }

        if (node.isSubCategoryAssigned()) {
            assigned.addAll(
                node
                    .getSubCategories()
                    .stream()
                    .map(
                        subCat -> buildAssignedCategoriesList(
                            subCat, 
                            String.join(
                                "/", 
                                parentPath,
                                getCategoryLabel(node)
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
        final ContentItem document = getDocument();
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
//        node.setSubCategoryAssigned(
//            isSubCategoryAssigned(category)
////            category
////                .getSubCategories()
////                .stream()
////                .allMatch(
////                    subCat -> categoryManager.isAssignedToCategory(
////                        subCat, document
////                    )
////                )
//        );
        node.setTitle(
            globalizationHelper.getValueFromLocalizedString(
                category.getTitle()
            )
        );
        node.setUniqueId(category.getUniqueId());

        return node;
    }

//    private boolean isSubCategoryAssigned(final Category category) {
//        boolean result = false;
//        for (final Category subCategory : category.getSubCategories()) {
//            result = result || categoryManager.isAssignedToCategory(subCategory, getDocument());
//            
//            if (!subCategory.getSubCategories().isEmpty()) {
//                result = result || isSubCategoryAssigned(subCategory);
//            }
//        }
//        
//        return result;
//    }
    
    private String getCategoryLabel(final CategorizationTreeNode node) {
        if (node.getTitle() == null || node.getTitle().isBlank()) {
            return node.getCategoryName();
        } else {
            return node.getTitle();
        }
    }
    
}
