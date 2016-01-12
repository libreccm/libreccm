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
package com.arsdigita.kernel;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

import java.util.StringJoiner;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(descBundle = "com.arsdigita.kernel.KernelConfigDescription",
               descKey = "kernel.config.description")
public final class KernelConfig {

    @Setting(descKey = "kernel.config.debug_enabled")
    private boolean debugEnabled = false;

    @Setting(descKey = "kernel.config.webdev_support_enabled")
    private boolean webdevSupportEnabled = false;

    @Setting(descKey = "kernel.config.data_permission_check_enabled")
    private boolean dataPermissionCheckEnabled = true;

    @Setting(descKey = "kernel.config.primary_user_identifier")
    private String primaryUserIdentifier = "email";

    @Setting(descKey = "kernel.config.sso_enabled")
    private boolean ssoEnabled = false;

    @Setting(descKey = "kernel.config.remember_login_enabled")
    private boolean rememberLoginEnabled = true;

    @Setting(descKey = "kernel_config.secure_login_enabled")
    private boolean secureLoginEnabled = false;

    @Setting(descKey = "kernel.config.supported_languages")
    private Set<String> supportedLanguages = new HashSet<>(
        Arrays.asList(new String[]{"en"}));

    @Setting(descKey = "kernel.config.default_language")
    private String defaultLanguage = "en";

    public KernelConfig() {
        super();
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isWebdevSupportEnabled() {
        return webdevSupportEnabled;
    }

    public void setWebdevSupportEnabled(final boolean webdevSupportEnabled) {
        this.webdevSupportEnabled = webdevSupportEnabled;
    }

    public boolean isDataPermissionCheckEnabled() {
        return dataPermissionCheckEnabled;
    }

    public void setDataPermissionCheckEnabled(
        final boolean dataPermissionCheckEnabled) {
        this.dataPermissionCheckEnabled = dataPermissionCheckEnabled;
    }

    public String getPrimaryUserIdentifier() {
        return primaryUserIdentifier;
    }

    public void setPrimaryUserIdentifier(final String primaryUserIdentifier) {
        if ("screen_name".equals(primaryUserIdentifier)
                || "email".equals(primaryUserIdentifier)) {
            this.primaryUserIdentifier = primaryUserIdentifier;
        } else {
            throw new IllegalArgumentException(
                "Primary user identifier can only be \"screen_name\" or "
                    + "\"email\"");
        }
    }

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public void setSsoEnabled(final boolean ssoEnabled) {
        this.ssoEnabled = ssoEnabled;
    }

    public boolean isRememberLoginEnabled() {
        return rememberLoginEnabled;
    }

    public void setRememberLoginEnabled(final boolean rememberLoginEnabled) {
        this.rememberLoginEnabled = rememberLoginEnabled;
    }

    public boolean isSecureLoginEnabled() {
        return secureLoginEnabled;
    }

    public void setSecureLoginEnabled(final boolean secureLoginEnabled) {
        this.secureLoginEnabled = secureLoginEnabled;
    }

    public Set<String> getSupportedLanguages() {
        if (supportedLanguages == null) {
            return null;
        } else {
            return new HashSet<>(supportedLanguages);
        }
    }

    public void setSupportedLanguages(final Set<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public void addSupportedLanguage(final String language) {
        if (language == null) {
            throw new IllegalArgumentException("Language can't be null.");
        }

        supportedLanguages.add(language);
    }

    public void removeSupportedLanguage(final String language) {
        supportedLanguages.remove(language);
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(final String defaultLanguage) {
        if (defaultLanguage == null) {
            throw new IllegalArgumentException("Default language can't be null");
        }

        if (!supportedLanguages.contains(defaultLanguage)) {
            throw new IllegalArgumentException(
                "Default language must be one of the supported languages");
        }

        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (debugEnabled ? 1 : 0);
        hash = 61 * hash + (webdevSupportEnabled ? 1 : 0);
        hash = 61 * hash + (dataPermissionCheckEnabled ? 1 : 0);
        hash = 61 * hash + Objects.hashCode(primaryUserIdentifier);
        hash = 61 * hash + (ssoEnabled ? 1 : 0);
        hash = 61 * hash + (rememberLoginEnabled ? 1 : 0);
        hash = 61 * hash + (secureLoginEnabled ? 1 : 0);
        hash = 61 * hash + Objects.hashCode(supportedLanguages);
        hash = 61 * hash + Objects.hashCode(defaultLanguage);
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
        if (!(obj instanceof KernelConfig)) {
            return false;
        }
        final KernelConfig other = (KernelConfig) obj;
        if (debugEnabled != other.isDebugEnabled()) {
            return false;
        }
        if (webdevSupportEnabled != other.isWebdevSupportEnabled()) {
            return false;
        }
        if (dataPermissionCheckEnabled != other.isDataPermissionCheckEnabled()) {
            return false;
        }
        if (ssoEnabled != other.isSsoEnabled()) {
            return false;
        }
        if (rememberLoginEnabled != other.isRememberLoginEnabled()) {
            return false;
        }
        if (secureLoginEnabled != other.isSecureLoginEnabled()) {
            return false;
        }
        if (!Objects.equals(primaryUserIdentifier,
                            other.getPrimaryUserIdentifier())) {
            return false;
        }
        if (!Objects.equals(supportedLanguages, other.getSupportedLanguages())) {
            return false;
        }

        return Objects.equals(defaultLanguage, other.getDefaultLanguage());
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(",");
        if (supportedLanguages != null) {
            supportedLanguages.forEach(s -> joiner.add(s));
        }

        return String.format("%s{ "
                                 + "debugEnabled = %b, "
                                 + "webdevSupportEnabled = %b, "
                                 + "dataPermissionCheckEnabled = %b, "
                                 + "primaryUserIdentifier = \"%s\", "
                                 + "ssoEnabled = %b, "
                                 + "rememberLoginEnabeled = %b, "
                                 + "secureLoginEnabled = %b, "
                                 + "supportedLanguages = \"%s\", "
                                 + "defaultLanguage = \"%s\""
                                 + " }",
                             super.toString(),
                             debugEnabled,
                             webdevSupportEnabled,
                             dataPermissionCheckEnabled,
                             primaryUserIdentifier,
                             ssoEnabled,
                             rememberLoginEnabled,
                             secureLoginEnabled,
                             joiner.toString(),
                             defaultLanguage);
    }

}
