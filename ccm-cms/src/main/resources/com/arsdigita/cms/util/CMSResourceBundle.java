/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.globalization.ChainedResourceBundle;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


// Developers Note (2013-04):
// No longer used because we found no way to make localization work properly.
// Back to use plain property files as of 2013-04 (version 6.6.8)
// Retained for easy reference to further develop localization infrastructure.


/**
 * Main ResourceBundle for CMS UI.
 * Can be extended using:
 * - addBundle - to add new keys
 * - putBundle - to override keys already in CMSResources e.g. to customize
 *               notification email text
 */
public class CMSResourceBundle extends ChainedResourceBundle implements CMSGlobalized {

    public CMSResourceBundle() {
        super();
        // addBundle((PropertyResourceBundle) getBundle(BUNDLE_NAME));
        
        // try to make proper localisation work, no success, ne regression either
        addBundle((PropertyResourceBundle) getBundle(BUNDLE_NAME,
                                                     ResourceBundle.Control.getNoFallbackControl(
                ResourceBundle.Control.FORMAT_DEFAULT)));
    }

}
