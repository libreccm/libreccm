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
package org.libreccm.web;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ApplicationRepository
    extends AbstractEntityRepository<Long, CcmApplication> {

    private static final long serialVersionUID = 165550885824851765L;

    @Override
    public Class<CcmApplication> getEntityClass() {
        return CcmApplication.class;
    }

    @Override
    public String getIdAttributeName() {
        return "objectId";
    }

    @Override
    public boolean isNew(final CcmApplication application) {
        return application.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final CcmApplication application) {

        super.initNewEntity(application);
        application.setUuid(UUID.randomUUID().toString());
        application.setApplicationType(application.getClass().getName());
    }

    /**
     * Retrieve the application mounted at the provided {@code path}.
     *
     * @param path The path on which the application is mounted.
     *
     * @return The application mounted at {@code path} or {@code null} if there
     *         is no application mounted at that {@code path}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<CcmApplication> retrieveApplicationForPath(final String path) {
        final TypedQuery<CcmApplication> query = getEntityManager()
            .createNamedQuery("CcmApplication.retrieveApplicationForPath",
                              CcmApplication.class);
        query.setParameter("path", path);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Find all applications of the specific {@code type}.
     *
     * @param type The type of the application.
     *
     * @return A list of the installed applications of the provided
     *         {@code type}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<CcmApplication> findByType(final String type) {
        final TypedQuery<CcmApplication> query = getEntityManager()
            .createNamedQuery("CcmApplication.findByType",
                              CcmApplication.class);
        query.setParameter("type", type);

        return query.getResultList();
    }

    /**
     * Finds a {@link CcmApplication} by its uuid.
     *
     * @param uuid The uuid of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<CcmApplication> findByUuid(final String uuid) {
        final TypedQuery<CcmApplication> query = getEntityManager()
            .createNamedQuery("CcmApplication.findByUuid",
                              CcmApplication.class);
        query.setParameter("uuid", uuid);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final CcmApplication application) {
        super.save(application);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final CcmApplication application) {
        super.delete(application);
    }

}
