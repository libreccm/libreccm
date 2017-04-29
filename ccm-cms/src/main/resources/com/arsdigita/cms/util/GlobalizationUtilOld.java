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
package com.arsdigita.cms.util;

import com.arsdigita.globalization.GlobalizedMessage;


// Developers Note:
// Counterpart to CMSResourceBundle java class.
// No longer used because we couldn't find a way to make proper localization
// work.
// Retained for easy reference to further develop localization infrastructure.


/**
 * <p>
 * .
 * Contains methods to simplify globalizing keys
 * </p>
 *
 * @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
 * @version $Revision: #7 $ $Date: 2004/08/17 $
 */
public class GlobalizationUtilOld {

    /**  Name of the Java class to handle CMS's globalisation.  */
    //public static String s_bundleName = "com.arsdigita.cms.util.CMSResourceBundle";
    public static String s_bundleName = "com.arsdigita.cms.CMSResources";

    /**
     *  This returns a globalized message using the package specific bundle,
     *  provided by method getBundleName() 
     */
    public static GlobalizedMessage globalize(String key) {
        return new GlobalizedMessage(key, getBundleName());
    }

    /**
     * Returns a globalized message object, using the package specific bundle,
     * provided by method getBundleName(). Also takes in an Object[] of 
     * arguments to interpolate into the retrieved message using the 
     * MessageFormat class. 
     */
    public static GlobalizedMessage globalize(String key, Object[] args) {
        return new GlobalizedMessage(key, getBundleName(), args);
    }

    /**
     * Returns the name of the package specific resource bundle.
     * @return 
     */
    public static String getBundleName() {
        return s_bundleName;
    }

    /*
     * Not a part of API. Otherwise it would need to be properly synchronized.
     * Only meant be used to override resource keys in CMSResources
     * by a custom application, in Initializer.
     */
    public static void internalSetBundleName(String bundleName) {
        s_bundleName = bundleName;
    }

}
