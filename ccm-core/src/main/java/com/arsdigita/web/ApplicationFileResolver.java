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
package com.arsdigita.web;

import org.libreccm.web.Application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;


/**
 * Interface specifies standard API tools to resolve an URL to a accessible
 * resource, stored in file system, database of any other suitable location.
 * The URL may include virtual resources, e.g. files stored in the database
 * instead of the file system. The URL may include other "virtual" parts with
 * must be mapped to an appropriate real path.
 */
public interface ApplicationFileResolver {

    /**
     * 
     * @param templatePath
     * @param sreq
     * @param sresp
     * @param app
     * @return 
     */
    RequestDispatcher resolve(String templatePath,
                              HttpServletRequest sreq,
                              HttpServletResponse sresp,
                              Application app);

}
