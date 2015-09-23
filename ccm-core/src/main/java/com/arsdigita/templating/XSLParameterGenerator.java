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
package com.arsdigita.templating;

import javax.servlet.http.HttpServletRequest;

/**
 *  This is an interface that allows developers to set variables
 *  that will be available inside of the XSL for use by all XSL stylesheets.
 *  Implementations of this class can be registered with the 
 *  {@link com.arsdigita.templating.Templating}
 *  class for use by any {@link com.arsdigita.templating.PresentationManager}
 *  class
 */
public interface XSLParameterGenerator {
    
    /**
     *  This returns the correct value for the parameter.  This is the
     *  value that is added to the transformer and is available to all
     *  stylesheets 
     * @param request
     * @return 
     */
    public String generateValue(HttpServletRequest request);
}
