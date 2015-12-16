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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for retrieving, storing and deleting {@code Repository}s.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 25/11/2015
 */
@RequestScoped
public class RepositoryRepository extends
        AbstractAuditedEntityRepository<Long, Repository> {

    @Inject
    private EntityManager entityManager;

    @Override
    public Long getEntityId(Repository entity) {
        return entity.getObjectId();
    }

    @Override
    public Class<Repository> getEntityClass() {
        return Repository.class;
    }

    @Override
    public boolean isNew(Repository entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save can't be null.");
        }
        return entity.getObjectId() == 0;
    }

    /**
     * Checks if the current subject has permissions grating him the
     * privilege to read the requested {@link Repository}(s) and removes the
     * ones he is not allowed to access.
     *
     * @param repositories The requested {@link Resource}s, found in the database
     * @return A list of {@link Resource}s the subject is allowed to access
     */
    private List<Repository> permissionFilter(List<Repository> repositories) {
        final CdiUtil cdiUtil = new CdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
        return repositories.stream().filter(repository -> permissionChecker
                .isPermitted("read", repository)).collect(Collectors.toList());
    }

    /**
     * Retrieve all {@link Repository}s a given {@link User} ownes.
     *
     * @param owner The owner of the {@link Repository}s
     *
     * @return The {@link Repository}s owned by the given {@link User}
     */
    public List<Repository> findForOwner(User owner) {
        final TypedQuery<Repository> query = entityManager.createNamedQuery(
                "DocRepo.findRepositoriesForOwner", Repository.class);
        query.setParameter("owner", owner);

        return permissionFilter(query.getResultList());
    }

}
