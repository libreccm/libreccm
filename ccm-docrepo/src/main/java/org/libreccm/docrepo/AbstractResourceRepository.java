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
package org.libreccm.docrepo;

import org.libreccm.auditing.AbstractAuditedEntityRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for retrieving, storing and deleting
 * {@code AbstractResource}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
public abstract class AbstractResourceRepository<T extends AbstractResource>
    extends AbstractAuditedEntityRepository<Long, T> {

    protected Class classOfT;

    @Inject
    protected EntityManager entityManager;

    @Override
    public Long getEntityId(T entity) {
        return entity.getObjectId();
    }

    @Override
    public Class<T> getEntityClass() {
        return classOfT;
    }

    @Override
    public boolean isNew(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save can't be null.");
        }
        return entity.getObjectId() == 0;
    }

    /**
     * Checks if the current subject has permissions grating him the privilege
     * to read the requested {@link AbstractResource}(s) and removes the ones he
     * is not allowed to access.
     *
     * @param resources The requested {@link AbstractResource}s, found in the
     *                  database
     *
     * @return A list of {@link AbstractResource}s the subject is allowed to
     *         access
     */
    private List<T> permissionFilter(List<T> resources) {
        final PermissionChecker permissionChecker = CdiUtil.createCdiUtil()
            .findBean(PermissionChecker.class);
        return resources.stream().filter(resource -> permissionChecker
            .isPermitted("read", resource)).collect(Collectors.toList());
    }

    /**
     * Checks if the current subject has permissions grating him the privilege
     * to read the one requested {@link AbstractResource} and removes it if he
     * is not allowed to access.
     *
     * @param resource The requested {@link AbstractResource}, found in the
     *                 database
     *
     * @return A list of at most one {@link AbstractResource} the subject is
     *         allowed to access
     */
    private T permissionFilter(T resource) {
        return permissionFilter(Collections.singletonList(resource)).get(0);
    }

    /**
     * Retrieve all {@link AbstractResource}s with the given name.
     *
     * @param name The name for the searched {@link AbstractResource}
     *
     * @return The {@link AbstractResource}s with the given name, if there
     *         aren't any an {@code EmptyList}
     */
    public List<T> findByName(final String name) {
        final TypedQuery<T> query = getFindByNameQuery();
        query.setParameter("name", name);

        return permissionFilter(query.getResultList());
    }

    /**
     * Abstract method to get a {@link TypedQuery}, specifically implemented in
     * the subclasses matching their own database requests, finding the
     * {@code T}-typed objects by name.
     *
     * @return A {@link TypedQuery} to find objects by name
     */
    protected abstract TypedQuery<T> getFindByNameQuery();

    /**
     * Retrieve a {@code AbstractResource} by its {@code path}.
     *
     * @param pathName The {@code path} to the {@code AbstractResource}.
     *
     * @return The {@code AbstractResource} identified by the given
     *         {@code path}, if there is such a {@code AbstractResource},
     *         {@code null} if not.
     */
    public T findByPathName(final String pathName) {
        final TypedQuery<T> query = getFindByPathNameQuery();
        query.setParameter("pathName", pathName);

        return permissionFilter(query.getSingleResult());
    }

    /**
     * Abstract method to get a {@link TypedQuery}, specifically implemented in
     * the subclasses matching their own database requests, finding the
     * {@code T}-typed objects by path name.
     *
     * @return A {@link TypedQuery} to find objects by path name
     */
    protected abstract TypedQuery<T> getFindByPathNameQuery();

    /**
     * Retrieve the {@code AbstractResource}s, a given {@link User} created.
     *
     * @param creator The {@link User}, who created the
     *                {@code AbstractResource}s.
     *
     * @return The {@code AbstractResource}s, created by the given {@link User},
     *         if there are such {@code AbstractResource}s, {@code EmptyList} if
     *         not.
     */
    public List<T> findForCreator(final User creator) {
        final TypedQuery<T> query = getFindForCreatorQuery();
        query.setParameter("user", creator);

        return permissionFilter(query.getResultList());
    }

    /**
     * Abstract method to get a {@link TypedQuery}, specifically implemented in
     * the subclasses matching their own database requests, finding the
     * {@code T}-typed objects created by a given/set {@link User}.
     *
     * @return A {@link TypedQuery} to find objects for creator.
     */
    protected abstract TypedQuery<T> getFindForCreatorQuery();

    /**
     * Retrieve the {@code AbstractResource}s, a given {@link User} last
     * modified.
     *
     * @param modifier The {@link User} who last modified the
     *                 {@code AbstractResource}s.
     *
     * @return The {@code AbstractResource}s, last modified by the given
     *         {@link User}, if there are such {@code AbstractResource}s,
     *         {@code EmptyList} if not.
     */
    public List<T> findForModifier(final User modifier) {
        final TypedQuery<T> query = getFindForModifierQuery();
        query.setParameter("user", modifier);

        return permissionFilter(query.getResultList());
    }

    /**
     * Abstract method to get a {@link TypedQuery}, specifically implemented in
     * the subclasses matching their own database requests, finding the
     * {@code T}-typed objects last modified for a given/set {@link User}.
     *
     * @return A {@link TypedQuery} to find objects for last modifier.
     */
    protected abstract TypedQuery<T> getFindForModifierQuery();

}
