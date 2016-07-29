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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ApplicationRepository
    extends AbstractEntityRepository<Long, CcmApplication> {

    @Override
    public Class<CcmApplication> getEntityClass() {
        return CcmApplication.class;
    }

    @Override
    public boolean isNew(final CcmApplication application) {
        return application.getObjectId() == 0;
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
    public CcmApplication retrieveApplicationForPath(final String path) {
        final TypedQuery<CcmApplication> query = getEntityManager()
            .createNamedQuery("CcmApplication.retrieveApplicationForPath",
                              CcmApplication.class);
        query.setParameter("path", path);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
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
    
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final CcmApplication application) {
        super.save(application);
    }
    
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.ADMIN_PRIVILEGE)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final CcmApplication application) {
        super.delete(application);
    }

}
