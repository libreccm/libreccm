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
package com.arsdigita.bebop;

import com.arsdigita.util.Assert;

/**
 * The base page class for use with the PageFactory
 * class. It sets two attributes on the XML tag for
 * the bebop:page, namely <code>application</code>
 * and <code>id</code>. The values for these two
 * tags correspond to the parameters passed to the 
 * PageFactory.buildPage method.
 * <p>
 * This class is intended to be subclassed to provide
 * the page infrastructure required by a project, for 
 * example, adding some navigation components.
 * The SimplePage class provides a easy implementation
 * whereby the navigation components can be specified
 * in the enterprise.init file.
 */
public class BasePage extends Page {

    public BasePage(String application,
                    Label title,
                    String id) {
        super(title, new SimpleContainer());
        
        Assert.exists(application, "application name");
        setAttribute("application", application);

        if (id != null) {
            setAttribute("id", id);
        }
    }
}
