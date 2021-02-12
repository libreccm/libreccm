package org.librecms.ui.contentsections;

import org.libreccm.security.AuthorizationRequired;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.mvc.Controller;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
@Path("/")
public class ContentSectionController {

    @Inject
    private HttpServletRequest request;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public Response redirectToContentSectionsList() {
        try {
            return Response
                .seeOther(
                    new URI(
                        request.getScheme(),
                        "",
                        request.getServerName(),
                        request.getServerPort(),
                        String.format(
                            "%s/@cms/contentsections",
                            request.getContextPath()
                        ),
                        "",
                        ""
                    )
                ).build();
        } catch (URISyntaxException ex) {
            throw new WebApplicationException(ex);
        }
    }
    
    @GET
    @Path("/{sectionIdentifier}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String redirectToDocumentFolders(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        return String.format("redirect:/%s/documentfolders", sectionIdentifier);
    }

}
