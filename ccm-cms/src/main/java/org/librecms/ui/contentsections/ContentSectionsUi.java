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
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Models;

/**
 * Provides common functions for controllers working with
 * {@link ContentSection}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentSectionsUi {

    /**
     * Used to retrieve {@link ContentSection}s.
     */
    @Inject
    private ContentSectionRepository sectionRepo;

    /**
     * Used to parse the identifier of {@link ContentSection}s.
     */
    @Inject
    private IdentifierParser identifierParser;

    /**
     * Used to provided data for views (here: error pages) without a named bean.
     */
    private Models models;

    /**
     * Retrieve a content section.
     *
     * @param identifierParam The identifier of the content section.
     *
     * @return An {@link Optional} with the content section, or an empty
     *         {@link Optional} if there is not {@link ContentSection} with the
     *         provided identifier.
     */
    public Optional<ContentSection> findContentSection(
        final String identifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            Objects.requireNonNull(
                identifierParam,
                "Can't retrieve a ContentSection for identifier null."
            )
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

    /**
     * Show the access denied error page.
     *
     * @param identifiers The identifiers of the resource.
     *
     * @return The template of the access denied error page.
     */
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

    /**
     * Show the access denied error page.
     *
     * @param identifiers The identifiers of the resource.
     *
     * @return The template of the access denied error page.
     */
    public String showAccessDenied(final Map<String, String> identifiers) {
        for (final Map.Entry<String, String> entry : identifiers.entrySet()) {
            models.put(entry.getKey(), entry.getValue());
        }
        return "org/librecms/ui/contentsection/access-denied.xhtml";
    }

    /**
     * Show the "content section not found" error page.
     *
     * @param sectionIdentifier The identifier of the content section.
     *
     * @return The template of the "content section not found" error page.
     */
    public String showContentSectionNotFound(final String sectionIdentifier) {
        models.put("sectionIdentifier", sectionIdentifier);
        return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
    }

}
