/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.applications.shortcuts;

import org.libreccm.shortcuts.ShortcutRepository;
import org.libreccm.shortcuts.ShortcutsConstants;
import org.libreccm.ui.admin.applications.ApplicationController;
import org.libreccm.ui.admin.applications.IsApplicationControllerFor;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
//@IsApplicationControllerFor(ShortcutsConstants.SHORTCUTS_APP_TYPE)
@Path("/application")
public class ShortcutsApplicationController implements ApplicationController {

    @Inject
    private Models models;

    @Inject
    private ShortcutRepository shortcutRepository;

    @GET
    @Path("/")
    @Override
    public String getApplication() {
        models.put("shortcuts", shortcutRepository.findAll());
        
        return "org/libreccm/ui/admin/applications/shortcuts/shortcuts.xhtml";
    }

}
