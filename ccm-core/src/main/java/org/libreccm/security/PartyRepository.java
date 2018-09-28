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

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository class for parties.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PartyRepository extends AbstractEntityRepository<Long, Party> {

    private static final long serialVersionUID = -8056652791690243141L;

    @Override
    public Class<Party> getEntityClass() {
        return Party.class;
    }

    @Override
    public String getIdAttributeName() {
        return "partyId";
    }

    @Override
    public Long getIdOfEntity(final Party entity) {
        return entity.getPartyId();
    }

    @Override
    public boolean isNew(final Party entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null");
        }
        return entity.getPartyId() == 0;
    }

    @Override
    public void initNewEntity(final Party party) {
        party.setUuid(UUID.randomUUID().toString());
    }
    
    /**
     * Finds a {@link Party} (which can be a user or group) by its name.
     *
     * @param name The name of the item to find
     *
     * @return An optional either with the found item or empty
     */
    public Optional<Party> findByName(final String name) {
        final TypedQuery<Party> query = getEntityManager().createNamedQuery(
            "Party.findByName", Party.class);
        query.setParameter("name", name);

        final List<Party> result = query.getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(result.get(0));
        }
    }

    public List<Party> findByRole(final Role role) {
        final TypedQuery<Party> query = getEntityManager()
            .createNamedQuery("Party.findByRole", Party.class);
        query.setParameter("role", role);

        return query.getResultList();
    }

    public List<Party> searchByName(final String term) {
        final TypedQuery<Party> query = getEntityManager().createNamedQuery(
            "Party.searchByName", Party.class);
        query.setParameter("term", term);
        return query.getResultList();
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final Party party) {
        super.save(party);
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(final Party party) {
        super.delete(party);
    }

}
