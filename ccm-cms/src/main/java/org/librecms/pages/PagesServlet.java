/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.pages;

import com.arsdigita.cms.ui.pages.PagesAdminPage;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.templating.Templating;
import com.arsdigita.web.BaseApplicationServlet;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.xml.Document;

import org.apache.shiro.authz.AuthorizationException;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet for the Admin UI for pages a {@link /ccm/{primaryUrl}}. The admin UI
 * itself is implemented by {@link PagesAdminPage}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {"/templates/servlet/pages/*"})
public class PagesServlet extends BaseApplicationServlet {

    private static final long serialVersionUID = -303317198251922697L;

    @Inject
    private ApplicationRepository applicationRepo;

    @Inject
    private PagesRepository pagesRepo;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private Shiro shiro;

    @Override
    protected void doService(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final CcmApplication application)
        throws ServletException, IOException {

        if (!shiro.getSubject().isAuthenticated()) {
            throw new LoginSignal(request);
        }

        if (!permissionChecker.isPermitted(PagesPrivileges.ADMINISTER_PAGES)) {
            throw new AuthorizationException("The current user is not "
                                                 + "permitted to administer pages.");
        }

        final PagesAdminPage page = new PagesAdminPage();

        if (!(application instanceof Pages)) {
            throw new ServletException(
                "Provided application is not an instance of Pages");
        }

        page.setPagesInstance((Pages) application);
        
        final Document document = page.buildDocument(request, response);

        final PresentationManager presentationManager = Templating
            .getPresentationManager();
        presentationManager.servePage(document, request, response);
    }

}
