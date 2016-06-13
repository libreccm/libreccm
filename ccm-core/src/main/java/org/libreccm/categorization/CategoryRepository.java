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
import org.libreccm.security.PermissionChecker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
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
    public List<Category> getTopLevelCategories() {
        final TypedQuery<Category> query = getEntityManager().createNamedQuery(
            "Category.topLevelCategories", Category.class);

        return query.getResultList();
    }

    public Category findByPath(final String path) {
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

        final Domain domain = domainRepo.findByDomainKey(tokens[0]);
        if (domain == null) {
            throw new InvalidCategoryPathException(String.format(
                "No domain identified by the key '%s' found.",
                tokens[0]));
        }

        return findByPath(domain, tokens[1]);
    }

    public Category findByPath(final Domain domain, final String path) {
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

        LOGGER.debug(String.format(
            "Trying to find category with path \"%s\" in "
                + "domain \"%s\".",
            normalizedPath,
            domain.getDomainKey()));
        final String[] tokens = normalizedPath.split("/");
        Category current = domain.getRoot();
        for (final String token : tokens) {
            if (current.getSubCategories() == null) {
                return null;
            }
            final Optional<Category> result = current.getSubCategories()
                .stream()
                .filter((c) -> {
                    LOGGER.debug("#findByPath(Domain, String): c = {}",
                                 c.toString());
                    LOGGER.debug(
                        "#findByPath(Domain, String): c.getName = \"{}\"",
                        c.getName());
                    LOGGER.debug("#findByPath(Domain, String): token = \"{}\"",
                                 token);
                    return c.getName().equals(token);
                })
                .findFirst();
            if (result.isPresent()) {
                current = result.get();
            } else {
                return null;
            }
        }

        return current;
    }
    
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void save(final Category category) {
        super.save(category);
    }
    
    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(final Category category) {
        super.save(category);
    }
}
