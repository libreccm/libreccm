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
package com.arsdigita.kernel.security;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(
    descBundle = "com.arsdigita.kernel.security.SecurityConfigDescription",
    descKey = "security.config.description")
public final class SecurityConfig {

    @Setting(descKey = "security.confg.excluded_extensions")
    private List<String> excludedExtensions = Arrays.asList(
        new String[]{".jpg", ".gif", ".png", ".pdf"});

    @Setting(descKey = "security.config.auto_registration_enabled")
    private Boolean autoRegistrationEnabled = false;

    @Setting(descKey = "security.config.password_recovery_enabled")
    private Boolean passwordRecoveryEnabled = true;

    @Setting(descKey = "security.config.hash_algorithm")
    private String hashAlgorithm = "SHA-512";

    @Setting(descKey = "security.config.salt_length")
    private Integer saltLength = 256;

    @Setting(descKey = "security.config.hash_iterations")
    private Integer hashIterations = 50000;

    public static SecurityConfig getConfig() {
        final CdiUtil cdiUtil = new CdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        return confManager.findConfiguration(SecurityConfig.class);
    }

    public List<String> getExcludedExtensions() {
        return new ArrayList<>(excludedExtensions);
    }

    public void setExcludedExtensions(final List<String> excludedExtensions) {
        this.excludedExtensions = excludedExtensions;
    }

    public Boolean isAutoRegistrationEnabled() {
        return autoRegistrationEnabled;
    }

    public void setAutoRegistrationEnabled(
        final Boolean autoRegistrationEnabled) {
        this.autoRegistrationEnabled = autoRegistrationEnabled;
    }

    public boolean isPasswordRecoveryEnabled() {
        return passwordRecoveryEnabled;
    }

    public void setPasswordRecoveryEnabled(
        final boolean passwordRecoveryEnabled) {
        this.passwordRecoveryEnabled = passwordRecoveryEnabled;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(final String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public Integer getSaltLength() {
        return saltLength;
    }

    public void setSaltLength(Integer saltLength) {
        this.saltLength = saltLength;
    }

    public Integer getHashIterations() {
        return hashIterations;
    }

    public void setHashIterations(final Integer hashIterations) {
        this.hashIterations = hashIterations;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(excludedExtensions);
        hash = 97 * hash + Objects.hashCode(autoRegistrationEnabled);
        hash = 97 * hash + Objects.hashCode(passwordRecoveryEnabled);
        hash = 97 * hash + Objects.hashCode(hashAlgorithm);
        hash = 97 * hash + Objects.hashCode(saltLength);
        hash = 97 * hash + Objects.hashCode(hashIterations);
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
        if (!(obj instanceof SecurityConfig)) {
            return false;
        }
        final SecurityConfig other = (SecurityConfig) obj;
        if (!Objects.equals(hashAlgorithm, other.getHashAlgorithm())) {
            return false;
        }
        if (!Objects.equals(excludedExtensions, other.getExcludedExtensions())) {
            return false;
        }
        if (!Objects.equals(autoRegistrationEnabled,
                            other.isAutoRegistrationEnabled())) {
            return false;
        }
        if (!(Objects.equals(passwordRecoveryEnabled,
                             other.isPasswordRecoveryEnabled()))) {
            return false;
        }
        if (!Objects.equals(saltLength, other.getSaltLength())) {
            return false;
        }
        return Objects.equals(hashIterations, other.getHashIterations());
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(", ");
        excludedExtensions.forEach(s -> joiner.add(s));

        return String.format("%s{ "
                                 + "excludedExtensions = { %s }, "
                                 + "autoRegistrationEnabled = %b, "
                                 + "passwordRecoveryEnabled = %b, "
                                 + "hashAlgorithm = \"%s\", "
                                 + "saltLength = %d, "
                                 + "hashIterations = %d"
                                 + " }",
                             super.toString(),
                             joiner.toString(),
                             autoRegistrationEnabled,
                             passwordRecoveryEnabled,
                             hashAlgorithm,
                             saltLength,
                             hashIterations);
    }

}
