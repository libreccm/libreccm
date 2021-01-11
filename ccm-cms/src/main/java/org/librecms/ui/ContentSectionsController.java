/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.libreccm.security.AuthorizationRequired;

import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
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
    
    @Inject
    private ServletContext servletContext;

    @GET
    @Path("/")
    @AuthorizationRequired
    public Response getRoot() {
        try {
            return Response.seeOther(
                new URI(
                    request.getScheme(),
                    "",
                    request.getServerName(),
                    request.getServerPort(),
                    String.format(
                        "%s/@content-sections/list",
                        request.getContextPath()
                    ),
                    "",
                    ""
                )
            ).build();
        } catch (URISyntaxException ex) {
            throw new WebApplicationException(ex);
        }
//        return String.format(
//            "redirect:/%s/@content-sections/list", request.getContextPath()
//        );
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
        return "org/librecms/ui/content-sections/search.xhtml";
    }

}
