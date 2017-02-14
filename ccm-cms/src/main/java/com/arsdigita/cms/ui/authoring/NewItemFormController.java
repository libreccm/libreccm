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

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Controller class for the {@link NewItemForm}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class NewItemFormController {

    @Inject
    private EntityManager entityManager;
    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean hasContentTypes(final ContentSection section) {
        Objects.requireNonNull(section, "Can't work with null for the section.");

//        final Optional<ContentSection> contentSection = sectionRepo.findById(
//            section.getObjectId());
//
//        if (contentSection.isPresent()) {
            final TypedQuery<Long> query = entityManager.createNamedQuery("ContentSection.countContentTypes", Long.class);
            query.setParameter("section", section);
            return query.getSingleResult() > 0;
//        } else {
//            throw new UnexpectedErrorException(String.format(
//                "ContentSection %s was passed to this method but does not exist "
//                + "in the database.",
//                Objects.toString(section)));
//        }
    }

}
