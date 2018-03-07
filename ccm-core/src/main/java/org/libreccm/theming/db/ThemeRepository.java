/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import org.libreccm.core.AbstractEntityRepository;

import java.util.Optional;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ThemeRepository extends AbstractEntityRepository<Long, Theme> {

    private static final long serialVersionUID = 2243313600794241908L;

    @Override
    public Class<Theme> getEntityClass() {
        return Theme.class;
    }

    @Override
    public String getIdAttributeName() {
        return "themeId";
    }

    @Override
    public Long getIdOfEntity(final Theme entity) {
        return entity.getThemeId();
    }

    @Override
    public boolean isNew(final Theme entity) {
        return entity.getThemeId() == 0;
    }

    @Override
    public void save(final Theme theme) {
        super.save(theme);;
    }
    
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Theme> findThemeByUuid(final String uuid,
                                           final ThemeVersion version) {

        final TypedQuery<Theme> query = getEntityManager()
            .createNamedQuery("Theme.findByUuid", Theme.class);
        query.setParameter("uuid", uuid);
        query.setParameter("version", version);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Theme> findThemeByName(final String name,
                                           final ThemeVersion version) {

        final TypedQuery<Theme> query = getEntityManager()
            .createNamedQuery("Theme.findByName", Theme.class);
        query.setParameter("name", name);
        query.setParameter("version", version);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
