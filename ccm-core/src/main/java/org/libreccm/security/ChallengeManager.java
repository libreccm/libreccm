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

import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.mail.Mail;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.LocalizedStringSetting;
import org.libreccm.core.CoreConstants;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.arsdigita.ui.login.LoginServlet.*;

/**
 * A service class for managing several so called challenges. These challenges
 * are using a {@link OneTimeAuthToken} and are used to verify email addresses
 * and recover passwords.
 *
 * For each challenge type there are three methods:
 *
 * <ul>
 * <li>a {@code create} method returning a string with the text to be send to
 * the user</li>
 * <li>a {@code send} method which creates a challenge using the create method
 * and sends it to the user per email</li>
 * <li>a {@code finish} method which accepts a {@link OneTimeAuthToken} and
 * executes the final action of the challenge</li>
 * </ul>
 *
 * The {@code create} method are {@code public} to provide maximum flexibility
 * for the users of the class.
 *
 * The texts used by this class can be customised using the
 * {@link EmailTemplates} configuration. Each template supports two
 * placeholders:
 *
 * <dl>
 * <dt><code>link</code></dt>
 * <dd>The link to the page for submitting the {@link OneTimeAuthToken}</dd>
 * <dt><code>expires_date</code></dt>
 * <dd>The time on which the {@link OneTimeAuthToken} expires.</dd>
 * </dl>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ChallengeManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ChallengeManager.class);

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ConfigurationManager configurationManager;

    @Inject
    private OneTimeAuthManager oneTimeAuthManager;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserManager userManager;

    @Inject
    private HttpServletRequest request;

    /**
     * Creates a email verification challenge.
     *
     * @param user The user for which the challenge is created.
     *
     * @return The text of the challenge mail.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public String createEmailVerification(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create an email verification challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
    }

    /**
     * Creates a email verification challenge and sends it to the user per email
     * using the users primary email address.
     *
     * @param user The user to which the challenge is send.
     *
     * @throws MessagingException If there is a problem sending the email to the
     *                            user.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void sendEmailVerification(final User user)
        throws MessagingException {
        final String text = createEmailVerification(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
            text);
    }

    /**
     * Finishes a email verification challenge. Checks if the submitted token
     * matches the token stored in the database and removes the challenge from
     * the database.
     *
     * @param user           The user which submitted the request.
     * @param submittedToken The token submitted by the user.
     *
     * @throws ChallengeFailedException If the provided token does not match the
     *                                  stored token.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void finishEmailVerification(final User user,
                                        final String submittedToken)
        throws ChallengeFailedException {

        if (finishChallenge(user,
                            submittedToken,
                            OneTimeAuthTokenPurpose.EMAIL_VERIFICATION)) {

            user.getPrimaryEmailAddress().setVerified(true);
            userRepository.save(user);

        } else {
            //No matching token
            throw new ChallengeFailedException(
                "Submitted token does not match any active email verification "
                    + "challenges.");
        }
    }

    /**
     * Creates an account activation challenge. This is used for example when a
     * new users is registered using the login application.
     *
     * @param user The user for which the challenge is created.
     *
     * @return The challenge message.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public String createAccountActivation(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create an user activation challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION);
    }

    /**
     * Creates a account activation challenge and sends it to the user by email.
     *
     * @param user The user to which the challenge is send.
     *
     * @throws MessagingException If something goes wrong when sending the
     *                            message.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void sendAccountActivation(final User user)
        throws MessagingException {
        final String text = createAccountActivation(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION),
            text);
    }

    /**
     * Finishes an account activation challenge. If the submitted token matches
     * the stored token the {@code banned} status for the user is set to
     * {@link false}.
     *
     * @param user           The user which submitted the request.
     * @param submittedToken The submitted token.
     *
     * @throws ChallengeFailedException If the submitted token does not match
     *                                  the stored token.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void finishAccountActivation(final User user,
                                        final String submittedToken)
        throws ChallengeFailedException {

        if (finishChallenge(user,
                            submittedToken,
                            OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION)) {

            user.setBanned(false);
            userRepository.save(user);
        } else {
            //Not matching token
            throw new ChallengeFailedException(
                "Submitted token does not match any active account activation "
                    + "challenges.");
        }
    }

    /**
     * Creates a password recover challenge for a user.
     *
     * @param user The user for which the password recover challenge is created.
     *
     * @return The challenge message.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public String createPasswordRecover(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create a password recover challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.RECOVER_PASSWORD);
    }

    /**
     * Creates a password recover challenge for the provided author and sends it
     * the user via email.
     *
     * @param user The user for which the challenge is created.
     *
     * @throws MessagingException If something goes wrong when sending the
     *                            message.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void sendPasswordRecover(final User user)
        throws MessagingException {
        final String text = createPasswordRecover(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.RECOVER_PASSWORD),
            text);
    }

    /**
     * Finishes a password recover challenge. If the submitted token matches to
     * stored token the password of the user is set to the provided new
     * password.
     *
     * @param user           The user which submitted the request.
     * @param submittedToken The submitted token.
     * @param newPassword    The new password.
     *
     * @throws ChallengeFailedException If the submitted token does not match
     *                                  the stored token.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_SYSTEM)
    public void finishPasswordRecover(final User user,
                                      final String submittedToken,
                                      final String newPassword)
        throws ChallengeFailedException {

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password can't be empty");
        }

        if (finishChallenge(user,
                            submittedToken,
                            OneTimeAuthTokenPurpose.RECOVER_PASSWORD)) {
            userManager.updatePassword(user, newPassword);
        } else {
            //Not matching token
            throw new ChallengeFailedException(
                "Submitted token does not match any active password recover "
                    + "challenges.");
        }
    }

    /**
     * A helper method for creating the emails send the the {@code send*}
     * methods.
     *
     * @param user    The user to which the mail is send.
     * @param purpose The purpose of the challenge.
     *
     * @return The text of the mail.
     */
    private String createMail(final User user,
                              final OneTimeAuthTokenPurpose purpose) {
        final OneTimeAuthToken token = oneTimeAuthManager.createForUser(
            user, purpose);

        final String template = retrieveEmailTemplate(purpose);
        final Map<String, String> values = new HashMap<>();
        values.put("expires_date", token.getValidUntil().toString());
        final String path;
        switch (purpose) {
            case ACCOUNT_ACTIVATION:
                path = ACTIVATE_ACCOUNT_PATH_INFO;
                break;
            case EMAIL_VERIFICATION:
                path = VERIFY_EMAIL_PATH_INFO;
                break;
            case RECOVER_PASSWORD:
                path = RESET_USER_PASSWORD_PATH_INFO;
                break;
            default:
                throw new IllegalArgumentException(String.format(
                    "Unsupported value \"%s\" for purpose.",
                    purpose.toString()));
        }
        values.put("link",
                   URL.there(request,
                             LoginConstants.LOGIN_PATH + path, null)
                   .getURL());

        final ParameterMap params = new ParameterMap();
        params.setParameter("email", user.getPrimaryEmailAddress().getAddress());
        params.setParameter("token", token.getToken());
        values.put("full_link",
                   URL.there(request,
                             LoginConstants.LOGIN_PATH + path, params)
                   .getURL());

        values.put("token", token.getToken());

        final StrSubstitutor substitutor = new StrSubstitutor(values);
        return substitutor.replace(template);
    }

    /**
     * Helper method for retrieving the email subject from the
     * {@link EmailTemplates} configuration.
     *
     * @param purpose The purpose of the challenge.
     *
     * @return The subject for the challenge mail for the provided purpose.
     */
    private String retrieveEmailSubject(final OneTimeAuthTokenPurpose purpose) {
        LOGGER.debug("Retreving email subject...");
        final Locale locale = globalizationHelper.getNegotiatedLocale();
        LOGGER.debug("Negoiated locale is {}.", locale.toString());

        final EmailTemplates emailTemplates = configurationManager
            .findConfiguration(EmailTemplates.class);
        final LocalizedStringSetting setting;
        switch (purpose) {
            case ACCOUNT_ACTIVATION:
                setting = emailTemplates.getAccountActivationSubject();
                break;
            case EMAIL_VERIFICATION:
                setting = emailTemplates.getEmailVerificationSubject();
                break;
            case RECOVER_PASSWORD:
                setting = emailTemplates.getPasswordRecoverSubject();
                break;
            default:
                throw new IllegalArgumentException(String.format(
                    "Unsupported value \"%s\" for purpose.",
                    purpose.toString()));
        }

        final LocalizedString localizedString = setting.getValue();
        if (localizedString.hasValue(locale)) {
            return localizedString.getValue(locale);
        } else {
            final KernelConfig kernelConfig = configurationManager
                .findConfiguration(KernelConfig.class);
            final Locale defaultLocale =kernelConfig.getDefaultLocale();
            return localizedString.getValue(defaultLocale);
        }
    }

    /**
     * Helper method for retrieving the email template.
     *
     * @param purpose The purpose of the challenge.
     *
     * @return The template for the challenge message for the provided purpose.
     */
    private String retrieveEmailTemplate(
        final OneTimeAuthTokenPurpose purpose) {

        final Locale locale = globalizationHelper.getNegotiatedLocale();

        final EmailTemplates emailTemplates = configurationManager
            .findConfiguration(EmailTemplates.class);
        final LocalizedStringSetting setting;
        switch (purpose) {
            case ACCOUNT_ACTIVATION:
                setting = emailTemplates.getAccountActivationMail();
                break;
            case EMAIL_VERIFICATION:
                setting = emailTemplates.getEmailVerificationMail();
                break;
            case RECOVER_PASSWORD:
                setting = emailTemplates.getPasswordRecoverMail();
                break;
            default:
                throw new IllegalArgumentException(String.format(
                    "Unsupported value \"%s\" for purpose.",
                    purpose.toString()));
        }

        final LocalizedString localizedString = setting.getValue();
        if (localizedString.hasValue(locale)) {
            return localizedString.getValue(locale);
        } else {
            final KernelConfig kernelConfig = configurationManager
                .findConfiguration(KernelConfig.class);
            final Locale defaultLocale = kernelConfig.getDefaultLocale();
            return localizedString.getValue(defaultLocale);
        }
    }

    /**
     * Helper method for validating a submitted token and deleting the
     * {@link OneTimeAuthToken} for the challenge.
     *
     * @param user           The user which submitted the challenge.
     * @param submittedToken The token submitted by the user.
     * @param purpose        The purpose of the challenge.
     *
     * @return {@code true} If the provided token matches the stored token,
     *         {@code false} if not.
     *
     * @throws ChallengeFailedException
     */
    private boolean finishChallenge(final User user,
                                    final String submittedToken,
                                    final OneTimeAuthTokenPurpose purpose)
        throws ChallengeFailedException {

        if (user == null || submittedToken == null) {
            throw new IllegalArgumentException(
                "User and/or submitted token can't be null.");
        }

        final List<OneTimeAuthToken> tokens = oneTimeAuthManager
            .retrieveForUser(user, purpose);
        if (tokens == null || tokens.isEmpty()) {
            throw new ChallengeFailedException(String.format(
                "No active %s challenge for user \"%s\".",
                purpose.toString(),
                Objects.toString(user)));
        }

        for (OneTimeAuthToken token : tokens) {
            if (oneTimeAuthManager.isValid(token)) {
                //A token which still valid is not deleted, therefore we can't
                //combine the two conditions.
                if (oneTimeAuthManager.verify(token, submittedToken)) {
                    oneTimeAuthManager.invalidate(token);
                    return true;
                }
            } else {
                oneTimeAuthManager.invalidate(token);
            }
        }

        return false;
    }

    /**
     * Helper method for sending emails.
     * 
     * @param user The user to which the mail is send.
     * @param subject The subject of the mail.
     * @param text The text (body) of the mail.
     * 
     * @throws MessagingException If something goes wrong when sending the mail.
     */
    private void sendMessage(final User user,
                             final String subject,
                             final String text) throws MessagingException {
        final KernelConfig kernelConfig = configurationManager
            .findConfiguration(KernelConfig.class);

        final Mail mail = new Mail(user.getPrimaryEmailAddress().getAddress(),
                                   kernelConfig.getSystemEmailAddress(),
                                   subject);
        mail.setBody(text);
        mail.send();
    }

}
