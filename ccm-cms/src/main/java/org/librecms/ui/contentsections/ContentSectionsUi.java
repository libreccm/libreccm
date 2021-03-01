/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Models;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ContentSectionsUi {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private IdentifierParser identifierParser;

    private Models models;

    public Optional<ContentSection> findContentSection(
        final String identifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            identifierParam
        );

        switch (sectionIdentifier.getType()) {
            case ID:
                return sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
            case UUID:
                return sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
            default:
                return sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
        }
    }

    public String showAccessDenied(final String... identifiers) {
        if (identifiers.length % 2 != 0) {
            throw new IllegalArgumentException(
                "The length of the identifiers must be even."
            );
        }
        for (int i = 1; i < identifiers.length; i = +2) {
            models.put(identifiers[i - 1], identifiers[i]);
        }
        return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
    }

    public String showAccessDenied(final Map<String, String> identifiers) {
        for (final Map.Entry<String, String> entry : identifiers.entrySet()) {
            models.put(entry.getKey(), entry.getValue());
        }
        return "org/librecms/ui/contentsection/access-denied.xhtml";
    }

    public String showContentSectionNotFound(final String sectionIdentifier) {
        models.put("sectionIdentifier", sectionIdentifier);
        return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
    }

    public RetrieveResult<ContentSection> retrieveContentSection(
        final String identifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            identifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (sectionResult.isPresent()) {
            return RetrieveResult.successful(sectionResult.get());
        } else {
            models.put("sectionIdentifier", sectionIdentifier);
            return RetrieveResult.failed(
                "org/librecms/ui/contentsection/contentsection-not-found.xhtml"
            );
        }
    }

}
