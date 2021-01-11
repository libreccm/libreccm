/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.libreccm.security.AuthorizationRequired;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
public class ContentSectionsController {

    @Inject
    private HttpServletRequest request;

    @GET
    @Path("/")
    @AuthorizationRequired
    public Response getRoot() {
        return Response
            .status(Response.Status.MOVED_PERMANENTLY)
            .entity(
                String.format("%s/@content-sections/list", request
                              .getContextPath())
            ).build();
    }

    @GET
    @Path("/list")
    @AuthorizationRequired
    public String getContentSections() {
        return "org/librecms/ui/content-sections/list.xhtml";
    }

    @GET
    @Path("/pages")
    @AuthorizationRequired
    public String getPages() {
        return "org/librecms/ui/content-sections/pages.xhtml";
    }

    @GET
    @Path("/search")
    @AuthorizationRequired
    public String getSearch() {
        return "org/librecms/ui/content-sections/pages.xhtml";
    }

}
