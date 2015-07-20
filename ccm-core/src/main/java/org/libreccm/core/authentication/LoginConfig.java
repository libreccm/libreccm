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

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.spi.LoginModule;

/**
 * Configuration for JAAS containing all active {@link LoginModule} 
 * implementations.
 *
 * The active modules are stored in the {@link SecurityConfig} as an array
 * of strings. The original {@code com.arsdigita.kernel.security.LoginConfig}
 * class parsed this string array in its constructor. For LibreCCM 7 the code
 * has been split up. The logic for parsing the configuration is now provided
 * by the {@link LoginConfigBuilder} class. This allowed us the greatly 
 * simplify this class. Also now we don't have a constructor which throws 
 * Exceptions.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LoginConfig extends Configuration {

    /**
     * The configuration entries.
     */
    private final Map<String, AppConfigurationEntry[]> appConfigs;

    private LoginConfig() {
        this.appConfigs = new HashMap<>();
    }
    
    LoginConfig(final Map<String, AppConfigurationEntry[]> appConfigs) {
        this.appConfigs = appConfigs;
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
