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

import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.shortcuts.Shortcut;
import org.libreccm.shortcuts.ShortcutRepository;
import org.libreccm.ui.admin.applications.ApplicationController;

import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/applications/shortcuts")
public class ShortcutsApplicationController implements ApplicationController {

    @Inject
    private Models models;

    @Inject
    private ShortcutRepository shortcutRepository;

    @GET
    @Path("/")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String getApplication() {
        models.put("shortcuts", shortcutRepository.findAll());

        return "org/libreccm/ui/admin/applications/shortcuts/shortcuts.xhtml";
    }

    @POST
    @Path("/add")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String addShortcut(
        @FormParam("urlKey") final String urlKey,
        @FormParam("redirect") final String redirect
    ) {
        final Shortcut shortcut = new Shortcut();
        shortcut.setUrlKey(urlKey);
        shortcut.setRedirect(redirect);
        
        shortcutRepository.save(shortcut);
        
        return "redirect:applications/shortcuts";
    }
    
    @POST
    @Path("/{shortcutId}/edit")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateShortcut(
        @PathParam("shortcutId") final long shortcutId,
        @FormParam("urlKey") final String urlKey,
        @FormParam("redirect") final String redirect
    ) {
        final Optional<Shortcut> result = shortcutRepository
            .findById(shortcutId);
        
        if (result.isPresent()) {
            final Shortcut shortcut = result.get();
            shortcut.setUrlKey(urlKey);
            shortcut.setRedirect(redirect);
            
            shortcutRepository.save(shortcut);
        }
        
        return "redirect:applications/shortcuts";
    }
    
    @POST
    @Path("/{shortcutId}/remove")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeShortcut(
        @PathParam("shortcutId") final long shortcutId
    ) {
        final Optional<Shortcut> result = shortcutRepository
            .findById(shortcutId);
        
        if (result.isPresent()) {
            shortcutRepository.delete(result.get());
        }
        
        return "redirect:applications/shortcuts";
    }

}
