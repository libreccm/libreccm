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
package org.libreccm.shortcuts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="konerman@tzi.de">Alexander Konermann</a>
 */
@RequestScoped
public class ShortcutRepository extends AbstractEntityRepository<Long, Shortcut> {

    private static final Logger LOGGER = LogManager.getLogger(
        ShortcutRepository.class);

    @Override
    public Class<Shortcut> getEntityClass() {
        return Shortcut.class;
    }

    @Override
    public boolean isNew(final Shortcut entity) {
        return entity.getShortcutId() == 0;
    }

    /**
     * Finds the first shortcut with the specified urlKey.
     *
     * @param urlKey the wanted urlKey
     *
     * @return The shortcut with the specified {@code urlKey} if there is any.
     */
    public Optional<Shortcut> findByUrlKey(final String urlKey) {
        LOGGER.debug("Trying to find Shortcut for urlKey {}",
                     cleanUrlKey(urlKey));
        final TypedQuery<Shortcut> query = getEntityManager().createNamedQuery(
            "Shortcut.findByUrlKey", Shortcut.class);
        query.setParameter("urlKey", cleanUrlKey(urlKey));

        try {
            final Shortcut result = query.getSingleResult();
            return Optional.of(result);
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    /**
     * Finds all shortcuts which redirect to the provided target.
     *
     * @param redirect the wanted redirect
     *
     * @return a List of Shortcuts with the specified {@code redirect}
     */
    public List<Shortcut> findByRedirect(final String redirect) {
        final TypedQuery<Shortcut> query = getEntityManager().createNamedQuery(
            "Shortcut.findByRedirect", Shortcut.class);
        query.setParameter("redirect", redirect);

        return query.getResultList();
    }

    @Override
    @AuthorizationRequired
    @RequiresPrivilege(ShortcutsConstants.SHORTSCUT_MANAGE_PRIVILEGE)
    public void save(final Shortcut shortcut) {
        //Cleanup the URL key
        shortcut.setUrlKey(cleanUrlKey(shortcut.getUrlKey()));

        super.save(shortcut);
    }

    @Override
    @AuthorizationRequired
    @RequiresPrivilege(ShortcutsConstants.SHORTSCUT_MANAGE_PRIVILEGE)
    public void delete(final Shortcut shortcut) {
        super.delete(shortcut);
    }

    private String cleanUrlKey(final String urlKey) {
        final StringBuffer result = new StringBuffer(urlKey.length());

        if (!urlKey.startsWith("/")) {
            result.append('/');
        }

        result.append(urlKey);

        if (!urlKey.endsWith("/")) {
            result.append('/');
        }

        return result.toString();
    }

}
