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

import org.libreccm.core.AbstractEntityRepository;

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
     * @return The shortcut with the specified urlKey if there is any.
     */
    public Optional<Shortcut> findByUrlKey(final String urlKey) {
        final TypedQuery<Shortcut> query = getEntityManager().createNamedQuery(
            "Shortcut.findByUrlKey", Shortcut.class);
        query.setParameter("urlKey", urlKey);

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
     * @return List<Shortcut> a List of Shortcuts with the specified redirect
     */
    public List<Shortcut> findByRedirect(final String redirect) {
        final TypedQuery<Shortcut> query = getEntityManager().createNamedQuery(
            "Shortcut.findByRedirect", Shortcut.class);
        query.setParameter("redirect", redirect);
        
        return query.getResultList();
    }

}
