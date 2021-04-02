/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
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
 * Controller for the CMS application which allows the management of content
 * sections.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/")
public class CmsController {

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

    /**
     * Redirects requests to the root path ({@code /@cms} or {@code /@cms/}) to
     * the {@code /@cms/contentsections}.
     *
     * @return A redirect response with the {@link Response.Status#SEE_OTHER}
     *         response code.
     */
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
                        "%s/@cms/contentsections/",
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

    /**
     * Shows all available content sections.
     *
     * @return The template to use.
     */
    @GET
    @Path("/contentsections/")
    @AuthorizationRequired
    public String getContentSections() {
        return "org/librecms/ui/cms/contentsections-list.xhtml";
    }

    /**
     * Creates a new content section.
     *
     * @param sectionName The name of the new section.
     *
     * @return Redirect to the content sections list.
     */
    @POST
    @Path("/contentsections/new")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String createContentSection(
        @FormParam("sectionName") final String sectionName
    ) {
        sectionManager.createContentSection(sectionName);

        return "redirect:/contentsections/";
    }

    /**
     * Renames a content section.
     *
     * @param identifierParam The identifier (see {@link Identifier} and
     *                        {@link IdentifierParser}) of the content section
     *                        to rename.
     * @param sectionName     The new name of the content section.
     *
     * @return Redirect to the list of content sections.
     */
    @POST
    @Path("/contentsections/{sectionIdentifier}/rename")
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public String renameContentSection(
        @PathParam("sectionIdentifier") final String identifierParam,
        @FormParam("sectionName") final String sectionName
    ) {
        final ContentSection section = findContentSection(identifierParam);

        sectionManager.renameContentSection(section, sectionName);

        return "redirect:/contentsections/";
    }

    /**
     * Deletes a content section. The content section must be empty (no items,
     * assets or folders in it).
     *
     * @param identifierParam The identifier (see {@link Identifier} and
     *                        {@link IdentifierParser}) of the content section
     *                        to delete.
     * @param confirmed       A string which must contain the value {@code true}
     *                        to be sure that the user confirmed the deletion of
     *                        the content section.
     *
     * @return Redirect to the list of content sections.
     */
    @POST
    @Path("/contentsections/{sectionIdentifier}/delete")
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

        return "redirect:/contentsections/";
    }

    /**
     * ToDo: Show UI for managing pages.
     *
     * @return Placeholder
     */
    @GET
    @Path("/pages")
    @AuthorizationRequired
    public String getPages() {
        return "org/librecms/ui/cms/pages.xhtml";
    }

    /**
     * ToDo: Search for content items (and assets?) in all content sections.
     *
     * @return Placeholder
     */
    @GET
    @Path("/search")
    @AuthorizationRequired
    public String getSearch() {
        return "org/librecms/ui/cms/search.xhtml";
    }

    /**
     * Helper function for retrieving a content section by an identifier.
     *
     * @param identifierParam The identifier paramter.
     *
     * @return The content section if a section identified by the provided
     *         identifier exists.
     *
     * @throws WebApplicationException A {@link WebApplicationException} with
     *                                 {@link Response.Status#NOT_FOUND} status
     *                                 code if there is not content section
     *                                 identified by the provided identifier.
     *
     * @see IdentifierParser
     * @see Identifier
     */
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

    /**
     * Helper function to determine of a content section can be deleted. Checks
     * if the {@link ContentSection#rootAssetsFolder} and the
     * {@link ContentSection#rootDocumentsFolder} are empty.
     *
     * @param section The section
     *
     * @return {@code true} if the content section is empty can be deleted,
     *         {@code false} is not.
     */
    protected boolean canDelete(final ContentSection section) {
        final Folder rootAssetsFolder = section.getRootAssetsFolder();
        final Folder rootDocumentsFolder = section.getRootDocumentsFolder();

        return rootAssetsFolder.getSubFolders().isEmpty()
                   && rootAssetsFolder.getObjects().isEmpty()
                   && rootDocumentsFolder.getSubFolders().isEmpty()
                   && rootDocumentsFolder.getObjects().isEmpty();
    }

}
