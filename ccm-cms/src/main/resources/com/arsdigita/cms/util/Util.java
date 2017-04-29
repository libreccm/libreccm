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
package com.arsdigita.cms.util;

import com.arsdigita.runtime.ConfigError;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Utility functions for use by installer classes.
 *
 * @author Jon Orris (jorris@redhat.com)
 * @version $Revision: #6 $ $DateTime: 2004/08/17 23:15:09 $
 */

public class Util {
    public static void validateURLParameter(String name, String value)
        throws ConfigError {

        final String pattern = "/[^A-Za-z_0-9\\-]+/";
        Perl5Util util = new Perl5Util();
        if ( util.match(pattern, value) ) {
            throw new ConfigError
                ("The \"" + name + "\" parameter must contain only " +
                 " alpha-numeric characters, underscores, and/or hyphens.");
        }
    }

}
