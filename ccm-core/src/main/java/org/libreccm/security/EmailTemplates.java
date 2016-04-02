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

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.LocalizedStringSetting;
import org.libreccm.configuration.Setting;
import org.libreccm.l10n.LocalizedString;

import java.util.Locale;
import java.util.Objects;

/**
 * Provides several templates for emails send by CCM.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class EmailTemplates {

    @Setting
    private LocalizedStringSetting emailVerificationSubject;

    @Setting
    private LocalizedStringSetting emailVerificationMail;

    @Setting
    private LocalizedStringSetting passwordRecoverSubject;

    @Setting
    private LocalizedStringSetting passwordRecoverMail;

    @Setting
    private LocalizedStringSetting accountActivationSubject;

    @Setting
    private LocalizedStringSetting accountActivationMail;

    public EmailTemplates() {
        emailVerificationSubject = new LocalizedStringSetting();
        emailVerificationSubject.setValue(new LocalizedString());
        emailVerificationSubject.getValue().addValue(
            Locale.ENGLISH, "Email verification");
        emailVerificationSubject.getValue().addValue(
            Locale.GERMAN, "Prüfung ihrer E-Mail-Adresse");

        emailVerificationMail = new LocalizedStringSetting();
        emailVerificationMail.setValue(new LocalizedString());
        emailVerificationMail.getValue().addValue(
            Locale.ENGLISH,
            "Please follow the following link to finish the email verfication "
                + "process:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Please be aware that your verification token expires"
                + "at ${expires_date}.");
        emailVerificationMail.getValue().addValue(
            Locale.GERMAN,
            "Bitte folgen Sie dem folgenden Link, um die Überprüfung ihrer E-"
                + "Mail-Adresse abzuschließen:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Bitte beachten Sie, dass Sie den Prozess bis zu folgendem "
                + "Zeitpunkt abschließen müssen: ${expires_date}");

        passwordRecoverSubject = new LocalizedStringSetting();
        passwordRecoverSubject.setValue(new LocalizedString());
        passwordRecoverSubject.getValue().addValue(
            Locale.ENGLISH, "Information regarding your password");
        passwordRecoverSubject.getValue().addValue(
            Locale.GERMAN, "Zurücksetzen ihres Passwortes");

        passwordRecoverMail = new LocalizedStringSetting();
        passwordRecoverMail.setValue(new LocalizedString());
        passwordRecoverMail.getValue().addValue(
            Locale.ENGLISH,
            "Please follow the following link to complete the password recover "
                + "process:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Please be aware that you must complete the process until "
                + "${expires_date}");
        passwordRecoverMail.getValue().addValue(
            Locale.GERMAN,
            "Bitte folgen Sie dem folgenden Link um ein neues Passwort "
                + "einzugeben:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Bitte beachten Sie, dass den den Prozess bis zu folgenden "
                + "Zeitpunkt abschließen müsssen: ${expires_date}");

        accountActivationSubject = new LocalizedStringSetting();
        accountActivationSubject.setValue(new LocalizedString());
        accountActivationSubject.getValue().addValue(
            Locale.ENGLISH, "Activate your account");
        accountActivationSubject.getValue().addValue(
            Locale.GERMAN, "Aktivieren Sie Ihr Benutzerkonto");

        accountActivationMail = new LocalizedStringSetting();
        accountActivationMail.setValue(new LocalizedString());
        accountActivationMail.getValue().addValue(
            Locale.ENGLISH,
            "Please follow the following link to enable your new account:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Please be aware that you must activate your account before "
                + "${expires_date}.");
        accountActivationMail.getValue().addValue(
            Locale.GERMAN,
            "Bitte folgen Sie den folgendem Link, um ihr Benutzerkonto zu "
                + "aktivieren:\n"
                + "\n"
                + "${link}"
                + "\n\n"
                + "Bitte beachten Sie, dass Sie ihr Benutzerkonto spätestens"
                + "bis zu folgendem Zeitpunkt aktivieren müssen: ${expires_date}");

    }

    public LocalizedStringSetting getEmailVerificationSubject() {
        return emailVerificationSubject;
    }

    public void setEmailVerificationSubject(
        final LocalizedStringSetting emailVerificationSubject) {
        this.emailVerificationSubject = emailVerificationSubject;
    }

    public LocalizedStringSetting getEmailVerificationMail() {
        return emailVerificationMail;
    }

    public void setEmailVerificationMail(
        final LocalizedStringSetting emailVerificationMail) {
        this.emailVerificationMail = emailVerificationMail;
    }

    public LocalizedStringSetting getPasswordRecoverSubject() {
        return passwordRecoverSubject;
    }

    public void setPasswordRecoverSubject(
        LocalizedStringSetting passwordRecoverSubject) {
        this.passwordRecoverSubject = passwordRecoverSubject;
    }

    public LocalizedStringSetting getPasswordRecoverMail() {
        return passwordRecoverMail;
    }

    public void setPasswordRecoverMail(
        LocalizedStringSetting passwordRecoverMail) {
        this.passwordRecoverMail = passwordRecoverMail;
    }

    public LocalizedStringSetting getAccountActivationSubject() {
        return accountActivationSubject;
    }

    public void setAccountActivationSubject(
        final LocalizedStringSetting accountActivationSubject) {
        this.accountActivationSubject = accountActivationSubject;
    }

    public LocalizedStringSetting getAccountActivationMail() {
        return accountActivationMail;
    }

    public void setAccountActivationMail(
        LocalizedStringSetting accountActivationMail) {
        this.accountActivationMail = accountActivationMail;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(emailVerificationSubject);
        hash = 53 * hash + Objects.hashCode(emailVerificationMail);
        hash = 53 * hash + Objects.hashCode(passwordRecoverSubject);
        hash = 53 * hash + Objects.hashCode(passwordRecoverMail);
        hash = 53 * hash * Objects.hashCode(accountActivationSubject);
        hash = 53 * hash + Objects.hashCode(accountActivationMail);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EmailTemplates)) {
            return false;
        }
        final EmailTemplates other = (EmailTemplates) obj;
        if (!Objects.equals(emailVerificationSubject,
                            other.getEmailVerificationSubject())) {
            return false;
        }
        if (!Objects.equals(emailVerificationMail,
                            other.getEmailVerificationMail())) {
            return false;
        }
        if (!Objects.equals(passwordRecoverSubject,
                            other.getPasswordRecoverSubject())) {
            return false;
        }
        if (!Objects.equals(passwordRecoverMail,
                            other.getPasswordRecoverMail())) {
            return false;
        }
        if (!Objects.equals(accountActivationMail,
                            other.getAccountActivationMail())) {
            return false;
        }
        return Objects.equals(accountActivationSubject, other
                              .getAccountActivationSubject());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "emailVerificationSubject = { %s }, "
                                 + "emailVerificationMail = { %s }, "
                                 + "passwordRecoverSubject = { %s }"
                                 + "passwordRecoverMail = { %s }, "
                                 + "accountActivationSubject = { %s }"
                                 + "accountActivationMail = { %s }"
                                 + " }",
                             super.toString(),
                             Objects.toString(emailVerificationSubject),
                             Objects.toString(emailVerificationMail),
                             Objects.toString(passwordRecoverSubject),
                             Objects.toString(passwordRecoverMail),
                             Objects.toString(accountActivationSubject),
                             Objects.toString(accountActivationMail));
    }

}
