/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.categorization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.subject.Subject;
import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides CRUB operations for {@link Category} objects.
 *
 * Note: This repository class does no permission checks when retrieving
 * categories. This is the responsibility of the application which uses the
 * retrieved categories.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CategoryRepository extends AbstractEntityRepository<Long, Category> {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryRepository.class);

    @Inject
    private DomainRepository domainRepo;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private Subject subject;

    @Override
    public Class<Category> getEntityClass() {
        return Category.class;
    }

    @Override
    public boolean isNew(final Category entity) {
        return entity.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Category category) {
        category.setUuid(UUID.randomUUID().toString());
    }

    /**
     * Retrieves a list of all top level categories (Categories without a parent
     * category).
     *
     * @return A list of all top level categories.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<Category> getTopLevelCategories() {
        final TypedQuery<Category> query = getEntityManager().createNamedQuery(
            "Category.topLevelCategories", Category.class);

        return query.getResultList();
    }

    /**
     * Finds a {@link Category} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<Category> findByUuid(final String uuid) {
        final TypedQuery<Category> query = getEntityManager().
                createNamedQuery("Category.findByUuid", Category.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Category> findByPath(final String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path can't be null or empty.");
        }

        final String[] tokens = path.split(":");
        if (tokens.length > 2) {
            throw new InvalidCategoryPathException(
                "The provided path is invalid: More than one colon found. "
                    + "Valid path format: domainKey:path");
        }

        if (tokens.length < 2) {
            throw new InvalidCategoryPathException(
                "The provided path is invalid: No domain found in path. "
                    + "Valid path format: domainKey:path");
        }

        final Optional<Domain> domain = domainRepo.findByDomainKey(tokens[0]);
        if (domain.isPresent()) {
            return findByPath(domain.get(), tokens[1]);
        } else {
            throw new InvalidCategoryPathException(String.format(
                "No domain identified by the key '%s' found.",
                tokens[0]));
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Category> findByPath(final Domain domain, final String path) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain can't be null.");
        }

        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path can't be null or empty.");
        }

        String normalizedPath = path.replace('.', '/');
        if (normalizedPath.charAt(0) == '/') {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0,
                                                      normalizedPath.length());
        }

        LOGGER.debug("Trying to find category with path \"{}\" in "
                         + "domain \"{}\".",
                     normalizedPath,
                     domain.getDomainKey());
        final String[] tokens = normalizedPath.split("/");
        Category current = domain.getRoot();
        for (final String token : tokens) {
            if (current.getSubCategories().isEmpty()) {
                return Optional.empty();
            }
            final Optional<Category> result = current.getSubCategories()
                .stream()
                .filter(c -> filterCategoryByName(c, token))
                .findFirst();

            if (result.isPresent()) {
                current = result.get();
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(current);
    }

    private boolean filterCategoryByName(final Category category,
                                         final String name) {
        LOGGER.debug("#findByPath(Domain, String): c = {}",
                     category.toString());
        LOGGER.debug(
            "#findByPath(Domain, String): c.getName = \"{}\"",
            category.getName());
        LOGGER.debug("#findByPath(Domain, String): token = \"{}\"",
                     name);
        return category.getName().equals(name);
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(
        @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_CATEGORY)
        final Category category) {

        super.save(category);
    }

    @AuthorizationRequired

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(
        @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_CATEGORY)
        final Category category) {

        super.save(category);
    }

}
