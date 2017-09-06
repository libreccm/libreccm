/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import com.arsdigita.ui.login.UserNewForm;
import org.apache.logging.log4j.util.Strings;
import org.libreccm.core.CoreConstants;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.Optional;


/**
 * The CDI bean encapsulates all steps for registering a user, for example by a
 * form provided to the user (like the {@link UserNewForm} of the login
 * application).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class RegistrationManager {

    public static final String REGISTERED_USERS = "registered-users";

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserManager userManager;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private GroupManager groupManager;

    @Inject
    private ChallengeManager challengeManager;

    /**
     * Register a new user.
     *
     * The method checks if there is already a{@code user} with the same
     * {@code user} name and/or email address than the provided. In that case an
     * {@link IllegalArgumentException} is thrown.
     *
     * If there is no {@code user} with the same username and/or email address
     * than the provided {@code user} the new {@code user} is created and added
     * to the group {@code registered-users}. If the group does exists the group
     * is created.
     *
     * Finally the method creates a {@code user} activation challenge and sends
     * it to the user.
     *
     *
     * @param userName     The user name of the new user.
     * @param familyName   The family name of the new user.
     * @param givenName    The given name of the new user.
     * @param emailAddress The email address of the new user.
     * @param password     The password of the new user.
     *
     * @throws MessagingException       If there is problem sending the
     *                                  activation challenge to the new user.
     * @throws IllegalArgumentException If the provided {@code user} is
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void registerUser(final String userName,
                             final String familyName,
                             final String givenName,
                             final String emailAddress,
                             final String password) throws MessagingException {

        if (Strings.isBlank(userName)) {
            throw new IllegalArgumentException(
                "The provided user name is blank.");
        }
        if (Strings.isBlank(familyName)) {
            throw new IllegalArgumentException("The family name is blank.");
        }
        if (Strings.isBlank(givenName)) {
            throw new IllegalArgumentException("The given name is blank.");
        }
        if (Strings.isBlank(emailAddress)) {
            throw new IllegalArgumentException("The email address is blank.");
        }
        if (Strings.isBlank(password)) {
            throw new IllegalArgumentException("The password is blank.");
        }

        if (checkIfUserNameExists(userName)) {
            throw new IllegalArgumentException(String.format(
                "There is already an user with the username \"%s\".",
                userName));
        }

        if (checkIfEmailIsInUse(emailAddress)) {
            throw new IllegalArgumentException(String.format(
                "The email address \"%s\" is already registered.",
                emailAddress));
        }

        final User user = userManager.createUser(givenName,
                                                 familyName,
                                                 userName,
                                                 emailAddress,
                                                 password);
        user.setBanned(true);
        userRepository.save(user);

        final Optional<Group> registeredUsers = groupRepository.findByName(
            REGISTERED_USERS);
        final Group group;
        if (registeredUsers.isPresent()) {
            group = registeredUsers.get();
        } else {
            final Group newGroup = new Group();
            newGroup.setName("registered-users");
            groupRepository.save(newGroup);
            group = newGroup;
        }

        groupManager.addMemberToGroup(user, group);

        challengeManager.sendAccountActivation(user);

    }

    private boolean checkIfUserNameExists(final String userName) {
        return userRepository.findByName(userName).isPresent();
    }

    private boolean checkIfEmailIsInUse(final String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress).isPresent();
    }

}
