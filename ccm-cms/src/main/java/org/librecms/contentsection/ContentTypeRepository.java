/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentTypeRepository
    extends AbstractEntityRepository<Long, ContentType> {

    private static final long serialVersionUID = 5871606965722748001L;

    @Inject
    private EntityManager entityManager;

    @Override
    public Class<ContentType> getEntityClass() {
        return ContentType.class;
    }

    @Override
    public boolean isNew(final ContentType type) {
        return type.getObjectId() == 0;
    }

    public Optional<ContentType> findByIdAndFetchAttributes(
        final Long typeId, final String... fetchAttributes) {

        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ContentType> criteriaQuery = builder
            .createQuery(getEntityClass());

        final Root<ContentType> from = criteriaQuery.from(getEntityClass());
        for (final String fetchAttribute : fetchAttributes) {
            from.fetch(fetchAttribute, JoinType.LEFT);
        }

        criteriaQuery.select(from);
        criteriaQuery.where(builder.equal(from.get("objectId"), typeId));

        try {
            final TypedQuery<ContentType> query = entityManager
                .createQuery(criteriaQuery);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds all {@link ContentType}s of a specific content section.
     *
     * @param section The section whose {@link ContentTyp}s are retrieved. Can't
     *                be {@code null}.
     *
     * @return A list of all {@link ContentType}s of the provided section
     *         ordered alphabetically.
     */
    public List<ContentType> findByContentSection(final ContentSection section) {
        if (section == null) {
            throw new IllegalArgumentException(
                "Parameter section of method "
                    + "ContentTypeRepo#findByContentSection("
                    + "ContentSection section) can't be null.");
        }

        final TypedQuery<ContentType> query = getEntityManager()
            .createNamedQuery("ContentType.findByContentSection",
                              ContentType.class);
        query.setParameter("contentSection", section);

        return query.getResultList();
    }

    /**
     * Retrieves a specific {@link ContentType}.
     *
     * @param section The section associated with the {@link ContentType} to
     *                retrieve. Can't be {@code null}.
     * @param clazz   The subclass of the {@link ContentItem} class associated
     *                with the content type. Can't be {@code null}.
     *
     * @return The requested {@link ContentType} if their is a
     *         {@link ContentType} for the provided class in the provided
     *         section. Otherwise the returned {@link Optional} will be empty.
     */
    public Optional<ContentType> findByContentSectionAndClass(
        final ContentSection section,
        final Class<? extends ContentItem> clazz) {

        if (section == null) {
            throw new IllegalArgumentException(
                "Parameter section of method "
                    + "ContentTypeRepo#findByContentSectionAndClass("
                    + "ContentSection section, "
                    + "Class<? extends ContentItem> clazz) can't be null.");
        }
        if (clazz == null) {
            throw new IllegalArgumentException(
                "Parameter clazz of method "
                    + "ContentTypeRepo#findByContentSectionAndClass("
                    + "ContentSection section, "
                    + "Class<? extends ContentItem> clazz) can't be null.");
        }

        return findByContentSectionAndClass(section, clazz.getName());
    }

    /**
     * Retrieves a specific {@link ContentType}.
     *
     * @param section   The section associated with the {@link ContentType} to
     *                  retrieve.
     * @param className The name of the subclass of the {@link ContentItem}
     *                  class associated with the content type. The class must
     *                  be a subclass of {@link ContentItem}.
     *
     * @return The requested {@link ContentType} if their is a
     *         {@link ContentType} for the provided class in the provided
     *         section. Otherwise the returned {@link Optional} will be empty.
     */
    public Optional<ContentType> findByContentSectionAndClass(
        final ContentSection section,
        final String className) {
        if (section == null) {
            throw new IllegalArgumentException(
                "Parameter section of method "
                    + "ContentTypeRepo#findByContentSectionAndClass("
                    + "ContentSection section, "
                    + "String className) can't be null.");
        }
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException(
                "Parameter className of method "
                    + "ContentTypeRepo#findByContentSectionAndClass("
                    + "ContentSection section, "
                    + "String className) can't be null.");
        }

        try {
            final Class<?> clazz = Class.forName(className);
            if (!ContentItem.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(String.format(
                    "The provided class \"%s\" is not a subclass of \"%s\".",
                    className,
                    ContentItem.class.getName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(String.format(
                "Class \"%s\" does not exist.", className));
        }

        final TypedQuery<ContentType> query = getEntityManager()
            .createNamedQuery("ContentType.findByContentSectionAndClass",
                              ContentType.class);
        query.setParameter("contentSection", section);
        query.setParameter("clazz", className);

        final List<ContentType> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else if (result.size() > 1) {
            throw new RuntimeException(String.format(
                "More than one ContentType for section \"%s\" and type \"%s\" "
                    + "found. This is an invalid state. Check your installation"
                    + "immediatly.",
                section.getLabel(),
                className));
        } else {
            return Optional.of(result.get(0));
        }
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type) {

        super.save(type);
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(
        @RequiresPrivilege(AdminPrivileges.ADMINISTER_CONTENT_TYPES)
        final ContentType type) {

        if (isContentTypeInUse(type)) {
            throw new IllegalArgumentException(String.format(
                "Contenttype \"%s\" in section \"%s\" is in use and can't be"
                    + "deleted.",
                type.getContentItemClass(),
                type.getContentSection().getDisplayName()));
        } else {
            super.delete(type);
        }
    }

    /**
     * Checks if there is any item of the provided content type and the content
     * section to which the type belongs.
     *
     * @param type The type to check for usage.
     *
     * @return {@code true} if the type is in use, {@code false} if not.
     */
    public boolean isContentTypeInUse(final ContentType type) {
        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "ContentType.isInUse", Long.class);
        query.setParameter("type", type);

        final long result = query.getSingleResult();

        return result > 0;
    }

}
