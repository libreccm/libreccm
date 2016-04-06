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

import org.apache.commons.lang.text.StrSubstitutor;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.LocalizedStringSetting;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;

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
    private ServletContext servletContext;

    public String createEmailVerification(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create an email verification challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.EMAIL_VERIFICATION);
    }

    public void sendEmailVerification(final User user)
        throws MessagingException {
        final String text = createEmailVerification(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.EMAIL_VERIFICATION),
            text);
    }

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

    public String createAccountActivation(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create an user activation challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION);
    }

    public void sendAccountActivation(final User user)
        throws MessagingException {
        final String text = createEmailVerification(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.ACCOUNT_ACTIVATION),
            text);
    }

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

    public String createPasswordRecover(final User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "Can't create a password recover challenge for user null.");
        }
        return createMail(user, OneTimeAuthTokenPurpose.RECOVER_PASSWORD);
    }

    public void sendPasswordRecover(final User user)
        throws MessagingException {
        final String text = createEmailVerification(user);
        sendMessage(
            user,
            retrieveEmailSubject(OneTimeAuthTokenPurpose.RECOVER_PASSWORD),
            text);
    }

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
                path = "activate-account";
                break;
            case EMAIL_VERIFICATION:
                path = "verify-email";
                break;
            case RECOVER_PASSWORD:
                path = "recover-password";
                break;
            default:
                throw new IllegalArgumentException(String.format(
                    "Unsupported value \"%s\" for purpose.",
                    purpose.toString()));
        }
        values.put("link",
                   String.format("%s/%s/register/%s",
                                 servletContext.getVirtualServerName(),
                                 servletContext.getContextPath(),
                                 path));

        final StrSubstitutor substitutor = new StrSubstitutor(values);
        return substitutor.replace(template);
    }

    private String retrieveEmailSubject(final OneTimeAuthTokenPurpose purpose) {
        final Locale locale = globalizationHelper.getNegotiatedLocale();

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
            final Locale defaultLocale = new Locale(kernelConfig
                .getDefaultLanguage());
            return localizedString.getValue(defaultLocale);
        }
    }

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
            final Locale defaultLocale = new Locale(kernelConfig
                .getDefaultLanguage());
            return localizedString.getValue(defaultLocale);
        }
    }

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

    private void sendMessage(final User user,
                             final String subject,
                             final String text) throws MessagingException {
        final KernelConfig kernelConfig = configurationManager
            .findConfiguration(KernelConfig.class);

        final Mail mail = new Mail(user.getPrimaryEmailAddress().getAddress(),
                                   kernelConfig.getSystemEmailAddress(),
                                   "email verification");
        mail.setBody(text);
        mail.send();
    }

}
