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

import org.libreccm.cdi.utils.CdiUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class KernelConfig {

    private static final String EMAIL = "email";
    private static final String SCREEN_NAME = "screen_name";

    @Setting
    private boolean debugEnabled = false;

    @Setting
    private boolean webdevSupportEnabled = false;

    @Setting
    private boolean dataPermissionCheckEnabled = true;

    @Setting
    private String primaryUserIdentifier = EMAIL;

    @Setting
    private boolean ssoEnabled = false;

    @Setting
    private boolean rememberLoginEnabled = true;

    @Setting
    private boolean secureLoginEnabled = false;
    
    @Setting
    private String systemEmailAddress = "libreccm@example.org"; 

    @Setting
    private Set<String> supportedLanguages = new HashSet<>(
        Arrays.asList(new String[]{"en", "de"}));

    @Setting
    private String defaultLanguage = "en";

    public static KernelConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(KernelConfig.class);
    }

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
        if (SCREEN_NAME.equals(primaryUserIdentifier)
                || EMAIL.equals(primaryUserIdentifier)) {
            this.primaryUserIdentifier = primaryUserIdentifier;
        } else {
            throw new IllegalArgumentException(
                "Primary user identifier can only be \"screen_name\" or "
                    + "\"email\"");
        }
    }

    public boolean emailIsPrimaryIdentifier() {
        return EMAIL.equals(primaryUserIdentifier);
    }

    public boolean screenNameIsPrimaryIdentifier() {
        return SCREEN_NAME.equals(primaryUserIdentifier);
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

    public boolean hasLanguage(final String language) {
        return supportedLanguages.contains(language);
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

    public Locale getDefaultLocale() {
        return new Locale(getDefaultLanguage());
    }
    
    public String getSystemEmailAddress() {
        return systemEmailAddress;
    }

    public void setSystemEmailAddress(final String systemEmailAddress) {
        this.systemEmailAddress = systemEmailAddress;
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
        hash = 61 * hash + Objects.hashCode(systemEmailAddress);
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

        if (!Objects.equals(defaultLanguage, other.getDefaultLanguage())) {
            return false;
        }
        
        return Objects.equals(systemEmailAddress, 
                              other.getSystemEmailAddress());
    }

    @Override
    public String toString() {
        final String languages;
        if (supportedLanguages == null) {
            languages = "";
        } else {
            languages = supportedLanguages.stream().collect(Collectors.joining(
                ", "));
        }

        return String.format(
            "%s{ "
                + "debugEnabled = %b, "
                + "webdevSupportEnabled = %b, "
                + "dataPermissionCheckEnabled = %b, "
                + "primaryUserIdentifier = \"%s\", "
                + "ssoEnabled = %b, "
                + "rememberLoginEnabeled = %b, "
                + "secureLoginEnabled = %b, "
                + "supportedLanguages = { \"%s\" }, "
                + "defaultLanguage = \"%s\", "
                + "systemEmailAddress = \"%s\""
                + " }",
            super.toString(),
            debugEnabled,
            webdevSupportEnabled,
            dataPermissionCheckEnabled,
            primaryUserIdentifier,
            ssoEnabled,
            rememberLoginEnabled,
            secureLoginEnabled,
            //supportedLanguages == null ? "" : supportedLanguages.stream().collect(Collectors.joining(", ")),
            languages,
            defaultLanguage,
            systemEmailAddress);
    }

}
