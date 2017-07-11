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
package org.librecms.contenttypes;

import org.libreccm.auditing.AbstractAuditedEntityRepository;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class MultiPartArticleSectionRepository
    extends AbstractAuditedEntityRepository<Long, MultiPartArticleSection> {

    @Override
    public Long getEntityId(final MultiPartArticleSection entity) {
        return entity.getSectionId();
    }

    @Override
    public Class<MultiPartArticleSection> getEntityClass() {
        return MultiPartArticleSection.class;
    }

    @Override
    public boolean isNew(final MultiPartArticleSection entity) {
        return entity.getSectionId() == 0;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<MultiPartArticleSection> findById(
        final long sectionId) {

        final TypedQuery<MultiPartArticleSection> query = getEntityManager()
            .createNamedQuery("MultiPartArticleSection.findById",
                              MultiPartArticleSection.class);
        query.setParameter("sectionId", sectionId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public MultiPartArticle findArticleOfSection(
        final MultiPartArticleSection section) {

        final TypedQuery<MultiPartArticle> query = getEntityManager()
        .createNamedQuery("MultiPartArticleSection.findArticleOfSection", 
                          MultiPartArticle.class);
        query.setParameter("section", section);
        
        return query.getSingleResult();
    }

}
