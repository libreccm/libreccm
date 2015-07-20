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
package org.libreccm.core.authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/**
 * Parses a string array/list of strings to create an configuration for JAAS.
 *
 * This class is based on the {@code com.arsdigita.kernel.security.LoginConfig}
 * class. The code itself has been heavily refactored using features like
 * Generics and other things added to the Java language and the Java Standard
 * Library in the last ten years since the original class was written.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LoginConfig extends Configuration {

    /**
     * Maps application names to {@code AppConfigurationEntry[]}.
     *
     * ToDo: To create a configuration this class parses strings. Of course the
     * parsing may fails. In this case the constructor throws an exception.
     * Throwing exceptions from a constructor is considered a bad practise
     * because this may leaves a party created object. Instead the better option
     * would be a factory method or a builder.
     */
    private final Map<String, AppConfigurationEntry[]> appConfigs = new HashMap<>();

    private LoginConfig() {
        //Nothing.
    }

    /**
     * Creates a new login configuration from a list of string. {@code Request}
     * and {@code Register} are mandatory contexts, WAF refuses to start if they
     * are not configured. Each login context can span multiple modules.
     *
     * The input list comprises of strings adhering to the following format:
     *
     * <pre>
     *    context:moduleName:controlFlag[:option1[:option2[:...]]]
     * </pre>
     *
     * <dl>
     *
     * <dt>context</dt>
     * <dd>String</dd>
     *
     * <dt>moduleName</dt>
     * <dd>String</dd>
     *
     * <dt>controlFlag</dt>
     * <dd>"required"</dd>
     * <dd>"requisite"</dd>
     * <dd>"sufficient"</dd>
     * <dd>"optional"</dd>
     *
     * <dt>option</dt>
     * <dd>"key=value"</dd>
     * </dl>
     *
     * <p>
     * Example:</p>
     *
     * <pre>
     *     Request:com.arsdigita.kernel.security.CredentialLoginModule:requisite:debug=true
     *     Register:com.arsdigita.kernel.security.LocalLoginModule:requisite
     *     Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite
     *     Register:com.arsdigita.kernel.security.CredentialLoginModule:optional
     * </pre>
     *
     * @param config The configuration in string format.
     *
     */
    public LoginConfig(final List<String> config) {
        final Map<String, List<String>> contextConfigs = new HashMap<>();

        for (String tuple : config) {
            final int pos = tuple.indexOf(':');
            final String context = tuple.substring(0, pos);
            final String moduleConfig = tuple.substring(pos + 1);
            final List<String> contextConfig = retrieveContextConfig(
                    context, contextConfigs);

            contextConfig.add(moduleConfig);
        }

        for (final Map.Entry<String, List<String>> entry : contextConfigs.
                entrySet()) {
            addAppConfig(entry.getKey(), entry.getValue());
        }
    }

    private List<String> retrieveContextConfig(
            final String context, final Map<String, List<String>> contextConfigs) {
        List<String> contextConfig = contextConfigs.get(context);

        if (contextConfig == null) {
            contextConfig = new ArrayList<>();
            contextConfigs.put(context, contextConfig);
        }

        return contextConfig;
    }

    private void addAppConfig(final String name, final List<String> entries) {
        final AppConfigurationEntry[] configEntries
                                      = new AppConfigurationEntry[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            final List<String> entry = Arrays.asList(entries.get(i).split(":"));
            configEntries[i] = loadAppConfigEntry(entry);

        }
        appConfigs.put(name, configEntries);
    }

    private AppConfigurationEntry loadAppConfigEntry(final List<String> entry) {
        if (entry.size() < 2) {
            throw new LoginConfigMalformedException("LoginConfig is malformed.");
        }

        final String name = entry.get(0);
        final AppConfigurationEntry.LoginModuleControlFlag flag = parseFlag(
                entry.get(1));
        final Map<String, String> options = new HashMap<>();

        if (entry.size() > 2) {
            for (int i = 2; i < entry.size(); i++) {
                addOption(entry.get(i), options);
            }
        }

        return new AppConfigurationEntry(name, flag, options);
    }

    private AppConfigurationEntry.LoginModuleControlFlag parseFlag(
            final String flagStr) {
        switch (flagStr) {
            case "REQUISITE":
                return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;

            case "REQUIRED":
                return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;

            case "SUFFICIENT":
                return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;

            case "OPTIONAL":
                return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;

            default:
                throw new LoginConfigMalformedException(String.format(
                        "Unknown flag \"%s\". Valid flags are: REQUISITE, "
                                + "REQUIRED, SUFFICIENT, OPTIONAL",
                        flagStr));
        }
    }

    private void addOption(final String option,
                           final Map<String, String> options) {
        final int index = option.indexOf('=');
        if (index == -1) {
            throw new LoginConfigMalformedException(String.format(
                    "The option string \"%s\" is malformed.", option));
        }

        final String key = option.substring(0, index);
        final String value = option.substring(index + 1);
        options.put(key, value);
    }

    public static void foo(final LoginConfig config) {
        if (config.appConfigs.get("foo") != null) {
            config.appConfigs.remove("foo");
        }
    }

    /**
     * Convenient constructor taking an arrays of strings containing the
     * configuration. Internally this constructor converts the array to a list
     * and calls {@link #LoginConfig(java.util.List)}.
     *
     * @param config The configuration in string form.
     *
     * @see #LoginConfig(java.util.List)
     */
    public LoginConfig(final String[] config) {
        this(Arrays.asList(config));
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(final String name) {
        return appConfigs.get(name);
    }

    @Override
    public void refresh() {
        // Nothing
    }

}
