/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.util;

import com.arsdigita.globalization.Globalized;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Compilation of methods to simplify the handling of globalizing keys.
 * Basically it adds the name of package's resource bundle files to the
 * globalize methods and forwards to GlobalizedMessage, shortening the
 * method invocation in the various application classes.
 *
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 */
public class GlobalizationUtil implements Globalized {
    
    /**  Name of Java resource files to handle CMS's globalisation.  */
    private static final String BUNDLE_NAME = "com.arsdigita.bebop.BebopResources";

    /**
     * Returns a globalized message using the package specific bundle,
     * provided by BUNDLE_NAME. 
     * @param key
     * @return 
     */
    public static GlobalizedMessage globalize(String key) {
	 return new GlobalizedMessage(key, BUNDLE_NAME);
     }

    /**
     * Returns a globalized message object, using the package specific bundle,
     * as specified by BUNDLE_NAME. Also takes in an Object[] of arguments to
     * interpolate into the retrieved message using the  MessageFormat class
     * (i.e. {0}, {1},... for adding variable strings).
     * @param key
     * @param args
     * @return new instance of a globalized message 
     */
    public static GlobalizedMessage globalize(String key, Object[] args) {
         return new GlobalizedMessage(key, BUNDLE_NAME, args);

  }
}
