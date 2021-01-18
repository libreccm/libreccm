/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Shiro;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.Folder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private HttpServletRequest request;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private PermissionChecker permissionChecker;

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

    @GET
    @Path("/{sectionIdentifier}/details")
    public String getContentSectionDetails() {
        throw new WebApplicationException(
            Response.status(Response.Status.NOT_FOUND).build()
        );
    }

    @POST
    @Path("/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createContentSection(
        @FormParam("sectionName") final String sectionName
    ) {
        sectionManager.createContentSection(sectionName);

        return "redirect:/list";
    }

    @POST
    @Path("/{sectionIdentifier}/rename")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameContentSection(
        @PathParam("sectionIdentifier") final String identifierParam,
        @FormParam("sectionName") final String sectionName
    ) {
        final ContentSection section = findContentSection(identifierParam);

        sectionManager.renameContentSection(section, sectionName);

        return "redirect:list";
    }

    @POST
    @Path("/{sectionIdentifier}/delete")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteContentSection(
        @PathParam("sectionIdentifier") final String identifierParam,
        @FormParam("confirmed") final String confirmed
    ) {
        if (Objects.equals(confirmed, "true")) {
            final ContentSection section = findContentSection(identifierParam);

            if (!canDelete(section)) {
                throw new WebApplicationException(
                    String.format(
                        "ContentSection %s is not empty and can't be deleted.",
                        section.getLabel()
                    ),
                    Response.Status.BAD_REQUEST
                );
            }

            sectionManager.deleteContentSection(section);
        }

        return "redirect:/list";
    }

    private ContentSection findContentSection(final String identifierParam) {
        final Identifier identifier = identifierParser.parseIdentifier(
            identifierParam
        );

        final ContentSection section;
        switch (identifier.getType()) {
            case ID:
                section = sectionRepo.findById(
                    Long.parseLong(identifier.getIdentifier())
                ).orElseThrow(
                    () -> new WebApplicationException(
                        String.format(
                            "No ContentSection identified by ID %s "
                                + "available.",
                            identifierParam
                        ),
                        Response.Status.NOT_FOUND
                    )
                );
                break;
            case UUID:
                section = sectionRepo
                    .findByUuid(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection identifed UUID %s "
                                    + "available.",
                                identifierParam
                            ),
                            Response.Status.NOT_FOUND
                        )
                    );
                break;
            default:
                section = sectionRepo
                    .findByLabel(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection with name %s "
                                    + "available.",
                                identifierParam
                            ),
                            Response.Status.NOT_FOUND
                        )
                    );
                break;
        }

        return section;
    }

    protected boolean canDelete(final ContentSection section) {
        final Folder rootAssetsFolder = section.getRootAssetsFolder();
        final Folder rootDocumentsFolder = section.getRootDocumentsFolder();

        return rootAssetsFolder.getSubFolders().isEmpty()
                   && rootAssetsFolder.getObjects().isEmpty()
                   && rootDocumentsFolder.getSubFolders().isEmpty()
                   && rootDocumentsFolder.getObjects().isEmpty();
    }

}
