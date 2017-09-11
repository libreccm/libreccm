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
package com.arsdigita.cms.ui.authoring;

import org.libreccm.categorization.Category;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ItemCategoryStepController {

    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<Category> getRootCategories(final ContentSection section) {

        Objects.requireNonNull(section);

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentSection with ID %d in the database.",
                    section.getObjectId())));

        return contentSection
            .getDomains()
            .stream()
            .map(domainOwnership -> domainOwnership.getDomain())
            .map(domain -> domain.getRoot())
            .collect(Collectors.toList());
    }

}
