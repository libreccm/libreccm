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

import com.arsdigita.kernel.security.SecurityConfig;
import com.arsdigita.runtime.ConfigError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.spi.LoginModule;

/**
 * Creates a {@link LoginConfig} instance from an array or an list of strings.
 *
 * The process of creating a {@link LoginConfig} instance from the string values
 * like those stored in the {@link SecurityConfig} is a complex process.
 * Originally the constructor of the
 * {@code com.arsdigita.kernel.security.LoginConfig} class has done all this
 * work. Some parts were outsourced to private methods.
 *
 * The problem with this approach is that several of this method may throw an
 * exception/error. But throwing exceptions from a constructor is considered a
 * bad practise. Also the private support methods made the original
 * {@code LoginConfig} class quite big and complex. Therefore the code has been
 * split up. The construction process is now done by this class which creates
 * {@code LoginConfig} from a provided list or array of strings. The strings
 * must be in the correct format:
 *
 * <pre>
 * context:moduleName:controlFlag[:option1[:option2[:...]]]
 * </pre>
 *
 * <dl>
 * <dt>{@code context}</dt>
 * <dd>String</dd>
 *
 * <dt>{@code moduleName}</dt>
 * <dd>Fully qualified class name of a {@link LoginModule}.</dd>
 *
 * <dt>{@code controlFlag}</dt>
 * <dd>
 * One of the following flags:
 * <ul>
 * <li>{@code required}</li>
 * <li>{@code requisite}</li>
 * <li>{@code sufficient}</li>
 * <li>{@code optional}</li>
 * </ul>
 * </dd>
 *
 * <dt>option</dt>
 * <dd>Options for the module in the following format: {@code key=value}</dd>
 * </dl>
 *
 * Examples:
 *
 * <pre>
 *     Request:com.arsdigita.kernel.security.CredentialLoginModule:requisite:debug=true
 *     Register:com.arsdigita.kernel.security.LocalLoginModule:requisite
 *     Register:com.arsdigita.kernel.security.UserIDLoginModule:requisite
 *     Register:com.arsdigita.kernel.security.CredentialLoginModule:optional
 * </pre>
 *
 * The build a {@link LoginConfig} first construct an instance of this class and
 * pass the string array containing the configuration. For example:
 *
 * <pre>
 *      final LoginConfigBuilder loginConfigBuilder =
 *          new LoginConfigBuilder(SecurityConfig.getInstance().getLoginConfig());
 * </pre>
 *
 * Then call the {@link #build()} method which does all the work:
 *
 * <pre>
 *      final LoginConfig loginConfig = loginConfigBuilder.build();
 * </pre>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LoginConfigBuilder {

    private final transient String[] config;

    /**
     * Creates a new {@code LoginConfigBuilder} for the provided configuration.
     *
     * @param config The configuration for which an {@link LoginConfig} should
     *               be created, as array of strings as provided by
     *               {@link SecurityConfig#getLoginConfig()}.
     */
    public LoginConfigBuilder(final String[] config) {
        this.config = config;
    }

    /**
     * Creates a {@link LoginConfig} from {@link #config}.
     *
     * If one entry of the {@link #config} is malformed a {@link ConfigError}
     * will be thrown.
     *
     * @return A {@link LoginConfig} object.
     */
    public LoginConfig build() {
        //Temporary storage for the data extracted from the config string array.
        final Map<String, List<String>> contextConfigs = new HashMap<>();

        //Parse the tuples in the config string array.
        for (final String tuple : config) {
            //Find the index of the first ':'.
            final int index = tuple.indexOf(':');
            //Extract context and module config parts from the tuple.
            final String context = tuple.substring(0, index);
            final String moduleConfig = tuple.substring(index + 1);

            //Put them in the list for the context.
            final List<String> contextConfig = retrieveContextConfig(
                context, contextConfigs);
            contextConfig.add(moduleConfig);

        }

        //Create the map of AppConfigurationEntry objects. 
        final Map<String, AppConfigurationEntry[]> appConfigs = new HashMap<>();
        for (final Map.Entry<String, List<String>> entry : contextConfigs
            .entrySet()) {
            //Add the config entry. The helper method called creates the
            //AppConfigurationEntry object from the string value.
            addAppConfig(appConfigs, entry.getKey(), entry.getValue());
        }

        //Create the LoginConfig object with the Map of AppConfigurationEntries.
        return new LoginConfig(appConfigs);

    }

    /**
     * Helper method for retrieving a list for specific context from the context
     * maps. Used by {@link #build()}. If the map has no entry with the provided
     * name a new list is created a put into the list.
     *
     * @param context        The name of the context, used as key in the
     *                       provided map.
     * @param contextConfigs The map of context configs.
     *
     * @return The context configs list for the provided context.
     */
    private List<String> retrieveContextConfig(
        final String context, final Map<String, List<String>> contextConfigs) {
        List<String> contextConfig = contextConfigs.get(context);

        if (contextConfig == null) {
            contextConfig = new ArrayList<>();
            contextConfigs.put(context, contextConfig);
        }

        return contextConfig;
    }

    /**
     * Helper method for creating an {@link AppConfigurationEntry} object from a
     * string.
     *
     * @param appConfigs The map of {@link AppConfigurationEntry} objects in
     *                   which the created entry will be stored.
     * @param name       The name of the context for which the
     *                   {@link AppConfigurationEntry} is created.
     * @param entries    The list of configuration entries to parse.
     */
    private void addAppConfig(
        final Map<String, AppConfigurationEntry[]> appConfigs,
        final String name,
        final List<String> entries) {

        //Map containing the parsed entries
        final AppConfigurationEntry[] configEntries
                                          = new AppConfigurationEntry[entries
            .size()];

        //Parse all entries. We use a "traditional" for loop here because we 
        //need the position in the array.
        for (int i = 0; i < entries.size(); i++) {
            //Load the current configuration entry.
            configEntries[i] = loadAppConfigEntry(entries.get(i));
        }

        //Put the parsed entires into the map
        appConfigs.put(name, configEntries);
    }

    /**
     * Helper method for parsing a single configuration entry. The
     *
     * tokens entry
     *
     * @return
     */
    private AppConfigurationEntry loadAppConfigEntry(final String entry) {
        //Split the string tokens. The tokens are limited by the ':' character.
        final String[] tokens = entry.split(":");

        //If there less then two tokens the entry is malformed and we throw an 
        //ConfigError.
        if (tokens.length < 2) {
            final StringBuilder builder = new StringBuilder();
            for (final String str : tokens) {
                builder.append(str);
            }
            throw new ConfigError(String.format(
                "Malformed SecurityConfig entry: %s", builder.toString()));
        }

        //Extract the name of the configured module (the first token)
        final String name = tokens[0];
        //Extract the flat (second token)
        final AppConfigurationEntry.LoginModuleControlFlag flag = parseFlag(
            tokens[1]);

        //Extract the provided options if any
        final Map<String, String> options = new HashMap<>();
        if (tokens.length > 2) {
            for (int i = 2; i < tokens.length; i++) {
                //The the option to the map of options.
                addOption(tokens[i], options);
            }
        }

        //Create an AppConfguration using the extracted data.
        return new AppConfigurationEntry(name, flag, options);
    }

    /**
     * Helper method to convert a string to a
     * {@link AppConfigurationEntry.LoginModuleControlFlag}. If the provided
     * string is not a valid flag a {@link ConfigError} is thrown.
     *
     * @param flag The string to convert.
     *
     * @return {@link AppConfigurationEntry.LoginModuleControlFlag} instance.
     */
    private AppConfigurationEntry.LoginModuleControlFlag parseFlag(
        final String flag) {
        switch (flag.toUpperCase()) {
            case "REQUISITE":
                return AppConfigurationEntry.LoginModuleControlFlag.REQUISITE;

            case "REQUIRED":
                return AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;

            case "SUFFICIENT":
                return AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT;

            case "OPTIONAL":
                return AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL;

            default:
                throw new ConfigError(String.format(
                    "Unknown flag \"%s\". Valid flags are: REQUISITE, "
                        + "REQUIRED, SUFFICIENT, OPTIONAL",
                    flag));
        }

    }

    /**
     * Helper method for extracting the key and value parts from an module 
     * option string. If the option string is malformed an {@link ConfigError} 
     * is thrown.
     *
     * @param option The option string to parse.
     * @param options The map of options to which the parsed option we be added.
     */
    private void addOption(final String option,
                           final Map<String, String> options) {
        //Find the index of the '=' character.
        final int index = option.indexOf('=');
        //If there is no '=' in the string the option string is invalid
        if (index == -1) {
            throw new ConfigError(String.format(
                "The option string \"%s\" is malformed.", option));
        }

        //Extract key and value an put them into the options map.
        final String key = option.substring(0, index);
        final String value = option.substring(index + 1);
        options.put(key, value);
    }

}
