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
package org.libreccm.core;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;

/**
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
    public boolean isNew(final User entity) {
        return entity.getSubjectId() == 0;
    }

    public User findByScreenName(final String screenname) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "findUserByScreenName", User.class);
        query.setParameter("screenname", screenname);

        final List<User> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            throw new MultipleMatchingUserException(String.format(
                "Found multipe users identified by screen name '%s'. "
                    + "Check your database.",
                screenname));
        }
    }

    public User findByEmailAddress(final String emailAddress) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "findUserByEmailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);
        
        final List<User> result = query.getResultList();
        
        if (result.isEmpty()) {
            return null;
        } else if(result.size() == 1) {
            return result.get(0);
        } else {
            throw new MultipleMatchingUserException(String.format(
                "Found multipe users identified by email address '%s'. "
                    + "Check your database.",
                emailAddress));
        }
    }

    private class MultipleMatchingUserException extends RuntimeException {

        private static final long serialVersionUID = 100237510055701060L;

        public MultipleMatchingUserException(final String message) {
            super(message);
        }

    }

}
