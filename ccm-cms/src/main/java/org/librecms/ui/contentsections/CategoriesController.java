/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/categorysystems")
public class CategoriesController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listCategorySystems(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        final Optional<ContentSection> sectionResult = retrieveContentSection(
            sectionIdentifier);
        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();

        final List<DomainListEntryModel> domains = section
            .getDomains()
            .stream()
            .map(this::buildDomainListEntryModel)
            .collect(Collectors.toList());

        models.put("categorySystems", domains);

        return "org/librecms/ui/contentsection/categorysystems/categorysystems.xhtml";
    }

    @GET
    @Path("/{key}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCategorySystem(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("key") final String domainKey
    ) {
        return showCategorySystem(sectionIdentifier, domainKey, "");
    }

    @GET
    @Path("/{key}/{categoryPath:(.+)?}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showCategorySystem(
        @PathParam("sectionIdentifier") final String sectionIdentifier,
        @PathParam("key") final String domainKey,
        @PathParam("categoryPath") final String categoryPath
    ) {
        //ToDo: Category System Model with
        //* List of category systems
        //* Category tree
        //* Display of active category (if none = root?) with edit options
        //  listed below
        //
        
        throw new UnsupportedOperationException();
    }

    //ToDo: Show category details
    //
    //ToDo: Rename category (disabled for root category)
    //
    //ToDo: Add, update, remove localized title
    //
    //ToDo: Set enabled, visible, abstract
    //
    //ToDo: Set and unset index element
    //
    //ToDo: Move category (disabled for root category)
    
    //ToDo: Delete category (disabled for root category)
    //
    //ToDo: List subcategories
    //
    //ToDo: Order subcategories
    //
    //ToDo: Add subcategory
    
    private Optional<ContentSection> retrieveContentSection(
        final String sectionIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );

        final Optional<ContentSection> sectionResult;
        switch (identifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(identifier.getIdentifier())
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(identifier
                    .getIdentifier());
                break;
            default:
                sectionResult = sectionRepo.findByLabel(identifier
                    .getIdentifier());
                break;
        }
        return sectionResult;
    }

    private DomainListEntryModel buildDomainListEntryModel(
        final DomainOwnership ownership
    ) {
        final Domain domain = ownership.getDomain();

        final DomainListEntryModel model = new DomainListEntryModel();
        model.setContext(ownership.getContext());
        model.setDomainKey(domain.getDomainKey());
        model.setReleased(
            DateTimeFormatter.ISO_DATE.withZone(ZoneId.systemDefault())
                .format(domain.getReleased()));
        model.setTitle(
            globalizationHelper.getValueFromLocalizedString(domain.getTitle())
        );
        model.setUri(domain.getUri());
        model.setVersion(domain.getVersion());

        return model;
    }

}
