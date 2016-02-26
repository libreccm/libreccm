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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;

/**
 * Repository for user objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class UserRepository extends AbstractEntityRepository<Long, User> {

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public boolean isNew(final User user) {
        if (user == null) {
            throw new IllegalArgumentException("Can't save null");
        }
        return user.getPartyId() == 0;
    }

    /**
     * Finds a user by its user name.
     *
     * @param name The name of the user to find.
     *
     * @return The user identified by the provided name. If there are multiple
     *         user matching the user name (should be possible) the first one is
     *         returned. If there is no matching user {@code null} is returned.
     */
    public User findByName(final String name) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "User.findByName", User.class);
        applyDefaultEntityGraph(query);
        query.setParameter("name", name);

        return getSingleResultOrNull(query);

//        final List<User> result = query.getResultList();
//        if (result.isEmpty()) {
//            return null;
//        } else {
//            return result.get(0);
//        }
    }

    /**
     * Finds a user by its name and applies the given named entity graph to the
     * query.
     *
     * @param name            The name of the user to find.
     * @param entityGraphName The named entity graph to use.
     *
     * @return The user identified by the provided name. If there are multiple
     *         user matching the user name (should be possible) the first one is
     *         returned. If there is no matching user {@code null} is returned.
     */
    public User findByName(final String name, final String entityGraphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<User> entityGraph
                                    = (EntityGraph<User>) getEntityManager()
            .getEntityGraph(entityGraphName);
        return findByName(name, entityGraph);
    }

    public User findByName(final String name,
                           final EntityGraph<User> entityGraph) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "User.findByName", User.class);
        query.setParameter("name", name);
        query.setHint(FETCH_GRAPH_HINT_KEY, entityGraph);

        return getSingleResultOrNull(query);

//        final List<User> result = query.getResultList();
//        if (result.isEmpty()) {
//            return null;
//        } else {
//            return result.get(0);
//        }
    }

    /**
     * Finds user by the primary email address.
     *
     * @param emailAddress The email address which identifies the user.
     *
     * @return The user identified by the provided email address. If there are
     *         multiple matching users only the first one is returned. If there
     *         is no matching user {@code null} is returned.
     */
    public User findByEmailAddress(final String emailAddress) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "User.findByEmailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        applyDefaultEntityGraph(query);

        return getSingleResultOrNull(query);
    }

    public User findByEmailAddress(final String emailAddress,
                                   final String entityGraphName) {
        @SuppressWarnings("unchecked")
        final EntityGraph<User> entityGraph
                                    = (EntityGraph<User>) getEntityManager()
            .getEntityGraph(entityGraphName);
        return findByEmailAddress(emailAddress, entityGraph);
    }

    public User findByEmailAddress(final String emailAddress,
                                   final EntityGraph<User> entityGraph) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "User.findByEmailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        query.setHint(FETCH_GRAPH_HINT_KEY, entityGraph);

        return getSingleResultOrNull(query);
    }

    public List<User> filtered(final String term) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "User.filterByNameAndEmail", User.class);
        query.setParameter("term", term);
        
        return query.getResultList();
    }

}
