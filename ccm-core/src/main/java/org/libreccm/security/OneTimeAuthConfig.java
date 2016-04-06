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

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

/**
 * Configuration for the one time authentication system.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class OneTimeAuthConfig {

    /**
     * How long is a OneTimeAuthToken valid (in seconds)?
     */
    @Setting
    private int tokenValid = 3600;

    /**
     * Length of the one time auth token (characters)
     */
    @Setting
    private int tokenLength = 64;

    public static OneTimeAuthConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
                .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(OneTimeAuthConfig.class);
    }

    public OneTimeAuthConfig() {
        super();
    }

    public int getTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(final int tokenValid) {
        this.tokenValid = tokenValid;
    }

    public int getTokenLength() {
        return tokenLength;
    }

    public void setTokenLength(final int tokenLength) {
        this.tokenLength = tokenLength;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + tokenValid;
        hash = 79 * hash + tokenLength;
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
        if (!(obj instanceof OneTimeAuthConfig)) {
            return false;
        }
        final OneTimeAuthConfig other = (OneTimeAuthConfig) obj;
        if (tokenValid != other.getTokenValid()) {
            return false;
        }

        return this.tokenLength == other.getTokenLength();
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                     + "tokenValid = %d,"
                                     + "tokenLength = %d"
                                     + " }",
                             super.toString(),
                             tokenValid,
                             tokenLength);
    }

}
