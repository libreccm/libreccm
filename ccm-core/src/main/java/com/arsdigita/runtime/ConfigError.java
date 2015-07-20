/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.runtime;

import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An error to indicate invalid configurations.
 *
 * Usage: throw new ConfigError( "message" );
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class ConfigError extends Error {

    private static final long serialVersionUID = 5224480607138738741L;

    /**
     * Constructs a new configuration error with the content
     * <code>message</code>.
     *
     * @param message A <code>String</code> describing what's wrong; it cannot
     *                be null
     */
    public ConfigError(final String message) {
        super(message);

        Assert.exists(message, String.class);
    }

    /**
     * Constructs a new configuration error with a default message.
     */
    public ConfigError() {
        super("Configuration is invalid");
    }

}
