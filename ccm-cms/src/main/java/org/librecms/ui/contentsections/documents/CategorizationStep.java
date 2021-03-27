/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections.documents;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.ObjectNotAssignedToCategoryException;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;

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
    private CategoryRepository categoryRepo;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    private ContentSection section;

    private ContentItem document;

    @Override
    public Class<? extends ContentItem> supportedDocumentType() {
        return ContentItem.class;
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.categorization.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("authoringsteps.categorization.description");
    }

    @Override
    public String getBundle() {
        return DefaultAuthoringStepConstants.BUNDLE;
    }

    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }

    @Override
    public ContentItem getContentItem() {
        return document;
    }

    @Override
    public void setContentItem(final ContentItem document) {
        this.document = document;
    }

    @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    @Override
    public String showStep() {
        return "org/librecms/ui/documents/categorization.xhtml";
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<CategorizationTree> getCategorizationTrees() {
        return section
            .getDomains()
            .stream()
            .map(DomainOwnership::getDomain)
            .map(this::buildCategorizationTree)
            .collect(Collectors.toList());
    }

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
