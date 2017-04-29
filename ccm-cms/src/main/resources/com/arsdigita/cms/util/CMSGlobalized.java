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
import com.arsdigita.globalization.Globalized;

/**
 * Modules which depend on CMS should implement this interface instead of 
 * com.arsdigita.globalization.Globalized to gain access to CMS globalization 
 * resource file (specifically important for content type packages).
 *
 * @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
 * @version $Revision: #5 $ $Date: 2004/08/17 $
 */
public interface CMSGlobalized extends Globalized {

    /*
     * The central CMS resource file (per language) which may be used by
     * all of CMS specific modules.
     * It overwrites the file provided by globalization package as a generic
     * default/fall back!
     */
    public static final String BUNDLE_NAME = "com.arsdigita.cms.CMSResources";
   
}
