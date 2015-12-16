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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for retrieving, storing and deleting {@code Resource}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 01/10/2015
 */
@RequestScoped
public class ResourceRepository extends AbstractAuditedEntityRepository<Long, Resource> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Long getEntityId(Resource entity) {
        return entity.getObjectId();
    }

    @Override
    public Class<Resource> getEntityClass() {
        return Resource.class;
    }

    @Override
    public boolean isNew(Resource entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save can't be null.");
        }
        return entity.getObjectId() == 0;
    }

    /**
     * Checks if the current subject has permissions grating him the
     * privilege to read the requested {@link Resource}(s) and removes the
     * ones he is not allowed to access.
     *
     * @param resources The requested {@link Resource}s, found in the database
     * @return A list of {@link Resource}s the subject is allowed to access
     */
    private List<Resource> permissionFilter(List<Resource> resources) {
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        return resources.stream().filter(resource -> permissionChecker
                .isPermitted("read", resource)).collect(Collectors.toList());
    }

    /**
     * Checks if the current subject has permissions grating him the
     * privilege to read the one requested {@link Resource} and removes it if
     * he is not allowed to access.
     *
     * @param resource The requested {@link Resource}, found in the database
     * @return A list of at most one {@link Resource} the subject is allowed to
     * access
     */
    private Resource permissionFilter(Resource resource) {
        return permissionFilter(Arrays.asList(resource)).get(0);
    }

    /**
     * Retrieve a {@code Resource} by its {@code path}.
     *
     * @param pathName  The {@code path} to the {@code Resource}.
     *
     * @return  The {@code Resource} identified by the given {@code path}, if there is
     *          such a {@code Resource}, {@code null} if not.
     */
    public Resource findByPathName(final String pathName) {
        final TypedQuery<Resource> query = entityManager.createNamedQuery(
                "DocRepo.findResourceByPath", Resource.class);
        query.setParameter("pathName", pathName);

        return permissionFilter(query.getSingleResult());
    }

    /**
     * Retrieve the {@code Resource}s, a given {@link User} created.
     *
     * @param creator   The {@link User}, who created the {@code Resource}s.
     *
     * @return  The {@code Resource}s, created by the given {@link User}, if there
     *          are such {@code Resource}s, {@code EmptyList} if not.
     */
    public List<Resource> findForCreator(final User creator) {
        final TypedQuery<Resource> query = entityManager.createNamedQuery(
                "DocRepo.findCreatedResourcesFromUser", Resource.class);
        query.setParameter("user", creator);

        return permissionFilter(query.getResultList());
    }

    /**
     * Retrieve the {@code Resource}s, a given {@link User} last modified.
     *
     * @param modifier  The {@link User}, who last modified the {@code Resource}s.
     *
     * @return  The {@code Resource}s, last modified by the given {@link User}, if
     *          there are such {@code Resource}s, {@code EmptyList} if not.
     */
    public List<Resource> findForModifier(final User modifier) {
        final TypedQuery<Resource> query = entityManager.createNamedQuery(
                "DocRepo.findModifiedResourcesFromUser", Resource.class);
        query.setParameter("user", modifier);

        return permissionFilter(query.getResultList());
    }

    /**
     * Retrieve all {@link Resource}s with the given name.
     *
     * @param name The name for the searched {@link Resource}
     * @return  The {@link Resource}s with the given name, if there aren't
     *          any an {@code EmptyList}
     */
    public List<Resource> findByName(final String name) {
        final TypedQuery<Resource> query = entityManager.createNamedQuery(
                "DocRepo.findResourcesByName", Resource.class);
        query.setParameter("name", name);

        return permissionFilter(query.getResultList());
    }
}
