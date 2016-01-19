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
package com.arsdigita.web;

import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Pattern;
import javax.validation.executable.ExecutableValidator;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class WebConfig {

    @Setting
    private String defaultScheme = "http";

    @Setting
    private String server;

    @Setting
    private String secureServer;

    @Setting
    private String siteName;

    @Setting
    private String host;

    @Setting
    private Set<String> secureRequiredFor = new HashSet<>();

    @Setting
    private String dispatcherServletPath = "/ccm";

    @Setting
    private String resolverClass = DefaultApplicationFileResolver.class
        .getName();

    @Setting
    private Boolean deactiveCacheHostNotifications = false;

    @Setting
    private String dynamicHostProvider;

    public static WebConfig getConfig() {
        final CdiUtil cdiUtil = new CdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        return confManager.findConfiguration(WebConfig.class);
    }

    public String getDefaultScheme() {
        return defaultScheme;
    }

    public void setDefaultScheme(final String defaultScheme) {
        this.defaultScheme = defaultScheme;
    }

    public String getServer() {
        return server;
    }

    public void setServer(
        @Pattern(regexp = "[\\w-.]*:[0-9]{1,5}") final String server) {
        final Method method;
        try {
            method = getClass().getMethod("setServer", String.class);
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Set<ConstraintViolation<WebConfig>> violations
                                                      = validateHostParameter(
                method, server);

        if (violations.isEmpty()) {
            this.server = server;
        } else {
            final StringJoiner joiner = new StringJoiner(", ");
            violations.forEach(v -> joiner.add(v.getMessage()));

            throw new IllegalArgumentException(joiner.toString());
        }
    }

    public String getSecureServer() {
        return secureServer;
    }

    public void setSecureServer(
        @Pattern(regexp = "[\\w-.]*:[0-9]{1,5}") final String secureServer) {
        final Method method;
        try {
            method = getClass().getMethod("setSecureServer", String.class);
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Set<ConstraintViolation<WebConfig>> violations
                                                      = validateHostParameter(
                method, secureServer);

        if (violations.isEmpty()) {
            this.secureServer = secureServer;
        } else {
            final StringJoiner joiner = new StringJoiner(", ");
            violations.forEach(v -> joiner.add(v.getMessage()));

            throw new IllegalArgumentException(joiner.toString());
        }
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(final String siteName) {
        this.siteName = siteName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(
        @Pattern(regexp = "[\\w-.]*:[0-9]{1,5}") final String host) {
        
        final Method method;
        try {
            method = getClass().getMethod("setHost", String.class);
        } catch (NoSuchMethodException ex) {
            throw new UncheckedWrapperException(ex);
        }

        final Set<ConstraintViolation<WebConfig>> violations
                                                      = validateHostParameter(
                method, host);

        if (violations.isEmpty()) {
            this.host = host;
        } else {
            final StringJoiner joiner = new StringJoiner(", ");
            violations.forEach(v -> joiner.add(v.getMessage()));

            throw new IllegalArgumentException(joiner.toString());
        }
    }

    public Set<String> getSecureRequiredFor() {
        return new HashSet<>(secureRequiredFor);
    }

    public void setSecureRequiredFor(final Set<String> secureRequiredFor) {
        this.secureRequiredFor = secureRequiredFor;
    }

    public String getDispatcherServletPath() {
        return dispatcherServletPath;
    }

    public void setDispatcherServletPath(final String dispatcherServletPath) {
        this.dispatcherServletPath = dispatcherServletPath;
    }

    public String getResolverClass() {
        return resolverClass;
    }

    public ApplicationFileResolver getResolver() {
        try {
            @SuppressWarnings("unchecked")
            final Class<ApplicationFileResolver> clazz
                                                     = (Class<ApplicationFileResolver>) Class
                .forName(resolverClass);
            return clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new UncheckedWrapperException(
                "Unable to retrieve ApplicationFileResolver", ex);
        }
    }

    public void setResolverClass(final String resolverClass) {
        try {
            final Class<?> clazz = Class.forName(resolverClass);
            if (!ApplicationFileResolver.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(
                    String.format("Provided class \"%s\" is not an "
                                      + "implementation of the interface \"%s\".",
                                  resolverClass,
                                  ApplicationFileResolver.class.getName()));
            }
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(
                String.format("Unable to retrieve class \"%s\".",
                              resolverClass),
                ex);
        }

        this.resolverClass = resolverClass;
    }

    public Boolean getDeactiveCacheHostNotifications() {
        return deactiveCacheHostNotifications;
    }

    public void setDeactiveCacheHostNotifications(
        final Boolean deactiveCacheHostNotifications) {
        this.deactiveCacheHostNotifications = deactiveCacheHostNotifications;
    }

    public String getDynamicHostProvider() {
        return dynamicHostProvider;
    }

    public void setDynamicHostProvider(final String dynamicHostProvider) {
        this.dynamicHostProvider = dynamicHostProvider;
    }

    private Set<ConstraintViolation<WebConfig>> validateHostParameter(
        final Method method,
        final String parameter) {
        final Object[] parameters = new Object[1];
        parameters[0] = parameter;

        final ValidatorFactory factory = Validation
            .buildDefaultValidatorFactory();
        final ExecutableValidator validator = factory.getValidator()
            .forExecutables();
        return validator.validateParameters(this, method, parameters);
    }

}
