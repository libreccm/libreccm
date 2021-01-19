/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}")
public class ContentSectionController {

    @Inject
    private CmsAdminMessages cmsAdminMessages;
    
    @Inject
    private ContentSectionModel contentSectionModel;
    
    @Inject
    private FolderBrowserModel folderBrowserModel;
    
    @Inject
    private Models models;
    
    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private IdentifierParser identifierParser;

    @GET
    @Path("/folderbrowser")
    @AuthorizationRequired
    public String listItems(
        @PathParam("sectionIdentifier") final String sectionIdentifier
    ) {
        final Identifier identifier = identifierParser.parseIdentifier(
            sectionIdentifier
        );
        final ContentSection section;
        switch (identifier.getType()) {
            case ID:
                section = sectionRepo
                    .findById(Long.parseLong(identifier.getIdentifier()))
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection with ID %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
            case UUID:
                section = sectionRepo
                    .findByUuid(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection with UUID %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
            default:
                section = sectionRepo
                    .findByLabel(identifier.getIdentifier())
                    .orElseThrow(
                        () -> new WebApplicationException(
                            String.format(
                                "No ContentSection named %s found.",
                                identifier.getIdentifier()
                            )
                        )
                    );
                break;
        }
        
        contentSectionModel.setSection(section);
        folderBrowserModel.setSection(section);
        
        return "org/librecms/ui/content-section/folderbrowser.xhtml";
        
    }

}
