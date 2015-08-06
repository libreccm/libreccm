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
import javax.inject.Inject;
import javax.persistence.TypedQuery;

/**
 * Provides methods for retrieving, storing and deleting {@link User} objects.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class UserRepository extends AbstractEntityRepository<Long, User> {

    @Inject
    private transient PrivilegeRepository privilegeRepository;

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public boolean isNew(final User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Can't save null.");
        }
        return entity.getSubjectId() == 0;
    }

    /**
     * Retrieves the internal system user which is permitted to execute almost
     * every operation.
     *
     * @return The internal system user.
     */
    public User retrieveSystemUser() {
        final User systemUser = new User();
        systemUser.setScreenName("system");

        final Privilege adminPrivilege = privilegeRepository.retrievePrivilege(
            "admin");
        final Permission systemPermission = new Permission();
        systemPermission.setGrantee(systemUser);
        systemPermission.setGrantedPrivilege(adminPrivilege);
        systemUser.addGrantedPermission(systemPermission);

        return systemUser;
    }

    /**
     * Retrieves the public user. The public user is used to represent the
     * privileges of a user which is not logged in. The public user is a
     * ordinary user account in the database with the screen name
     * {@code public-user}.
     *
     * @return The public user or {@code null} if there is no account for the
     *         public user.
     */
    public User retrievePublicUser() {
        return findByScreenName("public-user");
    }

    /**
     * Retrieve a user by its screen name.
     *
     * @param screenname The {@code screename} of the user.
     *
     * @return The user identified by the provided {@code screenname} if there
     *         is such a user, {@code null} if not.
     */
    public User findByScreenName(final String screenname) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "findUserByScreenName", User.class);
        query.setParameter("screenname", screenname);

        final List<User> result = query.getResultList();

        //Check if result list is empty and if not return the first element.
        //If their ist a result than there can only be one because the 
        //screen_name column has a unique constraint.
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * Finds a user by one of the email addresses assigned to the user.
     *
     * @param emailAddress The email address of the user.
     *
     * @return The user identified by the provided email address if there is
     *         such a user, {@code null} otherwise.
     *
     * @throws MultipleMatchingUserException Because the email addresses are
     *                                       represented by an embedded entity
     *                                       (see {@link User} and
     *                                       {@link EmailAddress}) it is not
     *                                       possible to enforce uniqueness on
     *                                       the database level. Therefore this
     *                                       method deals with the case that
     *                                       there is more than on matching user
     *                                       and throws an (unchecked) exception
     *                                       if this is the case. However if
     *                                       this the case something very
     *                                       strange has happened and the
     *                                       database should be checked
     *                                       carefully.
     */
    public User findByEmailAddress(final String emailAddress) {
        final TypedQuery<User> query = getEntityManager().createNamedQuery(
            "findUserByEmailAddress", User.class);
        query.setParameter("emailAddress", emailAddress);

        final List<User> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return result.get(0);
        } else {
            throw new MultipleMatchingUserException(String.format(
                "Found multipe users identified by email address '%s'. "
                    + "Check your database.",
                emailAddress));
        }
    }

}
