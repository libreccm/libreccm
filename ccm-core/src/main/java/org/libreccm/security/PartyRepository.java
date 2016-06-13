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
package org.libreccm.security;

import javax.enterprise.context.RequestScoped;

import org.libreccm.core.AbstractEntityRepository;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 * Repository class for parties.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PartyRepository extends AbstractEntityRepository<Long, Party> {

    @Override
    public Class<Party> getEntityClass() {
        return Party.class;
    }

    @Override
    public boolean isNew(final Party entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null");
        }
        return entity.getPartyId() == 0;
    }

    /**
     * Finds a party (which can be a user or group) by its name.
     *
     * @param name
     *
     * @return
     */
    public Party findByName(final String name) {
        final TypedQuery<Party> query = getEntityManager().createNamedQuery(
            "Party.findByName", Party.class);
        query.setParameter("name", name);

        final List<Party> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public List<Party> searchByName(final String term) {
        final TypedQuery<Party> query = getEntityManager().createNamedQuery(
            "Party.searchByName", Party.class);
        query.setParameter("term", term);
        return query.getResultList();
    }

    @AuthorizationRequired
    @RequiresPrivilege("admin")
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Party party) {
        super.save(party);
    }
    
    @AuthorizationRequired
    @RequiresPrivilege("admin")
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Party party) {
        super.delete(party);
    }
}
